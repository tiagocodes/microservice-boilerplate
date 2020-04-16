package tdd.micro.boot.service;

import tdd.micro.boot.exception.InsufficientFundsException;
import tdd.micro.boot.model.Account;

import java.math.BigDecimal;

public interface BalanceService {

    void decrement(Account account, BigDecimal amount) throws InsufficientFundsException;

    void increment(Account account, BigDecimal amount);

}
