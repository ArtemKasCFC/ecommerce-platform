class RegistrationPage {

    visit() {
        cy.visit('/register');
    }

    fillInName(name) {
        if (name !== '') cy.get("#name").type(name);
    }

    fillInEmail(email) {
        if (email !== '') cy.get("#email").type(email);
    }

    fillInPassword(password) {
        if (password !== '') cy.get("#password").type(password);
    }

    clickCreateAccountBtn() {
        cy.get('button[type="submit"]').click();
    }

    register({name, email, password}) {
        this.fillInName(name);
        this.fillInEmail(email);
        this.fillInPassword(password);

        this.clickCreateAccountBtn();
    }

    validateFieldError(field, message) {
        cy.get(`#${field} ~ .invalid-feedback`).should('be.visible').and('have.text', message);
        cy.get(`#${field}`).should("have.class", 'is-invalid');
    }
}

export default RegistrationPage;