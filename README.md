# FS_numerical-simulation

Requirements:
- Java 8 (preferred) or later
- JUnit (version 4 or later)

Optionnal:
- MemCached (But advised)
- Maven (if not already in the Eclipse package and that's the case for the latest versions.)

Simple Installation:
- Install Eclipse IDE for Java (e.g. Eclipse Oxygen)
- In Eclipse > Import > New Maven Project > Deselect all
- Import "simulator" and the top head of the other projects (hipparchus-1.2, orekit-9.1, slf4j-1.8.0-beta0 etc.)
- Right click on simulator project > Maven > Update Project... > Select all > Force Update of Snapchots/Releases > OK

The simulator project is now set up. You can launch the main method and some tests.

Simple Test Execution:
- In Eclipse, select the test to run in src/test/java/msp/simulator
- Run As > JUnit Test

Test Automation Execution:
- In Eclipse, Run > Run Configuration > JUnit.
- New Configuration > Run all tests in the select project > Ru;
