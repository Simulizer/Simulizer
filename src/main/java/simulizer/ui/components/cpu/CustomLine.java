package simulizer.ui.components.cpu;

/**
 * Represents a direction and magnitude, used when creating custom wires
 */
public class CustomLine {
    public enum Direction{
        UP,
        DOWN,
        LEFT,
        RIGHT;
    }

    double distance;
    Direction direction;

    /**
     * Sets up a new custom line
     * @param distance The distance to travel in
     * @param direction The direction to go in - up, down, left or right
     */
    public CustomLine(double distance, Direction direction){
        this.distance = distance;
        this.direction = direction;
    }

    /**
     * Gets the direction
     * @return The direction - up, down, left or right
     */
    public Direction getDirection(){
        return direction;
    }

    /**
     * Gets the distance
     * @return The distance
     */
    public double getDistance(){
        return distance;
    }
}



