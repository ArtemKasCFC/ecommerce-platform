import FormField from "./FormField.jsx";

function LoginForm({
                       email,
                       password,
                       errors,
                       serverError,
                       isSubmitting,
                       onChange,
                       onSubmit
                   }) {
    return (
        <form onSubmit={onSubmit} noValidate>
            <FormField
                id="email"
                label="Email"
                type="email"
                value={email}
                onChange={onChange}
                error={errors.email}
            />

            <FormField
                id="password"
                label="Password"
                type="password"
                value={password}
                onChange={onChange}
                error={errors.password}
            />

            {serverError && (
                <div className="alert alert-danger mb-3">
                    {serverError}
                </div>
            )}

            <button
                type="submit"
                disabled={isSubmitting}
                className="btn btn-neon w-100"
            >
                {isSubmitting
                    ? "Logging in ..."
                    : "Login"}
            </button>
        </form>
    );
}

export default LoginForm;