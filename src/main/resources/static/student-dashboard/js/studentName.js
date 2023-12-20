document.addEventListener('DOMContentLoaded', function () {
    const studentName = getStudentNameFromCookie();
    if (studentName) {
        document.getElementById('studentNameDisplay').textContent = decodeStudentName(studentName);
    }
});

function getStudentNameFromCookie() {
    const cookies = document.cookie.split('; ');
    const studentNameCookie = cookies.find(row => row.startsWith('studentName='));
    return studentNameCookie ? studentNameCookie.split('=')[1] : null;
}

function decodeStudentName(encodedName) {
    // Replace '+' with space before decoding
    return decodeURIComponent(encodedName.replace(/\+/g, ' '));
}