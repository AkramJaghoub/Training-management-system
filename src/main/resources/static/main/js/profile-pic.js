document.addEventListener("DOMContentLoaded", function () {

    const images = document.querySelectorAll('img[data-src]'); // Select images with data-src attribute
    const imageObserver = new IntersectionObserver((entries, observer) => {
        entries.forEach(entry => {
            if (entry.isIntersecting) {
                const image = entry.target;
                image.src = image.getAttribute('data-src');
                observer.unobserve(image); // Stop observing the image after it's loaded
            }
        });
    });

    images.forEach(image => {
        imageObserver.observe(image); // Observe each image
    });
    const defaultImageSvg = `url("data:image/svg+xml,%3Csvg xmlns='http://www.w3.org/2000/svg' fill='black' height='100%' viewBox='0 0 16 16' width='100%'%3E%3Cpath d='M11 6a3 3 0 1 1-6 0 3 3 0 0 1 6 0'/%3E%3Cpath d='M0 8a8 8 0 1 1 16 0A8 8 0 0 1 0 8m8-7a7 7 0 0 0-5.468 11.37C3.242 11.226 4.805 10 8 10s4.757 1.225 5.468 2.37A7 7 0 0 0 8 1' fill-rule='evenodd'/%3E%3C/svg%3E")`;

        images.forEach(image => {
            preloadImage(image.getAttribute('data-src'), () => {
                image.src = image.getAttribute('data-src');
                image.style.display = 'block'; // Show image after it's loaded
            });
        });

        function preloadImage(url, callback) {
            const img = new Image();
            img.onload = () => callback();
            img.src = url;
        }

    function loadImageWithFallback(imageData, fallbackTypes, callback) {
        if (fallbackTypes.length === 0) {
            console.error("Failed to load image with provided types.");
            return;
        }

        let imageType = fallbackTypes.shift();
        let img = new Image();
        img.onload = () => callback(`data:image/${imageType};base64,${imageData}`);
        img.onerror = () => loadImageWithFallback(imageData, fallbackTypes, callback);
        img.src = `data:image/${imageType};base64,${imageData}`;
    }

    // Update profile images
    function updateProfileImages(imageUrl) {
        const biPersonImages = document.querySelectorAll('.bi-person-circle');
        if (imageUrl) {
            biPersonImages.forEach(img => {
                img.querySelectorAll('path').forEach(path => path.style.display = 'none');
                img.style.backgroundImage = `url('${imageUrl}')`;
                img.style.backgroundSize = 'cover';
                img.style.backgroundPosition = 'center';
            });
        } else {
            biPersonImages.forEach(img => {
                img.querySelectorAll('path').forEach(path => path.style.display = 'block');
                img.style.backgroundImage = defaultImageSvg;
            });
        }

        const roundedImages = document.querySelectorAll('.rounded-circle');
        roundedImages.forEach(img => {
            img.src = imageUrl ? imageUrl : "data:image/svg+xml,%3Csvg xmlns='http://www.w3.org/2000/svg' fill='black' height='100%' viewBox='0 0 16 16' width='100%'%3E%3Cpath d='M11 6a3 3 0 1 1-6 0 3 3 0 0 1 6 0'/%3E%3Cpath d='M0 8a8 8 0 1 1 16 0A8 8 0 0 1 0 8m8-7a7 7 0 0 0-5.468 11.37C3.242 11.226 4.805 10 8 10s4.757 1.225 5.468 2.37A7 7 0 0 0 8 1' fill-rule='evenodd'/%3E%3C/svg%3E";
        });
    }

    // Get the image URL from the hidden input
    const hiddenImageUrlInput = document.getElementById('hiddenStudentImageUrl') || document.getElementById('hiddenCompanyImageUrl');
    const imageUrl = hiddenImageUrlInput ? hiddenImageUrlInput.value : null;

    // Preload the image if available
    if (imageUrl) {
        preloadImage(imageUrl);
    }

    // Load the image with fallbacks when DOM content is loaded
    if (imageUrl) {
        loadImageWithFallback(imageUrl, ['jpeg', 'svg+xml', 'png'], (imageUrl) => {
            updateProfileImages(imageUrl);
            const imagePreview = document.getElementById('imagePreview');
            if (imagePreview) {
                imagePreview.style.backgroundImage = `url('${imageUrl}')`;
            }
        });
    } else {
        updateProfileImages(null);
    }
});