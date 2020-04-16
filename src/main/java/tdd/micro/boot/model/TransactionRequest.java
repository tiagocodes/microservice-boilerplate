package tdd.micro.boot.model;

/**
 * Transaction Request class.
 * @author tiago
 */
public class TransactionRequest extends TransactionReference {

    private String channel;

    public TransactionRequest() {

    }

    public TransactionRequest(String reference, String channel) {
        this.reference = reference;
        this.channel = channel;
    }

    public String getChannel() {
        return channel;
    }

    public void setChannel(String channel) {
        this.channel = channel;
    }
}
