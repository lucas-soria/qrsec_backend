package com.lsoria.qrsec.domain.dto;

import com.lsoria.qrsec.domain.model.House;
import com.lsoria.qrsec.domain.model.Location;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AddressDTO {

    @Schema(accessMode = Schema.AccessMode.READ_ONLY)
    private String id;
    private String street;
    private Integer number;
    private House house;
    private Location location;

}
