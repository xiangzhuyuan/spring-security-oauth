package demo;

import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.http.converter.HttpMessageConverter;
import sparklr.common.AbstractResourceOwnerPasswordProviderTests;

import java.util.Collection;

/**
 * @author Dave Syer
 */
@SpringApplicationConfiguration(classes = Application.class)
public class ResourceOwnerPasswordProviderTests extends AbstractResourceOwnerPasswordProviderTests {

    @Override
    protected Collection<? extends HttpMessageConverter<?>> getAdditionalConverters() {
        return Converters.getJaxbConverters();
    }

}
