function updateClock() {
    const timeElement = document.getElementById("liveTime");
    const dateElement = document.getElementById("liveDate");

    if (!timeElement || !dateElement) {
        return;
    }

    const now = new Date();

    timeElement.textContent = now.toLocaleTimeString("en-US", {
        hour: "2-digit",
        minute: "2-digit",
        second: "2-digit"
    });

    dateElement.textContent = now.toLocaleDateString("en-US", {
        weekday: "long",
        year: "numeric",
        month: "long",
        day: "numeric"
    });
}

updateClock();
setInterval(updateClock, 1000);


// SweetAlert success and error messages
document.addEventListener("DOMContentLoaded", function () {
    const successElement = document.getElementById("sweetSuccessMessage");
    const errorElement = document.getElementById("sweetErrorMessage");

    if (successElement) {
        Swal.fire({
            icon: "success",
            title: "Success",
            text: successElement.dataset.message,
            confirmButtonText: "OK"
        });
    }

    if (errorElement) {
        Swal.fire({
            icon: "error",
            title: "Error",
            text: errorElement.dataset.message,
            confirmButtonText: "OK"
        });
    }
});


// SweetAlert confirmation for links
document.addEventListener("click", function (event) {
    const confirmLink = event.target.closest("[data-swal-confirm='true']");

    if (!confirmLink) {
        return;
    }

    event.preventDefault();

    const title = confirmLink.dataset.swalTitle || "Are you sure?";
    const text = confirmLink.dataset.swalText || "This action cannot be undone.";
    const icon = confirmLink.dataset.swalIcon || "warning";
    const confirmButtonText = confirmLink.dataset.swalConfirmButton || "Yes, continue";
    const cancelButtonText = confirmLink.dataset.swalCancelButton || "Cancel";

    Swal.fire({
        title: title,
        text: text,
        icon: icon,
        showCancelButton: true,
        confirmButtonText: confirmButtonText,
        cancelButtonText: cancelButtonText,
        confirmButtonColor: "#dc3545",
        cancelButtonColor: "#6c757d"
    }).then((result) => {
        if (result.isConfirmed) {
            window.location.href = confirmLink.href;
        }
    });
});