package tdd.micro.boot.component.impl;

import org.junit.Rule;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;
import org.mockito.stubbing.Answer;
import tdd.micro.boot.exception.InsufficientFundsException;
import tdd.micro.boot.model.Account;
import tdd.micro.boot.model.Transaction;
import tdd.micro.boot.repository.AccountRepository;
import tdd.micro.boot.repository.TransactionRepository;
import tdd.micro.boot.service.impl.BalanceServiceImpl;

import java.math.BigDecimal;

import static junit.framework.TestCase.assertTrue;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.when;

public class TransactionManagerImplTest {

    @InjectMocks
    TransactionManagerImpl transactionManager;

    @Mock
    TransactionRepository transactionRepository;

    @Mock
    BalanceServiceImpl balanceService;

    @Mock
    AccountRepository accountRepository;

    @Rule
    public MockitoRule mockitoRule = MockitoJUnit.rule();

    @Test
    public void saveTransactionPositive() throws InsufficientFundsException {
        Transaction transaction = new Transaction();
        transaction.setAmount(new BigDecimal(5));

        Account account = new Account();
        account.setIban("ES7921000813610123456789");

        transaction.setAccount(account);

        when(transactionRepository.save(transaction)).thenReturn(transaction);

        doAnswer(new Answer<Void>() {
            public Void answer(InvocationOnMock invocation) {
                Object[] args = invocation.getArguments();
                Account a = (Account) args[0];
                account.setBalance(
                        account.getBalance().add(transaction.getAmount())
                );
                return null;
            }
        }).when(balanceService).increment(account, transaction.getAmount());

        transactionManager.saveTransaction(transaction);

        assertTrue(account.getBalance().equals(transaction.getAmount()));

    }

    @Test
    public void saveTransactionNegative() throws InsufficientFundsException {
        BigDecimal initialBalance = new BigDecimal(14);

        Transaction transaction = new Transaction();
        transaction.setAmount(new BigDecimal(-5));

        Account account = new Account();
        account.setIban("ES7921000813610123456789");
        account.setBalance(initialBalance);

        transaction.setAccount(account);

        when(transactionRepository.save(transaction)).thenReturn(transaction);

        doAnswer(new Answer<Void>() {
            public Void answer(InvocationOnMock invocation) {
                Object[] args = invocation.getArguments();
                Account a = (Account) args[0];
                a.setBalance(
                        a.getBalance().add(transaction.getAmount())
                );
                return null;
            }
        }).when(balanceService).decrement(account, transaction.getAmount()
                .multiply(new BigDecimal(-1)));

        transactionManager.saveTransaction(transaction);

        assertTrue(account.getBalance().equals(
                initialBalance.add(transaction.getAmount())
        ));

    }

}