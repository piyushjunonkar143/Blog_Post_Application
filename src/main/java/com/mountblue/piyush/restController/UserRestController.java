package com.mountblue.piyush.restController;
import com.mountblue.piyush.entity.User;
import com.mountblue.piyush.security.SecurityService;
import com.mountblue.piyush.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/blog-posts")
public class UserRestController {
    @Autowired
    private UserService userService;

    @Autowired
    private SecurityService securityService;

    @PostMapping("/users")
    public ResponseEntity<String> createUser(@RequestBody User user) {
        if(user.getName() == null || user.getName().isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("name is required");
        }else if (user.getEmail() == null || user.getEmail().isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("email id is required");
        } else if (user.getPassword() == null || user.getPassword().isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("password is required");
        } else if (user.getConfirm_Password() == null || user.getConfirm_Password().isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("confirm password is required");
        } else if (user.getRoles() == null || user.getRoles().isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("role is required");
        } else {
            String responseRegistration = userService.addUser(user);
            if (responseRegistration != null) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(responseRegistration);
            }
            return new ResponseEntity<>("User Registered", HttpStatus.CREATED);
        }
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @GetMapping("/user")
    public ResponseEntity<List<User>> getAllUsers() {
        List<User> users = userService.getAllUsers();
        if(users != null) {
            return ResponseEntity.ok(users);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @PreAuthorize("hasAuthority('ADMIN') or @securityService.isOwner(authentication, #userId)")
    @GetMapping("/user/{userId}")
    public ResponseEntity<User> getUser(@PathVariable int userId) {
        User user = userService.getUserById(userId);
        if (user != null) {
            return ResponseEntity.ok(user);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @DeleteMapping("/user")
    public ResponseEntity<String> deleteAllUser() {
        userService.deleteAllUsers();
        return ResponseEntity.ok("All users deleted successfully");
    }

    @PreAuthorize("hasAuthority('ADMIN') or @securityService.isOwner(authentication, #userId)")
    @DeleteMapping("/user/{userId}")
    public ResponseEntity<String> deleteUserById(@PathVariable int userId) {
        if (userService.getUserById(userId) != null) {
            userService.deleteUserById(userId);
            return ResponseEntity.ok("User deleted successfully");
        } else {
            return new ResponseEntity<>("User with id Not Found", HttpStatus.NOT_FOUND);
        }
    }

    @PreAuthorize("hasAuthority('ADMIN') or @securityService.isOwner(authentication, #userId)")
    @PutMapping("/user/{userId}")
    public ResponseEntity<String> updateUser(@PathVariable int userId, @RequestBody User updatedUser) {
        if(updatedUser.getName() == null || updatedUser.getName().isEmpty()) {
            return ResponseEntity.badRequest().body("name is required");
        }else if (updatedUser.getEmail() == null || updatedUser.getEmail().isEmpty()) {
            return ResponseEntity.badRequest().body("email id is required");
        } else if (updatedUser.getPassword() == null || updatedUser.getPassword().isEmpty()) {
            return ResponseEntity.badRequest().body("password is required");
        } else if (updatedUser.getConfirm_Password() == null || updatedUser.getConfirm_Password().isEmpty()) {
            return ResponseEntity.badRequest().body("confirm password is required");
        } else if (updatedUser.getRoles() == null || updatedUser.getRoles().isEmpty()) {
            return ResponseEntity.badRequest().body("role is required");
        } else {
            boolean updated = userService.updateUser(userId, updatedUser);
            if (updated) {
                return ResponseEntity.ok("User updated successfully");
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found");
            }
        }
    }

}


