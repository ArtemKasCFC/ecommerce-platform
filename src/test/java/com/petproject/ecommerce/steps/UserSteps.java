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
import com.petproject.ecommerce.user.enums.UserStatuses;

import java.util.HashMap;
import java.util.Map;

public class UserSteps {

    public static Map<String, String> createAdmin() {

        UserCreateRequest body = UserFactory.defaultUser();

        RegistrationResponse response = UserApi.createUser(body, RegistrationResponse.class, 201);

        UsersDb.updateRoleById(response.getUser().getId(), Roles.ADMIN);

        LoginRequest loginRequest = new LoginRequest(body.getEmail(), body.getPassword());

        String token = AuthApi.login(loginRequest, LoginResponse.class, 200).getToken();

        return new HashMap<>() {{
            put("email", body.getEmail());
            put("password", body.getPassword());
            put("token", token);
        }};
    }

    public static Map<String, String> createDisabledUser() {

        UserCreateRequest body = UserFactory.defaultUser();

        RegistrationResponse response = UserApi.createUser(body, RegistrationResponse.class, 201);

        UsersDb.updateStatusById(response.getUser().getId(), UserStatuses.DISABLED);

        return new HashMap<>() {{
            put("email", body.getEmail());
            put("password", body.getPassword());
        }};
    }


    public static Map<String, String> createUser() {

        UserCreateRequest body = UserFactory.defaultUser();

        RegistrationResponse response = UserApi.createUser(body, RegistrationResponse.class, 201);

        return new HashMap<>() {{
            put("email", body.getEmail());
            put("password", body.getPassword());
            put("token", response.getToken());
        }};
    }
}
