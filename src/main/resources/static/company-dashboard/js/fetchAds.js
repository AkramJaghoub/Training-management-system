document.addEventListener('DOMContentLoaded', function () {
    resetFiltersAndSearch();
    attachEventListeners();
    filterAds();
    fetchAllCountries();
    updateAdStatusClasses();
});

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
        <strong>Company:</strong> <span style="color: #000">${ad.companyName}</span><br>
        <strong>Location:</strong> <span style="color: #000">${ad.city}, ${ad.country}</span><br>
         ${ad.description}  `;

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
        let textMatch = !searchInput || jobTitle.includes(searchInput);
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
        widget.classList.remove('approved', 'rejected');

        const adStatus = widget.dataset.adStatus.toLowerCase();

        if (adStatus === 'approved') {
            widget.classList.add('approved');
        } else if (adStatus === 'rejected') {
            widget.classList.add('rejected');
        }
    });
}

function askForConfirmation(adId, action, event) {
    if (event) {
        event.preventDefault();
        event.stopPropagation();
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

    const isDeleteAction = action === 'Delete';
    const actionText = isDeleteAction ? 'delete' : 'update';

    // Allow the modal to show for all statuses
    modalTitle.textContent = `Confirm ${actionText}`;
    actionTypeElement.textContent = actionText;
    companyNameElement.textContent = `[${jobTitle}]`;

    confirmBtn.onclick = function () {
        deleteAdvertisement(adId);
        confirmationModal.hide();
    };
    // Show the modal
    confirmationModal.show();
}

document.getElementById('updateAdButton').addEventListener('click', function () {
    document.getElementById('jobTitleError').textContent = '';
    document.getElementById('internsRequiredError').textContent = '';
    document.getElementById('jobDurationError').textContent = '';
    document.getElementById('countrySelectError').textContent = '';
    document.getElementById('citySelectError').textContent = '';
    document.getElementById('jobTypeError').textContent = '';
    document.getElementById('workModeError').textContent = '';
    document.getElementById('jobImageError').textContent = '';
    document.getElementById('descriptionError').textContent = '';
    document.getElementById('applicationLinkError').textContent = '';

    // Gather the updated data from the form fields
    const adId = document.getElementById('adId').value;
    const updatedJobTitle = document.getElementById('jobTitle').value;
    const updatedInternsRequired = document.getElementById('internsRequired').value;
    const updatedJobDuration = document.getElementById('jobDuration').value;
    const updatedCountry = document.getElementById('countrySelect').value;
    const updatedCity = document.getElementById('citySelect').value;
    const updatedJobType = document.querySelector('input[name="jobType"]:checked').value;
    const updatedWorkMode = document.getElementById('workMode').value;
    const updatedJobImage = document.getElementById('jobImage').files[0];
    const applicationLink = document.getElementById('applicationLink').value;
    const description = tinymce.get('editor').getContent();

    let isValid = true;

    if (updatedJobTitle.trim() === "") {
        document.getElementById('jobTitleError').textContent = 'Job Title is required';
        isValid = false;
    }
    if (updatedInternsRequired.trim() === "") {
        document.getElementById('internsRequiredError').textContent = 'Number of Interns is required';
        isValid = false;
    }
    if (updatedJobDuration.trim() === "") {
        document.getElementById('jobDurationError').textContent = 'Job Duration is required';
        isValid = false;
    }
    if (updatedCountry.trim() === "") {
        document.getElementById('countrySelectError').textContent = 'Country selection is required';
        isValid = false;
    }
    if (updatedCountry.trim() !== "" && updatedCity.disabled === false && updatedCity.value.trim() === "") {
        document.getElementById('citySelectError').textContent = 'City selection is required';
        isValid = false;
    }
    if (!updatedJobType) {
        document.getElementById('jobTypeError').textContent = 'Selecting a Job Type is required';
        isValid = false;
    }
    if (updatedWorkMode.trim() === "") {
        document.getElementById('workModeError').textContent = 'Work Mode selection is required';
        isValid = false;
    }
    if (updatedJobImage === 0) {
        document.getElementById('jobImageError').textContent = 'Uploading a Job Image is required';
        isValid = false;
    }
    if (description.trim() === "") {
        document.getElementById('descriptionError').textContent = 'Job Description is required';
        isValid = false;
    }
    if (applicationLink.trim() === "") {
        document.getElementById('applicationLinkError').textContent = 'Application Link is required';
        isValid = false;
    }

    if (!isValid) {
        return;
    }

    // Validate Number of Interns
    if (isNaN(updatedInternsRequired)) {
        document.getElementById('internsRequiredError').textContent = 'Please enter a valid number for interns required.';
        isValid = false;
    }

    // Validate Job Duration
    if (isNaN(updatedJobDuration)) {
        document.getElementById('jobDurationError').textContent = 'Please enter a valid number for job duration.';
        isValid = false;
    }

    if (!isValid) {
        return;
    }

    const formData = new FormData();
    formData.append('id', adId);
    formData.append('jobTitle', updatedJobTitle);
    formData.append('internsRequired', updatedInternsRequired);
    formData.append('jobDuration', updatedJobDuration);
    formData.append('country', updatedCountry);
    formData.append('city', updatedCity);
    formData.append('jobType', updatedJobType);
    formData.append('workMode', updatedWorkMode);
    formData.append('jobImage', updatedJobImage);
    formData.append('description', description);
    formData.append('applicationLink', applicationLink);

    // Send the updated data to the backend endpoint
    const updateUrl = `/advertisement/update`;
    fetch(updateUrl, {
        method: 'PUT',
        body: formData,
    })
        .then(response => {
            if (response.ok) {
                response.json().then(apiResponse => {
                    showSuccessAlert(apiResponse.message);

                    const updateAdModalElement = document.getElementById('updateAdModal');
                    const updateAdModal = bootstrap.Modal.getInstance(updateAdModalElement);
                    if (updateAdModal) {
                        updateAdModal.hide();
                    }

                    updateAdWidget(adId, {
                        jobTitle: updatedJobTitle,
                        internsRequired: updatedInternsRequired,
                        jobDuration: updatedJobDuration,
                        country: updatedCountry,
                        city: updatedCity,
                        jobType: updatedJobType,
                        workMode: updatedWorkMode,
                        applicationLink: applicationLink,
                        description: description,
                    });
                });
            } else if (response.status === 400) {
                // Handle 400 error - Invalid request
                response.json().then(apiResponse => {
                    // Display the error message
                    const generalError = document.getElementById('generalError');
                    generalError.textContent = apiResponse.message;
                });
            } else {
                // Handle other errors
                console.error("Failed to update advertisement");
            }
        })
        .catch(error => {
            // Handle network error
            console.error('Error:', error);
        });
});


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

function updateAdWidget(adId, updatedData) {
    const adWidget = document.querySelector(`[data-id="${adId}"]`);
    if (adWidget) {
        adWidget.querySelector('h4').textContent = updatedData.jobTitle;
        adWidget.querySelector('[data-interns-required]').textContent = updatedData.internsRequired;
        adWidget.querySelector('[data-job-duration]').textContent = updatedData.jobDuration;
        adWidget.querySelector('[data-job-type]').textContent = updatedData.jobType;
        adWidget.querySelector('[data-work-mode]').textContent = updatedData.workMode;
        adWidget.querySelector('[data-description]').setAttribute('data-description', updatedData.description);

        const citySpan = adWidget.querySelector('[data-city]');
        if (citySpan) {
            citySpan.textContent = updatedData.city;
        }

        const countrySpan = adWidget.querySelector('[data-country]');
        if (countrySpan) {
            countrySpan.textContent = updatedData.country;
        }
    }
}

function deleteAdvertisement(adId) {
    const url = `/advertisement/delete/${adId}`;

    // Send a DELETE request to the server
    fetch(url, {
        method: 'DELETE'
    })
        .then(response => {
            if (!response.ok) {
                throw new Error('Failed to delete advertisement');
            }
            return response.json();
        })
        .then(apiResponse => {
            showSuccessAlert(apiResponse.message);
            const adWidget = document.querySelector(`[data-id="${adId}"]`);
            if (adWidget) {
                adWidget.remove();
            }
        })
        .catch(error => {
            // Handle network errors
            console.error('Network error:', error);
        });
}

async function showUpdateForm(advertisementId) {
    // Retrieve the existing data from the widget with the given advertisementId
    const adWidget = document.querySelector(`[data-id="${advertisementId}"]`);
    if (!adWidget) {
        console.error(`Advertisement widget with ID ${advertisementId} not found.`);
        return;
    }

    document.getElementById('adId').value = advertisementId;

    // Extract data from the widget
    const jobTitle = adWidget.querySelector('h4').textContent;
    const internsRequired = adWidget.querySelector('[data-interns-required]').textContent;
    const jobDuration = adWidget.querySelector('[data-job-duration]').textContent;
    const jobType = adWidget.querySelector('[data-job-type]').textContent;
    const workMode = adWidget.querySelector('[data-work-mode]').textContent;
    const descriptionContainer = document.getElementById('description-container-' + advertisementId);
    const selectedCountry = descriptionContainer.getAttribute('data-country');
    const selectedCity = descriptionContainer.getAttribute('data-city');
    const description = descriptionContainer.getAttribute('data-description');
    const applicationLink = descriptionContainer.getAttribute('data-application-link');
    const compressedImageData = adWidget.getAttribute('data-image');

    document.getElementById('jobTitle').value = jobTitle;
    document.getElementById('internsRequired').value = internsRequired;
    document.getElementById('jobDuration').value = jobDuration;

    if (jobType === 'FULL_TIME') {
        document.getElementById('fullTime').checked = true;
    } else if (jobType === 'PART_TIME') {
        document.getElementById('partTime').checked = true;
    }

    document.getElementById('workMode').value = workMode;

    if (tinymce.get('editor')) {
        tinymce.get('editor').setContent(description);
    } else {
        tinymce.init({
            selector: 'textarea#editor',
            setup: function(editor) {
                editor.on('init', function() {
                    editor.setContent(description);
                });
            }
        });
    }

    document.getElementById('applicationLink').value = applicationLink;

    const jobImageInput = document.getElementById('jobImage');
    if (jobImageInput) {
        // Create a Blob with the appropriate type
        const blob = new Blob([compressedImageData], {type: `image/jpeg`});

        // Create a File from the Blob
        const file = new File([blob], 'jobImage.jpg', {type: 'image/jpeg'});

        // Create a new DataTransfer object and add the File to it
        const dataTransfer = new DataTransfer();
        dataTransfer.items.add(file);

        // Set the files property of the input element to the File
        jobImageInput.files = dataTransfer.files;

        // Read the content of the file as a data URL
        const reader = new FileReader();
        reader.onload = function (event) {
            const fileContent = event.target.result;
            console.log("File Content:", fileContent);
        };
        reader.readAsDataURL(file);
    }

    // Set the selected country in the country select element
    const countrySelect = document.getElementById('countrySelect');
    countrySelect.value = selectedCountry;

    // Fetch cities for the selected country
    if (selectedCountry) {
        fetchCities(selectedCountry);

        // Wait for a moment to ensure cities are fetched before setting the selected city
        setTimeout(() => {
            document.getElementById('citySelect').value = selectedCity;
        }, 1000); // Adjust the delay as needed
    }

    // Reset the modal to ensure it's displayed correctly
    const updateAdModal = new bootstrap.Modal(document.getElementById('updateAdModal'));
    updateAdModal.hide(); // Hide the modal
    updateAdModal.show(); // Show the modal again
}

function fetchAllCountries() {
    fetch('https://restcountries.com/v2/all')
        .then(response => response.json())
        .then(countries => {
            globalCountries = countries; // Store the countries globally
        })
        .catch(error => console.error('Error fetching countries:', error));
}

// Function to fetch cities based on the selected country code
function fetchCities(countryCode) {
    const citySelect = document.getElementById('citySelect');
    citySelect.innerHTML = '<option value="">Loading Cities...</option>';

    // Replace 'your_username' with your actual username or API key
    const url = `http://api.geonames.org/searchJSON?country=${countryCode}&maxRows=12&username=your_username&cities=cities1000`;

    fetch(url)
        .then(response => response.json())
        .then(data => {
            citySelect.innerHTML = ''; // Clear the loading message
            data.geonames.forEach(city => {
                const option = document.createElement('option');
                option.value = city.name;
                option.textContent = city.name;
                citySelect.appendChild(option);
            });
        })
        .catch(error => console.error('Error fetching cities:', error));
}

// Example: Fetch cities when a country is selected
document.getElementById('countrySelect').addEventListener('change', function () {
    const selectedCountryCode = this.value;
    fetchCities(selectedCountryCode);
});