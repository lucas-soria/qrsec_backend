package com.lsoria.qrsec.domain.dto;

import java.util.HashSet;
import java.util.Set;

import com.lsoria.qrsec.domain.model.Role;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AuthResponseDTO {

    private Set<Role> authorities = new HashSet<>();

}
