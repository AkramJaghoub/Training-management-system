document.addEventListener("DOMContentLoaded", function () {
    const urlParams = new URLSearchParams(window.location.search);

    function updateTimerDisplay(duration) {
        const timerDisplay = document.getElementById('timer');
        if (timerDisplay) {
            let minutes = Math.floor(duration / 60);
            let seconds = duration % 60;

            minutes = minutes < 10 ? "0" + minutes : minutes.toString();
            seconds = seconds < 10 ? "0" + seconds : seconds.toString();

            timerDisplay.textContent = minutes + ":" + seconds;
        }
    }

    let timerDuration;
    console.log("authFailed in session storage:", sessionStorage.getItem('authFailed'));
    if (sessionStorage.getItem('authFailed') === 'true') {
        timerDuration = 180; // Resetting timer to 3 minutes
        sessionStorage.removeItem('authFailed'); // Clear the flag
    } else {
        timerDuration = parseInt(localStorage.getItem('timerDuration'), 10) || 180;
    }

    updateTimerDisplay(timerDuration);

    const countdown = setInterval(function () {
        if (--timerDuration <= 0) {
            clearInterval(countdown);
            localStorage.removeItem('timerDuration');
            sessionStorage.removeItem('fromConfirmEmail');
            window.location.href = "/auth/2fa/confirm-email?tokenExpired=true";
            return;
        }
        updateTimerDisplay(timerDuration);
        localStorage.setItem('timerDuration', timerDuration.toString());
    }, 1000);

    // Form submission handling
    const form = document.querySelector('form');
    if (form) {
        form.addEventListener('submit', function (event) {
            event.preventDefault();

            const userEnteredCode = document.getElementById('verificationCode').value;
            const expectedToken = document.getElementById('expectedToken').value;

            if (userEnteredCode !== expectedToken) {
                document.getElementById('verificationCodeError').textContent = 'Invalid authentication code';
            } else {
                form.submit();
            }
        });
    }
});