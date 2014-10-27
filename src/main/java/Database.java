import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.util.*;
import java.util.List;

import au.com.bytecode.opencsv.CSVReader;

import org.neo4j.cypher.javacompat.ExecutionEngine;
import org.neo4j.cypher.javacompat.ExecutionResult;
import org.neo4j.graphdb.*;
import org.neo4j.graphdb.Label;
import org.neo4j.graphdb.factory.GraphDatabaseFactory;
import org.neo4j.graphdb.factory.GraphDatabaseSettings;
import org.neo4j.graphdb.index.Index;
import org.neo4j.graphdb.index.IndexHits;
import org.neo4j.graphdb.index.IndexManager;
import org.neo4j.helpers.collection.IteratorUtil;
import org.w3c.dom.NodeList;
import au.com.bytecode.opencsv.CSVWriter;
import sun.security.ssl.Debug;

import javax.management.relation.RoleInfo;
import javax.swing.*;

/**
 * Created by rik on 9/16/14.
 */
public class Database extends JApplet implements Relations {

    private static final String DB_PATH = "//home//rik//programs//development6";

    ArrayList<Node> nodeList = new ArrayList<>();
    ArrayList<Node> nodeClassList = new ArrayList<>();
    ArrayList<Person> personList = new ArrayList<>();
    ArrayList<Class> classList = new ArrayList<>();
    GraphDatabaseService graphDb;
    ButtonGroup buttonGroup = new ButtonGroup();
    JRadioButton radioButton1 = new JRadioButton("Button 1");
    JRadioButton radioButton2 = new JRadioButton("Button 2");


    Label person = DynamicLabel.label("Person");

    public static void main(String[] args) {
        Database db = new Database();


        db.createDb(); // creeren van database
        db.removeAllData(); // DELETE


        db.fillDb(); //CREATE
        db.loadPersonCSV("'file:/home/rik/Documents/studenten.csv'"); // import CSV
        db.loadProjectgroepCSV("'file:/home/rik/Documents/projectgroups.csv'");
        db.loadRelatiesCSV("'file:/home/rik/Documents/relaties.csv'");
        db.updateNode("nieuwe voornaam"); // UPDATE

        // READ

         /* return all nodes with relations*/
        System.out.println(db.query("MATCH (n)-[r]->(m)"
                + " RETURN n,r,m;"));

         /* return all personen gesoorteerd op voornaam*/
        System.out.println(db.query("MATCH (n:Person)"
                + " RETURN n.voornaam, n.achternaam"
                + " ORDER BY n.voornaam"));

         /* return all personen gesoorteerd op voornaam*/
        System.out.println(db.query("MATCH (n:projectGroup)"
                + " RETURN n.id, n.naam"
                + " ORDER BY n.id"));

        /* return person by firstname */
        System.out.println(db.query("MATCH (n)"
                + "Where n.voornaam = 'Rik'"
                + " RETURN n"));



         /* return person by lastname */
        System.out.println(db.query("MATCH (n)"
                + "Where n.achternaam = 'Winter'"
                + " RETURN n"));


        // na urennnnnnnnn eindelijk csv output :):)
        generateCsvFile("//home//rik//programs//development3//test.csv", db.query("MATCH (n:Person)"
                + " RETURN n.voornaam, n.achternaam"
                + " ORDER BY n.voornaam"));


        //complexe query

        System.out.println(db.query(
        "MATCH (n:projectGroup)-[IN_class]->(b:Class)" +
        "where b.name = 'INF2B'" +
        "match (p:Person)-[IS_PART_OF]->(n)" +
        "where not n.naam = 'groep2'" +
        "return count(p.id)"));

        // update multiple
        System.out.println(db.query(
                "MATCH (p:Person)-[IS_PART_OF]->(n:projectGroup)"+
                "WHERE n.naam='groep2'" +
                "SET p.geslacht = 'man'" +
                "RETURN p"));

        db.registerShutdownHook();





    }


    Index<Node> persons;
    void createDb()
    {
        this.graphDb = new GraphDatabaseFactory()
                .newEmbeddedDatabaseBuilder(DB_PATH)
                .setConfig(GraphDatabaseSettings.nodestore_mapped_memory_size, "10M")
                .setConfig(GraphDatabaseSettings.string_block_size, "60")
                .setConfig(GraphDatabaseSettings.array_block_size, "300")
                .newGraphDatabase();

        try (Transaction tx = graphDb.beginTx()) {

             IndexManager index = graphDb.index();
             persons = index.forNodes( "persons" );
             tx.success();
        }

    }

    void fillDb()
    {
        Class INF2B,INF2A;
        Person Rik,Sander,Robin,Davey,Jeroen,Johan,Thomas,Martin,Bas,Roel,Erik,Wesley,Marcel,Jerry,Peter,Dean,Nigel,Armindo,Wim,Amit,Jorik,Test;


        createClassNode(INF2B = new Class("INF2B"));


        createPersonNode(Test = new Person("Test","Persoon"));

        for (int i = 0; i < personList.size(); i++)
        {
            createClassRelationship(personList.get(i),INF2B, RelTypes.SITS_IN_CLASS);
        }

        /*
        createPersonNode( Jeroen = new Person("Jeroen", "Boer"));
        createPersonNode( Johan = new Person("Johan", "Boers"));
        createPersonNode( Thomas = new Person("Thomas", "Bolderheij"));
        createPersonNode( Martin = new Person("Martin", "Bolderheij"));
        createPersonNode( Bas= new Person("Bas", "Buijs"));
        createPersonNode( Roel = new Person("Roel", "Engelsman"));
        createPersonNode( Erik = new Person("Erik", "Euser"));
        createPersonNode( Wesley = new Person("Wesley", "Heetebrij"));
        createPersonNode( Marcel = new Person("Marcel", "Hollink"));
        createPersonNode( Jerry = new Person("Jerry", "Hu"));
        createPersonNode( Peter = new Person("Peter", "Kleinjan"));
        createPersonNode( Dean = new Person("Dean", "Koster"));
        createPersonNode( Nigel = new Person("Nigel", "Maduro"));
        createPersonNode( Thomas = new Person("Thomas", "Maurer"));
        createPersonNode( Amit = new Person("Amit", "Sanchit"));
        createPersonNode( Jorik = new Person("Jorik", "Schouten"));
        createPersonNode( Rik = new Person("Rik", "van der Werf"));
        createPersonNode( Sander = new Person("Sander", "de Winter"));
        createPersonNode( Robin = new Person("Robin", "Siep"));
        createPersonNode( Davey = new Person("Davey", "de Witter")); */









        //
    }

    void updateNode(String firstname) {
        try (Transaction tx = graphDb.beginTx()) {

            IndexHits<Node> test = persons.get("voornaam", "Test");
            test.getSingle().setProperty("voornaam", firstname);



            tx.success();
        }
    }

    private static void generateCsvFile(String sFileName, String query)
    {
        try
        {
            FileWriter writer = new FileWriter(sFileName);

            writer.append(query);
            writer.flush();
            writer.close();
        }
        catch(IOException e)
        {
            e.printStackTrace();
        }
    }

    void createClassRelationship(Person firstNode, Class secondNode, RelTypes relType)
    {
        int person1Nummer = 0;
        int class2Nummer = 0;

        for (int i = 0; i < personList.size(); i++)
        {
            if (personList.get(i).firstname == firstNode.firstname && personList.get(i).lastname == firstNode.lastname)
            {
                person1Nummer = i;

            }

        }

        for (int j = 0; j < classList.size(); j++)
        {


            if (classList.get(j).name.equals(secondNode.name) )
            {
                class2Nummer = j;

            }

        }

        try (Transaction tx = graphDb.beginTx())
        {

            nodeList.get(person1Nummer).createRelationshipTo(nodeClassList.get(class2Nummer), relType);



            tx.success();
        }
    }

    void createPersonRelationship(Person firstNode, Person secondNode, RelTypes relType, boolean isdouble)
    {
        int person1Nummer = 0;
        int person2Nummer = 0;

        for (int i = 0; i < personList.size(); i++)
        {
            if (personList.get(i).firstname == firstNode.firstname && personList.get(i).lastname == firstNode.lastname)
            {
                person1Nummer = i;

            }

        }

        for (int j = 0; j < personList.size(); j++)
        {


            if (personList.get(j).firstname.equals(secondNode.firstname) )
            {
                person2Nummer = j;

            }

        }


        try (Transaction tx = graphDb.beginTx())
        {

            nodeList.get(person1Nummer).createRelationshipTo(nodeList.get(person2Nummer), relType);
            if (isdouble)
                nodeList.get(person2Nummer).createRelationshipTo(nodeList.get(person1Nummer), relType);

            tx.success();
        }
    }



    void removeAllData()
    {
        ExecutionEngine engine = new ExecutionEngine(graphDb);
        System.out.println("Deleting all data...");
        engine.execute("MATCH (n)\n" +
                "OPTIONAL MATCH (n)-[r]-()\n" +
                "DELETE n,r");
    }


    public void updateMultiple() {

        String query = "MATCH (p:Person)-[IS_PART_OF]->(n:projectGroup)"+
        "WHERE n.naam='groep2'" +
        "SET p.geslacht = 'man'" +
        "RETURN p";

        ExecutionEngine engine = new ExecutionEngine(graphDb);
        ExecutionResult result2 =  engine.execute(query);
        String k = result2.dumpToString();
        System.out.println(k);
    }

    public void loadPersonCSV(String csvName)
    {



        String query = "LOAD CSV WITH HEADERS FROM " +csvName + " AS Line ";


        query += " CREATE (person:Person{id: toInt(Line.id), voornaam: Line.voornaam, achternaam: Line.achternaam})";




        ExecutionEngine engine = new ExecutionEngine(graphDb);
        ExecutionResult result2 =  engine.execute(query);
        String k = result2.dumpToString();
        System.out.println(k);
    }

    public void loadProjectgroepCSV(String name)
    {



        String query = "LOAD CSV WITH HEADERS FROM "+ name +  " AS Line "
        + " Match (n)"
                + " Where n.name = 'INF2B'";

        query += " CREATE (projectGroup:projectGroup{ id: toInt(Line.id), naam: Line.naam})"

        + " CREATE (projectGroup)-[:IN_CLASS]->(n)";


        ExecutionEngine engine = new ExecutionEngine(graphDb);
        ExecutionResult result2 =  engine.execute(query);
        String k = result2.dumpToString();
        System.out.println(k);
    }

    public void loadRelatiesCSV(String name) {

        String query = "USING PERIODIC COMMIT "
        +
        "LOAD CSV WITH HEADERS FROM "+ name + " AS csvLine "
        + "MATCH (person:Person { id: toInt(csvLine.personid)}),(projectGroup:projectGroup { id: toInt(csvLine.projectid)}) "
        + "CREATE (person)-[:IS_PART_OF]->(projectGroup)";


        ExecutionEngine engine = new ExecutionEngine(graphDb);
        ExecutionResult result2 =  engine.execute(query);
        String k = result2.dumpToString();
        System.out.println(k);

    }

    public void createPersonNode(Person p)
    {
        personList.add(p);
        try (Transaction tx = graphDb.beginTx()) {
            Node node  = graphDb.createNode();
            System.out.println("creating node...");
            for(int i = 0; i < p.propertyNames.size(); ++i)
            {
                node.setProperty(p.propertyNames.get(i), p.propertyValues.get(i));
                persons.add(node,p.propertyNames.get(i),  p.propertyValues.get(i));
            }

            nodeList.add(node);
            node.addLabel(person);
            tx.success();
        }
    }

    public void createClassNode(Class c)
    {
        classList.add(c);
        try (Transaction tx = graphDb.beginTx()) {
            Node node  = graphDb.createNode();
            System.out.println("creating node...");
            for(int i = 0; i < c.propertyNames.size(); ++i)
            {
                node.setProperty(c.propertyNames.get(i), c.propertyValues.get(i));

            }
            Label label = new Label() {
                @Override
                public String name() {
                    return "Class";
                }
            };

            node.addLabel(label);


            nodeClassList.add(node);

            tx.success();
        }
    }





    public void registerShutdownHook()
    {

        Runtime.getRuntime().addShutdownHook(new Thread() {
            @Override
            public void run() {
                graphDb.shutdown();
            }
        });

        System.out.println("graphDB shut down.");
    }






    public String query(String question)
    {

        ExecutionEngine engine = new ExecutionEngine(graphDb);
        ExecutionResult result = engine.execute(question);
        List<String> columns = result.columns();

        String dump = result.dumpToString();





        try {
            File file = new File("example.json");
            BufferedWriter output = new BufferedWriter(new FileWriter(file));
            output.write(dump);
            output.close();
        } catch ( IOException e ) {
            e.printStackTrace();
            System.out.println("faal");
        }




        return dump;

    }

}