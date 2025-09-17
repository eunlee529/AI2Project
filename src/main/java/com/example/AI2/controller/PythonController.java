package com.example.AI2.controller;

import com.example.AI2.dto.IrisDTO;
import com.example.AI2.entity.IrisEntity;
import com.example.AI2.repository.IrisRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;

@RestController
@RequestMapping(value="/python")
public class PythonController {
    @Autowired
    IrisRepository irisRepository;

    @PostMapping("/suwonout")
    public ResponseEntity<String> ss1(@RequestParam("bar_chart") MultipartFile barChartFile,
                                      @RequestParam("pie_chart") MultipartFile pieChartFile) throws IOException {

        // 저장 경로 설정 (원하면 변경 가능)
        File dir = new File("C:/ai/AI2/src/main/resources/static/image/");
        if (!dir.exists()) {
            dir.mkdirs(); // 디렉토리 없으면 생성
        }

        File barChartSaveFile = new File(dir, barChartFile.getOriginalFilename());
        barChartFile.transferTo(barChartSaveFile);

        File pieChartSaveFile = new File(dir, pieChartFile.getOriginalFilename());
        pieChartFile.transferTo(pieChartSaveFile);

        return ResponseEntity.ok("이미지 저장 성공");
    }

    @PostMapping("/irisout")
    public ResponseEntity<String> ss2(@RequestParam double loss,
                                      @RequestParam double accuracy,
                                      @RequestParam int epochs) throws IOException {

        System.out.println("PythonController/irisout");

        IrisDTO irisDTO=new IrisDTO();
        irisDTO.setLoss(loss);
        irisDTO.setAccuracy(accuracy);
        irisDTO.setEpochs(epochs);

        System.out.println();
        System.out.println("iris 데이터: "+irisDTO.toString());
        System.out.println();

        IrisEntity irisEntity=irisDTO.entity();
        irisRepository.save(irisEntity);

        return ResponseEntity.ok("데이터 수신 완료");
    }


}
