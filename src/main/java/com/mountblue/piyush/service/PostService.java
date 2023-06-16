package com.mountblue.piyush.service;

import java.time.LocalDate;
import java.util.*;

import com.mountblue.piyush.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.mountblue.piyush.dao.PostRepository;
import com.mountblue.piyush.dao.TagRepository;
import com.mountblue.piyush.entity.Post;
import com.mountblue.piyush.entity.Tag;

import jakarta.transaction.Transactional;

@Service
public class PostService {
    @Autowired
    private PostRepository postRepository;
    @Autowired
    private TagRepository tagRepository;

    public Post savePost(String title, String author, String content, List<Tag> tags) {
        Post post = new Post();
        post.setTitle(title);
        post.setAuthor(author);
        post.setContent(content);
        post.setExcerpt(content.substring(0, 150) + "...");

        List<Tag> savedTags = new ArrayList<>();
        for (Tag tag : tags) {
            List<Tag> existingTags = tagRepository.findByName(tag.getName());
            if (existingTags.isEmpty()) {
                Tag savedTag = tagRepository.save(tag);
                savedTags.add(savedTag);
            } else {
                savedTags.addAll(existingTags);
            }
        }

        post.setTags(tags);
        postRepository.save(post);
        return post;
    }

    public Page<Post> getPosts(int pageNumber, int pageSize) {
        Pageable pageable = PageRequest.of(pageNumber, pageSize);
        Page<Post> pagePost = this.postRepository.findAll(pageable);
        return pagePost;
    }

    public Page<Post> getPosts(int pageNumber, int pageSize, String query) {
        Pageable pageable = PageRequest.of(pageNumber, 100);
        Page<Post> pagePost = this.postRepository
                .findByTitleContainingIgnoreCaseOrAuthorContainingIgnoreCaseOrContentContainingIgnoreCaseOrTagsNameContainingIgnoreCase(
                        query, query, query, query, pageable);
        return pagePost;
    }

    public Post getPost(int postId) {
        Post post = postRepository.findById(postId);
        return post;
    }

    @Transactional
    public void deletePost(int postId) {
        Post post = postRepository.findById(postId);
        if (post != null) {
            postRepository.delete(post);
        }
    }

    @Transactional
    public Post updatePost(int postId, Post post) {
        List<Tag> tags = post.getTags();
        for (Tag tagName : tags) {
            List<Tag> existingTags = tagRepository.findByName(tagName.getName());
            if (existingTags.isEmpty()) {
                Tag tag = new Tag();
                tag.setName(tagName.getName());
                tagRepository.save(tag);
            }
        }
        postRepository.updatePost(postId, post.getTitle(), post.getExcerpt(), post.getContent(), post.getUpdatedAt());
        return post;
    }

    public Page<Post> sortPosts(int pageNumber, int pageSize, String order) {
        Sort sort;
        if (order.equalsIgnoreCase("descending")) {
            sort = Sort.by(Sort.Direction.DESC, "publishedAt");
        } else {
            sort = Sort.by(Sort.Direction.ASC, "publishedAt");
        }
        Pageable pageable = PageRequest.of(pageNumber, 100, sort);
        return postRepository.findAll(pageable);
    }

    public Page<Post> filterPost(String authorIds, String publishedAtIds, String tagIds, int pageNumber, int pageSize) {
        Pageable pageable = PageRequest.of(pageNumber, 100);
        List<Integer> filterAuthorAndPublishedAtIds = new ArrayList<>();
        List<Integer> filterTagIds = new ArrayList<>();
        String[] splitedAuthorIds = authorIds.split(",");
        String[] splitedPublishedAtIds = publishedAtIds.split(",");
        String[] splitedTagIds = tagIds.split(",");

        if (!authorIds.equals("")) {
            List<Integer> tempAuthorIds = new ArrayList<>();
            for (String splitedAuthorId : splitedAuthorIds) {
                tempAuthorIds.add(Integer.parseInt(splitedAuthorId));
            }
            for (int postId : tempAuthorIds) {
                filterAuthorAndPublishedAtIds
                        .addAll(postRepository.findIdsByAuthor(postRepository.findById(postId)
                                .getAuthor()));
            }
        }
        if (!publishedAtIds.equals("")) {
            List<Integer> tempPublishedAtIds = new ArrayList<>();
            for (String splitedPublishedAtId : splitedPublishedAtIds) {
                tempPublishedAtIds.add(Integer.parseInt(splitedPublishedAtId));
            }
            for (int postId : tempPublishedAtIds) {
                filterAuthorAndPublishedAtIds
                        .addAll(postRepository.findIdsByPublishedAt(postRepository.findById(postId)
                                .getPublishedAt()));
            }
        }
        if (!tagIds.equals("")) {
            List<Integer> tempTagIds = new ArrayList<>();
            for (String splitedTagId : splitedTagIds) {
                tempTagIds.add(Integer.parseInt(splitedTagId));
            }
            for (int tagId : tempTagIds) {
                filterTagIds.addAll(tagRepository.findTagIdsByName(tagRepository.findById(tagId).get().getName()));
            }
        }
        return postRepository.findAllByIdInOrTagsIn(filterAuthorAndPublishedAtIds, filterTagIds, pageable);
    }

    public List<Post> getPosts() {
        return postRepository.findAll();
    }

    public Boolean deletePostById(int postId) {
        return postRepository.deleteById(postId) == null? true: false;
    }

    public Map<String, Integer> getAuthorWithPostId() {
        List<Post> posts = this.getPosts();
        Map<String, Integer> authorWithPostId = new TreeMap<>();
        for (Post post : posts) {
            authorWithPostId.put(post.getAuthor(), post.getId());
        }
        return authorWithPostId;
    }

    public Map<LocalDate, Integer> getPublishedAtDateWithPostId() {
        List<Post> posts = this.getPosts();
        Map<LocalDate, Integer> publishedAtDateWithPostId = new TreeMap<>();
        for (Post post : posts) {
            publishedAtDateWithPostId.put(post.getPublishedAt(), post.getId());
        }
        return publishedAtDateWithPostId;
    }

    public Map<String, Integer> getTagNameWithId() {
        List<Tag> tags = tagRepository.findAll();
        Map<String, Integer> tagNameWithId = new TreeMap<>();
        for (Tag tag : tags) {
            tagNameWithId.put(tag.getName(), tag.getId());
        }
        return tagNameWithId;
    }
}
