import RegistrationPage from "../pages/registrationPage.js";
import {defaultUser} from "../factories/userFactory.js";
import {ROUTES} from "../constants/routes.js";
import {VALIDATION_MESSAGES} from "../constants/validationMessages.js";
import Navbar from "../pages/navbar.js";

const registrationPage = new RegistrationPage();
const navbar = new Navbar();

describe('Registration', () => {
    let user;

    beforeEach(() => {
        user = defaultUser();
        registrationPage.visit();
    })

    it('should register a user with valid data', () => {
        cy.intercept('POST', '**/users').as('registerRequest');

        registrationPage.register(user);

        cy.wait('@registerRequest').then(interception => {
            expect(interception.response.statusCode).eq(201);
            expect(interception.response.body).to.have.property('token').that.is.a('string').and.not.empty;
        });

        cy.window().its('localStorage').invoke('getItem', 'token')
            .should('be.a', 'string')
            .and("not.be.empty");

        cy.location('pathname').should('eq', ROUTES.HOME);

        navbar.validateAuthState(true);
    });

    it('should not register a user with empty name', () => {
        user.name = '';
        registrationPage.register(user);
        registrationPage.validateFieldError('name', VALIDATION_MESSAGES.NAME_REQUIRED);

        cy.location('pathname').should('eq', ROUTES.REGISTER);

        navbar.validateAuthState(false);
    });

    it('should not register a user with a name exceeding 100 characters', () => {
        user.name = 'A'.repeat(101);
        registrationPage.register(user);
        registrationPage.validateFieldError('name', VALIDATION_MESSAGES.NAME_TOO_LONG);

        cy.location('pathname').should('eq', ROUTES.REGISTER);

        navbar.validateAuthState(false);
    });

    it('should not register a user with empty email', () => {
        user.email = '';
        registrationPage.register(user);
        registrationPage.validateFieldError('email', VALIDATION_MESSAGES.EMAIL_REQUIRED);

        cy.location('pathname').should('eq', ROUTES.REGISTER);

        navbar.validateAuthState(false);
    });

    it('should not register a user with a duplicate email', () => {
        registrationPage.register(user);

        cy.contains('button', 'Logout').click();
        cy.contains('a', 'Register').click();

        cy.intercept('POST', '**/users').as('registerRequest');

        registrationPage.register(user);

        cy.wait('@registerRequest').then(interception => {
            expect(interception.response.statusCode).eq(409);
            expect(interception.response.body).to.have.property('status').eq(409);
            expect(interception.response.body).to.have.property('message').eq(VALIDATION_MESSAGES.EMAIL_ALREADY_EXISTS);
        });

        registrationPage.validateFieldError('email', VALIDATION_MESSAGES.EMAIL_ALREADY_EXISTS);

        cy.location('pathname').should('eq', ROUTES.REGISTER);

        navbar.validateAuthState(false);
    });

    ["invalid-email", "test@", "@gmail.com", "test@gmail", "test@gmail."].forEach(email => {
        it(`should not register a user with an invalid email format: ${email}`, () => {
            user.email = email;
            registrationPage.register(user);

            registrationPage.validateFieldError('email', VALIDATION_MESSAGES.EMAIL_INVALID);

            cy.location('pathname').should('eq', ROUTES.REGISTER);

            navbar.validateAuthState(false);
        });
    })


    it('should not register a user with empty password', () => {
        user.password = '';
        registrationPage.register(user);

        registrationPage.validateFieldError('password', VALIDATION_MESSAGES.PASSWORD_REQUIRED);

        cy.location('pathname').should('eq', ROUTES.REGISTER);

        navbar.validateAuthState(false);
    });

    [
        {password: 'Pass!23', message: VALIDATION_MESSAGES.PASSWORD_LENGTH_ERROR},
        {password: 'Pa!23'.repeat(13), message: VALIDATION_MESSAGES.PASSWORD_LENGTH_ERROR},
        {password: 'pass!123', message: VALIDATION_MESSAGES.PASSWORD_UPPERCASE_REQUIRED},
        {password: 'PASS!123', message: VALIDATION_MESSAGES.PASSWORD_LOWERCASE_REQUIRED},
        {password: 'Pass!!!!', message: VALIDATION_MESSAGES.PASSWORD_DIGIT_REQUIRED},
        {password: 'Pass1234', message: VALIDATION_MESSAGES.PASSWORD_SPEC_CHAR_REQUIRED}
    ].forEach(({password, message}) => {
        it(`should not register a user with an invalid password: ${message}`, () => {
            user.password = password;
            registrationPage.register(user);

            registrationPage.validateFieldError('password', message);

            cy.location('pathname').should('eq', ROUTES.REGISTER);

            navbar.validateAuthState(false);
        });
    })

    it('should clear validation errors when correcting fields', () => {
        registrationPage.clickCreateAccountBtn();

        registrationPage.validateFieldError('name', VALIDATION_MESSAGES.NAME_REQUIRED);
        registrationPage.validateFieldError('email', VALIDATION_MESSAGES.EMAIL_REQUIRED);
        registrationPage.validateFieldError('password', VALIDATION_MESSAGES.PASSWORD_REQUIRED);

        cy.intercept('POST', '**/users').as('registerRequest');

        registrationPage.fillInName(user.name);
        cy.get('#name ~ .invalid-feedback').should('not.exist');
        cy.get('#name').should("not.have.class", 'is-invalid');


        registrationPage.fillInEmail(user.email);
        cy.get('#email ~ .invalid-feedback').should('not.exist');
        cy.get('#email').should("not.have.class", 'is-invalid');


        registrationPage.fillInPassword(user.password);
        cy.get('#password ~ .invalid-feedback').should('not.exist');
        cy.get('#password').should("not.have.class", 'is-invalid');

        registrationPage.clickCreateAccountBtn();


        cy.wait('@registerRequest').then(interception => {
            expect(interception.response.statusCode).eq(201);
            expect(interception.response.body).to.have.property('token').that.is.a('string').and.not.empty;
        });

        cy.window().its('localStorage').invoke('getItem', 'token')
            .should('be.a', 'string')
            .and("not.be.empty");

        cy.location('pathname').should('eq', ROUTES.HOME);

        navbar.validateAuthState(true);
    });
});