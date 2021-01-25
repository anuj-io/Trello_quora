package com.upgrad.quora.service.business;

import com.upgrad.quora.service.dao.UserAuthDao;
import com.upgrad.quora.service.dao.UserDao;
import com.upgrad.quora.service.entity.UserAuthEntity;
//import com.upgrad.quora.service.entity.UserAuthTokenEntity;
import com.upgrad.quora.service.entity.UserEntity;
import com.upgrad.quora.service.exception.AuthorizationFailedException;
import com.upgrad.quora.service.exception.UserNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserCommonService {

        @Autowired
        UserAuthDao userAuthDao;

        @Autowired
        UserDao userDao;

        public void checkIfTokenIsValid(String accessToken) throws AuthorizationFailedException {
            UserAuthEntity userAuthEntity = userAuthDao.getUserAuthByToken(accessToken);
            if (userAuthEntity == null) {
                throw new AuthorizationFailedException("ATHR-001", "User has not signed in");
            }
            if (userAuthEntity.getLogoutAt() != null) {
                throw new AuthorizationFailedException("ATHR-002", "User is signed out.Sign in first to get user details");
            }
        }

        public UserEntity getUserById(final String userId) throws UserNotFoundException {
            UserEntity userEntity = userDao.getUserById(userId);
            if (userEntity == null) {
                throw new UserNotFoundException("USR-001", "User with entered uuid does not exist");
            }
            return userEntity;
        }


    }

