package com.shivatube.elastic.component;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

import javax.net.ssl.SSLContext;

import org.apache.http.client.config.RequestConfig;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.ssl.TrustStrategy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.JsonNode;
import com.shivatube.elastic.service.Helpers;

@Component("ElasticSearch")
public class ElasticSearch {

	private final Logger logger = LoggerFactory.getLogger(this.getClass());

	/**
	 * Call the elastic search with payloads
	 * 
	 * @param name
	 * @param elasticIndex the name for certain elastic index source
	 * @param payload
	 * @return elasticResponse
	 */
	public ResponseEntity<JsonNode> callElasticSearchGet(String elasticURL, String elasticAPIKey) {
		String elasticSearchURL = elasticURL;
		ResponseEntity<JsonNode> response = request(HttpMethod.GET, elasticSearchURL, "/_search", elasticAPIKey);
		if (response == null) {
			return null;
		}
		logger.info("The response from elastic search is: {}", Helpers.prettyPrintJsonString(response.getBody()));
		return response;

	}

	/**
	 * Helper method to make an HTTP Request
	 *
	 * @param method  type of request
	 * @param baseUrl hostname
	 * @param path    path
	 * @param payload payload to be passed to request
	 * @return the HttpResponse
	 */
	private ResponseEntity<JsonNode> request(HttpMethod method, String baseUrl, String path, String elasticAPIKey) {
		HttpHeaders headers = new HttpHeaders();
		headers.set("Authorization", "ApiKey " + elasticAPIKey);
		if (method == HttpMethod.POST) {
			headers.setContentType(MediaType.APPLICATION_JSON);
		}
		RequestEntity<JsonNode> entity;
		try {
			entity = new RequestEntity(headers, method, new URI(baseUrl + path));
		} catch (URISyntaxException ex) {
			throw new IllegalStateException("Error constructing URI", ex);
		}

		RestTemplate restTemplate = getRestTemplate();

		switch (method) {
		case GET:
			return restTemplate.exchange(entity, JsonNode.class);
		case POST:
			return restTemplate.exchange(entity, JsonNode.class);
		default:
			// not yet supported HTTP request method
			return null;
		}
	}

	/**
	 * Setting up RestTemplate which makes sure it would not be blocked by Https
	 * 
	 * @return
	 */
	public RestTemplate getRestTemplate() {
		HttpComponentsClientHttpRequestFactory factory = new HttpComponentsClientHttpRequestFactory();

		RequestConfig requestConfig = setSSLRequestConfig();

		try {
			SSLConnectionSocketFactory tmp = setIgnoreHostConnectionSocketFactory();
			CloseableHttpClient httpClient = HttpClients.custom().setDefaultRequestConfig(requestConfig)
					.setSSLSocketFactory(tmp).build();
			factory.setHttpClient(httpClient);
		} catch (Exception e) {

		}

		RestTemplate restTemplate = new RestTemplate(factory);

		return restTemplate;
	}

	/**
	 * Setting up to ignore host connection
	 * 
	 * @return
	 * @throws KeyManagementException
	 * @throws NoSuchAlgorithmException
	 * @throws KeyStoreException
	 * @throws IOException
	 */
	public SSLConnectionSocketFactory setIgnoreHostConnectionSocketFactory()
			throws KeyManagementException, NoSuchAlgorithmException, KeyStoreException, IOException {
		SSLContext sslContex = null;

		TrustStrategy acceptingTrustStrategy = new TrustStrategy() {
			@Override
			public boolean isTrusted(X509Certificate[] x509Certificates, String s) throws CertificateException {
				return true;
			}

		};

		sslContex = org.apache.http.ssl.SSLContexts.custom().loadTrustMaterial(null, acceptingTrustStrategy).build();
		return new SSLConnectionSocketFactory(sslContex);
	}

	/**
	 * Setting up timeout
	 * 
	 * @return
	 */

	public RequestConfig setSSLRequestConfig() {
		return RequestConfig.custom().setConnectTimeout(60000).setConnectionRequestTimeout(60000)
				.setSocketTimeout(60000).build();
	}

}
