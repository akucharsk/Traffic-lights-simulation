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
                },
                "right": {
                    x: cornerWidth + 0.5 * laneWidth, y: cornerHeight
                },
                "opposite": {
                    x: cornerWidth + 3.5 * laneWidth, y: cornerHeight
                }
            },

            "south": {
                "left": {
                    x: cornerWidth + 1.5 * laneWidth, y: this.height - cornerHeight
                },
                "middle": {
                    x: cornerWidth + 2.5 * laneWidth, y: this.height - cornerHeight
                },
                "right": {
                    x: cornerWidth + 3.5 * laneWidth, y: this.height - cornerHeight
                },
                "opposite": {
                    x: cornerWidth + 0.5 * laneWidth, y: this.height - cornerHeight
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
                },
                "opposite": {
                    x: cornerWidth, y: cornerHeight + 0.5 * laneWidth
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
                },
                "opposite": {
                    x: this.width - cornerWidth, y: cornerHeight + 3.5 * laneWidth
                }
            },
        }

        this.trajectories = {
            "north": {
                "left": [
                    {
                        x: this.junctionIntersections.north.left.x,
                        y: this.junctionIntersections.east.left.y,
                        angle: -Math.PI / 4
                    },
                    {
                        x: this.junctionIntersections.north.opposite.x,
                        y: this.junctionIntersections.east.opposite.y,
                        angle: -Math.PI / 2
                    },
                    {
                        x: this.width,
                        y: this.junctionIntersections.east.opposite.y,
                        angle: -Math.PI / 2
                    }
                ],
                "middle": [
                    {
                        x: this.junctionIntersections.north.middle.x,
                        y: this.junctionIntersections.east.left.y,
                        angle: Math.PI / 4
                    },
                    {
                        x: this.junctionIntersections.south.opposite.x,
                        y: this.junctionIntersections.east.opposite.y,
                        angle: 0
                    },
                    {
                        x: this.junctionIntersections.south.opposite.x,
                        y: this.height,
                        angle: 0
                    }
                ],
                "right": [
                    {
                        x: this.junctionIntersections.north.right.x,
                        y: this.junctionIntersections.west.opposite.y,
                        angle: Math.PI / 2
                    },
                    {
                        x: 0,
                        y: this.junctionIntersections.west.opposite.y,
                        angle: Math.PI / 2
                    }
                ]
            },
            "south": {
                "left": [
                    {
                        x: this.junctionIntersections.south.left.x,
                        y: this.junctionIntersections.west.left.y,
                        angle: -Math.PI / 4
                    },
                    {
                        x: this.junctionIntersections.south.opposite.x,
                        y: this.junctionIntersections.west.opposite.y,
                        angle: -Math.PI / 2
                    },
                    {
                        x: 0,
                        y: this.junctionIntersections.west.opposite.y,
                        angle: -Math.PI / 2
                    }
                ],
                "middle": [
                    {
                        x: this.junctionIntersections.south.middle.x,
                        y: this.junctionIntersections.west.left.y,
                        angle: Math.PI / 4
                    },
                    {
                        x: this.junctionIntersections.north.opposite.x,
                        y: this.junctionIntersections.west.opposite.y,
                        angle: 0
                    },
                    {
                        x: this.junctionIntersections.north.opposite.x,
                        y: 0,
                        angle: 0
                    }
                ],
                "right": [
                    {
                        x: this.junctionIntersections.south.right.x,
                        y: this.junctionIntersections.east.opposite.y,
                        angle: Math.PI / 2
                    },
                    {
                        x: this.width,
                        y: this.junctionIntersections.east.opposite.y,
                        angle: Math.PI / 2
                    }
                ]
            },
            "east": {
                "left": [
                    {
                        x: this.junctionIntersections.south.left.x,
                        y: this.junctionIntersections.east.left.y,
                        angle: -Math.PI / 4
                    },
                    {
                        x: this.junctionIntersections.south.opposite.x,
                        y: this.junctionIntersections.east.opposite.y,
                        angle: -Math.PI / 2
                    },
                    {
                        x: this.junctionIntersections.south.opposite.x,
                        y: this.height,
                        angle: -Math.PI / 2
                    }
                ],
                "middle": [
                    {
                        x: this.junctionIntersections.south.left.x,
                        y: this.junctionIntersections.east.middle.y,
                        angle: Math.PI / 4
                    },
                    {
                        x: this.junctionIntersections.south.opposite.x,
                        y: this.junctionIntersections.west.opposite.y,
                        angle: 0
                    },
                    {
                        x: 0,
                        y: this.junctionIntersections.west.opposite.y,
                        angle: 0
                    }
                ],
                "right": [
                    {
                        x: this.junctionIntersections.north.opposite.x,
                        y: this.junctionIntersections.east.right.y,
                        angle: Math.PI / 2
                    },
                    {
                        x: this.junctionIntersections.north.opposite.x,
                        y: 0,
                        angle: Math.PI / 2
                    }
                ]
            },
            "west": {
                "left": [
                    {
                        x: this.junctionIntersections.north.left.x,
                        y: this.junctionIntersections.west.left.y,
                        angle: -Math.PI / 4
                    },
                    {
                        x: this.junctionIntersections.north.opposite.x,
                        y: this.junctionIntersections.west.opposite.y,
                        angle: Math.PI / 2
                    },
                    {
                        x: this.junctionIntersections.north.opposite.x,
                        y: 0,
                        angle: Math.PI / 2
                    }
                ],
                "middle": [
                    {
                        x: this.junctionIntersections.north.left.x,
                        y: this.junctionIntersections.west.middle.y,
                        angle: Math.PI / 4
                    },
                    {
                        x: this.junctionIntersections.north.opposite.x,
                        y: this.junctionIntersections.east.opposite.y,
                        angle: 0
                    },
                    {
                        x: this.width,
                        y: this.junctionIntersections.east.opposite.y,
                        angle: 0
                    }
                ],
                "right": [
                    {
                        x: this.junctionIntersections.south.opposite.x,
                        y: this.junctionIntersections.west.right.y,
                        angle: Math.PI / 2
                    },
                    {
                        x: this.junctionIntersections.south.opposite.x,
                        y: this.height,
                        angle: Math.PI / 2
                    }
                ]
            }
        }
        this.sepWidth = Math.min(this.cornerWidth, this.cornerHeight) / 10;
        this._addLaneSeparators();
        TrafficLight.lightWidth = 1.2 * this.sepWidth;
        TrafficLight.lightHeight = 2.2 * this.sepWidth;
        TrafficLight.ctx = this.ctx;

        this.lightWidth = 1.2 * this.sepWidth;
        this.lightHeight = 2.2 * this.sepWidth;

        // North road lights
        const northConfig = ["right", "middle", "left"]
        for (let i = 0; i < 3; i++) {
            this.lights["north"][northConfig[i]] = new TrafficLight(
                "north", northConfig[i],
                cornerWidth + laneWidth * (i + 0.5) - this.lightWidth / 2,
                cornerHeight - sepLineWidth - this.lightHeight / 2
            );
        }

        // South road lights
        const southConfig = ["left", "middle", "right"]
        for (let i = 0; i < 3; i++) {
            this.lights["south"][southConfig[i]] = new TrafficLight(
                "south", southConfig[i],
                cornerWidth + laneWidth * (i + 1.5) - this.lightWidth / 2,
                this.height - cornerHeight + sepLineWidth - this.lightHeight / 2
            );
        }

        // West road lights

        const westConfig = ["left", "middle", "right"]
        for (let i = 0; i < 3; i++) {
            this.lights["west"][westConfig[i]] = new TrafficLight(
                "west", westConfig[i],
                cornerWidth - sepLineWidth - this.lightHeight / 2,
                cornerHeight + laneWidth * (i + 1.5) - this.lightWidth / 2,
            );
        }

        // East road lights
        const eastConfig = ["right", "middle", "left"]
        for (let i = 0; i < 3; i++) {
            this.lights["east"][eastConfig[i]] = new TrafficLight(
                "east", eastConfig[i],
                this.width - cornerWidth + sepLineWidth - this.lightHeight / 2,
                cornerHeight + laneWidth * (i + 0.5) - this.lightWidth / 2
            )
        }

        this.paint();
    }

    paint() {
        this.ctx.clearRect(0, 0, this.ctx.canvas.width, this.ctx.canvas.height);
        this.ctx.fillStyle = "blue";
        this.ctx.fillRect(0, 0, this.cornerWidth, this.cornerHeight);

        this.ctx.fillStyle = "lightgreen";
        this.ctx.fillRect(0, this.roadWidth + this.cornerHeight, this.cornerWidth, this.cornerHeight);

        this.ctx.fillStyle = "yellow";
        this.ctx.fillRect(this.roadWidth + this.cornerWidth, this.roadWidth + this.cornerHeight, this.cornerWidth, this.cornerHeight);

        this.ctx.fillStyle = "cyan";
        this.ctx.fillRect(this.roadWidth + this.cornerWidth, 0, this.cornerWidth, this.cornerHeight);

        this.ctx.strokeRect(this.cornerWidth + this.laneWidth * 3 + this.sepLineWidth / 2, 0, this.sepLineWidth, this.cornerHeight);
        this.ctx.strokeRect(this.cornerWidth + this.laneWidth * 3 - this.sepLineWidth * 1.5, 0, this.sepLineWidth, this.cornerHeight);

        this.ctx.strokeRect(this.cornerWidth + this.laneWidth - this.sepLineWidth / 2, this.height - this.cornerHeight,
            this.sepLineWidth, this.cornerHeight);
        this.ctx.strokeRect(this.cornerWidth + this.laneWidth + this.sepLineWidth * 1.5, this.height - this.cornerHeight,
            this.sepLineWidth, this.cornerHeight);

        this.ctx.strokeRect(0, this.cornerHeight + this.laneWidth - this.sepLineWidth / 2, this.cornerWidth, this.sepLineWidth);
        this.ctx.strokeRect(0, this.cornerHeight + this.laneWidth + this.sepLineWidth * 1.5, this.cornerWidth, this.sepLineWidth);

        this.ctx.strokeRect(this.width - this.cornerWidth, this.cornerHeight + 3 * this.laneWidth - this.sepLineWidth / 2,
            this.cornerWidth, this.sepLineWidth);
        this.ctx.strokeRect(this.width - this.cornerWidth, this.cornerHeight + 3 * this.laneWidth + this.sepLineWidth * 1.5,
            this.cornerWidth, this.sepLineWidth);

        this._addLaneSeparators();

        this.paintLights();
        this.paintVehicles();
    }

    paintLights() {
        this.ctx.fillStyle = "black";
        this.ctx.fillRect(this.cornerWidth - 2 * this.sepLineWidth, this.cornerHeight - 2 * this.sepLineWidth,
        3 * this.laneWidth - 2 * this.sepLineWidth, this.sepLineWidth);
        this.ctx.fillRect(this.cornerWidth - 2 * this.sepLineWidth, this.cornerHeight + this.laneWidth + 4 * this.sepLineWidth,
            this.sepLineWidth, 3 * this.laneWidth - 2  *this.sepLineWidth)
        this.ctx.fillRect(this.cornerWidth + this.laneWidth + 4 * this.sepLineWidth, this.height - this.cornerHeight + this.sepLineWidth,
            3 * this.laneWidth - 2  *this.sepLineWidth, this.sepLineWidth);
        this.ctx.fillRect(this.width - this.cornerWidth + this.sepLineWidth, this.cornerHeight - 2 * this.sepLineWidth,
            this.sepLineWidth, 3 * this.laneWidth - 2 * this.sepLineWidth);

        for (const direction of ["north", "south", "east", "west"]) {
            for (const lane of ["left", "right", "middle"]) {
                this.lights[direction][lane].paint();
            }
        }
    }

    paintVehicles() {
        for (const vehicleID in this.vehicles) {
            this.vehicles[vehicleID].draw();
        }
    }

    acquireVehicleID() {
        this.lowestVehicleID++;
        return this.lowestVehicleID;
    }

    _addLaneSeparators() {
        var startY = 0.0;

        while (startY + this.sepWidth < this.cornerHeight) {
            this.ctx.strokeRect(
                this.cornerWidth + this.laneWidth - this.sepLineWidth / 2,
                startY,
                this.sepLineWidth,
                this.sepWidth
            );

            this.ctx.strokeRect(
                this.cornerWidth + 2 * this.laneWidth - this.sepLineWidth / 2,
                startY,
                this.sepLineWidth,
                this.sepWidth
            );
            startY += this.sepWidth * 2;
        }

        var endY = this.height;
        while (endY - this.sepWidth > this.height - this.cornerHeight) {
            this.ctx.strokeRect(
                this.cornerWidth + 2 * this.laneWidth - this.sepLineWidth / 2,
                endY - this.sepWidth,
                this.sepLineWidth,
                this.sepWidth
            );

            this.ctx.strokeRect(
                this.cornerWidth + 3 * this.laneWidth - this.sepLineWidth / 2,
                endY - this.sepWidth,
                this.sepLineWidth,
                this.sepWidth
            );
            endY -= this.sepWidth * 2;
        }

        var startX = 0.0;
        while (startX + this.sepWidth < this.cornerWidth) {
            this.ctx.strokeRect(
                startX,
                this.cornerHeight + 2 * this.laneWidth - this.sepLineWidth / 2,
                this.sepWidth,
                this.sepLineWidth
            );

            this.ctx.strokeRect(
                startX,
                this.cornerHeight + 3 * this.laneWidth - this.sepLineWidth / 2,
                this.sepWidth,
                this.sepLineWidth
            );

            startX += this.sepWidth * 2;
        }

        var endX = this.width;
        while (endX - this.sepWidth > this.width - this.cornerWidth) {
            this.ctx.strokeRect(
                endX - this.sepWidth,
                this.cornerHeight + this.laneWidth - this.sepLineWidth / 2,
                this.sepWidth,
                this.sepLineWidth
            );

            this.ctx.strokeRect(
                endX - this.sepWidth,
                this.cornerHeight + 2 * this.laneWidth - this.sepLineWidth / 2,
                this.sepWidth,
                this.sepLineWidth
            );

            endX -= this.sepWidth * 2;
        }
    }

    addVehicle(id, startRoad, endRoad, lane, color=null) {
        this.vehicleAmount++;
        const vehicle = new Vehicle(id, startRoad, endRoad, lane, color);
        this.vehicles[id] = vehicle;
        this.vehicleLocations[startRoad][lane].push(id);
        vehicle.draw();
    }

    moveVehicles(vehicleIDs) {
        for (const id of vehicleIDs) {
            const vehicle = this.vehicles[id];
            vehicle.animateMotion();
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