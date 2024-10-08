package com.lsoria.qrsec.controller.rest;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import com.lsoria.qrsec.domain.dto.InviteDTO;
import com.lsoria.qrsec.domain.dto.PublicInviteDTO;
import com.lsoria.qrsec.domain.dto.mapper.InviteMapper;
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

    @Operation(summary = "Get all Invites (privileged)", description = "Get all Invites from the neighbourhood")
    @GetMapping("${api.path.admin.invites}")
    @Parameter(
            name = "X-Email",
            description = "Email of the Admin that wants to see the Invites",
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
                    description = "The neighbourhood has no Invites",
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
                    description = "Some error prevented the Invites from being retrieved",
                    content = @Content()
            )
    })
    public ResponseEntity<List<InviteDTO>> getInvites(
            @RequestHeader(value = "X-Email") @NotNull String email
    ) {

        try {

            // TODO: Add @PreAuthorize("hasAuthority('ADMIN')")
            if (!userService.userIsAuthorized(email, new Role(Role.ADMIN))) {

                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();

            }

            List<Invite> invites = inviteService.findAll();
            if (invites.isEmpty()) {

                return ResponseEntity.noContent().build();

            }

            return ResponseEntity.ok(invites.stream().map(inviteMapper::inviteToInviteDTO).collect(Collectors.toList()));

        }  catch (NotFoundException exception) {

            log.error("Message: {}.", exception.getMessage());

            return ResponseEntity.notFound().build();

        } catch (Exception exception) {

            log.error("Couldn't find all the Invites.\nMessage: {}.\nStackTrace:\n{}", exception.getMessage(), exception.getStackTrace());

        }

        return ResponseEntity.internalServerError().build();

    }

    @Operation(summary = "Get all Invites", description = "Get all Invites registered for the current Owner")
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
                    description = "User is not Authorized (not an Owner)",
                    content = @Content()
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "User not found on the database",
                    content = @Content()
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Some error prevented the Invites from being retrieved",
                    content = @Content()
            )
    })
    public ResponseEntity<List<InviteDTO>> getCurrentOwnerInvites(
            @RequestHeader(value = "X-Email") @NotNull String email
    ) {

        try {

            // TODO: Replace with @PreAuthorize("hasAuthority('OWNER')")
            if (!userService.userIsAuthorized(email, new Role(Role.OWNER))) {

                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();

            }

            List<Invite> invites = inviteService.findAllMyInvites(email);
            if (invites.isEmpty()) {

                return ResponseEntity.noContent().build();

            }

            return ResponseEntity.ok(invites.stream().map(inviteMapper::inviteToInviteDTO).collect(Collectors.toList()));

        } catch (NotFoundException exception) {

            log.error("Message: {}.", exception.getMessage());

            return ResponseEntity.notFound().build();

        } catch (Exception exception) {

            log.error("Couldn't find all the Invites.\nMessage: {}.\nStackTrace:\n{}", exception.getMessage(), exception.getStackTrace());

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

                return ResponseEntity.notFound().build();

            }
            Optional<Invite> invite = inviteService.findOne(id);
            if (invite.isEmpty()) {

                return ResponseEntity.notFound().build();

            }
            if ((currentUser.get().getAuthorities() == null || currentUser.get().getAuthorities().isEmpty()) || (userService.userIsAuthorized(email, new Role(Role.OWNER)) && !Objects.equals(invite.get().getOwner(), currentUser.get()))) {

                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();

            }

            InviteDTO inviteDTO = inviteMapper.inviteToInviteDTO(invite.get());

            return ResponseEntity.ok(inviteDTO);

        } catch (NotFoundException exception) {

            log.error("Message: {}.", exception.getMessage());

            return ResponseEntity.notFound().build();

        } catch (Exception exception) {

            log.error("Couldn't find the Invite {}.\nMessage: {}.\nStackTrace:\n{}", id, exception.getMessage(), exception.getStackTrace());

        }

        return ResponseEntity.internalServerError().build();

    }

    @Operation(summary = "Get a simplified Invite", description = "Get a specific Invite for the public to see in the web app")
    @GetMapping("${api.path.public.invites}/{id}")
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
                            schema = @Schema(implementation = PublicInviteDTO.class)
                    )
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
    public ResponseEntity<PublicInviteDTO> getPublicInvite(
            @PathVariable @NotNull String id
    ) {

        try {

           Optional<Invite> invite = inviteService.findOne(id);
            if (invite.isEmpty()) {

                return ResponseEntity.notFound().build();

            }

            PublicInviteDTO publicInviteDTO = inviteMapper.inviteToPublicInviteDTO(invite.get());

            return ResponseEntity.ok(publicInviteDTO);

        } catch (Exception exception) {

            log.error("Couldn't find the Invite {}.\nMessage: {}.\nStackTrace:\n{}", id, exception.getMessage(), exception.getStackTrace());

        }

        return ResponseEntity.internalServerError().build();

    }

    @Operation(summary = "Create an Invite", description = "Save an invite for later use")
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
                    responseCode = "209",
                    description = "Invite already existed",
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

            Invite createdInvite = inviteService.save(newInvite, email);

            return ResponseEntity.status(HttpStatus.CREATED).body(inviteMapper.inviteToInviteDTO(createdInvite));

        } catch (NotFoundException exception) {

            log.error("Message: {}.", exception.getMessage());

            return ResponseEntity.notFound().build();

        } catch (Exception exception) {

            log.error("Couldn't create Invite with values:\n{}\nMessage: {}.\nStackTrace:\n{}", inviteDTO, exception.getMessage(), exception.getStackTrace());

        }

        return ResponseEntity.internalServerError().build();

    }

    @Operation(summary = "Update an Invite", description = "Update invite's information")
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
                    description = "Invite or User not found",
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
            if (!userService.userIsAuthorized(email, new Role(Role.OWNER))) {

                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();

            }

            Optional<Invite> foundInvite = inviteService.findOne(id);
            if (foundInvite.isEmpty()) {

                return ResponseEntity.notFound().build();

            }

            Invite inviteToUpdate = foundInvite.get();
            Invite inviteNewValues = inviteMapper.inviteDTOToInvite(inviteDTO);

            Invite updatedInvite = inviteService.update(inviteToUpdate, inviteNewValues);

            return ResponseEntity.ok(inviteMapper.inviteToInviteDTO(updatedInvite));

        } catch (NotFoundException exception) {

            log.error("Message: {}.", exception.getMessage());

            return ResponseEntity.notFound().build();

        } catch (Exception exception) {

            log.error("Couldn't update Invite with values:\n{}\nMessage: {}.\nStackTrace:\n{}", inviteDTO, exception.getMessage(), exception.getStackTrace());

        }

        return ResponseEntity.internalServerError().build();

    }

    @Operation(summary = "Delete an Invite", description = "Delete or disable an Invite")
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
                    description = "Invite or Owner not found",
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

                return ResponseEntity.notFound().build();

            }
            Optional<Invite> invite = inviteService.findOne(id);
            if (invite.isEmpty()) {

                return ResponseEntity.notFound().build();

            }
            // TODO: Replace with @PreAuthorize("hasAuthority('OWNER')")
            if (userService.userIsAuthorized(email, new Role(Role.OWNER)) && !Objects.equals(invite.get().getOwner(), currentUser.get())) {

                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();

            }

            if (inviteService.delete(id)) {

                return ResponseEntity.noContent().build();

            }

            return ResponseEntity.ok().build();

        } catch (NotFoundException exception) {

            log.error("Message: {}.", exception.getMessage());

            return ResponseEntity.notFound().build();

        } catch (Exception exception) {

            log.error("Couldn't delete the Invite {}.\nMessage: {}.\nStackTrace:\n{}", id, exception.getMessage(), exception.getStackTrace());

        }

        return ResponseEntity.internalServerError().build();

    }

}
