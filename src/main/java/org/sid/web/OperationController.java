package org.sid.web;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.hibernate.Hibernate;
import org.sid.dao.AppUserRepository;
import org.sid.entities.AppUser;
import org.sid.entities.Operation;
import org.sid.service.AccountService;
import org.sid.service.OperationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class OperationController {

    @Autowired
    private OperationService operationService;
    @Autowired
    private AccountService accountService;
    @Autowired
    private AppUserRepository appUserRepository;
    @PostMapping("/operations/verser")
    public Operation verser(@RequestBody OperationFormRequest operationFormRequest){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        return operationService.saveOperation(email,
                operationFormRequest.getEmailBenificaire(), operationFormRequest.getAmount());
        // send smtp email

    }

    @GetMapping("/operations/versements")
    public List<Operation> getVersements(){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        return operationService.getVersements(email);
    }

    @GetMapping("/operations/beneficiaires")
    public List<Operation> getBeneficiaires(){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        return operationService.getBeneficiaires(email);
    }

    @PostMapping("/clients/verificationOperationCode")
    public ResponseEntity<String> verificationOperationCode(@RequestBody VerificationOperatioForm verificationOperationForm){
        return operationService.validateOperation(verificationOperationForm.getCode(),
                Long.parseLong(verificationOperationForm.getId()));
    }

}
@Data @ToString
class OperationFormRequest{
    private String emailVerseur;
    private String emailBenificaire;
    private double amount;
}

@Data
class  VerificationOperatioForm{
    private String code;
    private String id;
}
