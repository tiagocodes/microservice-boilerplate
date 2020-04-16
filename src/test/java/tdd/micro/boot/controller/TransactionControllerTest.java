package tdd.micro.boot.controller;

import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import tdd.micro.boot.enums.Sort;
import tdd.micro.boot.model.Account;
import tdd.micro.boot.model.Transaction;
import tdd.micro.boot.service.QueryService;
import tdd.micro.boot.service.TransactionService;

import java.math.BigDecimal;
import java.util.*;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.standaloneSetup;

public class TransactionControllerTest {

    @InjectMocks
    private TransactionController transactionController;

    @Mock
    private TransactionService transactionService;

    @Mock
    private QueryService queryService;

    private MockMvc mockMvc;

    @Rule
    public MockitoRule mockitoRule = MockitoJUnit.rule();

    @Before
    public void setUpMockMvc() {
        mockMvc = standaloneSetup(transactionController).build();
    }

    @Test
    public void createTransaction() throws Exception {
        mockMvc.perform(post("/transactions/ATM/add")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"dummy - service is mock\" : \"abc\"}"))
                .andExpect(status().is2xxSuccessful());
    }

    @Test
    public void searchInexistingIban() throws Exception {

        when(queryService.find("ES1234", Sort.ASC))
                .thenReturn(ResponseEntity.ok(new ArrayList<>()));

        MvcResult result = mockMvc.perform(get("/transactions/ATM/search/ES1234?sort=ASC")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is2xxSuccessful()).andReturn();
        assertTrue(result.getResponse().getContentAsString().equals("[]"));
    }

    @Test
    public void searchExistingIbanAscending() throws Exception {
        Transaction transaction1 = new Transaction(new BigDecimal(5), new BigDecimal(1));
        transaction1.setAccount(new Account("ES123"));
        Transaction transaction2 = new Transaction(new BigDecimal(10), new BigDecimal(4));
        transaction2.setAccount(new Account("ES123"));

        List<Transaction> transactions = new ArrayList<>();
        transactions.add(transaction1);
        transactions.add(transaction2);

        when(queryService.find("ES123", Sort.ASC))
                .thenReturn(ResponseEntity.ok(transactions));

        MvcResult result = mockMvc.perform(get("/transactions/ATM/search/ES123?sort=ASC")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is2xxSuccessful()).andReturn();

        JSONArray ja = new JSONArray(result.getResponse().getContentAsString());

        assertTrue(ja.length() == 2);

        assertTrue((Integer) ((JSONObject) ja.get(0)).get("amount") == 5);
    }

    @Test
    public void searchExistingIbanDescending() throws Exception {
        Transaction transaction1 = new Transaction(new BigDecimal(10), new BigDecimal(1));
        transaction1.setAccount(new Account("ES123"));
        Transaction transaction2 = new Transaction(new BigDecimal(5), new BigDecimal(4));
        transaction2.setAccount(new Account("ES123"));

        List<Transaction> transactions = new ArrayList<>();
        transactions.add(transaction1);
        transactions.add(transaction2);

        when(queryService.find("ES123", Sort.DESC))
                .thenReturn(ResponseEntity.ok(transactions));

        MvcResult result = mockMvc.perform(get("/transactions/ATM/search/ES123?sort=DESC")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is2xxSuccessful()).andReturn();

        JSONArray ja = new JSONArray(result.getResponse().getContentAsString());

        assertTrue(ja.length() == 2);

        assertTrue((Integer) ((JSONObject) ja.get(0)).get("amount") == 10);
    }

    @Test
    public void A_getInexistingTransaction() throws Exception {
        // Given: A transaction that is not stored in our system
        String request = "{\"reference\" : 99, \"channel\" : \"ATM\"}";

        // When: I check the status from any channel
        MvcResult result = mockMvc.perform(get("/transactions/get")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request))
                        .andExpect(status().is2xxSuccessful())
                        .andReturn();

        // Then: The system returns the status 'INVALID'
        assertTrue(result.getResponse().getContentAsString().contains("INVALID"));

    }

    @Test
    public void B_getBeforeTodayFromCLIENT() throws Exception {

        // Given: A transaction that is stored in our system
        Transaction transaction = new Transaction(new BigDecimal(5), new BigDecimal(1));
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        calendar.add(Calendar.DATE, -1);
        transaction.setDate(calendar.getTime());

        when(queryService.get("ABC123"))
                .thenReturn(Optional.of(transaction));

        // When: I check the status from CLIENT or ATM channel
        String request = "{\"reference\" : \"ABC123\", \"channel\" : \"CLIENT\"}";
        MvcResult result = mockMvc.perform(get("/transactions/get")
                .contentType(MediaType.APPLICATION_JSON)
                .content(request))
                .andExpect(status().is2xxSuccessful())
                .andReturn();

        // And the transaction date is before today
        /* day discounted above */

        // Then: The system returns the status 'SETTLED'
        assertTrue(result.getResponse().getContentAsString().contains("SETTLED"));

        // And the amount substracting the fee
        assertTrue(result.getResponse().getContentAsString().contains("\"amount\":4,"));
        assertTrue(result.getResponse().getContentAsString().contains("\"fee\":1"));

    }

    @Test
    public void B_getBeforeTodayFromATM() throws Exception {

        // Given: A transaction that is stored in our system
        Transaction transaction = new Transaction(new BigDecimal(5), new BigDecimal(1));
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        calendar.add(Calendar.DATE, -1);
        transaction.setDate(calendar.getTime());

        when(queryService.get("ABC123"))
                .thenReturn(Optional.of(transaction));

        // When: I check the status from CLIENT or ATM channel
        String request = "{\"reference\" : \"ABC123\", \"channel\" : \"ATM\"}";
        MvcResult result = mockMvc.perform(get("/transactions/get")
                .contentType(MediaType.APPLICATION_JSON)
                .content(request))
                .andExpect(status().is2xxSuccessful())
                .andReturn();

        // And the transaction date is before today
        /* day discounted above */

        // Then: The system returns the status 'SETTLED'
        assertTrue(result.getResponse().getContentAsString().contains("SETTLED"));

        // And the amount substracting the fee
        assertTrue(result.getResponse().getContentAsString().contains("\"amount\":4,"));
        assertTrue(result.getResponse().getContentAsString().contains("\"fee\":1"));

    }

    @Test
    public void C_getBeforeTodayFromINTERNAL() throws Exception {

        // Given: A transaction that is stored in our system
        Transaction transaction = new Transaction(new BigDecimal(5), new BigDecimal(1));
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        calendar.add(Calendar.DATE, -1);
        transaction.setDate(calendar.getTime());

        when(queryService.get("ABC123"))
                .thenReturn(Optional.of(transaction));

        // When: I check the status from INTERNAL channel
        String request = "{\"reference\" : \"ABC123\", \"channel\" : \"INTERNAL\"}";
        MvcResult result = mockMvc.perform(get("/transactions/get")
                .contentType(MediaType.APPLICATION_JSON)
                .content(request))
                .andExpect(status().is2xxSuccessful())
                .andReturn();

        // And the transaction date is before today
        /* day discounted above */

        // Then: The system returns the status 'SETTLED'
        assertTrue(result.getResponse().getContentAsString().contains("SETTLED"));

        // And the amount
        assertTrue(result.getResponse().getContentAsString().contains("\"amount\":5,"));
        // And the fee
        assertTrue(result.getResponse().getContentAsString().contains("\"fee\":1"));

    }

    @Test
    public void D_getTodayFromCLIENT() throws Exception {

        // Given: A transaction that is stored in our system
        Transaction transaction = new Transaction(new BigDecimal(5), new BigDecimal(1));
        transaction.setDate(new Date());

        when(queryService.get("ABC123"))
                .thenReturn(Optional.of(transaction));

        // When: I check the status from CLIENT or ATM channel
        String request = "{\"reference\" : \"ABC123\", \"channel\" : \"CLIENT\"}";
        MvcResult result = mockMvc.perform(get("/transactions/get")
                .contentType(MediaType.APPLICATION_JSON)
                .content(request))
                .andExpect(status().is2xxSuccessful())
                .andReturn();

        // And the transaction date is today
        /* day set above */

        // Then: The system returns the status 'PENDING'
        assertTrue(result.getResponse().getContentAsString().contains("PENDING"));

        // And the amount substracting the fee
        assertTrue(result.getResponse().getContentAsString().contains("\"amount\":4,"));
        assertTrue(result.getResponse().getContentAsString().contains("\"fee\":1"));

    }

    @Test
    public void D_getTodayFromATM() throws Exception {

        // Given: A transaction that is stored in our system
        Transaction transaction = new Transaction(new BigDecimal(5), new BigDecimal(1));
        transaction.setDate(new Date());

        when(queryService.get("ABC123"))
                .thenReturn(Optional.of(transaction));

        // When: I check the status from ATM channel
        String request = "{\"reference\" : \"ABC123\", \"channel\" : \"ATM\"}";
        MvcResult result = mockMvc.perform(get("/transactions/get")
                .contentType(MediaType.APPLICATION_JSON)
                .content(request))
                .andExpect(status().is2xxSuccessful())
                .andReturn();

        // And the transaction date is today
        /* day set above */

        // Then: The system returns the status 'PENDING'
        assertTrue(result.getResponse().getContentAsString().contains("PENDING"));

        // And the amount substracting the fee
        assertTrue(result.getResponse().getContentAsString().contains("\"amount\":4,"));
        assertTrue(result.getResponse().getContentAsString().contains("\"fee\":1"));

    }

    @Test
    public void E_getTodayFromINTERNAL() throws Exception {

        // Given: A transaction that is stored in our system
        Transaction transaction = new Transaction(new BigDecimal(5), new BigDecimal(1));
        transaction.setDate(new Date());

        when(queryService.get("ABC123"))
                .thenReturn(Optional.of(transaction));

        // When: I check the status from INTERNAL channel
        String request = "{\"reference\" : \"ABC123\", \"channel\" : \"INTERNAL\"}";
        MvcResult result = mockMvc.perform(get("/transactions/get")
                .contentType(MediaType.APPLICATION_JSON)
                .content(request))
                .andExpect(status().is2xxSuccessful())
                .andReturn();

        // And the transaction date is today
        /* day set above */

        // Then: The system returns the status 'PENDING'
        assertTrue(result.getResponse().getContentAsString().contains("PENDING"));

        // And the amount
        assertTrue(result.getResponse().getContentAsString().contains("\"amount\":5,"));
        // And the fee
        assertTrue(result.getResponse().getContentAsString().contains("\"fee\":1"));

    }

    @Test
    public void F_getAfterTodayFromCLIENT() throws Exception {

        // Given: A transaction that is stored in our system
        Transaction transaction = new Transaction(new BigDecimal(5), new BigDecimal(1));
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        calendar.add(Calendar.DATE, +1);
        transaction.setDate(calendar.getTime());

        when(queryService.get("ABC123"))
                .thenReturn(Optional.of(transaction));

        // When: I check the status from CLIENT channel
        String request = "{\"reference\" : \"ABC123\", \"channel\" : \"CLIENT\"}";
        MvcResult result = mockMvc.perform(get("/transactions/get")
                .contentType(MediaType.APPLICATION_JSON)
                .content(request))
                .andExpect(status().is2xxSuccessful())
                .andReturn();

        // And the transaction date is after today
        /* day added above */

        // Then: The system returns the status 'FUTURE'
        assertTrue(result.getResponse().getContentAsString().contains("FUTURE"));

        // And the amount substracting the fee
        assertTrue(result.getResponse().getContentAsString().contains("\"amount\":4,"));
        assertTrue(result.getResponse().getContentAsString().contains("\"fee\":1"));

    }

    @Test
    public void G_getAfterTodayFromATM() throws Exception {

        // Given: A transaction that is stored in our system
        Transaction transaction = new Transaction(new BigDecimal(5), new BigDecimal(1));
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        calendar.add(Calendar.DATE, +1);
        transaction.setDate(calendar.getTime());

        when(queryService.get("ABC123"))
                .thenReturn(Optional.of(transaction));

        // When: I check the status from ATM channel
        String request = "{\"reference\" : \"ABC123\", \"channel\" : \"ATM\"}";
        MvcResult result = mockMvc.perform(get("/transactions/get")
                .contentType(MediaType.APPLICATION_JSON)
                .content(request))
                .andExpect(status().is2xxSuccessful())
                .andReturn();

        // And the transaction date is after today
        /* day added above */

        // Then: The system returns the status 'PENDING'
        assertTrue(result.getResponse().getContentAsString().contains("PENDING"));

        // And the amount substracting the fee
        assertTrue(result.getResponse().getContentAsString().contains("\"amount\":4,"));
        assertTrue(result.getResponse().getContentAsString().contains("\"fee\":1"));

    }

    @Test
    public void H_getAfterTodayFromINTERNAL() throws Exception {

        // Given: A transaction that is stored in our system
        Transaction transaction = new Transaction(new BigDecimal(5), new BigDecimal(1));
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        calendar.add(Calendar.DATE, +1);
        transaction.setDate(calendar.getTime());

        when(queryService.get("ABC123"))
                .thenReturn(Optional.of(transaction));

        // When: I check the status from INTERNAL channel
        String request = "{\"reference\" : \"ABC123\", \"channel\" : \"INTERNAL\"}";
        MvcResult result = mockMvc.perform(get("/transactions/get")
                .contentType(MediaType.APPLICATION_JSON)
                .content(request))
                .andExpect(status().is2xxSuccessful())
                .andReturn();

        // And the transaction date is after today
        /* day added above */

        // Then: The system returns the status 'FUTURE'
        assertTrue(result.getResponse().getContentAsString().contains("FUTURE"));

        // And the amount
        assertTrue(result.getResponse().getContentAsString().contains("\"amount\":5,"));
        // And the feee
        assertTrue(result.getResponse().getContentAsString().contains("\"fee\":1"));

    }

}
