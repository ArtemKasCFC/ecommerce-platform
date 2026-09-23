import RegistrationPage from "../pages/registrationPage.js";
import {defaultUser} from "../factories/userFactory.js";
import {ROUTES} from "../constants/routes.js";
import {VALIDATION_MESSAGES} from "../constants/validationMessages.js";

const registrationPage = new RegistrationPage();

describe('Registration', () => {

    it('should register a user with valid data', () => {
        const user = defaultUser();

        registrationPage.visit();
        registrationPage.fillInRegisterForm(user);
        cy.location('pathname').should('eq', ROUTES.HOME);
    });

    it('should not register a user with empty name', () => {
        const user = defaultUser();
        user.name = ' ';

        registrationPage.visit();
        registrationPage.fillInRegisterForm(user);

        registrationPage.getError('name').should('be.visible').and('have.text', VALIDATION_MESSAGES.NAME_REQUIRED);
        cy.get('#name').should("have.class", 'is-invalid');
    });

    it('should not register a user with a name exceeding 100 characters', () => {
        const user = defaultUser();
        user.name = 'A'.repeat(101);

        registrationPage.visit();
        registrationPage.fillInRegisterForm(user);

        registrationPage.getError('name').should('be.visible').and('have.text', VALIDATION_MESSAGES.NAME_TOO_LONG);
        cy.get('#name').should("have.class", 'is-invalid');
    });

    it('should not register a user with empty email', () => {
        const user = defaultUser();
        user.email = ' ';

        registrationPage.visit();
        registrationPage.fillInRegisterForm(user);

        registrationPage.getError('email').should('be.visible').and('have.text', VALIDATION_MESSAGES.EMAIL_REQUIRED);
        cy.get('#email').should("have.class", 'is-invalid');
    });

    it('should not register a user with a duplicate email', () => {
        const user = defaultUser();

        registrationPage.visit();
        registrationPage.fillInRegisterForm(user);

        cy.contains('a', 'Create account').click();
        registrationPage.fillInRegisterForm(user);

        registrationPage.getError('email').should('be.visible').and('have.text', VALIDATION_MESSAGES.EMAIL_ALREADY_EXISTS);
        cy.get('#email').should("have.class", 'is-invalid');
    });

    ["invalid-email", "test@", "@gmail.com", "test@gmail", "test@gmail."].forEach(email => {
        it(`should not register a user with an invalid email format: ${email}`, () => {
            const user = defaultUser();
            user.email = email;

            registrationPage.visit();
            registrationPage.fillInRegisterForm(user);

            registrationPage.getError('email').should('be.visible').and('have.text', VALIDATION_MESSAGES.EMAIL_INVALID);
            cy.get('#email').should("have.class", 'is-invalid');
        });
    })


    it('should not register a user with empty password', () => {
        const user = defaultUser();

        registrationPage.visit();
        registrationPage.fillInName(user.name);
        registrationPage.fillInEmail(user.email);

        registrationPage.clickCreateAccountBtn();

        registrationPage.getError('password').should('be.visible').and('have.text', VALIDATION_MESSAGES.PASSWORD_REQUIRED);
        cy.get('#password').should("have.class", 'is-invalid');

    });

    [
        {password: 'Pass!23', message: VALIDATION_MESSAGES.PASSWORD_LENGTH_ERROR},
        {password: 'Pa!23'.repeat(13), message: VALIDATION_MESSAGES.PASSWORD_LENGTH_ERROR},
        {password: 'pass!123', message: VALIDATION_MESSAGES.PASSWORD_UPPERCASE_REQUIRED},
        {password: 'PASS!123', message: VALIDATION_MESSAGES.PASSWORD_LOWERCASE_REQUIRED},
        {password: 'Pass!!!!', message: VALIDATION_MESSAGES.PASSWORD_DIGIT_REQUIRED},
        {password: 'Pass1234', message: VALIDATION_MESSAGES.PASSWORD_SPEC_CHAR_REQUIRED}
    ].forEach(({password, message}) => {
        it(`should not register a user with an invalid password || message: ${message}`, () => {
            const user = defaultUser();
            user.password = password;

            registrationPage.visit();
            registrationPage.fillInRegisterForm(user);

            registrationPage.getError('password').should('be.visible').and('have.text', message);
            cy.get('#password').should("have.class", 'is-invalid');
        });
    })

    it('should clear validation errors when correcting fields', () => {
        registrationPage.visit();
        registrationPage.clickCreateAccountBtn();

        registrationPage.getError('name').should('be.visible').and('have.text', VALIDATION_MESSAGES.NAME_REQUIRED);
        cy.get('#name').should("have.class", 'is-invalid');
        registrationPage.getError('email').should('be.visible').and('have.text', VALIDATION_MESSAGES.EMAIL_REQUIRED);
        cy.get('#email').should("have.class", 'is-invalid');
        registrationPage.getError('password').should('be.visible').and('have.text', VALIDATION_MESSAGES.PASSWORD_REQUIRED);
        cy.get('#password').should("have.class", 'is-invalid');

        const user = defaultUser();

        registrationPage.fillInName(user.name);
        registrationPage.getError('name').should('not.exist');
        cy.get('#name').should("not.have.class", 'is-invalid');


        registrationPage.fillInEmail(user.email);
        registrationPage.getError('email').should('not.exist');
        cy.get('#email').should("not.have.class", 'is-invalid');


        registrationPage.fillInPassword(user.password);
        registrationPage.getError('password').should('not.exist');
        cy.get('#password').should("not.have.class", 'is-invalid');

        registrationPage.clickCreateAccountBtn();

        cy.location('pathname').should('eq', ROUTES.HOME);
    });
});