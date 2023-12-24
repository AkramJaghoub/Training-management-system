document.addEventListener('DOMContentLoaded', function() {
    function showDeleteDialog(email, userId) {
        const dialog = document.getElementById('deleteDialog');
        const userEmailToDelete = document.getElementById('userEmailToDelete');

        userEmailToDelete.textContent = `Are you sure you want to delete user with email ${email}?`;

        dialog.style.display = 'flex';
        setTimeout(() => {
            dialog.style.opacity = '1';
        }, 10);

        document.getElementById('confirmDelete').dataset.userId = userId;
    }

    function hideDeleteDialog() {
        const dialog = document.getElementById('deleteDialog');
        dialog.style.opacity = '0';
        setTimeout(() => {
            dialog.style.display = 'none';
        }, 500);
    }

    const deleteIcons = document.querySelectorAll('.delete-icon');
    deleteIcons.forEach(deleteIcon => {
        deleteIcon.addEventListener('click', function () {
            console.log("Delete icon clicked");

            const email = this.closest('tr').getAttribute('data-user-email');
            const userId = this.closest('tr').getAttribute('data-user-id');

            console.log("Email: ", email);
            console.log("User ID: ", userId);

            showDeleteDialog(email, userId);
        });
    });


    document.getElementById('cancelDelete').addEventListener('click', function () {
        hideDeleteDialog();
    });

    document.getElementById('confirmDelete').addEventListener('click', function () {
        const userId = this.dataset.userId;

        fetch(`/admin/delete/user/${userId}`, {
            method: 'DELETE'
        })
            .then(response => {
                if (response.ok) {
                    return response.text();
                }
                throw new Error('Network response was not ok');
            })
            .then(message => {
                hideDeleteDialog();

                // Remove the user row from the table
                const userRow = document.querySelector(`tr[data-user-id="${userId}"]`);
                if (userRow) {
                    userRow.remove();
                }

                // Show success alert next to the User List title
                const alertPlaceholder = document.getElementById('alertPlaceholder');
                alertPlaceholder.innerHTML = `<div class="alert alert-success" role="alert">${message}</div>`;
                alertPlaceholder.style.display = 'block'; // Show the alert

                // Fade out the alert after 3 seconds
                let opacity = 1; // Fully opaque
                const fadeOut = setInterval(() => {
                    if (opacity <= 0.1) {
                        clearInterval(fadeOut);
                        alertPlaceholder.style.display = 'none';
                        alertPlaceholder.innerHTML = ''; // Clear the alert message
                    }
                    opacity -= 0.1;
                    alertPlaceholder.style.opacity = opacity;
                }, 300); // Decrease opacity every 300ms
            })
            .catch(error => {
                console.error('Error deleting user:', error);
            });
    });


});