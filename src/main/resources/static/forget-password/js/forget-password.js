document.addEventListener("DOMContentLoaded", function () {
    const form = document.getElementById('forgetPasswordForm');
    const emailInput = document.getElementById('email'); // Get the email input field
    const emailError = document.getElementById('emailError'); // Get the email error div
    const forgetPasswordBox = document.getElementById('forgetErrorBox');

    const urlParams = new URLSearchParams(window.location.search);
    const errorMessage = urlParams.get('errorMessage');

    if (errorMessage) {
        forgetPasswordBox.textContent = 'Token has expired, please send an email to generate a new one';
        forgetPasswordBox.style.display = 'block';

        // Set a timer to fade out the message after 4 seconds
        setTimeout(function() {
            forgetPasswordBox.classList.add('fade-out');

            // Optionally, hide the element completely after the fade-out
            setTimeout(function() {
                forgetPasswordBox.style.display = 'none';
            }, 1000); // 1 second for the fade-out duration
        }, 4000); // 4 seconds before starting the fade-out
    }

    form.addEventListener('submit', function (event) {
        event.preventDefault();

        emailError.textContent = ''; // Clear any previous error message

        // Check if the email field is empty
        if (!emailInput.value.trim()) {
            emailError.textContent = 'Email field is required';
            return;
        }

        const formData = new FormData(form);

        fetch(form.action, {
            method: 'POST',
            body: formData
        })
            .then(response => {
                if (!response.ok) {
                    return response.json().then(data => {
                        if (data.status !== 'OK') {
                            emailError.textContent = data.message;
                        } else {
                            console.log(data.message);
                            displaySuccessMessage(data.message);
                        }
                        throw new Error('Server responded with an error');
                    });
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
    });

    function displaySuccessMessage(message) {
        const alertBox = document.getElementById('successAlert');
        const messageParagraph = document.getElementById('successMessage');

        messageParagraph.textContent = message;

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
        }, 4000);
    }
});