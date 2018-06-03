package pl.jakubmichalowski.kainos.utilities;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestTemplate;
import pl.jakubmichalowski.kainos.exceptions.BadDateFormatException;
import pl.jakubmichalowski.kainos.exceptions.DateOutOfRangeException;
import pl.jakubmichalowski.kainos.exceptions.WrongDateException;
import pl.jakubmichalowski.kainos.jsonMappings.CryptoData;
import pl.jakubmichalowski.kainos.jsonMappings.FormattedData;
import pl.jakubmichalowski.kainos.jsonMappings.Price;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Jakub Michałowski on 01.06.2018.
 * All rights reserved.
 */
public class DataGenerator {

    private List<Price> bitcoin;
    private List<Price> ethereum;
    private List<Price> litcoin;
    private DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");


    public DataGenerator(){
        getDataFromServer();
    }

    public FormattedData getData(String from, String to){

        LocalDate fromDate, toDate;

        try {
            fromDate = LocalDate.parse(from, formatter);
            toDate = LocalDate.parse(to, formatter);
        }catch (DateTimeParseException e){
            throw new BadDateFormatException();
        }

        if(fromDate.equals(toDate) || fromDate.isAfter(toDate))
            throw new WrongDateException();


        long days = ChronoUnit.DAYS.between(fromDate,toDate);

        int index = 0;
        int fromIndex;

        try {
            while (!bitcoin.get(index).getTime().toLocalDate().equals(toDate))
                index++;
            fromIndex = index;
            while (!bitcoin.get(fromIndex).getTime().toLocalDate().equals(fromDate))
                fromIndex++;
        }catch (IndexOutOfBoundsException e){
            throw new DateOutOfRangeException();
        }

        CryptoData bitcoin = new CryptoData("Bitcoin", Math.toIntExact(days+1));
        CryptoData ethereum = new CryptoData("Ethereum", Math.toIntExact(days+1));
        CryptoData litcoin = new CryptoData("Litcoin", Math.toIntExact(days+1));
        FormattedData fd = new FormattedData(Math.toIntExact(days+1));

        for(int i=Math.toIntExact(days);i>=0;i--, index++){

            fd.getDates()[i] = (this.bitcoin.get(index).getTime().format(formatter));

            bitcoin.getData()[i] = this.bitcoin.get(index).getAverage();
            if(index<this.ethereum.size())
                ethereum.getData()[i] = this.ethereum.get(index).getAverage();
            if(index<this.litcoin.size())
                litcoin.getData()[i] = this.litcoin.get(index).getAverage();

        }
        fd.setBitcoin(bitcoin);
        fd.setEthereum(ethereum);
        fd.setLitcoin(litcoin);

        return fd;
    }

    @Scheduled(cron = "0 0 01 * * ?")
    public void getDataFromServer() {
        RestTemplate restTemplate = new RestTemplate();
        String result = restTemplate.getForObject(ApiUrl.bitcoinUrl, String.class);
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            bitcoin = objectMapper.readValue(result, new TypeReference<List<Price>>(){});
        } catch (IOException e) {
            e.printStackTrace();
        }

        restTemplate = new RestTemplate();
        result = restTemplate.getForObject(ApiUrl.ethereumUrl, String.class);
        objectMapper = new ObjectMapper();
        try {
            ethereum = objectMapper.readValue(result, new TypeReference<List<Price>>(){});
        } catch (IOException e) {
            e.printStackTrace();
        }

        restTemplate = new RestTemplate();
        result = restTemplate.getForObject(ApiUrl.litcoinUrl, String.class);
        objectMapper = new ObjectMapper();
        try {
            litcoin = objectMapper.readValue(result, new TypeReference<List<Price>>(){});
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String getMinDate(){
        return bitcoin.get(bitcoin.size()-1).getTime().toLocalDate().format(formatter);
    }


    private static class ApiUrl{
        public static final String bitcoinUrl = "https://apiv2.bitcoinaverage.com/indices/global/history/BTCUSD?period=alltime&?format=json";
        public static final String ethereumUrl = "https://apiv2.bitcoinaverage.com/indices/global/history/ETHUSD?period=alltime&?format=json";
        public static final String litcoinUrl = "https://apiv2.bitcoinaverage.com/indices/global/history/LTCUSD?period=alltime&?format=json";

    }

}
