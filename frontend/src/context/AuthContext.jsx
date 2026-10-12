import {createContext, useState} from "react";
import {logoutUser} from "../api/usersApi.js";

const AuthContext = createContext(null);

export function AuthProvider({children}) {
    const [token, setToken] = useState(
        () => localStorage.getItem("token")
    );

    const isAuthenticated = Boolean(token);

    async function logout() {
        try {
            if (token) {
                await logoutUser(token);
            }
        } catch (error) {
            console.error("Logout error:", error);
        } finally {
            localStorage.removeItem("token");
            setToken(null);
        }
    }

    function login(token) {
        localStorage.setItem("token", token);
        setToken(token);
    }

    return (
        <AuthContext.Provider value={{token, isAuthenticated, login, logout}}>
            {children}
        </AuthContext.Provider>
    );
}

export default AuthContext;
