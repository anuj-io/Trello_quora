package com.upgrad.quora.service.dao;

import com.upgrad.quora.service.entity.QuestionEntity;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Repository
public class QuestionDao {

  @PersistenceContext
  private EntityManager entityManager;

  /**
   *
   * @param questionEntity Object which is to be persisted
   * @return same question entity as passed in params
   */
  public QuestionEntity createQuestion(QuestionEntity questionEntity) {
    entityManager.persist(questionEntity);
    return questionEntity;
  }

  /**
   * Get list of questions created by user
   * @param userUuid for User question created by whom are to be returned
   * @return List of question entities
   */
  public List<QuestionEntity> getQuestionsByUserId(String userUuid) {
    try {
      return entityManager.createNamedQuery("questionsByUserId", QuestionEntity.class).setParameter("userUuid", userUuid).getResultList();
    } catch (NoResultException nre) {
      return null;
    }
  }

  /**
   * Get all the questions in DB
   * @return List of question entities
   */
  public List<QuestionEntity> getAllQuestions() {
    try {
      return entityManager.createNamedQuery("questions", QuestionEntity.class).getResultList();
    } catch (NoResultException nre) {
      return null;
    }
  }

  /**
   * Get Question by its ID
   * @param questionUuid for question to be fetched
   * @return Question Entity
   */
  public QuestionEntity getQuestionById(String questionUuid) {
    try {
      return entityManager.createNamedQuery("questionById", QuestionEntity.class).setParameter("uuid", questionUuid).getSingleResult();
    } catch (NoResultException nre) {
      return null;
    }
  }

  /**
   * Delete Question by its id
   * @param questionUuid for question to be deleted
   */
  public void deleteQuestion(String questionUuid) {
      entityManager.createNamedQuery("deleteQuestionById").setParameter("uuid", questionUuid).executeUpdate();
  }

  /**
   * Updates the Question
   * @param questionEntity to be updated
   */
  public void updateQuestion(QuestionEntity questionEntity) {entityManager.merge(questionEntity);}
}
