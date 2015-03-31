package demo;

import org.junit.Test;
import org.springframework.boot.test.IntegrationTest;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.boot.test.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.annotation.DirtiesContext;
import sparklr.common.AbstractClientCredentialsProviderTests;

import java.util.Map;

import static org.junit.Assert.assertEquals;

/**
 * @author Dave Syer
 */
@SpringApplicationConfiguration(classes = Application.class)
@IntegrationTest({"server.servlet_path:/server", "server.port=0"})
@DirtiesContext
public class ServletPathClientCredentialsProviderTests extends AbstractClientCredentialsProviderTests {

    @Test
    public void testTokenKey() throws Exception {
        @SuppressWarnings("rawtypes")
        ResponseEntity<Map> response = new TestRestTemplate("my-client-with-secret", "secret").getForEntity(
                http.getUrl(tokenKeyPath()), Map.class);
        // This app has no token key.
        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
    }

}
