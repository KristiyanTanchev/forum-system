package com.team3.forum.helpers;


import com.team3.forum.exceptions.AuthorizationException;
import com.team3.forum.models.User;
import com.team3.forum.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;

@Component
public class TempAuthenticationHelper {
    private static final String AUTHORIZATION_HEADER_NAME = "Authorization";
    private static final String INVALID_AUTHENTICATION_ERROR = "Invalid authentication.";

    private final UserService userService;

    @Autowired
    public TempAuthenticationHelper(UserService userService) {
        this.userService = userService;
    }

    //Searches for userId in Authorization. To be replaced with a proper Authorization
    public User tryGetUser(HttpHeaders headers) {
        if (!headers.containsKey(AUTHORIZATION_HEADER_NAME)) {
            throw new AuthorizationException(INVALID_AUTHENTICATION_ERROR);
        }
        String userInfo = headers.getFirst(AUTHORIZATION_HEADER_NAME);
        if (userInfo == null){
            throw new AuthorizationException(INVALID_AUTHENTICATION_ERROR);
        }
        int userId;
        try{
            userId = Integer.parseInt(userInfo);
        } catch (NumberFormatException e) {
            throw new AuthorizationException(INVALID_AUTHENTICATION_ERROR);
        }
        return userService.findById(userId);
    }
}
