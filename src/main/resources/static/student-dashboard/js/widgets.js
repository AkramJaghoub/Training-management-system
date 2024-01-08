document.addEventListener('DOMContentLoaded', function () {
    resetFiltersAndSearch();
    attachEventListeners();
    filterAds();
});

function attachEventListeners() {
    // Kebab menu icon event listeners
    document.querySelectorAll('.menu-icon').forEach(function (menuIcon) {
        menuIcon.addEventListener('click', function (event) {
            event.stopPropagation();
            const adId = this.dataset.adId;
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
    document.getElementById('modeFilter').value = 'all';
    document.getElementById('typeFilter').value = 'all';
}

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

function showDescription(adId) {
    const descriptionContainer = document.getElementById('description-container-' + adId);
    const ad = {
        city: descriptionContainer.getAttribute('data-city'),
        country: descriptionContainer.getAttribute('data-country'),
        companyName: descriptionContainer.getAttribute('data-company-name'),
        description: descriptionContainer.getAttribute('data-description'),
        numOfEmployees: descriptionContainer.getAttribute('data-num-of-employees')
    };

    let companyInfo = `<strong>Company:</strong> <span style="color: #000">${ad.companyName}</span>`;
    if (ad.numOfEmployees) {
        companyInfo += `(Employees: ${ad.numOfEmployees})`; //TODO add range like linkedin
    }
    companyInfo += `<br>`;

    const modalContentHtml = `
        ${companyInfo}
        <strong>Location:</strong> <span style="color: #000">${ad.city}, ${ad.country}</span><br>
        ${ad.description}`;

    // Set the prepared content in the modal's body
    const modalBody = document.querySelector('#descriptionModal .modal-body');
    modalBody.innerHTML = modalContentHtml;

    const advertisementData = document.getElementById('description-container-' + adId);
    const applicationLink = advertisementData.getAttribute('data-application-link');

    // Set the application link on the Apply button
    const applyButton = document.getElementById('applyButton');
    applyButton.onclick = function() {
        window.open(applicationLink, '_blank');
    };

    new bootstrap.Modal(document.getElementById('descriptionModal')).show();
}

function filterAds() {
    let searchInput = document.getElementById('searchInput').value.toLowerCase();
    let modeFilter = document.getElementById('modeFilter').value.toLowerCase();
    let typeFilter = document.getElementById('typeFilter').value.toLowerCase();

    let adsList = document.querySelectorAll('.advertisement-widget');

    let activeWidgets = 0;
    let totalAds = adsList.length;
    let filtersApplied = searchInput !== '' || modeFilter !== '' || typeFilter !== '';

    adsList.forEach(function (ad) {
        let mode = ad.dataset.workMode ? ad.dataset.workMode.toLowerCase() : '';
        let type = ad.dataset.jobType ? ad.dataset.jobType.toLowerCase() : '';
        let modeMatch = modeFilter === 'all' || mode === modeFilter;
        let typeMatch = typeFilter === 'all' || type === typeFilter;
        let jobTitle = ad.querySelector('h4').textContent.toLowerCase();
        let companyName = ad.querySelector('[data-company-name]').getAttribute('data-company-name').toLowerCase();
        let textMatch = !searchInput || jobTitle.includes(searchInput) || companyName.includes(searchInput);

        if (modeMatch && typeMatch && textMatch) {
            ad.style.display = '';
            activeWidgets++;
        } else {
            ad.style.display = 'none';
        }
    });

    const noAdsMessageElement = document.getElementById('noAdsMessage');

    if (activeWidgets === 0 && totalAds > 0 && filtersApplied) {
        noAdsMessageElement.style.display = 'block';
    } else {
        noAdsMessageElement.style.display = 'none';
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