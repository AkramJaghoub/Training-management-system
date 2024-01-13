document.querySelectorAll('.delete-icon').forEach(item => {
    item.addEventListener('click', event => {
        event.preventDefault();

        const feedbackId = item.getAttribute('data-feedback-id');
        const studentId = item.getAttribute('data-student-id');
        const modal = $('#deleteConfirmationModal');
        modal.data('feedback-id', feedbackId);
        modal.data('student-id', studentId);

        modal.modal('show');
    });
});

document.getElementById('confirmDelete').addEventListener('click', () => {
    const modal = $('#deleteConfirmationModal');
    const feedbackId = modal.data('feedback-id');
    const studentId = modal.data('student-id');

    deleteFeedback(feedbackId, studentId);

    modal.modal('hide');
});

function deleteFeedback(feedbackId, studentId) {
    console.log("Deleting feedback with ID:", feedbackId);
    $.ajax({
        url: `/feedback/delete?feedbackId=${feedbackId}&studentId=${studentId}`,
        type: 'DELETE',
        success: function (response) {
            console.log("Delete successful:", response);
            showSuccessAlert(response.message);
            removeFeedbackElement(feedbackId);
        },
        error: function (error) {
            console.error("Delete failed:", error);
        }
    });
}

function removeFeedbackElement(feedbackId) {
    const feedbackElement = document.getElementById(`feedback-item-${feedbackId}`);
    if (feedbackElement) {
        feedbackElement.remove();
    }
}

function showSuccessAlert(message) {
    const alertBox = document.getElementById('successAlert');
    const messageParagraph = document.getElementById('successMessage');

    messageParagraph.textContent = message;
    alertBox.style.display = 'flex';
    alertBox.style.opacity = 1;

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