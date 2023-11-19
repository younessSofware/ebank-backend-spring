package org.sid.service;

import org.sid.entities.AppUser;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface AccountService {
    public AppUser saveUser(String nom, String prenom, String email,String password,String confirmedPassword);
    public AppUser addNewCustomer(String nom, String prenom, String email,String password,String confirmedPassword, double solde);

    public AppUser loadUserByEmail(String email);
    public List<AppUser> getUsersCustomer();
    public void addRoleToUser(String email,String roleName);
    void deleteUser(String id);
    void activateUser(String id);

    void updateUser(Long id, String nom, String prenom, double solde);

    ResponseEntity<String> resetPassword(String email, String code, String password, String passwordConfirmation);
}
