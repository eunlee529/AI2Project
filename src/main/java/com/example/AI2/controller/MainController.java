package com.example.AI2.controller;

import com.example.AI2.entity.Hub2Entity;
import com.example.AI2.entity.Hub3Entity;
import com.example.AI2.entity.HubEntity;
import com.example.AI2.entity.IrisEntity;
import com.example.AI2.repository.Hub2Repository;
import com.example.AI2.repository.Hub3Repository;
import com.example.AI2.repository.HubRepository;
import com.example.AI2.repository.IrisRepository;
import com.example.AI2.service.Hub3Service;
import com.example.AI2.service.HubService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.*;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Controller
public class MainController {
    @Autowired
    IrisRepository irisRepository;
    @Autowired
    HubRepository hubRepository;
    @Autowired
    Hub2Repository hub2Repository;
    @Autowired
    Hub3Repository hub3Repository;
    @Autowired
    Hub3Service hub3Service;
    @Autowired
    HubService hubService;
    @GetMapping(value = {"/", "/main"})
    public String ss1(){
        return "main";
    }

    @GetMapping("/suwon")
    public String ss2() throws IOException {
        ProcessBuilder pb=new ProcessBuilder("python",
                "src/main/resources/scripts/suwon1.py");

        pb.redirectOutput(ProcessBuilder.Redirect.INHERIT); // 파이썬 stdout -> 자바 콘솔
        pb.redirectError(ProcessBuilder.Redirect.INHERIT);  // 파이썬 stderr -> 자바 콘솔

        Process process = pb.start();

        return "suwon";
    }

    @GetMapping("/suwon2")
    public String ss3() throws IOException {
        ProcessBuilder pb=new ProcessBuilder("python",
                "src/main/resources/scripts/suwon1.py");

        pb.redirectOutput(ProcessBuilder.Redirect.INHERIT); // 파이썬 stdout -> 자바 콘솔
        pb.redirectError(ProcessBuilder.Redirect.INHERIT);  // 파이썬 stderr -> 자바 콘솔

        Process process = pb.start();

        return "suwon2";
    }

    @GetMapping("/iris")
    public String ss4() throws IOException {
        ProcessBuilder processBuilder=new ProcessBuilder("python",
                "src/main/resources/scripts/iris1.py");
        Process process=processBuilder.start();

        BufferedReader br = new BufferedReader(new InputStreamReader(process.getInputStream(), "utf-8"));

        StringBuilder output = new StringBuilder();
        String line;

        while((line=br.readLine())!=null){
            output.append(line);
        }

        System.out.println("Maincontroller/iris");

        return "redirect:/irisview";
    }

    @GetMapping("/irisview")
    public String ss5(Model model){
        List<IrisEntity> list=irisRepository.findAll();
        model.addAttribute("list", list);

        return "irisView";
    }

    @GetMapping("/mrimg")
    public String runMriScript(Model model) throws IOException, InterruptedException {
        // 1. Python 파일 경로 설정
        String scriptPath = "src/main/resources/scripts/mri_ad.py";

        // 2. Python 스크립트 실행
        ProcessBuilder pb = new ProcessBuilder("python", scriptPath);
        pb.redirectOutput(ProcessBuilder.Redirect.INHERIT);
        pb.redirectError(ProcessBuilder.Redirect.INHERIT);
        Process process = pb.start();
        process.waitFor(); // Python 스크립트 종료까지 대기

        // 3. PDF 파일 경로 모델에 전달
        String pdfUrl = "/report/training_report.pdf";  // static/report 내 PDF 경로
        model.addAttribute("pdfUrl", pdfUrl);

        return "reportView";  // templates/reportView.html 페이지 반환
    }





    @GetMapping("robo1")
    public String ss6() throws IOException {
        ProcessBuilder processBuilder =
                new ProcessBuilder("python","src/main/resources/scripts/garbage1.py");

        processBuilder.redirectOutput(ProcessBuilder.Redirect.INHERIT);
        processBuilder.redirectError(ProcessBuilder.Redirect.INHERIT);

        processBuilder.start();
        return "main";
    }

    @GetMapping("robo2")
    public String ss7() throws IOException {
        ProcessBuilder processBuilder =
                new ProcessBuilder("python","src/main/resources/scripts/human1.py");

        processBuilder.redirectOutput(ProcessBuilder.Redirect.INHERIT);
        processBuilder.redirectError(ProcessBuilder.Redirect.INHERIT);

        processBuilder.start();
        return "main";
    }

    @GetMapping("robo3")
    public String ss8() throws IOException {
        ProcessBuilder processBuilder =
                new ProcessBuilder("python","src/main/resources/scripts/count1.py");

        processBuilder.redirectOutput(ProcessBuilder.Redirect.INHERIT);
        processBuilder.redirectError(ProcessBuilder.Redirect.INHERIT);

        processBuilder.start();
        return "main";
    }

    @GetMapping("robo4")
    public String ss9() throws IOException {
        ProcessBuilder processBuilder =
                new ProcessBuilder("python","src/main/resources/scripts/ski1.py");

        processBuilder.redirectOutput(ProcessBuilder.Redirect.INHERIT);
        processBuilder.redirectError(ProcessBuilder.Redirect.INHERIT);

        processBuilder.start();
        return "main";
    }

    @GetMapping("hub1")
    public String ss10(@RequestParam(defaultValue = "0") int page,
                       @RequestParam(required = false) String keyword,
                       Model model) {

        int pageSize = 10; // 한 페이지당 데이터 개수
        int blockSize = 5; // 페이지 블럭 크기

        // ✅ 전체 or 검색 개수 가져오기
        int count = hubService.countByKeyword(keyword);
        int totalPages = (int) Math.ceil((double) count / pageSize);

        // ✅ 마지막 빈 페이지 방지
        if (page >= totalPages && totalPages > 0) {
            return "redirect:/hub1?page=" + (totalPages - 1) + "&keyword=" + (keyword != null ? keyword : "");
        }

        // ✅ 페이징 적용된 데이터 조회
        List<HubEntity> list = hubService.getHubEntitiesWithPagination(keyword, page, pageSize);

        // ✅ 블럭 계산
        int currentBlock = page / blockSize;
        int startPage = currentBlock * blockSize;
        int endPage = Math.min(startPage + blockSize - 1, totalPages - 1);

        model.addAttribute("list", list);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", totalPages);
        model.addAttribute("keyword", keyword);
        model.addAttribute("startPage", startPage);
        model.addAttribute("endPage", endPage);
        model.addAttribute("blockSize", blockSize);

        return "hubView";
    }



    @GetMapping("hub2")
    public String ss11(@RequestParam String id, Model model) {
        HubEntity item = hubRepository.findById(id).orElse(null);
        model.addAttribute("item", item);
        return "hubView2";
    }

    @GetMapping("search")
    public String ss12(){
        return "hubSearch";
    }

    @GetMapping("search2")
    public String ss13(@RequestParam String keyword,
                       @RequestParam(defaultValue = "0") int page,
                       Model model) throws IOException {

        int pageSize = 5; // ✅ 1페이지당 5개
        int blockSize = 5; // ✅ 블럭 크기 (5 페이지 단위)

        // ✅ 검색 결과 총 개수
        int count = hubRepository.countByKeyword(keyword);
        int totalPages = (int) Math.ceil((double) count / pageSize);

        // ✅ 마지막 빈 페이지 방지
        if (page >= totalPages && totalPages > 0) {
            return "redirect:/search2?page=" + (totalPages - 1) + "&keyword=" + URLEncoder.encode(keyword, "UTF-8");
        }

        // ✅ 페이징 데이터 가져오기
        int startRow = page * pageSize + 1;
        int endRow = (page + 1) * pageSize;
        List<HubEntity> list = hubRepository.findAllByKeywordWithPagination(keyword, startRow, endRow);

        // ✅ 블럭 계산
        int currentBlock = page / blockSize;
        int startPage = currentBlock * blockSize;
        int endPage = Math.min(startPage + blockSize - 1, totalPages - 1);

        // ✅ 모델에 데이터 전달
        model.addAttribute("list", list);
        model.addAttribute("keyword", keyword);
        model.addAttribute("count", count);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", totalPages);
        model.addAttribute("startPage", startPage);
        model.addAttribute("endPage", endPage);
        model.addAttribute("blockSize", blockSize);

        return "hubSearchView";  // 검색 결과 페이지
    }


    @GetMapping("hub3")
    public String ss14(@RequestParam(required = false) String keyword,
                       @RequestParam(defaultValue = "0") int page,
                       Model model) {

        int pageSize = 10; // 한 페이지당 데이터 개수
        int blockSize = 5; // 페이지 블럭 크기

        int count = hub3Service.countByKeyword(keyword);
        int totalPages = (int) Math.ceil((double) count / pageSize);

        // ✅ 마지막 빈 페이지 방지
        if (page >= totalPages && totalPages > 0) {
            return "redirect:/hub3?page=" + (totalPages - 1) + "&keyword=" + (keyword != null ? keyword : "");
        }

        List<Hub3Entity> list = hub3Service.getHub3EntitiesWithPagination(keyword, page, pageSize);

        // ✅ 블럭 계산
        int currentBlock = page / blockSize; // 현재 블럭 번호
        int startPage = currentBlock * blockSize; // 블럭 시작 페이지
        int endPage = Math.min(startPage + blockSize - 1, totalPages - 1); // 블럭 끝 페이지

        model.addAttribute("list", list);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", totalPages);
        model.addAttribute("keyword", keyword);

        // ✅ 블럭 관련 변수 추가
        model.addAttribute("startPage", startPage);
        model.addAttribute("endPage", endPage);
        model.addAttribute("blockSize", blockSize);

        return "hubView3";
    }



    @GetMapping("hub4")
    public String ss16(@RequestParam String dcid, Model model) {
        Hub3Entity item = hub3Repository.findByDocId(dcid).orElse(null);
        model.addAttribute("item", item);
        return "hubView4";
    }

    @GetMapping("lawsch")
    public String sea1() {
        return "lawSearch";
    }

    @GetMapping("lawsch2")
    public String sea2(@RequestParam String keyword,
                       @RequestParam(defaultValue = "0") int page,
                       Model model) throws IOException {

        int pageSize = 5;   // ✅ 1페이지당 5개
        int blockSize = 5;  // ✅ 블럭 크기

        // ✅ 총 데이터 개수
        int count = hub3Repository.countByKeyword(keyword);
        int totalPages = (int) Math.ceil((double) count / pageSize);

        // ✅ 잘못된 페이지 방지
        if (page >= totalPages && totalPages > 0) {
            return "redirect:/lawsch2?page=" + (totalPages - 1) + "&keyword=" + URLEncoder.encode(keyword, "UTF-8");
        }

        // ✅ startRow, endRow 계산
        int startRow = page * pageSize + 1;
        int endRow = (page + 1) * pageSize;

        // ✅ 페이징된 데이터 조회
        List<Hub3Entity> list = hub3Repository.findAllByKeywordWithPagination(keyword, startRow, endRow);

        // ✅ 블럭 계산
        int currentBlock = page / blockSize;
        int startPage = currentBlock * blockSize;
        int endPage = Math.min(startPage + blockSize - 1, totalPages - 1);

        // ✅ 모델 전달
        model.addAttribute("keyword", keyword);
        model.addAttribute("count", count);
        model.addAttribute("list", list);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", totalPages);
        model.addAttribute("startPage", startPage);
        model.addAttribute("endPage", endPage);
        model.addAttribute("blockSize", blockSize);

        return "lawSearchView";  // ✅ 뷰 이름 그대로
    }



    @GetMapping("input")
    public String ss19(){
        return "hubInput";
    }

    @GetMapping("input2")
    public String ss15(@RequestParam String keyword,
                       @RequestParam(defaultValue = "0") int page,
                       Model model) throws IOException {

        int pageSize = 5; // ✅ 1페이지당 5개
        int blockSize = 5; // 블럭 크기 (5 페이지 단위로 표시)

        // ✅ 검색 결과 총 개수
        int count = hub2Repository.countByKeyword(keyword);
        int totalPages = (int) Math.ceil((double) count / pageSize);

        // ✅ 마지막 빈 페이지 방지
        if (page >= totalPages && totalPages > 0) {
            return "redirect:/input2?page=" + (totalPages - 1) + "&keyword=" + URLEncoder.encode(keyword, "UTF-8");
        }

        // ✅ 페이징 데이터 가져오기
        int startRow = page * pageSize + 1;
        int endRow = (page + 1) * pageSize;
        List<Hub2Entity> list = hub2Repository.findAllByKeywordWithPagination(keyword, startRow, endRow);

        // ✅ 블럭 계산
        int currentBlock = page / blockSize;
        int startPage = currentBlock * blockSize;
        int endPage = Math.min(startPage + blockSize - 1, totalPages - 1);

        // ✅ 모델에 데이터 전달
        model.addAttribute("list", list);
        model.addAttribute("keyword", keyword);
        model.addAttribute("count", count);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", totalPages);
        model.addAttribute("startPage", startPage);
        model.addAttribute("endPage", endPage);
        model.addAttribute("blockSize", blockSize);

        return "hubInputView";
    }


    @GetMapping("detail")
    public String ss20(@RequestParam("num") Long num, Model model){
        Hub2Entity entity=hub2Repository.findById(num).orElse(null);
        model.addAttribute("item", entity);
        return "inputDetail";
    }

    @GetMapping("/call1")
    public String getCallPage(Model model) {
        File folder = new File("src/main/resources/static/voicedata");
        String[] files = folder.list((dir, name) -> name.toLowerCase().endsWith(".wav"));
        model.addAttribute("files", files);
        return "call1";
    }

    @PostMapping("/call1")
    public String processVoiceFile(@RequestParam("file") MultipartFile file, Model model)
            throws IOException, InterruptedException {

        if (file.isEmpty()) {
            model.addAttribute("result", "파일이 업로드되지 않았습니다.");
            return "call1";
        }

        // 1️⃣ MultipartFile → 임시 파일 생성
        File tempFile = File.createTempFile("voice_", ".wav");
        file.transferTo(tempFile);

        try {
            // 2️⃣ Python 스크립트 실행
            String scriptPath = "C:/aihub/springboot/AI2/src/main/resources/scripts/callvoice.py";
            ProcessBuilder pb = new ProcessBuilder("python", scriptPath, tempFile.getAbsolutePath());
            pb.environment().put("PYTHONIOENCODING", "utf-8");
            pb.redirectErrorStream(true);

            Process process = pb.start();
            BufferedReader br = new BufferedReader(
                    new InputStreamReader(process.getInputStream(), StandardCharsets.UTF_8));
            StringBuilder output = new StringBuilder();
            String line;
            while ((line = br.readLine()) != null) {
                output.append(line);
            }
            process.waitFor();

            System.out.println("Python script output: " + output);

            // 3️⃣ 결과 파싱
            ObjectMapper mapper = new ObjectMapper();
            Map<String, Object> resultMap = mapper.readValue(output.toString(), Map.class);

            String text = (String) resultMap.get("text");
            if (text == null) {
                text = (String) resultMap.get("error");
                if (text == null) text = "변환 실패";
            }

            model.addAttribute("filename", file.getOriginalFilename());
            model.addAttribute("result", text);

        } finally {
            // 4️⃣ 임시 파일 삭제
            if (tempFile.exists()) {
                tempFile.delete();
            }
        }

        return "call1";
    }






    @GetMapping("/carno1")
    public String carno1(Model model) throws IOException, InterruptedException {
        String scriptPath = "C:\\aihub\\springboot\\AI2\\src\\main\\resources\\scripts\\carnumm.py";

        ProcessBuilder pb = new ProcessBuilder("python", scriptPath);

        // ✅ Python 출력 UTF-8 강제
        pb.environment().put("PYTHONIOENCODING", "utf-8");

        pb.redirectOutput(ProcessBuilder.Redirect.PIPE);
        pb.redirectError(ProcessBuilder.Redirect.PIPE);
        Process process = pb.start();
        process.waitFor();

        BufferedReader br = new BufferedReader(new InputStreamReader(process.getInputStream(), StandardCharsets.UTF_8));
        StringBuilder output = new StringBuilder();
        String line;
        while ((line = br.readLine()) != null) {
            output.append(line);
        }

        // ✅ 디버깅 출력
        System.out.println("Python script output: " + output.toString());

        ObjectMapper mapper = new ObjectMapper();
        Map<String, Object> resultMap = mapper.readValue(output.toString(), Map.class);

        // ✅ null 처리: plate는 기본값 설정
        String plate = (String) resultMap.get("plate");
        if (plate == null) {
            plate = "번호 없음";
        }

        // ✅ accuracy 값이 null일 경우 기본값 설정 (예: 0.0)
        Double accuracy = (Double) resultMap.get("accuracy");
        if (accuracy == null) {
            accuracy = 0.0;  // 기본값 0.0 설정
        }

        // ✅ 모델에 전달 (null 방지)
        model.addAttribute("plate", plate);
        model.addAttribute("accuracy", accuracy);

        // ✅ 기본 이미지 (static 폴더 기준)
        model.addAttribute("imagePath", "/image/carnum1.jpg");

        return "carno1";
    }

    @PostMapping("/carno1")
    public String carno1(@RequestParam("selectedImage") MultipartFile selectedImage, Model model) throws IOException, InterruptedException {
        // 파일을 메모리에서 바로 처리하기 위한 InputStream 생성
        InputStream inputStream = selectedImage.getInputStream();

        // Python 스크립트 실행
        String scriptPath = "C:/aihub/springboot/AI2/src/main/resources/scripts/carnumm.py";

        // ProcessBuilder를 사용하여 Python 스크립트에 이미지 데이터를 전달
        ProcessBuilder pb = new ProcessBuilder("python", scriptPath);

        // Python 스크립트에 파일을 넘기기 위한 InputStream 처리
        Process process = pb.start();
        OutputStream processInputStream = process.getOutputStream();

        // InputStream에서 데이터를 읽어 Python 스크립트로 전달
        byte[] buffer = new byte[1024];
        int bytesRead;
        while ((bytesRead = inputStream.read(buffer)) != -1) {
            processInputStream.write(buffer, 0, bytesRead);
        }

        processInputStream.close();
        inputStream.close();

        // 프로세스 완료 대기
        process.waitFor();

        // Python 스크립트의 출력 결과를 읽기
        BufferedReader br = new BufferedReader(new InputStreamReader(process.getInputStream(), StandardCharsets.UTF_8));
        StringBuilder output = new StringBuilder();
        String line;
        while ((line = br.readLine()) != null) {
            output.append(line);
        }

        // 디버깅 출력
        System.out.println("Python script output: " + output.toString());

        // 결과 파싱
        if (output.length() == 0) {
            System.err.println("Python output is empty.");
            return "error";  // 빈 출력 처리 시 에러 페이지로 리다이렉션
        }

        ObjectMapper mapper = new ObjectMapper();
        Map<String, Object> resultMap = mapper.readValue(output.toString(), Map.class);

        // 번호판과 정확도 값 처리
        String plate = (String) resultMap.get("plate");
        if (plate == null) {
            plate = "번호 없음";
        }

        Double accuracy = (Double) resultMap.get("accuracy");
        if (accuracy == null) {
            accuracy = 0.0;  // 기본값 0.0 설정
        }

        model.addAttribute("plate", plate);
        model.addAttribute("accuracy", accuracy);

        // 이미지 경로 설정 (static 폴더 내에서 이미지가 위치하는 경로)
        model.addAttribute("imagePath", "/image/" + selectedImage.getOriginalFilename());

        return "carno1";
    }



    @GetMapping("/select-image")
    public String selectImage(Model model) {
        // 이미지 디렉토리 경로
        String imageDirPath = "C:/aihub/springboot/AI2/src/main/resources/static/image";

        // 디렉토리 객체 생성
        File imageDir = new File(imageDirPath);

        // 이미지 파일 목록 가져오기 (.jpg, .png 확장자 파일만)
        String[] imageFiles = imageDir.list((dir, name) -> name.toLowerCase().endsWith(".jpg") || name.toLowerCase().endsWith(".png"));

        // 모델에 이미지 파일 목록 전달
        model.addAttribute("imageFiles", imageFiles);

        return "select-image"; // 이미지를 선택하는 페이지로 리턴
    }



}
