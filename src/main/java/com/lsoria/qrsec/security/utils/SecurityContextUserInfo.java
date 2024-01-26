package com.lsoria.qrsec.security.utils;

import com.lsoria.qrsec.domain.model.User;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;

public class SecurityContextUserInfo {

    private Authentication authentication;

    /*
        Get the password of the authenticated user: getCredentials()
        Get the assigned roles of the authenticated user: getAuthorities()
        Get further details of the authenticated user: getDetails()
     */

    public SecurityContextUserInfo() {
        this.authentication = SecurityContextHolder.getContext().getAuthentication();
    }

    public String getUsername() {
        Object principal = this.authentication.getPrincipal();
        String username;
        if (principal instanceof UserDetails) {
            username = ((User)principal).getUsername();
        } else {
            username = principal.toString();
        }
        return username;
    }

}
