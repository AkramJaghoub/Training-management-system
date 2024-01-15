document.addEventListener("DOMContentLoaded", function () {
    clearAddFeedbackForm();

    document.getElementById('addStudentId').value = document.getElementById('hiddenStudentId').value;

    const addComment = document.getElementById('addComment');
    const addCommentCounter = document.getElementById('addCommentCounter');

    if (addComment) {
        addComment.addEventListener('input', function () {
            updateCharacterCounter(addComment, addCommentCounter, 250);
        });
    }

    // Submit New Feedback
    const addFeedbackForm = document.getElementById('addFeedbackForm');
    if (addFeedbackForm) {
        addFeedbackForm.addEventListener('submit', function (event) {
            event.preventDefault();

            if (addComment.value.length > 250) {
                addComment.classList.add('is-invalid');
                return;
            }

            submitFeedbackForm('add');
        });
    }
});

function updateCharacterCounter(commentElement, counterElement, maxChars) {
    const currentLength = commentElement.value.length;
    counterElement.textContent = `${currentLength}/${maxChars}`;

    if (currentLength > maxChars) {
        counterElement.classList.add('text-danger');
        counterElement.classList.remove('text-muted');
    } else {
        counterElement.classList.remove('text-danger');
        counterElement.classList.add('text-muted');
    }
}

function clearAddFeedbackForm() {
    document.getElementById('addCompanyName').value = '';
    document.getElementById('addRating').value = '';
    document.getElementById('addComment').value = '';
    document.getElementById('addCommentCounter').textContent = '0/250';
    document.getElementById('addStudentId').value = ''; // Set this based on your application's context

    // Display SVG icon if studentImage is not present
    const studentImage = document.getElementById('studentImage');
    const studentImageContainer = document.getElementById('studentImageContainer');

    if (!studentImage) {
        const svgIcon = `
            <svg class="bi bi-person-circle" fill="black" height="60" viewBox="0 0 16 16" width="60" xmlns="http://www.w3.org/2000/svg">
                <path d="M11 6a3 3 0 1 1-6 0 3 3 0 0 1 6 0"/>
                <path d="M0 8a8 8 0 1 1 16 0A8 8 0 0 1 0 8m8-7a7 7 0 0 0-5.468 11.37C3.242 11.226 4.805 10 8 10s4.757 1.225 5.468 2.37A7 7 0 0 0 8 1"
                      fill-rule="evenodd"/>
            </svg>
        `;
        if (studentImageContainer) {
            studentImageContainer.innerHTML = svgIcon;
        }
    }
}

function submitFeedbackForm(formType) {
    let feedbackDto;
    let url;

    if (formType === 'add') {
        feedbackDto = {
            studentId: document.getElementById('addStudentId').value,
            companyName: document.getElementById('addCompanyName').value,
            comment: document.getElementById('addComment').value,
            rating: document.getElementById('addRating').value
        };
        url = '/feedback/provide';
    }

    // AJAX request to server
    $.ajax({
        url: url,
        type: 'POST',
        contentType: 'application/json',
        data: JSON.stringify(feedbackDto),
        success: function (response) {
            showSuccessAlert(response.message);
            if (formType === 'add') {
                clearAddFeedbackForm();
                updateMyPostsTab();
            }
        },
        error: function (error) {
            // Handle error
        }
    });
}

function updateMyPostsTab() {
    fetch('/feedback/latest')
        .then(response => response.json())
        .then(latestFeedback => {
            const myPostsContainer = document.getElementById('yourPosts');
            const feedbackHtml = formatFeedbackHtml(latestFeedback);

            let feedbackListContainer = myPostsContainer.querySelector('.feedback-list-container');
            if (!feedbackListContainer) {
                feedbackListContainer = document.createElement('div');
                feedbackListContainer.className = 'feedback-list-container';
                myPostsContainer.appendChild(feedbackListContainer);
                // Hide 'No feedback' message if present
                const noFeedbackMessage = myPostsContainer.querySelector('.no-feedback-message');
                if (noFeedbackMessage) {
                    noFeedbackMessage.style.display = 'none';
                }
            }

            feedbackListContainer.insertAdjacentHTML('afterbegin', feedbackHtml);
        })
        .catch(error => console.error('Error fetching latest feedback:', error));
}

function formatFeedbackHtml(feedback) {
    let formattedDate = new Date(feedback.postDate).toLocaleDateString('en-US', { year: 'numeric', month: 'long', day: 'numeric' });
    console.log("Feedback status:", feedback.status);
    let statusBadgeHtml = getStatusBadgeHtml(feedback.status);

    let imageHtml = feedback.studentImage ?
        `<img src="${feedback.studentImage}" alt="avatar" class="rounded-circle shadow-1-strong me-3" width="60" height="60"/>` :
        `
<svg class="bi bi-person-circle" fill="black" height="60" viewBox="0 0 16 16" width="60" xmlns="http://www.w3.org/2000/svg">
   <path d="M11 6a3 3 0 1 1-6 0 3 3 0 0 1 6 0"/>
   <path d="M0 8a8 8 0 1 1 16 0A8 8 0 0 1 0 8m8-7a7 7 0 0 0-5.468 11.37C3.242 11.226 4.805 10 8 10s4.757 1.225 5.468 2.37A7 7 0 0 0 8 1" fill-rule="evenodd"/>
</svg>
`;
    return `
<div class="d-flex flex-start" id="feedback-item-${feedback.id}">
   <div>${imageHtml}</div>
   <div>
      <div class="name-date d-flex align-items-center mb-3">
         <h6 class="fw-bold mb-0 me-2">${feedback.student.firstName} ${feedback.student.lastName}</h6>
         <span class="boxs">${formattedDate}</span>
        <a href="#edit" class="link-muted edit-icon" style="color: green; margin-left: 28rem"
            data-feedback-id="${feedback.id}" data-student-id="${feedback.student.id}"
            data-student-name="${feedback.student.firstName} ${feedback.student.lastName}"
            data-company-name="${feedback.companyName}" data-comment="${feedback.comment}"
            data-rating="${feedback.rating}">
         <i class="fas fa-pencil-alt ms-2"></i>
         </a>
         <a href="#" class="link-muted delete-icon" style="color: red; margin-right: 1rem;"
            data-feedback-id="${feedback.id}" data-student-id="${feedback.student.id}">
            <svg class="icon-20 delete-icon ms-2" width="20" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg" stroke="currentColor">
            <path d="M19.3248 9.46826C19.3248 9.46826 18.7818 16.2033 18.4668 19.0403C18.3168 20.3953 17.4798 21.1893 16.1088 21.2143C13.4998 21.2613 10.8878 21.2643 8.27979 21.2093C6.96079 21.1823 6.13779 20.3783 5.99079 19.0473C5.67379 16.1853 5.13379 9.46826 5.13379 9.46826" stroke="currentColor" stroke-width="1.5" stroke-linecap="round" stroke-linejoin="round"></path>
            <path d="M20.708 6.23975H3.75" stroke="currentColor" stroke-width="1.5" stroke-linecap="round" stroke-linejoin="round"></path>
            <path d="M17.4406 6.23973C16.6556 6.23973 15.9796 5.68473 15.8256 4.91573L15.5826 3.69973C15.4326 3.13873 14.9246 2.75073 14.3456 2.75073H10.1126C9.53358 2.75073 9.02558 3.13873 8.87558 3.69973L8.63258 4.91573C8.47858 5.68473 7.80258 6.23973 7.01758 6.23973" stroke="currentColor" stroke-width="1.5" stroke-linecap="round" stroke-linejoin="round"></path>
         </svg>
         </a>
               <p class="mb-0">        
                    ${statusBadgeHtml}
               </p>

      </div>
      <div class="d-flex align-items-center mb-1">
         <div style="margin-left: 36rem">
            <span class="company-name me-2" style="font-weight: bold; margin-left: 9rem">${feedback.company.companyName}</span>
            <span>Rating: ${feedback.rating}</span>
            <svg xmlns="http://www.w3.org/2000/svg" width="24" height="24" viewBox="0 0 24 24" style="margin-bottom: .5rem">
               <path fill="#ffd43b" d="M9.153 5.408C10.42 3.136 11.053 2 12 2c.947 0 1.58 1.136 2.847 3.408l.328.588c.36.646.54.969.82 1.182c.28.213.63.292 1.33.45l.636.144c2.46.557 3.689.835 3.982 1.776c.292.94-.546 1.921-2.223 3.882l-.434.507c-.476.557-.715.836-.822 1.18c-.107.345-.071.717.001 1.46l.066.677c.253 2.617.38 3.925-.386 4.506c-.766.582-1.918.051-4.22-1.009l-.597-.274c-.654-.302-.981-.452-1.328-.452c-.347 0-.674.15-1.328.452l-.596.274c-2.303
                  1.06-3.455 1.59-4.22 1.01c-.767-.582-.64-1.89-.387-4.507l.066-.676c.072-.744.108-1.116 0-1.46c-.106-.345-.345-.624-.821-1.18l-.434-.508c-1.677-1.96-2.515-2.941-2.223-3.882c.293-.941 1.523-1.22 3.983-1.776l.636-.144c.699-.158 1.048-.237 1.329-.45c.28-.213.46-.536.82-1.182z"/>
            </svg>
         </div>
      </div>
      <p class="mb-0 feedback-comment">${feedback.comment}</p>
   </div>
</div>
<hr class="my-0"/>
`;
}

function getStatusBadgeHtml(status) {
    switch(status) {
        case 'APPROVED':
            return '<span class="badge bg-success">APPROVED</span>';
        case 'REJECTED':
            return '<span class="badge bg-danger">REJECTED</span>';
        case 'PENDING':
            return '<span class="badge bg-secondary">PENDING</span>';
        default:
            return '<span class="badge bg-secondary">UNKNOWN</span>';
    }
}

function showSuccessAlert(message) {
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
