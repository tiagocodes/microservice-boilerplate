package tdd.micro.boot.service;

import tdd.micro.boot.exception.InsufficientFundsException;
import tdd.micro.boot.model.Transaction;

public interface TransactionService {
    Transaction validateAndSave(Transaction transaction) throws InsufficientFundsException;
}
