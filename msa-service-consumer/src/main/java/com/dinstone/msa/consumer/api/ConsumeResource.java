package com.dinstone.msa.consumer.api;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import com.dinstone.msa.consumer.service.UserClientService;
import com.dinstone.msa.model.User;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixProperty;

@RestController
@RequestMapping("/consume")
public class ConsumeResource {

	private static final Logger log = LoggerFactory.getLogger(ConsumeResource.class);

	@Autowired
	private UserClientService userClientService;

	@Autowired
	ConsumerService consumerService;

	@GetMapping("/get/{uid}")
	public User get(@PathVariable("uid") long uid) {
		log.debug("get access");
		return userClientService.get(uid);
	}

	@GetMapping("/list")
	public List<User> list() {
		log.info("list access");
		return userClientService.list();
	}

	@GetMapping("/dc")
	public String dc() {
		log.debug("dc access");
		return consumerService.consumer();
	}

	@Component
	class ConsumerService {

		@Autowired
		RestTemplate restTemplate;

		@HystrixCommand(fallbackMethod = "fallback", commandProperties = @HystrixProperty(name = "execution.isolation.thread.timeoutInMilliseconds", value = "1000"))
		public String consumer() {
			return restTemplate.getForObject("http://eureka-client/dc", String.class);
		}

		public String fallback() {
			return "fallback";
		}

	}
}
