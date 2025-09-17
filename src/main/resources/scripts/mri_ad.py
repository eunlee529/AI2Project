import torch
import torch.nn as nn
import torch.optim as optim
from torchvision import datasets, transforms, models
from torchvision.models import ResNet18_Weights
from torch.utils.data import DataLoader
from PIL import Image
import matplotlib.pyplot as plt
from reportlab.pdfgen import canvas
from reportlab.lib.pagesizes import A4
from reportlab.lib.units import cm
from reportlab.pdfbase import pdfmetrics
from reportlab.pdfbase.ttfonts import TTFont
from datetime import datetime
import os

# ====== 1. 경로 설정 ======
train_dir = r"C:\aihub\springboot\AI2\src\main\resources\ad_data\train"
test_dir = r"C:\aihub\springboot\AI2\src\main\resources\ad_data\test"
output_dir = r"C:\aihub\springboot\AI2\src\main\resources\static\report"  # static 폴더 내에 저장
os.makedirs(output_dir, exist_ok=True)

graph_path = os.path.join(output_dir, "training_graph.png")
pdf_path = os.path.join(output_dir, "training_report.pdf")

# ====== 2. 데이터 전처리 ======
transform = transforms.Compose([
    transforms.Resize((224, 224)),
    transforms.ToTensor(),
    transforms.Normalize([0.5, 0.5, 0.5], [0.5, 0.5, 0.5])  # 정규화
])

# ====== 3. 데이터셋 및 데이터로더 ======
train_data = datasets.ImageFolder(train_dir, transform=transform)
test_data = datasets.ImageFolder(test_dir, transform=transform)

train_loader = DataLoader(train_data, batch_size=16, shuffle=True)
test_loader = DataLoader(test_data, batch_size=16, shuffle=False)

print("클래스:", train_data.classes)  # 예: ['ad', 'normal']

# ====== 4. 모델 정의 ======
device = torch.device("cuda" if torch.cuda.is_available() else "cpu")
model = models.resnet18(weights=ResNet18_Weights.IMAGENET1K_V1)
model.fc = nn.Linear(model.fc.in_features, 2)  # 2개 클래스를 구분 (AD vs Normal)
model = model.to(device)

# ====== 5. 손실 함수 및 옵티마이저 ======
criterion = nn.CrossEntropyLoss()  # 다중 클래스 분류 손실 함수
optimizer = optim.Adam(model.parameters(), lr=0.001)  # Adam 옵티마이저

# ====== 6. 학습 루프 ======
num_epochs = 5
train_losses = []
train_accuracies = []

for epoch in range(num_epochs):
    model.train()
    running_loss = 0.0
    correct, total = 0, 0

    for images, labels in train_loader:
        images, labels = images.to(device), labels.to(device)

        optimizer.zero_grad()  # 기울기 초기화
        outputs = model(images)  # 모델 예측
        loss = criterion(outputs, labels)  # 손실 계산
        loss.backward()  # 역전파
        optimizer.step()  # 옵티마이저 스텝

        running_loss += loss.item()
        _, preds = torch.max(outputs, 1)  # 예측값 얻기
        correct += (preds == labels).sum().item()  # 정확도 계산
        total += labels.size(0)

    # 에폭마다 손실과 정확도 출력
    epoch_loss = running_loss / len(train_loader)
    epoch_acc = correct / total
    train_losses.append(epoch_loss)
    train_accuracies.append(epoch_acc)

    print(f"Epoch {epoch + 1}, Loss: {epoch_loss:.4f}, Accuracy: {100 * epoch_acc:.2f}%")

# ====== 7. 테스트 정확도 측정 ======
model.eval()  # 모델 평가 모드로 전환
correct, total = 0, 0
with torch.no_grad():  # 평가 시 기울기 계산 방지
    for images, labels in test_loader:
        images, labels = images.to(device), labels.to(device)
        outputs = model(images)
        _, preds = torch.max(outputs, 1)
        correct += (preds == labels).sum().item()
        total += labels.size(0)

test_acc = correct / total
print(f"테스트 정확도: {100 * test_acc:.2f}%")

# ====== 8. 그래프 저장 ======
plt.figure(figsize=(12, 5))
plt.rcParams['font.family'] = 'Malgun Gothic'  # 한글 깨짐 방지

# 손실률 변화 그래프
plt.subplot(1, 2, 1)
plt.plot(range(1, num_epochs + 1), train_losses, marker='o', color='red')
plt.title('손실률 변화')
plt.xlabel('반복횟수')
plt.ylabel('손실률')

# 정확도 변화 그래프
plt.subplot(1, 2, 2)
plt.plot(range(1, num_epochs + 1), [acc * 100 for acc in train_accuracies], marker='o', color='blue')
plt.title('정확도 변화')
plt.xlabel('반복횟수')
plt.ylabel('정확도 (%)')

plt.tight_layout()
plt.savefig(graph_path)  # 그래프 이미지 저장
plt.close()
print(f"그래프 저장 완료: {graph_path}")

# ====== 9. PDF 리포트 생성 ======
# ① 한글 폰트 등록 (malgun.ttf 경로는 윈도우 기준)
pdfmetrics.registerFont(TTFont('Malgun', r'C:\Windows\Fonts\malgun.ttf'))

# ② PDF 시작
c = canvas.Canvas(pdf_path, pagesize=A4)
width, height = A4

# ③ 제목
c.setFont("Malgun", 20)
c.drawCentredString(width / 2, height - 2 * cm, "MRI 이미지 분류 리포트")

# ④ 내용
c.setFont("Malgun", 14)
c.drawString(3 * cm, height - 4 * cm, f"테스트 정확도: {100 * test_acc:.2f}%")
c.drawString(3 * cm, height - 5 * cm, f"훈련 반복 횟수: {num_epochs}")

# ⑤ 그래프 이미지 삽입
img_width = 14 * cm
img_height = 10 * cm
img_x = (width - img_width) / 2
img_y = height - 15 * cm
c.drawImage(graph_path, img_x, img_y, width=img_width, height=img_height)

# ⑥ 작성일
c.setFont("Malgun", 10)
date_str = datetime.today().strftime("%Y-%m-%d")
c.drawRightString(width - 2 * cm, 1.5 * cm, f"작성일: {date_str}")

# ⑦ 저장
c.save()
print(f"PDF 리포트 저장 완료: {pdf_path}")
