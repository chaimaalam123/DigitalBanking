package org.chaimaa.digitalbanking;

import org.chaimaa.digitalbanking.dtos.BankAccountDTO;
import org.chaimaa.digitalbanking.dtos.CurrentBankAccountDTO;
import org.chaimaa.digitalbanking.dtos.CustomerDTO;
import org.chaimaa.digitalbanking.dtos.SavingBankAccountDTO;
import org.chaimaa.digitalbanking.exceptions.CustomerNotFoundException;
import org.chaimaa.digitalbanking.services.BankAccountService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import java.util.List;
import java.util.stream.Stream;

@SpringBootApplication
public class DigitalBankingApplication {

    public static void main(String[] args) {
        SpringApplication.run(DigitalBankingApplication.class, args);
    }
    @Bean
    CommandLineRunner commandLineRunner(BankAccountService bankAccountService){
        return args -> {
            Stream.of("Hassan","Imane","Mohamed").forEach(name->{
                CustomerDTO customer=new CustomerDTO();
                customer.setName(name);
                customer.setEmail(name+"@gmail.com");
                bankAccountService.saveCustomer(customer);
            });
            bankAccountService.listCustomer().forEach(customer->{
                try {
                    bankAccountService.saveCurrentBankAccount(Math.random()*90000,9000,customer.getId());
                    bankAccountService.saveSavingBankAccount(Math.random()*120000,5.5,customer.getId());

                } catch (CustomerNotFoundException e) {
                    e.printStackTrace();
                }
            });
            List<BankAccountDTO> bankAccounts = bankAccountService.bankAccountList();
            for (BankAccountDTO bankAccount:bankAccounts){
                for (int i = 0; i <10 ; i++) {
                    String accountId;
                    if(bankAccount instanceof SavingBankAccountDTO){
                        accountId=((SavingBankAccountDTO) bankAccount).getId();
                    } else{
                        accountId=((CurrentBankAccountDTO) bankAccount).getId();
                    }
                    bankAccountService.credit(accountId,10000+Math.random()*12000,"Credit");
                    bankAccountService.debit(accountId,1000+Math.random()*9000,"Debit");
                }
            }
        };
    }


  /*  @Bean
    CommandLineRunner start(CustomerRepository customerRepository , BankAccountRepository bankAccountRepository , OperationRepository operationRepository){
        return  args -> {
            Stream.of("chaimaa","imane","ilham").forEach(name ->{
                Customer customer = Customer.builder().name(name).email(name+"@gmail.com").build();
                customerRepository.save(customer);
            });
            customerRepository.findAll().forEach(customer -> {
                CurrentAccount currentAccount = new CurrentAccount();
                currentAccount.setId(UUID.randomUUID().toString());
                currentAccount.setOverDraft(9000);
                currentAccount.setBalance(Math.random()*9000);
                currentAccount.setCreatedAt(new Date());
                currentAccount.setStatus(AccountStatus.ACTIVATED);
                currentAccount.setCustomer(customer);
                bankAccountRepository.save(currentAccount);
                SavingAccount savingAccount= new SavingAccount();
                savingAccount.setId(UUID.randomUUID().toString());
                savingAccount.setInterestRate(5000);
                savingAccount.setBalance(Math.random()*9000);
                savingAccount.setCreatedAt(new Date());
                savingAccount.setStatus(AccountStatus.ACTIVATED);
                savingAccount.setCustomer(customer);
                bankAccountRepository.save(savingAccount);
            });
            bankAccountRepository.findAll().forEach(acc ->{
                for(int i=0;i<10 ;i++){
                    Operation operation = Operation.builder()
                            .operationDate(new Date())
                            .amount(Math.random()*12000)
                            .type(Math.random()>0.5 ? OperationType.DEBIT:OperationType.CREDIT)
                            .bankAccount(acc)
                            .build();
                    operationRepository.save(operation);
                }
            });
        };
    }*/

}
