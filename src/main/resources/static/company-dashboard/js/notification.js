document.addEventListener('DOMContentLoaded', () => {
    document.querySelectorAll('.dropdown-item').forEach(item => {
        item.addEventListener('click', function(event) {
            console.log('Notification clicked, id:', this.dataset.notificationId);
            event.preventDefault();
            const notificationId = Number(this.dataset.notificationId); // Convert to number
            const confirmReadButton = document.getElementById('confirmRead');
            confirmReadButton.dataset.notificationId = notificationId;
            const myModal = new bootstrap.Modal(document.getElementById('markAsReadModal'), {
                keyboard: false
            });
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
                const notificationCount = parseInt(notificationCountElement.textContent, 10);
                notificationCountElement.textContent = (notificationCount - 1).toString();
            })
            .catch(error => {
                console.error('Error marking notification as read:', error);
            });
    });
});