document.addEventListener('DOMContentLoaded', function () {
    document.getElementById('submitButton').addEventListener('click', function (event) {
        event.preventDefault();

        // Clear previous error messages
        document.getElementById('jobTitleError').textContent = '';
        document.getElementById('internsRequiredError').textContent = '';
        document.getElementById('jobDurationError').textContent = '';
        document.getElementById('countrySelectError').textContent = '';
        document.getElementById('citySelectError').textContent = '';
        document.getElementById('jobTypeError').textContent = '';
        document.getElementById('workModeError').textContent = '';
        document.getElementById('jobImageError').textContent = '';
        document.getElementById('descriptionError').textContent = '';

        const form = document.getElementById('postAdForm');
        const jobTitle = form.elements['jobTitle'].value;
        const internsRequired = form.elements['internsRequired'].value;
        const jobDuration = form.elements['jobDuration'].value;
        const countrySelect = form.elements['countrySelect'].value;
        const citySelect = form.elements['citySelect'];
        const jobType = form.querySelector('input[name="jobType"]:checked');
        const workMode = form.elements['workMode'].value;
        const jobImage = form.elements['jobImage'].files.length;
        const description = form.elements['description'].value;

        let isValid = true;

        // Add validations for each field
        if (jobTitle.trim() === "") {
            document.getElementById('jobTitleError').textContent = 'Job Title is required.';
            isValid = false;
        }
        if (internsRequired.trim() === "") {
            document.getElementById('internsRequiredError').textContent = 'Number of Interns Required is required.';
            isValid = false;
        }
        if (jobDuration.trim() === "") {
            document.getElementById('jobDurationError').textContent = 'Job Duration is required.';
            isValid = false;
        }
        if (countrySelect.trim() === "") {
            document.getElementById('countrySelectError').textContent = 'Country selection is required.';
            isValid = false;
        }
        if (countrySelect.trim() !== "" && citySelect.disabled === false && citySelect.value.trim() === "") {
            document.getElementById('citySelectError').textContent = 'City selection is required.';
            isValid = false;
        }
        if (!jobType) {
            document.getElementById('jobTypeError').textContent = 'Selecting a Job Type is required.';
            isValid = false;
        }
        if (workMode.trim() === "") {
            document.getElementById('workModeError').textContent = 'Work Mode selection is required.';
            isValid = false;
        }
        if (jobImage === 0) {
            document.getElementById('jobImageError').textContent = 'Uploading a Job Image is required.';
            isValid = false;
        }
        if (description.trim() === "") {
            document.getElementById('descriptionError').textContent = 'Job Description is required.';
            isValid = false;
        }

        if (!isValid) {
            return;
        }

        const formData = new FormData(form);

        fetch('/company/job/post', {
            method: 'POST', body: formData
        })
            .then(response => {
                if (response.status === 201) {
                    console.log("Advertisement added successfully");
                } else if (response.status === 400) {
                    response.text().then(errorMessage => {
                        const jobTitleError = document.getElementById('jobTitleError');
                        jobTitleError.textContent = errorMessage;
                    });
                } else {
                    console.error("Failed to add advertisement");
                }
            })
            .catch(error => console.error('Error:', error));
    });
});