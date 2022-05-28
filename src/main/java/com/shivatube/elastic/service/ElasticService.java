package com.shivatube.elastic.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.JsonNode;
import com.shivatube.elastic.component.ElasticSearch;
import com.shivatube.elastic.config.ElasticConfig;

@Service
public class ElasticService {

	private final ElasticConfig elasticConfig;
	
	private ElasticSearch elasticSearch;
	
	@Autowired
	public ElasticService(ElasticConfig properties, ElasticSearch elasticSearch) {
		this.elasticConfig = properties;
		this.elasticSearch = elasticSearch;
	}
	
	public ResponseEntity<JsonNode> searchData() {

		/* Setting up the Elastic Search Request */
		String elasticSearchURL = elasticConfig.getShivatube().getElasticURL();
		String elasticAPIKey = elasticConfig.getShivatube().getElasticSearchAPIKey();
		
		ResponseEntity<JsonNode> response = elasticSearch.callElasticSearchGet( elasticSearchURL, elasticAPIKey);
		
		return response;
	}
}
