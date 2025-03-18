# Traffic Lights Simulation

The application is designed to simulate the work of traffic lights on a junction that adjust the light cycles to the intensity of traffic on each road. The simulation can be run
in two forms:
  1. Perform one single simulation that reads commands from an input file (must be of the .json format) and writes the results to an output file (also .json). This option
doesn't have a GUI visualization.
  2. Launch a Spring Boot web server and dynamically manage the state of the junction by adding vehicles or making them move.

### Requirements for a single simulation
As stated above you will need an input file. Name it whatever you want, I'll be refering it as `input.json`. Same rule for `output.json`. The `input.json` file **must** be placed
in the `src/main/resources/` directory in order for the program to work correctly.

The `output.json` file doesn't need to be created beforehand. If it doesn't exist it will be dynamically created in the `output/` directory. Note that if you want
to write to an existing file you must put it in `output/`.

#### Input file format
This file should contain a list of commands to execute during runtime. There are two accepted types of commands:
  - *addVehicle*: as the name implies it adds a vehicle to the junction (you must specify an ID for the vehicle as well as the road it's start road and end (destination) road
  - *step*: steps forward in time making all vehicles that have a green light leave the junction. The configuration of green lights can change here

**Example** `input.json` **file**:
```
{
"commands": [
    {
      "type": "addVehicle",
      "vehicleId": "v1",
      "startRoad": "north",
      "endRoad": "south"
    },
    {
      "type": "step"
    },
    {
      "type": "step"
    }
  ]
}
```
#### The output file format
In the `output.json` file you'll see the list of vehicles that have left the junction after each step. The example below will be the output file of the commands listed in the example above.
```
{
  "stepStatuses": [
    {
      "leftVehicles": [
        "v1"
      ],
    },
    {
      "leftVehicles": []
    }
  ]
}
```
In the first step the vehicle *v1* leaves the junction and in step 2 there would be no vehicles left so the result is an empty list.

## How the system works
In the simulated junction there are four intersecting roads (technically it's two roads that cross but it's easier to explain with 4):
  - North
  - East
  - South
  - West

Each road is divided into 4 lanes:
  - Right turn lane
  - Straight ahead lane
  - Left turn lane
  - Opposite lane (for vehicles who's destination is this road)

![image](https://github.com/user-attachments/assets/2b420a3d-3e20-492b-97a2-9f0f1400fae9)
Each opposite lane is separated from the incomming lanes by a double straight line.

A ***lights configuration*** will refer to a set of traffic lights that can be green at the same time. In the system there are four configurations that guarantee no collisions
will happen and every incomming lane will have it's turn.

##### Todo: images of the configurations

### The priority formula
The priority *P* for a traffic lights configuration is given with the formula 
$$P(a)=a$$

## Build and run using a Makefile
Build and run the web service:
```
make
```

Build and run one simulation
```
make ARGS="input.json output.json"
```

## Build and run with Docker
Build the project with the following command:
```
docker build -t traffic .
```

When it's ready run it:
```
docker run -p 8080:8080 traffic
```

## Build with Maven
In order to build the project run the following commands:
```
mvn clean install
mvn clean package
```

Running one simulation (without running the web service) described in your `input.json` file that writes the result to `output.json`:
```
java -jar target/traffic-1.1-SNAPSHOT.jar input.json output.json
```
Replace `input.json` and `output.json` with the file names you're using.

In order to run the web service simply run the command above without any extra arguments:
```
java -jar target/traffic-1.1-SNAPSHOT.jar
```