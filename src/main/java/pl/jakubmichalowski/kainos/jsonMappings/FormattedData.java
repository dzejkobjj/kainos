package pl.jakubmichalowski.kainos.jsonMappings;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.Date;

/**
 * Created by Jakub Michałowski on 01.06.2018.
 * All rights reserved.
 */
@Getter @Setter
public class FormattedData {
    private CryptoData ethereum;
    private CryptoData bitcoin;
    private CryptoData litcoin;
    private String[] dates;

    public FormattedData(int size){
        this.dates = new String[size];
    }
}
