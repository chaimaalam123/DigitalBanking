package org.chaimaa.digitalbanking.services;

import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.chaimaa.digitalbanking.dtos.*;
import org.chaimaa.digitalbanking.entities.*;
import org.chaimaa.digitalbanking.enums.OperationType;
import org.chaimaa.digitalbanking.exceptions.BalenceNotSufficientException;
import org.chaimaa.digitalbanking.exceptions.BankAccountNotFoundException;
import org.chaimaa.digitalbanking.exceptions.CustomerNotFoundException;
import org.chaimaa.digitalbanking.mappers.BankAccountMapperImpl;
import org.chaimaa.digitalbanking.repository.BankAccountRepository;
import org.chaimaa.digitalbanking.repository.CustomerRepository;
import org.chaimaa.digitalbanking.repository.OperationRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;


@Service
@Transactional
@AllArgsConstructor
@Slf4j
public class BankAccountServiceImpl implements BankAccountService {
    private CustomerRepository customerRepository;
    private BankAccountRepository bankAccountRepository;
    private OperationRepository operationRepository;
    private BankAccountMapperImpl bankAccountMapper;
    @Override
    public CustomerDTO saveCustomer(CustomerDTO customerDTO) {
        log.info("Saving new Customer");
        Customer customer = bankAccountMapper.fromCustomerDTOToCustomer(customerDTO);
        Customer savedCustomer= customerRepository.save(customer);
        return  bankAccountMapper.fromCustomerToCustomerDTO(savedCustomer);
    }

    @Override
    public CurrentBankAccountDTO saveCurrentBankAccount(double initialBalance, double overDraft, Long customerId) throws CustomerNotFoundException {
        CurrentAccount currentAccount = new CurrentAccount();
        Customer customer = customerRepository.findById(customerId).orElse(null);
        if(customer == null){
            throw new CustomerNotFoundException("Customer not found");
        }
        currentAccount.setId(UUID.randomUUID().toString());
        currentAccount.setCreatedAt(new Date());
        currentAccount.setBalance(initialBalance);
        currentAccount.setCustomer(customer);
        currentAccount.setOverDraft(overDraft);
        CurrentAccount savedBankAccount = bankAccountRepository.save(currentAccount);
        return bankAccountMapper.fromCurrentBankAccountToCurrentBankAccountDTO(savedBankAccount);
    }

    @Override
    public SavingBankAccountDTO saveSavingBankAccount(double initialBalance, double interestRate, Long customerId) throws CustomerNotFoundException {
        SavingAccount savingAccount  = new SavingAccount ();
        Customer customer = customerRepository.findById(customerId).orElse(null);
        if(customer == null){
            throw new CustomerNotFoundException("Customer not found");
        }
        savingAccount.setId(UUID.randomUUID().toString());
        savingAccount.setCreatedAt(new Date());
        savingAccount.setBalance(initialBalance);
        savingAccount.setCustomer(customer);
        savingAccount .setInterestRate(interestRate);
        SavingAccount savedBankAccount = bankAccountRepository.save(savingAccount );
        return bankAccountMapper.fromSavingBankAccountToSavingBankAccountDTO(savedBankAccount);
    }

    @Override
    public List<CustomerDTO> listCustomer() {
        List<Customer> customers = customerRepository.findAll();
        List<CustomerDTO> customerDTOS = customers.stream().map(customer -> bankAccountMapper.fromCustomerToCustomerDTO(customer)).collect(Collectors.toList());
       /* List<CustomerDTO> customerDTOS1 = new ArrayList<>();
        for(Customer customer:customers){
            CustomerDTO customerDTO = bankAccountMapper.fromCustomerToCustomerDTO(customer);
            customerDTOS1.add(customerDTO);
        }*/
        return customerDTOS;
    }

    @Override
    public CustomerDTO getCustomer(Long customerId) throws CustomerNotFoundException {
        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new CustomerNotFoundException("Customer Not found"));
        return bankAccountMapper.fromCustomerToCustomerDTO(customer);
    }

    @Override
    public CustomerDTO updateCustomer(CustomerDTO customerDTO) {
        log.info("Saving new Customer");
        Customer customer = bankAccountMapper.fromCustomerDTOToCustomer(customerDTO);
        Customer savedCustomer= customerRepository.save(customer);
        return  bankAccountMapper.fromCustomerToCustomerDTO(savedCustomer);
    }

    @Override
    public void deleteCustomer(Long customerId){
        customerRepository.deleteById(customerId);
    }
    @Override
    public BankAccountDTO getBankAccount(String accountId) throws BankAccountNotFoundException {
        BankAccount bankAccount = bankAccountRepository.findById(accountId).orElseThrow(() -> new BankAccountNotFoundException("BankAccount not found"));
        if(bankAccount instanceof CurrentAccount){
            CurrentAccount currentAccount = (CurrentAccount) bankAccount;
            return bankAccountMapper.fromCurrentBankAccountToCurrentBankAccountDTO(currentAccount);
        }
        else{
            SavingAccount savingAccount = (SavingAccount) bankAccount;
            return  bankAccountMapper.fromSavingBankAccountToSavingBankAccountDTO(savingAccount);
        }
    }

    @Override
    public void debit(String accountId, double amount, String description) throws BankAccountNotFoundException, BalenceNotSufficientException {
    BankAccount bankAccount = bankAccountRepository.findById(accountId).orElseThrow(() -> new BankAccountNotFoundException("BankAccount not found"));
    if(bankAccount.getBalance()< amount){
        throw new BalenceNotSufficientException("Balence not sufficient");
    }
    Operation operation = new Operation();
    operation.setType(OperationType.DEBIT);
    operation.setAmount(amount);
    operation.setDescription(description);
    operation.setOperationDate(new Date());
    operation.setBankAccount(bankAccount);
    operationRepository.save(operation);
    bankAccount.setBalance(bankAccount.getBalance()-amount);
    bankAccountRepository.save(bankAccount);
    }

    @Override
    public void credit(String accountId, double amount, String description) throws BankAccountNotFoundException, BalenceNotSufficientException {
        BankAccount bankAccount = bankAccountRepository.findById(accountId).orElseThrow(() -> new BankAccountNotFoundException("BankAccount not found"));
        if(bankAccount.getBalance()< amount){
            throw new BalenceNotSufficientException("Balence not sufficient");
        }
        Operation operation = new Operation();
        operation.setType(OperationType.CREDIT);
        operation.setAmount(amount);
        operation.setDescription(description);
        operation.setOperationDate(new Date());
        operation.setBankAccount(bankAccount);
        operationRepository.save(operation);
        bankAccount.setBalance(bankAccount.getBalance()+amount);
        bankAccountRepository.save(bankAccount);
    }

    @Override
    public void transfer(String accountIdSource, String accountIdDestination, double amount) throws BankAccountNotFoundException, BalenceNotSufficientException {
      this.debit(accountIdSource,amount,"Transfer to "+ accountIdDestination);
      this.credit(accountIdDestination,amount,"Transfer from "+ accountIdSource);
    }

    @Override
    public List<BankAccountDTO> bankAccountList() {
        List<BankAccount> bankAccounts = bankAccountRepository.findAll();
        List<BankAccountDTO> bankAccountDTOS = bankAccounts.stream().map(bankAccount -> {
           if(bankAccount instanceof  SavingAccount){
             SavingAccount savingAccount = (SavingAccount) bankAccount;
               return bankAccountMapper.fromSavingBankAccountToSavingBankAccountDTO(savingAccount);
           }
           else{
               CurrentAccount currentAccount= (CurrentAccount) bankAccount;
               return  bankAccountMapper.fromCurrentBankAccountToCurrentBankAccountDTO(currentAccount);
           }
        }).collect(Collectors.toList());
        return bankAccountDTOS;
    }

    @Override
    public List<AccountOperationDTO> accountHistory(String accountId){
        List<Operation> accountOperations = operationRepository.findByBankAccountId(accountId);
        return accountOperations.stream().map(op->bankAccountMapper.fromOperationToOperationDTO(op)).collect(Collectors.toList());
    }

    @Override
    public AccountHistoryDTO  getAccountHistory(String accountId , int page ,int size) throws BankAccountNotFoundException {
        BankAccount bankAccount = bankAccountRepository.findById(accountId).orElse(null);
        if(bankAccount == null) throw new BankAccountNotFoundException("Account not found");
        Page<Operation> accountOperations = operationRepository.findByBankAccountIdOrderByOperationDateDesc(accountId, PageRequest.of(page, size));
        AccountHistoryDTO accountHistoryDTO=new AccountHistoryDTO();
        accountHistoryDTO.setAccountId(bankAccount.getId());
        accountHistoryDTO.setBalance(bankAccount.getBalance());
        accountHistoryDTO.setCurrentPage(page);
        accountHistoryDTO.setPageSize(size);
        accountHistoryDTO.setTotalPages(accountOperations.getTotalPages());
        List<AccountOperationDTO> accountOperationDTOS = accountOperations.getContent().stream().map
                (op -> bankAccountMapper.fromOperationToOperationDTO(op)).collect(Collectors.toList());
        accountHistoryDTO.setAccountOperationDTOS(accountOperationDTOS);
        return accountHistoryDTO;
    }
}
