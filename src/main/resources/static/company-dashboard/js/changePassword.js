document.addEventListener("DOMContentLoaded", function() {
    const changePasswordForm = document.getElementById("changePasswordForm");

    changePasswordForm.addEventListener("submit", function (event) {
        event.preventDefault();

        const currentPassword = document.getElementById("currentPassword").value;
        const newPassword = document.getElementById("newPassword").value;
        const confirmPassword = document.getElementById("confirmPassword").value;

        // Reset error messages
        document.getElementById("errorCurrentPassword").textContent = '';
        document.getElementById("errorNewPassword").textContent = '';
        document.getElementById("errorConfirmPassword").textContent = '';

        let isValid = true;

        // Validate each field
        if (currentPassword === '') {
            document.getElementById("errorCurrentPassword").textContent = 'Current Password is required';
            isValid = false;
        }
        if (newPassword === '') {
            document.getElementById("errorNewPassword").textContent = 'New Password is required';
            isValid = false;
        }
        if (confirmPassword === '') {
            document.getElementById("errorConfirmPassword").textContent = 'Confirm Password is required';
            isValid = false;
        }

        if (newPassword !== confirmPassword) {
            document.getElementById("errorConfirmPassword").textContent = "New password and confirm password do not match.";
            isValid = false;
        }

        if (!isValid) {
            return;
        }

        const data = {
            currentPassword: currentPassword,
            newPassword: newPassword
        };

        fetch("/change-password", {
            method: "PUT",
            headers: {
                "Content-Type": "application/json",
                "currentPassword": currentPassword,
                "newPassword": newPassword
            },
            body: JSON.stringify(data)
        })
            .then(response => response.json())
            .then(data => {
                if (data.status === 'OK') {
                    showSuccessAlert(data.message);
                } else {
                    document.getElementById("errorCurrentPassword").textContent = data.message
                }
            })
            .catch((error) => {
                console.error('Error:', error);
                alert("An error occurred while changing the password.");
            });
    });

    function showSuccessAlert(message) {
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