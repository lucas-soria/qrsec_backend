package com.lsoria.qrsec.domain.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = "addresses")
public class Address {

    @Id
    private String id;
    private String street;
    private Integer number;
    @Indexed(unique = true, sparse = true)
    private House house;
    private Location location;

}
