document.addEventListener("DOMContentLoaded", function () {
    // Function to update profile images in  the header
    function updateProfileImages(imageUrl) {
        const profileImages = document.querySelectorAll('.bi-person-circle');
        // Check if an uploaded image exists
        const uploadedImageExists = imageUrl && imageUrl.trim() !== '';
        if (uploadedImageExists) {
            profileImages.forEach(img => {
                img.querySelectorAll('path').forEach(path => path.style.display = 'none');
                // Create an img element
                const imgElement = new Image();
                imgElement.src = `data:image/jpeg;base64,${imageUrl}`;
                imgElement.alt = 'Profile Image';
                imgElement.style.width = '10%';
                imgElement.style.height = '100%';

                imgElement.style.borderRadius = '50%';
                // Clear any existing content and append the img element
                img.innerHTML = '';
                img.appendChild(imgElement);
            });
        }
    }



    const companyImageUrl = document.getElementById('hiddenCompanyImageUrl').value;

    updateProfileImages(companyImageUrl);
});