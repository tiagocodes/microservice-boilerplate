package tdd.micro.boot.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.query.Param;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tdd.micro.boot.enums.Sort;
import tdd.micro.boot.exception.InsufficientFundsException;
import tdd.micro.boot.model.Transaction;
import tdd.micro.boot.model.TransactionRequest;
import tdd.micro.boot.model.TransactionResponse;
import tdd.micro.boot.service.QueryService;
import tdd.micro.boot.service.TransactionService;
import tdd.micro.boot.util.StatusUtil;

import java.util.List;
import java.util.Optional;

/**
 * Controller class to receive Http requests.
 * @author tiago
 */
@RestController
@RequestMapping(value = "/transactions")
public class TransactionController {

    @Autowired
    private TransactionService transactionService;

    @Autowired
    private QueryService queryService;

    /**
     * Create transactions endpoint.
     * @param transaction
     * @param channel
     * @return
     * @throws InsufficientFundsException
     */
    @PostMapping(path = "/{channel}/add")
    public ResponseEntity<Transaction> add(@RequestBody Transaction transaction,
                                           @PathVariable @Param(value = "channel") String channel)
                                                        throws InsufficientFundsException {
        Transaction result = transactionService.validateAndSave(transaction);

        return ResponseEntity.ok(result);
    }

    /**
     * Search transactions endpoint.
     * @param channel
     * @param iban
     * @param sort
     * @return
     */
    @GetMapping(path = "/{channel}/search/{iban}")
    public ResponseEntity<List<Transaction>> search(@PathVariable @Param(value = "channel") String channel,
                                                    @PathVariable @Param(value = "iban") String iban,
                                                    @RequestParam @Param(value = "sort") Sort sort) {

        return queryService.find(iban, sort);
    }

    /**
     * Endpoint to retreive transaction by reference.
     * @param transactionRequest
     * @return
     */
    @GetMapping(path = "/get")
    public ResponseEntity<TransactionResponse> get(
                                    @RequestBody TransactionRequest transactionRequest) {

        Optional<Transaction> result = queryService.get(transactionRequest.getReference());

        return StatusUtil.getResponse(result, transactionRequest);
    }

}
