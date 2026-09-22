import FormField from "./FormField.jsx";

function RegistrationForm({
                              name,
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
                id="name"
                label="Name"
                type="text"
                value={name}
                onChange={onChange}
                error={errors.name}
            />

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
                    ? "Creating account..."
                    : "Create account"}
            </button>
        </form>
    );
}

export default RegistrationForm;