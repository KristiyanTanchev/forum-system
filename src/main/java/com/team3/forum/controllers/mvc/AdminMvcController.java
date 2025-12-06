package com.team3.forum.controllers.mvc;

import com.team3.forum.models.userDtos.UserPage;
import com.team3.forum.security.CustomUserDetails;
import com.team3.forum.services.CommentService;
import com.team3.forum.services.PostService;
import com.team3.forum.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@Controller
@RequestMapping("/admin")
public class AdminMvcController {
    private final UserService userService;
    private final PostService postService;
    private final CommentService commentService;

    @Autowired
    public AdminMvcController(UserService userService, PostService postService, CommentService commentService) {
        this.userService = userService;
        this.postService = postService;
        this.commentService = commentService;
    }

    @GetMapping
    public String getAdminDashBoard(@RequestParam(required = false) String search,
                                    @RequestParam(required = false) String status,
                                    @RequestParam(required = false, defaultValue = "username") String sort,
                                    @RequestParam(required = false, defaultValue = "asc") String direction,
                                    @RequestParam(required = false, defaultValue = "1") int page, Model model) {

        Map<String, Object> stats = new HashMap<>();
        stats.put("totalUsers", userService.getUsersCount());
        stats.put("totalPosts", postService.getPostsCount());
        stats.put("totalComments", commentService.getCommentCount());
        stats.put("blockedUsers", userService.getBlockedUsersCount());
        model.addAttribute("stats", stats);

        UserPage userPage = userService.getUsersWithFiltersPaginated(page, 10, search, status, sort, direction);
        model.addAttribute("users", userPage.getItems());
        model.addAttribute("pageInfo", userPage);
        model.addAttribute("searchQuery", search);
        model.addAttribute("statusFilter", status);
        model.addAttribute("sortBy", sort);
        model.addAttribute("sortDirection", direction);

        return "AdminView";
    }
    @PostMapping("/users/{id}/block")
    public String blockUser(@PathVariable int id,
                            @AuthenticationPrincipal CustomUserDetails requester) {
        userService.blockUser(id, requester.getId());
        return "redirect:/admin";
    }

    @PostMapping("/users/{id}/unblock")
    public String unblockUser(@PathVariable int id,
                              @AuthenticationPrincipal CustomUserDetails requester) {
        userService.unblockUser(id, requester.getId());
        return "redirect:/admin";
    }

    @PostMapping("/users/{id}/promote-admin")
    public String promoteToAdmin(@PathVariable int id) {
        userService.promoteToAdmin(id);
        return "redirect:/admin";
    }

    @PostMapping("/users/{id}/promote-moderator")
    public String promoteToModerator(@PathVariable int id) {
        userService.promoteToModerator(id);
        return "redirect:/admin";
    }
}

