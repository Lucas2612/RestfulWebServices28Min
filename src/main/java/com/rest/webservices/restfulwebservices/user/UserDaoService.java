package com.rest.webservices.restfulwebservices.user;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

@Component
public class UserDaoService {

	private static int userCount = 3;
	private static List<User> users = new ArrayList<User>();
	private static List<Post> posts = new ArrayList<Post>();
	
	static {
		users.add(new User(1, "Adam", new Date()));
		users.add(new User(2, "Eve", new Date()));
		users.add(new User(3, "Jack", new Date()));
		
		posts.add(new Post(1, 1, "Post1Post1Post1Post1"));
		posts.add(new Post(2, 1, "Post2Post2Post2Post2Post2"));
		posts.add(new Post(3, 2, "Post3Post3Post3Post3"));
	}
	
	
	public List<User> findAll(){
		return users;
	}
	
	public User save(User user) {
		if (user.getId()==null) {
			user.setId(++userCount);
		}else{
			++userCount;
		}
		users.add(user);
		return user;
	}
	
	public Post savePost(Post post) {
		if (post.getId()==0) {
			post.setId(++userCount);
		}else{
			++userCount;
		}
		posts.add(post);
		return post;
	}
	
	public User findOne(int id) {
		User user = users.stream()
				  .filter(tempUser -> id == tempUser.getId())
				  .findAny()
				  .orElse(null);
		return user;
	}
	
	public User deleteById(int id) {
		User user = findOne(id);
		users.removeIf(u -> u.getId()==id);
		
		return user;
	}
	
	public List<Post> findAllPosts(int userId){
		List<Post> userPosts = posts.stream()
				.filter(tempPost -> userId == tempPost.getUserId())
				.collect(Collectors.toList());
		return userPosts;
	}
	
	public Post findOnePost(int id) {
		Post post = posts.stream()
				  .filter(tempPost -> id == tempPost.getId())
				  .findAny()
				  .orElse(null);
		
		return post;
	}
	
	
}
