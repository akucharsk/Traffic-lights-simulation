
async function addVehicle() {
    const vehicleID = document.getElementById('vehicleID').value;
    const error = document.getElementById("error");
    if (vehicleID === undefined) {
        error.textContent = "Vehicle ID is missing";
        return;
    }
    const start = document.getElementById("start-road").value;
    const end = document.getElementById("end-road").value;

    if (start == end) {
        error.textContent = "Start road and end road must be different";
        return;
    }
    error.textContent = "";
    const resp = await fetch("/api/vehicles",
        {
            method: "POST",
            "Content-Type": "application/json",
            body: JSON.stringify({vehicleID: vehicleID, startRoad: start, endRoad: end})
        }
    )
    const data = await resp.json();
    await window.canvasHandler.configureVehicles(JSON.parse(data));
}