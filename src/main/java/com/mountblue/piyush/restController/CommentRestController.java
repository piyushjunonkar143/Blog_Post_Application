package com.mountblue.piyush.restController;

import com.mountblue.piyush.entity.Comment;
import com.mountblue.piyush.service.CommentService;
import com.mountblue.piyush.service.PostService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/blog-posts")
public class CommentRestController {

    @Autowired
    private CommentService commentService;
    @Autowired
    private PostService postService;

    @GetMapping("/posts/{postId}/comments")
    public ResponseEntity<List<Comment>> getAllCommentsForPost(@PathVariable int postId) {
        List<Comment> comments = commentService.getComments(postId);
        if(comments != null) {
            return ResponseEntity.ok(comments);
        } else {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/posts/{postId}/comments/{commentId}")
    public ResponseEntity<?> getCommentById(@PathVariable int commentId) {
        Comment comment = commentService.getComment(commentId);
        if (comment != null) {
            return ResponseEntity.ok(comment);
        } else {
            return ResponseEntity.badRequest().body("Post with postId not found");
        }
    }

    @PostMapping("/posts/{postId}/comments")
    public ResponseEntity<?> createCommentForPost(@PathVariable int postId, @RequestBody Comment comment) {
        if (comment.getName() == null || comment.getName().isEmpty()) {
            return ResponseEntity.badRequest().body("Name required");
        } else if (comment.getEmail() == null || comment.getEmail().isEmpty()) {
            return ResponseEntity.badRequest().body("email required");
        }
        comment.setPost(postService.getPost(postId));
        Comment createdComment = commentService.saveComment(comment);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdComment);
    }

    @PreAuthorize("@securityService.isOwnerForPost(authentication, #postId)")
    @PutMapping("/posts/{postId}/comments/{commentId}")
    public ResponseEntity<Comment> updateComment(@PathVariable int postId,
                                                 @PathVariable int commentId,
                                                 @RequestBody Comment comment) {

        comment.setPost(postService.getPost(postId));
        Comment updatedComment = commentService.updateComment(comment, commentId);
        if (updatedComment != null) {
            return ResponseEntity.ok(updatedComment);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @PreAuthorize("@securityService.isOwnerForPost(authentication, #postId)")
    @DeleteMapping("/posts/{postId}/comments/{commentId}")
    public ResponseEntity<String> deleteCommentById(@PathVariable int commentId, @PathVariable int postId) {
        boolean deleted = commentService.deleteComment(commentId);
        if (deleted) {
            return ResponseEntity.ok("Comment deleted successfully");
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @PreAuthorize("@securityService.isOwnerForPost(authentication, #postId)")
    @DeleteMapping("/posts/{postId}/comments")
    public ResponseEntity<String> deleteAllCommentsForPost(@PathVariable int postId) {
        boolean deleted = commentService.deleteAllCommentsByPostId(postId);
        if (deleted) {
            return ResponseEntity.ok("All comments for the post deleted successfully");
        } else {
            return ResponseEntity.notFound().build();
        }
    }



}
