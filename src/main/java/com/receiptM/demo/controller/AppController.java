package com.receiptM.demo.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import kr.co.shineware.nlp.komoran.constant.DEFAULT_MODEL;
import kr.co.shineware.nlp.komoran.core.Komoran;
import kr.co.shineware.nlp.komoran.model.KomoranResult;
import kr.co.shineware.nlp.komoran.model.Token;
import org.springframework.core.io.ClassPathResource;
import org.springframework.web.bind.annotation.*;

import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class AppController {
    private final Komoran komoran;

    public AppController() {
        // Komoran 초기화 (FULL 모델 사용)
        this.komoran = new Komoran(DEFAULT_MODEL.FULL);
    }

    @GetMapping(value = "/analyzeFromFile", produces = "application/json; charset=UTF-8")
    public Map<String, String> analyzeFromFile() {
        try {
            // data.json 파일 불러오기
            ClassPathResource resource = new ClassPathResource("data.json");
            InputStream inputStream = resource.getInputStream();

            ObjectMapper objectMapper = new ObjectMapper();
            Map<String, String> jsonMap = objectMapper.readValue(inputStream, Map.class);
            String text = jsonMap.get("text");

            // 형태소 분석
            KomoranResult result = komoran.analyze(text);
            List<Token> tokenList = result.getTokenList();

            // NNP만 필터링
            List<String> properNouns = tokenList.stream()
                    .filter(token -> token.getPos().equals("NNP"))
                    .map(Token::getMorph)
                    .toList();

            // 결과 반환
            Map<String, String> response = new HashMap<>();
            response.put("입력 문장", text);
            response.put("고유명사(NNP)", String.join("",properNouns));
            return response;

        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "파일 읽기 실패: " + e.getMessage());
            return error;
        }
    }
}