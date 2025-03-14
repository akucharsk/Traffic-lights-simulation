export default class CanvasHandler {
    constructor(canvas) {
        const opts = document.getElementById("opts");
        canvas.width = (window.innerWidth - opts.clientWidth) * 0.8;
        canvas.height = window.innerHeight * 0.9;

        this.lowestVehicleID = 0;

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

        this.vehicles = {
            "north": {"left": [], "middle": [], "right": []},
            "south": {"left": [], "middle": [], "right": []},
            "west": {"left": [], "middle": [], "right": []},
            "east": {"left": [], "middle": [], "right": []},
        }
        this.vehiclesByID = {};
        this.vehicleLength = laneWidth / 2;
        this.vehicleWidth = laneWidth / 4;

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
        this.lightWidth = 1.2 * this.sepWidth;
        this.lightHeight = 2.2 * this.sepWidth;

        this.ctx.fillStyle = "black";

        // North road lights
        this.ctx.fillRect(cornerWidth - 2 * sepLineWidth, cornerHeight - 2 * sepLineWidth,
            3 * laneWidth - 2 * sepLineWidth, sepLineWidth);
        this.northLights = {}
        const northConfig = ["right", "middle", "left"]
        for (let i = 0; i < 3; i++) {
            const light = {
                x: cornerWidth + laneWidth * (i + 0.5) - this.lightWidth / 2,
                y: cornerHeight - sepLineWidth - this.lightHeight / 2,
                w: this.lightWidth,
                h: this.lightHeight,
            }
            this.northLights[northConfig[i]] = light;
            this.ctx.fillRect(light.x, light.y, light.w, light.h);
        }
        for (const config of northConfig) {
            const light = this.northLights[config];
            this.northLights[config]["red"] = {x: light.x + light.w / 2, y: light.y + 5 * light.h / 6, r: light.h / 6};
            this.northLights[config]["yellow"] = {x: light.x + light.w / 2, y: light.y + light.h / 2, r: light.h / 6};
            this.northLights[config]["green"] = {x: light.x + light.w / 2, y: light.y + light.h / 6, r: light.h / 6};
        }

        // South road lights
        this.ctx.fillRect(cornerWidth - 2 * sepLineWidth, cornerHeight + laneWidth + 4 * sepLineWidth,
            sepLineWidth, 3 * laneWidth - 2  *sepLineWidth)

        this.southLights = {}
        const southConfig = ["left", "middle", "right"]
        for (let i = 0; i < 3; i++) {
            const light = {
                x: cornerWidth + laneWidth * (i + 1.5) - this.lightWidth / 2,
                y: this.height - cornerHeight + sepLineWidth - this.lightHeight / 2,
                w: this.lightWidth,
                h: this.lightHeight,
            }
            this.southLights[southConfig[i]] = light;
            this.ctx.fillRect(light.x, light.y, light.w, light.h);
        }

        for (const config of southConfig) {
            const light = this.southLights[config];
            this.southLights[config]["red"] = {x: light.x + light.w / 2, y: light.y + light.h / 6, r: light.h / 6};
            this.southLights[config]["yellow"] = {x: light.x + light.w / 2, y: light.y + light.h / 2, r: light.h / 6};
            this.southLights[config]["green"] = {x: light.x + light.w / 2, y: light.y + 5 * light.h / 6, r: light.h / 6};
        }

        // West road lights
        this.ctx.fillRect(cornerWidth + laneWidth + 4 * sepLineWidth, this.height - cornerHeight + sepLineWidth,
            3 * laneWidth - 2  *sepLineWidth, sepLineWidth);

        this.westLights = {}
        const westConfig = ["left", "middle", "right"]
        for (let i = 0; i < 3; i++) {
            const light = {
                x: cornerWidth - sepLineWidth - this.lightHeight / 2,
                y: cornerHeight + laneWidth * (i + 1.5) - this.lightWidth / 2,
                w: this.lightHeight,
                h: this.lightWidth
            }
            this.westLights[westConfig[i]] = light;
            this.ctx.fillRect(light.x, light.y, light.w, light.h);
        }
        for (const config of westConfig) {
            const light = this.westLights[config];
            this.westLights[config]["red"] = {x: light.x + 5 * light.w / 6, y: light.y + light.h / 2, r: light.w / 6};
            this.westLights[config]["yellow"] = {x: light.x + light.w / 2, y: light.y + light.h / 2, r: light.w / 6};
            this.westLights[config]["green"] = {x: light.x + light.w / 6, y: light.y + light.h / 2, r: light.w / 6};
        }

        // East road lights
        this.ctx.fillRect(this.width - cornerWidth + sepLineWidth, cornerHeight - 2 * sepLineWidth,
            sepLineWidth, 3 * laneWidth - 2 * sepLineWidth);

        this.eastLights = {}
        const eastConfig = ["right", "middle", "left"]
        for (let i = 0; i < 3; i++) {
            const light = {
                x: this.width - cornerWidth + sepLineWidth - this.lightHeight / 2,
                y: cornerHeight + laneWidth * (i + 0.5) - this.lightWidth / 2,
                w: this.lightHeight,
                h: this.lightWidth,
            }
            this.eastLights[eastConfig[i]] = light;
            this.ctx.fillRect(light.x, light.y, light.w, light.h);
        }

        for (const config of eastConfig) {
            const light = this.eastLights[config];
            this.eastLights[config]["red"] = {x: light.x + light.w / 6, y: light.y + light.h / 2, r: light.w / 6};
            this.eastLights[config]["yellow"] = {x: light.x + light.w / 2, y: light.y + light.h / 2, r: light.w / 6};
            this.eastLights[config]["green"] = {x: light.x + 5 * light.w / 6, y: light.y + light.h / 2, r: light.w / 6};
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

    drawLight(params) {
        this.ctx.beginPath();
        this.ctx.arc(params.x, params.y, params.r, 0, 2 * Math.PI);
        this.ctx.fill();
    }

    addVehicle(id, road, lane, color=null) {
        const vehicleAmount = this.vehicles[road][lane].length;
        this.vehicles[road][lane].push(id);
        var junction;
        var vehicle;
        color = color !== null ? color :
            `rgb(${Math.random() * 128}, ${Math.random() * 128}, ${Math.random() * 128})`
        this.ctx.fillStyle = color;
        switch (road) {
            case "north":
                junction = this.junctionIntersections[road][lane];
                vehicle = {id: id, color: color, x: junction.x - this.vehicleWidth / 2,
                    y: junction.y - (vehicleAmount + 2) * this.vehicleLength,
                    w: this.vehicleWidth, h: this.vehicleLength}
                break;
            case "south":
                junction = this.junctionIntersections[road][lane];
                vehicle = {id: id, color: color, x: junction.x - this.vehicleWidth / 2,
                    y: junction.y + (vehicleAmount + 1) * this.vehicleLength,
                    w: this.vehicleWidth, h: this.vehicleLength}
                break;
            case "west":
                junction = this.junctionIntersections[road][lane];
                vehicle = {id: id, color: color, x: junction.x - (vehicleAmount + 2) * this.vehicleLength,
                    y: junction.y - this.vehicleWidth / 2, w: this.vehicleLength, h: this.vehicleWidth}
                break;
            case "east":
                junction = this.junctionIntersections[road][lane];
                vehicle = {id: id, color: color, x: junction.x + (vehicleAmount + 1) * this.vehicleLength,
                    y: junction.y - this.vehicleWidth / 2, w: this.vehicleLength, h: this.vehicleWidth}
                break;
        }
        this.ctx.fillRect(vehicle.x, vehicle.y, vehicle.w, vehicle.h);
        this.vehicles[road][lane].push(vehicle);
        this.vehiclesByID[id] = vehicle;
    }

    configureVehicles(map) {
        for (const direction of ["north", "south", "east", "west"]) {
            for (const lane of ["left", "middle", "right"]) {
                this.vehicles[direction][lane] = [];
                for (const id of map[direction][lane]["vehicles"]) {
                    const color = this.vehiclesByID[id] !== undefined ?
                        this.vehiclesByID[id].color : null;
                    this.addVehicle(id, direction, lane, color);
                }
                const lights = `${direction}Lights`;
                const light = this[lights][lane];
                this.ctx.fillStyle = "black";
                this.ctx.fillRect(light.x, light.y, light.w, light.h);

                const color = map[direction][lane]["lights"];
                this.ctx.fillStyle = color;
                this.drawLight(light[color]);
            }
        }
    }

    clearVehicles() {
        for (const direction of ["north", "south", "east", "west"]) {
            for (const lane of ["left", "middle", "right"]) {
                for (const vehicle of this.vehicles[direction][lane]) {
                    this.ctx.clearRect(vehicle.x - 2, vehicle.y - 2, vehicle.w + 4, vehicle.h + 4);
                }
                this.vehicles[direction][lane] = [];
            }
        }
    }
}