const studentSignUpButton = document.getElementById('studentSignUp');
const companySignUpButton = document.getElementById('companySignUp');
const container = document.getElementById('fcontainer');
console.log("dfcvgb" + studentSignUpButton + " " + companySignUpButton);
studentSignUpButton.addEventListener('click', () => {
    container.classList.add("right-panel-active");
});

companySignUpButton.addEventListener('click', () => {
    container.classList.remove("right-panel-active");
});

document.addEventListener("DOMContentLoaded", () => {
    const registrationForms = document.querySelectorAll("form[action='/register']");

    registrationForms.forEach(form => {
        form.addEventListener("submit", function(event) {
            event.preventDefault();

            const formData = new FormData(this);
            const userData = Object.fromEntries(formData);
            const userDataJson = JSON.stringify(userData);

            fetch(this.action, {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                },
                body: userDataJson,
            })
                .then(response => response.json())
                .then(data => {
                    window.location.href = data.redirectUrl;
                })
                .catch((error) => {
                    console.error('Error:', error);
                });
        });
    });
});
