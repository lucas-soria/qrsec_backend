package com.lsoria.qrsec.controller.rest;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import com.lsoria.qrsec.domain.dto.InviteDTO;
import com.lsoria.qrsec.domain.dto.mapper.InviteMapper;
import com.lsoria.qrsec.domain.model.Invite;
import com.lsoria.qrsec.service.InviteService;

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
@Tag(name = "Invite controller", description = "CRUD of invites")
@RequestMapping(path = "${api.path}", produces = MediaType.APPLICATION_JSON_VALUE)
public class InviteController {

    @Autowired
    InviteService inviteService;

    @Autowired
    InviteMapper inviteMapper;

    @Operation(summary = "Get all Invites (privileged)", description = "Get all Invites from the neighbourhood")
    @GetMapping("${api.path.admin.invites}")
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
                    responseCode = "500",
                    description = "Some error prevented the Invites from being retrieved",
                    content = @Content()
            )
    })
    // TODO: Add @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<List<InviteDTO>> getInvites() {

        List<Invite> invites = inviteService.findAll();

        return ResponseEntity.ok(invites.stream().map(inviteMapper::inviteToInviteDTO).collect(Collectors.toList()));

    }

    @Operation(summary = "Get all Invites", description = "Get all Invites registered for the current Owner")
    @GetMapping("${api.path.invites}")
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
                    responseCode = "500",
                    description = "Some error prevented the Invites from being retrieved",
                    content = @Content()
            )
    })
    // TODO: Add @PreAuthorize("hasAuthority('OWNER')")
    public ResponseEntity<List<InviteDTO>> getCurrentOwnerGuests() {

        List<Invite> invites = inviteService.findAll();

        return ResponseEntity.ok(invites.stream().map(inviteMapper::inviteToInviteDTO).collect(Collectors.toList())); // TODO: Create corresponding method.findAllMyGuests());

    }

    @Operation(summary = "Get an Invite", description = "Get an specific Invite")
    @GetMapping("${api.path.invites}/{id}")
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
            @PathVariable @NotNull String id
    ) {

        Optional<Invite> invite = inviteService.findOne(id);

        if (invite.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        InviteDTO inviteDTO = inviteMapper.inviteToInviteDTO(invite.get());

        return ResponseEntity.ok(inviteDTO);

    }

    @Operation(summary = "Create an Invite", description = "Save an invite for later use")
    @PostMapping("${api.path.invites}")
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
                    responseCode = "500",
                    description = "Some error prevented the Invite from being created",
                    content = @Content()
            )
    })
    public ResponseEntity<InviteDTO> createInvite(
            @RequestBody InviteDTO inviteDTO
    ) {

        return ResponseEntity.status(HttpStatus.CREATED).body(inviteDTO);
        // return ResponseEntity.status(HttpStatus.CREATED).body(inviteService.save(invite));

    }

    @Operation(summary = "Update an Invite", description = "Update invite's information")
    @PutMapping("${api.path.invites}/{id}")
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
            @PathVariable @NotNull String id,
            @RequestBody @NotNull InviteDTO inviteDTO
    ) {

        log.info("REST request to update Invite {}: {}", id, inviteDTO);
        return ResponseEntity.ok(inviteDTO); // TODO: Use correct method

    }

    @Operation(summary = "Delete an Invite", description = "Delete or disable an Invite")
    @DeleteMapping("${api.path.invites}/{id}")
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
    public void deleteInvite(
            @PathVariable @NotNull String id
    ) {

        log.info("REST request to delete Invite {}", id); // TODO: Delete or disable Invite

    }

}
