

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

    document.addEventListener("DOMContentLoaded", () => {
    document.getElementById('loginForm').addEventListener('submit', function(event) {
        event.preventDefault();

        const formData = new FormData(this);
        const userData = Object.fromEntries(formData);
        const userDataJson = JSON.stringify(userData);

        fetch(this.action, {
            method: 'POST',
            headers: {'Content-Type': 'application/json'},
            body: userDataJson,
        })
            .then(response => {
                if (!response.ok) {
                    throw new Error('Network response was not ok');
                }
                if(response.redirected) {
                    window.location.href = response.url;
                } else {
                    return response.json();
                }
            })
            .then(data => {
            })
            .catch(error => {
                console.error('Error:', error);
            });
    });
});

