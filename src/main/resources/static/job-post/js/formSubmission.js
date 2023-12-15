document.addEventListener('DOMContentLoaded', function () {
    console.log("ssssffffffffffffffffFFF");
    document.getElementById('submitButton').addEventListener('click', function (event) {
        event.preventDefault();
        const form = document.getElementById('postAdForm');
        const formData = new FormData(form);

        console.log(formData);
        fetch('/company/job/post', {
            method: 'POST',
            body: formData
        })
            .then(response => {
                if (response.status === 201) {
                    console.log("Advertisement updated successfully");
                    fetchAdvertisements();
                    hideUpdateForm();
                } else if (response.status === 400) {
                    response.text().then(errorMessage => {
                        const jobTitleError = document.getElementById('jobTitleError');
                        jobTitleError.textContent = errorMessage;
                    });
                } else {
                    console.error("Failed to update advertisement");
                }
            })
            .catch(error => console.error('Error:', error));
    });
});