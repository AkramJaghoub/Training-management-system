document.addEventListener('DOMContentLoaded', function () {
    const form = document.querySelector('form');
    const emailInput = document.getElementById('email');
    const emailError = document.getElementById('emailError');
    const urlParams = new URLSearchParams(window.location.search);
    const authErrorBox = document.getElementById('authErrorBox');
    const authErrorText = authErrorBox ? authErrorBox.querySelector('.text') : null;
    console.log("Setting authFailed in session storage");
    sessionStorage.setItem('authFailed', 'true');
    console.log(sessionStorage.getItem('authFailed') + " Ssssss")
    // Check if redirected here due to authentication failure
    if (urlParams.has('authFailed')) {
        // Display appropriate error message
        if (authErrorText) {
            authErrorText.textContent = 'User is not authenticated — Enter your email to verify yourself';
            authErrorBox.style.display = 'block';
        }
    } else if (urlParams.has('tokenExpired')) {
        // Handle token expiration message
        if (authErrorText) {
            authErrorText.textContent = 'Token has expired, please send an email to generate a new one';
            authErrorBox.style.display = 'block';
        }
    }

    function validateEmail() {
        const emailValue = emailInput.value.trim();
        emailError.textContent = '';

        if (!emailValue) {
            emailError.textContent = 'Email field is required';
            return false;
        }

        const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
        if (!emailRegex.test(emailValue)) {
            emailError.textContent = 'Please enter a valid email address';
            return false;
        }

        return true;
    }

    form.addEventListener('submit', function (event) {
        if (!validateEmail()) {
            event.preventDefault(); // Prevent form submission
        }
    });
});