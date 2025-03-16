export default class TrafficLight {
    static lightWidth;
    static lightHeight;
    static ctx;

    constructor(road, lane, x, y) {
        this.x = x;
        this.y = y;
        this.state = ["red"];

        if (road === "north" || road === "south") {
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
        TrafficLight.ctx.fillStyle = "black";
        TrafficLight.ctx.fillRect(this.x, this.y, this.w, this.h);
    }

    draw(color) {
        TrafficLight.ctx.fillStyle = color;
        TrafficLight.ctx.beginPath();
        TrafficLight.ctx.arc(this[color].x, this[color].y, this[color].r, 0, 2 * Math.PI, false);
        TrafficLight.ctx.fill();
    }

    paint() {
        this.clear();
        for (const state of this.state)
            this.draw(state);
    }

    async setState(state) {
        if (this.state.length === 1 && this.state[0] === state)
            return;

        if (state === "red") {
            this.clear();
            this.state = ["yellow"]
            this.draw("yellow");
            return new Promise((resolve, reject) => {
                setTimeout(resolve, 500);
            }).then(() => {this.clear(); this.draw("red"); this.state = ["red"]});
        }
        this.state.push("yellow");
        this.draw("yellow");
        return new Promise((resolve, reject) => {
            setTimeout(resolve, 500);
        }).then(() => {this.clear(); this.draw("green"); this.state = ["green"];});
    }
}