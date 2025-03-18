# Traffic Lights Simulation
### Content:
  - [Building with a makefile](#Build-and-run-using-a-Makefile)
  - [Building with Docker](#Build-and-run-with-docker)
  - [Building with Maven](#Build-with-Maven)
  - [Single simulation requirements](#Requirements-for-a-single-simulation)
  - [Input file format](#Input-file-format)
  - [Output file format](#Output-file-format)
  - [How the system works](#How-the-system-works)
  - [The priority formula](#The-priority-formula)
  - [The algorithm](#The-algorithm)
  - [Understanding the web service](#Understanding-the-web-service)

The application is designed to simulate the work of traffic lights on a junction that adjust the light cycles to the intensity of traffic on each road. The simulation can be run
in two forms:
  1. Perform one single simulation that reads commands from an input file (must be of the .json format) and writes the results to an output file (also .json). This option
doesn't have a GUI visualization.
  2. Launch a Spring Boot web server and dynamically manage the state of the junction by adding vehicles or making them move. Once launched visit http://localhost:8080

## Build and run using a Makefile

**Build and run the web service**:
```
make
```

**Build and run a single simulation**:
```
make ARGS="input.json output.json"
```

## Build and run with Docker
Build the project with the following command:
```
docker build -t traffic .
```

#### Run a single simulation with
```
docker run -v ./output:/app/output traffic input.json output.json
```

#### Run the web server with
```
docker run -p 8080:8080 traffic
```

## Build with Maven
In order to build the project run the following commands:
```
mvn clean package
```

**Running one simulation**:
```
java -jar target/traffic-1.1-SNAPSHOT.jar input.json output.json
```

**Running the web service**:
```
java -jar target/traffic-1.1-SNAPSHOT.jar
```

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
#### Output file format
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

A ***lights configuration*** will refer to a set of traffic lights that can be green at the same time. In the system there are four configurations that guarantee no collisions will happen and every incomming lane will have it's turn.

***Active steps*** refer to the number of consecutive steps where a given configuration had a green light.

***Steps till empty*** refer to the maximum number of steps required for a configuration to reach 0 waiting vehicles

##### List of configurations:
<div>
  <img src="https://github.com/user-attachments/assets/6a775419-8e5f-4c16-a30b-a9bb156a93b6" width="40%">
  <img src="https://github.com/user-attachments/assets/4df10b28-3f22-41a7-b4cf-46cfc0291bd8" width="40%">
  <img src="https://github.com/user-attachments/assets/f865e21c-8df3-437b-a19c-e01698f39dae" width="40%">
  <img src="https://github.com/user-attachments/assets/e58391aa-7c12-4068-ad07-f84d6d3b60fb" width="40%">
</div>



### The priority formula
The priority *P* for a traffic lights configuration is a heuristic function developed to evaluate the state of the lights configuration.
The basic idea was to:
  - allow a minimum amount of steps to happen with top priority (5)
  - prevent a configuration from being active too long (maximum 20 steps)
  - be in favor of configurations with more vehicles and punish those that have more active steps or required steps till it empties

The formula below was developed empirically:


$$P(A, V, E, on)=\begin{cases}
\infty & \text{if} & on \wedge A < 5 \wedge V > 0\\
-\infty & \text{if} & !on \vee A \geq 20 \vee V == 0\\
\frac{V}{E} & \text{if} & !on\\
\frac{V}{E + 2A - 10} & \text{if} & on
\end{cases}$$

Where *A* refers to the *active steps* of the configuration, *V* to the total number of vehicles in the configuration, *E* is the number of *steps till empty* of the configuration, and *on* is a boolean value indicating if the configuration is active.

This formula is intended to allow at least steps to be possible (to avoid situations where the lights turn to red after only one car leaves and there are more waiting).
Furthermore it prevents one configuration to be active too long. One road can be very congested and the formula must allow other vehicles to pass as well. This is where the maximum number of steps (20) comes to play. Of course if that's the only configuration that contains vehicles the lights won't turn to red.

### The algorithm
The configuraions are set to activate sequentially so that no vehicle waits additional turns and so that the system is deterministic. The only time a configuration can be skipped is when it has *no vehicles waiting for a green light*.

If there are no vehicles in the junction it's set to *on demand* mode meaning the configuration of that vehicle is automatically turned on and the rest off.

```
BEGIN
  C = current_active_configuration
  MAX_PRIO = PRIORITY(C)

  // The next configuration to be activated if the current one's priority isn't the highest
  NEXT_LIVE_CONFIG = NULL

  BEST_CONFIG = C
  for each CONFIG after C {

    // This prevents scenario types explained below the algorithm
    if VEHICLES_WITH_RED_LIGHT(CONFIG) == 0 {
      skip
    } else if NEXT_LIVE_CONFIG == NULL {
      NEXT_LIVE_CONFIG = CONFIG
    }

    if PRIORITY(CONFIG) > MAX_PRIO {
      BEST_CONFIG = CONFIG
      break
    }
  }

  // this means there are no vehicles on the junction. The junction should enter the lights on demand mode
  if NEXT_LIVE_CONFIG == NULL & MAX_PRIO == -INF {
    ON_DEMAND = true 
  }

  if BEST_CONFIG != C {
    DEACTIVATE(C)
    ACTIVATE(BEST_CONFIG)
  }
END
```

The *VEHICLES_WITH_RED_LIGHT* check is intended to prevent situations like the one in the following example:

Suppose there are vehicles coming only from the south on the middle and right lane. Nobody else is in the junction so both middle and right lane lights should be green at all time. It's important to notice that the lights on the right turn are **also part of another configuration**. Therefore as the active steps grow the current configuration's priority decreases and eventually **that other configuration's** priority would be superior! In that case only the right-turning vehicles would have a green light and that would be highly ineffective!

## Understanding the web service
Once started you'll have a pretty straight-forward user interface. You can add vehicles indicating their start and end roads (the ID is created automatically) and make steps as shown in the list of commands earlier. Apart from that you have some extra commands:

![image](https://github.com/user-attachments/assets/8e53442f-49d0-47b5-9958-a52415e62499)

You can **record** the commands you're sending to the server (adding vehicles and making steps) and then **download** them at any time. Please keep in mind that in order to preserve integrity you can only start recording once **the junction is empty**. So be aware before stopping recording. You can also **upload** JSON files containing commands in the [input file format](#Input-file-format). Simply drag them onto the shown area. The **next command** button is activated once you upload a file. As the name implies it executes the next command listed in the file.

The **download report** button downloads a file containing the vehicles that left after each step written in the [output file format](#Output-file-format)
