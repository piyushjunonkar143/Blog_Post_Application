package com.mountblue.piyush.controller;

import com.mountblue.piyush.dao.TagRepository;
import com.mountblue.piyush.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.mountblue.piyush.entity.Post;
import com.mountblue.piyush.service.PostService;

@Controller
public class HomeController {
    @Autowired
    private PostService postService;

    @Autowired
    private UserService userService;

    @GetMapping("/")
    public String getHome(Model model,
                          @RequestParam(value = "search", defaultValue = "", required = false) String search,
                          @RequestParam(value = "authorId", defaultValue = "", required = false) String authorIds,
                          @RequestParam(value = "publishedAtId", defaultValue = "", required = false) String publishedAtIds,
                          @RequestParam(value = "tagId", defaultValue = "", required = false) String tagIds,
                          @RequestParam(value = "order", defaultValue = "", required = false) String order,
                          @RequestParam(value = "start", defaultValue = "0", required = false) int start,
                          @RequestParam(value = "limit", defaultValue = "10", required = false) int limit) {

        Page<Post> posts;
        start = start/10;
        if (!search.equals("")) {
            posts = postService.getPosts(start, limit, search);
        } else if ((!authorIds.equals("")) || !(publishedAtIds.equals("")) || !(tagIds.equals(""))) {
            posts = postService.filterPost(authorIds, publishedAtIds, tagIds, start, limit);
        } else {
            posts = postService.getPosts(start, limit);
        }

        if (!order.equals("")) {
            posts = postService.sortPosts(start, limit, order);
        }
        model.addAttribute("authors", postService.getAuthorWithPostId());
        model.addAttribute("publishedAtDates", postService.getPublishedAtDateWithPostId());
        model.addAttribute("tags", postService.getTagNameWithId());
        model.addAttribute("posts", posts);
        model.addAttribute("currentPage", start);
        model.addAttribute("totalPages", posts.getTotalPages());
        return "home";
    }
}
