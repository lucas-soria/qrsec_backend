package com.lsoria.qrsec.domain.model;

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

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = "invites")
public class Invite {

    @Id
    private String id;
    @DocumentReference
    @CreatedBy
    private User owner;
    @DocumentReference
    private Set<Guest> guests  = new HashSet<>();
    private Set<String> days  = new HashSet<>();
    private List<List<String>> hours;
    @Field("max_allowed_time")
    private Integer maxTime;
    private Integer passengers;
    private Boolean drop;
    private LocalDateTime arrival;
    private LocalDateTime departure;
    @CreatedDate
    private LocalDateTime created;
    @LastModifiedDate
    private LocalDateTime modified;

}
