package tdd.micro.boot.service.impl;

import org.springframework.stereotype.Service;
import tdd.micro.boot.exception.InsufficientFundsException;
import tdd.micro.boot.model.Account;
import tdd.micro.boot.service.BalanceService;

import java.math.BigDecimal;

/**
 * Class which controls the account balances and does not allow negative balances.
 * @author tiago
 */
@Service
public class BalanceServiceImpl implements BalanceService {

    /**
     * @param account
     * @param amount
     * @throws InsufficientFundsException
     */
    public void decrement(Account account, BigDecimal amount) throws InsufficientFundsException {
        BigDecimal decrementedBalance = account.getBalance().subtract(amount);

        if (decrementedBalance.compareTo(BigDecimal.ZERO) < 0)
            throw new InsufficientFundsException("Insufficient funds.");

        account.setBalance(decrementedBalance);

    }

    /**
     * @param account
     * @param amount
     */
    public void increment(Account account, BigDecimal amount) {
        account.setBalance(
                account.getBalance().add(amount)
        );

    }

}
