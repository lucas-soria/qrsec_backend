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
import com.lsoria.qrsec.service.exception.UnauthorizedException;

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

    @Operation(summary = "Get all Guests (privileged)", description = "Get all Guests from the neighbourhood")
    @GetMapping("/all/${api.path.guests}")
    @Parameter(
            name = "X-Email",
            description = "Email of the Owner that wants to add the Guest",
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
                    description = "The neighbourhood has no Guests",
                    content = @Content()
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "User not found on the database",
                    content = @Content()
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Some error prevented the Guests from being retrieved",
                    content = @Content()
            )
    })
    // TODO: Add @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<List<GuestDTO>> getGuests(
            @RequestHeader(value = "X-Email") @NotNull String email
    ) {

        try {

            List<Guest> guests = guestService.findAll(email, new Role(Role.ADMIN));
            if (guests.isEmpty()) {

                return ResponseEntity.noContent().build();

            }

            return ResponseEntity.ok(guests.stream().map(guestMapper::guestToGuestDTO).collect(Collectors.toList()));

        } catch (NotFoundException exception) {

            log.error("User not found");

            return ResponseEntity.notFound().build();

        } catch (UnauthorizedException exception) {

            log.error("User is not authorized to Use this endpoint");

            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();

        } catch (Exception exception) {

            log.error("Couldn't find all the Guests.\nMessage: {}.\nStackTrace:\n{}", exception.getMessage(), exception.getStackTrace());

        }

        return ResponseEntity.internalServerError().build();

    }

    @Operation(summary = "Get all Guests", description = "Get all Guests registered for the current Owner")
    @GetMapping("${api.path.guests}")
    @Parameter(
            name = "X-Email",
            description = "Email of the Owner that wants to add the Guest",
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
                    description = "The Owner has no Guests",
                    content = @Content()
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "User not found on the database",
                    content = @Content()
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Some error prevented the Guests from being retrieved",
                    content = @Content()
            )
    })
    // TODO: Add @PreAuthorize("hasAuthority('OWNER')")
    public ResponseEntity<List<GuestDTO>> getCurrentOwnerGuests(
            @RequestHeader(value = "X-Email") @NotNull String email
    ) {

        try {

            List<Guest> guests = guestService.findAllMyGuests(email, new Role(Role.OWNER));
            if (guests.isEmpty()) {

                return ResponseEntity.noContent().build();

            }

            return ResponseEntity.ok(guests.stream().map(guestMapper::guestToGuestDTO).collect(Collectors.toList()));

        } catch (NotFoundException exception) {

            log.error("User not found");

            return ResponseEntity.notFound().build();

        } catch (UnauthorizedException exception) {

            log.error("User is not authorized to Use this endpoint");

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
            description = "Email of the Owner that wants to add the Guest",
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

        Optional<User> currentUser = userService.findByUsername(email);
        if (currentUser.isEmpty()) {

            return ResponseEntity.notFound().build();

        }
        Optional<Guest> guest = guestService.findOne(id);
        if (guest.isEmpty()) {

            return ResponseEntity.notFound().build();

        }
        if (currentUser.get().getAuthorities().contains(new Role(Role.OWNER)) && !guest.get().getOwner().contains(currentUser.get())) {

            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();

        }

        GuestDTO guestDTO = guestMapper.guestToGuestDTO(guest.get());

        return ResponseEntity.ok(guestDTO);

    }

    @Operation(summary = "Create a Guest", description = "Save a guest for later use on an invite")
    @PostMapping("${api.path.guests}")
    @Parameter(
            name = "X-Email",
            description = "Email of the Owner that wants to add the Guest",
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
                    responseCode = "500",
                    description = "Some error prevented the Guest from being created",
                    content = @Content()
            )
    })
    // TODO: Add @PreAuthorize("hasAuthority('OWNER')")
    public ResponseEntity<GuestDTO> createGuest(
            @RequestHeader(value = "X-Email") @NotNull String email,
            @RequestBody @NotNull GuestDTO guestDTO
    ) {

        try {

            if (!userService.userIsAuthorized(email, new Role(Role.OWNER))) {

                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();

            }

            Guest newGuest = guestMapper.guestDTOToGuest(guestDTO);
            newGuest.setId(null);

            Guest createdGuest = guestService.save(newGuest, email);

            return ResponseEntity.status(HttpStatus.CREATED).body(guestMapper.guestToGuestDTO(createdGuest));

        } catch (NotFoundException exception) {

            log.error("User {} not found", email);

            return ResponseEntity.notFound().build();

        } catch (Exception exception) {

            log.error("Couldn't create Guest with values:\n{}\nMessage: {}.\nStackTrace:\n{}", guestDTO, exception.getMessage(), exception.getStackTrace());

        }

        return ResponseEntity.internalServerError().build();

    }

    @Operation(summary = "Update a Guest (privileged)", description = "Update guest's information")
    @PutMapping("${api.path.guests}/{id}")
    @Parameter(
            name = "X-Email",
            description = "Email of the Owner that wants to add the Guest",
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
    // TODO: Add @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<GuestDTO> updateGuest(
            @RequestHeader(value = "X-Email") @NotNull String email,
            @PathVariable @NotNull String id,
            @RequestBody @NotNull GuestDTO guestDTO
    ) {

        log.info("REST request to update Guest {}: {}", id, guestDTO);
        return ResponseEntity.ok(guestDTO); // TODO: Use correct method

    }

    @Operation(summary = "Delete a Guest", description = "Delete an Owner from the Guest list or the Guest if it was the last Owner")
    @DeleteMapping("${api.path.guests}/{id}")
    @Parameter(
            name = "X-Email",
            description = "Email of the Owner that wants to add the Guest",
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
    // TODO: Add @PreAuthorize("hasAuthority('OWNER || ADMIN')")
    public void deleteGuest(
            @RequestHeader(value = "X-Email") @NotNull String email,
            @PathVariable @NotNull String id
    ) {

        log.info("REST request to delete Guest {}", id); // TODO: Delete Guest

    }

}
