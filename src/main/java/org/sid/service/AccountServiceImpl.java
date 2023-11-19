package org.sid.service;

import org.sid.dao.AppUserRepository;
import org.sid.dao.OperationRepository;
import org.sid.entities.AppUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class AccountServiceImpl implements AccountService {
    @Autowired
    private OperationRepository operationRepository;
    @Autowired
    private AppUserRepository appUserRepository;
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    public AccountServiceImpl(AppUserRepository appUserRepository, BCryptPasswordEncoder bCryptPasswordEncoder) {
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
    }

    @Override
    public AppUser saveUser(String nom, String prenom, String email, String password, String confirmedPassword) {
        AppUser  user = appUserRepository.findByEmail(email);
        if(user!=null) throw new RuntimeException("User already exists");
        if(!password.equals(confirmedPassword)) throw new RuntimeException("Please confirm your password");
        AppUser appUser=new AppUser();
        appUser.setEmail(email);
        appUser.setNom(nom);
        appUser.setPrenom(prenom);
        appUser.setActived(false);
        appUser.setPassword(bCryptPasswordEncoder.encode(password));
        appUserRepository.save(appUser);
        addRoleToUser(email,"CUSTOMER");
        return appUser;
    }

    @Override
    public AppUser addNewCustomer(String nom, String prenom, String email, String password, String confirmedPassword, double solde) {
        AppUser user = appUserRepository.findByEmail(email);
        if(user!=null) throw new RuntimeException("User already exists");
        if(!password.equals(confirmedPassword)) throw new RuntimeException("Please confirm your password");
        AppUser appUser = new AppUser();
        appUser.setEmail(email);
        appUser.setActived(true);
        appUser.setSolde(solde);
        appUser.setNom(nom);
        appUser.setPrenom(prenom);
        appUser.setPassword(bCryptPasswordEncoder.encode(password));
        appUserRepository.save(appUser);
        addRoleToUser(email,"CUSTOMER");
        return appUser;
    }


    @Override
    public AppUser loadUserByEmail(String email) {
        return appUserRepository.findByEmail(email);
    }

    @Override
    public void addRoleToUser(String email, String roleName) {
        AppUser appUser = appUserRepository.findByEmail(email);
        appUser.setRole(roleName);
    }

    @Override
    public List<AppUser> getUsersCustomer(){
        return appUserRepository.getAppUserByRole("CUSTOMER");
    }

    @Override
    public void deleteUser(String id){
        Long userId = Long.parseLong(id);
        appUserRepository.deleteOperationsByUserId(userId);
        appUserRepository.deleteById(userId);
    }

    @Override
    public void activateUser(String id){
        AppUser appUser = appUserRepository.findById(Long.parseLong(id)).get();
        appUser.setActived(!appUser.isActived());
        appUserRepository.save(appUser);
    }

    @Override
    public void updateUser(Long id, String nom, String prenom, double solde){
        AppUser appUser = appUserRepository.findById(id).get();
        appUser.setNom(nom);
        appUser.setPrenom(prenom);
        appUser.setSolde(solde);

        appUserRepository.save(appUser);
    }

    @Override
    public  ResponseEntity<String>  resetPassword(String email, String code, String password, String passwordConfirmation) {
        AppUser appUser = appUserRepository.findByEmail(email);
        if (!appUser.getCodeVerification().equals(code)) {
            return new ResponseEntity<>("Incorrect verification code", HttpStatus.BAD_REQUEST);
        }
        if (!password.equals(passwordConfirmation)) {
            return new ResponseEntity<>("password et confirmationPassord sot different", HttpStatus.BAD_REQUEST);
        }
        appUser.setPassword(bCryptPasswordEncoder.encode(password));
        appUser.setCodeVerification(null);

        appUserRepository.save(appUser);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

}
