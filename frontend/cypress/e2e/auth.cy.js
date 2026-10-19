import LoginPage from "../pages/loginPage.js";
import {ROUTES} from "../constants/routes.js";
import {VALIDATION_MESSAGES} from "../constants/validationMessages.js";
import Navbar from "../pages/navbar.js";

const loginPage = new LoginPage();
const navbar = new Navbar();

describe('Login and Logout', () => {

    beforeEach(() => {
        loginPage.visit();
    })

    it('should log in with valid credentials as a user', () => {
        let token;

        cy.intercept('POST', '**/auth/login').as('loginRequest');
        cy.env(['userEmail', 'userPassword']).then(
            ({userEmail, userPassword}) => {
                loginPage.login(userEmail, userPassword)
            }
        );

        cy.wait('@loginRequest').then(interception => {
            expect(interception.response.statusCode).eq(200);
            expect(interception.response.body).to.have.property('token').that.is.a('string').and.not.empty;

            token = interception.response.body.token;
        });

        cy.window().then(win => expect(win.localStorage.getItem("token")).eq(token));

        cy.location('pathname').should('eq', ROUTES.HOME);

        navbar.validateAuthState(true);
    });

    it('should log in with valid credentials as an admin', () => {
        let token;

        cy.intercept('POST', '**/auth/login').as('loginRequest');
        cy.env(['adminEmail', 'adminPassword']).then(
            ({adminEmail, adminPassword}) => {
                loginPage.login(adminEmail, adminPassword)
            }
        );

        cy.wait('@loginRequest').then(interception => {
            expect(interception.response.statusCode).eq(200);
            expect(interception.response.body).to.have.property('token').that.is.a('string').and.not.empty;

            token = interception.response.body.token;
        });

        cy.window().then(win => expect(win.localStorage.getItem("token")).eq(token));

        cy.location('pathname').should('eq', ROUTES.HOME);

        navbar.validateAuthState(true);
    });

    it('should remain authenticated after page reload', () => {
        let token;

        cy.intercept('POST', '**/auth/login').as('loginRequest');
        cy.env(['userEmail', 'userPassword']).then(
            ({userEmail, userPassword}) => {
                loginPage.login(userEmail, userPassword)
            }
        );

        cy.wait('@loginRequest').then(interception => {
            expect(interception.response.statusCode).eq(200);
            expect(interception.response.body).to.have.property('token').that.is.a('string').and.not.empty;

            token = interception.response.body.token;
        });

        cy.window().then(win => expect(win.localStorage.getItem("token")).eq(token));

        cy.location('pathname').should('eq', ROUTES.HOME);

        navbar.validateAuthState(true);

        cy.reload();

        cy.window().then(win => expect(win.localStorage.getItem("token")).eq(token));

        navbar.validateAuthState(true);
    });

    it('should not log in with an incorrect password', () => {
        cy.intercept('POST', '**/auth/login').as('loginRequest');
        cy.env(['userEmail']).then(
            ({userEmail}) => {
                loginPage.login(userEmail, "NotRealPass!123");
            }
        );

        loginPage.validateFormError(VALIDATION_MESSAGES.INVALID_CREDENTIALS);

        cy.wait('@loginRequest').then(interception => {
            expect(interception.response.statusCode).eq(401);
            expect(interception.response.body).to.have.property('status').eq(401);
            expect(interception.response.body).to.have.property('message').eq(VALIDATION_MESSAGES.INVALID_CREDENTIALS);
        });

        cy.window().its('localStorage').invoke('getItem', 'token').should('be.null');

        cy.location('pathname').should('eq', ROUTES.LOGIN);

        navbar.validateAuthState(false);
    });


    it('should not log in with a non-existing email', () => {
        cy.intercept('POST', '**/auth/login').as('loginRequest');
        cy.env(['userPassword']).then(
            ({userPassword}) => {
                loginPage.login('notreal@gmail.com', userPassword);
            }
        );

        loginPage.validateFormError(VALIDATION_MESSAGES.INVALID_CREDENTIALS);

        cy.wait('@loginRequest').then(interception => {
            expect(interception.response.statusCode).eq(401);
            expect(interception.response.body).to.have.property('status').eq(401);
            expect(interception.response.body).to.have.property('message').eq(VALIDATION_MESSAGES.INVALID_CREDENTIALS);
        });

        cy.window().its('localStorage').invoke('getItem', 'token').should('be.null');

        cy.location('pathname').should('eq', ROUTES.LOGIN);

        navbar.validateAuthState(false);
    });


    it('should not log in as a disabled user', () => {
        cy.intercept('POST', '**/auth/login').as('loginRequest');
        cy.env(['disabledUserEmail', 'disabledUserPassword']).then(
            ({disabledUserEmail, disabledUserPassword}) => {
                loginPage.login(disabledUserEmail, disabledUserPassword);
            }
        );

        loginPage.validateFormError(VALIDATION_MESSAGES.INVALID_CREDENTIALS);

        cy.wait('@loginRequest').then(interception => {
            expect(interception.response.statusCode).eq(401);
            expect(interception.response.body).to.have.property('status').eq(401);
            expect(interception.response.body).to.have.property('message').eq(VALIDATION_MESSAGES.INVALID_CREDENTIALS);
        });

        cy.window().its('localStorage').invoke('getItem', 'token').should('be.null');

        cy.location('pathname').should('eq', ROUTES.LOGIN);

        navbar.validateAuthState(false);
    });


    it('should not log in with an empty email', () => {
        cy.env(['userPassword']).then(
            ({userPassword}) => {
                loginPage.login('', userPassword)
            }
        );

        loginPage.validateFieldError('email', VALIDATION_MESSAGES.EMAIL_REQUIRED);

        cy.location('pathname').should('eq', ROUTES.LOGIN);

        navbar.validateAuthState(false);
    });

    it('should not log in with an empty password', () => {
        cy.env(['userEmail']).then(
            ({userEmail}) => {
                loginPage.login(userEmail, '')
            }
        );

        loginPage.validateFieldError('password', VALIDATION_MESSAGES.PASSWORD_REQUIRED);

        cy.location('pathname').should('eq', ROUTES.LOGIN);

        navbar.validateAuthState(false);
    });

    ["invalid-email", "test@", "@gmail.com", "test@gmail", "test@gmail."].forEach(userEmail => {
        it(`should not log in with an invalid email format: ${userEmail}`, () => {
            cy.env(['userPassword']).then(
                ({userPassword}) => {
                    loginPage.login(userEmail, userPassword)
                }
            );

            loginPage.validateFieldError('email', VALIDATION_MESSAGES.EMAIL_INVALID);

            cy.location('pathname').should('eq', ROUTES.LOGIN);

            navbar.validateAuthState(false);
        });
    });

    it('should log out as an authenticated user', () => {
        cy.intercept('POST', '**/auth/logout').as('logoutRequest');
        cy.env(['userEmail', 'userPassword']).then(
            ({userEmail, userPassword}) => {
                loginPage.login(userEmail, userPassword)
            }
        );

        cy.contains('button', 'Logout').click();

        cy.wait('@logoutRequest').then(interception => {
            expect(interception.response.statusCode).eq(204);
        });

        cy.window().its('localStorage').invoke('getItem', 'token').should('be.null');

        cy.location('pathname').should('eq', ROUTES.HOME);

        navbar.validateAuthState(false);
    });

    it('should revoke token after logout', () => {
        let token;

        cy.intercept('POST', '**/auth/login').as('loginRequest');
        cy.intercept('POST', '**/auth/logout').as('logoutRequest');

        cy.env(['adminEmail', 'adminPassword']).then(
            ({adminEmail, adminPassword}) => {
                loginPage.login(adminEmail, adminPassword);
            }
        );

        cy.wait('@loginRequest').then(({response}) => {
            token = response.body.token;
        });

        cy.contains('button', 'Logout').click();

        cy.wait('@logoutRequest').then(({response}) => {
            expect(response.statusCode).to.eq(204);
        });

        cy.then(() => {
            return cy.request({
                method: 'POST',
                url: 'http://localhost:8080/products',
                body: {
                    title: "Some Product",
                    price: 1500
                },
                headers: {
                    Authorization: `Bearer ${token}`
                },
                failOnStatusCode: false
            });
        }).then((response) => {
            expect(response.status).to.eq(401);
            expect(response.body.status).to.eq(401);
            expect(response.body.message).to.eq(VALIDATION_MESSAGES.INVALID_TOKEN);
        });
    });
});