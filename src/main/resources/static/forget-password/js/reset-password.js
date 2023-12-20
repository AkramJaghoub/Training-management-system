document.addEventListener("DOMContentLoaded", function () {
    const form = document.getElementById('resetPasswordForm');

    form.addEventListener('submit', function (event) {
        event.preventDefault(); // Prevent the default form submission behavior

        const newPassword = document.getElementById('newPassword').value;
        const confirmPassword = document.getElementById('confirmPassword').value;
        const confirmPasswordError = document.getElementById('confirmPasswordError');
        const newPasswordError = document.getElementById('newPasswordError');
        const password = form.elements['newPassword'].value;
        const passwordRegex = /^(?=.*[A-Za-z])(?=.*\d)(?=.*[@$!%*#?&])[A-Za-z\d@$!%*#?&]{8,}$/;

        confirmPasswordError.textContent = '';
        newPasswordError.textContent = '';

        let isValid = true;

        if (newPassword === '') {
            newPasswordError.textContent = 'New Password is required';
            isValid = false;
        }

        if (newPassword !== confirmPassword) {
            confirmPasswordError.textContent = 'Passwords don\'t match';
            isValid = false;
        }
        if (password && !passwordRegex.test(password)) {
            document.getElementById('newPasswordError').innerHTML =
                'Password requires: [8+ chars, 1+ uppercase,<br> 1+ number, 1+ special char]';
            isValid = false;
        }

        if (isValid) {
            fetch(form.action, {
                method: 'POST',
                body: new FormData(form),
            })
                .then(response => {
                    if (!response.ok) {
                        throw new Error('Network response was not ok ' + response.statusText);
                    }
                    return response.json();
                })
                .then(data => {
                    console.log(data);
                })
                .catch(error => {
                    console.error('Error:', error);
                });
        }
    });
});