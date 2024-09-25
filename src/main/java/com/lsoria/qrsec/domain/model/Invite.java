package com.lsoria.qrsec.domain.model;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.DocumentReference;
import org.springframework.data.mongodb.core.mapping.Field;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = "invites")
public class Invite {

    @Id
    private String id;
    private String description;
    @DocumentReference
    @CreatedBy
    private User owner;
    @DocumentReference
    private Set<Guest> guests  = new HashSet<>();
    private Set<String> days  = new HashSet<>();
    private List<List<String>> hours;
    @Field("max_time_allowed")
    private Integer maxTimeAllowed;
    @Field("number_of_passengers")
    private Integer numberOfPassengers;
    @Field("drops_true_guest")
    private Boolean dropsTrueGuest;
    @Field("arrival_time")
    private LocalDateTime arrivalTime;
    @Field("departure_time")
    private LocalDateTime departureTime;
    @CreatedDate
    @Field("created_at")
    private LocalDateTime createdAt;
    @LastModifiedDate
    @Field("last_modified_at")
    private LocalDateTime lastModifiedAt;
    private Boolean enabled = false;

}
