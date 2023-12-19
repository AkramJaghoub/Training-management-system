document.addEventListener("DOMContentLoaded", function () {
    // Function to update profile images in  the header
    function updateProfileImages(imageUrl) {
        const profileImages = document.querySelectorAll('.bi-person-circle');
        // Check if an uploaded image exists
        const uploadedImageExists = imageUrl && imageUrl.trim() !== '';
        if (uploadedImageExists) {
            profileImages.forEach(img => {
                img.querySelectorAll('path').forEach(path => path.style.display = 'none');
                img.style.backgroundImage = `url('data:image/jpeg;base64,${imageUrl}')`;
                img.style.backgroundSize = 'cover';
            });
        }
    }

    const companyImageUrl = document.getElementById('hiddenCompanyImageUrl').value;

    updateProfileImages(companyImageUrl);
});