import java.util.*;

public class Classifier {

    String type;
    double[] vector;

    public Classifier(double[] vector, String type) {
        this.vector = vector;
        this.type = type;
    }

    public Classifier(double[] vector) {
        this.vector = vector;
        this.type = "unclassified";
    }

    public String toString() {
        return "Type: " + type + " " + "Vector: " + Arrays.toString(vector);
    }


}
