package tdd.micro.boot.model;

import tdd.micro.boot.enums.Status;

import java.math.BigDecimal;

/**
 * Transaction Response class.
 * @author tiago
 */
public class TransactionResponse extends TransactionReference {

    private Status status;

    private BigDecimal amount;

    private BigDecimal fee;

    public TransactionResponse(String reference, Status status) {
        this.reference = reference;
        this.status = status;
    }

    public TransactionResponse(String reference, Status status, BigDecimal amount, BigDecimal fee) {
        this.reference = reference;
        this.status = status;
        this.amount = amount;
        this.fee = fee;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
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
}


