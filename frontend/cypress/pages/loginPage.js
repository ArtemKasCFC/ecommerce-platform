class LoginPage {

    visit() {
        cy.visit('/login');
    }

    fillInEmail(email) {
        if (email !== '') cy.get("#email").type(email);
    }


    fillInPassword(password) {
        if (password !== '') cy.get("#password").type(password);
    }

    clickLoginBtn() {
        cy.get('button[type="submit"]').click();
    }

    login(email, password) {
        this.fillInEmail(email);
        this.fillInPassword(password);

        this.clickLoginBtn();
    }

    validateFieldError(field, message) {
        cy.get(`#${field} ~ .invalid-feedback`).should('be.visible').and('have.text', message);
        cy.get(`#${field}`).should("have.class", 'is-invalid');
    }


    validateFormError(message) {
        cy.get('.alert-danger').should('be.visible').and('have.text', message);
    }
}

export default LoginPage;