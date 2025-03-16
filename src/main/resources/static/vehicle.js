export default class Vehicle {
    static vehicleWidth;
    static vehicleLength;
    static stepSize;
    
    constructor(id, startRoad, endRoad, startLane, color) {
        this.ctx = window.canvasHandler.ctx;
        this.startRoad = startRoad;
        this.endRoad = endRoad;
        this.startLane = startLane;
        this.id = id;
        this.angle = 0;
        this.tyreSubFactor = 0.05;
        this.tyreLengthFactor = 5 * this.tyreSubFactor;
        this.tyreAddFactor = 1 - this.tyreLengthFactor + this.tyreSubFactor;
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

    static configureTrajectories() {
        const laneWidth = window.canvasHandler.laneWidth;
        const cornerWidth = window.canvasHandler.cornerWidth;
        const cornerHeight = window.canvasHandler.cornerHeight;
    }

    center() {
        return {x: this.x + this.w / 2, y: this.y + this.h / 2};
    }

    draw() {
        this.ctx.save();
        this.ctx.translate(this.center().x, this.center().y);
        this.ctx.rotate(this.angle);
        this.ctx.fillStyle = "black";
        this.ctx.fillRect(
            -this.w / 2 - this.w * this.tyreSubFactor,
            -this.h / 2 - this.h * this.tyreSubFactor,
            this.w * this.tyreLengthFactor, this.h * this.tyreLengthFactor);
        this.ctx.fillRect(
            -this.w / 2 - this.w * this.tyreSubFactor,
            -this.h / 2 + this.h * this.tyreAddFactor,
            this.w * this.tyreLengthFactor, this.h * this.tyreLengthFactor);
        this.ctx.fillRect(
            -this.w / 2 + this.w * this.tyreAddFactor,
            -this.h / 2 + this.h * this.tyreAddFactor,
            this.w * this.tyreLengthFactor, this.h * this.tyreLengthFactor);
        this.ctx.fillRect(
            -this.w / 2 + this.w * this.tyreAddFactor,
            -this.h / 2 - this.h * this.tyreSubFactor,
            this.w * this.tyreLengthFactor, this.h * this.tyreLengthFactor);

        this.ctx.fillStyle = this.color;
        this.ctx.fillRect(-this.w / 2, -this.h / 2, this.w, this.h);
        this.ctx.restore();
    }

    erase() {
        this.ctx.save();
        this.ctx.translate(this.center().x, this.center().y);
        this.ctx.rotate(this.angle);
        this.ctx.clearRect(
            -this.w / 2 - this.tyreSubFactor * this.w - 2,
            -this.h / 2 - this.tyreSubFactor * this.h - 2,
            this.w + 2 * this.tyreSubFactor * this.w + 4,
            this.h + 2 * this.tyreSubFactor * this.h + 4);
        this.ctx.restore();
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

    animateMotion() {
        // const laneWidth = window.canvasHandler.laneWidth;
        // const cornerWidth = window.canvasHandler.cornerWidth;
        // const cornerHeight = window.canvasHandler.cornerHeight;
        // const target = {x: null, y: null};
        // switch (this.endRoad) {
        //     case "north":
        //         target.x = cornerWidth + 3.5 * laneWidth;
        //         target.y = 0;
        //         break;
        //     case "south":
        //         target.x = cornerWidth + 0.5 * laneWidth;
        //         target.y = this.ctx.canvas.height;
        //         break;
        //     case "east":
        //         target.x = this.ctx.canvas.width;
        //         target.y = cornerHeight + 0.5 * laneWidth;
        //         break;
        //     case "west":
        //         target.x = this.ctx.canvas.width;
        //         target.y = cornerHeight + 3.5 * laneWidth;
        // }
        // const dx = (target.x - this.center().x) / 100;
        // const dy = (target.y - this.center().y) / 100;
        this.animate({index: 0, steps: 20, initialSteps: 20});
    }

    animate(config) {
        const trajectory = window.canvasHandler.trajectories[this.startRoad][this.startLane];
        if (config.index === trajectory.length) {
            this.erase();
            return;
        }

        config.steps--;
        const checkpoint = trajectory[config.index];

        if (config.dx === undefined) {
            config.dx = (checkpoint.x - this.center().x) / config.initialSteps;
            config.dy = (checkpoint.y - this.center().y) / config.initialSteps;
        }
        this.erase();
        this.x += config.dx;
        this.y += config.dy;
        if (config.steps === 0) {
            this.angle = checkpoint.angle;
            this.x = checkpoint.x - this.w / 2;
            this.y = checkpoint.y - this.h / 2;
            config.index++;
            config.steps = config.initialSteps;
            delete config.dx;
            delete config.dy;
        }
        this.draw();
        window.canvasHandler.paintLights();
        window.canvasHandler.paintVehicles();

        requestAnimationFrame(() => this.animate(config));
    }
}