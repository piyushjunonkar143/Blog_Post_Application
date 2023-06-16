package com.mountblue.piyush.restController;

import com.mountblue.piyush.entity.Post;
import com.mountblue.piyush.service.PostService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/blog-posts")
public class PostRestController {

    @Autowired
    private PostService postService;

    @GetMapping("/posts")
    public ResponseEntity<Page<Post>> getAllPosts(@RequestParam(value = "search", defaultValue = "", required = false) String search,
                                                  @RequestParam(value = "authorId", defaultValue = "", required = false) String authorIds,
                                                  @RequestParam(value = "publishedAtId", defaultValue = "", required = false) String publishedAtIds,
                                                  @RequestParam(value = "tagId", defaultValue = "", required = false) String tagIds,
                                                  @RequestParam(value = "order", defaultValue = "", required = false) String order,
                                                  @RequestParam(value = "start", defaultValue = "0", required = false) int start,
                                                  @RequestParam(value = "limit", defaultValue = "10", required = false) int limit) {
        Page<Post> posts;
        start = start / 10;
        if (!search.isEmpty()) {
            posts = postService.getPosts(start, limit, search);
        } else if (!authorIds.isEmpty() || !publishedAtIds.isEmpty() || !tagIds.isEmpty()) {
            posts = postService.filterPost(authorIds, publishedAtIds, tagIds, start, limit);
        } else {
            posts = postService.getPosts(start, limit);
        }

        if (!order.isEmpty()) {
            posts = postService.getPosts(start, limit);
        }
        return ResponseEntity.ok(posts);
    }

    @PreAuthorize("hasAuthority('ADMIN') or @securityService.isOwnerForPost(authentication, #postId)")
    @GetMapping("/posts/{postId}")
    public ResponseEntity<?> getPostById(@PathVariable int postId) {
        Post post = postService.getPost(postId);
        if (post != null) {
            return ResponseEntity.ok(post);
        } else {
            return ResponseEntity.badRequest().body("Post with postId not found");
        }
    }

    @PreAuthorize("hasAuthority('ADMIN') or @securityService.isAuthorisedToCreatePost(authentication, #post)")
    @PostMapping("/posts")
    public ResponseEntity<String> createPost(@RequestBody Post post) {
        if (post.getTitle() == null || post.getTitle().isEmpty()) {
            return ResponseEntity.badRequest().body("Title required");
        } else if (post.getContent() == null || post.getContent().isEmpty()) {
            return ResponseEntity.badRequest().body("Content required");
        } else if (post.getAuthor() == null || post.getAuthor().isEmpty()) {
            return ResponseEntity.badRequest().body("Author required");
        } else if (post.getTags() == null || post.getTags().isEmpty()) {
            return ResponseEntity.badRequest().body("Tags required");
        } else {
            Post createdPost = postService.savePost(post.getTitle(), post.getAuthor(), post.getContent(), post.getTags());
            if (createdPost != null) {
                return ResponseEntity.status(HttpStatus.CREATED).body("Post created");
            }
            return new ResponseEntity<>("Post not created", HttpStatus.UNPROCESSABLE_ENTITY);
        }
    }

    @PreAuthorize("hasAuthority('ADMIN') or @securityService.isOwnerForPost(authentication, #postId)")
    @PutMapping("/posts/{postId}")
    public ResponseEntity<?> updatePost(@PathVariable int postId, @RequestBody Post post) {
        Post updatedPost = postService.updatePost(postId, post);
        if (updatedPost != null) {
            return ResponseEntity.ok(updatedPost);
        } else {
            return ResponseEntity.badRequest().body("Post with postId not found");
        }
    }

    @PreAuthorize("hasAuthority('ADMIN') or @securityService.isOwnerForPost(authentication, #postId)")
    @DeleteMapping("/posts/{postId}")
    public ResponseEntity<String> deletePostById(@PathVariable int postId) {
        Post post = postService.getPost(postId);
        if (post == null) {
            return new ResponseEntity<>("post not found", HttpStatus.NOT_FOUND);
        }

        boolean deleted = postService.deletePostById(postId);
        if (deleted) {
            return ResponseEntity.ok("Post deleted successfully");
        } else {
            return new ResponseEntity<>("post not deleted", HttpStatus.BAD_REQUEST);
        }
    }

}
