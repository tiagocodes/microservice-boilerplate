package tdd.micro.boot.service.impl;

import org.iban4j.IbanUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tdd.micro.boot.component.TransactionManager;
import tdd.micro.boot.exception.InsufficientFundsException;
import tdd.micro.boot.model.Account;
import tdd.micro.boot.model.Transaction;
import tdd.micro.boot.repository.AccountRepository;
import tdd.micro.boot.service.TransactionService;

import java.util.Optional;

/**
 * Service to manage transactions.
 * @author tiago
 */
@Service
public class TransactionServiceImpl implements TransactionService {

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private TransactionManager transactionManager;

    @Override
    @Transactional
    public Transaction validateAndSave(Transaction transaction) throws InsufficientFundsException {
        IbanUtil.validate(transaction.getAccount().getIban());
        Optional<Account> account = accountRepository.findById(transaction.getAccount().getIban());
        transaction.setAccount(account.get());
        return save(transaction);
    }

    /**
     * @param transaction
     * @return
     * @throws InsufficientFundsException
     */
    private Transaction save(Transaction transaction) throws InsufficientFundsException {
        return transactionManager.saveTransaction(transaction);
    }


}
