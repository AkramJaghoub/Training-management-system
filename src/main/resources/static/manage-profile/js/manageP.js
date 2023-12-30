// function togglePasswordVisibility() {
//     const passwordInput = document.getElementById('input-password');
//     const passwordIcon = document.getElementById('password-icon');
//     if (passwordInput.type === "password") {
//         passwordInput.type = "text";
//         passwordIcon.classList.remove('fas', 'fa-eye');
//         passwordIcon.classList.add('fas', 'fa-eye-slash');
//     } else {
//         passwordInput.type = "password";
//         passwordIcon.classList.remove('fas', 'fa-eye-slash');
//         passwordIcon.classList.add('fas', 'fa-eye');
//     }
// }

document.addEventListener("DOMContentLoaded", function () {
    const form = document.getElementById('profileForm');
    const imagePreview = document.getElementById('imagePreview');
    const defaultImageSvg = `url("data:image/svg+xml,%3Csvg xmlns='http://www.w3.org/2000/svg' fill='black' height='100%' viewBox='0 0 16 16' width='100%'%3E%3Cpath d='M11 6a3 3 0 1 1-6 0 3 3 0 0 1 6 0'/%3E%3Cpath d='M0 8a8 8 0 1 1 16 0A8 8 0 0 1 0 8m8-7a7 7 0 0 0-5.468 11.37C3.242 11.226 4.805 10 8 10s4.757 1.225 5.468 2.37A7 7 0 0 0 8 1' fill-rule='evenodd'/%3E%3C/svg%3E")`;

    let selectedImageData = null; // Global variable to store image data

    function readURL(input) {
        if (input.files && input.files[0]) {
            const reader = new FileReader();
            reader.onload = function (e) {
                imagePreview.style.backgroundImage = 'url(' + e.target.result + ')';
                selectedImageData = e.target.result; // Update the global variable with the selected image data
            };
            reader.readAsDataURL(input.files[0]);
        } else {
            imagePreview.style.backgroundImage = defaultImageSvg;
            selectedImageData = null; // Reset to null if no image is selected
        }
    }

    document.getElementById('companyImage').addEventListener('change', function () {
        readURL(this);
    });

    function updateProfileImages(imageUrl) {
        const profileImages = document.querySelectorAll('.bi-person-circle');
        if (imageUrl) {
            profileImages.forEach(img => {
                img.style.backgroundImage = `url(${imageUrl})`;
                img.style.backgroundSize = 'cover';
                img.querySelectorAll('path').forEach(path => path.style.display = 'none');
            });
        }
    }

    const companyImageUrl = document.getElementById('hiddenCompanyImageUrl').value;

    function loadImageWithFallback(imageData, fallbackTypes, callback) {
        if (fallbackTypes.length === 0) {
            console.error("Failed to load image with provided types.");
            return;
        }

        let imageType = fallbackTypes.shift();
        let img = new Image();
        img.onload = () => callback(imageType);
        img.onerror = () => loadImageWithFallback(imageData, fallbackTypes, callback);
        img.src = `data:image/${imageType};base64,${imageData}`;
    }

    function updatePreviewAndProfileImages(imageType, imageData) {
        let imageUrl = `data:image/${imageType};base64,${imageData}`;
        imagePreview.style.backgroundImage = `url('${imageUrl}')`;
        updateProfileImages(imageUrl);
    }

    if (companyImageUrl) {
        loadImageWithFallback(companyImageUrl, ['jpeg', 'svg+xml', 'png'], (imageType) => {
            updatePreviewAndProfileImages(imageType, companyImageUrl);
        });
    } else {
        imagePreview.style.backgroundImage = defaultImageSvg;
    }


    form.addEventListener('submit', function (e) {
        e.preventDefault();
        const formData = new FormData(this);

        fetch('/company/manage-profile', {
            method: 'PUT',
            body: formData
        })
            .then(response => {
                if (!response.ok) {
                    throw new Error('Network response was not ok');
                }
                return response.text();
            })
            .then(data => {
                if (selectedImageData) {
                    updateProfileImages(selectedImageData);
                }
            })
            .catch(error => {
                console.error('Error:', error);
            });
    });
});


function getCompanyNameFromCookie() {
    const cookies = document.cookie.split('; ');
    const companyNameCookie = cookies.find(row => row.startsWith('companyName='));
    return companyNameCookie ? decodeURIComponent(companyNameCookie.split('=')[1]) : null;
}

document.addEventListener('DOMContentLoaded', function () {
    const companyName = getCompanyNameFromCookie();

    if (companyName) {
        document.getElementById('companyNameDisplay1').textContent = companyName;
        document.getElementById('companyNameDisplay2').textContent = companyName;
    }

});

function handlePhoneInput(input) {
    if (!input.value.startsWith("+962")) {
        input.value = "+962";
    }

    if (input.value.length > 13) {
        input.value = input.value.slice(0, 13);
    }
}
