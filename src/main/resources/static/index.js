function setAutoconfigureID() {
    const idEntry = document.getElementById('vehicleID');
    if (idEntry.disabled) {
        idEntry.disabled = false;
        window.canvasHandler.disableAutoConfig();
    } else {
        idEntry.disabled = true;
        window.canvasHandler.enableAutoConfig();
    }
}

async function addVehicle() {
    const vehicleID = window.canvasHandler.acquireVehicleID();
    const error = document.getElementById("error");
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
    console.log(data);
    await window.canvasHandler.addVehicle(vehicleID, start, data);
}

async function makeStep() {
    const resp = await fetch("/api/step");
    const data = await resp.json();
    await window.canvasHandler.clearVehicles();
    await window.canvasHandler.configureVehicles(JSON.parse(data));
}