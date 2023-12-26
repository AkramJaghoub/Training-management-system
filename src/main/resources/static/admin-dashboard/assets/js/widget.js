document.addEventListener('DOMContentLoaded', function () {
    // Reset filters and search input on page load
    resetFiltersAndSearch();

    // Attach event listeners
    attachEventListeners();

    // Initialize filtering once to apply any default values
    filterAds();

    // Initialize Pagination and Display only the first page of widgets
    initializePagination();
    displayPage(1);
});

// Attach click event listeners to kebab menu icons
function attachEventListeners() {
    // Kebab menu icon event listeners
    document.querySelectorAll('.menu-icon').forEach(function (menuIcon) {
        menuIcon.addEventListener('click', function (event) {
            event.stopPropagation();
            const adId = this.dataset.adId;

            // Toggle the kebab menu for the clicked icon
            toggleKebabMenu(adId);
        });
    });

    // Window click to close kebab menus
    window.addEventListener('click', function () {
        closeAllKebabMenus();
    });

    // Modal close buttons
    const closeButtons = document.querySelectorAll('#descriptionModal .btn-secondary');
    closeButtons.forEach(function (closeButton) {
        closeButton.addEventListener('click', function () {
            hideModal('descriptionModal');
        });
    });

    // Modal outside click
    const modalElement = document.getElementById('descriptionModal');
    modalElement.addEventListener('click', function (event) {
        if (event.target === modalElement) {
            hideModal('descriptionModal');
        }
    });

    // Filter change events
    document.getElementById('modeFilter').addEventListener('change', filterAds);
    document.getElementById('typeFilter').addEventListener('change', filterAds);
    document.getElementById('searchInput').addEventListener('input', filterAds);
}

// Function to hide the modal
function hideModal(modalId) {
    const modalElement = document.getElementById(modalId);
    if (modalElement) {
        modalElement.classList.remove('show');
        modalElement.style.display = 'none';
        document.body.classList.remove('modal-open');

        const backdrops = document.getElementsByClassName('modal-backdrop');
        while (backdrops[0]) {
            backdrops[0].parentNode.removeChild(backdrops[0]);
        }
    }
}

function resetFiltersAndSearch() {
    document.getElementById('modeFilter').value = 'all';
    document.getElementById('typeFilter').value = 'all';
    document.getElementById('searchInput').value = '';
}

// Toggles the display of the kebab menu for the given ad ID
function toggleKebabMenu(adId) {
    const currentMenu = document.getElementById('menu-dropdown-' + adId);
    if (currentMenu) {
        currentMenu.style.display = currentMenu.style.display === 'block' ? 'none' : 'block';
    }
    // Close other menus
    document.querySelectorAll('.menu-container').forEach(function (menu) {
        if (menu.id !== 'menu-dropdown-' + adId) {
            menu.style.display = 'none';
        }
    });
}

function closeAllKebabMenus() {
    document.querySelectorAll('.menu-container').forEach(function (menu) {
        menu.style.display = 'none';
    });
}


// Shows the description in a modal for the given ad ID
function showDescription(adId) {
    // Fetch the description text from the hidden container
    const descriptionContainer = document.getElementById('description-container-' + adId);
    const ad = {
        city: descriptionContainer.getAttribute('data-city'),
        country: descriptionContainer.getAttribute('data-country'),
        // Assuming companyName is stored in a data attribute
        companyName: descriptionContainer.getAttribute('data-company-name'),
        description: descriptionContainer.getAttribute('data-description'),
    };

    const modalContentHtml = `
        <strong>Company:</strong> ${ad.companyName}<br>
        <strong>Location:</strong> ${ad.city}, ${ad.country}<br>
        <p>${ad.description}</p>
    `;

    // Set the prepared content in the modal's body
    const modalBody = document.querySelector('#descriptionModal .modal-body');
    modalBody.innerHTML = modalContentHtml; // Use innerHTML as we are setting HTML content

    // Show the modal using Bootstrap's JavaScript API
    new bootstrap.Modal(document.getElementById('descriptionModal')).show();
}

// Approves an advertisement
function approve(adId) {
    console.log('Approving ad with ID:', adId);
    // Your logic for approving the advertisement
    closeAllKebabMenus();
}

// Declines an advertisement
function decline(adId) {
    console.log('Declining ad with ID:', adId);
    // Your logic for declining the advertisement
    closeAllKebabMenus();
}


function filterAds() {
    let searchInput = document.getElementById('searchInput').value.toLowerCase();
    let modeFilter = document.getElementById('modeFilter').value.toLowerCase();
    let typeFilter = document.getElementById('typeFilter').value.toLowerCase();
    let adsList = document.querySelectorAll('.advertisement-widget');

    adsList.forEach(function (ad) {
        let mode = ad.dataset.workMode ? ad.dataset.workMode.toLowerCase() : '';
        let type = ad.dataset.jobType ? ad.dataset.jobType.toLowerCase() : '';
        let jobTitle = ad.querySelector('h4').textContent.toLowerCase();
        let companyName = ad.querySelector('[data-company-name]').getAttribute('data-company-name').toLowerCase();
        let textMatch = !searchInput || jobTitle.includes(searchInput) || companyName.includes(searchInput);
        let modeMatch = modeFilter === 'all' || mode === modeFilter;
        let typeMatch = typeFilter === 'all' || type === typeFilter;
        ad.style.display = (modeMatch && typeMatch && textMatch) ? '' : 'none';
    });
    initializePagination();
}

const widgetsPerPage = 9;
let pageCount;
const paginationContainer = document.getElementById('ads-pagination');

function displayPage(page) {
    const widgets = document.querySelectorAll('.advertisement-widget');
    widgets.forEach((widget, index) => {
        const startIndex = (page - 1) * widgetsPerPage;
        const endIndex = startIndex + widgetsPerPage;
        widget.style.display = (index >= startIndex && index < endIndex) ? '' : 'none';
    });
}

function initializePagination() {
    const visibleWidgets = document.querySelectorAll('.advertisement-widget:not([style*="display: none"])');
    const widgetsCount = visibleWidgets.length;
    pageCount = Math.ceil(widgetsCount / widgetsPerPage);

    updatePagination(1); // Initialize the pagination
}

function createPaginationItem(pageNumber, isActive, isDisabled, text) {
    const li = document.createElement('li');
    li.className = 'page-item';
    if (isActive) li.classList.add('active');
    if (isDisabled) li.classList.add('disabled');

    const a = document.createElement('a');
    a.className = 'page-link';
    a.href = '#';
    a.textContent = text || pageNumber;

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
    paginationContainer.innerHTML = '';
    if (currentPage === 0) return;

    paginationContainer.appendChild(createPaginationItem(currentPage - 1, false, currentPage === 1, 'Previous'));

    for (let i = 1; i <= pageCount; i++) {
        paginationContainer.appendChild(createPaginationItem(i, i === currentPage));
    }

    paginationContainer.appendChild(createPaginationItem(currentPage + 1, false, currentPage === pageCount, 'Next'));
}