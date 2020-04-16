package tdd.micro.boot.model;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.Entity;
import javax.persistence.Id;
import java.math.BigDecimal;

/**
 * Account entity.
 * @author tiago
 */
@Entity
public class Account {

    public Account() {

    }

    public Account(String iban) {
        this.iban = iban;
    }

    @Id
    private String iban;

    @JsonIgnore
    private BigDecimal balance = BigDecimal.ZERO;

    public String getIban() {
        return iban;
    }

    public void setIban(String iban) {
        this.iban = iban;
    }

    public BigDecimal getBalance() {
        return balance;
    }

    public void setBalance(BigDecimal balance) {
        this.balance = balance;
    }
}
