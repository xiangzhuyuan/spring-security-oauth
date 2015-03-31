package demo;

import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.http.converter.HttpMessageConverter;
import sparklr.common.AbstractRefreshTokenSupportTests;

import java.util.Collection;

/**
 * @author Ryan Heaton
 * @author Dave Syer
 */
@SpringApplicationConfiguration(classes = Application.class)
public class RefreshTokenSupportTests extends AbstractRefreshTokenSupportTests {

    @Override
    protected Collection<? extends HttpMessageConverter<?>> getAdditionalConverters() {
        return Converters.getJaxbConverters();
    }

}
