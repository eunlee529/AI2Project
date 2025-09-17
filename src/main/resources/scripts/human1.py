import cv2
from ultralytics import YOLO

model_path1 = r"C:\AI_project\robotest\yolov5\runs\segment\debris_yolov8_seg2\weights\best.pt"
model_path2 = r"C:\aihub\springboot\AI2\src\main\resources\robodata\saram.mp4"
model = YOLO(model_path1)

cap=cv2.VideoCapture(model_path2)

while True:
    ret, frame = cap.read()

    if not ret:
        break

    results = model.predict(source=frame, show=False, conf=0.02, task='segment')
    annotated_frame = results[0].plot(masks=True, boxes=False)

    cv2.imshow('test', annotated_frame)

    if cv2.waitKey(1) & 0xFF == ord("q"):
        break

cap.release()
cv2.destroyAllWindows()

