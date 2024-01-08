document.addEventListener("DOMContentLoaded", function () {
    const form = document.getElementById('resetPasswordForm');

    form.addEventListener('submit', function (event) {
        event.preventDefault();

        const newPassword = document.getElementById('newPassword').value;
        const confirmPassword = document.getElementById('confirmPassword').value;
        const confirmPasswordError = document.getElementById('confirmPasswordError');
        const newPasswordError = document.getElementById('newPasswordError');
        const passwordRegex = /^(?=.*[A-Za-z])(?=.*\d)(?=.*[@$!%*#?&])[A-Za-z\d@$!%*#?&]{8,}$/;

        confirmPasswordError.textContent = '';
        newPasswordError.textContent = '';

        let isValid = true;

        if (newPassword === '' || confirmPassword === '') {
            isValid = false;
            if (newPassword === '') {
                newPasswordError.textContent = 'New Password is required';
            }
            if (confirmPassword === '') {
                confirmPasswordError.textContent = 'Confirm Password is required';
            }
        } else if (newPassword !== confirmPassword) {
            confirmPasswordError.textContent = 'Passwords don\'t match';
            isValid = false;
        } else if (!passwordRegex.test(newPassword)) {
            newPasswordError.innerHTML = 'Password requires: [8+ chars, 1+ uppercase,<br> 1+ number, 1+ special char]';
            isValid = false;
        }

        if (isValid) {
            fetch(form.action, {
                method: 'POST',
                body: new FormData(form),
            })
                .then(response => {
                    if (response.status === 400) {
                        // Handle BAD_REQUEST response
                        return response.json().then(data => {
                            if (data.message.includes("Your new password matches the current password!")) {
                                newPasswordError.textContent = data.message;
                            } else {
                                newPasswordError.textContent = 'An error occurred. Please try again.';
                            }
                            throw new Error('Bad Request: ' + data.message);
                        });
                    } else if (!response.ok) {
                        throw new Error('Network response was not ok ' + response.statusText);
                    }
                    return response.json();
                })
                .then(data => {
                    displaySuccessMessage(data.message);
                    form.reset();
                })
                .catch(error => {
                    console.error('Error:', error);
                });
        }
    });

    function displaySuccessMessage(message) {
        const alertBox = document.getElementById('successAlert');
        const messageParagraph = document.getElementById('successMessage');

        messageParagraph.textContent = message;

        alertBox.style.display = 'flex';
        alertBox.style.opacity = 1;

        // Wait 4 seconds before starting to fade out
        setTimeout(() => {
            let opacity = 1;
            const fadeInterval = setInterval(() => {
                if (opacity <= 0) {
                    clearInterval(fadeInterval);
                    alertBox.style.display = 'none';
                } else {
                    opacity -= 0.05;
                    alertBox.style.opacity = opacity;
                }
            }, 50);
        }, 4000);
    }
});