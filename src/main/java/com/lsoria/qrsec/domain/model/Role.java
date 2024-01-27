package com.lsoria.qrsec.domain.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Role implements GrantedAuthority {

    public static final String ADMIN = "ADMIN";
    public static final String GUARD = "GUARD";
    public static final String OWNER = "OWNER";

    private String authority;

    @Override
    public String getAuthority() {
        return this.authority;
    }

}
