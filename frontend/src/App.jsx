import {Route, Routes} from "react-router-dom";
import HomePage from "./pages/HomePage.jsx";
import RegistrationPage from "./pages/RegistrationPage.jsx";
import Navbar from "./components/Navbar.jsx";

function App() {
    return (
        <>
            <Navbar/>

            <Routes>
                <Route path="/" element={<HomePage/>}/>
                <Route path="/register" element={<RegistrationPage/>}/>
            </Routes>
        </>
    );
}

export default App;