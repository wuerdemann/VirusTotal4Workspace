/**
 * 
 */
package net.miwu.virustotal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

import net.miwu.virustotal.client.VirusTotalClient;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.jackson.JacksonConverterFactory;

/**
 * @author miwu
 */
@Configuration
public class VirusTotal4WorkspaceConfiguration {

	@Autowired
	private AppProperties vtProp;

	@Autowired
	private ObjectMapper objectMapper;

	@Bean
	public OkHttpClient okHttpClient() {
		return new OkHttpClient();
	}

	@Bean
	public Retrofit retrofit(OkHttpClient client) {
		objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
		return new Retrofit.Builder().addConverterFactory(JacksonConverterFactory.create(objectMapper))
				.baseUrl(vtProp.getVtAPIUri()).client(client).build();
	}

	@Bean
	public VirusTotalClient virusTotalClient(Retrofit retrofit) {
		return retrofit.create(VirusTotalClient.class);
	}
}
