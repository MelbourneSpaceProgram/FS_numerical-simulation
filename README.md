# MSP Numerical Simulation Validation Bench

## Requirements:
- Java 8 (preferred) or later
- Apache Maven (version 3.5.3 or later) 
- JUnit (version 4 or later)

Optional, but advised:
- MemCached
- VTS - a space 3D data analysis and visualization software. (http://timeloop.fr/vts/)

## Simple Installation:
- Install Eclipse IDE for Java (e.g. Eclipse Oxygen)
- In Eclipse > Import > New Maven Project > Deselect all
- Import "simulator" and the top head of the other projects (hipparchus-1.2, orekit-9.1, slf4j-1.8.0-beta0 etc.)
- Right click on simulator project > Maven > Update Project... > Select all > Force Update of Snapchots/Releases > OK

The simulator project is now set up. You can launch the main method and some tests.
## Command Line Execution of Simulation 
### Linux 
- cd into /simulator
- $mvn compile
- $mvn exec:java -D"exec.mainClass"="msp.simulator.Main"
## Simple Test Execution:
- In Eclipse, select the test to run in src/test/java/msp/simulator
- Run As > JUnit Test

### Test Automation Execution:
- In Eclipse, Run > Run Configuration > JUnit.
- New Configuration > Run all tests in the select project > Ru;

## Setting up a VTS project
VTS is the 3D visualization tool of the numerical simulation facility. It can run offline or in real-time.
- Create a new VTS project
- In the Satellite section, select one of the default 3D model or add your own .3DS.
- Set the size of the satellite between 1e2 and 1e6 depending on the desired view.
- In Satellite Orientation, set the attitude and orbit ephemeris source:
 - In case of offline mode, copy the AEM and OEM file from the simulator resources/ephemeris folder to the data folder of the project.
 - In case of real-time mode, set the source to "Stream" with the ID "pos" and "att" respectively for position and attitude.
- In the Application section, right click and add the Celestia view (the 3D engine).

### Important notice on real-time mode:
- Launch the VTS project before launching the simulation.
- Expand the "broker" and select a 3D camera of your choice. (e.g. Satellite Inertial Camera) otherwise the view will remain black by default.
