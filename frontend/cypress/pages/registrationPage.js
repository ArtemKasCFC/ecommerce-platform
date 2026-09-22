class RegistrationPage {

    visit() {
        cy.visit('/register');
    }

    fillInName(name) {
        cy.get("#name").type(name);
    }


    fillInEmail(email) {
        cy.get("#email").type(email);
    }


    fillInPassword(password) {
        cy.get("#password").type(password);
    }

    clickCreateAccountBtn() {
        cy.get('button[type="submit"]').click();
    }

    fillInRegisterForm({name, email, password}) {
        this.fillInName(name);
        this.fillInEmail(email);
        this.fillInPassword(password);

        this.clickCreateAccountBtn();
    }

    getError(field) {
        return cy.get(`#${field} ~ .invalid-feedback`);
    }
}

export default RegistrationPage;