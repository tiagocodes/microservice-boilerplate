package tdd.micro.boot.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import tdd.micro.boot.enums.Sort;
import tdd.micro.boot.model.Account;
import tdd.micro.boot.model.Transaction;
import tdd.micro.boot.repository.TransactionRepository;
import tdd.micro.boot.service.QueryService;

import java.util.List;
import java.util.Optional;

/**
 * Service to query transactions.
 * @author tiago
 */
@Service
public class QueryServiceImpl implements QueryService {

    @Autowired
    private TransactionRepository transactionRepository;

    @Override
    public Optional<Transaction> get(String reference) {
        return Optional.ofNullable(transactionRepository.findByReference(reference));

    }

    /**
     * @param iban
     * @param sort
     * @return
     */
    @Override
    public ResponseEntity<List<Transaction>> find(String iban, Sort sort) {
        if (sort.equals(Sort.ASC)) {
            List<Transaction> result = findAscending(iban);
            return ResponseEntity.ok(result);

        } else if (sort.equals(Sort.DESC)) {
            List<Transaction> result = findDescending(iban);
            return ResponseEntity.ok(result);

        } else {
            return ResponseEntity.badRequest().build();

        }
    }

    /**
     * @param iban
     * @return
     */
    private List<Transaction> findAscending(String iban) {
        return transactionRepository.findAllByAccountOrderByAmount(new Account(iban));
    }

    /**
     * @param iban
     * @return
     */
    private List<Transaction> findDescending(String iban) {
        return transactionRepository.findAllByAccountOrderByAmountDesc(new Account(iban));
    }

}
