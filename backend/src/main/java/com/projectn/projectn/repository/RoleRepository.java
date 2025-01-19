package com.projectn.projectn.repository;

import com.projectn.projectn.common.enums.RoleEnum;
import com.projectn.projectn.model.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {
    boolean existsByName(RoleEnum name);

    Optional<Role> findByName(RoleEnum name);

    Optional<Role> getRoleByName(RoleEnum name);
}
