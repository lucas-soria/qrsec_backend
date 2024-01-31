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
                    description = "Users successfully retrieved"
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
    @Parameter(description = "User ID", required = true, example = "5f15a5256d2a2a1ac0e4d999", in = ParameterIn.PATH)
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "User successfully retrieved"
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
            @PathVariable String id
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
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "201",
                    description = "User successfully created"
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
            @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "New User", required = true, content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = UserDTO.class))) @RequestBody @NotNull UserDTO user
    ) {

        try {
            User createdUser = userService.save(userMapper.userDTOToUser(user));

            return ResponseEntity.ok(userMapper.userToUserDTO(createdUser));
        } catch (ConflictException conflictException) {
            log.error("Couldn't create USER.\nMessage: {}.", conflictException.getMessage());

            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        } catch (Exception exception) {
            log.error("Couldn't create USER {}.\nMessage: {}.\nStackTrace:\n{}", user, exception.getMessage(), exception.getStackTrace());
        }

        return ResponseEntity.internalServerError().build();

    }

    @Operation(summary = "Update a User", description = "Update user's information")
    @PutMapping("${api.path.users}/{id}")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "User successfully updated"
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
    public ResponseEntity<User> updateUser(
            @Parameter(description = "User ID", required = true, example = "5f15a5256d2a2a1ac0e4d999", in = ParameterIn.PATH) @PathVariable String id,
            @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Updated User", required = true, content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = User.class))) @RequestBody @NotNull User user
    ) {

        log.info("REST request to update User {}: {}", id, user);
        return ResponseEntity.ok(user); // TODO: Use correct method

    }

    @Operation(summary = "Delete a User", description = "Delete a User")
    @DeleteMapping("${api.path.users}/{id}")
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
            @Parameter(description = "User ID", required = true, example = "5f15a5256d2a2a1ac0e4d999", in = ParameterIn.PATH) String id
    ) {

        log.info("REST request to delete User {}", id); // TODO: Delete User

    }

}