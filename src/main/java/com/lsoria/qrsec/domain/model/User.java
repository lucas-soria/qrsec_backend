package com.lsoria.qrsec.domain.model;

import java.util.HashSet;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.DocumentReference;
import org.springframework.data.mongodb.core.mapping.Field;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = "users")
public class User {

    @Id
    private String id;
    @Indexed(unique = true, sparse = true)
    @Field("email")
    private String username;
    private Set<Role> authorities = new HashSet<>();
    @Field("first_name")
    private String firstName;
    @Field("last_name")
    private String lastName;
    @Indexed(unique = true, sparse = true)
    private String dni;
    @DocumentReference
    private Address address;
    private String phone;
    private Boolean enabled = false;

    public User(String username, Set<Role> authorities){
        this.username = username;
        this.authorities = authorities;
    }

    @JsonIgnore
    public boolean isEnabled() {
        return this.enabled;
    }

}
