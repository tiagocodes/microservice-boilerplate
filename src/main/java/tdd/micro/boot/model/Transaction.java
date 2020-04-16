package tdd.micro.boot.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import javax.persistence.*;
import javax.validation.constraints.DecimalMax;
import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.Digits;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * Transaction class.
 * @author tiago
 */
@Entity
@Table(indexes = {@Index(columnList = "reference", name = "idx_reference"),
                  @Index(columnList = "iban", name = "idx_iban")})
public class Transaction implements Serializable {


    public Transaction() {

    }

    public Transaction(BigDecimal amount, BigDecimal fee) {
        this.amount = amount;
        this.fee = fee;
    }

    @Id
    @JsonIgnore
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(unique = true)
    private String reference;

    @JsonProperty("account_iban")
    @ManyToOne
    @JoinColumn(name = "iban")
    private Account account;

    private Date date;

    @NotNull
    @DecimalMin(value = "-5000")
    @DecimalMax(value = "5000")
    @Digits(integer=5, fraction=2)
    private BigDecimal amount;

    private BigDecimal fee = BigDecimal.ZERO;

    private String description;


    public String getReference() {
        return reference;
    }

    public void setReference(String reference) {
        this.reference = reference;
    }

    public Account getAccount() {
        return account;
    }

    public void setAccount(Account account) {
        this.account = account;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public BigDecimal getFee() {
        return fee;
    }

    public void setFee(BigDecimal fee) {
        this.fee = fee;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}