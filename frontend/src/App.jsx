import {Route, Routes} from "react-router-dom";
import HomePage from "./pages/HomePage.jsx";
import RegistrationPage from "./pages/RegistrationPage.jsx";
import Navbar from "./components/Navbar.jsx";
import LoginPage from "./pages/LoginPage.jsx";
import {AuthProvider} from "./context/AuthContext.jsx";

function App() {
    return (
        <AuthProvider>
            <Navbar/>

            <Routes>
                <Route path="/" element={<HomePage/>}/>
                <Route path="/register" element={<RegistrationPage/>}/>
                <Route path="/login" element={<LoginPage/>}/>
            </Routes>
        </AuthProvider>
    );
}

export default App;