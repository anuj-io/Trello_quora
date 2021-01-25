package com.upgrad.quora.service.business;

import com.upgrad.quora.service.dao.QuestionDao;
import com.upgrad.quora.service.dao.UserAuthDao;
import com.upgrad.quora.service.entity.QuestionEntity;
import com.upgrad.quora.service.entity.UserAuthEntity;
import com.upgrad.quora.service.exception.AuthorizationFailedException;
import com.upgrad.quora.service.exception.InvalidQuestionException;
import com.upgrad.quora.service.exception.UserNotFoundException;
import java.time.ZonedDateTime;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
public class QuestionBusinessService {
  @Autowired
  private QuestionDao questionDao;

  @Autowired
  private UserAuthDao userAuthDao;

  @Transactional(propagation = Propagation.REQUIRED)
  public QuestionEntity createQuestion(final String accessToken, final QuestionEntity questionEntity)
      throws AuthorizationFailedException {
    if(accessToken == null) {
      throw new AuthorizationFailedException("ATHR-001", "User has not signed in");
    }
    UserAuthEntity userAuthTokenEntity = userAuthDao.getUserAuthByToken(accessToken);
      if(userAuthTokenEntity ==null) {
        throw new AuthorizationFailedException("ATHR-001", "User has not signed in");
      }
      if(userAuthTokenEntity.getLogoutAt() != null) {
        throw new AuthorizationFailedException("ATHR-002", "User is signed out.Sign in first to post a question");
      }
      questionEntity.setUserEntity(userAuthTokenEntity.getUserEntity());
      return questionDao.createQuestion(questionEntity);
  }

  public List<QuestionEntity> getAllQuestions(final String accessToken)
      throws AuthorizationFailedException {
    if(accessToken == null) {
      throw new AuthorizationFailedException("ATHR-001", "User has not signed in");
    }
    UserAuthEntity userAuthTokenEntity = userAuthDao.getUserAuthByToken(accessToken);
    if(userAuthTokenEntity ==null) {
      throw new AuthorizationFailedException("ATHR-001", "User has not signed in");
    }
    if(userAuthTokenEntity.getLogoutAt() != null) {
      throw new AuthorizationFailedException("ATHR-002", "User is signed out.Sign in first to get all questions");
    }
    return questionDao.getAllQuestions();
  }

  public List<QuestionEntity> getAllQuestionsByUserId(final String accessToken, final String user_uuid)
      throws AuthorizationFailedException, UserNotFoundException {
    if(accessToken == null) {
      throw new AuthorizationFailedException("ATHR-001", "User has not signed in");
    }
    UserAuthEntity userAuthTokenEntity = userAuthDao.getUserAuthByToken(accessToken);
    if(userAuthTokenEntity ==null) {
      throw new AuthorizationFailedException("ATHR-001", "User has not signed in");
    }
    if(userAuthTokenEntity.getLogoutAt() != null) {
      throw new AuthorizationFailedException("ATHR-002", "User is signed out.Sign in first to get all questions posted by a specific user");
    }
    List<QuestionEntity> questionsByUuid = questionDao.getQuestionsByUserId(user_uuid);
    if(questionsByUuid == null || questionsByUuid.size() == 0) {
      throw new UserNotFoundException("USR-001", "User with entered uuid whose question details are to be seen does not exist");
    }
    return questionsByUuid;
  }

  @Transactional(propagation = Propagation.REQUIRED)
  public QuestionEntity deleteQuestion(final String accessToken, final String questionUuid)
      throws AuthorizationFailedException, InvalidQuestionException {
    if(accessToken == null) {
      throw new AuthorizationFailedException("ATHR-001", "User has not signed in");
    }
    UserAuthEntity userAuthTokenEntity = userAuthDao.getUserAuthByToken(accessToken);
    if(userAuthTokenEntity ==null) {
      throw new AuthorizationFailedException("ATHR-001", "User has not signed in");
    }
    if(userAuthTokenEntity.getLogoutAt() != null) {
      throw new AuthorizationFailedException("ATHR-002", "User is signed out.Sign in first to delete a question");
    }

    QuestionEntity questionEntity = questionDao.getQuestionById(questionUuid);

    if(questionEntity == null) {
      throw new InvalidQuestionException("QUES-001", "Entered question uuid does not exist");
    }

    if(userAuthTokenEntity.getUserEntity().getId() != questionEntity.getUserEntity().getId()) {
      throw new AuthorizationFailedException("ATHR-003", "Only the question owner or admin can delete the question");
    }
    questionDao.deleteQuestion(questionUuid);
    return questionEntity;
  }

  @Transactional(propagation = Propagation.REQUIRED)
  public QuestionEntity updateQuestionEntity(final String accessToken, final String questionUuid, final String newContent)
      throws AuthorizationFailedException, InvalidQuestionException {
    if(accessToken == null) {
      throw new AuthorizationFailedException("ATHR-001", "User has not signed in");
    }
    UserAuthEntity userAuthTokenEntity = userAuthDao.getUserAuthByToken(accessToken);
    if(userAuthTokenEntity ==null) {
      throw new AuthorizationFailedException("ATHR-001", "User has not signed in");
    }
    if(userAuthTokenEntity.getLogoutAt() != null) {
      throw new AuthorizationFailedException("ATHR-002", "User is signed out.Sign in first to edit the question");
    }

    QuestionEntity questionEntity = questionDao.getQuestionById(questionUuid);

    if(questionEntity == null) {
      throw new InvalidQuestionException("QUES-001", "Entered question uuid does not exist");
    }

    if(userAuthTokenEntity.getUserEntity().getId() != questionEntity.getUserEntity().getId()) {
      throw new AuthorizationFailedException("ATHR-003", "Only the question owner can edit the question");
    }
    questionEntity.setContent(newContent);
    questionDao.updateQuestion(questionEntity);
    return questionEntity;
  }
}
