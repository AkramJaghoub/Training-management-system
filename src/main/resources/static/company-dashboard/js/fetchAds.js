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

    advertisements.forEach(ad => {
        const article = document.createElement('article');
        // Create and append figure with image


        // Create and append article body content
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
            showDescription(ad);
        };

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

document.addEventListener('DOMContentLoaded', function () {
    const companyName = getCompanyNameFromCookie();
    if (companyName) {
        document.getElementById('companyNameDisplay').textContent = companyName;
    }
});