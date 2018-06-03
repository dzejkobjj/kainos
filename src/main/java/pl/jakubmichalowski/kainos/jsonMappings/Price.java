package pl.jakubmichalowski.kainos.jsonMappings;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Created by Jakub Michałowski on 01.06.2018.
 * All rights reserved.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class Price {

    @Getter @Setter
    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss")
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    private LocalDateTime time;

    @Getter @Setter
    private float average;

    public Price(){};
    public Price(LocalDateTime time, float average){
        this.time = time;
        this.average = average;
    }


}
