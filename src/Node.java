public class Node {
    public double x; // x and y coordinates
    public double y;
    Node(){}

    /**
     * @param x x coordinate
     * @param y y coordinate
     */

    Node(double x, double y){
        this.x = x;
        this.y = y;
    }


    public String toString(){
        return ""+x+","+y;
    }
}
