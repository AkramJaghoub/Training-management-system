let currentPage = 1;

document.addEventListener('DOMContentLoaded', function() {
    const rowsPerPage = 10;
    const paginationContainer = document.getElementById('pagination');

    function displayPage(page) {
        const rows = document.querySelectorAll('#user-list-table tbody tr');
        rows.forEach((row, index) => {
            row.style.display = 'none';
            if (index >= (page - 1) * rowsPerPage && index < page * rowsPerPage) {
                row.style.display = '';
            }
        });
    }

    function createPaginationItem(pageNumber, isActive, isDisabled, text) {
        const li = document.createElement('li');
        li.className = 'page-item';
        if (isActive) li.classList.add('active');
        if (isDisabled) li.classList.add('disabled');

        const a = document.createElement('a');
        a.className = 'page-link';
        a.href = '#';
        a.innerText = text || pageNumber;
        a.addEventListener('click', (e) => {
            e.preventDefault();
            if (!isDisabled) {
                currentPage = pageNumber;
                updatePagination();
                displayPage(pageNumber);
            }
        });

        li.appendChild(a);
        return li;
    }


    function updatePagination() {
        const rowsCount = document.querySelectorAll('#user-list-table tbody tr').length;
        const pageCount = Math.ceil(rowsCount / rowsPerPage);
        paginationContainer.innerHTML = '';

        paginationContainer.appendChild(createPaginationItem(currentPage - 1, false, currentPage === 1, 'Previous'));

        for (let i = 1; i <= pageCount; i++) {
            paginationContainer.appendChild(createPaginationItem(i, i === currentPage));
        }

        paginationContainer.appendChild(createPaginationItem(currentPage + 1, false, currentPage === pageCount, 'Next'));
    }

    function refreshCurrentPage() {
        const allRows = Array.from(document.querySelectorAll('#user-list-table tbody tr'));
        const rowsCount = allRows.length;
        const pageCount = Math.ceil(rowsCount / rowsPerPage);

        // Adjust current page if it exceeds the total number of pages
        if (currentPage > pageCount) {
            currentPage = pageCount > 0 ? pageCount : 1;
        }

        const currentStartIndex = (currentPage - 1) * rowsPerPage;
        const currentEndIndex = currentStartIndex + rowsPerPage;

        // Hide all rows first
        allRows.forEach(row => row.style.display = 'none');

        // Show only the rows for the current page
        for (let i = currentStartIndex; i < currentEndIndex && i < rowsCount; i++) {
            allRows[i].style.display = '';
        }
    }


    updatePagination();
    displayPage(currentPage);



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
        }, 500);
    }

    const userTable = document.getElementById('user-list-table');
    userTable.addEventListener('click', function(event) {
        let target = event.target;

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

    document.getElementById('confirmDelete').addEventListener('click', function() {
        const userId = this.dataset.userId;

        fetch(`/admin/delete/user/${userId}`, {
            method: 'DELETE'
        })
            .then(response => {
                if (!response.ok) {
                    throw new Error('Network response was not ok');
                }
                return response.text();
            })
            .then(message => {
                hideDeleteDialog();
                const userRow = document.querySelector(`tr[data-user-id="${userId}"]`);
                if (userRow) {
                    userRow.remove();
                }

                refreshCurrentPage();
                updatePagination();
                displayPage(currentPage); // This might now be redundant since refreshCurrentPage handles displaying

                const alertPlaceholder = document.getElementById('alertPlaceholder');
                alertPlaceholder.innerHTML = `<div class="alert alert-success d-flex align-items-center" role="alert">${message}</div>`;
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