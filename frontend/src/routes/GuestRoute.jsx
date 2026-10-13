import {Navigate, Outlet} from "react-router-dom";
import {useContext} from "react";
import AuthContext from "../context/AuthContext.jsx";

export default function GuestRoute() {
    const {isAuthenticated} = useContext(AuthContext);

    if (isAuthenticated) {
        return <Navigate to="/" replace/>;
    }

    return <Outlet/>;
}