package pl.jakubmichalowski.kainos.jsonMappings;

import lombok.Getter;
import lombok.Setter;

/**
 * Created by Jakub Michałowski on 03.06.2018.
 * All rights reserved.
 */
@Getter
@Setter
public class CryptoData {
    private String name;
    private float data[];

    public CryptoData(String name, int size){
        this.data = new float[size];
        this.name = name;
    }
}
