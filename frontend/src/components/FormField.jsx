function FormField({
                       id,
                       label,
                       type,
                       value,
                       onChange,
                       error
                   }) {
    return (
        <div className="mb-4">

            <label
                htmlFor={id}
                className="form-label neon-label"
            >
                {label}
            </label>

            <input
                id={id}
                type={type}
                value={value}
                onChange={onChange}
                className={`form-control neon-input ${
                    error ? "is-invalid" : ""
                }`}
            />

            {error && (
                <div className="invalid-feedback neon-error">
                    {error}
                </div>
            )}

        </div>
    );
}

export default FormField;