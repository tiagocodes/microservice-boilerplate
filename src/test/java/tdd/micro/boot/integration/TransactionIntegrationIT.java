package tdd.micro.boot.integration;


import org.json.JSONArray;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractTransactionalJUnit4SpringContextTests;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.util.NestedServletException;
import tdd.micro.boot.BankApplication;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import static junit.framework.TestCase.assertTrue;
import static org.junit.Assert.assertNotNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.webAppContextSetup;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment= SpringBootTest.WebEnvironment.RANDOM_PORT)
@ContextConfiguration(classes = {DataSourceAutoConfiguration.class, HibernateJpaAutoConfiguration.class, BankApplication.class})
public class TransactionIntegrationIT extends AbstractTransactionalJUnit4SpringContextTests {

    @Autowired
    private WebApplicationContext webApplicationContext;

    private MockMvc mockMvc;


    @Before
    public void setUpMockMvc() {
        mockMvc = webAppContextSetup(webApplicationContext).build();
    }

    @PersistenceContext
    EntityManager entityManager;

    @Test
    public void createTransactions() throws Exception {
        ExecutorService executorService = Executors.newFixedThreadPool(100);

        Callable<ResultActions> callable = new Callable<ResultActions>() {
            @Override
            public ResultActions call() {
                ResultActions result = null;
                try {
                    result = mockMvc.perform(post("/transactions/ATM/add")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("{\"account_iban\" : \"ES7921000813610123456789\", \"amount\" : \"1\"}"))
                            .andExpect(status().is2xxSuccessful());
                } catch (Exception e) {
                    e.printStackTrace();
                    return null;
                }
                return result;
            }
        };

        Callable<ResultActions> callableRef = new Callable<ResultActions>() {
            @Override
            public ResultActions call() {
                ResultActions result = null;
                try {
                    result = mockMvc.perform(post("/transactions/ATM/add")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("{\"reference\" : \"ABC123\", \"date\": \"2019-07-16T16:55:42.000Z\", \"account_iban\" : \"ES7921000813610123456789\", \"amount\" : \"5\"}"))
                            .andExpect(status().is2xxSuccessful());
                } catch (Exception e) {
                    e.printStackTrace();
                    return null;
                }
                return result;
            }
        };

        List<Callable<ResultActions>> callables = new ArrayList<>();
        for (int i = 0; i < 100; i++) {
            callables.add(callable);
        }
        callables.add(callableRef);

        List<Future<ResultActions>> futures = executorService.invokeAll(callables);

        for (final Future<ResultActions> future : futures) {
            ResultActions resultActions = future.get();
            assertNotNull(resultActions);
        }

        MvcResult resultGet = mockMvc.perform(get("/transactions/get")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"reference\" : \"ABC123\", \"channel\" : \"ATM\"}"))
                .andExpect(status().is2xxSuccessful()).andReturn();

        String response = resultGet.getResponse().getContentAsString();
        assertTrue(response.contains("\"reference\":\"ABC123\""));
        assertTrue(response.contains("\"amount\":5"));

        MvcResult resultAll = mockMvc.perform(get("/transactions/ATM/search/ES7921000813610123456789?sort=DESC")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is2xxSuccessful()).andReturn();

        String transactions = resultAll.getResponse().getContentAsString();
        JSONArray jsonArray = new JSONArray(transactions);

        assertTrue(jsonArray.length() == 101);

        Query q = entityManager.createNativeQuery("select * from account");
        List<Object[]> accounts = q.getResultList();
        Object[] o = accounts.get(0);
        BigDecimal balance = (BigDecimal) o[1];
        assertTrue(balance.intValue() == 105);

    }

    @Test()
    public void createTransaction() throws Exception {
        mockMvc.perform(post("/transactions/ATM/add")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"account_iban\" : \"ES7921000813610123456789\", \"amount\" : \"2\"}"))
                .andExpect(status().is2xxSuccessful());

    }

    @Test(expected = NestedServletException.class)
    public void createTransactionInvalidIban() throws Exception {
        mockMvc.perform(post("/transactions/ATM/add")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"account_iban\" : \"ES123\", \"amount\" : \"2\"}"))
                .andExpect(status().is5xxServerError());

    }

}
