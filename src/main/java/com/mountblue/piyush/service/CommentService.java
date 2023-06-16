package com.mountblue.piyush.service;

import java.util.List;

import com.mountblue.piyush.dao.PostRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.mountblue.piyush.dao.CommentRepository;
import com.mountblue.piyush.entity.Comment;

import jakarta.transaction.Transactional;

@Service
public class CommentService {
    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private PostRepository postRepository;

    public Comment saveComment(Comment comment) {
        commentRepository.save(comment);
        return comment;
    }

    public List<Comment> getComments(int postId) {
        return commentRepository.findAllByPostId(postId);
    }

    public Comment getComment(int commentId) {
        return commentRepository.findById(commentId);
    }

    @Transactional
    public Comment updateComment(Comment comment, int commentId) {
        commentRepository.updateComment(commentId, comment.getName(), comment.getEmail(),
                comment.getComment(), comment.getUpdatedAt());
        return comment;
    }

    public Boolean deleteComment(int commentId) {
        return commentRepository.deleteById(commentId) == null? true: false;
    }

    public void deleteComments(List<Comment> comments) {
        commentRepository.deleteAll(comments);
    }

    @Transactional
    public boolean deleteAllCommentsByPostId(int postId) {
        return commentRepository.deleteAllComments(postRepository.findById(postId).getComments()) ;
    }
}
