package com.lsoria.qrsec.controller.rest;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import com.lsoria.qrsec.domain.dto.InviteDTO;
import com.lsoria.qrsec.domain.dto.PublicInviteDTO;
import com.lsoria.qrsec.domain.dto.SimplifiedGuestDTO;
import com.lsoria.qrsec.domain.dto.mapper.InviteMapper;
import com.lsoria.qrsec.domain.model.Guest;
import com.lsoria.qrsec.domain.model.Invite;
import com.lsoria.qrsec.domain.model.Role;
import com.lsoria.qrsec.domain.model.User;
import com.lsoria.qrsec.service.InviteService;
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
@Tag(name = "Invite controller", description = "CRUD of invites")
@RequestMapping(path = "${api.path}", produces = MediaType.APPLICATION_JSON_VALUE)
public class InviteController {

    @Autowired
    InviteService inviteService;

    @Autowired
    UserService userService;

    @Autowired
    InviteMapper inviteMapper;

    List<String> availableActions = Arrays.asList("enable", "disable", "arrival", "departure");

    @Operation(summary = "Get all Invites", description = "Get Invites based on the current User:\n" +
            "- ADMIN: All invites on the database\n" +
            "- GUARD: All invites valid that day\n" +
            "- OWNER: All owner's invites")
    @GetMapping("${api.path.invites}")
    @Parameter(
            name = "X-Email",
            description = "Email of the Owner that wants to see their Invites",
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
                    description = "Invites successfully retrieved",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            array = @ArraySchema(
                                    schema = @Schema(implementation = InviteDTO.class)
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "204",
                    description = "The Owner has no Invites",
                    content = @Content()
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "User is not Authorized",
                    content = @Content()
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Some error prevented the Invites from being retrieved",
                    content = @Content()
            )
    })
    public ResponseEntity<List<InviteDTO>> getCurrentUserInvites(
            @RequestHeader(value = "X-Email") @NotNull String email
    ) {

        try {

            // TODO: Replace with @PreAuthorize("hasAuthority('OWNER') or hasAuthority('ADMIN') or hasAuthority('GUARD')")
            if (userService.findByUsername(email).isEmpty()) {

                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();

            }

            List<Invite> invites = inviteService.findInvitesByCurrentUser(email);
            if (invites.isEmpty()) {

                return ResponseEntity.noContent().build();

            }

            return ResponseEntity.ok(invites.stream().map(inviteMapper::inviteToInviteDTO).collect(Collectors.toList()));

        } catch (NotFoundException exception) {

            log.error("Message: {}.", exception.getMessage());

            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();

        } catch (Exception exception) {

            log.error("Couldn't find the Invites.\nMessage: {}.\nStackTrace:\n{}", exception.getMessage(), exception.getStackTrace());

        }

        return ResponseEntity.internalServerError().build();

    }

    @Operation(summary = "Get an Invite", description = "Get an specific Invite")
    @GetMapping("${api.path.invites}/{id}")
    @Parameter(
            name = "X-Email",
            description = "Email of the User that wants to see an Invite",
            in = ParameterIn.HEADER,
            required = true,
            schema = @Schema(
                    type = "string",
                    example = "exa@mple.com"
            )
    )
    @Parameter(
            name = "id",
            description = "Invite uuid",
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
                    description = "Invite successfully updated",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = InviteDTO.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "User is not Authorized",
                    content = @Content()
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Invite not found",
                    content = @Content()
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Some error prevented the Invite from being updated",
                    content = @Content()
            )
    })
    public ResponseEntity<InviteDTO> getInvite(
            @RequestHeader(value = "X-Email") @NotNull String email,
            @PathVariable @NotNull String id
    ) {

        try {

            // TODO: Replace with @PreAuthorize("hasAuthority('OWNER') or hasAuthority('ADMIN') or hasAuthority('GUARD')")
            Optional<User> currentUser = userService.findByUsername(email);
            if (currentUser.isEmpty()) {

                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();

            }
            Optional<Invite> invite = inviteService.findOne(id);
            if (invite.isEmpty()) {

                return ResponseEntity.notFound().build();

            }
            if (
                (
                    currentUser.get().getAuthorities() == null ||
                    currentUser.get().getAuthorities().isEmpty()
                ) ||
                (
                    userService.userIsAuthorized(email, new Role(Role.OWNER)) &&
                    !Objects.equals(invite.get().getOwner(), currentUser.get())
                )
            ) {

                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();

            }

            InviteDTO inviteDTO = inviteMapper.inviteToInviteDTO(invite.get());

            return ResponseEntity.ok(inviteDTO);

        } catch (NotFoundException exception) {

            log.error("Message: {}.", exception.getMessage());

            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();

        } catch (Exception exception) {

            log.error("Couldn't find the Invite {}.\nMessage: {}.\nStackTrace:\n{}", id, exception.getMessage(), exception.getStackTrace());

        }

        return ResponseEntity.internalServerError().build();

    }

    @Operation(summary = "Validate Guest access to Invite", description = "Get a specific and simplified Invite for the public to see in the web app")
    @PostMapping("${api.path.invites.public}/{id}")
    @Parameter(
            name = "id",
            description = "Invite uuid",
            in = ParameterIn.PATH,
            required = true,
            schema = @Schema(
                    type = "string",
                    format = "uuid",
                    example = "5f15a5256d2a2a1ac0e4d999"
            )
    )
    @io.swagger.v3.oas.annotations.parameters.RequestBody(
            description = "Guest info",
            required = true,
            content = @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = SimplifiedGuestDTO.class)
            )
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Invite successfully updated",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = PublicInviteDTO.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid data provided",
                    content = @Content()
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Some error prevented the Invite from being updated",
                    content = @Content()
            )
    })
    public ResponseEntity<PublicInviteDTO> getPublicInvite(
            @PathVariable @NotNull String id,
            @RequestBody SimplifiedGuestDTO simplifiedGuest
    ) {

        try {

           Optional<Invite> invite = inviteService.findOne(id);
            if (invite.isEmpty()) {

                return ResponseEntity.badRequest().build();

            }

            boolean hasGuest = false;
            for (Guest guest : invite.get().getGuests()) {
                if (guest.getDni().equals(simplifiedGuest.getDni())) {

                    hasGuest = true;
                    break;

                }
            }

            if (!hasGuest) {

                return ResponseEntity.badRequest().build();

            }

            PublicInviteDTO publicInviteDTO = inviteMapper.inviteToPublicInviteDTO(invite.get());

            return ResponseEntity.ok(publicInviteDTO);

        } catch (Exception exception) {

            log.error("Couldn't find the Invite {}.\nMessage: {}.\nStackTrace:\n{}", id, exception.getMessage(), exception.getStackTrace());

        }

        return ResponseEntity.internalServerError().build();

    }

    @Operation(summary = "Create an Invite (self)", description = "Save an invite for later use")
    @PostMapping("${api.path.invites}")
    @Parameter(
            name = "X-Email",
            description = "Email of the Owner that wants to create the Invite",
            in = ParameterIn.HEADER,
            required = true,
            schema = @Schema(
                    type = "string",
                    example = "exa@mple.com"
            )
    )
    @io.swagger.v3.oas.annotations.parameters.RequestBody(
            description = "New Invite",
            required = true,
            content = @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = InviteDTO.class)
            )
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "201",
                    description = "Invite successfully created",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = InviteDTO.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "User is not Authorized",
                    content = @Content()
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Some error prevented the Invite from being created",
                    content = @Content()
            )
    })
    public ResponseEntity<InviteDTO> createInvite(
            @RequestHeader(value = "X-Email") @NotNull String email,
            @RequestBody InviteDTO inviteDTO
    ) {

        try {

            // TODO: Replace with @PreAuthorize("hasAuthority('OWNER')")
            if (!userService.userIsAuthorized(email, new Role(Role.OWNER))) {

                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();

            }

            Invite newInvite = inviteMapper.inviteDTOToInvite(inviteDTO);
            newInvite.setId(null);
            newInvite.setArrivalTime(null);
            newInvite.setDepartureTime(null);
            newInvite.setEnabled(true);

            Invite createdInvite = inviteService.save(newInvite, email);

            return ResponseEntity.status(HttpStatus.CREATED).body(inviteMapper.inviteToInviteDTO(createdInvite));

        } catch (NotFoundException exception) {

            log.error("Message: {}.", exception.getMessage());

            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();

        } catch (Exception exception) {

            log.error("Couldn't create Invite with values:\n{}\nMessage: {}.\nStackTrace:\n{}", inviteDTO, exception.getMessage(), exception.getStackTrace());

        }

        return ResponseEntity.internalServerError().build();

    }

    @Operation(summary = "Update an Invite (self)", description = "Update invite's information")
    @PutMapping("${api.path.invites}/{id}")
    @Parameter(
            name = "X-Email",
            description = "Email of the Owner that wants to update the Invite",
            in = ParameterIn.HEADER,
            required = true,
            schema = @Schema(
                    type = "string",
                    example = "exa@mple.com"
            )
    )
    @Parameter(
            name = "id",
            description = "Invite uuid",
            in = ParameterIn.PATH,
            required = true,
            schema = @Schema(
                    type = "string",
                    format = "uuid",
                    example = "5f15a5256d2a2a1ac0e4d999"
            )
    )
    @io.swagger.v3.oas.annotations.parameters.RequestBody(
            description = "Updated Invite",
            required = true,
            content = @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = InviteDTO.class)
            )
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Invite successfully updated",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = InviteDTO.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "User is not Authorized",
                    content = @Content()
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Invite not found",
                    content = @Content()
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Some error prevented the Invite from being updated",
                    content = @Content()
            )
    })
    public ResponseEntity<InviteDTO> updateInvite(
            @RequestHeader(value = "X-Email") @NotNull String email,
            @PathVariable @NotNull String id,
            @RequestBody @NotNull InviteDTO inviteDTO
    ) {

        try {

            // TODO: Replace with @PreAuthorize("hasAuthority('OWNER')")
            Optional<User> currentUser = userService.findByUsername(email);
            if (currentUser.isEmpty()) {

                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();

            }

            Optional<Invite> foundInvite = inviteService.findOne(id);
            if (foundInvite.isEmpty()) {

                return ResponseEntity.notFound().build();

            }

            if (
                (
                    currentUser.get().getAuthorities() == null ||
                    currentUser.get().getAuthorities().isEmpty()
                ) ||
                (
                    userService.userIsAuthorized(email, new Role(Role.OWNER)) &&
                    !Objects.equals(foundInvite.get().getOwner(), currentUser.get())
                )
            ) {

                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();

            }

            Invite inviteToUpdate = foundInvite.get();
            Invite inviteNewValues = inviteMapper.inviteDTOToInvite(inviteDTO);

            Invite updatedInvite = inviteService.update(inviteToUpdate, inviteNewValues);

            return ResponseEntity.ok(inviteMapper.inviteToInviteDTO(updatedInvite));

        } catch (NotFoundException exception) {

            log.error("Message: {}.", exception.getMessage());

            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();

        } catch (Exception exception) {

            log.error("Couldn't update Invite with values:\n{}\nMessage: {}.\nStackTrace:\n{}", inviteDTO, exception.getMessage(), exception.getStackTrace());

        }

        return ResponseEntity.internalServerError().build();

    }

    @Operation(summary = "Delete an Invite (self)", description = "Delete or disable an Invite")
    @DeleteMapping("${api.path.invites}/{id}")
    @Parameter(
            name = "X-Email",
            description = "Email of the Owner that wants to delete the Invite",
            in = ParameterIn.HEADER,
            required = true,
            schema = @Schema(
                    type = "string",
                    example = "exa@mple.com"
            )
    )
    @Parameter(
            name = "id",
            description = "Invite uuid",
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
                    description = "Invite successfully disabled",
                    content = @Content()
            ),
            @ApiResponse(
                    responseCode = "204",
                    description = "Invite successfully deleted",
                    content = @Content()
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Invite not found",
                    content = @Content()
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Some error prevented the Invite from being deleted or the Owner from being removed",
                    content = @Content()
            )
    })
    public ResponseEntity<InviteDTO> deleteInvite(
            @RequestHeader(value = "X-Email") @NotNull String email,
            @PathVariable @NotNull String id
    ) {

        try {

            Optional<User> currentUser = userService.findByUsername(email);
            if (currentUser.isEmpty()) {

                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();

            }
            Optional<Invite> invite = inviteService.findOne(id);
            if (invite.isEmpty()) {

                return ResponseEntity.notFound().build();

            }
            // TODO: Replace with @PreAuthorize("hasAuthority('OWNER')")
            if (
                !userService.userIsAuthorized(email, new Role(Role.OWNER)) &&
                !Objects.equals(invite.get().getOwner(), currentUser.get())
            ) {

                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();

            }

            if (inviteService.delete(id)) {

                return ResponseEntity.noContent().build();

            }

            return ResponseEntity.ok().build();

        } catch (NotFoundException exception) {

            log.error("Message: {}.", exception.getMessage());

            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();

        } catch (Exception exception) {

            log.error("Couldn't delete the Invite {}.\nMessage: {}.\nStackTrace:\n{}", id, exception.getMessage(), exception.getStackTrace());

        }

        return ResponseEntity.internalServerError().build();

    }

    @Operation(summary = "Validate an Invite (guard)", description = "Check if an Invite is valid in a certain moment in time")
    @GetMapping("${api.path.invites.validate}/{id}")
    @Parameter(
            name = "X-Email",
            description = "Email of the Guard that wants to validate the Invite",
            in = ParameterIn.HEADER,
            required = true,
            schema = @Schema(
                    type = "string",
                    example = "exa@mple.com"
            )
    )
    @Parameter(
            name = "X-Client-Timestamp",
            description = "Timestamp of the Guard that is trying to validate the invite.\nShould be ISO 8601 formatted",
            in = ParameterIn.HEADER,
            required = true,
            schema = @Schema(
                    type = "string",
                    example = "2024-09-23T14:30:00+02:00"
            )
    )
    @Parameter(
            name = "id",
            description = "Invite uuid",
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
                    description = "Invite is valid",
                    content = @Content()
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invite is invalid",
                    content = @Content()
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "User is not Authorized",
                    content = @Content()
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Invite not found",
                    content = @Content()
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Some error prevented the Invite from being validated",
                    content = @Content()
            )
    })
    public ResponseEntity<InviteDTO> validateInvite(
            @RequestHeader(value = "X-Email") @NotNull String email,
            @RequestHeader(value = "X-Client-Timestamp") @NotNull String timestamp,
            @PathVariable @NotNull String id
    ) {

        try {

            Optional<User> currentUser = userService.findByUsername(email);
            if (currentUser.isEmpty()) {

                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();

            }
            Optional<Invite> invite = inviteService.findOne(id);
            if (invite.isEmpty()) {

                return ResponseEntity.notFound().build();

            }
            // TODO: Replace with @PreAuthorize("hasAuthority('GUARD')")
            if (!userService.userIsAuthorized(email, new Role(Role.GUARD))) {

                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();

            }

            if (!inviteService.inviteIsValid(invite.get(), timestamp)) {

                return ResponseEntity.badRequest().build();

            }

            return ResponseEntity.ok().build();

        } catch (NotFoundException exception) {

            log.error("Message: {}.", exception.getMessage());

            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();

        } catch (Exception exception) {

            log.error("Couldn't validate the Invite {}.\nMessage: {}.\nStackTrace:\n{}", id, exception.getMessage(), exception.getStackTrace());

        }

        return ResponseEntity.internalServerError().build();

    }

    @Operation(summary = "Invite action (guard)", description = "Update Invite's arrival or departure time")
    @PostMapping("${api.path.invites}/{id}/action/{action}")
    @Parameter(
            name = "X-Email",
            description = "Email of the Guard that wants to update the Invite's arrival or departure time",
            in = ParameterIn.HEADER,
            required = true,
            schema = @Schema(
                    type = "string",
                    example = "exa@mple.com"
            )
    )
    @Parameter(
            name = "X-Client-Timestamp",
            description = "Timestamp of the Guard that is trying to update the Invite's arrival or departure time.\nShould be ISO 8601 formatted",
            in = ParameterIn.HEADER,
            required = true,
            schema = @Schema(
                    type = "string",
                    example = "2024-09-23T14:30:00+02:00"
            )
    )
    @Parameter(
            name = "id",
            description = "Invite uuid",
            in = ParameterIn.PATH,
            required = true,
            schema = @Schema(
                    type = "string",
                    format = "uuid",
                    example = "5f15a5256d2a2a1ac0e4d999"
            )
    )
    @Parameter(
            name = "action",
            description = "Actions you can do over an invite",
            in = ParameterIn.PATH,
            required = true,
            schema = @Schema(
                    type = "string",
                    allowableValues = {
                            "arrival",
                            "departure",
                            "enable",
                            "disable"
                    },
                    example = "5f15a5256d2a2a1ac0e4d999"
            )
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Invite is valid",
                    content = @Content()
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Path param is invalid",
                    content = @Content()
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "User is not Authorized",
                    content = @Content()
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Invite not found",
                    content = @Content()
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Some error prevented the Invite from being updated",
                    content = @Content()
            )
    })
    public ResponseEntity<InviteDTO> inviteAction(
            @RequestHeader(value = "X-Email") @NotNull String email,
            @RequestHeader(value = "X-Client-Timestamp") @NotNull String timestamp,
            @PathVariable @NotNull String id,
            @PathVariable @NotNull String action
    ) {

        try {

            if (!availableActions.contains(action)) {

                return ResponseEntity.badRequest().build();

            }

            Optional<User> currentUser = userService.findByUsername(email);
            if (currentUser.isEmpty()) {

                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();

            }
            Optional<Invite> invite = inviteService.findOne(id);
            if (invite.isEmpty()) {

                return ResponseEntity.notFound().build();

            }
            // TODO: Replace with @PreAuthorize("hasAuthority('OWNER')")
            if (!userService.userIsAuthorized(email, new Role(Role.OWNER))) {

                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();

            }

            LocalDateTime parsedTimestamp;

            try {

                parsedTimestamp = LocalDateTime.parse(timestamp, DateTimeFormatter.ISO_DATE_TIME);

            } catch (Exception e) {

                return ResponseEntity.badRequest().build();

            }

            Invite updatedInvite = inviteService.doAction(invite.get(), action, parsedTimestamp);

            return ResponseEntity.ok(inviteMapper.inviteToInviteDTO(updatedInvite));

        } catch (NotFoundException exception) {

            log.error("Message: {}.", exception.getMessage());

            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();

        } catch (Exception exception) {

            log.error("Couldn't update the Invite's arrival or departure time {}.\nMessage: {}.\nStackTrace:\n{}", id, exception.getMessage(), exception.getStackTrace());

        }

        return ResponseEntity.internalServerError().build();

    }

}
