package com.lsoria.qrsec.domain.dto;

import com.lsoria.qrsec.domain.model.Address;
import com.lsoria.qrsec.domain.model.Role;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashSet;
import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserDTO {

    private String id;
    private String email;
    private String password;
    private Set<Role> authorities = new HashSet<>();
    private String firstName;
    private String lastName;
    private String dni;
    private Address address;
    private String phone;
    private Boolean enabled = false;

}
