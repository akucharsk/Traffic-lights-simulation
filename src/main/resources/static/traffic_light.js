export default class TrafficLight {
    static lightWidth;
    static lightHeight;

    constructor(road, lane, x, y) {
        this.x = x;
        this.y = y;
        this.state = "red";
        this.ctx = window.canvasHandler.ctx;

        if (road in ["north", "south"]){
            this.w = TrafficLight.lightWidth;
            this.h = TrafficLight.lightHeight;
        } else {
            this.w = TrafficLight.lightHeight;
            this.h = TrafficLight.lightWidth;
        }

        switch (road) {
            case "north":
                this.red = {x: this.x + this.w / 2, y: this.y + 5 * this.h / 6, r: this.h / 6};
                this.yellow = {x: this.x + this.w / 2, y: this.y + this.h / 2, r: this.h / 6};
                this.green = {x: this.x + this.w / 2, y: this.y + this.h / 6, r: this.h / 6};
                break;
            case "south":
                this.red = {x: this.x + this.w / 2, y: this.y + this.h / 6, r: this.h / 6};
                this.yellow = {x: this.x + this.w / 2, y: this.y + this.h / 2, r: this.h / 6};
                this.green = {x: this.x + this.w / 2, y: this.y + 5 * this.h / 6, r: this.h / 6};
                break;
            case "west":
                this.red = {x: this.x + 5 * this.w / 6, y: this.y + this.h / 2, r: this.w / 6};
                this.yellow = {x: this.x + this.w / 2, y: this.y + this.h / 2, r: this.w / 6};
                this.green = {x: this.x + this.w / 6, y: this.y + this.h / 2, r: this.w / 6};
                break;
            case "east":
                this.red = {x: this.x + this.w / 6, y: this.y + this.h / 2, r: this.w / 6};
                this.yellow = {x: this.x + this.w / 2, y: this.y + this.h / 2, r: this.w / 6};
                this.green = {x: this.x + 5 * this.w / 6, y: this.y + this.h / 2, r: this.w / 6};
                break;
        }
        this.clear();
        this.draw("red");
    }

    clear() {
        this.ctx.fillStyle = "black";
        this.ctx.fillRect(this.x, this.y, this.w, this.h);
    }

    draw(color) {
        this.ctx.fillStyle = color;
        this.ctx.beginPath();
        this.ctx.arc(this[color].x, this[color].y, this[color].r, 0, 2 * Math.PI, false);
        this.ctx.fill();
    }

    setState(state) {
        if (this.state === state)
            return;
        if (this.state === "green") {
            this.clear();
            this.draw("yellow");
            return new Promise((resolve, reject) => {
                setTimeout(resolve, 500);
            }).then(() => {this.clear(); this.draw("red");});
        }
        this.draw("yellow");
        return new Promise((resolve, reject) => {
            setTimeout(resolve, 500);
        }).then(() => {this.clear(); this.draw("green");});
    }
}