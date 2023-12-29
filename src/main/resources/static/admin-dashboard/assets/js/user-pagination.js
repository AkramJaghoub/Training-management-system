document.addEventListener('DOMContentLoaded', function () {

    //TODO: fix pagination on deletion of a user

    const rowsPerPage = 10;
    const rows = document.querySelectorAll('#user-list-table tbody tr');
    const rowsCount = rows.length;
    const pageCount = Math.ceil(rowsCount / rowsPerPage);
    const paginationContainer = document.getElementById('pagination');

    function displayPage(page) {
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
                updatePagination(pageNumber);
                displayPage(pageNumber);
            }
        });

        li.appendChild(a);
        return li;
    }

    function updatePagination(currentPage) {
        paginationContainer.innerHTML = ''; // Clear existing pagination items

        // "Previous" button
        paginationContainer.appendChild(createPaginationItem(currentPage - 1, false, currentPage === 1, 'Previous'));

        for (let i = 1; i <= pageCount; i++) {
            paginationContainer.appendChild(createPaginationItem(i, i === currentPage));
        }

        // "Next" button
        paginationContainer.appendChild(createPaginationItem(currentPage + 1, false, currentPage === pageCount, 'Next'));
    }

    displayPage(1); // Initialize the first page
    updatePagination(1); // Initialize pagination
});
