export default class TrafficLight {
    static lightWidth;
    static lightHeight;

    constructor(road, lane, x, y) {
        this.x = x;
        this.y = y;

        if (road in ["north", "south"]){
            this.w = TrafficLight.lightWidth;
            this.h = TrafficLight.lightHeight;
        } else {
            this.w = TrafficLight.lightHeight;
            this.h = TrafficLight.lightWidth;
        }
    }
}