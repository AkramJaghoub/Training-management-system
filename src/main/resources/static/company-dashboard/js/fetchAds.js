document.addEventListener('DOMContentLoaded', function () {
    fetchAdvertisements();
});

function fetchAdvertisements() {
    fetch('/company/get/ads')
        .then(response => response.json())
        .then(data => {
            updateAdvertisements(data);
        })
        .catch(error => console.error('Error fetching advertisements:', error));
}

function updateAdvertisements(advertisements) {
    const articlesContainer = document.querySelector('.articles');
    articlesContainer.innerHTML = '';

    advertisements.forEach((ad, index) => {
        const article = document.createElement('article'); // Define 'article' here
        article.classList.add('article'); // Add this line if you have CSS styles for 'article
        const articleBody = document.createElement('div');
        articleBody.classList.add('article-body');
        const title = document.createElement('h2');
        title.textContent = ad.jobTitle;

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
        readMoreLink.onclick = function(event) {
            event.preventDefault();
            event.stopPropagation(); // Prevents the event from bubbling up to parent elements
            showDescription(ad);
        };

        const kebabMenuId = 'kebabMenu' + index;
        const menuContainerId = 'menuContainer' + index;

        const kebabMenu = document.createElement('div');
        kebabMenu.innerHTML = `<svg xmlns="http://www.w3.org/2000/svg" width="20" height="20" fill="currentColor" class="bi bi-three-dots-vertical" viewBox="0 0 16 16"><path d="M9.5 13a1.5 1.5 0 1 1-3 0 1.5 1.5 0 0 1 3 0m0-5a1.5 1.5 0 1 1-3 0 1.5 1.5 0 0 1 3 0m0-5a1.5 1.5 0 1 1-3 0 1.5 1.5 0 0 1 3 0"/></svg>`;
        kebabMenu.id = kebabMenuId;
        kebabMenu.style.cursor = 'pointer'; // Ensure it looks clickable
        kebabMenu.classList.add('menu-icon');

        const menuContainer = document.createElement('div');
        menuContainer.classList.add('menu-container');
        menuContainer.id = menuContainerId;
        menuContainer.style.display = 'none';

        kebabMenu.addEventListener('click', function() {
            const currentMenu = document.getElementById(menuContainerId);
            currentMenu.style.display = currentMenu.style.display === 'none' ? 'block' : 'none';
        });

        const updateLink = document.createElement('a');
        updateLink.href = '#';
        updateLink.classList.add('update-link');
        updateLink.onclick = function(event) {
            event.preventDefault();
        };

        const updateIcon = document.createElement('iconify-icon');
        updateIcon.setAttribute('icon', 'bxs:edit');
        updateLink.appendChild(updateIcon);

        const updateText = document.createElement('span');
        updateText.textContent = 'Update';
        updateLink.appendChild(updateText);

        const deleteLink = document.createElement('a');
        deleteLink.textContent = 'Delete';
        deleteLink.href = '#'; // Update with correct link or JavaScript function

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
    closeSpan.onclick = function() {
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

window.onclick = function(event) {
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

window.onclick = function(event) {
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

document.addEventListener('DOMContentLoaded', function () {
    const companyName = getCompanyNameFromCookie();
    if (companyName) {
        document.getElementById('companyNameDisplay').textContent = companyName;
    }
});



document.addEventListener('DOMContentLoaded', function() {
    const deleteLinks = document.querySelectorAll('.delete-link');
    deleteLinks.forEach(function(link) {
        link.addEventListener('click', function(event) {
            event.preventDefault();
            showDeleteDialog();
        });
    });

    document.getElementById('confirmDelete').addEventListener('click', function() {
        // Call your delete function here
        hideDeleteDialog();
    });

    document.getElementById('cancelDelete').addEventListener('click', function() {
        hideDeleteDialog();
    });
});

function showDeleteDialog() {
    const dialog = document.getElementById('deleteDialog');
    dialog.style.display = 'flex';
    setTimeout(() => {
        dialog.style.top = '0'; // Slide down
    }, 10);
}

function hideDeleteDialog() {
    const dialog = document.getElementById('deleteDialog');
    dialog.style.top = '-100%'; // Slide up
    setTimeout(() => {
        dialog.style.display = 'none';
    }, 500); // Match the duration of the slide-up transition
}



