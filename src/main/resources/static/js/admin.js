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