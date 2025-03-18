async function addVehicle(id = null,
                          startRoad = null,
                          endRoad = null) {
    const vehicleID = id !== null ? id : window.canvasHandler.acquireVehicleID();
    const error = document.getElementById("error");
    const start = startRoad !== null ? startRoad : document.getElementById("start-road").value;
    const end = endRoad !== null ? endRoad : document.getElementById("end-road").value;

    if (start === end) {
        error.textContent = "Start road and end road must be different";
        return;
    }
    error.textContent = "";
    const resp = await fetch("/api/vehicles",
        {
            method: "POST",
            headers: {
                "Content-Type": "application/json",
                "Accept": "text/plain",
            },
            body: JSON.stringify({id: vehicleID, startRoad: start, endRoad: end})
        }
    )
    const lane = await resp.text();
    await window.canvasHandler.addVehicle(vehicleID, start, end, lane);

    if (window.canvasHandler.vehicleAmount === 1)
        await requestLights();
}

async function makeStep() {
    const resp = await fetch("/api/step");
    const data = await resp.text();
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
    const data = await resp.text();
    const lights = JSON.parse(data);
    console.log(lights);
    await window.canvasHandler.configureLights(lights["lights"]);
}

async function uploadCommands(file) {
    const form = new FormData();
    document.getElementById("next-command").disabled = false;
    form.append("file", file);
    const resp = await fetch("/api/commands/upload",
        {
            method: "POST",
            body: form
        });
    const data = await resp.text();
    const dropzone = document.getElementById("dropzone");
    dropzone.textContent = resp.status === 200 ? "Commands uploaded successfully!" : data;
    setTimeout(() => {
        dropzone.textContent = "Drop your commands JSON file here";
    }, 5000);
}

async function nextCommand() {
    const resp = await fetch("/api/commands/next");
    const data = await resp.text();
    const dataSplit = data.split(",");
    if (data[data.length - 1] === "FINISHED") {
        document.getElementById("next-command").disabled = true;
    }
    if (dataSplit[0] === "addVehicle")
        await addVehicle(dataSplit[1], dataSplit[2], dataSplit[3]);
    else
        await makeStep();
}

async function download(url) {
    const urlPath = url.split("/");
    const resource = urlPath[urlPath.length - 1];
    const resp = await fetch(url);
    const data = await resp.blob();
    const anchor = document.createElement("a");
    anchor.href = URL.createObjectURL(data);
    anchor.download = resource;
    document.body.appendChild(anchor);
    anchor.click();
    document.body.removeChild(anchor);
}

async function downloadReport() {
    const filename = prompt("File name:", "report");
    if (!filename)
        return;
    await download(`/api/report/download/${filename}.json`);
}

async function recordCommands() {
    const resp = await fetch("/api/recording/state", {method: "PUT"});
    const data = await resp.text();
    if (resp.status === 403) {
        alert(data);
        return;
    }
    const recordingButton = document.getElementById("recording");
    if (data === "true") {
        recordingButton.textContent = "Stop recording";
    } else {
        recordingButton.textContent = "Record commands";
    }
}

async function downloadRecording() {
    const filename = prompt("File name:", "commands");
    if (!filename)
        return
    await download(`/api/recording/download/${filename}.json`);
}