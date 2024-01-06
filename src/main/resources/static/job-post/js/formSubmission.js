document.addEventListener('DOMContentLoaded', function () {
    document.getElementById('submitButton').addEventListener('click', function (event) {
        event.preventDefault();

        document.getElementById('jobTitleError').textContent = '';
        document.getElementById('internsRequiredError').textContent = '';
        document.getElementById('jobDurationError').textContent = '';
        document.getElementById('countrySelectError').textContent = '';
        document.getElementById('citySelectError').textContent = '';
        document.getElementById('jobTypeError').textContent = '';
        document.getElementById('workModeError').textContent = '';
        document.getElementById('jobImageError').textContent = '';
        document.getElementById('descriptionError').textContent = '';
        document.getElementById('applicationLinkError').textContent = '';

        const form = document.getElementById('postAdForm');
        const jobTitle = form.elements['jobTitle'].value;
        const internsRequired = form.elements['internsRequired'].value;
        const jobDuration = form.elements['jobDuration'].value;
        const countrySelect = form.elements['countrySelect'].value;
        const citySelect = form.elements['citySelect'];
        const jobType = form.querySelector('input[name="jobType"]:checked');
        const workMode = form.elements['workMode'].value;
        const jobImage = form.elements['jobImage'].files.length;
        const description = tinymce.get('editor').getContent();
        const applicationLink = form.elements['applicationLink'].value;

        let isValid = true;

        if (isNaN(internsRequired)) {
            document.getElementById('internsRequiredError').textContent = 'Please enter a valid number for interns required.';
            isValid = false;
        }
        if (isNaN(jobDuration)) {
            document.getElementById('jobDurationError').textContent = 'Please enter a valid number for job duration.';
            isValid = false;
        }
        if (jobTitle.trim() === "") {
            document.getElementById('jobTitleError').textContent = 'Job Title is required';
            isValid = false;
        }
        if (internsRequired.trim() === "") {
            document.getElementById('internsRequiredError').textContent = 'Number of Interns is required';
            isValid = false;
        }
        if (jobDuration.trim() === "") {
            document.getElementById('jobDurationError').textContent = 'Job Duration is required';
            isValid = false;
        }
        if (countrySelect.trim() === "") {
            document.getElementById('countrySelectError').textContent = 'Country selection is required';
            isValid = false;
        }
        if (countrySelect.trim() !== "" && citySelect.disabled === false && citySelect.value.trim() === "") {
            document.getElementById('citySelectError').textContent = 'City selection is required';
            isValid = false;
        }
        if (!jobType) {
            document.getElementById('jobTypeError').textContent = 'Selecting a Job Type is required';
            isValid = false;
        }
        if (workMode.trim() === "") {
            document.getElementById('workModeError').textContent = 'Work Mode selection is required';
            isValid = false;
        }
        if (jobImage === 0) {
            document.getElementById('jobImageError').textContent = 'Uploading a Job Image is required';
            isValid = false;
        }
        if (description.trim() === "") {
            document.getElementById('descriptionError').textContent = 'Job Description is required';
            isValid = false;
        }
        if (applicationLink.trim() === "") {
            document.getElementById('applicationLinkError').textContent = 'Application Link is required';
            isValid = false;
        }

        if (!isValid) {
            return;
        }
        const formData = new FormData(form);
        formData.append('description', description);

        fetch('/advertisement/post', {
            method: 'POST',
            body: formData
        })
            .then(response => {
                if (response.status === 201) {
                    return response.json().then(apiResponse => {
                        showSuccessAlert(apiResponse.message);
                        form.reset();
                    });
                } else if (response.status === 400) {
                    return response.json().then(errorResponse => {
                        const jobTitleError = document.getElementById('jobTitleError');
                        jobTitleError.textContent = errorResponse.message;
                    });
                } else {
                    console.error("Failed to add advertisement");
                }
            })
            .catch(error => console.error('Error:', error));
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