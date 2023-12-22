document.addEventListener('DOMContentLoaded', function () {
    const studentName = getStudentNameFromCookie();
    if (studentName) {
        document.getElementById('studentNameDisplay').textContent = decodeStudentName(studentName);
    }
});


function readURL(input) {
    const imagePreview = document.getElementById('imagePreview');
    if (input.files && input.files[0]) {
        const reader = new FileReader();
        reader.onload = function (e) {
            imagePreview.style.backgroundImage = 'url(' + e.target.result + ')';
        };
        reader.readAsDataURL(input.files[0]);
    } else {
        imagePreview.style.backgroundImage = defaultImageSvg;
    }
}

document.getElementById('studentImage').addEventListener('change', function () {
    readURL(this);
});

document.addEventListener("DOMContentLoaded", function () {
    const form = document.getElementById('profileForm');
    const imagePreview = document.getElementById('imagePreview');
    const defaultImageSvg = `url("data:image/svg+xml,%3Csvg xmlns='http://www.w3.org/2000/svg' fill='black' height='100%' viewBox='0 0 16 16' width='100%'%3E%3Cpath d='M11 6a3 3 0 1 1-6 0 3 3 0 0 1 6 0'/%3E%3Cpath d='M0 8a8 8 0 1 1 16 0A8 8 0 0 1 0 8m8-7a7 7 0 0 0-5.468 11.37C3.242 11.226 4.805 10 8 10s4.757 1.225 5.468 2.37A7 7 0 0 0 8 1' fill-rule='evenodd'/%3E%3C/svg%3E")`;

    // Function to update profile images in the header
    function updateProfileImages(imageUrl) {
        const profileImages = document.querySelectorAll('.bi-person-circle');
        if (imageUrl) {
            profileImages.forEach(img => {
                img.style.backgroundImage = `url('data:image/jpeg;base64,${imageUrl}')`;
                img.style.backgroundSize = 'cover';
                img.querySelectorAll('path').forEach(path => path.style.display = 'none');
            });
        }
    }

    const studentImageUrl = document.getElementById('hiddenStudentImageUrl').value;
    if (studentImageUrl) {
        imagePreview.style.backgroundImage = `url('data:image/jpeg;base64,${studentImageUrl}')`;
        updateProfileImages(studentImageUrl);
    } else {
        imagePreview.style.backgroundImage = defaultImageSvg;
    }

    form.addEventListener('submit', function (e) {
        e.preventDefault();
        const formData = new FormData(this);

        fetch('/student/manage-profile', {
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
                console.log(data);
                // Handle the response data
            })
            .catch(error => {
                console.error('Error:', error);
            });
    });
});

function getStudentNameFromCookie() {
    const cookies = document.cookie.split('; ');
    const studentNameCookie = cookies.find(row => row.startsWith('studentName='));
    if (studentNameCookie) {
        const fullName = studentNameCookie.split('=')[1];
        return fullName.replace('+', ' ');
    } else {
        return null;
    }
}

document.addEventListener('DOMContentLoaded', function () {
    const studentName = getStudentNameFromCookie();

    if (studentName) {
        document.getElementById('studentNameDisplay1').textContent = studentName;
        document.getElementById('studentNameDisplay2').textContent = studentName;
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

function decodeStudentName(encodedName) {
    // Replace '+' with space before decoding
    return decodeURIComponent(encodedName.replace(/\+/g, ' '));
}