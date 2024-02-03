package com.lsoria.qrsec.controller.rest;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import com.lsoria.qrsec.domain.dto.UserDTO;
import com.lsoria.qrsec.domain.dto.mapper.UserMapper;
import com.lsoria.qrsec.domain.model.User;
import com.lsoria.qrsec.service.UserService;
import com.lsoria.qrsec.service.exception.ConflictException;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.ArraySchema;
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
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@Tag(name = "User controller", description = "CRUD of users")
@RequestMapping(path = "${api.path}", produces = MediaType.APPLICATION_JSON_VALUE)
public class UserController {

    @Autowired
    UserService userService;

    @Autowired
    UserMapper userMapper;

    @Operation(summary = "Get all Users (privileged)", description = "Get all Users from the neighbourhood")
    @GetMapping("${api.path.users}")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Users successfully retrieved",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            array = @ArraySchema(
                                    schema = @Schema(implementation = UserDTO.class)
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "204",
                    description = "The neighbourhood has no Users",
                    content = @Content()
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Some error prevented the Users from being retrieved",
                    content = @Content()
            )
    })
    // TODO: Add @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<List<UserDTO>> getUsers() {

        List<User> users = userService.findAll();

        return ResponseEntity.ok(users.stream().map(userMapper::userToUserDTO).collect(Collectors.toList()));

    }

    @Operation(summary = "Get a User", description = "Get an specific User")
    @GetMapping("${api.path.users}/{id}")
    @Parameter(
            name = "id",
            description = "User uuid",
            in = ParameterIn.PATH,
            required = true,
            schema = @Schema(
                    type = "string",
                    format = "uuid",
                    example = "5f15a5256d2a2a1ac0e4d999"
            )
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "User successfully retrieved",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = UserDTO.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "User not found",
                    content = @Content()
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Some error prevented the User from being retrieved",
                    content = @Content()
            )
    })
    public ResponseEntity<UserDTO> getUser(
            @PathVariable @NotNull String id
    ) {

        Optional<User> user = userService.findOne(id);

        if (user.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        UserDTO userDTO = userMapper.userToUserDTO(user.get());

        return ResponseEntity.ok(userDTO);

    }

    @Operation(summary = "Create a User", description = "Save a user for later use on an invite")
    @PostMapping("${api.path.users}")
    @io.swagger.v3.oas.annotations.parameters.RequestBody(
            description = "New User",
            required = true,
            content = @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = UserDTO.class)
            )
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "201",
                    description = "User successfully created",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = UserDTO.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "209",
                    description = "User already existed",
                    content = @Content()
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Some error prevented the User from being created",
                    content = @Content()
            )
    })
    public ResponseEntity<UserDTO> createUser(
            @RequestBody @NotNull UserDTO userDTO
    ) {

        // TODO: Make id null

        try {
            User createdUser = userService.save(userMapper.userDTOToUser(userDTO));

            return ResponseEntity.ok(userMapper.userToUserDTO(createdUser));
        } catch (ConflictException conflictException) {
            log.error("Couldn't create USER.\nMessage: {}.", conflictException.getMessage());

            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        } catch (Exception exception) {
            log.error("Couldn't create USER {}.\nMessage: {}.\nStackTrace:\n{}", userDTO, exception.getMessage(), exception.getStackTrace());
        }

        return ResponseEntity.internalServerError().build();

    }

    @Operation(summary = "Update a User", description = "Update user's information")
    @PutMapping("${api.path.users}/{id}")
    @Parameter(
            name = "id",
            description = "User uuid",
            in = ParameterIn.PATH,
            required = true,
            schema = @Schema(
                    type = "string",
                    format = "uuid",
                    example = "5f15a5256d2a2a1ac0e4d999"
            )
    )
    @io.swagger.v3.oas.annotations.parameters.RequestBody(
            description = "Updated User",
            required = true,
            content = @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = UserDTO.class)
            )
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "User successfully updated",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = UserDTO.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "User not found",
                    content = @Content()
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Some error prevented the User from being updated",
                    content = @Content()
            )
    })
    public ResponseEntity<UserDTO> updateUser(
            @PathVariable @NotNull String id,
            @RequestBody @NotNull UserDTO userDTO
    ) {

        // TODO: Use Path id and replace in body after search

        log.info("REST request to update User {}: {}", id, userDTO);
        return ResponseEntity.ok(userDTO); // TODO: Use correct method

    }

    @Operation(summary = "Delete a User", description = "Delete a User")
    @DeleteMapping("${api.path.users}/{id}")
    @Parameter(
            name = "id",
            description = "User uuid",
            in = ParameterIn.PATH,
            required = true,
            schema = @Schema(
                    type = "string",
                    format = "uuid",
                    example = "5f15a5256d2a2a1ac0e4d999"
            )
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "204",
                    description = "User successfully deleted",
                    content = @Content()
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "User not found",
                    content = @Content()
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Some error prevented the User from being deleted or the Owner from being removed",
                    content = @Content()
            )
    })
    public void deleteGuest(
            @PathVariable @NotNull String id
    ) {

        log.info("REST request to delete User {}", id); // TODO: Delete User

    }

}
