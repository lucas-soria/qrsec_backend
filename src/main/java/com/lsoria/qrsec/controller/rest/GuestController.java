package com.lsoria.qrsec.controller.rest;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import com.lsoria.qrsec.domain.dto.GuestDTO;
import com.lsoria.qrsec.domain.dto.mapper.GuestMapper;
import com.lsoria.qrsec.domain.model.Guest;
import com.lsoria.qrsec.domain.model.Role;
import com.lsoria.qrsec.domain.model.User;
import com.lsoria.qrsec.service.GuestService;
import com.lsoria.qrsec.service.UserService;
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
@Tag(name = "Guest controller", description = "CRUD of guests")
@RequestMapping(path = "${api.path}", produces = MediaType.APPLICATION_JSON_VALUE)
public class GuestController {

    @Autowired
    GuestService guestService;

    @Autowired
    UserService userService;

    @Autowired
    GuestMapper guestMapper;

    @Operation(summary = "Get all Guests (privileged or self)", description = """
            Get Guests based on the current User:
            - ADMIN: All Guests on the database
            - OWNER: All owner's guests""")
    @GetMapping("${api.path.guests}")
    @Parameter(
            name = "X-Email",
            description = "Email of the User that wants to see the Guests",
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
                    description = "Guests successfully retrieved",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            array = @ArraySchema(
                                    schema = @Schema(implementation = GuestDTO.class)
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "204",
                    description = "The neighbourhood has no Guests or the Owner has no Guests",
                    content = @Content()
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "User is not Authorized",
                    content = @Content()
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Some error prevented the Guests from being retrieved",
                    content = @Content()
            )
    })
    public ResponseEntity<List<GuestDTO>> getCurrentUserGuests(
            @RequestHeader(value = "X-Email") @NotNull String email
    ) {

        try {

            // TODO: Replace with @PreAuthorize("hasAuthority('OWNER')") or @PreAuthorize("hasAuthority('ADMIN')")
            if (!userService.userIsAuthorized(email, new Role(Role.OWNER)) && !userService.userIsAuthorized(email, new Role(Role.ADMIN))) {

                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();

            }

            List<Guest> guests = guestService.findGuestsByCurrentUser(email);
            if (guests.isEmpty()) {

                return ResponseEntity.noContent().build();

            }

            return ResponseEntity.ok(guests.stream().map(guestMapper::guestToGuestDTO).collect(Collectors.toList()));

        } catch (NotFoundException exception) {

            log.error("Message: {}.", exception.getMessage());

            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();

        } catch (Exception exception) {

            log.error("Couldn't find all the Guests.\nMessage: {}.\nStackTrace:\n{}", exception.getMessage(), exception.getStackTrace());

        }

        return ResponseEntity.internalServerError().build();

    }

    @Operation(summary = "Get a Guest", description = "Get an specific Guest")
    @GetMapping("${api.path.guests}/{id}")
    @Parameter(
            name = "X-Email",
            description = "Email of the User that wants to see a Guest",
            in = ParameterIn.HEADER,
            required = true,
            schema = @Schema(
                    type = "string",
                    example = "exa@mple.com"
            )
    )
    @Parameter(
            name = "id",
            description = "Guest uuid",
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
                    description = "Guest successfully retrieved",
                    content = @Content(
                        mediaType = MediaType.APPLICATION_JSON_VALUE,
                        schema = @Schema(implementation = GuestDTO.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "User is not Authorized",
                    content = @Content()
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Guest not found",
                    content = @Content()
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Some error prevented the Guest from being retrieved",
                    content = @Content()
            )
    })
    public ResponseEntity<GuestDTO> getGuest(
            @RequestHeader(value = "X-Email") @NotNull String email,
            @PathVariable @NotNull String id
    ) {

        try {

            // TODO: Replace with @PreAuthorize("hasAuthority('OWNER') or hasAuthority('ADMIN') or hasAuthority('GUARD')")
            Optional<User> currentUser = userService.findByUsername(email);
            if (currentUser.isEmpty()) {

                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();

            }
            Optional<Guest> guest = guestService.findOne(id);
            if (guest.isEmpty()) {

                return ResponseEntity.notFound().build();

            }
            if (
                (
                    currentUser.get().getAuthorities() == null ||
                    currentUser.get().getAuthorities().isEmpty()
                ) ||
                (
                    !currentUser.get().getAuthorities().contains(new Role(Role.ADMIN)) &&
                    (
                        userService.userIsAuthorized(email, new Role(Role.OWNER)) &&
                        !guest.get().getOwners().contains(currentUser.get())
                    )
                )
            ) {

                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();

            }

            GuestDTO guestDTO = guestMapper.guestToGuestDTO(guest.get());

            return ResponseEntity.ok(guestDTO);

        } catch (NotFoundException exception) {

            log.error("Message: {}.", exception.getMessage());

            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();

        } catch (Exception exception) {

            log.error("Couldn't find the Guest {}.\nMessage: {}.\nStackTrace:\n{}", id, exception.getMessage(), exception.getStackTrace());

        }

        return ResponseEntity.internalServerError().build();

    }

    @Operation(summary = "Create a Guest", description = "Save a guest for later use on an invite")
    @PostMapping("${api.path.guests}")
    @Parameter(
            name = "X-Email",
            description = "Email of the Owner that wants to create the Guest",
            in = ParameterIn.HEADER,
            required = true,
            schema = @Schema(
                    type = "string",
                    example = "exa@mple.com"
            )
    )
    @io.swagger.v3.oas.annotations.parameters.RequestBody(
            description = "New Guest",
            required = true,
            content = @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = GuestDTO.class)
            )
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "201",
                    description = "Guest successfully created",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = GuestDTO.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "User is not Authorized",
                    content = @Content()
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Some error prevented the Guest from being created",
                    content = @Content()
            )
    })
    public ResponseEntity<GuestDTO> createGuest(
            @RequestHeader(value = "X-Email") @NotNull String email,
            @RequestBody @NotNull GuestDTO guestDTO
    ) {

        try {

            // TODO: Replace with @PreAuthorize("hasAuthority('OWNER')") OR @PreAuthorize("hasAuthority('ADMIN')")
            if (!userService.userIsAuthorized(email, new Role(Role.OWNER)) && !userService.userIsAuthorized(email, new Role(Role.ADMIN))) {

                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();

            }

            Guest newGuest = guestMapper.guestDTOToGuest(guestDTO);
            newGuest.setId(null);

            Guest createdGuest = guestService.save(newGuest, email);

            return ResponseEntity.status(HttpStatus.CREATED).body(guestMapper.guestToGuestDTO(createdGuest));

        } catch (NotFoundException exception) {

            log.error("Message: {}.", exception.getMessage());

            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();

        } catch (Exception exception) {

            log.error("Couldn't create Guest with values:\n{}\nMessage: {}.\nStackTrace:\n{}", guestDTO, exception.getMessage(), exception.getStackTrace());

        }

        return ResponseEntity.internalServerError().build();

    }

    @Operation(summary = "Update a Guest (privileged)", description = "Update guest's information")
    @PutMapping("${api.path.guests}/{id}")
    @Parameter(
            name = "X-Email",
            description = "Email of the Admin that wants to update the Guest",
            in = ParameterIn.HEADER,
            required = true,
            schema = @Schema(
                    type = "string",
                    example = "exa@mple.com"
            )
    )
    @Parameter(
            name = "id",
            description = "Guest uuid",
            in = ParameterIn.PATH,
            required = true,
            schema = @Schema(
                    type = "string",
                    format = "uuid",
                    example = "5f15a5256d2a2a1ac0e4d999"
            )
    )
    @io.swagger.v3.oas.annotations.parameters.RequestBody(
            description = "Updated Guest",
            required = true,
            content = @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = GuestDTO.class)
            )
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Guest successfully updated",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = GuestDTO.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "User is not Authorized",
                    content = @Content()
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Guest not found",
                    content = @Content()
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Some error prevented the Guest from being updated",
                    content = @Content()
            )
    })
    public ResponseEntity<GuestDTO> updateGuest(
            @RequestHeader(value = "X-Email") @NotNull String email,
            @PathVariable @NotNull String id,
            @RequestBody @NotNull GuestDTO guestDTO
    ) {

        try {

            // TODO: Replace with @PreAuthorize("hasAuthority('ADMIN')")
            if (!userService.userIsAuthorized(email, new Role(Role.ADMIN))) {

                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();

            }

            Optional<Guest> foundGuest = guestService.findOne(id);
            if (foundGuest.isEmpty()) {

                return ResponseEntity.notFound().build();

            }

            Guest guestToUpdate = foundGuest.get();
            Guest guestNewValues = guestMapper.guestDTOToGuest(guestDTO);

            Guest updatedGuest = guestService.update(guestToUpdate, guestNewValues);

            return ResponseEntity.ok(guestMapper.guestToGuestDTO(updatedGuest));

        } catch (NotFoundException exception) {

            log.error("Message: {}.", exception.getMessage());

            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();

        } catch (Exception exception) {

            log.error("Couldn't update Guest with values:\n{}\nMessage: {}.\nStackTrace:\n{}", guestDTO, exception.getMessage(), exception.getStackTrace());

        }

        return ResponseEntity.internalServerError().build();

    }

    @Operation(summary = "Delete a Guest (self)", description = "Delete an Owner from the Guest list or the Guest if it was the last Owner")
    @DeleteMapping("${api.path.guests}/{id}")
    @Parameter(
            name = "X-Email",
            description = "Email of the Owner that wants to delete the Guest",
            in = ParameterIn.HEADER,
            required = true,
            schema = @Schema(
                    type = "string",
                    example = "exa@mple.com"
            )
    )
    @Parameter(
            name = "id",
            description = "Guest uuid",
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
                    description = "Guest successfully deleted or Owner successfully removed",
                    content = @Content()
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "User is not Authorized",
                    content = @Content()
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Guest not found",
                    content = @Content()
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Some error prevented the Guest from being deleted or the Owner from being removed",
                    content = @Content()
            )
    })
    public ResponseEntity<GuestDTO> deleteGuest(
            @RequestHeader(value = "X-Email") @NotNull String email,
            @PathVariable @NotNull String id
    ) {

        try {

            Optional<User> currentUser = userService.findByUsername(email);
            if (currentUser.isEmpty()) {

                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();

            }
            Optional<Guest> guest = guestService.findOne(id);
            if (guest.isEmpty()) {

                return ResponseEntity.notFound().build();

            }
            // TODO: Replace with @PreAuthorize("hasAuthority('OWNER')")
            if (
                userService.userIsAuthorized(email, new Role(Role.OWNER)) &&
                !guest.get().getOwners().contains(currentUser.get())
            ) {

                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();

            }

            guestService.delete(id, email);

            return ResponseEntity.noContent().build();

        } catch (NotFoundException exception) {

            log.error("Message: {}.", exception.getMessage());

            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();

        } catch (Exception exception) {

            log.error("Couldn't delete the Guest {}.\nMessage: {}.\nStackTrace:\n{}", id, exception.getMessage(), exception.getStackTrace());

        }

        return ResponseEntity.internalServerError().build();

    }

}
