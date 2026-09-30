package com.petproject.ecommerce.steps;

import com.petproject.ecommerce.api.AuthApi;
import com.petproject.ecommerce.api.UserApi;
import com.petproject.ecommerce.auth.dto.request.LoginRequest;
import com.petproject.ecommerce.auth.dto.response.LoginResponse;
import com.petproject.ecommerce.database.UsersDb;
import com.petproject.ecommerce.factories.UserFactory;
import com.petproject.ecommerce.user.dto.request.UserCreateRequest;
import com.petproject.ecommerce.user.dto.response.RegistrationResponse;
import com.petproject.ecommerce.user.enums.Roles;

public class UserSteps {

    public static String createAdminToken() {

        UserCreateRequest body = UserFactory.defaultUser();

        RegistrationResponse response = UserApi.createUser(body, RegistrationResponse.class, 201);

        UsersDb.updateRoleById(response.getUser().getId(), Roles.ADMIN);

        LoginRequest loginRequest = new LoginRequest(body.getEmail(), body.getPassword());

        LoginResponse login = AuthApi.login(loginRequest, LoginResponse.class, 200);

        return login.getToken();
    }


    public static String createUserToken() {

        UserCreateRequest body = UserFactory.defaultUser();

        RegistrationResponse response = UserApi.createUser(body, RegistrationResponse.class, 201);
        
        return response.getToken();
    }
}
