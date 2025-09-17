from tensorflow.keras.models import Sequential
from tensorflow.keras.layers import Dense
from sklearn.model_selection import train_test_split
import numpy as np
import pandas as pd

import os

# 현재 스크립트 경로 기준
base_path = os.path.dirname(os.path.abspath(__file__))

# 상대 경로로 data 폴더 내 CSV 파일 경로 생성
csv_path = os.path.abspath(os.path.join(base_path, '../static/data/iris3.csv'))

print("CSV 실제 경로:", csv_path)

df=pd.read_csv(csv_path, delimiter=',')

X=df.iloc[:, :-1]
y=df.iloc[:,-1]

y_dummy=pd.get_dummies(y)

X_train, X_test, y_train, y_test = train_test_split(X, y_dummy, test_size=0.2, random_state=42)

model=Sequential()
model.add(Dense(64, input_dim=np.shape(X)[1], activation='relu'))
model.add(Dense(12, activation='relu'))
model.add(Dense(3, activation='softmax'))

model.compile(loss='categorical_crossentropy', optimizer='adam', metrics=['accuracy'])

epoch=20
history=model.fit(X_train, y_train, epochs=epoch, batch_size=16)

ss=model.evaluate(X_test, y_test)

import requests
import json

data={
    'loss': ss[0],
    'accuracy': ss[1],
    'epochs': epoch
}

url="http://localhost:1820/python/irisout"
response = requests.post(url, data=data)
print('서버 응답:', response.text)
