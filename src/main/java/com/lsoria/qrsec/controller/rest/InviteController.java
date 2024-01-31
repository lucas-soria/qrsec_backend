package com.lsoria.qrsec.controller.rest;

import java.util.List;

import com.lsoria.qrsec.domain.model.Invite;
import com.lsoria.qrsec.service.InviteService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@Tag(name = "Invite controller", description = "CRUD of invites")
@RequestMapping(path = "${api.path}", produces = MediaType.APPLICATION_JSON_VALUE)
public class InviteController {

    @Autowired
    InviteService inviteService;

    @Operation(summary = "Get all Invites (privileged)", description = "Get all Invites from the neighbourhood")
    @GetMapping("/all/${api.path.invites}")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Invites successfully retrieved"
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
    public ResponseEntity<List<Invite>> getInvites() {

        log.info("REST request to get all Invite");
        return ResponseEntity.ok(inviteService.findAll());

    }

    @Operation(summary = "Get all Invites", description = "Get all Invites registered for the current Owner")
    @GetMapping("${api.path.invites}")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Invites successfully retrieved"
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
    public ResponseEntity<List<Invite>> getCurrentOwnerGuests() {

        log.info("REST request to get all Guest of currently logged user");
        return ResponseEntity.ok(inviteService.findAll()); // TODO: Create corresponding method.findAllMyGuests());

    }

    @Operation(summary = "Get an Invite", description = "Get an specific Invite")
    @GetMapping("${api.path.invites}/{id}")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Invite successfully updated"
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
    public ResponseEntity<Invite> getInvite(
            @Parameter(description = "Invite ID", required = true, example = "5f15a5256d2a2a1ac0e4d999", in = ParameterIn.PATH) String id
    ) {

        log.info("REST request to get Invite : {}", id);
        return ResponseEntity.of(inviteService.findOne(id));

    }

    @Operation(summary = "Create an Invite", description = "Save an invite for later use")
    @PostMapping("${api.path.invites}")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "201",
                    description = "Invite successfully created"
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
    public ResponseEntity<Invite> createInvite(
            @RequestBody(description = "New Invite", required = true) Invite invite
    ) {

        log.info("REST request to save Invite : {}", invite);
        return ResponseEntity.status(HttpStatus.CREATED).body(inviteService.save(invite));

    }

    @Operation(summary = "Update an Invite", description = "Update invite's information")
    @PutMapping("${api.path.invites}/{id}")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Invite successfully updated"
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
    public ResponseEntity<Invite> updateInvite(
            @Parameter(description = "Invite ID", required = true, example = "5f15a5256d2a2a1ac0e4d999", in = ParameterIn.PATH) String id,
            @RequestBody(description = "Updated Invite", required = true) Invite invite
    ) {

        log.info("REST request to update Invite {}: {}", id, invite);
        return ResponseEntity.ok(invite); // TODO: Use correct method

    }

    @Operation(summary = "Delete an Invite", description = "Delete or disable an Invite")
    @DeleteMapping("${api.path.invites}/{id}")
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
            @Parameter(description = "Invite ID", required = true, example = "5f15a5256d2a2a1ac0e4d999", in = ParameterIn.PATH) String id
    ) {

        log.info("REST request to delete Invite {}", id); // TODO: Delete or disable Invite

    }

}
