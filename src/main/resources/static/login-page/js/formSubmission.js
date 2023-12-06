
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

    document.getElementById('loginForm').addEventListener('submit', function(event) {
        event.preventDefault();

        const email = document.getElementById('email').value;
        const password = document.getElementById('password').value;
        const loginError = document.getElementById('errorMessage');

        fetch('/login', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
            },
            body: JSON.stringify({ email, password }),
        })
            .then(response => response.json())
            .then(data => {
                if (data.status === "BAD_REQUEST") {
                    loginError.textContent = data.message; // Displaying error message
                    loginError.style.display = 'block'; // Ensure the error message is visible
                } else {
                    window.location.href = data.message; // Redirect on successful login
                }
            })
            .catch(error => {
                console.error('Error:', error);
                loginError.textContent = 'An error occurred. Please try again.';
                loginError.style.display = 'block';
            });
    });

