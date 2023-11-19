package org.sid.service;

import lombok.AllArgsConstructor;
import org.sid.dao.AppUserRepository;
import org.sid.dao.OperationRepository;
import org.sid.entities.AppUser;
import org.sid.entities.Operation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
@Service
@Transactional
public class OperationServiceImpl implements OperationService {
    @Autowired
    private AppUserRepository appUserRepository;
    @Autowired
    private OperationRepository operationRepository;
    @Autowired
    private MailService mailService;
    @Override
    public Operation saveOperation(String emailVerseur, String emailBeneficiaire, double amount) throws RuntimeException {
        // Retrieve the user who is transferring funds (verseur)
        AppUser appVerseur = appUserRepository.findByEmail(emailVerseur);
        // Retrieve the user who is receiving the funds (beneficiaire)
        AppUser appBeneficiaire = appUserRepository.findByEmail(emailBeneficiaire);
        // Check if either user is null (not found in the repository)
        System.out.println(emailVerseur);
        System.out.println(appBeneficiaire.getEmail());
        if (appVerseur == null || appBeneficiaire == null) {
            // Handle the case where one of the users is not found
            throw new RuntimeException("Customer "+emailBeneficiaire+" not found");
        }

        if(emailBeneficiaire.equals(emailVerseur)){
            throw new RuntimeException("The beneficiary's email must be different to your email.");
        }
        // Check if the verseur has sufficient balance for the transfer
        if (appVerseur.getSolde() < amount) {
            // Handle the case where the verseur does not have enough balance
            throw new RuntimeException("Insufficient balance for the transfer");
        }
        String code = "1234";
        mailService.sendMail(emailVerseur, "Vérification d'une operation", "Bonjour, le code pour vérifier votre operation d'envoyer un "+amount+" vers "+appBeneficiaire.getEmail()+" est : " + code);

        // Create a new operation
        Operation operation = new Operation(null, amount, code, false, appVerseur, appBeneficiaire, new Date());

        appVerseur.setSolde(appVerseur.getSolde() - amount);
        appBeneficiaire.setSolde(appBeneficiaire.getSolde() + amount);

        // Save the changes to the users and the new operation
        appUserRepository.save(appVerseur);
        appUserRepository.save(appBeneficiaire);
        operationRepository.save(operation);
        return operationRepository.save(operation);
    }
    @Override
    public List<Operation> getVersements(String email){
        AppUser verseur = appUserRepository.findByEmail(email);
        return operationRepository.findByVerseur(verseur);
    }

    @Override
    public List<Operation> getBeneficiaires(String email){
        AppUser beneficiaire = appUserRepository.findByEmail(email);
        return operationRepository.findByBeneficiaire(beneficiaire);
    }

    @Override
    public ResponseEntity<String> validateOperation(String code, Long id){
        Operation operation = operationRepository.getOne(id);
        if(!operation.getCodeVerificationOperatiom().equals(code)){
            return new ResponseEntity<>("Incorrect verification code", HttpStatus.BAD_REQUEST);
        }
        operation.setCodeVerificationOperatiom(null);
        operation.setActive(true);
        try {
            mailService.sendMail( operation.getBeneficiaire().getEmail(), "Versmenet de " + operation.getAmount(),
                    "Bonjour, " + operation.getVerseur().getPrenom() + " " + operation.getVerseur().getNom() + "a verser un solde de " + operation.getAmount());
        }catch (Exception exception){
            System.out.println(exception.toString());
        }
        return new ResponseEntity<>("opertation valide", HttpStatus.OK);
    }

}
