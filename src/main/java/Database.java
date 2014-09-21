import java.util.*;

import org.neo4j.cypher.javacompat.ExecutionEngine;
import org.neo4j.cypher.javacompat.ExecutionResult;
import org.neo4j.graphdb.*;
import org.neo4j.graphdb.factory.GraphDatabaseFactory;
import org.neo4j.graphdb.factory.GraphDatabaseSettings;
import org.w3c.dom.NodeList;

/**
 * Created by rik on 9/16/14.
 */
public class Database implements Relations {

    private static final String DB_PATH = "//home//rik//programs//development";

    ArrayList<Node> nodeList = new ArrayList<>();
    ArrayList<Person> personList = new ArrayList<>();

    GraphDatabaseService graphDb;


    public static void main(String[] args) {
        Database db = new Database();
        db.createDb();
        db.removeAllData();
        db.fillDb();
       // db.loadPersonCSV("//home/rik/programs/development/studenten.csv");

        System.out.println(db.query("MATCH (n)-[r]->(m)"
                + " RETURN n,r,m;"));

        db.registerShutdownHook();
    }

    void createDb() {
        this.graphDb = new GraphDatabaseFactory()
                .newEmbeddedDatabaseBuilder(DB_PATH)
                .setConfig(GraphDatabaseSettings.nodestore_mapped_memory_size, "10M")
                .setConfig(GraphDatabaseSettings.string_block_size, "60")
                .setConfig(GraphDatabaseSettings.array_block_size, "300")
                .newGraphDatabase();
    }

    void fillDb()
    {
        Person Rik,Sander;
        createPersonNode( Rik = new Person("Rik", "van der Werf"));
        createPersonNode( Sander = new Person("Sander", "de Winter"));
        createRelationship(Rik,Sander,RelTypes.IS_FRIENDS_WITH);

    }

    void createRelationship(Person firstNode, Person secondNode, RelTypes relType)
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




    public void loadPersonCSV(String csvName) {
        // neo4j 10.6 load CCSV
        String query = "LOAD CSV WITH HEADERS FROM '" + csvName + "' AS Line ";
        query += " CREATE (:Person{voornaam: Line.voornaam, achternaam: Line.achternaam})";
        ExecutionEngine engine = new ExecutionEngine(graphDb);
        engine.execute(query);
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

            }
            nodeList.add(node);

            tx.success();
        }
    }





    public void registerShutdownHook() {

        Runtime.getRuntime().addShutdownHook(new Thread() {
            @Override
            public void run() {
                graphDb.shutdown();
            }
        });

        System.out.println("graphDB shut down.");
    }






    public String query(String question) {

        ExecutionEngine engine = new ExecutionEngine(graphDb);
        ExecutionResult result = engine.execute(question);

        String dump=result.dumpToString();
        

        return dump;

    }

}
