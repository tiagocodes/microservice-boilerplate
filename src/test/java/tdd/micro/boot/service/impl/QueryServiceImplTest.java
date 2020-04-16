package tdd.micro.boot.service.impl;

import org.junit.Rule;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;
import org.springframework.http.ResponseEntity;
import tdd.micro.boot.enums.Sort;
import tdd.micro.boot.model.Transaction;
import tdd.micro.boot.repository.TransactionRepository;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

public class QueryServiceImplTest {

    @Mock
    private TransactionRepository transactionRepository;

    @InjectMocks
    QueryServiceImpl queryService;

    @Rule
    public MockitoRule mockitoRule = MockitoJUnit.rule();

    @Test
    public void get() {
        String reference = "abc123";
        Transaction transaction = new Transaction();
        transaction.setReference(reference);
        when(transactionRepository.findByReference(reference)).thenReturn(transaction);
        Optional<Transaction> optional = queryService.get(reference);
        Transaction result = optional.get();
        assertTrue(result.getReference().equals(reference));
    }

    @Test
    public void find() {
        List<Transaction> transactions = new ArrayList<>();
        Transaction transaction1 = new Transaction();
        transaction1.setAmount(new BigDecimal(1));
        transactions.add(transaction1);

        Transaction transaction2 = new Transaction();
        transaction2.setAmount(new BigDecimal(8));
        transactions.add(transaction2);

        Transaction transaction3 = new Transaction();
        transaction3.setAmount(new BigDecimal(5));
        transactions.add(transaction3);

        when(transactionRepository.findAllByAccountOrderByAmount(any())).thenReturn(transactions);
        ResponseEntity<List<Transaction>> response1 = queryService.find("ES123", Sort.ASC);
        assertTrue(response1.getBody().size() == 3);

        when(transactionRepository.findAllByAccountOrderByAmountDesc(any())).thenReturn(transactions);
        ResponseEntity<List<Transaction>> response2 = queryService.find("ES123", Sort.DESC);
        assertTrue(response2.getBody().size() == 3);
    }
}