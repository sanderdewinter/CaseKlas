import java.util.ArrayList;

/**
 * Created by rik on 9/16/14.
 */
public class Class {

    String name;


    public ArrayList<String> propertyNames = new ArrayList<>();
    public ArrayList<String> propertyValues = new ArrayList<>();

    public Class(String name)
    {
        this.name = name;

        this.propertyNames.add("name");
        this.propertyValues.add(name);
    }
}
