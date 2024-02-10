package com.lsoria.qrsec.controller.rest;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import com.lsoria.qrsec.domain.dto.UserDTO;
import com.lsoria.qrsec.domain.dto.mapper.UserMapper;
import com.lsoria.qrsec.domain.model.Role;
import com.lsoria.qrsec.domain.model.User;
import com.lsoria.qrsec.service.UserService;
import com.lsoria.qrsec.service.exception.ConflictException;
import com.lsoria.qrsec.service.exception.NotFoundException;

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
import org.springframework.web.bind.annotation.RequestHeader;
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
    @GetMapping("${api.path.admin.users}")
    @Parameter(
            name = "X-Email",
            description = "Email of the Admin that wants to see the Users",
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
                    responseCode = "401",
                    description = "User is not Authorized (not a privileged User)",
                    content = @Content()
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "User not found on the database",
                    content = @Content()
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Some error prevented the Users from being retrieved",
                    content = @Content()
            )
    })
    public ResponseEntity<List<UserDTO>> getUsers(
            @RequestHeader(value = "X-Email") @NotNull String email
    ) {

        try {

            // TODO: Add @PreAuthorize("hasAuthority('ADMIN')")
            if (!userService.userIsAuthorized(email, new Role(Role.ADMIN))) {

                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();

            }

            List<User> users = userService.findAll();
            if (users.isEmpty()) {

                return ResponseEntity.noContent().build();

            }

            return ResponseEntity.ok(users.stream().map(userMapper::userToUserDTO).collect(Collectors.toList()));

        } catch (NotFoundException exception) {

            log.error("Message: {}.", exception.getMessage());

            return ResponseEntity.notFound().build();

        } catch (Exception exception) {

            log.error("Couldn't find all the Users.\nMessage: {}.\nStackTrace:\n{}", exception.getMessage(), exception.getStackTrace());

        }

        return ResponseEntity.internalServerError().build();

    }

    @Operation(summary = "Get a User (privileged, guard or self)", description = "Get an specific User")
    @GetMapping("${api.path.users}/{id}")
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
                    responseCode = "401",
                    description = "User is not Authorized",
                    content = @Content()
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
            @RequestHeader(value = "X-Email") @NotNull String email,
            @PathVariable @NotNull String id
    ) {

        try {

            // TODO: Replace with @PreAuthorize("hasAuthority('OWNER') or hasAuthority('ADMIN') or hasAuthority('GUARD')")
            Optional<User> currentUser = userService.findByUsername(email);
            if (currentUser.isEmpty()) {

                return ResponseEntity.notFound().build();

            }
            Optional<User> user = userService.findOne(id);
            if (user.isEmpty()) {

                return ResponseEntity.notFound().build();

            }
            if ((currentUser.get().getAuthorities() == null || currentUser.get().getAuthorities().isEmpty()) || (currentUser.get().getAuthorities().contains(new Role(Role.OWNER)) && !Objects.equals(currentUser.get().getId(), user.get().getId()))) {

                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();

            }

            if (!userService.userIsAuthorized(email, new Role(Role.ADMIN)) && !user.get().isEnabled()) {

                return ResponseEntity.notFound().build();

            }

            UserDTO userDTO = userMapper.userToUserDTO(user.get());

            return ResponseEntity.ok(userDTO);

        } catch (Exception exception) {

            log.error("Couldn't find the Guest {}.\nMessage: {}.\nStackTrace:\n{}", id, exception.getMessage(), exception.getStackTrace());

        }

        return ResponseEntity.internalServerError().build();

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

        try {

            User userToCreate = userMapper.userDTOToUser(userDTO);
            userToCreate.setId(null);
            userToCreate.setEnabled(false);

            User createdUser = userService.save(userToCreate);

            return ResponseEntity.status(HttpStatus.CREATED).body(userMapper.userToUserDTO(createdUser));

        } catch (ConflictException conflictException) {

            log.error("Message: {}.", conflictException.getMessage());

            return ResponseEntity.status(HttpStatus.CONFLICT).build();

        } catch (Exception exception) {

            log.error("Couldn't create User with values:\n{}\nMessage: {}.\nStackTrace:\n{}", userDTO, exception.getMessage(), exception.getStackTrace());

        }

        return ResponseEntity.internalServerError().build();

    }

    @Operation(summary = "Update a User (privileged or self)", description = "Update user's information")
    @PutMapping("${api.path.users}/{id}")
    @Parameter(
            name = "X-Email",
            description = "Email of the User that wants to update the User",
            in = ParameterIn.HEADER,
            required = true,
            schema = @Schema(
                    type = "string",
                    example = "exa@mple.com"
            )
    )
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
                    responseCode = "401",
                    description = "User is not Authorized",
                    content = @Content()
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
            @RequestHeader(value = "X-Email") @NotNull String email,
            @PathVariable @NotNull String id,
            @RequestBody @NotNull UserDTO userDTO
    ) {

        try {

            // TODO: Replace with @PreAuthorize("hasAuthority('OWNER') or hasAuthority('ADMIN')")
            Optional<User> currentUser = userService.findByUsername(email);
            if (currentUser.isEmpty()) {

                return ResponseEntity.notFound().build();

            }
            Optional<User> foundUser = userService.findOne(id);
            if (foundUser.isEmpty()) {

                return ResponseEntity.notFound().build();

            }
            if (currentUser.get().getAuthorities().contains(new Role(Role.ADMIN)) || (currentUser.get().getAuthorities().contains(new Role(Role.OWNER)) && !Objects.equals(currentUser.get().getId(), foundUser.get().getId()))) {

                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();

            }

            User userToUpdate = foundUser.get();
            User userNewValues = userMapper.userDTOToUser(userDTO);

            User updatedUser = userService.update(userToUpdate, userNewValues);

            return ResponseEntity.ok(userMapper.userToUserDTO(updatedUser));

        } catch (Exception exception) {

            log.error("Couldn't update User with values:\n{}\nMessage: {}.\nStackTrace:\n{}", userDTO, exception.getMessage(), exception.getStackTrace());

        }

        return ResponseEntity.internalServerError().build();

    }

    @Operation(summary = "Delete a User (privileged or self)", description = "The user will be disabled")
    @DeleteMapping("${api.path.users}/{id}")
    @Parameter(
            name = "X-Email",
            description = "Email of the User that wants to delete it's account",
            in = ParameterIn.HEADER,
            required = true,
            schema = @Schema(
                    type = "string",
                    example = "exa@mple.com"
            )
    )
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
                    responseCode = "401",
                    description = "User is not Authorized",
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
    public ResponseEntity<UserDTO> deleteUser(
            @RequestHeader(value = "X-Email") @NotNull String email,
            @PathVariable @NotNull String id
    ) {

        try {

            Optional<User> currentUser = userService.findByUsername(email);
            if (currentUser.isEmpty()) {

                return ResponseEntity.notFound().build();

            }
            // TODO: Replace with @PreAuthorize("hasAuthority('OWNER') or hasAuthority('ADMIN')")
            if ((!Objects.equals(currentUser.get().getId(), id) && userService.userIsAuthorized(email, new Role(Role.OWNER))) || userService.userIsAuthorized(email, new Role(Role.GUARD))) {

                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();

            }

            userService.delete(id);

            return ResponseEntity.noContent().build();

        } catch (NotFoundException exception) {

            log.error("Message: {}.", exception.getMessage());

            return ResponseEntity.notFound().build();

        } catch (Exception exception) {

            log.error("Couldn't delete User {}.\nMessage: {}.\nStackTrace:\n{}", id, exception.getMessage(), exception.getStackTrace());

        }

        return ResponseEntity.internalServerError().build();

    }

}
