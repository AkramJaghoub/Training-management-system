document.getElementById('loginForm').addEventListener('submit', function (event) {
    event.preventDefault(); // Prevent the default form submission

    const emailInput = document.getElementById('email');
    const passwordInput = document.getElementById('password');
    const emailValue = emailInput.value.trim(); // Trim to remove leading/trailing spaces
    const passwordValue = passwordInput.value;

    // Clear any existing error messages
    const loginError = document.getElementById('errorMessage');
    const emailError = document.getElementById('emailError');
    const passwordError = document.getElementById('passwordError');
    loginError.textContent = '';
    emailError.textContent = '';
    passwordError.textContent = '';

    // Check if both email and password are provided
    if (!emailValue || !passwordValue) {
        // Show an error message for missing email or password
        if (!emailValue) {
            emailError.textContent = 'Email field is required';
        }
        if (!passwordValue) {
            passwordError.textContent = 'Password field is required';
        }
    }

    // Check if the email is correctly formatted (matches the regex)
    const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/; // Simple regex for email validation
    if (!emailRegex.test(emailValue) && emailValue !== 'root') {
        // Show an error message for invalid email format
        emailError.textContent = 'Please enter a valid email address';
        return; // Exit early, no need to proceed further
    }

    fetch('/login', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json',
        },
        body: JSON.stringify({email: emailValue, password: passwordValue}),
    })
        .then(response => {
            if (response.redirected) {
                let redirectUrl = response.url;
                redirectUrl += '?authFailed=true'; // Append query parameter
                window.location.href = redirectUrl; // Redirect to the URL provided by the server
                return; // Prevent further execution of the then-chain
            }
            return response.json(); // Continue with JSON processing for non-redirect responses
        })
        .then(data => {
            if (!data) return; // Skip further processing if we've handled a redirect

            console.log('Response Data:', data); // Print the entire JSON response

            if (data.status === "UNAUTHORIZED") {
                loginError.textContent = data.message; // Displaying error message
                loginError.style.display = 'block'; // Ensure the error message is visible
            } else {
                window.location.href = data.message; // Redirect on successful login
            }
        })
        .catch(error => {
            console.error('Error:', error);
            loginError.textContent = 'An error occurred. Please try again';
        });
});

// function handleForgotPasswordLink() {
//     const email = document.getElementById("email").value;
//     const forgotPasswordLink = document.getElementById("forgotPassword");
//     if (email && email.trim() !== "") {
//         forgotPasswordLink.href = "/forget-password?email=" + email;
//     } else {
//         forgotPasswordLink.href = "/forget-password";
//     }
//     return true;
// }

function handleForgotPasswordLink() {
    const email = document.getElementById("email").value;
    const forgotPasswordLink = document.getElementById("forgotPassword");
    if (email && email.trim() !== "") {
        forgotPasswordLink.href = "/forget-password?email=" + email;
    } else {
        forgotPasswordLink.href = "/forget-password";
    }
    return true;
}