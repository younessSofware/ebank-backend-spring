package org.sid.service;

import org.sid.entities.Operation;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface OperationService {
    public Operation saveOperation(String emailVerseur, String emailBeneficiaire, double amount);
    public List<Operation> getVersements(String email);
    public List<Operation> getBeneficiaires(String email);
    ResponseEntity<String> validateOperation(String code, Long id);
}
