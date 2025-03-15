import Vehicle from "./vehicle.js";
import TrafficLight from "./traffic_light.js";

export default class CanvasHandler {
    constructor(canvas) {
        const opts = document.getElementById("opts");
        canvas.width = (window.innerWidth - opts.clientWidth) * 0.8;
        canvas.height = window.innerHeight * 0.9;

        this.lowestVehicleID = 0;
        this.vehicleAmount = 0;

        this.ctx = canvas.getContext('2d');
        this.width = canvas.width;
        this.height = canvas.height;

        this.roadWidth = Math.min(this.width, this.height) / 2.5;

        const cornerWidth = (this.width - this.roadWidth) / 2;
        const cornerHeight = (this.height - this.roadWidth) / 2;
        this.cornerWidth = cornerWidth;
        this.cornerHeight = cornerHeight;

        this.ctx.fillStyle = "blue";
        this.ctx.fillRect(0, 0, cornerWidth, cornerHeight);

        this.ctx.fillStyle = "lightgreen";
        this.ctx.fillRect(0, this.roadWidth + cornerHeight, cornerWidth, cornerHeight);

        this.ctx.fillStyle = "yellow";
        this.ctx.fillRect(this.roadWidth + cornerWidth, this.roadWidth + cornerHeight, cornerWidth, cornerHeight);

        this.ctx.fillStyle = "cyan";
        this.ctx.fillRect(this.roadWidth + cornerWidth, 0, cornerWidth, cornerHeight);

        // double lines indicating opposite lanes
        this.ctx.strokeStyle = "black";
        const sepLineWidth = this.roadWidth / 100;
        const laneWidth = this.roadWidth / 4;

        this.laneWidth = laneWidth;
        this.sepLineWidth = sepLineWidth;

        this.vehicleLocations = {
            "north": {"left": [], "middle": [], "right": []},
            "south": {"left": [], "middle": [], "right": []},
            "west": {"left": [], "middle": [], "right": []},
            "east": {"left": [], "middle": [], "right": []},
        }

        this.vehicles = {};
        this.lights = structuredClone(this.vehicleLocations);
        Vehicle.vehicleLength = laneWidth / 2;
        Vehicle.vehicleWidth = laneWidth / 4;
        Vehicle.stepSize = Vehicle.vehicleLength * 1.5;

        this.junctionIntersections = {
            "north": {
                "left": {
                    x: cornerWidth + 2.5 * laneWidth, y: cornerHeight
                },
                "middle": {
                    x: cornerWidth + 1.5 * laneWidth, y: cornerHeight
                }, "right": {
                    x: cornerWidth + 0.5 * laneWidth, y: cornerHeight
                }},

            "south": {
                "left": {
                    x: cornerWidth + 1.5 * laneWidth, y: this.height - cornerHeight
                },
                "middle": {
                    x: cornerWidth + 2.5 * laneWidth, y: this.height - cornerHeight
                },
                "right": {
                    x: cornerWidth + 3.5 * laneWidth, y: this.height - cornerHeight
                }
            },

            "west": {
                "left": {
                    x: cornerWidth, y: cornerHeight + 1.5 * laneWidth
                },
                "middle": {
                    x: cornerWidth, y: cornerHeight + 2.5 * laneWidth
                },
                "right": {
                    x: cornerWidth, y: cornerHeight + 3.5 * laneWidth
                }
            },
            "east": {
                "left": {
                    x: this.width - cornerWidth, y: cornerHeight + 2.5 * laneWidth
                },
                "middle": {
                    x: this.width - cornerWidth, y: cornerHeight + 1.5 * laneWidth
                },
                "right": {
                    x: this.width - cornerWidth, y: cornerHeight + 0.5 * laneWidth
                }},
        }

        this.ctx.strokeRect(cornerWidth + laneWidth * 3 + sepLineWidth / 2, 0, sepLineWidth, cornerHeight);
        this.ctx.strokeRect(cornerWidth + laneWidth * 3 - sepLineWidth * 1.5, 0, sepLineWidth, cornerHeight);

        this.ctx.strokeRect(cornerWidth + laneWidth - sepLineWidth / 2, this.height - cornerHeight,
            sepLineWidth, cornerHeight);
        this.ctx.strokeRect(cornerWidth + laneWidth + sepLineWidth * 1.5, this.height - cornerHeight,
            sepLineWidth, cornerHeight);

        this.ctx.strokeRect(0, cornerHeight + laneWidth - sepLineWidth / 2, cornerWidth, sepLineWidth);
        this.ctx.strokeRect(0, cornerHeight + laneWidth + sepLineWidth * 1.5, cornerWidth, sepLineWidth);

        this.ctx.strokeRect(this.width - cornerWidth, cornerHeight + 3 * laneWidth - sepLineWidth / 2,
            cornerWidth, sepLineWidth);
        this.ctx.strokeRect(this.width - cornerWidth, cornerHeight + 3 * laneWidth + sepLineWidth * 1.5,
            cornerWidth, sepLineWidth);

        this._addLaneSeparators();
        TrafficLight.lightWidth = 1.2 * this.sepWidth;
        TrafficLight.lightHeight = 2.2 * this.sepWidth;
        this.lightWidth = 1.2 * this.sepWidth;
        this.lightHeight = 2.2 * this.sepWidth;

        this.ctx.fillStyle = "black";

        // North road lights
        this.ctx.fillRect(cornerWidth - 2 * sepLineWidth, cornerHeight - 2 * sepLineWidth,
            3 * laneWidth - 2 * sepLineWidth, sepLineWidth);
        const northConfig = ["right", "middle", "left"]
        for (let i = 0; i < 3; i++) {
            this.lights["north"][northConfig[i]] = new TrafficLight(
                "north", northConfig[i],
                cornerWidth + laneWidth * (i + 0.5) - this.lightWidth / 2,
                cornerHeight - sepLineWidth - this.lightHeight / 2
            );
        }

        // South road lights
        this.ctx.fillRect(cornerWidth - 2 * sepLineWidth, cornerHeight + laneWidth + 4 * sepLineWidth,
            sepLineWidth, 3 * laneWidth - 2  *sepLineWidth)

        const southConfig = ["left", "middle", "right"]
        for (let i = 0; i < 3; i++) {
            this.lights["south"][southConfig[i]] = new TrafficLight(
                "south", southConfig[i],
                cornerWidth + laneWidth * (i + 1.5) - this.lightWidth / 2,
                this.height - cornerHeight + sepLineWidth - this.lightHeight / 2
            );
        }

        // West road lights
        this.ctx.fillRect(cornerWidth + laneWidth + 4 * sepLineWidth, this.height - cornerHeight + sepLineWidth,
            3 * laneWidth - 2  *sepLineWidth, sepLineWidth);

        const westConfig = ["left", "middle", "right"]
        for (let i = 0; i < 3; i++) {
            this.lights["west"][westConfig[i]] = new TrafficLight(
                "west", westConfig[i],
                cornerWidth - sepLineWidth - this.lightHeight / 2,
                cornerHeight + laneWidth * (i + 1.5) - this.lightWidth / 2,
            );
        }

        // East road lights
        this.ctx.fillRect(this.width - cornerWidth + sepLineWidth, cornerHeight - 2 * sepLineWidth,
            sepLineWidth, 3 * laneWidth - 2 * sepLineWidth);

        const eastConfig = ["right", "middle", "left"]
        for (let i = 0; i < 3; i++) {
            this.lights["east"][eastConfig[i]] = new TrafficLight(
                "east", eastConfig[i],
                this.width - cornerWidth + sepLineWidth - this.lightHeight / 2,
                cornerHeight + laneWidth * (i + 0.5) - this.lightWidth / 2
            )
        }
    }

    acquireVehicleID() {
        this.lowestVehicleID++;
        return this.lowestVehicleID;
    }

    _addLaneSeparators() {
        const sepWidth = Math.min(this.cornerWidth, this.cornerHeight) / 10;
        this.sepWidth = sepWidth;
        var startY = 0.0;

        while (startY + sepWidth < this.cornerHeight) {
            this.ctx.strokeRect(
                this.cornerWidth + this.laneWidth - this.sepLineWidth / 2,
                startY,
                this.sepLineWidth,
                sepWidth
            );

            this.ctx.strokeRect(
                this.cornerWidth + 2 * this.laneWidth - this.sepLineWidth / 2,
                startY,
                this.sepLineWidth,
                sepWidth
            );
            startY += sepWidth * 2;
        }

        var endY = this.height;
        while (endY - sepWidth > this.height - this.cornerHeight) {
            this.ctx.strokeRect(
                this.cornerWidth + 2 * this.laneWidth - this.sepLineWidth / 2,
                endY - sepWidth,
                this.sepLineWidth,
                sepWidth
            );

            this.ctx.strokeRect(
                this.cornerWidth + 3 * this.laneWidth - this.sepLineWidth / 2,
                endY - sepWidth,
                this.sepLineWidth,
                sepWidth
            );
            endY -= sepWidth * 2;
        }

        var startX = 0.0;
        while (startX + sepWidth < this.cornerWidth) {
            this.ctx.strokeRect(
                startX,
                this.cornerHeight + 2 * this.laneWidth - this.sepLineWidth / 2,
                sepWidth,
                this.sepLineWidth
            );

            this.ctx.strokeRect(
                startX,
                this.cornerHeight + 3 * this.laneWidth - this.sepLineWidth / 2,
                sepWidth,
                this.sepLineWidth
            );

            startX += sepWidth * 2;
        }

        var endX = this.width;
        while (endX - sepWidth > this.width - this.cornerWidth) {
            this.ctx.strokeRect(
                endX - sepWidth,
                this.cornerHeight + this.laneWidth - this.sepLineWidth / 2,
                sepWidth,
                this.sepLineWidth
            );

            this.ctx.strokeRect(
                endX - sepWidth,
                this.cornerHeight + 2 * this.laneWidth - this.sepLineWidth / 2,
                sepWidth,
                this.sepLineWidth
            );

            endX -= sepWidth * 2;
        }
    }

    addVehicle(id, road, lane, color=null) {
        this.vehicleAmount++;
        const vehicle = new Vehicle(id, road, lane, color);
        this.vehicles[id] = vehicle;
        this.vehicleLocations[road][lane].push(id);
        vehicle.draw();
    }

    moveVehicles(vehicleIDs) {
        for (const id of vehicleIDs) {
            const vehicle = this.vehicles[id];
            vehicle.erase();
            this.vehicleAmount--;
            const neighbors = this.vehicleLocations[vehicle.startRoad][vehicle.startLane];
            neighbors.shift();
            neighbors.forEach(function (vehicleID) {this.vehicles[vehicleID].moveForward();}.bind(this))

            delete this.vehicles[id];
        }
    }

    configureLights(lightConfig) {
        for (const direction of ["north", "south", "east", "west"]) {
            for (const lane of ["left", "middle", "right"]) {
                const color = lightConfig[direction][lane];
                this.lights[direction][lane].setState(color);
            }
        }
    }
}