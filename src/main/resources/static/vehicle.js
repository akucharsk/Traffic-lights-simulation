export default class Vehicle {
    static vehicleWidth;
    static vehicleLength;
    static stepSize;
    
    constructor(id, startRoad, startLane, color) {
        this.ctx = window.canvasHandler.ctx;
        this.startRoad = startRoad;
        this.startLane = startLane;
        this.id = id;
        if (color === null) {
            let rgbLimit = 640;
            const red = Math.round(Math.random() * 256);
            rgbLimit -= red;
            const green = Math.round(Math.random() * 256);
            rgbLimit -= green;
            const blue = Math.min(rgbLimit, Math.round(Math.random() * 256));
            this.color = `rgb(${red}, ${green}, ${blue})`;
        } else {
            this.color = color;
        }

        this.intersections = window.canvasHandler.junctionIntersections;

        const vehicleAmount = window.canvasHandler.vehicleLocations[startRoad][startLane].length;
        const intersection = this.intersections[startRoad][startLane];

        switch (startRoad) {
            case "north":
                this.x = intersection.x - Vehicle.vehicleWidth / 2;
                this.y = intersection.y - (vehicleAmount + 2) * Vehicle.stepSize;
                this.w = Vehicle.vehicleWidth;
                this.h = Vehicle.vehicleLength;
                break;
            case "south":
                this.x = intersection.x - Vehicle.vehicleWidth / 2;
                this.y = intersection.y + (vehicleAmount + 1) * Vehicle.stepSize;
                this.w = Vehicle.vehicleWidth;
                this.h = Vehicle.vehicleLength;
                break;
            case "west":
                this.x = intersection.x - (vehicleAmount + 2) * Vehicle.stepSize;
                this.y = intersection.y - Vehicle.vehicleWidth / 2;
                this.w = Vehicle.vehicleLength;
                this.h = Vehicle.vehicleWidth;
                break;
            case "east":
                this.x = intersection.x + (vehicleAmount + 1) * Vehicle.stepSize;
                this.y = intersection.y - Vehicle.vehicleWidth / 2;
                this.w = Vehicle.vehicleLength;
                this.h = Vehicle.vehicleWidth;
                break;
        }
    }

    draw() {
        this.ctx.fillStyle = this.color;
        this.ctx.fillRect(this.x, this.y, this.w, this.h);
    }

    erase() {
        this.ctx.clearRect(this.x - 2, this.y - 2, this.w + 4, this.h + 4);
    }

    moveForward() {
        this.erase();
        switch (this.startRoad) {
            case "north":
                this.y += Vehicle.stepSize;
                break;
            case "south":
                this.y -= Vehicle.stepSize;
                break;
            case "east":
                this.x -= Vehicle.stepSize;
                break;
            case "west":
                this.x += Vehicle.stepSize;
                break;
        }
        this.draw();
    }
}