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
folder_path = r"C:\aihub\springboot\AI2\src\main\resources\hub1data"
data = read_json(folder_path)
df = pd.DataFrame(data)
print("불러온 컬럼:", df.columns)

# 중복된 c_id 제거 + 인덱스 초기화
df = df.drop_duplicates(subset=["c_id"]).reset_index(drop=True)

# 2. 오라클 연결
oracledb.init_oracle_client(lib_dir=r"C:\aihub\instantclient_11_2")
conn = oracledb.connect(user="mbc", password="1234", dsn="localhost")
cursor = conn.cursor()

# 3. 데이터 insert (중복된 c_id는 스킵)
for i in range(len(df)):
    try:
        cursor.execute("""
            INSERT INTO hubdata (domain, source, c_id, creation_year, source_spec, content)
            VALUES (:1, :2, :3, :4, :5, :6)
        """, (
            int(df["domain"][i]),
            int(df["source"][i]),
            df["c_id"][i],
            df["creation_year"][i],
            df["source_spec"][i],
            df["content"][i]
        ))
        conn.commit()
    except oracledb.IntegrityError as e:
        print(f"[중복 스킵] {df['c_id'][i]} - {e}")
    except Exception as e:
        print(f"[기타 오류] {df['c_id'][i]} - {e}")

print("✅ 모든 데이터 저장 완료")
