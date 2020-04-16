package tdd.micro.boot.service;

import org.springframework.http.ResponseEntity;
import tdd.micro.boot.enums.Sort;
import tdd.micro.boot.model.Transaction;

import java.util.List;
import java.util.Optional;

public interface QueryService {
    Optional<Transaction> get(String reference);
    ResponseEntity<List<Transaction>> find(String iban, Sort sort);

}
