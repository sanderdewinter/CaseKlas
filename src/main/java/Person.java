import javax.xml.crypto.Data;
import java.util.ArrayList;

/**
 * Created by rik on 9/16/14.
 */
public class Person  {

    String firstname;
    String lastname;

    public ArrayList<String> propertyNames = new ArrayList<>();
    public ArrayList<String> propertyValues = new ArrayList<>();

    Database db = new Database();


    public Person(String firstname, String lastname)
    {
        this.firstname = firstname;
        this.lastname = lastname;
        this.propertyNames.add("firstname");
        this.propertyNames.add("Lastname");
        this.propertyValues.add(firstname);
        this.propertyValues.add(lastname);



    }
}
