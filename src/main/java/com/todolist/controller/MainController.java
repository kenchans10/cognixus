package com.todolist.controller;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.todolist.repository.todolistRepo;
import com.google.api.client.http.HttpTransport;
import com.todolist.exception.ResourceNotFoundException;
import com.todolist.model.todolist;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.view.RedirectView;

import com.google.api.client.auth.oauth2.AuthorizationCodeRequestUrl;
import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.auth.oauth2.TokenResponse;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets.Details;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.gmail.GmailScopes;
import com.google.api.services.gmail.model.ListMessagesResponse;
import com.google.api.services.gmail.model.Message;

@RestController
@RequestMapping("/api")
public class MainController {
	
	@Autowired
	todolistRepo todolistRepo;
	
	private static final String APPLICATION_NAME = "GmailAlexa";
	private static HttpTransport httpTransport;
	private static final JsonFactory JSON_FACTORY = JacksonFactory.getDefaultInstance();
	private static com.google.api.services.gmail.Gmail client;

	GoogleClientSecrets clientSecrets;
	GoogleAuthorizationCodeFlow flow;
	Credential credential;

	@Value("${gmail.client.clientId}")
	private String clientId;

	@Value("${gmail.client.clientSecret}")
	private String clientSecret;

	@Value("${gmail.client.redirectUri}")
	private String redirectUri;

	@RequestMapping(value = "/login/gmail", method = RequestMethod.GET)
	public RedirectView googleConnectionStatus(HttpServletRequest request) throws Exception {
		return new RedirectView(authorize());
	}

	private String authorize() throws Exception {
		AuthorizationCodeRequestUrl authorizationUrl;
		if (flow == null) {
			Details web = new Details();
			web.setClientId(clientId);
			web.setClientSecret(clientSecret);
			clientSecrets = new GoogleClientSecrets().setWeb(web);
			httpTransport = GoogleNetHttpTransport.newTrustedTransport();
			flow = new GoogleAuthorizationCodeFlow.Builder(httpTransport, JSON_FACTORY, clientSecrets,
					Collections.singleton(GmailScopes.GMAIL_READONLY)).build();
		}
		authorizationUrl = flow.newAuthorizationUrl().setRedirectUri(redirectUri);

		System.out.println("gamil authorizationUrl ->" + authorizationUrl);
		return authorizationUrl.build();
	}

	@GetMapping("/")
	public String helloWorld() {
		return "Hello World";
	}
	
	
	@GetMapping("/todolist")
	public List<todolist> getAllList() {
		return todolistRepo.findAll();
	}
	
	@PostMapping("/todolist")
	public todolist createTodo(@RequestBody todolist todo) {
		return todolistRepo.save(todo);
	}

	@GetMapping("/todolist/{id}")
	public todolist getCommentById(@PathVariable(value = "id") Long todoId) {
		return todolistRepo.findById(todoId)
				.orElseThrow(() -> new ResourceNotFoundException("Todolist", "id", todoId));
	}

	@PutMapping("/todolist/mark-complete/{id}")
	public todolist updateComment(@PathVariable(value = "id") Long todoId, @RequestBody todolist todoDetails) {

		todolist todo = todolistRepo.findById(todoId)
				.orElseThrow(() -> new ResourceNotFoundException("Todolist", "id", todoId));

		todo.setStatus(todoDetails.getStatus());

		todolist updatedtodo = todolistRepo.save(todo);
		return updatedtodo;
	}

	@DeleteMapping("/todolist/{id}")
	public ResponseEntity<?> deleteComment(@PathVariable(value = "id") Long todoId) {
		todolist comment = todolistRepo.findById(todoId)
				.orElseThrow(() -> new ResourceNotFoundException("Todolist", "id", todoId));

		todolistRepo.delete(comment);

		return ResponseEntity.ok("Successfully Deleted");
	}
	
	@RequestMapping("/logout")
	public ResponseEntity<Object> redirectToExternalUrl() throws URISyntaxException {
	    URI yahoo = new URI("https://accounts.google.com/logout");
	    HttpHeaders httpHeaders = new HttpHeaders();
	    httpHeaders.setLocation(yahoo);
	    return new ResponseEntity<>(httpHeaders, HttpStatus.SEE_OTHER);
	}
}
