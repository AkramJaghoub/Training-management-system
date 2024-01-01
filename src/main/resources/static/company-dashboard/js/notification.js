document.addEventListener('DOMContentLoaded', () => {
    document.querySelectorAll('.dropdown-item.notification-drop').forEach(item => {
        item.addEventListener('click', function(event) {
            event.preventDefault();
            event.stopPropagation();
            console.log('Notification clicked, id:', this.dataset.notificationId);
            const notificationId = Number(this.dataset.notificationId);
            const confirmReadButton = document.getElementById('confirmRead');
            confirmReadButton.dataset.notificationId = notificationId;
            const myModal = new bootstrap.Modal(document.getElementById('markAsReadModal'));
            myModal.show();
        });
    });

    document.getElementById('confirmRead').addEventListener('click', function() {
        const notificationId = Number(this.dataset.notificationId); // Convert to number
        fetch(`/notification/mark-as-read/${notificationId}`, {
            method: 'DELETE'
        })
            .then(response => {
                if (!response.ok) {
                    throw new Error('Network response was not ok');
                }
                return response.json(); // Adjust this according to your server's response
            })
            .then(result => {
                const myModalEl = document.getElementById('markAsReadModal');
                const myModal = bootstrap.Modal.getInstance(myModalEl);
                myModal.hide();
                document.querySelector(`a[data-notification-id="${notificationId}"]`).closest('li').remove();
                const notificationCountElement = document.getElementById('notificationCount');
                const notificationCount = parseInt(notificationCountElement.textContent, 10) - 1;
                notificationCountElement.textContent = notificationCount.toString();

                // Disable dropdown if no notifications are left
                if (notificationCount === 0) {
                    disableNotificationDropdown();
                }
            })
            .catch(error => {
                console.error('Error marking notification as read:', error);
            });
    });
});

function disableNotificationDropdown() {
    const dropdownToggle = document.getElementById('navbarDropdown');
    dropdownToggle.classList.add('disabled'); // Add the disabled class
    dropdownToggle.removeAttribute('data-bs-toggle'); // Remove dropdown toggle functionality
}