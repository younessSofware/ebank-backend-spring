package org.sid.web;

import lombok.Data;
import org.sid.dao.AppUserRepository;
import org.sid.entities.AppUser;
import org.sid.service.AccountService;
import org.sid.service.MailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class UserController {
    @Autowired
    private AccountService accountService;
    @Autowired
    private MailService mailService;
    @Autowired
    private AppUserRepository appUserRepository;
    @PostMapping("/register")
    public AppUser register(@RequestBody  UserForm registerForm){
        return  accountService.saveUser(
                registerForm.getNom(),
                registerForm.getPrenom(),
                registerForm.getEmail(),registerForm.getPassword(),registerForm.getConfirmedPassword());
    }

    @PostMapping ("/forgotPassword")
    public ResponseEntity<Void> forgotPassword(@RequestBody  ForgotPaswordForm forgotPaswordForm){
        String email = forgotPaswordForm.getEmail();
        AppUser appUser = accountService.loadUserByEmail(email);
        if(appUser == null){
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        String code = "1234";
        appUser.setCodeVerification(code);
        appUserRepository.save(appUser);
        mailService.sendMail(email, "Vérification de compte", "Bonjour, le code pour vérifier votre compte est : " + code);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }


    @PostMapping ("/resetPassword")
    public ResponseEntity<String> resetPassword(@RequestBody  ResetPasswordForm resetPasswordForm){
        return accountService.resetPassword(resetPasswordForm.getEmail(), resetPasswordForm.getCode(),
                resetPasswordForm.getPassword(), resetPasswordForm.getPasswordConfirmation());
    }

    @PostMapping("/users/addNewClient")
    public AppUser addNewClient(@RequestBody  UserForm userForm){
        return  accountService.addNewCustomer(
                userForm.getNom(),
                userForm.getPrenom(),
                userForm.getEmail(),userForm.getPassword(),userForm.getConfirmedPassword(), userForm.getSolde());
    }
    @GetMapping("/users/all")
    public List<AppUser> getAll(){

        return accountService.getUsersCustomer();
    }

    @GetMapping("/users/profile")
    public AppUser getProfile() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        return accountService.loadUserByEmail(email);
    }

    @GetMapping("/users/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable String id) {
        accountService.deleteUser(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @PostMapping("/users/editClient")
    public ResponseEntity<Void> editUser(@RequestBody  UserForm userForm) {
        accountService.updateUser(userForm.getId(), userForm.getNom(),
                userForm.getPrenom(), userForm.getSolde());
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PostMapping("/users/{id}/activate")
    public ResponseEntity<Void> activateUser(@PathVariable String id) {
        accountService.activateUser(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }


}

@Data
class UserForm{
    private Long id;
    private  String nom;
    private  String prenom;
    private String email;
    private String password;
    private String confirmedPassword;
    private double solde;
}

@Data
class  ForgotPaswordForm{
    private String email;
}



@Data
class  ResetPasswordForm{
    private String email;
    private String code;
    private String password;
    private String passwordConfirmation;
}
