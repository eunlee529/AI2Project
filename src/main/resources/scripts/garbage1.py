import cv2
from ultralytics import YOLO

model_path1 = r"C:\AI_project\robotest\yolov5\runs\detect\debris_yolov88\weights\best.pt"
model_path2 = r"C:\aihub\springboot\AI2\src\main\resources\robodata\trashh.mp4"

model = YOLO(model_path1)

cap = cv2.VideoCapture(model_path2)

while True:
    ret, frame = cap.read()
    if not ret:
        break

    # conf 값 적당히 올려보기 (0.25 ~ 0.4 추천)
    results = model.predict(
        source=frame,
        show=False,
        conf=0.3,
        task="segment"   # segmentation 모델이면 꼭 넣어야 함
    )

    # detection 모델이면 boxes=True, segmentation이면 masks=True
    annotated_frame = results[0].plot(masks=True, boxes=True)

    cv2.imshow("test", annotated_frame)

    if cv2.waitKey(1) & 0xFF == ord("q"):
        break

cap.release()
cv2.destroyAllWindows()
