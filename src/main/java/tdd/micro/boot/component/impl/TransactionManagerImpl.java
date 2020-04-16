package tdd.micro.boot.component.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import tdd.micro.boot.component.TransactionManager;
import tdd.micro.boot.exception.InsufficientFundsException;
import tdd.micro.boot.model.Transaction;
import tdd.micro.boot.repository.TransactionRepository;
import tdd.micro.boot.service.BalanceService;

import java.math.BigDecimal;
import java.util.Date;

/**
 * Component responsable for managing transactions.
 * @author tiago
 */
@Component
public class TransactionManagerImpl implements TransactionManager {

    private static String REF = "REF";

    @Autowired
    private BalanceService balanceService;

    @Autowired
    private TransactionRepository transactionRepository;

    /**
     * @param transaction
     * @return
     * @throws InsufficientFundsException
     */
    public Transaction saveTransaction(Transaction transaction) throws InsufficientFundsException {
        if (transaction.getReference() == null) {
            transaction.setReference(
                    new StringBuilder().append(REF)
                            .append(new Date().getTime()).toString()
            );
        }

        if (transaction.getAmount().compareTo(BigDecimal.ZERO) < 0) {
            balanceService.decrement(transaction.getAccount(), transaction.getAmount()
                    .multiply(new BigDecimal(-1)));

        } else if (transaction.getAmount().compareTo(BigDecimal.ZERO) > 0) {
            balanceService.increment(transaction.getAccount(), transaction.getAmount());

        }

        return transactionRepository.save(transaction);
    }

}
