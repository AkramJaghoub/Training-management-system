
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
        const loginError = document.getElementById('loginError');

        fetch('/login', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
            },
            body: JSON.stringify({ email, password }),
        })
            .then(response => response.json())
            .then(data => {
                if (data.status === 400) {
                    const errorMessageDiv = document.getElementById('errorMessage');
                    errorMessageDiv.textContent = data.message;
                    errorMessageDiv.style.display = 'block'; // Show the error message
                } else {
                    window.location.href = data.message;
                }
            })
            .catch(error => console.error('Error:', error));
    });

