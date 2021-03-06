package org.springframework.security.oauth.examples.tonr;


import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.springframework.security.authentication.TestingAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.context.SecurityContextImpl;
import org.springframework.security.oauth2.client.DefaultOAuth2ClientContext;
import org.springframework.security.oauth2.client.OAuth2RestTemplate;
import org.springframework.security.oauth2.client.token.AccessTokenRequest;
import org.springframework.security.oauth2.client.token.DefaultAccessTokenRequest;
import org.springframework.security.oauth2.client.token.grant.password.ResourceOwnerPasswordResourceDetails;
import org.springframework.security.oauth2.common.DefaultOAuth2AccessToken;
import org.springframework.security.oauth2.common.OAuth2AccessToken;

import java.util.Arrays;
import java.util.Date;

import static org.junit.Assert.*;

/**
 * @author Dave Syer
 */
public class RefreshTokenGrantTests {

    @Rule
    public ServerRunning serverRunning = ServerRunning.isRunning();

    private OAuth2AccessToken existingToken;

    private ResourceOwnerPasswordResourceDetails resource;

    @Before
    public void setup() {
        resource = new ResourceOwnerPasswordResourceDetails();

        resource.setAccessTokenUri(serverRunning.getUrl("/sparklr2/oauth/token"));
        resource.setClientId("my-trusted-client");
        resource.setId("sparklr");
        resource.setScope(Arrays.asList("trust"));
        resource.setUsername("marissa");
        resource.setPassword("koala");

        OAuth2RestTemplate template = new OAuth2RestTemplate(resource);
        existingToken = template.getAccessToken();
        ((DefaultOAuth2AccessToken) existingToken).setExpiration(new Date(0L));

        SecurityContextImpl securityContext = new SecurityContextImpl();
        securityContext.setAuthentication(new TestingAuthenticationToken("marissa", "koala", "ROLE_USER"));
        SecurityContextHolder.setContext(securityContext);

    }

    @Test
    public void testConnectDirectlyToResourceServer() throws Exception {

        assertNotNull(existingToken.getRefreshToken());
        // It won't be expired on the server, but we can force the client to refresh it
        assertTrue(existingToken.isExpired());

        AccessTokenRequest request = new DefaultAccessTokenRequest();
        request.setExistingToken(existingToken);

        OAuth2RestTemplate template = new OAuth2RestTemplate(resource, new DefaultOAuth2ClientContext(request));
        String result = template.getForObject(serverRunning.getUrl("/sparklr2/photos/user/message"), String.class);
        assertEquals("Hello, Trusted User marissa", result);

        assertFalse("Tokens match so there was no refresh", existingToken.equals(template.getAccessToken()));

    }

}
