package com.example.resttemplate;

import com.example.resttemplate.model.User;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.http.*;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@SpringBootApplication
public class RestTemplateApplication {

	private static final String URL = "http://94.198.50.185:7081/api/users";
	private static final String DELETE_URL ="http://94.198.50.185:7081/api/users/{id}";


	public static void main(String[] args) {
		SpringApplication.run(RestTemplateApplication.class, args);

		RestTemplate restTemplate = new RestTemplate();

		HttpHeaders httpHeaders = new HttpHeaders();
		httpHeaders.setContentType(MediaType.APPLICATION_JSON);

		//HttpEntity для request
		HttpEntity<String> entityString = new HttpEntity<>(httpHeaders);

		ResponseEntity<String> responseEntity = restTemplate.exchange(URL, HttpMethod.GET, entityString, String.class);

		//Получаем set-cookie
		String cookies = responseEntity.getHeaders().getFirst("set-cookie");

		System.out.println("Cookies: " + cookies);
		System.out.println("Body: " + responseEntity.getBody());

		//Устанавливаем header
		restTemplate.getInterceptors().add(new ClientHttpRequestInterceptor(){
			@Override
			public ClientHttpResponse intercept(HttpRequest request, byte[] body, ClientHttpRequestExecution execution) throws IOException {
				request.getHeaders().set("cookie", cookies);
				return execution.execute(request, body);
			}
		});

		//POST
		User newUser = new User(3L, "James", "Brown", (byte) 44 );
		HttpEntity<User> postUser = new HttpEntity<>(newUser, httpHeaders);
		ResponseEntity<String> resultPOST = restTemplate.exchange(URL, HttpMethod.POST, postUser, String.class);
		System.out.println("POST: " + resultPOST.getBody());

		//PUT
		User editUser = new User (3L, "Thomas", "Shelby", (byte) 44);
		HttpEntity<User> putUser = new HttpEntity<>(editUser, httpHeaders);
		ResponseEntity<String> resultPUT = restTemplate.exchange(URL, HttpMethod.PUT, putUser, String.class);
		System.out.println("PUT: " + resultPUT.getBody());

		//DELETE
		Map<String, Long> parameter = new HashMap<>();
		parameter.put("id", 3L);
		ResponseEntity<String> resultDELETE = restTemplate.exchange(DELETE_URL, HttpMethod.DELETE, null, String.class, parameter);
		System.out.println("DELETE: " + resultDELETE.getBody());

	}

}
