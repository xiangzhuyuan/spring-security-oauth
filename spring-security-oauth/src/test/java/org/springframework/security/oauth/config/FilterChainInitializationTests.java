package org.springframework.security.oauth.config;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.oauth.common.signature.SharedConsumerSecret;
import org.springframework.security.oauth.provider.ConsumerDetails;
import org.springframework.security.oauth.provider.ConsumerDetailsService;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

@ContextConfiguration
@RunWith(SpringJUnit4ClassRunner.class)
public class FilterChainInitializationTests {

    @Autowired
    private ConsumerDetailsService clientDetailsService;

    @Test
    public void testClientDetailsFromPropertyFile() {
        ConsumerDetails consumer = clientDetailsService.loadConsumerByConsumerKey("my-client-key");
        assertNotNull(consumer);
        assertEquals("my-client-secret", ((SharedConsumerSecret) consumer.getSignatureSecret()).getConsumerSecret());
    }

}
