import org.json.JSONArray;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.restdocs.JUnitRestDocumentation;
import org.springframework.restdocs.RestDocumentationContext;
import org.springframework.restdocs.RestDocumentationContextProvider;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractTransactionalJUnit4SpringContextTests;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.util.NestedServletException;
import tdd.micro.boot.MegaApplication;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import static junit.framework.TestCase.assertTrue;
import static org.junit.Assert.assertNotNull;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.documentationConfiguration;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.webAppContextSetup;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment= SpringBootTest.WebEnvironment.RANDOM_PORT)
@ContextConfiguration(classes = {DataSourceAutoConfiguration.class, HibernateJpaAutoConfiguration.class, MegaApplication.class})
public class MegaTest extends AbstractTransactionalJUnit4SpringContextTests {

    @Rule
    public JUnitRestDocumentation restDocumentation = new JUnitRestDocumentation("target/generated-snippets");

    @Test()
    public void createTransaction() throws Exception {

        Callable<String> callable81 = new Callable<String>() {
            @Override
            public String call() {
                RestTemplate restTemplate = new RestTemplate();
                String url = "http://127.0.0.1:8081/transactions/ATM/add";
                HttpHeaders headers = new HttpHeaders();
                headers.setContentType(MediaType.APPLICATION_JSON);

                String json = "{\"account_iban\" : \"ES7921000813610123456789\", \"amount\" : \"1\"}";
                HttpEntity<String> entity = new HttpEntity<String>(json, headers);

                String answer = restTemplate.postForObject(url, entity, String.class);

                return answer;
            }
        };

        Callable<String> callable82 = new Callable<String>() {
            @Override
            public String call() {
                RestTemplate restTemplate = new RestTemplate();
                String url = "http://127.0.0.1:8082/transactions/ATM/add";
                HttpHeaders headers = new HttpHeaders();
                headers.setContentType(MediaType.APPLICATION_JSON);

                String reference = UUID.randomUUID().toString();
                String json = "{\"reference\" : \"" + reference + "\", \"account_iban\" : \"ES7921000813610123456789\", \"amount\" : \"1\"}";
                HttpEntity<String> entity = new HttpEntity<String>(json, headers);

                String answer = restTemplate.postForObject(url, entity, String.class);

                return answer;
            }
        };

        Callable<String> callable83 = new Callable<String>() {
            @Override
            public String call() {
                RestTemplate restTemplate = new RestTemplate();
                String url = "http://127.0.0.1:8083/transactions/ATM/add";
                HttpHeaders headers = new HttpHeaders();
                headers.setContentType(MediaType.APPLICATION_JSON);

                String json = "{\"account_iban\" : \"ES7921000813610123456789\", \"amount\" : \"1\"}";
                HttpEntity<String> entity = new HttpEntity<String>(json, headers);

                String answer = restTemplate.postForObject(url, entity, String.class);

                return answer;
            }
        };

        ExecutorService executorService = Executors.newFixedThreadPool(100);

        List<Callable<String>> callables = new ArrayList<>();

        for (int i = 0; i < 100; i++) {
            callables.add(callable81);
            callables.add(callable82);
            callables.add(callable83);
        }

        List<Future<String>> futures = executorService.invokeAll(callables);

        for (final Future<String> future : futures) {
            String resultActions = future.get();
            assertNotNull(resultActions);
        }



    }


}
