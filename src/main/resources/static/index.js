async function addVehicle() {
    const vehicleID = window.canvasHandler.acquireVehicleID();
    const error = document.getElementById("error");
    const start = document.getElementById("start-road").value;
    const end = document.getElementById("end-road").value;

    if (start === end) {
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
    const lane = await resp.json();
    await window.canvasHandler.addVehicle(vehicleID, start, end, lane);

    if (window.canvasHandler.vehicleAmount === 1)
        await requestLights();
}

async function makeStep() {
    const resp = await fetch("/api/step");
    const data = await resp.json();
    const jsonData = JSON.parse(data);
    console.log(jsonData);
    const lightConfig = jsonData["lights"];
    const departedVehicles = jsonData["departedVehicles"];
    console.log(departedVehicles);
    await window.canvasHandler.moveVehicles(departedVehicles);
    await window.canvasHandler.configureLights(lightConfig);
}

async function requestLights() {
    const resp = await fetch("/api/lights");
    const data = await resp.json();
    const lights = JSON.parse(data);
    console.log(lights);
    await window.canvasHandler.configureLights(lights["lights"]);
}