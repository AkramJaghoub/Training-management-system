document.addEventListener('DOMContentLoaded', function() {

    function showDeleteDialog(email, userId) {
        const dialog = document.getElementById('deleteDialog');
        const userEmailToDelete = document.getElementById('userEmailToDelete');

        userEmailToDelete.textContent = `Are you sure you want to delete user with email ${email}?`;
        dialog.style.opacity = '1';
        dialog.style.display = 'flex';
        document.getElementById('confirmDelete').dataset.userId = userId;
    }

    function hideDeleteDialog() {
        const dialog = document.getElementById('deleteDialog');
        dialog.style.opacity = '0';
        setTimeout(() => {
            dialog.style.display = 'none';
        }, 500); // Delay to allow the opacity transition to finish
    }

    const userTable = document.getElementById('user-list-table');
    userTable.addEventListener('click', function (event) {
        let target = event.target;

        // Check if the clicked target is within the delete button area
        while (target != null && !target.classList.contains('btn-icon')) {
            target = target.parentNode;
        }

        if (target && target.classList.contains('btn-icon')) {
            const email = target.closest('tr').getAttribute('data-user-email');
            const userId = target.closest('tr').getAttribute('data-user-id');

            showDeleteDialog(email, userId);
        }
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

                // Show success alert
                const alertPlaceholder = document.getElementById('alertPlaceholder');
                alertPlaceholder.innerHTML = `<div class="alert alert-success" role="alert">${message}</div>`;
                alertPlaceholder.style.display = 'block';

                setTimeout(() => {
                    alertPlaceholder.style.display = 'none';
                    alertPlaceholder.innerHTML = '';
                }, 3000); // Hide the alert after 3 seconds
            })
            .catch(error => {
                console.error('Error deleting user:', error);
            });
    });
});