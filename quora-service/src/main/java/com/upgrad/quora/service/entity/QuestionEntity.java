package com.upgrad.quora.service.entity;

import java.io.Serializable;
import java.time.ZonedDateTime;
import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

@Entity
@Table(name = "question")
@NamedQueries({
    @NamedQuery(name = "questionsByUserId", query = "select q from QuestionEntity q where q.userEntity.uuid = :userUuid"),
    @NamedQuery(name = "questions", query = "select q from QuestionEntity q"),
    @NamedQuery(name = "questionById", query = "select q from QuestionEntity q where q.uuid = :uuid"),
    @NamedQuery(name = "deleteQuestionById", query = "delete from QuestionEntity q where q.uuid = :uuid")
})

/*
This model class maps to the Question table in DB
 */
public class QuestionEntity  implements Serializable {

  @Id
  @Column(name = "id")
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Integer id;

  @Column(name = "uuid")
  @Size(max = 200)
  private String uuid;

  @Column(name = "date")
  @NotNull
  private ZonedDateTime date;

  @Column(name = "content")
  @NotNull
  @Size(max = 500)
  private String content;


  @ManyToOne
  @OnDelete(action = OnDeleteAction.CASCADE)
  @JoinColumn(name = "user_id")
  private UserEntity userEntity;

  public Integer getId() {
    return id;
  }

  public String getUuid() {
    return uuid;
  }

  public ZonedDateTime getDate() {
    return date;
  }

  public String getContent() {
    return content;
  }

  public UserEntity getUserEntity() {
    return userEntity;
  }

  public void setId(Integer id) {
    this.id = id;
  }

  public void setUuid(String uuid) {
    this.uuid = uuid;
  }

  public void setDate(ZonedDateTime date) {
    this.date = date;
  }

  public void setContent(String content) {
    this.content = content;
  }

  public void setUserEntity(UserEntity userEntity) {
    this.userEntity = userEntity;
  }

  @Override
  public boolean equals(Object obj) {
    return new EqualsBuilder().append(this, obj).isEquals();
  }

  @Override
  public int hashCode() {
    return new HashCodeBuilder().append(this).hashCode();
  }

  @Override
  public String toString() {
    return ToStringBuilder.reflectionToString(this, ToStringStyle.MULTI_LINE_STYLE);
  }


}
