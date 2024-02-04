package com.lsoria.qrsec.controller.rest;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import com.lsoria.qrsec.domain.dto.AddressDTO;
import com.lsoria.qrsec.domain.dto.mapper.AddressMapper;
import com.lsoria.qrsec.domain.model.Address;
import com.lsoria.qrsec.service.AddressService;
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
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@Tag(name = "Address controller", description = "CRUD of addresses")
@RequestMapping(path = "${api.path}", produces = MediaType.APPLICATION_JSON_VALUE)
public class AddressController {

    @Autowired
    AddressService addressService;

    @Autowired
    AddressMapper addressMapper;

    @Operation(summary = "Get all Addresses (privileged)", description = "Get all Addresses from the neighbourhood")
    @GetMapping("${api.path.addresses}")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Addresses successfully retrieved",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            array = @ArraySchema(
                                    schema = @Schema(implementation = AddressDTO.class)
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "204",
                    description = "The neighbourhood has no Addresses",
                    content = @Content()
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Some error prevented the Addresses from being retrieved",
                    content = @Content()
            )
    })
    // TODO: Add @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<List<AddressDTO>> getAddresses() {

        List<Address> addresses = addressService.findAll();

        return ResponseEntity.ok(addresses.stream().map(addressMapper::addressToAddressDTO).collect(Collectors.toList()));

    }

    @Operation(summary = "Get an Address (privileged)", description = "Get an specific Address")
    @GetMapping("${api.path.addresses}/{id}")
    @Parameter(
            name = "id",
            description = "Address uuid",
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
                    description = "Address successfully retrieved",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = AddressDTO.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Address not found",
                    content = @Content()
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Some error prevented the Address from being retrieved",
                    content = @Content()
            )
    })
    // TODO: Add @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<AddressDTO> getAddress(
            @PathVariable @NotNull String id
    ) {

        Optional<Address> address = addressService.findOne(id);

        if (address.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        AddressDTO addressDTO = addressMapper.addressToAddressDTO(address.get());

        return ResponseEntity.ok(addressDTO);

    }

    @Operation(summary = "Create an Address", description = "Save an Address for later use on a User")
    @PostMapping("${api.path.addresses}")
    @io.swagger.v3.oas.annotations.parameters.RequestBody(
            description = "New Address",
            required = true,
            content = @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = AddressDTO.class)
            )
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "201",
                    description = "Address successfully created",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = AddressDTO.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "209",
                    description = "Address already existed",
                    content = @Content()
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Some error prevented the Address from being created",
                    content = @Content()
            )
    })
    public ResponseEntity<AddressDTO> createAddress(
            @RequestBody @NotNull AddressDTO addressDTO
    ) {

        Address addressToCreate = addressMapper.addressDTOToAddress(addressDTO);
        addressToCreate.setId("");

        try {

            Address createdAddress = addressService.save(addressToCreate);

            return ResponseEntity.status(HttpStatus.CREATED).body(addressMapper.addressToAddressDTO(createdAddress));

        } catch (ConflictException conflictException) {

            log.error("Couldn't create ADDRESS.\nMessage: {}.", conflictException.getMessage());

            return ResponseEntity.status(HttpStatus.CONFLICT).build();

        } catch (Exception exception) {

            log.error("Couldn't create ADDRESS with values:\n{}\nMessage: {}.\nStackTrace:\n{}", addressDTO, exception.getMessage(), exception.getStackTrace());

        }

        return ResponseEntity.internalServerError().build();

    }

    @Operation(summary = "Update an Address", description = "Update address's information")
    @PutMapping("${api.path.addresses}/{id}")
    @Parameter(
            name = "id",
            description = "Address uuid",
            in = ParameterIn.PATH,
            required = true,
            schema = @Schema(
                    type = "string",
                    format = "uuid",
                    example = "5f15a5256d2a2a1ac0e4d999"
            )
    )
    @io.swagger.v3.oas.annotations.parameters.RequestBody(
            description = "Updated Address",
            required = true,
            content = @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = AddressDTO.class)
            )
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Address successfully updated",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = AddressDTO.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Address not found",
                    content = @Content()
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Some error prevented the Address from being updated",
                    content = @Content()
            )
    })
    public ResponseEntity<AddressDTO> updateAddress(
            @PathVariable @NotNull String id,
            @RequestBody @NotNull AddressDTO addressDTO
    ) {

        Optional<Address> foundAddress = addressService.findOne(id);

        if (foundAddress.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        Address addressToUpdate = foundAddress.get();
        Address addressNewValues = addressMapper.addressDTOToAddress(addressDTO);

        try {

            Address updatedAddress = addressService.update(addressToUpdate, addressNewValues);

            return ResponseEntity.ok(addressMapper.addressToAddressDTO(updatedAddress));

        } catch (Exception exception) {

            log.error("Couldn't update ADDRESS with values:\n{}\nMessage: {}.\nStackTrace:\n{}", addressDTO, exception.getMessage(), exception.getStackTrace());

        }

        return ResponseEntity.internalServerError().build();

    }

    @Operation(summary = "Delete an Address", description = "The Address will no longer be available in the database")
    @DeleteMapping("${api.path.addresses}/{id}")
    @Parameter(
            name = "id",
            description = "Address uuid",
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
                    description = "Address successfully deleted",
                    content = @Content()
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Address not found",
                    content = @Content()
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Some error prevented the Address from being deleted",
                    content = @Content()
            )
    })
    public ResponseEntity<AddressDTO> deleteAddress(
            @PathVariable @NotNull String id
    ) {

        try {

            addressService.delete(id);

            return ResponseEntity.noContent().build();

        } catch (NotFoundException exception) {

            return ResponseEntity.notFound().build();

        } catch (Exception exception) {

            log.error("Couldn't delete ADDRESS {}.\nMessage: {}.\nStackTrace:\n{}", id, exception.getMessage(), exception.getStackTrace());

        }

        return ResponseEntity.internalServerError().build();

    }

}
