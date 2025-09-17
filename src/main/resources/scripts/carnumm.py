import sys
import cv2
import easyocr
import json
import numpy as np
import io
import re

# UTF-8 강제 출력
sys.stdout = io.TextIOWrapper(sys.stdout.buffer, encoding='utf-8')

try:
    # 이미지 읽기 (Java에서 stdin으로 전달됨)
    input_stream = sys.stdin.buffer.read()
    if not input_stream:
        sys.stdout.write(json.dumps({"error": "이미지 입력이 비어있습니다."}, ensure_ascii=False) + "\n")
        sys.stdout.flush()
        sys.exit(0)

    img = cv2.imdecode(np.frombuffer(input_stream, np.uint8), cv2.IMREAD_COLOR)
    if img is None:
        sys.stdout.write(json.dumps({"error": "이미지를 디코딩할 수 없습니다."}, ensure_ascii=False) + "\n")
        sys.stdout.flush()
        sys.exit(0)

    # 전처리
    gray = cv2.cvtColor(img, cv2.COLOR_BGR2GRAY)
    _, thresh = cv2.threshold(gray, 0, 255, cv2.THRESH_BINARY + cv2.THRESH_OTSU)

    if thresh is None:
        sys.stdout.write(json.dumps({"error": "이미지 전처리 실패"}, ensure_ascii=False) + "\n")
        sys.stdout.flush()
        sys.exit(0)

    # OCR
    reader = easyocr.Reader(['ko', 'en'], gpu=False)
    results = reader.readtext(thresh)

    if results:
        # OCR 결과를 왼쪽→오른쪽 순으로 정렬
        results_sorted = sorted(results, key=lambda x: x[0][0][0])

        # 글자 합치기 (공백 제거)
        texts = [text.replace(" ", "") for (_, text, prob) in results_sorted]
        combined = "".join(texts)

        # 번호판 패턴 찾기 (숫자2~3 + 한글 + 숫자4)
        match = re.search(r"(\d{2,3})([가-힣])(\d{4})", combined)

        if match:
            plate_text = match.group(1) + match.group(2) + " " + match.group(3)
        else:
            plate_text = combined

        # 평균 정확도
        probs = [prob for (_, text, prob) in results_sorted]
        avg_prob = round(sum(probs) / len(probs), 2)

        data = {
            "plate": plate_text,
            "accuracy": avg_prob
        }
    else:
        data = {"error": "번호판을 인식할 수 없습니다."}

    # ✅ 반드시 개행 + flush
    sys.stdout.write(json.dumps(data, ensure_ascii=False) + "\n")
    sys.stdout.flush()

except Exception as e:
    # 에러 발생 시에도 JSON 출력 보장
    error_data = {"error": f"스크립트 실행 중 오류 발생: {str(e)}"}
    sys.stdout.write(json.dumps(error_data, ensure_ascii=False) + "\n")
    sys.stdout.flush()
    sys.exit(1)
