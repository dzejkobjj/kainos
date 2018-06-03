package pl.jakubmichalowski.kainos.controllers;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.util.JSONPObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import pl.jakubmichalowski.kainos.exceptions.WrongDateException;
import pl.jakubmichalowski.kainos.jsonMappings.FormattedData;
import pl.jakubmichalowski.kainos.jsonMappings.Price;
import pl.jakubmichalowski.kainos.utilities.DataGenerator;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


/**
 * Created by Jakub Michałowski on 01.06.2018.
 * All rights reserved.
 */
@RestController
public class ApiController {

    private DataGenerator dataGenerator;

    @GetMapping("/test")
    public List<Price> test() throws IOException {
        RestTemplate restTemplate = new RestTemplate();
        String result = restTemplate.getForObject("https://apiv2.bitcoinaverage.com/indices/global/history/BTCUSD?period=daily&?format=json", String.class);

        ObjectMapper objectMapper = new ObjectMapper();
        List<Price> data = new ArrayList<>();
        data = objectMapper.readValue(result, new TypeReference<List<Price>>(){});

        return data;
    }

    @GetMapping("/api")
    public FormattedData getDate(@RequestParam(value = "from") String fromDate, @RequestParam(value = "to") String toDate){

        FormattedData result;

        result = dataGenerator.getData(fromDate, toDate);


        return result;
    }

    @GetMapping("/api/min")
    public ArrayNode getMinDate(){
        ObjectMapper mapper = new ObjectMapper();

        ArrayNode arrayNode = mapper.createArrayNode();
        ObjectNode objectNode1 = mapper.createObjectNode();
        objectNode1.put("minDate", dataGenerator.getMinDate());
        arrayNode.add(objectNode1);
        return arrayNode;
    }

    @Autowired
    public void setDataGenerator(DataGenerator dataGenerator) {
        this.dataGenerator = dataGenerator;
    }
}
