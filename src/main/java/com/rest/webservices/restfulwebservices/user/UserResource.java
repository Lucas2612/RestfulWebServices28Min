package com.rest.webservices.restfulwebservices.user;

import java.net.URI;
import java.util.List;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.rest.webservices.restfulwebservices.exception.PostAlreadyExistsException;
import com.rest.webservices.restfulwebservices.exception.PostNotFoundException;
import com.rest.webservices.restfulwebservices.exception.UserAlreadyExistsException;
import com.rest.webservices.restfulwebservices.exception.UserNotFoundException;

@RestController
public class UserResource {
	
	@Autowired
	private UserDaoService service;
	
	// GET /users
	// retrive all
	@GetMapping("/users")
	public List<User> retriveAllUsers(){
		return service.findAll();
	}
	
	// GET /users/{id}
	// retrive one
	@GetMapping("/users/{id}")
	public User retriveUser(@PathVariable int id) {
		User user = service.findOne(id);
		if (user==null) {
			throw new UserNotFoundException("id-" + id);
		}
		return user;
	}
	
	// CREATED
	// input of user
	//output user
	@PostMapping("/users")
	public ResponseEntity<User> createUser(@Valid @RequestBody User user) {
		// check if id already exists the 409 conflict
		
		User tempUser = service.findOne(user.getId());
		
		if (tempUser!=null) {
			throw new UserAlreadyExistsException("Duplicate id: " + user.getId());
		}
		
		User savedUser = service.save(user);
		// CREATED
		// /users/savedUser.getId()
		URI location = ServletUriComponentsBuilder
			.fromCurrentRequest()
			.path("/{id}")
			.buildAndExpand(savedUser.getId()).toUri();
		return ResponseEntity.created(location).build();
		
	}
	
	// Retrieve all posts from an user GET /users/{id}/posts
	@GetMapping("/users/{userId}/posts")
	public List<Post> findAllPosts(@PathVariable int userId){
		return service.findAllPosts(userId);
	}
	
	// Create posts from an user POST /users/{id}/posts
	@PostMapping("/users/{userId}/posts")
	public ResponseEntity<Post> createUserPost(@PathVariable int userId, @RequestBody Post post) {
		
		// Check if user exists
		User tempUser = service.findOne(userId);
		if (tempUser==null) {
			throw new UserNotFoundException("user id: " + userId);
		}
		post.setUserId(userId);
		
		// check if id already exists the 409 conflict
		Post tempPost = service.findOnePost(post.getId());
		
		if (tempPost!=null) {
			throw new PostAlreadyExistsException(
					"Duplicate id userId: " + userId + " post id:" + tempPost.getId());
		}
		
		Post savedPost = service.savePost(post);
		URI location = ServletUriComponentsBuilder
			.fromCurrentRequest()
			.path("/{id}")
			.buildAndExpand(savedPost.getId()).toUri();
		return ResponseEntity.created(location).build();
		
	}
	
	// retrieve details of a post GET /users/{id}/posts/{id}
	@GetMapping("/users/{userId}/posts/{id}")
	public Post retrivePost(@PathVariable int userId, @PathVariable int id) {
		
		// Check if user exists
		User tempUser = service.findOne(userId);
		if (tempUser==null) {
			throw new UserNotFoundException("user id: " + userId);
		}
				
		Post post = service.findOnePost(id);
		if (post==null) {
			throw new PostNotFoundException("post id-" + id);
		}
		return post;
	}
	
	@DeleteMapping("/users/{id}")
	public void deleteUser(@PathVariable Integer id) {
		User user = service.deleteById(id);
		if (user==null) {
			throw new UserNotFoundException("id: " + id);
		}
		
	}
	
}
