package org.sid.dao;

import org.sid.entities.AppUser;
import org.sid.entities.Operation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OperationRepository  extends JpaRepository<Operation,Long> {
    public List<Operation> findByVerseur(AppUser verseur);
    public List<Operation> findByBeneficiaire(AppUser beneficiaire);
}
