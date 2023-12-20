document.addEventListener('DOMContentLoaded', function () {
    const studentName = getStudentNameFromCookie();
    console.log(studentName);
    if (studentName) {
        document.getElementById('studentNameDisplay').textContent = studentName;
    }
});

function getStudentNameFromCookie() {
    const cookies = document.cookie.split('; ');
    const studentNameCookie = cookies.find(row => row.startsWith('studentName='));
    return studentNameCookie ? decodeURIComponent(studentNameCookie.split('=')[1]) : null;
}
