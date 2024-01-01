document.addEventListener("DOMContentLoaded", function () {
    const imagePreview = document.getElementById('imagePreview');
    const defaultImageSvg = `url("data:image/svg+xml,%3Csvg xmlns='http://www.w3.org/2000/svg' fill='black' height='100%' viewBox='0 0 16 16' width='100%'%3E%3Cpath d='M11 6a3 3 0 1 1-6 0 3 3 0 0 1 6 0'/%3E%3Cpath d='M0 8a8 8 0 1 1 16 0A8 8 0 0 1 0 8m8-7a7 7 0 0 0-5.468 11.37C3.242 11.226 4.805 10 8 10s4.757 1.225 5.468 2.37A7 7 0 0 0 8 1' fill-rule='evenodd'/%3E%3C/svg%3E")`;

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

    function updateProfileImages(imageUrl) {
        const profileImages = document.querySelectorAll('.bi-person-circle');
        if (imageUrl) {
            profileImages.forEach(img => {
                img.querySelectorAll('path').forEach(path => path.style.display = 'none');
                img.style.backgroundImage = `url('${imageUrl}')`;
                img.style.backgroundSize = 'cover';
            });
        } else {
            profileImages.forEach(img => {
                img.style.backgroundImage = defaultImageSvg;
            });
        }
    }

    const hiddenImageUrlInput = document.getElementById('hiddenStudentImageUrl') || document.getElementById('hiddenCompanyImageUrl');
    const imageUrl = hiddenImageUrlInput ? hiddenImageUrlInput.value : null;

    if (imageUrl) {
        loadImageWithFallback(imageUrl, ['jpeg', 'svg+xml', 'png'], (imageUrl) => {
            updateProfileImages(imageUrl);
            imagePreview.style.backgroundImage = `url('${imageUrl}')`;
        });
    } else {
        updateProfileImages(null);
    }
});