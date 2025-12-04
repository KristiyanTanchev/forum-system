package com.team3.forum.controllers.mvc;

import com.team3.forum.helpers.PostMapper;
import com.team3.forum.helpers.UserMapper;
import com.team3.forum.models.Post;
import com.team3.forum.models.User;
import com.team3.forum.models.postDtos.PostResponseDto;
import com.team3.forum.models.userDtos.UserUpdateDto;
import com.team3.forum.security.CustomUserDetails;
import com.team3.forum.services.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/profile")
public class ProfileMvcController {
    private final UserService userService;
    private final UserMapper userMapper;
    private final PostMapper postMapper;

    @Autowired
    public ProfileMvcController(UserService userService,
                                UserMapper userMapper,
                                PostMapper postMapper) {
        this.userService = userService;
        this.userMapper = userMapper;
        this.postMapper = postMapper;
    }

    @GetMapping
    public String redirectToOwnProfile(@AuthenticationPrincipal CustomUserDetails userDetails) {
        return "redirect:/profile/" + userDetails.getUsername();
    }

    @GetMapping("/{username}")
    public String viewUserProfile(
            @PathVariable String username,
            @AuthenticationPrincipal CustomUserDetails userDetails,
            Model model) {

        User user = userService.findByUsername(username);

        List<PostResponseDto> postDtos = user.getPosts().stream()
                .map(postMapper::toResponseDto)
                .toList();

        model.addAttribute("user", userMapper.toResponseDto(user));
        model.addAttribute("userStats", userService.getUserStats(user.getId()));
        model.addAttribute("posts", postDtos);
        model.addAttribute("isOwnProfile", userDetails.getUsername().equals(username));
        model.addAttribute("isAdmin", userDetails.isAdmin());

        return "ProfileView";
    }

    @GetMapping("/edit")
    public String showEditProfileForm(
            @AuthenticationPrincipal CustomUserDetails principal,
            Model model) {

        User user = userService.findByUsername(principal.getUsername());
        UserUpdateDto updateDto = userMapper.toUpdateDto(user);

        model.addAttribute("user", userMapper.toResponseDto(user));
        model.addAttribute("userUpdateDto", updateDto);
        return "EditProfileView";
    }

    @PostMapping("/edit")
    public String updateProfile(
            @Valid @ModelAttribute("userUpdateDto") UserUpdateDto userUpdateDto,
            BindingResult bindingResult,
            @AuthenticationPrincipal CustomUserDetails userDetails,
            Model model) {

        if (bindingResult.hasErrors()) {
            User user = userService.findByUsername(userDetails.getUsername());
            model.addAttribute("user", userMapper.toResponseDto(user));
            return "EditProfileView";
        }
        userService.updateUser(userDetails.getId(), userUpdateDto, userDetails.getId());
        return "redirect:/profile/" + userDetails.getUsername() + "?updated=true";
    }
}




