package com.team3.forum.exceptions;

import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@ControllerAdvice
public class MvcExceptionHandler {

    @ExceptionHandler(EntityNotFoundException.class)
    public ModelAndView handleEntityNotFound(EntityNotFoundException e) {
        ModelAndView mav = new ModelAndView("ErrorView404");
        mav.addObject("message", e.getMessage());
        return mav;
    }

    @ExceptionHandler(AuthorizationException.class)
    public ModelAndView handleAuthorizationException(AuthorizationException e) {
        ModelAndView mav = new ModelAndView("ErrorView403");
        mav.addObject("message", e.getMessage());
        return mav;
    }

    @ExceptionHandler(DuplicateEntityException.class)
    public String handleDuplicateEntityException(DuplicateEntityException e,
                                                       RedirectAttributes redirectAttributes) {
      redirectAttributes.addFlashAttribute("message", e.getMessage());
        return "redirect:/auth/register";
    }

    @ExceptionHandler(BadCredentialsException.class)
    public String handleBadCredentialsException(BadCredentialsException e,
                                                RedirectAttributes redirectAttributes) {
        redirectAttributes.addFlashAttribute("error", "Invalid username or password");
        return "redirect:/auth/login";
    }

    @ExceptionHandler(UsernameNotFoundException.class)
    public String handleUsernameNotFoundException(UsernameNotFoundException e,
                                                  RedirectAttributes redirectAttributes) {
        redirectAttributes.addFlashAttribute("error", "Invalid username or password");
        return "redirect:/auth/login";
    }

    @ExceptionHandler(LockedException.class)
    public String handleLockedException(LockedException e,
                                        RedirectAttributes redirectAttributes) {
        redirectAttributes.addFlashAttribute("error", "Your account has been blocked.");
        return "redirect:/auth/login";
    }

    @ExceptionHandler(Exception.class)
    public ModelAndView handleGenericException(Exception e) {
        ModelAndView mav = new ModelAndView("ErrorView500");
        mav.addObject("message", "An unexpected error occurred. Please try again.");
        mav.addObject("details", e.getMessage());
        return mav;
    }




}
