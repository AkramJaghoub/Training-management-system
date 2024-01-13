document.querySelectorAll('.edit-icon').forEach(item => {
    item.addEventListener('click', event => {
        event.preventDefault();

        const updateComment = document.getElementById('updateComment');
        const commentCounter = document.getElementById('commentCounter');

        updateComment.addEventListener('input', function() {
            const currentLength = updateComment.value.length;
            commentCounter.textContent = `${currentLength}/250`;

            if (currentLength > 250) {
                commentCounter.classList.add('text-danger');
                commentCounter.classList.remove('text-muted');
            } else {
                commentCounter.classList.remove('text-danger');
                commentCounter.classList.add('text-muted');
            }
        });

        const feedbackId = item.getAttribute('data-feedback-id');
        const studentId = item.getAttribute('data-student-id');
        const companyName = item.getAttribute('data-company-name');
        const comment = item.getAttribute('data-comment');
        const rating = item.getAttribute('data-rating');

        const modal = new bootstrap.Modal(document.getElementById('updateFeedbackModal'));

        // Populate existing comment and count characters
        updateComment.value = comment;
        const currentLength = comment.length;
        commentCounter.textContent = `${currentLength}/250`;

        document.getElementById('updateFeedbackId').value = feedbackId;
        document.getElementById('updateStudentId').value = studentId; // Assuming there's an input field for this
        document.getElementById('updateCompanyName').value = companyName;
        document.getElementById('updateRating').value = rating;

        modal.show();
    });
});

document.getElementById('updateFeedbackForm').addEventListener('submit', function(event) {
    event.preventDefault();

    const updateComment = document.getElementById('updateComment');

    if (updateComment.value.length > 250) {
        updateComment.classList.add('is-invalid');
    } else {
        updateComment.classList.remove('is-invalid');

        const feedbackId = document.getElementById('updateFeedbackId').value;
        const studentId = document.getElementById('updateStudentId').value;
        const companyName = document.getElementById('updateCompanyName').value;
        const comment = document.getElementById('updateComment').value;
        const rating = document.getElementById('updateRating').value;

        const feedbackDto = {
            studentId: studentId,
            companyName: companyName,
            comment: comment,
            rating: rating
        };

        $.ajax({
            url: `/feedback/update?feedbackId=${feedbackId}`,
            type: 'PUT',
            contentType: 'application/json',
            data: JSON.stringify(feedbackDto),
            success: function(response) {
                const feedbackElement = document.getElementById(`feedback-item-${feedbackId}`);
                if (feedbackElement) {
                    const editIcon = feedbackElement.querySelector('.edit-icon');

                    feedbackElement.querySelector('.company-name').textContent = companyName;
                    feedbackElement.querySelector('.feedback-comment').textContent = comment;
                    feedbackElement.querySelector('.rating').textContent = `Rating: ${rating}`;

                    editIcon.setAttribute('data-company-name', companyName);
                    editIcon.setAttribute('data-comment', comment);
                    editIcon.setAttribute('data-rating', rating);
                }
                showSuccessAlert(response.message);
            },
            error: function(error) {
            }
        });

        bootstrap.Modal.getInstance(document.getElementById('updateFeedbackModal')).hide();
    }
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