document.addEventListener('DOMContentLoaded', function () {
    const form = document.getElementById('profileForm');
    const imagePreviewDiv = document.getElementById('imagePreview');
    const defaultImageSvg = `
        <svg fill='black' height='100%' viewBox='0 0 16 16' width='100%' xmlns='http://www.w3.org/2000/svg'>
            <path d='M11 6a3 3 0 1 1-6 0 3 3 0 0 1 6 0'/>
            <path d='M0 8a8 8 0 1 1 16 0A8 8 0 0 1 0 8m8-7a7 7 0 0 0-5.468 11.37C3.242 11.226 4.805 10 8 10s4.757 1.225 5.468 2.37A7 7 0 0 0 8 1' fill-rule='evenodd'/>
        </svg>`;

    let selectedImageURL = null;

    document.getElementById('studentImage').addEventListener('change', function () {
        if (this.files && this.files[0]) {
            if (selectedImageURL) {
                URL.revokeObjectURL(selectedImageURL);
            }

            const file = this.files[0];
            selectedImageURL = URL.createObjectURL(file);
            imagePreviewDiv.innerHTML = `<img src="${selectedImageURL}" alt="Student Image" class="avatar-image" style="display: block;">`;
        } else {
            if (selectedImageURL) {
                URL.revokeObjectURL(selectedImageURL);
            }
            imagePreviewDiv.innerHTML = defaultImageSvg;
        }
    });

    function updateProfileImages(newImageUrl) {
        const profileIconContainers = document.querySelectorAll('.circle-icon');

        profileIconContainers.forEach(container => {
            while (container.firstChild) {
                container.removeChild(container.firstChild);
            }

            if (newImageUrl) {
                const img = document.createElement('img');
                img.src = newImageUrl;
                img.alt = 'Profile Image';
                img.classList.add('profile-image');
                container.appendChild(img);
            } else {
                container.innerHTML = defaultImageSvg;
            }
        });
    }

    form.addEventListener('submit', function (e) {
        e.preventDefault();

        const formData = new FormData();
        if (selectedImageURL) {
            formData.append('studentImage', document.getElementById('studentImage').files[0]);
        }

        fetch('/student/manage-profile', {
            method: 'PUT',
            body: formData
        })
            .then(response => {
                if (!response.ok) {
                    return response.json().then(data => {
                        throw new Error(data.message || 'Network response was not ok');
                    });
                }
                return response.json();
            })
            .then(data => {
                if (selectedImageURL) {
                    updateProfileImages(selectedImageURL);
                }
                displaySuccessMessage(data.message);
            })
            .catch(error => {
                console.error('Error updating profile:', error);
            });
    });

    function displaySuccessMessage(message) {
        const alertBox = document.getElementById('successAlert');
        const messageParagraph = document.getElementById('successMessage');

        messageParagraph.textContent = message;

        alertBox.style.display = 'flex';
        alertBox.style.opacity = 1;

        setTimeout(() => {
            let opacity = 1;
            const fadeInterval = setInterval(() => {
                if (opacity <= 0) {
                    clearInterval(fadeInterval);
                    alertBox.style.display = 'none';
                } else {
                    opacity -= 0.05;
                    alertBox.style.opacity = opacity;
                }
            }, 50);
        }, 4000);
    }
});