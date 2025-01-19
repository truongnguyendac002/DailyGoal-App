package com.projectn.projectn.model;

import com.projectn.projectn.common.enums.Status;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.UUID;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
@Table(name = "user")
public class User implements UserDetails {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "email", unique = true, nullable = false)
    private String email;

    @Column(name = "password", nullable = false)
    private String password;

    @ManyToOne()
    @JoinColumn(name = "role_id")
    private Role role;

    @Column(name = "created_at")
    private Date created_at;

    @Column(name = "updated_at")
    private Date updated_at;

    @Column(name = "name")
    private String name;

    @Column(name = "status")
    @Enumerated(EnumType.STRING)
    private Status status;

    private String avatar;

    @Column(name = "wallet")
    private int wallet;

    @PrePersist
    public void prePersist() {
        created_at = new Date();
        wallet = 0;
        if (updated_at == null) updated_at = created_at;
    }

    @PreUpdate
    public void preUpdate() {
        updated_at = new Date();
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return role != null ? role.getAuthorities() : Collections.emptyList();
    }

    @Override
    public String getUsername() {
        return email;
    }

    public boolean isEnabled() {
        return status.equals(Status.ACTIVE);
    }

}
