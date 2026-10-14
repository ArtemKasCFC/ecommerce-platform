class Navbar {
    validateAuthState(isLoggedIn) {
        if (isLoggedIn) {
            cy.contains('button', 'Logout').should('be.visible');
            cy.get("a[href='/register']").should('not.exist');
            cy.get("a[href='/login']").should('not.exist');
        } else {
            cy.contains('button', 'Logout').should('not.exist');
            cy.get("a[href='/register']").should('be.visible');
            cy.get("a[href='/login']").should('be.visible');
        }
    }
}

export default Navbar;