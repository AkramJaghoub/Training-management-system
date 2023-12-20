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
        const phoneNumberRegex = /^\+?\d{10,15}$/;

        let userData = {};

        if (formType === 'STUDENT') {
            document.getElementById('studentLastNameError').textContent = '';
            document.getElementById('studentMajorError').textContent = '';
            document.getElementById('studentFirstNameError').textContent = '';
            document.getElementById('studentUniversityError').textContent = '';
            document.getElementById('studentPasswordError').textContent = '';
            document.getElementById('studentEmailError').textContent = '';

            const form = document.getElementById('studentRegisterForm');
            const email = form.elements['email'].value;
            const password = form.elements['password'].value;
            const firstName = form.elements['firstName'].value;
            const lastName = form.elements['lastName'].value;
            const university = form.elements['university'].value;
            const major = form.elements['major'].value;

            userData.email = form.elements['email'].value;
            userData.password = form.elements['password'].value;
            userData.role = formType;
            userData.firstName = form.elements['firstName'].value;
            userData.lastName = form.elements['lastName'].value;
            userData.university = form.elements['university'].value;
            userData.major = form.elements['major'].value;

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

            if (university.trim() === "") {
                document.getElementById('studentUniversityError').textContent = 'University is required';
                isValid = false;
            }

            if (major.trim() === "") {
                document.getElementById('studentMajorError').textContent = 'Major is required';
                isValid = false;
            }


            if (email && !emailRegex.test(email)) {
                document.getElementById('studentEmailError').textContent = 'Please enter a valid email address';
                isValid = false;
            }
            if (password && !passwordRegex.test(password)) {
                document.getElementById('studentPasswordError').textContent = 'Password must contain at least 8 characters, one uppercase, one number, and one special character.';
                isValid = false;
            }

        } else {
            document.getElementById('companyEmailError').textContent = '';
            document.getElementById('companyNameError').textContent = '';
            document.getElementById('companyPhoneNumberError').textContent = '';
            document.getElementById('companyPasswordError').textContent = '';

            const form = document.getElementById('companyRegisterForm');

            const email = form.elements['email'].value;
            const password = form.elements['password'].value;
            const phoneNumber = form.elements['phoneNumber'].value; // Updated for phone number
            const companyName = form.elements['name'].value;
            userData.email = form.elements['email'].value;
            userData.password = form.elements['password'].value;
            userData.role = formType;
            userData.companyName = form.elements['name'].value;
            userData.phoneNumber = form.elements['phoneNumber'].value; // Updated for phone number

            if (email.trim() === "") {
                document.getElementById('companyEmailError').textContent = 'Email is required';
                isValid = false;
            }

            if (password.trim() === "") {
                document.getElementById('companyPasswordError').textContent = 'Password is required';
                isValid = false;
            }

            if (phoneNumber.trim() === "") { // Validation for phone number
                document.getElementById('companyPhoneNumberError').textContent = 'Phone number is required';
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
                document.getElementById('companyPasswordError').textContent = 'Password requires: [8+ chars, 1+ uppercase,<br> 1+ number, 1+ special char]';
                isValid = false;
            }

            if (phoneNumber && !phoneNumberRegex.test(phoneNumber)) {
                document.getElementById('companyPhoneNumberError').textContent = 'Please enter a valid phone number';
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
            .then(response => {
                if (!response.ok) {
                    throw new Error(`Server responded with status: ${response.statusText}`);
                }
                return response.json();
            })
            .then(data => {
                if (data.redirectUrl) {
                    window.location.href = data.redirectUrl;
                } else {
                    console.error('No redirect URL in response:', data);
                }
            })

            .catch(error => {
                console.error('Error:', error);
            });
    }
});
