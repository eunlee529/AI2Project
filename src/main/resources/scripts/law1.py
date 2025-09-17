import numpy as np
import pandas as pd
import json
from pathlib import Path
import oracledb

# JSON 파일들을 읽는 함수
def read_json(folder_path):
    data_list = []
    folder = Path(folder_path)
    for file in folder.glob("*.json"):
        try:
            with file.open(encoding="utf-8-sig") as f:
                data = json.load(f)
                data_list.append(data)
        except Exception as e:
            print(f"파일 오류: {file.name} - {e}")
    return data_list

# 1. JSON 파일 읽기
folder_path = r"C:\aihub\springboot\AI2\src\main\resources\law1data"
data = read_json(folder_path)
df = pd.DataFrame(data)
print("불러온 컬럼:", df.columns)

# 2. 오라클 연결
oracledb.init_oracle_client(lib_dir=r"C:\aihub\instantclient_11_2")
conn = oracledb.connect(user="mbc", password="1234", dsn="localhost")
cursor = conn.cursor()

# 3. 데이터 insert
for i in range(len(df)):
    # casenames가 50자를 초과하면 자르기
    casenames = df["casenames"][i]
    if len(casenames) > 50:
        print(f"Warning: casenames is too long (length: {len(casenames)}), trimming to 50 characters.")
        casenames = casenames[:50]  # 50자로 자르기

    # sentences 리스트를 JSON 문자열로 변환
    sentences_json = json.dumps(df["sentences"][i], ensure_ascii=False)
    
    try:
        cursor.execute("""
            INSERT INTO hubdata4 (doc_class, doc_id, casenames, normalized_court, casetype, sentences, announce_date)
            VALUES (:1, :2, :3, :4, :5, :6, :7)
        """, (
            int(df["doc_class"][i]),
            df["doc_id"][i],
            casenames,
            df["normalized_court"][i],
            df["casetype"][i],
            sentences_json,
            df["announce_date"][i]
        ))
        conn.commit()
    except Exception as e:
        print(f"데이터 삽입 오류: {e}")

print("✅ 모든 데이터 저장 완료")

cursor.close()
conn.close()
