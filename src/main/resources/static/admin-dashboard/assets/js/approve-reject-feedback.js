$(document).ready(function () {
    let clickedElement;

    $(".approve-link, .reject-link").on("click", function (e) {
        e.preventDefault();

        clickedElement = $(this);
        const feedbackId = $(this).data("feedback-id");
        const studentName = $(this).data("student-name");
        const companyName = $(this).data("company-name");
        const comment = $(this).data("comment");
        const rating = $(this).data("rating");
        const actionType = $(this).data("action-type");

        $("#actionType").text(actionType + " feedback?");
        $("#feedbackDetails").html(
            `Student Name: ${studentName}<br>
             Company Name: ${companyName}<br>
             Comment: ${comment}<br>
             Rating: ${rating}`
        );

        const currentStatus = $(this).data("current-status");

        $("#confirmBtn").data("feedback-id", feedbackId);
        $("#confirmBtn").data("action-type", actionType);
        $("#confirmBtn").data("current-status", currentStatus);

        $("#confirmationModal").modal("show");
    });

    $("#confirmBtn").on("click", function () {
        const feedbackId = $(this).data("feedback-id");
        const currentStatus = $(this).data("current-status");
        const actionType = $(this).data("action-type");

        const actionTypeToEnum = {
            'approve': 'APPROVED',
            'reject': 'REJECTED'
        };

        const enumActionType = actionTypeToEnum[actionType];
        console.log(currentStatus);
        console.log(enumActionType);
        if (currentStatus === enumActionType) {
            $("#confirmationModal").modal("hide");
            showWarningAlert("Feedback is already " + enumActionType.toLowerCase());
            return;
        }

        const endpoint = `/admin/update/feedback-status/${feedbackId}`;

        $.ajax({
            url: endpoint,
            type: "PUT",
            headers: {
                "newStatus": enumActionType
            },
            success: function (response) {
                $("#confirmationModal").modal("hide");
                showSuccessAlert(response.message);

                const feedbackStatusBadge = $("#feedback-item-" + feedbackId + " .badge");

                if (feedbackStatusBadge.length) {
                    if (enumActionType === 'APPROVED') {
                        feedbackStatusBadge.removeClass('bg-danger bg-secondary').addClass('bg-success').text('APPROVED');
                    } else if (enumActionType === 'REJECTED') {
                        feedbackStatusBadge.removeClass('bg-success bg-secondary').addClass('bg-danger').text('REJECTED');
                    }
                }

                $(".approve-link[data-feedback-id='" + feedbackId + "'], .reject-link[data-feedback-id='" + feedbackId + "']")
                    .data("current-status", enumActionType);
            },
            error: function (error) {
            }
        });
    });

    function showSuccessAlert(message) {
        // Hide any existing alerts
        $('#successAlert').hide();
        $('#errorAlert').hide();

        const alertBox = document.getElementById('successAlert');
        const messageParagraph = document.getElementById('successMessage');

        messageParagraph.textContent = message;
        alertBox.style.display = 'flex'; // Change display to flex to make it visible
        alertBox.style.opacity = 1;

        // Wait 4 seconds before starting to fade out
        setTimeout(() => {
            let opacity = 1;
            const fadeInterval = setInterval(() => {
                if (opacity <= 0) {
                    clearInterval(fadeInterval);
                    alertBox.style.display = 'none'; // Hide it again after fade out
                } else {
                    opacity -= 0.05; // Decrease the opacity
                    alertBox.style.opacity = opacity;
                }
            }, 50); // Adjust the interval to control the speed of the fade-out
        }, 4000);
    }

    function showWarningAlert(message) {
        // Hide any existing alerts
        $('#successAlert').hide();
        $('#errorAlert').hide();

        const alertBox = document.getElementById('errorAlert');
        const messageParagraph = document.getElementById('errorMessage');

        messageParagraph.textContent = message;
        alertBox.style.display = 'block';
        alertBox.style.opacity = 1; // Set initial opacity to 1

        setTimeout(() => {
            let opacity = 1;
            const fadeInterval = setInterval(() => {
                if (opacity <= 0) {
                    clearInterval(fadeInterval);
                    alertBox.style.display = 'none';
                } else {
                    opacity -= 0.05; // Decrease the opacity
                    alertBox.style.opacity = opacity;
                }
            }, 50);
        }, 4000);
    }
});