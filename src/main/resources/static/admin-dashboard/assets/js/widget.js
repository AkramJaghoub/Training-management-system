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
    document.getElementById('statusFilter').value = 'all';
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

function filterAds() {
    let searchInput = document.getElementById('searchInput').value.toLowerCase();
    let statusFilter = document.getElementById('statusFilter').value.toLowerCase();

    let adsList = document.querySelectorAll('.advertisement-widget');

    let activeWidgets = 0;
    let totalAds = adsList.length; // Get the total number of ads before filtering
    let filtersApplied = searchInput !== '' || statusFilter !== 'all'; // Check if any filters are applied

    adsList.forEach(function (ad) {
        let status = ad.dataset.adStatus ? ad.dataset.adStatus.toLowerCase() : '';
        let jobTitle = ad.querySelector('h4').textContent.toLowerCase();
        let companyName = ad.querySelector('[data-company-name]').getAttribute('data-company-name').toLowerCase();
        let textMatch = !searchInput || jobTitle.includes(searchInput) || companyName.includes(searchInput);
        let statusMatch = statusFilter === 'all' || status === statusFilter;

        if (textMatch && statusMatch) {
            ad.style.display = '';
            activeWidgets++;
        } else {
            ad.style.display = 'none';
        }
    });

    // Get the "no ads" message element
    const noAdsMessageElement = document.getElementById('noAdsMessage');

    // Check if there are no active widgets, filters are applied, and there are ads available to filter
    if (activeWidgets === 0 && filtersApplied && totalAds > 0) {
        noAdsMessageElement.style.display = 'block'; // Show the message
    } else {
        noAdsMessageElement.style.display = 'none'; // Hide the message
    }

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

function askForConfirmation(adId, newStatus, event) {
    if (event) {
        event.preventDefault();
    }

    // Get references to modal elements
    const confirmationModal = new bootstrap.Modal(document.getElementById('confirmationModal'));
    const modalTitle = document.getElementById('confirmationModalLabel');
    const actionTypeElement = document.getElementById('actionType');
    const companyNameElement = document.getElementById('companyNameModal');
    const confirmBtn = document.getElementById('confirmBtn');

    // Get the ad's information
    const adWidget = document.querySelector(`[data-id="${adId}"]`);
    const jobTitle = adWidget.querySelector('h4').textContent;
    const companyName = adWidget.querySelector('[data-company-name]').dataset.companyName;

    const currentStatus = adWidget.dataset.adStatus.toUpperCase();
    console.log("Current status:", currentStatus, "New status:", newStatus);

    const actionText = newStatus === 'APPROVED' ? 'approve' : 'reject';

    // Ensure case-insensitive comparison
    if (newStatus.toUpperCase() === currentStatus) {
        showWarningAlert(`This advertisement is already ${currentStatus.toLowerCase()}`);
        return;
    }

    // Update the modal elements
    modalTitle.textContent = `Confirm ${actionText}`;
    actionTypeElement.textContent = actionText;
    companyNameElement.textContent = `[${jobTitle}] for [${companyName}]`;

    confirmBtn.onclick = function() {
        updateAdStatus(adId, newStatus);
        confirmationModal.hide();
    };
    // Show the modal
    confirmationModal.show();
}

function updateAdStatus(adId, newStatus) {
    const url = `/admin/update/ad-status/${adId}`;
    const headers = new Headers();
    headers.append('Content-Type', 'application/json');
    headers.append('newStatus', newStatus);

    fetch(url, {
        method: 'PUT',
        headers: headers
    })
        .then(response => {
            if (!response.ok) {
                throw new Error('Network response was not ok: ' + response.statusText);
            }
            const contentType = response.headers.get('content-type');
            if (contentType && contentType.includes('application/json')) {
                return response.json();
            } else {
                throw new Error('Received non-JSON response from server');
            }
        })
        .then(data => {
            console.log('Success:', data.message);
            const adWidget = document.querySelector(`[data-id="${adId}"]`);
            const statusElement = adWidget.querySelector('.ad-status');

            // Update the visual status
            statusElement.textContent = newStatus;
            adWidget.classList.remove('approved', 'rejected');
            adWidget.classList.add(newStatus.toLowerCase());

            // Update the data-ad-status attribute
            adWidget.dataset.adStatus = newStatus;

            // Show success message
            showSuccessAlert(data.message);
        })
        .catch((error) => {
            console.error('Error:', error);
        });
}

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

function showWarningAlert(message) {
    const alertBox = document.getElementById('errorAlert');
    const messageParagraph = document.getElementById('errorMessage');

    messageParagraph.textContent = message;
    alertBox.style.display = 'block';
    alertBox.style.opacity = 1; // Set initial opacity to 1

    setTimeout(() => {
        let opacity = 1;
        const fadeInterval = setInterval(() => {
            if (opacity <= 0) {
                clearInterval(fadeInterval);
                alertBox.style.display = 'none';
            } else {
                opacity -= 0.05; // Decrease the opacity
                alertBox.style.opacity = opacity;
            }
        }, 50);
    }, 4000);
}