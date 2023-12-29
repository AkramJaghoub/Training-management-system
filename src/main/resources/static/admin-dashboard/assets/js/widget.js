document.addEventListener('DOMContentLoaded', function () {
    resetFiltersAndSearch();
    attachEventListeners();
    filterAds();
    updateAdStatusClasses(); // Call the function here
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
    let statusFilter = document.getElementById('statusFilter').value.toLowerCase();

    let adsList = document.querySelectorAll('.advertisement-widget');

    let activeWidgets = 0;

    adsList.forEach(function (ad) {
        let status = ad.dataset.adStatus ? ad.dataset.adStatus.toLowerCase() : '';
        let jobTitle = ad.querySelector('h4').textContent.toLowerCase();
        let companyName = ad.querySelector('[data-company-name]').getAttribute('data-company-name').toLowerCase();
        let textMatch = !searchInput || jobTitle.includes(searchInput) || companyName.includes(searchInput);
        let statusMatch = statusFilter === 'all' || status === statusFilter;
        ad.style.display = (textMatch && statusMatch) ? '' : 'none';
        if (textMatch && statusMatch)
            activeWidgets++;
    });
    initializePagination(activeWidgets);
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

function displayPageForFilters(page) {
    const widgets = document.querySelectorAll('.advertisement-widget');
    const visibleWidgets = Array.from(widgets).filter(widget => {
        return window.getComputedStyle(widget).display !== 'none';
    });

    visibleWidgets.forEach((widget, index) => {
        const startIndex = (page - 1) * widgetsPerPage;
        const endIndex = startIndex + widgetsPerPage;
        widget.style.display = (index >= startIndex && index < endIndex) ? '' : 'none';
    });
}


function initializePagination(activeWidgets) {
    pageCount = Math.ceil(activeWidgets / widgetsPerPage);
    updatePagination(1);
    displayPageForFilters(1);
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
    if (pageCount === 0 || pageCount === 1) return;

    paginationContainer.appendChild(createPaginationItem(currentPage - 1, false, currentPage === 1, 'Previous'));

    for (let i = 1; i <= pageCount; i++) {
        paginationContainer.appendChild(createPaginationItem(i, i === currentPage));
    }

    paginationContainer.appendChild(createPaginationItem(currentPage + 1, false, currentPage === pageCount, 'Next'));
}

function updateAdStatusClasses() {
    document.querySelectorAll('.advertisement-widget').forEach(widget => {
        // Remove existing status classes
        widget.classList.remove('approved', 'rejected');

        // Add new status class based on adStatus
        const adStatus = widget.dataset.adStatus.toLowerCase();
        console.log(`Ad ID: ${widget.dataset.id}, Status: ${adStatus}`); // Corrected debugging line

        if (adStatus === 'approved') {
            widget.classList.add('approved');
        } else if (adStatus === 'rejected') {
            widget.classList.add('rejected');
        }
        // No need for 'pending' as it's the default state
    });
}