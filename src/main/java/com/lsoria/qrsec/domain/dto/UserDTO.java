package com.lsoria.qrsec.domain.dto;

import java.util.HashSet;
import java.util.Set;

import com.lsoria.qrsec.domain.model.Address;
import com.lsoria.qrsec.domain.model.Role;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserDTO {

    @Schema(accessMode = Schema.AccessMode.READ_ONLY)
    private String id;
    private String email;
    private String password;
    private Set<Role> authorities = new HashSet<>();
    private String firstName;
    private String lastName;
    private String dni;
    private Address address;
    private String phone;
    @Schema(accessMode = Schema.AccessMode.READ_ONLY)
    private Boolean enabled = false;

}
