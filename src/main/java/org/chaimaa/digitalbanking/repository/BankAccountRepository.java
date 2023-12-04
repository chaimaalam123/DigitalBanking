package org.chaimaa.digitalbanking.repository;

import org.chaimaa.digitalbanking.entities.BankAccount;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BankAccountRepository  extends JpaRepository<BankAccount,String> {
}
