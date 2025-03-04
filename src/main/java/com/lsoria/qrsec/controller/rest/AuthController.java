package com.lsoria.qrsec.controller.rest;

import java.util.Optional;

import com.lsoria.qrsec.domain.dto.AuthResponseDTO;
import com.lsoria.qrsec.domain.dto.UserDTO;
import com.lsoria.qrsec.domain.dto.mapper.UserMapper;
import com.lsoria.qrsec.domain.model.User;
import com.lsoria.qrsec.service.UserService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.NotNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@Tag(name = "Auth controller", description = "Token Authentication")
@RequestMapping(path = "${api.path}", produces = MediaType.APPLICATION_JSON_VALUE)
public class AuthController {

    @Autowired
    UserService userService;

    @Autowired
    UserMapper userMapper;

    @Operation(summary = "Validates Google's JWT (privileged, guard or self)", description = "Validates that the specified token is valid and returns user info")
    @PostMapping("${api.path.auth}/google")
    @Parameter(
            name = "X-Email",
            description = "Email of the User that wants to see a User",
            in = ParameterIn.HEADER,
            required = true,
            schema = @Schema(
                    type = "string",
                    example = "exa@mple.com"
            )
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "JWT successfully validated",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = UserDTO.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "JWT is invalid",
                    content = @Content()
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "User doesn't exists",
                    content = @Content()
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Some error prevented the JWT from being validated",
                    content = @Content()
            )
    })
    public ResponseEntity<AuthResponseDTO> validateToken(
            @RequestHeader(value = "X-Email") @NotNull String email
    ) {

        try {

            // TODO: Replace with @PreAuthorize("hasAuthority('OWNER') or hasAuthority('ADMIN') or hasAuthority('GUARD')")
            Optional<User> currentUser = userService.findByUsername(email);
            if (currentUser.isEmpty()) {

                return ResponseEntity.notFound().build();

            }

            User user = currentUser.get();
            if (!user.getEnabled()) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
            }

            UserDTO userDTO = userMapper.userToUserDTO(currentUser.get());

            return ResponseEntity.ok(new AuthResponseDTO(userDTO.getAuthorities()));

        } catch (Exception exception) {

            log.error("Couldn't validate JWT {}.\nMessage: {}.\nStackTrace:\n{}", email, exception.getMessage(), exception.getStackTrace());

        }

        return ResponseEntity.internalServerError().build();

    }

}
