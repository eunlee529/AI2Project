import pandas as pd
import matplotlib.pyplot as plt
import os
import io
import requests

# 현재 스크립트 경로 기준
base_path = os.path.dirname(os.path.abspath(__file__))

# CSV 경로
csv_path = os.path.abspath(os.path.join(base_path, '../static/data/suwon.csv'))

print("CSV 실제 경로:", csv_path)

df = pd.read_csv(csv_path, encoding="cp949")
df.drop(columns=['시군'], inplace=True)

# '구' 컬럼을 인덱스로 설정
df.set_index('구', inplace=True)

plt.rcParams['font.family'] = 'Malgun Gothic'

# 1) 바 차트 그리기
fig_bar = plt.figure()
df.plot(kind='bar')
plt.title('수원시 주택현황 - 바 차트')
plt.xlabel('구')
plt.ylabel('인구수')
plt.xticks(rotation=0)
plt.grid(True)
plt.tight_layout()

img_buf_bar = io.BytesIO()
plt.savefig(img_buf_bar, format='png')
img_buf_bar.seek(0)
plt.close(fig_bar)

# 2) 원형 그래프
housing_columns = df.columns.tolist()  # 6개 주택 유형 컬럼명

districts = df.index.tolist()

fig_pie, axes = plt.subplots(2, 3, figsize=(12, 8))
axes = axes.flatten()

for i, housing in enumerate(housing_columns):
    values = df[housing].astype(float)
    axes[i].pie(values, labels=districts, autopct='%1.1f%%', startangle=90)
    axes[i].set_title(f'{housing} - 구별 분포')
    axes[i].axis('equal')

plt.tight_layout()
img_buf_pie = io.BytesIO()
plt.savefig(img_buf_pie, format='png')
img_buf_pie.seek(0)
plt.close(fig_pie)


# 3) 이미지 파일 서버로 전송
files = {
    'bar_chart': ('bar_chart.png', img_buf_bar, 'image/png'),
    'pie_chart': ('pie_chart.png', img_buf_pie, 'image/png')
}

url = "http://localhost:1820/python/suwonout"
response = requests.post(url, files=files)
print('서버 응답:', response.text)
