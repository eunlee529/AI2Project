import os
import sys
import io
import warnings
import whisper
import logging
import json

# Whisper 다운로드나 로딩 중 출력되는 경고 제거
os.environ["PYTHONWARNINGS"] = "ignore"
os.environ["TRANSFORMERS_NO_ADVISORY_WARNINGS"] = "1"
warnings.filterwarnings("ignore")
logging.getLogger("whisper").setLevel(logging.ERROR)

# UTF-8 강제 출력 (한글 깨짐 방지)
sys.stdout = io.TextIOWrapper(sys.stdout.buffer, encoding="utf-8")

try:
    # 파일 경로 인자 받기
    if len(sys.argv) < 2:
        sys.stdout.write(json.dumps({"error": "음성 파일 경로가 전달되지 않았습니다."}, ensure_ascii=False) + "\n")
        sys.stdout.flush()
        sys.exit(0)

    filename = sys.argv[1]

    if not os.path.exists(filename):
        sys.stdout.write(json.dumps({"error": f"파일을 찾을 수 없습니다: {filename}"}, ensure_ascii=False) + "\n")
        sys.stdout.flush()
        sys.exit(0)

    # Whisper 모델 로드
    model = whisper.load_model("base")

    # 음성 인식 실행
    result = model.transcribe(filename, language="ko")

    text = result.get("text", "").strip()

    if not text:
        data = {"error": "음성을 인식하지 못했습니다."}
    else:
        data = {"text": text}

    sys.stdout.write(json.dumps(data, ensure_ascii=False) + "\n")
    sys.stdout.flush()

except Exception as e:
    error_data = {"error": f"스크립트 실행 중 오류 발생: {str(e)}"}
    sys.stdout.write(json.dumps(error_data, ensure_ascii=False) + "\n")
    sys.stdout.flush()
    sys.exit(1)
