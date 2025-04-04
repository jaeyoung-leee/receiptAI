package com.receiptM.demo.controller;

import com.fasterxml.jackson.annotation.JsonProperty;
import kr.co.shineware.nlp.komoran.constant.DEFAULT_MODEL;
import kr.co.shineware.nlp.komoran.core.Komoran;
import kr.co.shineware.nlp.komoran.model.Token;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/receipt")
public class ReceiptController {

    // 📦 클라이언트에서 받은 원본 아이템
    static class ReceiptItem {
        public String name;
        public String count;

        @Override
        public String toString() {
            return "{name=" + name + ", count=" + count + "}";
        }
    }

    // ✅ 형태소 필터링 후 반환할 결과 객체
    static class FilteredReceiptItem {
        public String originalName;
        public String filteredName;
        public String count;

        public FilteredReceiptItem(String originalName, String filteredName, String count) {
            this.originalName = originalName;
            this.filteredName = filteredName;
            this.count = count;
        }

        @Override
        public String toString() {
            return "{originalName=" + originalName + ", filteredName=" + filteredName + ", count=" + count + "}";
        }
    }

    @PostMapping("/upload")
    public List<FilteredReceiptItem> uploadReceipt(@RequestBody List<ReceiptItem> items) {
        System.out.println("📥 [Spring] 클라이언트에서 전송된 데이터 수: " + items.size());

        Komoran komoran = new Komoran(DEFAULT_MODEL.FULL);
        Set<String> targetPos = Set.of("NNP", "SN", "SL"); // 고유명사, 숫자, 외국어

        List<FilteredReceiptItem> result = new ArrayList<>();

        for (ReceiptItem item : items) {
            System.out.println("▶ 원본 데이터: name = " + item.name + ", count = " + item.count);

            // 형태소 분석 및 필터링
            List<Token> tokens = komoran.analyze(item.name).getTokenList();
            String filtered = tokens.stream()
                    .filter(token -> targetPos.contains(token.getPos()))
                    .map(Token::getMorph)
                    .collect(Collectors.joining(" "));

            // 결과 생성
            FilteredReceiptItem filteredItem = new FilteredReceiptItem(
                    item.name,
                    filtered.trim(),
                    item.count
            );

            System.out.println("✔ 형태소 필터링 결과: " + filteredItem);
            result.add(filteredItem);
        }

        System.out.println("✅ [Spring] 전송 완료. 최종 반환 데이터 수: " + result.size());
        return result;
    }
}