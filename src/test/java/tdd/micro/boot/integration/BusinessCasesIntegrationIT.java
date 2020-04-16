package tdd.micro.boot.integration;

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
import org.springframework.web.context.WebApplicationContext;
import tdd.micro.boot.BankApplication;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.webAppContextSetup;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment= SpringBootTest.WebEnvironment.RANDOM_PORT)
@ContextConfiguration(classes = {DataSourceAutoConfiguration.class, HibernateJpaAutoConfiguration.class, BankApplication.class})
public class BusinessCasesIntegrationIT extends AbstractTransactionalJUnit4SpringContextTests {

    @Autowired
    private WebApplicationContext webApplicationContext;

    private MockMvc mockMvc;

    @Before
    public void setUpMockMvc() {
        mockMvc = webAppContextSetup(webApplicationContext).build();
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

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        calendar.add(Calendar.DATE, -1);

        // Given: A transaction that is stored in our system
        MvcResult result = mockMvc.perform(post("/transactions/CLIENT/add")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"reference\" : \"ABC1234\", \"date\": \""
                        + new SimpleDateFormat("yyyy-MM-dd").format(calendar.getTime())
                        + "T16:55:42.000Z\", \"account_iban\" : \"ES7921000813610123456789\", " +
                        "\"amount\" : 5, \"fee\" : 1}"))
                .andExpect(status().is2xxSuccessful()).andReturn();

        // When: I check the status from CLIENT or ATM channel
        String request = "{\"reference\" : \"ABC1234\", \"channel\" : \"CLIENT\"}";
        MvcResult mvcResult = mockMvc.perform(get("/transactions/get")
                .contentType(MediaType.APPLICATION_JSON)
                .content(request))
                .andExpect(status().is2xxSuccessful())
                .andReturn();

        // And the transaction date is before today
        /* day discounted above */

        // Then: The system returns the status 'SETTLED'
        assertTrue(mvcResult.getResponse().getContentAsString().contains("SETTLED"));

        // And the amount substracting the fee
        assertTrue(mvcResult.getResponse().getContentAsString().contains("\"amount\":4,"));
        assertTrue(mvcResult.getResponse().getContentAsString().contains("\"fee\":1"));

    }

    @Test
    public void B_getBeforeTodayFromATM() throws Exception {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        calendar.add(Calendar.DATE, -1);

        // Given: A transaction that is stored in our system
        MvcResult result = mockMvc.perform(post("/transactions/ATM/add")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"reference\" : \"ABC1235\", \"date\": \""
                        + new SimpleDateFormat("yyyy-MM-dd").format(calendar.getTime())
                        + "T16:55:42.000Z\", \"account_iban\" : \"ES7921000813610123456789\", " +
                        "\"amount\" : 5, \"fee\" : 1}"))
                .andExpect(status().is2xxSuccessful()).andReturn();

        // When: I check the status from CLIENT or ATM channel
        String request = "{\"reference\" : \"ABC1235\", \"channel\" : \"ATM\"}";
        MvcResult mvcResult = mockMvc.perform(get("/transactions/get")
                .contentType(MediaType.APPLICATION_JSON)
                .content(request))
                .andExpect(status().is2xxSuccessful())
                .andReturn();

        // And the transaction date is before today
        /* day discounted above */

        // Then: The system returns the status 'SETTLED'
        assertTrue(mvcResult.getResponse().getContentAsString().contains("SETTLED"));

        // And the amount substracting the fee
        assertTrue(mvcResult.getResponse().getContentAsString().contains("\"amount\":4,"));
        assertTrue(mvcResult.getResponse().getContentAsString().contains("\"fee\":1"));

    }

    @Test
    public void C_getBeforeTodayFromINTERNAL() throws Exception {

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        calendar.add(Calendar.DATE, -1);

        // Given: A transaction that is stored in our system
        MvcResult result = mockMvc.perform(post("/transactions/INTERNAL/add")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"reference\" : \"ABC1236\", \"date\": \""
                        + new SimpleDateFormat("yyyy-MM-dd").format(calendar.getTime())
                        + "T16:55:42.000Z\", \"account_iban\" : \"ES7921000813610123456789\", " +
                        "\"amount\" : 5, \"fee\" : 1}"))
                .andExpect(status().is2xxSuccessful()).andReturn();

        // When: I check the status from INTERNAL channel
        String request = "{\"reference\" : \"ABC1236\", \"channel\" : \"INTERNAL\"}";
        MvcResult mvcResult = mockMvc.perform(get("/transactions/get")
                .contentType(MediaType.APPLICATION_JSON)
                .content(request))
                .andExpect(status().is2xxSuccessful())
                .andReturn();

        // And the transaction date is before today
        /* day discounted above */

        // Then: The system returns the status 'SETTLED'
        assertTrue(mvcResult.getResponse().getContentAsString().contains("SETTLED"));

        // And the amount
        assertTrue(mvcResult.getResponse().getContentAsString().contains("\"amount\":5,"));
        // And the fee
        assertTrue(mvcResult.getResponse().getContentAsString().contains("\"fee\":1"));

    }

    @Test
    public void D_getTodayFromCLIENT() throws Exception {

        // Given: A transaction that is stored in our system
        MvcResult result = mockMvc.perform(post("/transactions/CLIENT/add")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"reference\" : \"ABC1237\", \"date\": \""
                        + new SimpleDateFormat("yyyy-MM-dd").format(new Date())
                        + "T16:55:42.000Z\", \"account_iban\" : \"ES7921000813610123456789\", " +
                        "\"amount\" : 5, \"fee\" : 1}"))
                .andExpect(status().is2xxSuccessful()).andReturn();


        // When: I check the status from CLIENT or ATM channel
        String request = "{\"reference\" : \"ABC1237\", \"channel\" : \"CLIENT\"}";
        MvcResult mvcResult = mockMvc.perform(get("/transactions/get")
                .contentType(MediaType.APPLICATION_JSON)
                .content(request))
                .andExpect(status().is2xxSuccessful())
                .andReturn();

        // And the transaction date is today
        /* day set above */

        // Then: The system returns the status 'PENDING'
        assertTrue(mvcResult.getResponse().getContentAsString().contains("PENDING"));

        // And the amount substracting the fee
        assertTrue(mvcResult.getResponse().getContentAsString().contains("\"amount\":4,"));
        assertTrue(mvcResult.getResponse().getContentAsString().contains("\"fee\":1"));

    }

    @Test
    public void D_getTodayFromATM() throws Exception {

        // Given: A transaction that is stored in our system
        MvcResult result = mockMvc.perform(post("/transactions/ATM/add")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"reference\" : \"ABC1238\", \"date\": \""
                        + new SimpleDateFormat("yyyy-MM-dd").format(new Date())
                        + "T16:55:42.000Z\", \"account_iban\" : \"ES7921000813610123456789\", " +
                        "\"amount\" : 5, \"fee\" : 1}"))
                .andExpect(status().is2xxSuccessful()).andReturn();


        // When: I check the status from ATM channel
        String request = "{\"reference\" : \"ABC1238\", \"channel\" : \"ATM\"}";
        MvcResult mvcResult = mockMvc.perform(get("/transactions/get")
                .contentType(MediaType.APPLICATION_JSON)
                .content(request))
                .andExpect(status().is2xxSuccessful())
                .andReturn();

        // And the transaction date is today
        /* day set above */

        // Then: The system returns the status 'PENDING'
        assertTrue(mvcResult.getResponse().getContentAsString().contains("PENDING"));

        // And the amount substracting the fee
        assertTrue(mvcResult.getResponse().getContentAsString().contains("\"amount\":4,"));
        assertTrue(mvcResult.getResponse().getContentAsString().contains("\"fee\":1"));

    }

    @Test
    public void E_getTodayFromINTERNAL() throws Exception {

        // Given: A transaction that is stored in our system
        MvcResult result = mockMvc.perform(post("/transactions/INTERNAL/add")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"reference\" : \"ABC1239\", \"date\": \""
                        + new SimpleDateFormat("yyyy-MM-dd").format(new Date())
                        + "T16:55:42.000Z\", \"account_iban\" : \"ES7921000813610123456789\", " +
                        "\"amount\" : 5, \"fee\" : 1}"))
                .andExpect(status().is2xxSuccessful()).andReturn();

        // When: I check the status from INTERNAL channel
        String request = "{\"reference\" : \"ABC1239\", \"channel\" : \"INTERNAL\"}";
        MvcResult mvcResult = mockMvc.perform(get("/transactions/get")
                .contentType(MediaType.APPLICATION_JSON)
                .content(request))
                .andExpect(status().is2xxSuccessful())
                .andReturn();

        // And the transaction date is today
        /* day set above */

        // Then: The system returns the status 'PENDING'
        assertTrue(mvcResult.getResponse().getContentAsString().contains("PENDING"));

        // And the amount
        assertTrue(mvcResult.getResponse().getContentAsString().contains("\"amount\":5,"));
        // And the fee
        assertTrue(mvcResult.getResponse().getContentAsString().contains("\"fee\":1"));

    }

    @Test
    public void F_getAfterTodayFromCLIENT() throws Exception {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        calendar.add(Calendar.DATE, +1);

        // Given: A transaction that is stored in our system
        MvcResult result = mockMvc.perform(post("/transactions/CLIENT/add")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"reference\" : \"ABC12310\", \"date\": \""
                        + new SimpleDateFormat("yyyy-MM-dd").format(calendar.getTime())
                        + "T16:55:42.000Z\", \"account_iban\" : \"ES7921000813610123456789\", " +
                        "\"amount\" : 5, \"fee\" : 1}"))
                .andExpect(status().is2xxSuccessful()).andReturn();


        // When: I check the status from CLIENT channel
        String request = "{\"reference\" : \"ABC12310\", \"channel\" : \"CLIENT\"}";
        MvcResult mvcResult = mockMvc.perform(get("/transactions/get")
                .contentType(MediaType.APPLICATION_JSON)
                .content(request))
                .andExpect(status().is2xxSuccessful())
                .andReturn();

        // And the transaction date is after today
        /* day added above */

        // Then: The system returns the status 'FUTURE'
        assertTrue(mvcResult.getResponse().getContentAsString().contains("FUTURE"));

        // And the amount substracting the fee
        assertTrue(mvcResult.getResponse().getContentAsString().contains("\"amount\":4,"));
        assertTrue(mvcResult.getResponse().getContentAsString().contains("\"fee\":1"));

    }

    @Test
    public void G_getAfterTodayFromATM() throws Exception {

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        calendar.add(Calendar.DATE, +1);

        // Given: A transaction that is stored in our system
        MvcResult result = mockMvc.perform(post("/transactions/ATM/add")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"reference\" : \"ABC12311\", \"date\": \""
                        + new SimpleDateFormat("yyyy-MM-dd").format(calendar.getTime())
                        + "T16:55:42.000Z\", \"account_iban\" : \"ES7921000813610123456789\", " +
                        "\"amount\" : 5, \"fee\" : 1}"))
                .andExpect(status().is2xxSuccessful()).andReturn();

        // When: I check the status from ATM channel
        String request = "{\"reference\" : \"ABC12311\", \"channel\" : \"ATM\"}";
        MvcResult mvcResult = mockMvc.perform(get("/transactions/get")
                .contentType(MediaType.APPLICATION_JSON)
                .content(request))
                .andExpect(status().is2xxSuccessful())
                .andReturn();

        // And the transaction date is after today
        /* day added above */

        // Then: The system returns the status 'PENDING'
        assertTrue(mvcResult.getResponse().getContentAsString().contains("PENDING"));

        // And the amount substracting the fee
        assertTrue(mvcResult.getResponse().getContentAsString().contains("\"amount\":4,"));
        assertTrue(mvcResult.getResponse().getContentAsString().contains("\"fee\":1"));

    }

    @Test
    public void H_getAfterTodayFromINTERNAL() throws Exception {

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        calendar.add(Calendar.DATE, +1);

        // Given: A transaction that is stored in our system
        MvcResult result = mockMvc.perform(post("/transactions/INTERNAL/add")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"reference\" : \"ABC12312\", \"date\": \""
                        + new SimpleDateFormat("yyyy-MM-dd").format(calendar.getTime())
                        + "T16:55:42.000Z\", \"account_iban\" : \"ES7921000813610123456789\", " +
                        "\"amount\" : 5, \"fee\" : 1}"))
                .andExpect(status().is2xxSuccessful()).andReturn();


        // When: I check the status from INTERNAL channel
        String request = "{\"reference\" : \"ABC12312\", \"channel\" : \"INTERNAL\"}";
        MvcResult mvcResult = mockMvc.perform(get("/transactions/get")
                .contentType(MediaType.APPLICATION_JSON)
                .content(request))
                .andExpect(status().is2xxSuccessful())
                .andReturn();

        // And the transaction date is after today
        /* day added above */

        // Then: The system returns the status 'FUTURE'
        assertTrue(mvcResult.getResponse().getContentAsString().contains("FUTURE"));

        // And the amount
        assertTrue(mvcResult.getResponse().getContentAsString().contains("\"amount\":5,"));
        // And the feee
        assertTrue(mvcResult.getResponse().getContentAsString().contains("\"fee\":1"));

    }

}
