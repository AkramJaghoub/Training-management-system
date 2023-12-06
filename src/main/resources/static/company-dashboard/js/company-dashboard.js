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
        const articleWrapper = document.createElement('div');
        articleWrapper.classList.add('article-wrapper');

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
            showDescription(ad.description, ad);
        };

        articleBody.appendChild(title);
        articleBody.appendChild(internsRequired);
        articleBody.appendChild(duration);
        articleBody.appendChild(jobType);
        articleBody.appendChild(workMode);
        articleBody.appendChild(readMoreLink);

        articleWrapper.appendChild(articleBody);
        article.appendChild(articleWrapper);
        articlesContainer.appendChild(article);
    });
}

function showDescription(description, ad) {
    const location = document.createElement('div');
    location.innerHTML = '<strong>' + ad.city + ', ' + ad.country + '</strong>';
    location.classList.add('modal-location');

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
    descriptionParagraph.textContent = description;

    modalContent.appendChild(closeSpan);
    modalContent.appendChild(location); // Add the location div here
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