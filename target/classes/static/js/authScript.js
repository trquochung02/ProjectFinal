/**
 * Handles all authentication-related client-side scripting.
 */
$(document).ready(function () {

    // --- DOM Elements ---
    const passwordInput = $('#password');
    const confirmPasswordInput = $('#confirmPassword');
    const passwordError = $("#passwordError");
    const confirmPasswordError = $("#confirmPasswordError");

    // --- Reusable Function for Toggling Password Visibility ---
    /**
     * Toggles the visibility of a password field.
     * @param {jQuery} inputField - The password input field.
     * @param {jQuery} toggleIcon - The icon element to toggle.
     */
    function togglePasswordVisibility(inputField, toggleIcon) {
        const type = inputField.attr('type') === 'password' ? 'text' : 'password';
        inputField.attr('type', type);
        toggleIcon.toggleClass('bi-eye-slash bi-eye'); // Toggle between two Bootstrap icons
    }

    // --- Event Listeners ---

    // Apply toggle function to the main password field
    $('#togglePassword').on('click', function () {
        togglePasswordVisibility(passwordInput, $(this));
    });

    // Apply toggle function to the confirm password field
    $('#toggleConfirmPassword').on('click', function () {
        togglePasswordVisibility(confirmPasswordInput, $(this));
    });

    // AJAX form submission for "Forgot Password"
    $('#forgotPasswordForm').on('submit', function (event) {
        event.preventDefault();
        const email = $('#email').val();
        const errorMessage = $('#error-message');
        const successMessage = $('#success-message');

        errorMessage.text('');
        successMessage.text('');

        $.ajax({
            url: '/api/auth/password/forgot',
            type: 'POST',
            data: { email: email },
            success: function (response) {
                successMessage.text("We've sent an email with the link to reset your password.");
            },
            error: function (xhr, status, error) {
                errorMessage.text("The Email address doesn't exist. Please try again.");
            }
        });
    });

    // --- Validation for Reset Password Form ---

    /**
     * Validates password and confirm password fields.
     * @returns {boolean} - True if inputs are valid, otherwise false.
     */
    function validateResetPasswordForm() {
        const passwordValue = passwordInput.val();
        const confirmPasswordValue = confirmPasswordInput.val();
        const passwordCriteria = /^(?=.*[A-Za-z])(?=.*\d)[A-Za-z\d]{7,}$/;
        let isValid = true;

        // Validate password strength
        if (!passwordCriteria.test(passwordValue)) {
            passwordError.text("Use at least one letter, one number and seven characters");
            isValid = false;
        } else {
            passwordError.text("");
        }

        // Validate password match
        if (passwordValue !== confirmPasswordValue) {
            confirmPasswordError.text("Passwords do not match. Please try again.");
            isValid = false;
        } else {
            confirmPasswordError.text("");
        }
        return isValid;
    }

    // Attach validation to the form submission
    $('#resetPasswordForm > form').on('submit', function (event) {
        if (!validateResetPasswordForm()) {
            event.preventDefault();
        }
    });

    // Validate on input change for immediate feedback
    passwordInput.on("input", validateResetPasswordForm);
    confirmPasswordInput.on("input", validateResetPasswordForm);
});