package com.lsoria.qrsec.domain.dto;

import com.lsoria.qrsec.domain.model.Location;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PublicInviteDTO {

    private String id;
    private Location location;

}