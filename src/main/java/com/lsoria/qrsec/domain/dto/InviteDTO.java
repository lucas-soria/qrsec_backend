package com.lsoria.qrsec.domain.dto;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.lsoria.qrsec.domain.model.Guest;
import com.lsoria.qrsec.domain.model.User;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class InviteDTO {

    @Schema(accessMode = Schema.AccessMode.READ_ONLY)
    private String id;
    @Schema(accessMode = Schema.AccessMode.READ_ONLY)
    private User owner;
    private Set<Guest> guests  = new HashSet<>();
    private Set<String> days  = new HashSet<>();
    private List<List<String>> hours;
    private Integer maxTimeAllowed;
    private Integer numberOfPassengers;
    private Boolean dropsTrueGuest;
    @Schema(accessMode = Schema.AccessMode.READ_ONLY)
    private Boolean enabled;

}
