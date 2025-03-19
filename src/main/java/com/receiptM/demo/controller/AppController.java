package com.receiptM.demo.controller;

import kr.co.shineware.nlp.komoran.constant.DEFAULT_MODEL;
import kr.co.shineware.nlp.komoran.core.Komoran;
import kr.co.shineware.nlp.komoran.model.KomoranResult;
import kr.co.shineware.nlp.komoran.model.Token;
import org.springframework.web.bind.annotation.*;

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

    @GetMapping("/analyze")
    public Map<String, Object> analyzeText(@RequestParam String text) {
        KomoranResult result = komoran.analyze(text);
        List<String> nouns = result.getNouns();
        List<Token> tokenList = result.getTokenList();

        // 형태소 분석 결과 저장
        Map<String, Object> response = new HashMap<>();
        response.put("plainText", result.getPlainText());
        response.put("nouns", nouns);
        response.put("tokens", tokenList.stream().map(token ->
                token.getMorph() + "/" + token.getPos() + "(" + token.getBeginIndex() + "," + token.getEndIndex() + ")"
        ).toList());

        return response;
    }
}
