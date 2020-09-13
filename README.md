# Evolution gaming home task

## Texas holdem hand evaluator compilation and running instructions

## Prerequisites:

1. JDK
2. Maven

## Compilation:

1. Clone the repository
2. To compile an executable .jar file, from the top directory execute
   <code>mvn package</code>.
   This generates the executable ./target/texas-holdem-1.0-SNAPSHOT.jar

## Running:

1. If necessary, add execution permission to the generated .jar file
2. Run the program by giving it input from stdin: <code>cat \<path_to_input_file\> | java -jar target/texas-holdem-1.0-SNAPSHOT.jar</code>
