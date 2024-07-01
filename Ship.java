import java.util.ArrayList;

public class Ship {

    private String name; //name of each ship object
    private int length; //length of each ship
    private String startingCoord; //starting coordinate, helps the player place their ships
    private ArrayList<String> listOfCoords = new ArrayList<String>(); //list of coordinates the ship object occupies
    private int hitCount; //amount of times a ship has been hit, if it equals the length of the ship, it will be sunk

    //Constructor
    public Ship(int len, String n) {
        length = len;
        name = n;
    }

    //Methods
    public int getLength() {
        return length;
    }

    public String getStartingCoord() {
        return startingCoord;
    }

    public ArrayList<String> getListOfCoords() {
        return listOfCoords;
    }

    public void setStartingCoord(String coord) {
        startingCoord = coord;
    }

    public void setListOfCoords(String coord) {
        listOfCoords.add(coord);
    }

    public int getHitCount() {
        return hitCount;
    }
    
    public void setHitCount(int count) {
        hitCount = count;
    }
    

    public String toString() {
        return name;
    }
}
