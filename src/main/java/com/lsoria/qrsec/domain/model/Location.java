package com.lsoria.qrsec.domain.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Location {

    private String type;
    private List<Double> coordinates;

}
