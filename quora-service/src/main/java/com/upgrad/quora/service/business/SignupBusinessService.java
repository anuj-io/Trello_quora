package com.upgrad.quora.service.business;

import com.upgrad.quora.service.dao.UserDao;
import com.upgrad.quora.service.entity.UserEntity;
import com.upgrad.quora.service.exception.SignUpRestrictedException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
public class SignupBusinessService {
  @Autowired
  private UserDao userDao;

  @Autowired
  private PasswordCryptographyProvider cryptographyProvider;

  /**
   * Create the user in DB
   * @param userEntity to be created
   * @return Created userEntity
   * @throws SignUpRestrictedException
   */
  @Transactional(propagation = Propagation.REQUIRED)
  public UserEntity signup(UserEntity userEntity) throws SignUpRestrictedException{
    if (userDao.getUserByUserName(userEntity.getUserName()) != null) {
      throw new SignUpRestrictedException("SGR-001", "Try any other Username, this Username has already been taken");
    }
    if (userDao.getUserByEmailId(userEntity.getEmail()) !=null) {
      throw new SignUpRestrictedException("SGR-002", "This user has already been registered, try with any other emailId");
    }
    String password = userEntity.getPassword();
    if (password == null) {
      userEntity.setPassword("password");
    }
    String[] encryptedText = cryptographyProvider.encrypt(userEntity.getPassword());
    userEntity.setSalt(encryptedText[0]);
    userEntity.setPassword(encryptedText[1]);
    return userDao.createUser(userEntity);
  }
}
