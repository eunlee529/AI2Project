from ultralytics import YOLO
import cv2
import numpy as np

# 모델 및 비디오 경로 설정
model_path = r"C:\AI_project\robotest\yolov5\runs\pose\debris_pose\weights\best.pt"
video_path = r"C:\aihub\springboot\AI2\src\main\resources\robodata\ski1.mp4"

# 모델 로딩
model = YOLO(model_path)

# 비디오 열기
cap = cv2.VideoCapture(video_path)

# 새로운 사용자 정의 skeleton 구조 (상체와 하체 연결)
skeleton = [
    (7, 8), (8, 9), (9, 10), (10, 11), (11, 14),  # new-point-7 -> 8 -> 9 -> 10 -> 11 -> 14
    (14, 17), (17, 18), (18, 19), (19, 20),         # new-point-14 -> 17 -> 18 -> 19 -> 20
    (7, 2), (8, 3), (9, 4), (10, 5),                # 상체 연결: 7 -> 2 -> 3 -> 4 -> 5
    (14, 6)                                       # center -> 6 (상체와 하체 연결)
]

while True:
    ret, frame = cap.read()
    if not ret:
        cap.set(cv2.CAP_PROP_POS_FRAMES, 0)
        continue

    # 예측
    results = model.predict(source=frame, conf=0.1, save=False)
    annotated_frame = frame.copy()

    keypoints = results[0].keypoints
    if keypoints is not None:
        for person in keypoints.xy:
            points = []
            # 키포인트에서 (x, y) 좌표와 confidence 가져오기
            for i, point in enumerate(person):
                x, y = point[:2]  # (x, y 좌표)
                conf = point[2] if len(point) > 2 else 1  # confidence 값 (없을 경우 1로 설정)

                if conf > 0.2:  # confidence가 0.2 이상인 키포인트만 표시
                    points.append((int(x), int(y)))  # 좌표 저장
                    cv2.circle(annotated_frame, (int(x), int(y)), 4, (0, 255, 0), -1)  # 보라색 점으로 표시

            # 선 그리기
            for pt1, pt2 in skeleton:  # 사람이 가진 뼈대 구조에 따라 선 연결
                if pt1 < len(points) and pt2 < len(points):  # 인덱스 범위 확인
                    x1, y1 = points[pt1]
                    x2, y2 = points[pt2]
                    cv2.line(annotated_frame, (int(x1), int(y1)), (int(x2), int(y2)), (255, 0, 0), 2)  # 파란색 선으로 연결

    # 출력
    cv2.imshow("Pose Estimation with Skeleton", annotated_frame)

    if cv2.waitKey(1) & 0xFF == ord("q"):
        break

cap.release()
cv2.destroyAllWindows()
