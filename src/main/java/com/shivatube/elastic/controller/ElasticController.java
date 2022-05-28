package com.shivatube.elastic.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import com.fasterxml.jackson.databind.JsonNode;
import com.shivatube.elastic.service.ElasticService;

@Controller
public class ElasticController {

	@Autowired
	private ElasticService elasticService;
	
	@GetMapping("/elastic/search")
	public ResponseEntity<JsonNode> elasticSearch(){
		return elasticService.searchData();
	}
}
