package com.upgrad.quora.service.business;

import com.upgrad.quora.service.dao.UserAuthDao;
import com.upgrad.quora.service.dao.UserDao;
import com.upgrad.quora.service.entity.UserAuthEntity;
import com.upgrad.quora.service.entity.UserEntity;
import com.upgrad.quora.service.exception.AuthorizationFailedException;
import com.upgrad.quora.service.exception.UserNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserAdminBusinessService {

    @Autowired
    private UserDao userDao;

    @Autowired
    private UserAuthDao userAuthDao;


    @Transactional(propagation = Propagation.REQUIRED)
    public UserEntity deleteUser(final String userUuid, final String accessToken) throws
            AuthorizationFailedException, UserNotFoundException
    {
        if(accessToken == null) {
            throw new AuthorizationFailedException("ATHR-001", "User has not signed in");
        }
        UserAuthEntity userAuthTokenEntity = userAuthDao.getUserAuthByToken(accessToken);

        //Check if user has signed-in
        if(userAuthTokenEntity == null)
        {
            throw new AuthorizationFailedException("ATHR-001","User has not signed in");
        }

        //Check if user has signed-out
        if(userAuthTokenEntity.getLogoutAt() != null)
        {
            throw new AuthorizationFailedException("ATHR-002","User is signed out");
        }

        //Check if the user has admin privilege
        String role = userAuthTokenEntity.getUserEntity().getRole();
        if(role.equals("nonadmin"))
        {
            throw new AuthorizationFailedException("ATHR-003","Unauthorized Access, Entered user is not an admin");
        }
        UserEntity userEntity = userDao.getUser(userUuid);

        //Check if user to be deleted is present in repository
        if(userEntity == null)
        {
            throw new UserNotFoundException("USR-001", "User with entered uuid to be deleted does not exist");
        }else{
            userDao.deleteUserEntity(userEntity);
            return userEntity;
        }
    }
}