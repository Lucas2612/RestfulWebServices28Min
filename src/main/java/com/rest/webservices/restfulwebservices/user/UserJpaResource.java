package com.rest.webservices.restfulwebservices.user;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

import java.net.URI;
import java.util.List;
import java.util.Optional;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;
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
public class UserJpaResource {
	
	@Autowired
	private UserDaoService service;
	
	@Autowired
	private UserRepository repo;
	
	@Autowired
	private PostRepository postRepo;
	
	
	// GET /users
	// retrive all
	@GetMapping("/jpa/users")
	public List<User> retrieveAllUsers(){
		return repo.findAll();
	}
	
	// GET /users/{id}
	// retrive one
	@GetMapping("/jpa/users/{id}")
	public EntityModel<User> retrieveUser(@PathVariable int id) {
		Optional<User> user = repo.findById(id);
		if (!user.isPresent()) {
			throw new UserNotFoundException("id-" + id);
		}
		
		// HATEOAS
		
		// all-users 
		EntityModel<User> model = new EntityModel<>(user.get());
		WebMvcLinkBuilder linkTo = linkTo(methodOn(this.getClass()).retrieveAllUsers());
		model.add(linkTo.withRel("all-users"));
		
		return model;
	}
	
	// CREATED
	// input of user
	//output user
	@PostMapping("/jpa/users")
	public ResponseEntity<User> createUser(@Valid @RequestBody User user) {
		// check if id already exists the 409 conflict
		
		if (user.getId()!=null) {
			Optional<User> tempUser = repo.findById(user.getId());
			if (!tempUser.isPresent()) {
				throw new UserAlreadyExistsException("Duplicate id: " + user.getId());
			}
		}
		
		User savedUser = repo.save(user);
		// CREATED
		// /users/savedUser.getId()
		URI location = ServletUriComponentsBuilder
			.fromCurrentRequest()
			.path("/{id}")
			.buildAndExpand(savedUser.getId()).toUri();
		return ResponseEntity.created(location).build();
		
	}
	
	// Retrieve all posts from an user GET /users/{id}/posts
	@GetMapping("/jpa/users/{id}/posts")
	public List<Post> findAllPosts(@PathVariable int id){
		Optional<User> userOptional = repo.findById(id);
		
		if (!userOptional.isPresent()) {
			throw new UserNotFoundException("id: " + id);
		}
		return userOptional.get().getPosts();
	}
	
	// Create posts from an user POST /users/{id}/posts
	@PostMapping("/jpa/users/{userId}/posts")
	public ResponseEntity<Post> createUserPost(@PathVariable int userId, @RequestBody Post post) {
		
		// Check if user exists
		Optional<User> tempUser = repo.findById(userId);
		if (!tempUser.isPresent()) {
			throw new UserNotFoundException("user id: " + userId);
		}
		
		User user = tempUser.get();
		post.setUser(user);
		Post savedPost = postRepo.save(post);
		
		URI location = ServletUriComponentsBuilder
			.fromCurrentRequest()
			.path("/{id}")
			.buildAndExpand(savedPost.getId()).toUri();
		return ResponseEntity.created(location).build();
		
	}
	
	// retrieve details of a post GET /users/{id}/posts/{id}
	@GetMapping("/jpa/users/{userId}/posts/{id}")
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
	
	@DeleteMapping("/jpa/users/{id}")
	public void deleteUser(@PathVariable Integer id) {
		repo.deleteById(id);
	}
	
}
