package org.chaimaa.digitalbanking.services;

import org.chaimaa.digitalbanking.dtos.*;
import org.chaimaa.digitalbanking.entities.BankAccount;
import org.chaimaa.digitalbanking.entities.CurrentAccount;
import org.chaimaa.digitalbanking.entities.Customer;
import org.chaimaa.digitalbanking.entities.SavingAccount;
import org.chaimaa.digitalbanking.exceptions.BalenceNotSufficientException;
import org.chaimaa.digitalbanking.exceptions.BankAccountNotFoundException;
import org.chaimaa.digitalbanking.exceptions.CustomerNotFoundException;

import java.util.List;

public interface BankAccountService {
    public CustomerDTO saveCustomer(CustomerDTO customer);
    public CurrentBankAccountDTO saveCurrentBankAccount(double initialBalance, double overDraft, Long customerId) throws CustomerNotFoundException;
    public SavingBankAccountDTO saveSavingBankAccount(double initialBalance, double interestRate, Long customerId) throws CustomerNotFoundException;
    public List<CustomerDTO> listCustomer();

    CustomerDTO getCustomer(Long customerId) throws CustomerNotFoundException;

    CustomerDTO updateCustomer(CustomerDTO customerDTO);

    void deleteCustomer(Long customerId);

    public BankAccountDTO getBankAccount(String accountId) throws BankAccountNotFoundException;
    public void debit(String accountId,double amount ,String description) throws BankAccountNotFoundException, BalenceNotSufficientException;

    public void credit(String accountId,double amount ,String description) throws BankAccountNotFoundException, BalenceNotSufficientException;
     public void transfer(String accountIdSource,String accountIdDestination,double amount) throws BankAccountNotFoundException, BalenceNotSufficientException;

     public List<BankAccountDTO> bankAccountList();


    List<AccountOperationDTO> accountHistory(String accountId);

    AccountHistoryDTO  getAccountHistory(String accountId, int page, int size) throws BankAccountNotFoundException;
}
