package com.upgrad.quora.service.dao;

import com.upgrad.quora.service.exception.SignUpRestrictedException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import com.upgrad.quora.service.entity.UserEntity;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;

@Repository
public class UserDao {
  @PersistenceContext
  private EntityManager entityManager;

  /**
   * Persists user entity
   * @param userEntity object to be persisted
   * @return passed down user entity
   */
  public UserEntity createUser(UserEntity userEntity){
    entityManager.persist(userEntity);
    return userEntity;
  }

  /**
   * Get user entity from id
   * @param userUuid id of the user
   * @return nullable entity of user type
   */
  public UserEntity getUser(final String userUuid) {
    try {
      return entityManager.createNamedQuery("userByUuid", UserEntity.class).setParameter("uuid", userUuid).getSingleResult();
    } catch (NoResultException nre) {
      return null;
    }
  }

  /**
   * This methods gets the user details based on the username passed.
   *
   * @param userName username of the user whose information is to be fetched.
   * @return null if the user with given username doesn't exist in DB.
   */
  public UserEntity getUserByUserName(final String userName) {
    try {
      return entityManager.createNamedQuery("userByUserName", UserEntity.class).setParameter("userName", userName).getSingleResult();
    } catch (NoResultException nre) {
      return null;
    }
  }

  /**
   *
   */
  public UserEntity getUserByEmailId(final String email) {
    try {
      return entityManager.createNamedQuery("userByEmail", UserEntity.class).setParameter("email", email).getSingleResult();
    } catch (NoResultException nre) {
      return null;
    }
  }

  /**
   * Updates the user entity
   * @param updatedUserEntity
   */
  public void updateUserEntity(final UserEntity updatedUserEntity) {
    entityManager.merge(updatedUserEntity);
  }

  /**
   * Deletes the given user entity
   * @param userEntity to be deleted
   */
  public void deleteUserEntity(final UserEntity userEntity) {
    entityManager.remove(userEntity);
  }
}
