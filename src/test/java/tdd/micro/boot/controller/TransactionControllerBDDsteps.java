package tdd.micro.boot.controller;

import io.cucumber.java.Before;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import tdd.micro.boot.model.Transaction;
import tdd.micro.boot.service.QueryService;
import tdd.micro.boot.service.TransactionService;

import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.util.Calendar;
import java.util.Date;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.standaloneSetup;

public class TransactionControllerBDDsteps {

    @InjectMocks
    private TransactionController transactionController;

    @Mock
    private TransactionService transactionService;

    @Mock
    private QueryService queryService;

    @Before
    public void setUpMockMvc() {
        MockitoAnnotations.initMocks(this);
        mockMvc = standaloneSetup(transactionController).build();
    }

    MockMvc mockMvc;
    String request;
    MvcResult result;
    Transaction transaction;

    @Given("^A transaction that is not stored in our system$")
    public void transaction_that_is_not_stored_in_our_system() throws Exception {
        request = "{\"reference\" : 99, \"channel\" : \"ATM\"}";
    }

    @When("^I check the status from any channel$")
    public void I_check_the_status_from_any_channel() throws Exception {
        result = mockMvc.perform(get("/transactions/get")
                .contentType(MediaType.APPLICATION_JSON)
                .content(request))
                .andExpect(status().is2xxSuccessful())
                .andReturn();
    }

    @Then("The system returns the status {string}")
    public void The_system_returns_the_status_INVALID(String status) throws Exception {
        assertTrue(result.getResponse().getContentAsString().contains(status));

    }





    @Given("A transaction that is stored in our system")
    public void a_transaction_that_is_stored_in_our_system() {
        transaction = new Transaction(new BigDecimal(5), new BigDecimal(1));
        when(queryService.get("ABC123"))
                .thenReturn(Optional.of(transaction));
    }

    @When("I check the status from CLIENT channel")
    public void i_check_the_status_from_CLIENT__channel() throws Exception {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        calendar.add(Calendar.DATE, -1);
        transaction.setDate(calendar.getTime());
        request = "{\"reference\" : \"ABC123\", \"channel\" : \"CLIENT\"}";
        result = mockMvc.perform(get("/transactions/get")
                .contentType(MediaType.APPLICATION_JSON)
                .content(request))
                .andExpect(status().is2xxSuccessful())
                .andReturn();
    }

    @When("I check the status from ATM channel")
    public void i_check_the_status_from_ATM_channel() throws Exception {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        calendar.add(Calendar.DATE, -1);
        transaction.setDate(calendar.getTime());
        request = "{\"reference\" : \"ABC123\", \"channel\" : \"ATM\"}";
        result = mockMvc.perform(get("/transactions/get")
                .contentType(MediaType.APPLICATION_JSON)
                .content(request))
                .andExpect(status().is2xxSuccessful())
                .andReturn();
    }

    @When("^the transaction date is before today$")
    public void the_transaction_date_is_before_today() {
    }


    @Then("the amount substracting the fee")
    public void the_amount_substracting_the_fee() throws UnsupportedEncodingException {
        assertTrue(result.getResponse().getContentAsString().contains("\"amount\":4,"));
        assertTrue(result.getResponse().getContentAsString().contains("\"fee\":1"));
    }


}
