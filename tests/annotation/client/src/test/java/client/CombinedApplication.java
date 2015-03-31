/*
 * Copyright 2013-2014 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 */
package client;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.SecurityProperties;
import org.springframework.boot.autoconfigure.security.SecurityProperties.User;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.authentication.configurers.GlobalAuthenticationConfigurerAdapter;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.oauth2.config.annotation.configurers.ClientDetailsServiceConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;
import org.springframework.security.oauth2.config.annotation.web.configuration.ResourceServerConfigurerAdapter;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.*;

/**
 * Combined OAuth2 client and server app for testing. Normally it only makes sense for the client to be a separate app
 * (otherwise it wouldn't need HTTP resources from the server because it could get them on local channels), but for
 * testing we can fake it to make things easier to set up and run. Run this main method and visit http://localhost:8080:
 * <p/>
 * <ul>
 * <li>Client doesn't have a token so redirects to auth server /oauth/authorize</li>
 * <li>Auth server prompts for authentication (username/password=user/password)</li>
 * <li>Auth server prompts for approval of the token grant and redirects to client app</li>
 * <li>Client app obtains token in back channel /oauth/token</li>
 * <li>Client app obtains content from protected resource /admin/beans (hard-coded content for the demo)</li>
 * <li>Client renders content</li>
 * </ul>
 * <p/>
 * In this demo the client app is very basic (it just re-renders content it got from the resource server), but in a real
 * app it can do whatever it likes with the resource content.
 *
 * @author Dave Syer
 */
@Configuration
@RestController
public class CombinedApplication {

    public static void main(String[] args) {
        new SpringApplicationBuilder(ClientApplication.class, CombinedApplication.class).profiles("combined").run(args);
    }

    @RequestMapping("/admin/beans")
    public List<Map<String, Object>> beans() {
        return Arrays.asList(Collections.<String, Object>singletonMap("message", "Hello World"));
    }

    @RequestMapping("/admin/info")
    public Map<String, Object> info() {
        return Collections.<String, Object>emptyMap();
    }

    @Configuration
    @EnableAuthorizationServer
    protected static class AuthorizationServerConfiguration extends AuthorizationServerConfigurerAdapter {

        @Override
        public void configure(ClientDetailsServiceConfigurer clients) throws Exception {
            clients.inMemory().withClient("my-trusted-client").authorizedGrantTypes("authorization_code")
                    .authorities("ROLE_CLIENT").scopes("read", "write").resourceIds("oauth2-resource");

        }

    }

    @Configuration
    @EnableResourceServer
    protected static class ResourceServerConfiguration extends ResourceServerConfigurerAdapter {

        @Override
        public void configure(HttpSecurity http) throws Exception {
            http.antMatcher("/admin/beans").authorizeRequests().anyRequest().authenticated();
        }

    }

    @Configuration
    protected static class AuthenticationConfigurerAdapter extends
            GlobalAuthenticationConfigurerAdapter {

        private static Log logger = LogFactory
                .getLog(AuthenticationConfigurerAdapter.class);

        @Autowired
        private SecurityProperties security;

        @Override
        public void init(AuthenticationManagerBuilder auth) throws Exception {
            User user = this.security.getUser();
            if (user.isDefaultPassword()) {
                logger.info("\n\nUsing default security password: " + user.getPassword()
                        + "\n");
            }

            Set<String> roles = new LinkedHashSet<String>(user.getRole());
            auth.inMemoryAuthentication().withUser(user.getName())
                    .password(user.getPassword())
                    .roles(roles.toArray(new String[roles.size()]));
        }

    }

}
