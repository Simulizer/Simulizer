package simulizer.cpu.visualisation.components;


public class CustomLine {
    public enum Direction{
        UP,
        DOWN,
        LEFT,
        RIGHT;
    }

    double distance;
    Direction direction;

    public CustomLine(int distance, Direction direction){
        this.distance = distance;
        this.direction = direction;
    }

    public Direction getDirection(){
        return direction;
    }

    public double getDistance(){
        return distance;
    }
}



