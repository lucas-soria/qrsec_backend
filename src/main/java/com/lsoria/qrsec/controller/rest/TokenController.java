package com.lsoria.qrsec.controller.rest;

import java.io.IOException;
import java.time.Instant;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.lsoria.qrsec.domain.model.User;
import com.lsoria.qrsec.service.UserService;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.http.HttpStatus.UNAUTHORIZED;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@Slf4j
@RestController
@Tag(name = "Token controller", description = "Token re-generation endpoint")
@RequestMapping(path = "${api.path}", produces = MediaType.APPLICATION_JSON_VALUE)
public class TokenController {

    @Autowired
    UserService userService;

    @Deprecated
    @Operation(summary = "Refresh your token", description = "Get a new token from the refresh token")
    @GetMapping("${api.path.token.refresh}")
    @ApiResponses(value = {
        @ApiResponse(
                responseCode = "200",
                description = "Token successfully refreshed"
        ),
        @ApiResponse(
                responseCode = "401",
                description = "The user couldn't refresh the token",
                content = @Content()
        ),
        @ApiResponse(
                responseCode = "500",
                description = "Some error prevented the Token from being refreshed",
                content = @Content()
        )
    })
    public void refreshToken(HttpServletRequest request, HttpServletResponse response) throws IOException {
        log.info("REST request to refresh token");
        String authorizationHeader = request.getHeader(AUTHORIZATION);
        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            try {
                String refresh_token = authorizationHeader.substring("Bearer ".length());
                Algorithm algorithm = Algorithm.HMAC256("secret".getBytes());  // TODO: Change this for the public key
                JWTVerifier jwtVerifier = JWT.require(algorithm).build();
                DecodedJWT decodedJWT = jwtVerifier.verify(refresh_token);
                String username = decodedJWT.getSubject();
                User user;
                Optional<User> optionalUser = userService.findByUsername(username);
                if (optionalUser.isPresent()) {
                    user = optionalUser.get();
                } else {
                    throw new RuntimeException("User not found");
                }
                String access_token = JWT.create()
                        .withIssuer(request.getRequestURL().toString())
                        .withSubject(user.getUsername())
                        .withClaim("first_name", user.getFirstName())
                        .withClaim("last_name", user.getLastName())
                        .withClaim("roles", user.getAuthorities().stream().map(GrantedAuthority::getAuthority).collect(Collectors.toList()))
                        .withExpiresAt(Date.from(Instant.now().plusSeconds(5 * 60 * 60)))
                        .sign(algorithm);
                Map<String, String> tokens = new HashMap<>();
                tokens.put("access_token", access_token);
                tokens.put("refresh_token", refresh_token);
                response.setContentType(APPLICATION_JSON_VALUE);
                new ObjectMapper().writeValue(response.getOutputStream(), tokens);

            } catch (Exception exception) {
                log.error("Error logging in : {}", exception.getMessage());
                response.setHeader("error", exception.getMessage());
                response.setStatus(UNAUTHORIZED.value());
                Map<String, String> error = new HashMap<>();
                error.put("error_message", exception.getMessage());
                response.setContentType(APPLICATION_JSON_VALUE);
                new ObjectMapper().writeValue(response.getOutputStream(), error);
            }
        } else {
            throw new RuntimeException("Refresh token is missing");
        }
    }

}
