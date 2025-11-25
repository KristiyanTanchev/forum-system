package com.team3.forum.controllers.mvc;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/forum")
public class TestMvcController {
    @GetMapping("")
    public String getHomePage() {
        return "HomeView";
    }

    @GetMapping("/folderPage")
    public String getFolderPage() {
        return "FolderView";
    }

    @GetMapping("/login")
    public String getLoginPage() {
        return "LoginView";
    }

    @GetMapping("/register")
    public String getRegisterPage() {
        return "RegisterView";
    }

    @GetMapping("/postPage")
    public String getPostPage() {
        return "PostView";
    }


    @GetMapping("/admin")
    public String getAdminPage() {
        return "AdminView";
    }

    @GetMapping("/createPost")
    public String getCreatePostPage() {
        return "CreatePostView";
    }

    @GetMapping("/EditPost")
    public String getEditPostPage() {
        return "EditPostView";
    }

    @GetMapping("/EditUser")
    public String getEditUserPage() {
        return "EditUserView";
    }

    @GetMapping("/user")
    public String getUserPage() {
        return "UserView";
    }
}
