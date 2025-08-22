package com.webapp.bankingportal.entity;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToOne;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Data
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotEmpty
    private String name;

    @NotEmpty
    private String password;

    @Email
   @NotEmpty
    @Column(unique = true)
    private String email;

    @NotEmpty
    private String countryCode;

    @NotEmpty
    @Column(unique = true)
    private String phoneNumber;

    @NotEmpty
    private String address;

    // Establishing a one-to-one relationship with the account
    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL)
    private Account account;

}
