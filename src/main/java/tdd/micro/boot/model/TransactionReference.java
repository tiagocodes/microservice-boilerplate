package tdd.micro.boot.model;

/**
 * Class to be extended.
 * @author tiago
 */
public abstract class TransactionReference {

    String reference;

    public String getReference() {
        return reference;
    }

    public void setReference(String reference) {
        this.reference = reference;
    }
}
