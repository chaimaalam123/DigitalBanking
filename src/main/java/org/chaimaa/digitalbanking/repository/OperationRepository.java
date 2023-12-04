package org.chaimaa.digitalbanking.repository;

import org.chaimaa.digitalbanking.entities.Operation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.util.List;

public interface OperationRepository extends JpaRepository<Operation,Long> {

     public List<Operation> findByBankAccountId(String accountId);
     public Page<Operation> findByBankAccountIdOrderByOperationDateDesc(String accountId, Pageable pageable);

}
