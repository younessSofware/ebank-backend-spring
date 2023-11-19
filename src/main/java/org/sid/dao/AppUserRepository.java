package org.sid.dao;

import org.sid.entities.AppUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@RepositoryRestResource
public interface AppUserRepository extends JpaRepository<AppUser,Long> {
    public AppUser findByEmail(String email);
    public List<AppUser> getAppUserByRole(String roleName);

    @Transactional
    @Modifying
    @Query("DELETE FROM Operation o WHERE o.verseur.id = :userId OR o.beneficiaire.id = :userId")
    void deleteOperationsByUserId(Long userId);
}
