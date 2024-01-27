package com.lsoria.qrsec.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class JsonUserLoginDTO {

    private String username;
    private String password;

}
