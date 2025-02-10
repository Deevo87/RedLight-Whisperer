##  **Red Light Whisperer**

A system that intelligently "whispers" to the lights when to change.

<h2>Prerequisites</h2>

* Java 21 or later
* Maven

<h2>Installation</h2>

Clone the repository on your local machine:

```
git clone https://github.com/Deevo87/RedLight-Whisperer
```
Go to the project directory:
```
cd RedLight_Whisperer
```
Build project:
```
mvn clean install
```
And run it:
```
mvn javafx:run -Djavafx.args="<command_file>.json <output_file>.json"
```
The application will be launched.

The traffic light control system in **Intersection** is based on analyzing traffic flow across different lane groups. The algorithm operates as follows:

1. **Initialization:**  
   `TrafficLane` objects representing individual lanes are created and grouped into `LaneGroup` categories, such as straight lanes and left turns. Each lane group receives an initial vehicle count (`trafficGroupCnts`).

2. **Command Processing:**
    - If the command is **ADD_VEHICLE**, a vehicle is added to the appropriate lane.
    - If the command is **STEP**, the algorithm decides whether a traffic light change is necessary.

3. **Traffic Light Priority Calculation:**
    - The number of waiting vehicles is counted for each lane group.
    - If a lane group already has a green light and still has waiting vehicles, it receives a **priority bonus** (`RED_LIGHT_CHANGE_BUFFER`).
    - The lane group with the highest vehicle count gets priority.
    - If the same group has had a green light for too long (`TRAFFIC_USAGE_LIMIT`), a forced light change is triggered.

4. **Traffic Light Change:**
    - If the new priority group differs from the previous one, both groups (old and new) undergo a light change.
    - If the same group remains the highest priority, its usage counter (`trafficUsage`) increases.

5. **Simulation Delay:**  
   After each light change, a delay (`Thread.sleep(1000)`) simulates the real-world passing of time.

6. **Saving Results:**  
   At the end of the simulation, the status of each step is saved to a JSON file.  
