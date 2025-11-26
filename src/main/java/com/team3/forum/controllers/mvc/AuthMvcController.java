package com.team3.forum.controllers.mvc;

import com.team3.forum.models.userDtos.UserCreateDto;
import com.team3.forum.security.JwtTokenProvider;
import com.team3.forum.services.UserService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/auth")
public class AuthMvcController {
    private final UserService userService;
    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider jwtTokenProvider;

    @Autowired
    public AuthMvcController(UserService userService,
                             AuthenticationManager authenticationManager,
                             JwtTokenProvider jwtTokenProvider) {
        this.userService = userService;
        this.authenticationManager = authenticationManager;
        this.jwtTokenProvider = jwtTokenProvider;
    }

    @GetMapping("/login")
    public String getLoginPage() {
        return "LoginView";
    }

    @GetMapping("/register")
    public String getRegisterPage(Model model) {
        model.addAttribute("userCreateDto", new UserCreateDto());
        return "RegisterView";
    }

    @PostMapping("/login")
    public String login(@RequestParam String username,
                        @RequestParam String password,
                        @RequestParam(required = false) String rememberMe,
                        HttpServletResponse response) {

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(username, password)
        );
        String jwt = jwtTokenProvider.generateToken(authentication);

        Cookie jwtCookie = new Cookie("jwt", jwt);
        jwtCookie.setHttpOnly(true);
        jwtCookie.setSecure(false);
        jwtCookie.setPath("/");

        if (rememberMe != null) {
            jwtCookie.setMaxAge(30 * 24 * 60 * 60);  // 30 days
        } else {
            jwtCookie.setMaxAge(24 * 60 * 60);  // 1 day
        }

        response.addCookie(jwtCookie);
        return "redirect:/forum";
    }

    @PostMapping("/register")
    public String registerUser(@Valid @ModelAttribute("userCreateDto") UserCreateDto userCreateDto,
                               BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return "RegisterView";
        }
        userService.createUser(userCreateDto);
        return "redirect:/auth/login?registered=true";
    }

}
