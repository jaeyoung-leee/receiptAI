package com.receiptM.demo;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import kr.co.shineware.nlp.komoran.constant.DEFAULT_MODEL;
import kr.co.shineware.nlp.komoran.core.Komoran;
import kr.co.shineware.nlp.komoran.model.Token;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.io.InputStream;
import java.util.*;

@RestController
public class AnalysisController {

    static class Item {
        public String name;
        public int count;
    }

    static class CleanedItem {
        public String name;
        public String filteredName;
        public int count;

        public CleanedItem(String name, String filteredName, int count) {
            this.name = name;
            this.filteredName = filteredName;
            this.count = count;
        }
    }

    @GetMapping("/analyze")
    @ResponseBody
    public String analyze() throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        InputStream is = getClass().getClassLoader().getResourceAsStream("data.json");
        List<Item> itemList = mapper.readValue(is, new TypeReference<List<Item>>() {});

        Komoran komoran = new Komoran(DEFAULT_MODEL.FULL);
        Set<String> targetPos = new HashSet<>(Arrays.asList("NNP", "SN", "SL"));

        List<CleanedItem> results = new ArrayList<>();
        for (Item item : itemList) {
            List<Token> tokens = komoran.analyze(item.name).getTokenList();
            StringBuilder filtered = new StringBuilder();
            for (Token token : tokens) {
                if (targetPos.contains(token.getPos())) {
                    filtered.append(token.getMorph()).append(" ");
                }
            }
            results.add(new CleanedItem(item.name, filtered.toString().trim(), item.count));
        }

        // 👇 JSON을 예쁘게 포맷해서 문자열로 리턴
        return mapper.writerWithDefaultPrettyPrinter().writeValueAsString(results);
    }
}