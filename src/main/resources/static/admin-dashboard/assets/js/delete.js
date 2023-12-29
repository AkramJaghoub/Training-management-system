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
            event.preventDefault();

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

                const alertPlaceholder = document.getElementById('alertPlaceholder');
                alertPlaceholder.innerHTML = `
    <div class="alert alert-success d-flex align-items-center" role="alert">
        <svg xmlns="http://www.w3.org/2000/svg" width="18" height="18" fill="currentColor" class="bi bi-check-circle-fill me-2" viewBox="0 0 16 16">
            <path d="M16 8A8 8 0 1 1 0 8a8 8 0 0 1 16 0m-3.97-3.03a.75.75 0 0 0-1.08.022L7.477 9.417 5.384 7.323a.75.75 0 0 0-1.06 1.06L6.97 11.03a.75.75 0 0 0 1.079-.02l3.992-4.99a.75.75 0 0 0-.01-1.05z"/>
        </svg>
        ${message}
    </div>`;
                alertPlaceholder.style.display = 'block';

                setTimeout(() => {
                    alertPlaceholder.style.display = 'none';
                    alertPlaceholder.innerHTML = '';
                }, 3000);
            })
            .catch(error => {
                console.error('Error deleting user:', error);
            });
    });
});