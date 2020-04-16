package tdd.micro.boot.component;

import tdd.micro.boot.exception.InsufficientFundsException;
import tdd.micro.boot.model.Transaction;

public interface TransactionManager {
    Transaction saveTransaction(Transaction transaction) throws InsufficientFundsException;
}
