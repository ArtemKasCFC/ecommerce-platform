package com.petproject.ecommerce.constants;

public final class ValidationMessages {

    // Users
    public static final String NAME_REQUIRED = "Name is required";
    public static final String NAME_TOO_LONG = "Name must not exceed 100 characters";

    public static final String EMAIL_REQUIRED = "Email is required";
    public static final String EMAIL_INVALID = "Invalid email";
    public static final String EMAIL_ALREADY_EXISTS = "User with this email already exists";

    public static final String PASSWORD_REQUIRED = "Password is required";
    public static final String PASSWORD_LENGTH_ERROR = "Password's length must be between 8 and 64 characters";
    public static final String PASSWORD_UPPERCASE_REQUIRED = "Password must contain an uppercase letter";
    public static final String PASSWORD_LOWERCASE_REQUIRED = "Password must contain a lowercase letter";
    public static final String PASSWORD_DIGIT_REQUIRED = "Password must contain a digit";
    public static final String PASSWORD_SPEC_CHAR_REQUIRED = "Password must contain a special character";


    // Products
    public static final String ID_MUST_BE_POSITIVE = "Id must be positive";

    public static final String TITLE_REQUIRED = "Title must not be blank";
    public static final String TITLE_TOO_LONG = "Title must not exceed 50 characters";

    public static final String PRICE_REQUIRED = "Price must not be null";
    public static final String PRICE_MUST_BE_POSITIVE = "Price must be positive";
    public static final String PRICE_TOO_HIGH = "Price must not exceed 10000";

    public static final String PRODUCT_NOT_FOUND = "Product with id %d not found";
}
