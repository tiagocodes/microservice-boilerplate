package tdd.micro.boot.service.impl;

import org.junit.Rule;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;
import tdd.micro.boot.exception.InsufficientFundsException;
import tdd.micro.boot.model.Account;
import tdd.micro.boot.repository.AccountRepository;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.when;

public class BalanceServiceImplTest {

    @Mock
    AccountRepository accountRepository;

    @InjectMocks
    BalanceServiceImpl balanceService;

    @Rule
    public MockitoRule mockitoRule = MockitoJUnit.rule();

    @Test
    public void decrement() {
        Account account = new Account();
        when(accountRepository.findById(account.getIban())).thenReturn(Optional.of(account));
        try {
            balanceService.decrement(account, new BigDecimal(1));
        } catch (InsufficientFundsException e) {
            e.printStackTrace();
            return;
        }
        fail();
    }

    @Test
    public void increment() {
        Account account = new Account();
        assertTrue(account.getBalance().equals(new BigDecimal(0)));
        when(accountRepository.findById(account.getIban())).thenReturn(Optional.of(account));
        balanceService.increment(account, new BigDecimal(1));
        assertTrue(account.getBalance().equals(new BigDecimal(1)));
    }

}