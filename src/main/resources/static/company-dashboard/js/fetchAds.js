let globalCountries = []; // Global variable to store countries

let selectedMode = 'all';
let selectedType = 'all';

document.addEventListener('DOMContentLoaded', function () {
    fetchAdvertisements();
    fetchAllCountries();

    // Set the initial values for mode and type to 'all'
    document.getElementById('mode').value = 'all';
    document.getElementById('type').value = 'all';

    document.getElementById('mode').addEventListener('change', function() {
        selectedMode = this.value;
        fetchAdvertisements();
    });

    document.getElementById('type').addEventListener('change', function() {
        selectedType = this.value;
        fetchAdvertisements();
    });
});

function fetchAdvertisements() {
    fetch('/advertisement/get')
        .then(response => response.json())
        .then(data => {
            updateAdvertisements(data);
        })
        .catch(error => console.error('Error fetching advertisements:', error));
}

function updateAdvertisements(advertisements) {
    const articlesContainer = document.querySelector('.articles');
    articlesContainer.innerHTML = '';

    if (advertisements.length === 0) {
        const noAdvertisementsMessage = document.createElement('div');
        noAdvertisementsMessage.classList.add('no-advertisements');
        noAdvertisementsMessage.textContent = 'No advertisements available right now.';
        articlesContainer.appendChild(noAdvertisementsMessage);
        return;
    }

    let filteredAds = advertisements;
    if (selectedMode !== 'all' || selectedType !== 'all') {
        filteredAds = filteredAds.filter(ad =>
            (selectedMode === 'all' || ad.workMode === selectedMode) &&
            (selectedType === 'all' || ad.jobType === selectedType)
        );
    }

    filteredAds.forEach((ad, index) => {
        const article = document.createElement('article'); // Define 'article' here
        article.classList.add('article'); // Add this line if you have CSS styles for 'article
        const articleBody = document.createElement('div');
        articleBody.classList.add('article-body');
        const title = document.createElement('h2');
        title.textContent = ad.jobTitle;

        article.dataset.adId = ad.id; // Store the advertisement ID

        const internsRequired = document.createElement('p');
        internsRequired.textContent = 'Interns Required: ' + ad.internsRequired;

        const duration = document.createElement('p');
        duration.textContent = 'Duration: ' + ad.jobDuration + ' months';

        const jobType = document.createElement('p');
        jobType.textContent = 'Type: ' + ad.jobType;

        const workMode = document.createElement('p');
        workMode.textContent = 'Mode: ' + ad.workMode;

        const readMoreLink = document.createElement('a');
        readMoreLink.href = '#';
        readMoreLink.textContent = 'View Description';
        readMoreLink.onclick = function (event) {
            event.preventDefault();
            event.stopPropagation(); // Prevents the event from bubbling up to parent elements
            showDescription(ad);
        };

        const kebabMenuId = 'kebabMenu' + index;
        const menuContainerId = 'menuContainer' + index;

        const kebabMenuSVG = document.createElementNS("http://www.w3.org/2000/svg", "svg");
        kebabMenuSVG.setAttributeNS(null, "width", "30");
        kebabMenuSVG.setAttributeNS(null, "height", "30");
        kebabMenuSVG.setAttributeNS(null, "fill", "currentColor");
        kebabMenuSVG.setAttributeNS(null, "viewBox", "0 0 16 16");
        kebabMenuSVG.classList.add('menu-icon');
        kebabMenuSVG.style.cursor = 'pointer'; // Ensure it looks clickable

// Create the SVG path
        const svgPath = document.createElementNS("http://www.w3.org/2000/svg", "path");
        svgPath.setAttributeNS(null, "d", "M9.5 13a1.5 1.5 0 1 1-3 0 1.5 1.5 0 0 1 3 0m0-5a1.5 1.5 0 1 1-3 0 1.5 1.5 0 0 1 3 0m0-5a1.5 1.5 0 1 1-3 0 1.5 1.5 0 0 1 3 0");
        kebabMenuSVG.appendChild(svgPath);

// Create the kebab menu div and append the SVG to it
        const kebabMenu = document.createElement('div');
        kebabMenu.id = kebabMenuId;
        kebabMenu.appendChild(kebabMenuSVG);

// Add click event listener to the SVG element
        kebabMenuSVG.addEventListener('click', function (event) {
            const currentMenu = document.getElementById(menuContainerId);
            currentMenu.style.display = currentMenu.style.display === 'none' ? 'block' : 'none';
            event.stopPropagation(); // Add this line to stop event propagation
        });

        const menuContainer = document.createElement('div');
        menuContainer.classList.add('menu-container');
        menuContainer.id = menuContainerId;
        menuContainer.style.display = 'none';

        const updateLink = document.createElement('a');
        updateLink.href = '#';
        updateLink.classList.add('update-link');
        updateLink.dataset.position = ad.jobTitle; // Assuming jobTitle is the unique identifier
        updateLink.onclick = function (event) {
            event.preventDefault();
            document.getElementById(menuContainerId).style.display = 'none';
            currentAd = advertisements.find(ad => ad.jobTitle === this.dataset.position); // Store the ad data
            showUpdateForm(currentAd); // Pass the ad to the update form
        };

        const updateIcon = document.createElement('iconify-icon');
        updateIcon.setAttribute('icon', 'bxs:edit');
        updateLink.appendChild(updateIcon);

        const updateText = document.createElement('span');
        updateText.textContent = 'Update';
        updateLink.appendChild(updateText);

        const deleteLink = document.createElement('a');
        deleteLink.textContent = 'Delete';
        deleteLink.href = '#';
        deleteLink.classList.add('delete-link');
        deleteLink.dataset.position = ad.jobTitle; // Assuming jobTitle is the unique identifier
        deleteLink.onclick = function (event) {
            event.preventDefault();
            document.getElementById(menuContainerId).style.display = 'none';
            currentAdPosition = this.dataset.position; // Store the position for later use
            showDeleteDialog();
        };

        const deleteIcon = document.createElement('iconify-icon');
        deleteIcon.setAttribute('icon', 'mdi:trash-outline');

        // Append the icon to the delete link
        deleteLink.prepend(deleteIcon); // This places the icon before the text


        menuContainer.appendChild(updateLink);
        menuContainer.appendChild(deleteLink);

        articleBody.appendChild(kebabMenu);
        articleBody.appendChild(menuContainer);

        articleBody.appendChild(title);
        articleBody.appendChild(internsRequired);
        articleBody.appendChild(duration);
        articleBody.appendChild(jobType);
        articleBody.appendChild(workMode);
        articleBody.appendChild(readMoreLink);


        // Append articleBody to article
        article.appendChild(articleBody);
        articlesContainer.appendChild(article);
    });
}

function showDescription(ad) {
    const location = document.createElement('div');
    location.innerHTML = '<strong>Location: </strong>' + ad.city + ', ' + ad.country + '<br>';
    location.classList.add('modal-location');

    const companyNameText = getCompanyNameFromCookie() || 'Unknown Company';
    const companyNameDiv = document.createElement('div');
    companyNameDiv.innerHTML = '<strong>Company: </strong>' + companyNameText;


    const modal = document.createElement('div');
    modal.classList.add('modal');

    const modalContent = document.createElement('div');
    modalContent.classList.add('modal-content');

    const closeSpan = document.createElement('span');
    closeSpan.classList.add('close');
    closeSpan.innerHTML = '&times;';
    closeSpan.onclick = function () {
        modal.style.display = "none";
        modal.remove();
    };

    const descriptionParagraph = document.createElement('p');
    descriptionParagraph.textContent = ad.description;

    modalContent.appendChild(closeSpan);
    modalContent.appendChild(location);
    modalContent.appendChild(companyNameDiv);
    modalContent.appendChild(descriptionParagraph);
    modal.appendChild(modalContent);

    document.body.appendChild(modal);

    modal.style.display = "block";
}

window.onclick = function (event) {
    const modals = document.getElementsByClassName('modal');

    for (let i = 0; i < modals.length; i++) {
        if (event.target === modals[i]) {
            modals[i].style.display = "none";
            modals[i].remove();
        }
    }
}

function getCompanyNameFromCookie() {
    const cookies = document.cookie.split('; ');
    const companyNameCookie = cookies.find(row => row.startsWith('companyName='));
    return companyNameCookie ? decodeURIComponent(companyNameCookie.split('=')[1]) : null;
}

window.addEventListener('click', function (event) {
    const openMenus = document.querySelectorAll('.menu-container');
    openMenus.forEach(menu => {
        // Check if the click is outside the menu
        if (!menu.contains(event.target)) {
            menu.style.display = 'none';
        }
    });
});

window.onclick = function (event) {
    if (!event.target.matches('.menu-icon')) {
        const menus = document.getElementsByClassName('menu-container');
        for (let i = 0; i < menus.length; i++) {
            let openMenu = menus[i];
            if (openMenu.style.display === 'block') {
                openMenu.style.display = 'none';
            }
        }
    }
};

window.onclick = function (event) {
    const updateModal = document.getElementById('myModal');
    if (event.target === updateModal) {
        updateModal.style.display = "none";
    }
}


document.addEventListener('DOMContentLoaded', function () {
    const companyName = getCompanyNameFromCookie();
    if (companyName) {
        document.getElementById('companyNameDisplay').textContent = companyName;
    }
});

function showDeleteDialog() {
    const dialog = document.getElementById('deleteDialog');
    dialog.style.display = 'flex';
    setTimeout(() => {
        dialog.style.opacity = '1'; // Fade in
    }, 10);
}

function hideDeleteDialog() {
    const dialog = document.getElementById('deleteDialog');
    dialog.style.opacity = '0'; // Fade out
    setTimeout(() => {
        dialog.style.display = 'none';
    }, 500); // Match the duration of the fade-out transition
}

document.getElementById('cancelDelete').addEventListener('click', function () {
    hideDeleteDialog();
});


function hideUpdateForm() {
    const dialog = document.getElementById('formDialog');
    dialog.style.opacity = '0'; // Fade out
    setTimeout(() => {
        dialog.style.display = 'none';
    }, 500); // Match the duration of the fade-out transition
}

document.getElementById('cancelUpdate').addEventListener('click', function () {
    hideUpdateForm()
});

document.getElementById('confirmDelete').addEventListener('click', function () {
    if (currentAdPosition) {
        fetch(`/advertisement/delete/${currentAdPosition}`, {
            method: 'DELETE',
            // Add any necessary headers and body
        })
            .then(response => {
                console.log("Advertisement deleted successfully");
                hideDeleteDialog();
                fetchAdvertisements(); // Refresh the list of advertisements
            })
            .catch(error => console.error('Error deleting advertisement:', error));
    }
});

function fetchAllCountries() {
    fetch('https://restcountries.com/v2/all')
        .then(response => response.json())
        .then(countries => {
            globalCountries = countries; // Store the countries globally
        })
        .catch(error => console.error('Error fetching countries:', error));
}

function showUpdateForm(ad) {
    // Populate the form with the advertisement data

    const form = document.getElementById('updateAdForm');
    const article = document.querySelector(`.article[data-ad-id="${currentAd.id}"]`);
    form.jobTitle.value = ad.jobTitle;
    form.internsRequired.value = ad.internsRequired;
    form.jobDuration.value = ad.jobDuration;
    form.description.value = ad.description;

    const jobImageInput = form.querySelector('[name="jobImage"]');
    if (jobImageInput) {
        const file = new File([ad.jobImage], 'jobImage.png', {type: 'image/png'}); // You can adjust the filename and type as needed
        const files = new DataTransfer();
        files.items.add(file);
        jobImageInput.files = files.files;
    }

    document.addEventListener('DOMContentLoaded', function () {
        const descriptionTextarea = document.getElementById('description');
        const maxHeight = 170;

        function resizeTextarea() {
            if (descriptionTextarea.scrollHeight < maxHeight) {
                descriptionTextarea.style.height = 'auto';
                descriptionTextarea.style.height = descriptionTextarea.scrollHeight + 'px';
            } else {
                descriptionTextarea.style.overflowY = 'scroll';
                descriptionTextarea.style.height = maxHeight + 'px';
            }
        }

        descriptionTextarea.addEventListener('input', resizeTextarea, false);
    });

    // Assuming 'FULL_TIME' and 'PART_TIME' are the only two job types
    if (ad.jobType === 'FULL_TIME') {
        document.getElementById('fullTime').checked = true;
    } else if (ad.jobType === 'PART_TIME') {
        document.getElementById('partTime').checked = true;
    }
    form.workMode.value = ad.workMode;

    const adIdInput = document.createElement('input');
    adIdInput.type = 'hidden';
    adIdInput.name = 'id';
    adIdInput.value = article.dataset.adId;
    form.appendChild(adIdInput);

    // Populate country select and then cities
    const countrySelect = document.querySelector('#countrySelect');
    populateCountrySelect(countrySelect, globalCountries, ad.country);

    // Fetch and set cities
    getCountryCodeByName(ad.country).then(code => {
        fetchCities(code, ad.city);
    });

    // Show the form dialog
    const dialog = document.getElementById('formDialog');
    dialog.style.display = 'flex';
    setTimeout(() => {
        dialog.style.opacity = '1'; // Fade in
    }, 10);

    document.getElementById('saveChangesButton').addEventListener('click', function () {
        submitUpdateForm(currentAd);
    });
}

function populateCountrySelect(selectElement, countries, selectedCountryName) {
    selectElement.innerHTML = '';
    countries.forEach(country => {
        const option = document.createElement('option');
        option.value = country.alpha2Code;
        option.textContent = country.name;
        selectElement.appendChild(option);
    });

    const selectedCountryCode = countries.find(country => country.name === selectedCountryName)?.alpha2Code;
    if (selectedCountryCode) {
        selectElement.value = selectedCountryCode;
    }
}

// Fetch countries and set the selected country

let cityCache = {}; // Global cache for cities

async function fetchCities(countryCode, selectedCity) {
    if (cityCache[countryCode]) {
        setCityOptions(cityCache[countryCode], selectedCity);
    } else {
        const url = `http://api.geonames.org/searchJSON?country=${countryCode}&maxRows=12&username=your_username&cities=cities1000`;
        fetch(url)
            .then(response => response.json())
            .then(data => {
                cityCache[countryCode] = data.geonames; // Cache the city data
                setCityOptions(data.geonames, selectedCity);
            })
            .catch(error => console.error('Error fetching cities:', error));
    }
}


function setCityOptions(cities, selectedCity) {
    const citySelect = document.querySelector('#citySelect');
    citySelect.innerHTML = '';
    cities.forEach(city => {
        const option = document.createElement('option');
        option.value = city.name;
        option.textContent = city.name;
        citySelect.appendChild(option);
    });
    citySelect.value = selectedCity;
}


async function getCountryCodeByName(countryName) {
    const response = await fetch('https://restcountries.com/v2/all');
    const countries = await response.json();
    const countryData = countries.find(country => country.name === countryName);
    return countryData ? countryData.alpha2Code : null;
}


function submitUpdateForm(ad) {
    const form = document.getElementById('updateAdForm');
    const formData = new FormData(form);

    // Retrieve the country code from the form
    const countryCode = formData.get('country');
    // Find the country name based on the country code
    const country = globalCountries.find(country => country.alpha2Code === countryCode);
    if (country) {
        // Replace the country code with the country name in the formData
        formData.set('country', country.name);
    }

    document.getElementById('jobTitleError').textContent = '';
    document.getElementById('internsRequiredError').textContent = '';
    document.getElementById('jobDurationError').textContent = '';

    const internsRequired = form.elements['internsRequired'].value;
    const jobDuration = form.elements['jobDuration'].value;

    let isValid = true;

    // Validate Number of Interns
    if (isNaN(internsRequired) || internsRequired === "") {
        document.getElementById('internsRequiredError').textContent = 'Please enter a valid number for interns required.';
        isValid = false;
    }

    // Validate Job Duration
    if (isNaN(jobDuration) || jobDuration === "") {
        document.getElementById('jobDurationError').textContent = 'Please enter a valid number for job duration.';
        isValid = false;
    }

    if (!isValid) {
        return;
    }

    // Include the advertisement ID in the formData
    formData.append('id', ad.id);

    fetch('/advertisement/update', {
        method: 'PUT',
        body: formData
    })
        .then(response => {
            if (response.ok) {
                console.log("Advertisement updated successfully");
                fetchAdvertisements();
                hideUpdateForm();
            } else if (response.status === 400) {
                response.text().then(errorMessage => {
                    const jobTitleError = document.getElementById('jobTitleError');
                    jobTitleError.textContent = errorMessage;
                });
            } else {
                console.error("Failed to update advertisement");
            }
        })
        .catch(error => console.error('Error:', error));
}

