package com.mountblue.piyush.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.mountblue.piyush.entity.Comment;
import com.mountblue.piyush.service.CommentService;
import com.mountblue.piyush.service.PostService;

@Controller
public class CommentController {
    @Autowired
    private PostService postService;
    @Autowired
    private CommentService commentService;

    @GetMapping("/newComment/{postId}")
    public String newComment(@PathVariable("postId") int postId, Model model) {
        model.addAttribute("postId", postId);
        return "newComment";
    }

    @PostMapping("/newComment/{postId}")
    public String addComment(@RequestParam("name") String name, @RequestParam("email") String email,
                             @RequestParam("comment") String commentData, @PathVariable("postId") int postId) {
        Comment comment = new Comment();
        comment.setName(name);
        comment.setEmail(email);
        comment.setComment(commentData);
        comment.setPost(postService.getPost(postId));
        commentService.saveComment(comment);
        return "redirect:/post/" + postId;
    }

    @GetMapping("comment/edit/{commentId}")
    @PreAuthorize("@commentService.getComment(#commentId).getPost().getAuthor().equals(authentication.name)")
    public String editComment(@PathVariable("commentId") int commentId, Model model, Authentication authentication) {
        Comment existingComment = commentService.getComment(commentId);
        if (!existingComment.getPost().getAuthor().equals(authentication.getName())) {
            return "redirect:/post/" + existingComment.getPost().getId();
        }
        model.addAttribute("commentData", existingComment);
        return "editComment";
    }

    @PostMapping("comment/edit/{commentId}")
    @PreAuthorize("@commentService.getComment(#commentId).getPost().getAuthor() == #authentication.name")
    public String updateComment(@PathVariable("commentId") int commentId, @RequestParam("name") String name,
                                @RequestParam("email") String email, @RequestParam("comment") String commentData,
                                Model model, Authentication authentication) {
        Comment existingComment = commentService.getComment(commentId);
        if (!existingComment.getPost().getAuthor().equals(authentication.getName())) {
            return "redirect:/post/" + existingComment.getPost().getId();
        }
        existingComment.setName(name);
        existingComment.setEmail(email);
        existingComment.setComment(commentData);
        commentService.updateComment(existingComment, commentId);
        return "redirect:/post/" + existingComment.getPost().getId();
    }

    @GetMapping("comment/delete/{commentId}")
    @PreAuthorize("@commentService.getComment(#commentId).getPost().getAuthor() == #authentication.name")
    public String deleteComment(@PathVariable("commentId") int commentId, Authentication authentication) {
        Comment existingComment = commentService.getComment(commentId);
        if (!existingComment.getPost().getAuthor().equals(authentication.getName())) {
            return "redirect:/post/" + existingComment.getPost().getId();
        } else {
            commentService.deleteComment(commentId);
        }
        return "redirect:/post/" + existingComment.getPost().getId();
    }

}
