package tdd.micro.boot.service.impl;

import org.iban4j.IbanFormatException;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;
import org.mockito.stubbing.Answer;
import tdd.micro.boot.component.impl.TransactionManagerImpl;
import tdd.micro.boot.exception.InsufficientFundsException;
import tdd.micro.boot.model.Account;
import tdd.micro.boot.model.Transaction;
import tdd.micro.boot.repository.AccountRepository;
import tdd.micro.boot.repository.TransactionRepository;

import java.math.BigDecimal;
import java.util.Optional;

import static junit.framework.TestCase.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.when;

public class TransactionServiceImplTest {

    @Mock
    private TransactionRepository transactionRepository;

    @Mock
    AccountRepository accountRepository;

    @InjectMocks
    TransactionServiceImpl transactionService;

    @Mock
    BalanceServiceImpl balanceService;

    @Mock
    TransactionManagerImpl transactionManager;

    @Rule
    public MockitoRule mockitoRule = MockitoJUnit.rule();


    @Test
    public void validateAndSaveIncrement() throws InsufficientFundsException {
        Account account = new Account();
        account.setIban("ES7921000813610123456789");
        assertTrue(account.getBalance().equals(BigDecimal.ZERO));
        Transaction transaction = new Transaction();
        transaction.setAccount(account);
        transaction.setAmount(new BigDecimal(5));

        when(accountRepository.findById(account.getIban())).thenReturn(Optional.of(account));

        doAnswer(new Answer<Void>() {
            public Void answer(InvocationOnMock invocation) {
                Object[] args = invocation.getArguments();
                Transaction transaction1 = (Transaction) args[0];
                transaction1.getAccount().setBalance(
                        transaction1.getAccount().getBalance().add(transaction.getAmount())
                );
                return null;
            }
        }).when(transactionManager).saveTransaction(transaction);

        transactionService.validateAndSave(transaction);
        assertTrue(account.getBalance().equals(transaction.getAmount()));

    }

    @Test
    public void validateAndSaveDecrement() throws InsufficientFundsException {
        BigDecimal initialAmount = new BigDecimal(10);
        Account account = new Account();
        account.setIban("ES7921000813610123456789");
        account.setBalance(initialAmount);

        Transaction transaction = new Transaction();
        transaction.setAccount(account);
        transaction.setAmount(new BigDecimal(-5));

        when(accountRepository.findById(account.getIban())).thenReturn(Optional.of(account));

        doAnswer(new Answer<Void>() {
            public Void answer(InvocationOnMock invocation) {
                Object[] args = invocation.getArguments();
                Transaction transaction1 = (Transaction) args[0];
                transaction1.getAccount().setBalance(
                        transaction1.getAccount().getBalance().subtract(transaction.getAmount()
                                .multiply(new BigDecimal(-1)))
                );
                return null;
            }
        }).when(transactionManager).saveTransaction(transaction);

        transactionService.validateAndSave(transaction);
        assertTrue(account.getBalance().equals(
                initialAmount.subtract(transaction.getAmount().multiply(new BigDecimal(-1)))
        ));
    }

    @Test
    public void validateAndSaveDecrementInsufficientFunds() {
        Account account = new Account();
        account.setIban("ES7921000813610123456789");

        Transaction transaction = new Transaction();
        transaction.setAccount(account);
        transaction.setAmount(new BigDecimal(-5));

        when(accountRepository.findById(account.getIban())).thenReturn(Optional.of(account));

        try {
            doAnswer(new Answer<Void>() {
                public Void answer(InvocationOnMock invocation) throws InsufficientFundsException {
                    throw new InsufficientFundsException("no funds");

                }
            }).when(transactionManager).saveTransaction(transaction);
        } catch (InsufficientFundsException e) {
            e.printStackTrace();
        }

        try {
            transactionService.validateAndSave(transaction);
        } catch (InsufficientFundsException e) {
            return;
        }

        fail();
    }

    @Test
    public void invalidIban() throws InsufficientFundsException {
        Account account = new Account();
        account.setIban("ES123");
        assertTrue(account.getBalance().equals(BigDecimal.ZERO));
        Transaction transaction = new Transaction();
        transaction.setAccount(account);
        transaction.setAmount(new BigDecimal(5));

        when(accountRepository.findById(account.getIban())).thenReturn(Optional.of(account));

        doAnswer(new Answer<Void>() {
            public Void answer(InvocationOnMock invocation) {
                Object[] args = invocation.getArguments();
                Transaction transaction1 = (Transaction) args[0];
                transaction1.getAccount().setBalance(
                        transaction1.getAccount().getBalance().add(transaction.getAmount())
                );
                return null;
            }
        }).when(transactionManager).saveTransaction(transaction);

        try {
            transactionService.validateAndSave(transaction);
            fail();
        } catch (InsufficientFundsException e) {
            fail();
        } catch (IbanFormatException e) {
            e.printStackTrace();
        }

    }
}