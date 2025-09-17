import numpy as np
import pandas as pd
import json
from pathlib import Path
import oracledb

# JSON 파일을 읽어오는 함수
def read_json(folder_path):
    data_list = []
    folder = Path(folder_path)
    for file in folder.glob("*.json"):
        try:
            with file.open(encoding="utf-8-sig") as f:
                data = json.load(f)
                # JSON이 리스트 형식이면 확장
                if isinstance(data, list):
                    data_list.extend(data)
                else:
                    data_list.append(data)
        except Exception as e:
            print(f"파일 오류: {file.name} - {e}")
    return data_list

# 폴더 위치 지정
folder_path = r"C:\aihub\springboot\AI2\src\main\resources\qnadata"
data = read_json(folder_path)

# DataFrame으로 변환
df = pd.DataFrame(data)
print("컬럼 확인:", df.columns)

# 오라클 DB 연결
oracledb.init_oracle_client(lib_dir=r"C:\aihub\instantclient_11_2")
connect = oracledb.connect(user="mbc", password="1234", dsn="localhost")
c = connect.cursor()

# DB에 데이터 삽입
for i in range(len(df)):
    try:
        qa_id = int(df["qa_id"][i])
        domain = int(df["domain"][i])
        q_type = int(df["q_type"][i])
        question = df["question"][i]
        answer = df["answer"][i]

        num = i + 1  # ✅ 자동으로 num 값 생성

        # 중복 검사
        c.execute("SELECT COUNT(*) FROM hubdata2 WHERE qa_id = :1", (qa_id,))
        if c.fetchone()[0] == 0:
            # INSERT 쿼리 실행
            c.execute("""
                INSERT INTO hubdata2 (qa_id, domain, q_type, num, question, answer)
                VALUES (:1, :2, :3, :4, :5, :6)
            """, (qa_id, domain, q_type, num, question, answer))
            print(f"{qa_id} 저장 완료")

    except Exception as e:
        print(f"{i}번째 데이터 처리 중 오류: {e}")

# 커밋 후 연결 종료
connect.commit()
connect.close()

print("모든 자료 저장 완료!")
