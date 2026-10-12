import {NavLink} from "react-router-dom";
import {useContext} from "react";
import AuthContext from "../context/AuthContext.jsx";

function Navbar() {
    const {isAuthenticated, logout} = useContext(AuthContext);

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

                    {isAuthenticated ? (
                        <button
                            type="button"
                            className="nav-link neon-nav-link"
                            onClick={logout}
                        >
                            Logout
                        </button>
                    ) : (
                        <>
                            <NavLink
                                to="/login"
                                className="nav-link neon-nav-link"
                            >
                                Login
                            </NavLink>

                            <NavLink
                                to="/register"
                                className="nav-link neon-nav-link"
                            >
                                Register
                            </NavLink>
                        </>
                    )}
                </div>

            </div>
        </nav>
    );
}

export default Navbar;