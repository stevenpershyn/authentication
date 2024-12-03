package com.oauth.authentication.controller;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import com.oauth.authentication.model.Session;
import com.oauth.authentication.model.UserDtls;
import com.oauth.authentication.service.SessionService;
import com.oauth.authentication.service.UserService;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;

@RestController
public class AuthController {

    @Autowired
    private UserService userService;
    
    private final SessionService sessionService;

    @Autowired
    public AuthController(UserService userService, SessionService sessionService) {
        this.userService = userService;
        this.sessionService = sessionService;
    }

    @PostMapping("/signin")
    public ResponseEntity<?> signin(
            @RequestParam String username, 
            @RequestParam String password, 
            HttpServletResponse response) {

    	RestTemplate restTemplate = new RestTemplate();
    	
    	String otherServiceUrl = "http://localhost:8090/user";

        // Prepare request parameters
        Map<String, String> params = new HashMap<>();
        params.put("username", username);
        params.put("password", password);
    	
        // Step 1: Validate User Credentials
        UserDtls user = userService.getUserByEmail(username);
        
        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(otherServiceUrl)
                .queryParam("username", username)
                .queryParam("password", password);
        

        ResponseEntity<String> otherServiceResponse = restTemplate.getForEntity(builder.toUriString(), String.class);

        if (user == null) {
            // If credentials are invalid, return an error response
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid email or password");
        }else {
        	Session session = sessionService.createSession(user.getId());
        	  Cookie sessionCookie = new Cookie("sessionId", session.getSessionId());
              sessionCookie.setHttpOnly(true);
              sessionCookie.setPath("/"); // Accessible to all paths
              sessionCookie.setMaxAge(24 * 60 * 60);  // Set cookie expiration to 24 hours
              response.addCookie(sessionCookie);

              // Step 5: Redirect to main page or user page in main service
              try {
            	    String redirectUrl = "http://localhost:8090/user/"; // Adjust this URL
            	    response.sendRedirect(redirectUrl);
            	} catch (IOException e) {
            	    e.printStackTrace();
            	    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
            	            .body("Redirect failed");
            	}
              
              return null;
        	//return ResponseEntity.status(otherServiceResponse.getStatusCode()).body(otherServiceResponse.getBody());
            //return ResponseEntity.ok("Session created with ID: " + session.getSessionId());
        }

        // Step 2: Generate Session ID
//        String sessionId = sessionService.generateSessionId();
//        sessionService.storeSession(sessionId, user.getId());

        // Step 3: Set session ID in a cookie
//        Cookie sessionCookie = new Cookie("sessionId", "ABCSession");
//        sessionCookie.setHttpOnly(true);
//        sessionCookie.setPath("/");
//        sessionCookie.setMaxAge(24 * 60 * 60);  // Set cookie expiration to 24 hours
//        response.addCookie(sessionCookie);

        // Step 4: Redirect the user back to the main service after successful login
//        String redirectUrl = "http://localhost:8090/";  // Adjust this to your main service URL
//        String sessionId = "ABCSession";
//		return ResponseEntity.status(HttpStatus.FOUND)
//        		.header("Location", redirectUrl)
//        		.header("sessionId", sessionId)
//        		.build();
    }
    
    @GetMapping("/signin/validate-session/{sessionId}")
    public ResponseEntity<UserDtls> validateSession(@PathVariable String sessionId) {
        Session session = sessionService.findBySessionId(sessionId);

        if (session != null) {
            UserDtls user = userService.findUserById(session.getUserId());
            if (user == null) {
                System.out.println("User not found for session ID: " + sessionId);
                return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
            }
            return new ResponseEntity<>(user, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
    }
}
