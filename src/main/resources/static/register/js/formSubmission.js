document.addEventListener('DOMContentLoaded', function () {
    // Handle Company Registration Form
    document.getElementById('companyRegisterForm').addEventListener('submit', function (event) {
        handleRegistrationForm(event, this, 'COMPANY');
    });

    // Handle Student Registration Form
    document.getElementById('studentRegisterForm').addEventListener('submit', function (event) {
        handleRegistrationForm(event, this, 'STUDENT');
    });

    function handleRegistrationForm(event, formElement, formType) {

        event.preventDefault();

        let isValid = true;

        const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
        const passwordRegex = /^(?=.*[A-Za-z])(?=.*\d)(?=.*[@$!%*#?&])[A-Za-z\d@$!%*#?&]{8,}$/;

        let userData = {};

        let confirmPassword;
        let passwordConfirmationErrorId;

        if (formType === 'STUDENT') {
            document.getElementById('studentLastNameError').textContent = '';
            document.getElementById('studentFirstNameError').textContent = '';
            document.getElementById('studentPasswordError').textContent = '';
            document.getElementById('studentEmailError').textContent = '';
            document.getElementById('studentConfirmationPasswordError').textContent = '';

            const form = document.getElementById('studentRegisterForm');
            const email = form.elements['email'].value;
            const password = form.elements['password'].value;
            const firstName = form.elements['firstName'].value;
            const lastName = form.elements['lastName'].value;
            confirmPassword = form.elements['confirmPassword'].value;
            passwordConfirmationErrorId = 'studentConfirmationPasswordError';

            userData.email = form.elements['email'].value;
            userData.password = form.elements['password'].value;
            userData.role = formType;
            userData.firstName = form.elements['firstName'].value;
            userData.lastName = form.elements['lastName'].value;

            document.getElementById(passwordConfirmationErrorId).textContent = '';

            if (email.trim() === "") {
                document.getElementById('studentEmailError').textContent = 'email is required';
                isValid = false;
            }

            if (password.trim() === "") {
                document.getElementById('studentPasswordError').textContent = 'Password is required';
                isValid = false;
            }

            if (firstName.trim() === "") {
                document.getElementById('studentFirstNameError').textContent = 'First Name is required';
                isValid = false;
            }

            if (lastName.trim() === "") {
                document.getElementById('studentLastNameError').textContent = 'Last Name is required';
                isValid = false;
            }

            if (email && !emailRegex.test(email)) {
                document.getElementById('studentEmailError').textContent = 'Please enter a valid email address';
                isValid = false;
            }
            if (password && !passwordRegex.test(password)) {
                document.getElementById('studentPasswordError').innerHTML =
                    'Password requires: [8+ chars, 1+ uppercase,<br> 1+ number, 1+ special char]';
                isValid = false;
            }

            if (password !== confirmPassword) {
                document.getElementById('studentConfirmationPasswordError').textContent = 'Password does not match';
                isValid = false;
            }
        } else {
            document.getElementById('companyEmailError').textContent = '';
            document.getElementById('companyNameError').textContent = '';
            document.getElementById('companyPasswordError').textContent = '';
            document.getElementById('companyConfirmationPasswordError').textContent = '';

            const form = document.getElementById('companyRegisterForm');

            const email = form.elements['email'].value;
            const password = form.elements['password'].value;
            const companyName = form.elements['name'].value;
            userData.email = form.elements['email'].value;
            userData.password = form.elements['password'].value;
            userData.role = formType;
            userData.companyName = form.elements['name'].value;
            confirmPassword = form.elements['confirmPassword'].value;
            passwordConfirmationErrorId = 'companyConfirmationPasswordError';

            document.getElementById(passwordConfirmationErrorId).textContent = '';

            if (email.trim() === "") {
                document.getElementById('companyEmailError').textContent = 'Email is required';
                isValid = false;
            }

            if (password.trim() === "") {
                document.getElementById('companyPasswordError').textContent = 'Password is required';
                isValid = false;
            }

            if (companyName.trim() === "") {
                document.getElementById('companyNameError').textContent = 'Company Name is required';
                isValid = false;
            }

            if (email && !emailRegex.test(email)) {
                document.getElementById('companyEmailError').textContent = 'Please enter a valid email address';
                isValid = false;
            }

            if (password && !passwordRegex.test(password)) {
                document.getElementById('companyPasswordError').innerHTML =
                    'Password requires: [8+ chars, 1+ uppercase,<br> 1+ number, 1+ special char]';
                isValid = false;
            }
            if (password !== confirmPassword) {
                document.getElementById('companyConfirmationPasswordError').textContent = 'Password does not match';
                isValid = false;
            }

        }

        if (!isValid) {
            return;
        }

        fetch('/register', {
            method: 'POST',
            headers: {'Content-Type': 'application/json'},
            body: JSON.stringify(userData),
        })
            .then(response => response.json().then(data => {
                console.log(data)
                if (!response.ok) {
                    if (response.status === 400) {
                        const emailErrorField = formType === 'STUDENT' ? 'studentEmailError' : 'companyEmailError';
                        document.getElementById(emailErrorField).textContent = 'An account with this email already exists.';
                    } else {
                        throw new Error(data.message || `Server responded with status: ${response.statusText}`);
                    }
                } else {
                    if (data.message) {
                        displaySuccessMessage(data.message);
                        if (data.redirectUrl) {
                            setTimeout(() => {
                                window.location.href = data.redirectUrl;
                            }, 2000);
                        }
                    }
                }
            }))
            .catch(error => {
                console.error('Error:', error);
            });
    }

    function displaySuccessMessage(message) {
        const alertBox = document.getElementById('successAlert');
        const messageParagraph = document.getElementById('successMessage');

        messageParagraph.textContent = message + ", now redirecting to your dashboard....";

        alertBox.style.display = 'flex'; // Change display to flex to make it visible
        alertBox.style.opacity = 1;

        // Wait 4 seconds before starting to fade out
        setTimeout(() => {
            let opacity = 1;
            const fadeInterval = setInterval(() => {
                if (opacity <= 0) {
                    clearInterval(fadeInterval);
                    alertBox.style.display = 'none'; // Hide it again after fade out
                } else {
                    opacity -= 0.05; // Decrease the opacity
                    alertBox.style.opacity = opacity;
                }
            }, 50); // Adjust the interval to control the speed of the fade-out
        }, 5000);
    }
});
