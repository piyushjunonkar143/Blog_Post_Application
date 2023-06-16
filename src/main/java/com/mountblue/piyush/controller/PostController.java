package com.mountblue.piyush.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.mountblue.piyush.entity.Post;
import com.mountblue.piyush.entity.Tag;
import com.mountblue.piyush.service.CommentService;
import com.mountblue.piyush.service.PostService;

@Controller
public class PostController {
    @Autowired
    private PostService postService;
    @Autowired
    private CommentService commentService;

    @GetMapping("/newPost")
    public String getNewPost() {
        return "newPost";
    }

    @PostMapping("/newPost")
    public String addNewPost(@RequestParam("title") String title, @RequestParam("tags") String tags,
                             @RequestParam("author") String author, @RequestParam("content") String content) {
        String[] tagNames = tags.split(",");
        List<Tag> tagsList = new ArrayList<>();

        for (String tagName : tagNames) {
            Tag tag = new Tag();
            tag.setName(tagName.trim());
            tagsList.add(tag);
        }

        postService.savePost(title, author, content, tagsList);
        return null;
    }

    @GetMapping("/post/{postId}")
    public String getPost(@PathVariable("postId") int postId, Model model) {
        model.addAttribute("post", postService.getPost(postId));
        model.addAttribute("comments", commentService.getComments(postId));
        return "post";
    }

    @PreAuthorize("hasAuthority('ADMIN') or @postService.getPost(#postId).getAuthor() == #authentication.name")
    @GetMapping("/posts/delete/{postId}")
    public String deletePost(@PathVariable("postId") int postId, Model model, Authentication authentication) {
        Post post = postService.getPost(postId);
        commentService.deleteComments(post.getComments());
        postService.deletePost(postId);
        return "redirect:/";
    }

    @PreAuthorize("hasAuthority('ADMIN') or @postService.getPost(#postId).getAuthor() == #authentication.name")
    @GetMapping("/posts/update/{postId}")
    public String updatePost(@PathVariable("postId") int postId, Model model, Authentication authentication) {
        Post existingPost = postService.getPost(postId);
        if (authentication.getAuthorities().stream().anyMatch(auth -> auth.getAuthority().equals("AUTHOR"))) {
            String currentUsername = authentication.getName();
            if (existingPost.getAuthor().equals(currentUsername)) {
                List<String> tagNames = existingPost.getTags().stream().map(Tag::getName).collect(Collectors.toList());
                model.addAttribute("tagNames", String.join(", ", tagNames));
                model.addAttribute("post", existingPost);
                return "editPost";
            }
        }

        if (authentication.getAuthorities().stream().anyMatch(auth -> auth.getAuthority().equals("ADMIN"))) {
            List<String> tagNames = existingPost.getTags().stream().map(Tag::getName).collect(Collectors.toList());
            model.addAttribute("tagNames", String.join(", ", tagNames));
            model.addAttribute("post", existingPost);
            return "editPost";
        }
        return "redirect:/post/" + postId;
    }

    @PreAuthorize("hasAuthority('ADMIN') or @postService.getPost(#postId).getAuthor() == #authentication.name")
    @PostMapping("/posts/update/{postId}")
    public String updatePost(@PathVariable("postId") int postId, @RequestParam("title") String title,
                             @RequestParam("tags") String tags, @RequestParam("author") String author,
                             @RequestParam("content") String content, Model model, Authentication authentication) {
        Post existingPost = postService.getPost(postId);
        existingPost.setTitle(title);
        existingPost.setAuthor(author);
        existingPost.setContent(content);
        existingPost.setExcerpt(content.substring(0, 150) + "...");
        String[] tagNames = tags.split(",");
        List<Tag> tagsName = new ArrayList<>();

        for (String tagName : tagNames) {
            Tag tag = new Tag();
            tag.setName(tagName.trim());
            tagsName.add(tag);
        }

        existingPost.setTags(tagsName);
        postService.updatePost(postId, existingPost);
        return "redirect:/post/" + postId;
    }
}
