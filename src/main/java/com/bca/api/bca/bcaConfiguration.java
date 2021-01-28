package com.bca.api.bca;

import io.dropwizard.Configuration;
import io.dropwizard.client.JerseyClientConfiguration;
import io.dropwizard.util.Duration;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.hibernate.validator.constraints.*;

import javax.validation.Valid;
import javax.validation.constraints.*;

public class bcaConfiguration extends Configuration {

    @Valid
    @NotNull
    private JerseyClientConfiguration jerseyClient = new JerseyClientConfiguration();
	
    @JsonProperty("jerseyClient")
    public JerseyClientConfiguration getJerseyClientConfiguration() {
		jerseyClient.setTimeout(Duration.seconds(10));
		jerseyClient.setConnectionRequestTimeout(Duration.seconds(10));
		jerseyClient.setConnectionTimeout(Duration.seconds(10));
				
        return jerseyClient;
    }
}
