# Evolution gaming home task

## Texas hold'em hand evaluator

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

## Notes:

The program will output error messages for incorrectly specified cards (invalid rank or suit values).

The program assumes that cards on an input line are from a single deck, but DOES NOT check for this, so duplicates are allowed, which would not be the case in a real poker game. This check would be trivial to implement, but I have not done this.

The program DOES NOT support the '--omaha' command line parameter to compare Omaha hold'em hands. I did, however, write this implementation with the option to extend it to do that in mind, and I believe my design of the HandEvaluator and Hand classes is such that there would not be needed modifications to these to add such support. I would rather treat the combinations of 2 out of 4 hand cards and 3 out of 5 board cards as separate 5 card 'hands', finding the strongest hand among these and use that to represent the strength of a given Omaha hand.
