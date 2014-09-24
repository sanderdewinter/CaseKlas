import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.util.*;
import java.util.List;

import com.mxgraph.layout.mxCircleLayout;
import com.mxgraph.swing.mxGraphComponent;
import org.jgraph.graph.DefaultEdge;
import org.jgrapht.ListenableGraph;
import org.jgrapht.ext.JGraphXAdapter;
import org.jgrapht.graph.ListenableDirectedGraph;
import org.neo4j.cypher.javacompat.ExecutionEngine;
import org.neo4j.cypher.javacompat.ExecutionResult;
import org.neo4j.graphdb.*;
import org.neo4j.graphdb.Label;
import org.neo4j.graphdb.factory.GraphDatabaseFactory;
import org.neo4j.graphdb.factory.GraphDatabaseSettings;
import org.neo4j.helpers.collection.IteratorUtil;
import org.w3c.dom.NodeList;

import javax.management.relation.RoleInfo;
import javax.swing.*;

/**
 * Created by rik on 9/16/14.
 */
public class Database extends JApplet implements Relations {

    private static final String DB_PATH = "//home//rik//programs//development";

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
        db.init();
        db.createDb();
        db.removeAllData();
        db.fillDb();
       // db.loadPersonCSV("//home/rik/programs/development/studenten.csv");

        System.out.println(db.query("MATCH (n)-[r]->(m)"
                + " RETURN n,r,m;"));

        db.registerShutdownHook();


        //visualisation

        db.init();
        JFrame frame = new JFrame();
        frame.getContentPane().add(db);
        frame.setTitle("JGraphT Adapter to JGraph Demo");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        frame.pack();
        frame.setVisible(true);


    }




    private static final long serialVersionUID = 2202072534703043194L;
    private static final Dimension DEFAULT_SIZE = new Dimension(530, 320);


    ListenableGraph<String, DefaultEdge> g =
            new ListenableDirectedGraph<String, DefaultEdge>(
                    DefaultEdge.class);

    private JGraphXAdapter<String, DefaultEdge> jgxAdapter;


    public void init()
    {
        // create a JGraphT graph


        // create a visualization using JGraph, via an adapter
        jgxAdapter = new JGraphXAdapter<String, DefaultEdge>(g);

        getContentPane().add(new mxGraphComponent(jgxAdapter));
        resize(DEFAULT_SIZE);

        String v1 = "";








        // add some sample data (graph manipulated via JGraphX)




        // positioning via jgraphx layouts
        mxCircleLayout layout = new mxCircleLayout(jgxAdapter);
        layout.execute(jgxAdapter.getDefaultParent());

        // that's all there is to it!...
    }


    void createDb()
    {
        this.graphDb = new GraphDatabaseFactory()
                .newEmbeddedDatabaseBuilder(DB_PATH)
                .setConfig(GraphDatabaseSettings.nodestore_mapped_memory_size, "10M")
                .setConfig(GraphDatabaseSettings.string_block_size, "60")
                .setConfig(GraphDatabaseSettings.array_block_size, "300")
                .newGraphDatabase();
    }

    void fillDb()
    {
        Class INF1B;
        Person Rik,Sander,Robin,Davey;

        createClassNode(INF1B = new Class("INF1B"));
        createPersonNode( Rik = new Person("Rik", "van der Werf"));
        createPersonNode( Sander = new Person("Sander", "de Winter"));
        createPersonNode( Robin = new Person("Robin", "Siep"));
        createPersonNode( Davey = new Person("Davey", "de Witter"));


        createPersonRelationship(Rik, Sander, RelTypes.IS_FRIENDS_WITH, true);
        createPersonRelationship(Rik, Robin, RelTypes.IS_FRIENDS_WITH, true);
        createPersonRelationship(Rik, Davey, RelTypes.IS_FRIENDS_WITH, true);
        createPersonRelationship(Sander, Robin, RelTypes.IS_FRIENDS_WITH, true);
        createPersonRelationship(Sander, Davey, RelTypes.IS_FRIENDS_WITH, true);
        createPersonRelationship(Davey, Robin, RelTypes.IS_FRIENDS_WITH, true);


        createClassRelationship(Rik,INF1B, RelTypes.SITS_IN_CLASS);
        createClassRelationship(Sander,INF1B, RelTypes.SITS_IN_CLASS);
        createClassRelationship(Robin,INF1B, RelTypes.SITS_IN_CLASS);
        createClassRelationship(Davey,INF1B, RelTypes.SITS_IN_CLASS);
        //
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
        g.addEdge(personList.get(person1Nummer).firstname, classList.get(class2Nummer).name);
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

        g.addEdge(personList.get(person1Nummer).firstname, personList.get(person2Nummer).firstname);
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




    public void loadPersonCSV(String csvName)
    {
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
            g.addVertex(p.firstname);
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
            g.addVertex(c.name);

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
