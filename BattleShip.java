import java.lang.String;
import java.util.ArrayList;
import java.util.Random;
import java.util.Scanner;
public class BattleShip {
/*
x 1 2 3 4 5 6 7 8 9 10
A
B
C
D
E
F
G
H
I
J
*/

//Scanner
static Scanner scanner = new Scanner(System.in);

//Random
static Random rand = new Random();

//Game Boards
static String[][] playerHomeGrid = new String[11][11]; //grid where player has battleships
static String[][] playerAttackGrid = new String[11][11]; //grid where player marks hits and misses

//Positions of ship coordinates
static ArrayList<String> playerOccupiedCoords = new ArrayList<String>();
static ArrayList<String> opponentOccupiedCoords = new ArrayList<String>();

//Player guessing variables
static ArrayList<String> playerHitCoordinates = new ArrayList<String>();
static ArrayList<String> playerMissCoordinates = new ArrayList<String>();

//Opponent guessing variables
static ArrayList<String> opponentHitCoordinates = new ArrayList<String>();
static ArrayList<String> opponentMissCoordinates = new ArrayList<String>();
static String guess = ""; // Initialize guess to an empty string


//Player's Ships
static Ship carrier = new Ship(5, "Carrier");
static Ship battleship = new Ship(4, "Battleship");
static Ship cruiser = new Ship(3, "Cruiser");
static Ship submarine = new Ship(3, "Submarine");
static Ship destroyer = new Ship(2, "Destroyer");
static ArrayList<Ship> listOfShipsPlayer = new ArrayList<Ship>();

//Opponent's Ships
static Ship carrierOpponent = new Ship(5, "Carrier");
static Ship battleshipOpponent = new Ship(4, "Battleship");
static Ship cruiserOpponent = new Ship(3, "Cruiser");
static Ship submarineOpponent = new Ship(3, "Submarine");
static Ship destroyerOpponent = new Ship(2, "Destroyer");
static ArrayList<Ship> listOfShipsOpponent = new ArrayList<Ship>();

//ends game when all ships are sunk
static boolean gameOver = false;


public static void main(String[] args) {
    //clears terminal and adds a new line for readibility
    System.out.print("\033[H\033[2J");
    System.out.flush(); 
    System.out.println();

    //initializes grids and places ships
    createGrids();
    placePlayerShips();
    placeOpponentShips();

    //Main gameplay
    while(!gameOver) {
        playerGuess();
        //pause for 2 seconds between turns
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        //if the player wins, the game is ended before the opponent has a chance to guess
        if(gameOver) {
            break; 
        }
        opponentGuess();
    }
    scanner.close();
}

//Methods:

//input a Coordinate (ex A3) and changes the character at that coordinate
public static String[][] updateGrid(String coordinate, String character, String[][] grid) {
    int row = (int)(coordinate.charAt(0) - 64); //converts ASCII of the letters to an int
    int column = Integer.parseInt(coordinate.substring(1)); //grabs the number from the coordinate
    grid[row][column] = character;
    return grid;
}

//checks if a given coordinate is valid (fits the grid or is in the correct format)
static boolean checkCoordinate(String coordinate, String[][] grid) {
    boolean loop = true;
    boolean value = true; //value to return
    //loops through various exceptions so the game does not end when the player makes a mistake inputting
    while (loop) {
        try{
            int row = (int)(coordinate.charAt(0) - 64); //converts ASCII of the letters to an int
            int column = Integer.parseInt(coordinate.substring(1)); //grabs the number from the coordinate
            if(column > 10) {
                return false;
            }
            @SuppressWarnings("unused")
            boolean test = grid[row][column].equals("test"); //only a test value to see if row an column exists on the grid
        }
        
        catch (Exception e) {
            return false;
        }
        loop = false;
    }
    return value;
}

//prints a specified grid
public static void printGrid(String[][] grid) {
    for(int i = 0; i < 11; i++) {
        System.out.println();
        for(int j = 0; j < 11; j++) {
            System.out.print(grid[i][j]);
        }
    }
    System.out.println();
}

//Initializes the game grids
static void createGrids() {
    for(int i = 0; i < 11; i++) {
        for(int j = 0; j < 11; j++) {
            playerHomeGrid[i][j] = " .";
            playerAttackGrid[i][j] = " .";
        }
    }
    //Changes the spot between rows and columns to x
    playerHomeGrid[0][0] = "x ";
    playerAttackGrid[0][0] = "x ";
    

    for(int i = 1; i < 11; i++) {
        String value = Integer.toString(i);
        playerHomeGrid[0][i] = value + " "; //fills in the numbers 1-10 for the Home Grid
        playerAttackGrid[0][i] = value + " "; //fils in the numbers 1-10 for the Attack Grid
    }

    for(int i = 1; i < 11; i++) {
        String letter = (char)(i+64) + ""; //iterates ASCII values for letters A through J and converts to String
        playerHomeGrid[i][0] = letter; //updates each row to begin with letters A through J
        playerAttackGrid[i][0] = letter;
    }
    
}

//handle's player's guessing logic
static void playerGuess() {
    String input;
    System.out.print("\nOpponents Grid:");
    printGrid(playerAttackGrid);
    System.out.println("Where would you like to attack?");
    input = scanner.nextLine();
    while (checkCoordinate(input, playerAttackGrid) == false) { //asks for input until there is no error
        System.out.print("Invalid input, try again: ");
        input = scanner.nextLine();
    } 
    while(playerMissCoordinates.contains(input) || playerHitCoordinates.contains(input)) {
        System.out.print("You have already guessed this coordinate, try again: ");
        input = scanner.nextLine();
    }
    //logic for the player hitting a ship
    if(opponentOccupiedCoords.contains(input)) {
        System.out.println("HIT!");
        playerHitCoordinates.add(input);
        playerAttackGrid = updateGrid(input, " X", playerAttackGrid); //changes the coordinate the player hit to "X" to represent a ship that has been hit
        opponentOccupiedCoords.remove(input);
        for(int i = 0; i < listOfShipsOpponent.size(); i++) {
            if(listOfShipsOpponent.get(i).getListOfCoords().contains(input)) { //if the input is one of the indexed ship's coordinate
                listOfShipsOpponent.get(i).setHitCount(listOfShipsOpponent.get(i).getHitCount() + 1); //incriment the ship's hit count
                //logic for the ship beingsunk
                if(listOfShipsOpponent.get(i).getHitCount() == listOfShipsOpponent.get(i).getLength()) {
                    System.out.println("You sunk your opponent's " + listOfShipsOpponent.get(i).toString() + "!");
                    for(int k = 0; k < listOfShipsOpponent.get(i).getListOfCoords().size(); k++) {
                        playerAttackGrid = updateGrid(listOfShipsOpponent.get(i).getListOfCoords().get(k), " S", playerAttackGrid);
                    }
                    listOfShipsOpponent.remove(listOfShipsOpponent.get(i));
                    //all ships are sunk
                    if(listOfShipsOpponent.isEmpty()) {
                        System.out.println("You sunk all of your opponents ships, you win!");
                        gameOver = true;
                    }
                }
            }
        }
    }
    //Logic for the player missing
    else {
        System.out.println("Miss!");
        playerAttackGrid = updateGrid(input, " 0", playerAttackGrid);
        playerMissCoordinates.add(input);
    }
}

//logic for the opponent's guesses
static void opponentGuess() {
    if(opponentMissCoordinates.contains(guess) && !guess.isEmpty()) { //if last guess was a miss, and it is not the first guess
        playerHomeGrid = updateGrid(guess, " .", playerHomeGrid); // Resets spot on grid that shows the miss
    }

    do {
        guess = (char)(rand.nextInt(10) + 65) + Integer.toString(rand.nextInt(10) + 1); //guess is a random coordinate
    } while (opponentMissCoordinates.contains(guess) || opponentHitCoordinates.contains(guess)); //repeat until the guess is something that has not been guessed before

    
    //Logic for opponent guessing correctly
    if(playerOccupiedCoords.contains(guess)) { //HIT

        playerHomeGrid = updateGrid(guess, " X", playerHomeGrid); //Show the player where there ship has been hit
        playerOccupiedCoords.remove(guess);
        opponentHitCoordinates.add(guess);

        //Prints player's home grid and shows the user the opponent's guess
        System.out.print("\nYour Grid:");
        printGrid(playerHomeGrid);
        System.out.print("Opponent guesses: " + guess + " | ");
        System.out.println("HIT!");

        for(int i = 0; i < listOfShipsPlayer.size(); i++) {
            if(listOfShipsPlayer.get(i).getListOfCoords().contains(guess)) {
                listOfShipsPlayer.get(i).setHitCount(listOfShipsPlayer.get(i).getHitCount() + 1); //incriments hitCount
                if(listOfShipsPlayer.get(i).getHitCount() == listOfShipsPlayer.get(i).getLength()) { //detects if the player has hit each coordinate
                    System.out.println("Your opponent sunk your " + listOfShipsPlayer.get(i).toString() + "!");
                    for(int k = 0; k < listOfShipsPlayer.get(i).getListOfCoords().size(); k++) {
                        playerHomeGrid = updateGrid(listOfShipsPlayer.get(i).getListOfCoords().get(k), " S", playerHomeGrid); //changes each coordinate of the sunk ship to "S" to represent a sunk ship
                    }
                    listOfShipsPlayer.remove(listOfShipsPlayer.get(i));
                    if(listOfShipsPlayer.isEmpty()) {
                        System.out.println("Your opponent sunk all of your ships, you lose!");
                        gameOver = true;
                    }
                        
                }
            }
        }
    }
    //logic for opponent missing
    else {

        playerHomeGrid = updateGrid(guess, " 0", playerHomeGrid);
        opponentMissCoordinates.add(guess);

        System.out.print("\nYour Grid:");
        printGrid(playerHomeGrid);
        System.out.print("Opponent guesses: " + guess + " | ");
        System.out.println("Miss!");
    }
    
    //Sleep to give the user time to interpret the opponent's guess and see where they were hit
    try {
        Thread.sleep(3000);
    } catch (InterruptedException e) {
        e.printStackTrace();
    }
}



//logic for the player initially placing their ships
static void placePlayerShips() {

    listOfShipsPlayer.add(carrier);
    listOfShipsPlayer.add(battleship);
    listOfShipsPlayer.add(cruiser);
    listOfShipsPlayer.add(submarine);
    listOfShipsPlayer.add(destroyer);

    String input; //coordinate to input
    int row; //row value of input (letter)
    int column; //column value of input (number)
    int shipNumber = 0; //allows the for loop to reset at the ship that an error occured

    ArrayList<String> playerCoordsToAdd = new ArrayList<String>();

    boolean loop = true;
    place_ships:
    while(loop) {
        printGrid(playerHomeGrid); //prints the grid for the player to see where they are placing their ships
        //Sets Starting Coordinates for each of the player's ships
        for(int i = shipNumber; i < listOfShipsPlayer.size(); i++) {
            playerCoordsToAdd.clear();
            System.out.println("Where would you like to place your " + listOfShipsPlayer.get(i).toString() + "(" + listOfShipsPlayer.get(i).getLength() + " spaces long)?");
            input = scanner.nextLine();

            while (checkCoordinate(input, playerHomeGrid) == false) { //asks for input until there is no error
                System.out.print("Invalid input, try again: ");
                input = scanner.nextLine();
            }

            //checks if the coord the player inputted intersects with an existing ship
            if(playerOccupiedCoords.contains(input)) {
                System.out.println("ERROR: intersection with another ship");
                //Sleep to give the user time to notice their error
                try {
                    Thread.sleep(1600);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                shipNumber = i;
                continue place_ships;
            }
            // else statement not needed since the loop will continue if previous statement is true
            listOfShipsPlayer.get(i).setStartingCoord(input);

            //Asks the user for which direction they would like their ship to point to calculate each coordinate of each ship
            row = (int)listOfShipsPlayer.get(i).getStartingCoord().charAt(0); //ASCII value of char
            column = Integer.parseInt(listOfShipsPlayer.get(i).getStartingCoord().substring(1));

            System.out.println("What direction would you like your ship to go? (U, D, L, R)");
            input = scanner.nextLine();
            switch (input.toUpperCase()) {
                case "U": //UP
                    if(row - listOfShipsPlayer.get(i).getLength() + 1 >= 65) { //ASCII value of row minus the length must be greater than or equal to the ASCII value for 'A'
                                                                         //plus one because the length of the ship includes the starting coordinate
                        for(int j = row; j >= (row - listOfShipsPlayer.get(i).getLength() + 1); j--) {
                            String coord = (char)j + Integer.toString(column);
                            if(playerOccupiedCoords.contains(coord)) {
                                System.out.println("ERROR: intersection with another ship");
                                //Sleep to give the user time to notice their error
                                try {
                                    Thread.sleep(1600);
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
                                shipNumber = i;
                                continue place_ships;
                            }
                            //if no intersection occurs
                            playerCoordsToAdd.add(coord);
                        }
                        //adds each element in playerCoordsToAdd after each coordinate has been calculated in case an intersection occurs mid-calculation
                        for(int a = 0; a < playerCoordsToAdd.size(); a++) {
                            listOfShipsPlayer.get(i).setListOfCoords(playerCoordsToAdd.get(a));
                                playerOccupiedCoords.add(playerCoordsToAdd.get(a));
                            }
                    }
                    //out of the bounds of the grid
                    else {
                        System.out.println("ERROR: out of bounds");
                        //Sleep to give the user time to notice their error
                        try {
                            Thread.sleep(1600);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        shipNumber = i;
                        continue place_ships;
                    }
                    break;
                case "D": //DOWN
                    if(row + listOfShipsPlayer.get(i).getLength() - 1 <= 74) { //ASCII value of row + length has to be less than or equal to 'J'. Subtract 1 for same reason as Up logic
                        for(int j = row; j <= (row + listOfShipsPlayer.get(i).getLength() - 1); j++) { //each coord below the previous until the length of the ship is reached
                            String coord = (char)j + Integer.toString(column);
                                if(playerOccupiedCoords.contains(coord)) {
                                    System.out.println("ERROR: intersection with another ship");
                                    //Sleep to give the user time to notice their error
                                    try {
                                        Thread.sleep(1600);
                                    } catch (InterruptedException e) {
                                        e.printStackTrace();
                                    }
                                    shipNumber = i;
                                    continue place_ships;
                                }
                                playerCoordsToAdd.add(coord);
                        }
                        for(int a = 0; a < playerCoordsToAdd.size(); a++) {
                            listOfShipsPlayer.get(i).setListOfCoords(playerCoordsToAdd.get(a));
                                playerOccupiedCoords.add(playerCoordsToAdd.get(a));
                            }
                    }
                    else { //out of the bounds of the grid
                        System.out.println("ERROR: out of bounds");
                        //Sleep to give the user time to notice their error
                        try {
                            Thread.sleep(1600);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        shipNumber = i;
                        continue place_ships;
                    }
                    break;
                case "L": //LEFT
                    if(column - listOfShipsPlayer.get(i).getLength() + 1 >= 1) { //column of input - length (+ 1 for reason stated in UP logic) has to be greater than or equal 10 to fit the grid
                        for(int j = column; j >= column - listOfShipsPlayer.get(i).getLength() +1; j--) {
                            String coord = (char)row + Integer.toString(j);
                            if(playerOccupiedCoords.contains(coord)) {
                                System.out.println("ERROR: intersection with another ship");
                                //Sleep to give the user time to notice their error
                                try {
                                    Thread.sleep(1600);
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
                                shipNumber = i;
                                continue place_ships;
                            }
                            playerCoordsToAdd.add(coord);
                        }
                        for(int a = 0; a < playerCoordsToAdd.size(); a++) {
                            listOfShipsPlayer.get(i).setListOfCoords(playerCoordsToAdd.get(a));
                                playerOccupiedCoords.add(playerCoordsToAdd.get(a));
                            }
                    }
                    else {
                        System.out.println("ERROR: out of bounds");
                        //Sleep to give the user time to notice their error
                        try {
                            Thread.sleep(1600);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        shipNumber = i;
                        continue place_ships;
                    }
                    break;
                case "R": //RIGHT
                    if(column + listOfShipsPlayer.get(i).getLength() - 1 <= 10) { //column of input + the length (- 1 for reason above) has to be less than or equal to 10 in order to fit in the grid
                        for(int j = column; j <= column + listOfShipsPlayer.get(i).getLength() - 1; j++) {
                            String coord = (char)row + Integer.toString(j);
                            if(playerOccupiedCoords.contains(coord)) {
                                System.out.println("ERROR: intersection with another ship");
                                //Sleep to give the user time to notice their error
                                try {
                                    Thread.sleep(1600);
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
                                shipNumber = i;
                                continue place_ships;
                            }
                            playerCoordsToAdd.add(coord);
                        }
                        for(int a = 0; a < playerCoordsToAdd.size(); a++) {
                        listOfShipsPlayer.get(i).setListOfCoords(playerCoordsToAdd.get(a));
                            playerOccupiedCoords.add(playerCoordsToAdd.get(a));
                        }
                    }
                    else { //out of the grid's bounds
                        System.out.println("ERROR: out of bounds");
                        //Sleep to give the user time to notice their error
                        try {
                            Thread.sleep(1600);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        shipNumber = i;
                        continue place_ships;
                    }
                    break;
                default:
                    System.out.println("ERROR: not a valid direction");
                    //Sleep to give the user time to notice their error
                    try {
                        Thread.sleep(1600);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    shipNumber = i;
                    continue place_ships;
            }
            //after the coords for each ship is calculated, it is added to the Home Grid
            for(int k = 0; k < listOfShipsPlayer.get(i).getListOfCoords().size(); k++) {
                playerHomeGrid = updateGrid(listOfShipsPlayer.get(i).getListOfCoords().get(k), " #", playerHomeGrid);
            }
            printGrid(playerHomeGrid);
            
        }
        loop = false; //stops looping the code if succesfully ran
    }
}


//logic for the random placement of the opponent's ships. Code is mostly the same as placePlayerShips() without user selection and errors
static void placeOpponentShips() {
    
    listOfShipsOpponent.add(carrierOpponent);
    listOfShipsOpponent.add(battleshipOpponent);
    listOfShipsOpponent.add(cruiserOpponent);
    listOfShipsOpponent.add(submarineOpponent);
    listOfShipsOpponent.add(destroyerOpponent);
    int row, column;
    int i = 0;

    while (i < 5) {
        int direction = rand.nextInt(4);
        //sets the startingCoord of each ship
        listOfShipsOpponent.get(i).setStartingCoord((char)(rand.nextInt(10) + 65) + Integer.toString(rand.nextInt(10) + 1));
        String startingCoord = listOfShipsOpponent.get(i).getStartingCoord();

        //if the random startingCoord is equal to an already existing coord, the loop restarts which assigns a new random startingCoord
        if (opponentOccupiedCoords.contains(startingCoord)) {
            continue;
        } 
        else {
            row = startingCoord.charAt(0);
            column = Integer.parseInt(startingCoord.substring(1));
            boolean validPlacement = true;

            switch (direction) {
                case 0: // UP
                    if (row - listOfShipsOpponent.get(i).getLength() + 1 >= 65) {
                        for (int j = row; j >= row - listOfShipsOpponent.get(i).getLength() + 1; j--) {
                            String coord = (char)j + Integer.toString(column);
                            //checks if each new coordinate generated from this direction overlaps an existing coord, if so
                            if (opponentOccupiedCoords.contains(coord)) {
                                validPlacement = false;
                                break; //breaks out of switch, which does not let i increment, which makes the ship that failed get new coordinates
                            }
                        }
                        if (validPlacement) {
                            for (int j = row; j >= row - listOfShipsOpponent.get(i).getLength() + 1; j--) {
                                String coord = (char) j + Integer.toString(column);
                                listOfShipsOpponent.get(i).setListOfCoords(coord);
                                opponentOccupiedCoords.add(coord);
                            }
                            i++;
                        }
                    }
                    break;

                case 1: // DOWN
                    if (row + listOfShipsOpponent.get(i).getLength() - 1 <= 74) {
                        for (int j = row; j <= row + listOfShipsOpponent.get(i).getLength() - 1; j++) {
                            String coord = (char) j + Integer.toString(column);
                            if (opponentOccupiedCoords.contains(coord)) {
                                validPlacement = false;
                                break;
                            }
                        }
                        if (validPlacement) {
                            for (int j = row; j <= row + listOfShipsOpponent.get(i).getLength() - 1; j++) {
                                String coord = (char) j + Integer.toString(column);
                                listOfShipsOpponent.get(i).setListOfCoords(coord);
                                opponentOccupiedCoords.add(coord);
                            }
                            i++;
                        }
                    }
                    break;

                case 2: // LEFT
                    if (column - listOfShipsOpponent.get(i).getLength() + 1 >= 1) {
                        for (int j = column; j >= column - listOfShipsOpponent.get(i).getLength() + 1; j--) {
                            String coord = (char) row + Integer.toString(j);
                            if (opponentOccupiedCoords.contains(coord)) {
                                validPlacement = false;
                                break;
                            }
                        }
                        if (validPlacement) {
                            for (int j = column; j >= column - listOfShipsOpponent.get(i).getLength() + 1; j--) {
                                String coord = (char) row + Integer.toString(j);
                                listOfShipsOpponent.get(i).setListOfCoords(coord);
                                opponentOccupiedCoords.add(coord);
                            }
                            i++;
                        }
                    }
                    break;

                case 3: // RIGHT
                    if (column + listOfShipsOpponent.get(i).getLength() - 1 <= 10) {
                        for (int j = column; j <= column + listOfShipsOpponent.get(i).getLength() - 1; j++) {
                            String coord = (char) row + Integer.toString(j);
                            if (opponentOccupiedCoords.contains(coord)) {
                                validPlacement = false;
                                break;
                            }
                        }
                        if (validPlacement) {
                            for (int j = column; j <= column + listOfShipsOpponent.get(i).getLength() - 1; j++) {
                                String coord = (char) row + Integer.toString(j);
                                listOfShipsOpponent.get(i).setListOfCoords(coord);
                                opponentOccupiedCoords.add(coord);
                            }
                            i++;
                        }
                    }
                    break;

                default:
                    break;
            }
        }
    }
}
}