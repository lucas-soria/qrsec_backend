package com.lsoria.qrsec.security.filter;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.lsoria.qrsec.domain.dto.JsonUserLoginDTO;
import com.lsoria.qrsec.domain.model.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import javax.servlet.FilterChain;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.time.Instant;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@Slf4j
public class CustomAuthenticationFilter extends UsernamePasswordAuthenticationFilter {

    private AuthenticationManager authenticationManager;
    private String jsonPassword;
    private String jsonUsername;

    public CustomAuthenticationFilter() {
        super();
    }

    /*
    public CustomAuthenticationFilter(AuthenticationManager authenticationManager) {
        this.authenticationManager = authenticationManager;
    }
    */

    @Override
    public Authentication attemptAuthentication(jakarta.servlet.http.HttpServletRequest request, jakarta.servlet.http.HttpServletResponse response) throws AuthenticationException {
        if ("application/json".equals(request.getHeader("Content-Type"))) {
            try {
                /*
                 * HttpServletRequest can be read only once
                 */
                StringBuilder stringBuffer = new StringBuilder();
                String line;

                BufferedReader reader = request.getReader();
                while ((line = reader.readLine()) != null){
                    stringBuffer.append(line);
                }

                //json transformation
                ObjectMapper mapper = new ObjectMapper();
                JsonUserLoginDTO jsonUserLoginDTO = mapper.readValue(stringBuffer.toString(), JsonUserLoginDTO.class);
                this.jsonUsername = jsonUserLoginDTO.getUsername();
                this.jsonPassword = jsonUserLoginDTO.getPassword();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(this.jsonUsername, this.jsonPassword);
        return this.authenticationManager.authenticate(authenticationToken);
        // return super.attemptAuthentication(request, response);
    }

    @Override
    protected String obtainPassword(jakarta.servlet.http.HttpServletRequest request) {
        String password;
        if ("application/json".equals(request.getHeader("Content-Type"))) {
            password = this.jsonPassword;
        } else {
            password = super.obtainPassword(request);
        }
        return password;
        // return super.obtainPassword(request);
    }

    @Override
    protected String obtainUsername(jakarta.servlet.http.HttpServletRequest request) {
        String username;
        if ("application/json".equals(request.getHeader("Content-Type"))) {
            username = this.jsonUsername;
        } else {
            username = super.obtainUsername(request);
        }
        return username;
        // return super.obtainUsername(request);
    }

    @Override
    protected void setDetails(jakarta.servlet.http.HttpServletRequest request, UsernamePasswordAuthenticationToken authRequest) {
        super.setDetails(request, authRequest);
    }

    @Override
    public void setUsernameParameter(String usernameParameter) {
        super.setUsernameParameter(usernameParameter);
    }

    @Override
    public void setPasswordParameter(String passwordParameter) {
        super.setPasswordParameter(passwordParameter);
    }

    @Override
    public void setPostOnly(boolean postOnly) {
        super.setPostOnly(postOnly);
    }

    public void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain, Authentication authentication) throws IOException {
        User user = (User)authentication.getPrincipal();
        Algorithm algorithm = Algorithm.HMAC256("secret".getBytes());  // TODO: Change this for the public key
        String access_token = JWT.create()
                .withIssuer(request.getRequestURL().toString())
                .withSubject(user.getUsername())
                .withClaim("first_name", user.getFirstName())
                .withClaim("last_name", user.getLastName())
                .withClaim("roles", user.getAuthorities().stream().map(GrantedAuthority::getAuthority).collect(Collectors.toList()))
                .withExpiresAt(Date.from(Instant.now().plusSeconds(5 * 60 * 60)))
                .sign(algorithm);
        String refresh_token = JWT.create()
                .withIssuer(request.getRequestURL().toString())
                .withSubject(user.getUsername())
                .withExpiresAt(Date.from(Instant.now().plusSeconds(10 * 60 * 60)))
                .sign(algorithm);
        Map<String, String> tokens = new HashMap<>();
        tokens.put("access_token", access_token);
        tokens.put("refresh_token", refresh_token);
        response.setContentType(APPLICATION_JSON_VALUE);
        new ObjectMapper().writeValue(response.getOutputStream(), tokens);
    }
}
