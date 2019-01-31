package com.bca.api.bca;

import java.util.HashMap;
import java.util.Map;

import javax.ws.rs.client.Client;

import org.glassfish.jersey.client.ClientProperties;

import com.bca.api.bca.client.BCAClient;
import com.bca.api.bca.resources.BCAService;
import com.bca.api.bca.payload.BalanceInformation;
import com.bca.api.bca.resources.BCAService;

import io.dropwizard.Application;
import io.dropwizard.client.JerseyClientBuilder;
import io.dropwizard.client.JerseyClientConfiguration;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;

import com.google.gson.*;

public class bcaApplication extends Application<bcaConfiguration> {
    public static void main(final String[] args) throws Exception {
        new bcaApplication().run(args);
    }

    @Override
    public String getName() {
        return "bca";
    }

    @Override
    public void initialize(final Bootstrap<bcaConfiguration> bootstrap) {

    }

    @Override
    public void run(final bcaConfiguration configuration, final Environment environment) {

        // Setup jersey client configuration
        JerseyClientConfiguration conf = configuration.getJerseyClientConfiguration();
        conf.setChunkedEncodingEnabled(false);

        // Create Jersey http client
        final Client client = new JerseyClientBuilder(environment).using(conf).build(getName())
                .property(ClientProperties.SUPPRESS_HTTP_COMPLIANCE_VALIDATION, true);

        BCAClient bcaClient = new BCAClient(client);
        bcaClient.getToken();

        environment.jersey().register(new BCAService(client));

    }

}
