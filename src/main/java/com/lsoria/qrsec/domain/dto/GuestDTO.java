package com.lsoria.qrsec.domain.dto;

import java.util.HashSet;
import java.util.Set;

import com.lsoria.qrsec.domain.model.User;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GuestDTO {

    @Schema(accessMode = Schema.AccessMode.READ_ONLY)
    private String id;
    private String firstName;
    private String lastName;
    private String dni;
    private String phone;
    private Set<User> owners = new HashSet<>();

}
