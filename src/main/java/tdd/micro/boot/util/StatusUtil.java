package tdd.micro.boot.util;

import org.springframework.http.ResponseEntity;
import tdd.micro.boot.enums.Channel;
import tdd.micro.boot.enums.Status;
import tdd.micro.boot.model.Transaction;
import tdd.micro.boot.model.TransactionRequest;
import tdd.micro.boot.model.TransactionResponse;

import java.util.Optional;

/**
 * Utility class to generate responses.
 * @author tiago
 */
public class StatusUtil {

    public static ResponseEntity<TransactionResponse> getResponse(Optional<Transaction> result,
                                                    TransactionRequest transactionRequest) {

        if (result.isPresent()) {
            if (DateUtil.isBeforeToday(result.get().getDate())) {
                if (transactionRequest.getChannel().equals(Channel.CLIENT.toString()) ||
                        transactionRequest.getChannel().equals(Channel.ATM.toString())
                ) {
                    return ResponseEntity.ok(
                            new TransactionResponse(transactionRequest.getReference(), Status.SETTLED,
                                    result.get().getAmount().subtract(result.get().getFee()),
                                    result.get().getFee()));

                } else if (transactionRequest.getChannel().equals(Channel.INTERNAL.toString())) {
                    return ResponseEntity.ok(
                            new TransactionResponse(transactionRequest.getReference(), Status.SETTLED,
                                    result.get().getAmount(),
                                    result.get().getFee()));
                }

            } else if (DateUtil.isToday(result.get().getDate())) {
                if (transactionRequest.getChannel().equals(Channel.CLIENT.toString()) ||
                        transactionRequest.getChannel().equals(Channel.ATM.toString())
                ) {
                    return ResponseEntity.ok(
                            new TransactionResponse(transactionRequest.getReference(), Status.PENDING,
                                    result.get().getAmount().subtract(result.get().getFee()),
                                    result.get().getFee()));

                } else if (transactionRequest.getChannel().equals(Channel.INTERNAL.toString())) {
                    return ResponseEntity.ok(
                            new TransactionResponse(transactionRequest.getReference(), Status.PENDING,
                                    result.get().getAmount(),
                                    result.get().getFee()));
                }

            } else if (DateUtil.isAfterToday(result.get().getDate())) {
                if (transactionRequest.getChannel().equals(Channel.CLIENT.toString())) {
                    return ResponseEntity.ok(
                            new TransactionResponse(transactionRequest.getReference(), Status.FUTURE,
                                    result.get().getAmount().subtract(result.get().getFee()),
                                    result.get().getFee()));

                } else if (transactionRequest.getChannel().equals(Channel.ATM.toString())) {
                    return ResponseEntity.ok(
                            new TransactionResponse(transactionRequest.getReference(), Status.PENDING,
                                    result.get().getAmount().subtract(result.get().getFee()),
                                    result.get().getFee()));

                } else if (transactionRequest.getChannel().equals(Channel.INTERNAL.toString())) {
                    return ResponseEntity.ok(
                            new TransactionResponse(transactionRequest.getReference(), Status.FUTURE,
                                    result.get().getAmount(),
                                    result.get().getFee()));

                }

            }
        }

        return ResponseEntity.of(Optional.of(new TransactionResponse(transactionRequest.getReference(), Status.INVALID)));

    }

}
