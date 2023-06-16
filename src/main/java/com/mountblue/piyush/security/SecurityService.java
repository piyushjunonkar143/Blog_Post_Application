package com.mountblue.piyush.security;

import com.mountblue.piyush.entity.Post;
import com.mountblue.piyush.entity.User;
import com.mountblue.piyush.service.PostService;
import com.mountblue.piyush.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.Authentication;

@Configuration
public class SecurityService {

    @Autowired
    private UserService userService;

    @Autowired
    private PostService postService;

    public boolean isOwner(Authentication authentication, int userId) {
        String username = authentication.getName();
        User user = userService.getUserById(userId);
        return user != null && user.getName().equals(username);
    }

    public boolean isOwnerForPost(Authentication authentication, int postId) {
        Post post = postService.getPost(postId);
        return post != null && authentication.getName().equals(post.getAuthor());
    }

    public boolean isAuthorisedToCreatePost(Authentication authentication, Post post) {
        String author = post.getAuthor();
        return author != null && authentication.getName().equals(author);
    }

}
