package tdd.micro.boot.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import tdd.micro.boot.model.Account;
import tdd.micro.boot.model.Transaction;

import java.util.List;

/**
 * Transaction Repository.
 * @author tiago
 */
@Repository
public interface TransactionRepository extends JpaRepository<Transaction, String> {

    Transaction findByReference(String reference);

    List<Transaction> findAllByAccountOrderByAmount(Account account);

    List<Transaction> findAllByAccountOrderByAmountDesc(Account account);

}
