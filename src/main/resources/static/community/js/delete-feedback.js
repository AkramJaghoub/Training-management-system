document.querySelectorAll('.delete-icon').forEach(item => {
    item.addEventListener('click', event => {
        event.preventDefault();

        // Fetch feedback and student IDs from data attributes
        const feedbackId = item.getAttribute('data-feedback-id');
        const studentId = item.getAttribute('data-student-id');

        // Store these IDs for later use (e.g., in a global variable or in the modal's element)
        // Assuming you have a modal for confirmation
        const modal = $('#deleteConfirmationModal');
        modal.data('feedback-id', feedbackId);
        modal.data('student-id', studentId);

        // Show the modal for confirmation
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
    console.log("Deleting feedback with ID:", feedbackId); // Debugging line
    $.ajax({
        url: `/community/delete-feedback?feedbackId=${feedbackId}&studentId=${studentId}`,
        type: 'DELETE',
        success: function (response) {
            console.log("Delete successful:", response); // Debugging line
            showSuccessAlert(response.message);
            removeFeedbackElement(feedbackId);
        },
        error: function (error) {
            console.error("Delete failed:", error); // Debugging line
        }
    });
}

function removeFeedbackElement(feedbackId) {
    console.log("Removing feedback element with ID:", feedbackId); // Debugging line
    const feedbackElement = document.getElementById(`feedback-item-${feedbackId}`);
    if (feedbackElement) {
        feedbackElement.remove(); // This should remove the element from the DOM
    } else {
        console.log("Element not found for ID:", feedbackId); // Debugging line
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