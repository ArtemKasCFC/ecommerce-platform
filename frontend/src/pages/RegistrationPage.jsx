import {useEffect, useState} from "react";
import {useNavigate} from "react-router-dom";
import {createUser} from "../api/usersApi.js";
import RegistrationForm from "../components/RegistrationForm.jsx";

function RegistrationPage() {
    const navigate = useNavigate();

    useEffect(() => {
        document.title = "Create account | Ecommerce Platform";
    }, []);

    const [name, setName] = useState("");
    const [email, setEmail] = useState("");
    const [password, setPassword] = useState("");
    const [errors, setErrors] = useState({});
    const [serverError, setServerError] = useState("");
    const [isSubmitting, setIsSubmitting] = useState(false);

    function validate() {
        const newErrors = {};

        if (!name.trim()) {
            newErrors.name = "Name is required";
        } else if (name.trim().length > 100) {
            newErrors.name = "Name must not exceed 100 characters";
        }

        if (!email.trim()) {
            newErrors.email = "Email is required";
        } else if (!/^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(email)) {
            newErrors.email = "Invalid email";
        }

        if (!password) {
            newErrors.password = "Password is required";
        } else if (password.length < 8 || password.length > 64) {
            newErrors.password =
                "Password's length must be between 8 and 64 characters";
        } else if (!/[A-Z]/.test(password)) {
            newErrors.password =
                "Password must contain an uppercase letter";
        } else if (!/[a-z]/.test(password)) {
            newErrors.password =
                "Password must contain a lowercase letter";
        } else if (!/\d/.test(password)) {
            newErrors.password =
                "Password must contain a digit";
        } else if (!/[^A-Za-z\d]/.test(password)) {
            newErrors.password =
                "Password must contain a special character";
        }

        return newErrors;
    }

    function handleChange(event) {
        const {id, value} = event.target;

        if (id === "name") {
            setName(value);
        }

        if (id === "email") {
            setEmail(value);
        }

        if (id === "password") {
            setPassword(value);
        }

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

        setServerError("");

        const request = {
            name: name.trim(),
            email: email.trim(),
            password
        };

        setIsSubmitting(true);

        try {
            const {response, data} = await createUser(request);

            if (!response.ok) {
                if (response.status === 400 && data.errors) {
                    setErrors(data.errors);
                    return;
                }

                if (response.status === 409) {
                    setErrors({
                        email: data.message
                    });
                    return;
                }

                setServerError(
                    "Something went wrong. Please try again."
                );
                return;
            }

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

    return (
        <main className="container page-container">
            <div className="registration-wrapper">

                <div className="neon-card">
                    <p className="retro-label">
                        USER REGISTRATION
                    </p>

                    <h1 className="neon-title">
                        Create account
                    </h1>

                    <p className="neon-text">
                        Join the platform and start shopping.
                    </p>

                    <RegistrationForm
                        name={name}
                        email={email}
                        password={password}
                        errors={errors}
                        serverError={serverError}
                        isSubmitting={isSubmitting}
                        onChange={handleChange}
                        onSubmit={handleSubmit}
                    />
                </div>

            </div>
        </main>
    );
}

export default RegistrationPage;