package com.upgrad.quora.api.controller;

import com.upgrad.quora.api.model.SigninResponse;
import com.upgrad.quora.api.model.SignoutResponse;
import com.upgrad.quora.api.model.SignupUserRequest;
import com.upgrad.quora.api.model.SignupUserResponse;
import com.upgrad.quora.service.business.SignupBusinessService;
import com.upgrad.quora.service.business.UserAuthenticationService;
import com.upgrad.quora.service.entity.UserAuthEntity;
import com.upgrad.quora.service.entity.UserEntity;
import com.upgrad.quora.service.exception.AuthenticationFailedException;
import com.upgrad.quora.service.exception.SignOutRestrictedException;
import com.upgrad.quora.service.exception.SignUpRestrictedException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.Base64;
import java.util.UUID;

@RestController
@RequestMapping("/user")
public class UserController {
  @Autowired
  private SignupBusinessService signupBusinessService;

  @Autowired
  private UserAuthenticationService userAuthService;

  /**
   * Endpoint for signing up new user
   * @param signupUserRequest
   * @return
   * @throws SignUpRestrictedException
   */
  @RequestMapping(method = RequestMethod.POST, path = "/signup", consumes = MediaType.APPLICATION_JSON_UTF8_VALUE, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
  public ResponseEntity<SignupUserResponse> signup(final SignupUserRequest signupUserRequest) throws SignUpRestrictedException {
    final UserEntity userEntity = new UserEntity();
    userEntity.setUuid(UUID.randomUUID().toString());
    userEntity.setFirstName(signupUserRequest.getFirstName());
    userEntity.setLastName(signupUserRequest.getLastName());
    userEntity.setEmail(signupUserRequest.getEmailAddress());
    userEntity.setPassword(signupUserRequest.getPassword());
    userEntity.setContactNumber(signupUserRequest.getContactNumber());
    userEntity.setAboutMe(signupUserRequest.getAboutMe());
    userEntity.setCountry(signupUserRequest.getCountry());
    userEntity.setDob(signupUserRequest.getDob());
    userEntity.setRole();
    userEntity.setUserName(signupUserRequest.getUserName());



    final UserEntity createdUserEntity = signupBusinessService.signup(userEntity);
    SignupUserResponse signupUserResponse = new SignupUserResponse();
    signupUserResponse.setId(createdUserEntity.getUuid());
    signupUserResponse.setStatus("USER SUCCESSFULLY REGISTERED");
    return new ResponseEntity<SignupUserResponse>(signupUserResponse, HttpStatus.CREATED);
  }

  /**
   * End point for signing in the user with new session
   * @param authorization
   * @return
   * @throws AuthenticationFailedException
   */
  @RequestMapping(method = RequestMethod.POST, path = "/signin", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
  public ResponseEntity<SigninResponse> signin(@RequestHeader("authorization") final String authorization) throws AuthenticationFailedException {

    byte[] decode = Base64.getDecoder().decode(authorization);

    String decodedText = new String(decode);
    decodedText=decodedText.split("Basic ")[1];
    String[] decodedArray = decodedText.split(":");
    UserAuthEntity userAuthEntity = userAuthService.signin(decodedArray[0], decodedArray[1]);

    HttpHeaders headers = new HttpHeaders();
    headers.add("access-token", userAuthEntity.getAccessToken());

    SigninResponse signinResponse = new SigninResponse();
    signinResponse.setId(userAuthEntity.getUserEntity().getUuid());
    signinResponse.setMessage("SIGNED IN SUCCESSFULLY");

    return new ResponseEntity<SigninResponse>(signinResponse, headers, HttpStatus.OK);
  }
  @RequestMapping(method = RequestMethod.POST, path = "/signout", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
  public ResponseEntity<SignoutResponse> signout(@RequestHeader("authorization") final String accessToken) throws SignOutRestrictedException {
    UserEntity userEntity = userAuthService.signout(accessToken);
    SignoutResponse signoutResponse = new SignoutResponse().id(userEntity.getUuid()).message("SIGNED OUT SUCCESSFULLY");
    return new ResponseEntity<SignoutResponse>(signoutResponse, HttpStatus.OK);

  }

}
