import {useContext, useEffect, useState} from "react";
import LoginForm from "../components/LoginForm.jsx";
import {loginUser} from "../api/usersApi.js";
import {useNavigate} from "react-router-dom";
import AuthContext from "../context/AuthContext.jsx";

function LoginPage() {
    const navigate = useNavigate();

    useEffect(() => {
        document.title = "Login | Ecommerce Platform";
    }, []);

    const {login} = useContext(AuthContext);

    const [email, setEmail] = useState("");
    const [password, setPassword] = useState("");
    const [errors, setErrors] = useState({});
    const [serverError, setServerError] = useState("");
    const [isSubmitting, setIsSubmitting] = useState(false);

    function handleChange(event) {
        const {id, value} = event.target;

        if (id === "email") setEmail(value);
        if (id === "password") setPassword(value);

        setErrors(previousErrors => ({
            ...previousErrors,
            [id]: ""
        }));
    }

    async function handleSubmit(event) {
        event.preventDefault();

        const validationErrors = validate();

        if (Object.keys(validationErrors).length > 0) {
            setErrors(validationErrors);
            return;
        }

        setServerError("")

        setIsSubmitting(true);

        try {
            const {response, data} = await loginUser({email, password});

            if (!response.ok) {
                if (response.status === 401) {
                    setServerError("Invalid email or password");
                    return;
                }

                setServerError("Something went wrong. Please try again.");
                return;
            }

            const token = data.token;
            login(token);

            navigate("/");

        } catch (error) {
            console.error("Network error:", error);

            setServerError(
                "Unable to connect to the server. Please try again."
            );
        } finally {
            setIsSubmitting(false);
        }
    }

    function validate() {
        const newErrors = {};

        if (!email.trim()) {
            newErrors.email = "Email is required";
        } else if (!/^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(email)) {
            newErrors.email = "Invalid email";
        }

        if (!password.trim()) {
            newErrors.password = "Password is required";
        }

        return newErrors;
    }

    return (
        <main className="container page-container">

            <div className="neon-card">
                <p className="retro-label">
                    WELCOME BACK
                </p>

                <h1 className="neon-title">
                    Login
                </h1>

                <LoginForm
                    email={email}
                    password={password}
                    errors={errors}
                    serverError={serverError}
                    isSubmitting={isSubmitting}
                    onChange={handleChange}
                    onSubmit={handleSubmit}
                />
            </div>

        </main>
    );
}

export default LoginPage;