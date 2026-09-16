import {NavLink} from "react-router-dom";

function Navbar() {
    return (
        <nav className="navbar navbar-expand">
            <div className="container">

                <NavLink
                    to="/"
                    className="navbar-brand neon-brand"
                >
                    ECOMMERCE
                </NavLink>

                <div className="navbar-nav ms-auto">
                    <NavLink
                        to="/"
                        className="nav-link neon-nav-link"
                    >
                        Home
                    </NavLink>

                    <NavLink
                        to="/register"
                        className="nav-link neon-nav-link"
                    >
                        Register
                    </NavLink>
                </div>

            </div>
        </nav>
    );
}

export default Navbar;