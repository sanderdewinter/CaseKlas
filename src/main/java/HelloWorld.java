import java.util.GregorianCalendar;
import java.util.List;

import org.neo4j.cypher.javacompat.ExecutionEngine;
import org.neo4j.cypher.javacompat.ExecutionResult;
import org.neo4j.graphdb.*;
import org.neo4j.graphdb.factory.GraphDatabaseFactory;


/**
 * Created by rik on 9/16/14.
 */
public class HelloWorld {

    

        private static final String DB_PATH = "//home//rik//programs//dev-case";

        private static enum RelTypes implements RelationshipType
        {
            KNOWS
        }


        GraphDatabaseService graphDb;
        Node firstNode;
        Node secondNode;
        Relationship relationship;

        public static void main(String[] args) {

            HelloWorld hello = new HelloWorld();

            hello.createDb();

            System.out.println(hello.query());
            hello.registerShutdownHook();
        }

    void removeAllData()
    {
        ExecutionEngine engine = new ExecutionEngine(graphDb);
        System.out.println("Deleting all data...");
        engine.execute("MATCH (n)\n" +
                "OPTIONAL MATCH (n)-[r]-()\n" +
                "DELETE n,r");
    }

        void createDb() {
            graphDb = new GraphDatabaseFactory().newEmbeddedDatabase(DB_PATH);
            registerShutdownHook(graphDb);

            try (Transaction tx = graphDb.beginTx()) {
                // Database operations go here

                firstNode = graphDb.createNode();
                firstNode.setProperty("message", "Hello, ");
                secondNode = graphDb.createNode();
                secondNode.setProperty("message", "World!");

                relationship = firstNode.createRelationshipTo(secondNode, RelTypes.KNOWS);
                relationship.setProperty("message", "brave Neo4j ");

                System.out.print(firstNode.getProperty("message"));
                System.out.print(relationship.getProperty("message"));
                System.out.print(secondNode.getProperty("message"));
                tx.success();
            }
        }



    public String query() {
        // blz 18
        String vraag = "";

        vraag = "MATCH (n) "
                + " RETURN n";



        System.out.println("method query\n"+vraag);

        ExecutionEngine engine = new ExecutionEngine(graphDb);
        ExecutionResult result = engine.execute(vraag);

        String dump=result.dumpToString();
        List<String> lijst=result.columns();
        for(String kol: lijst){
            System.out.println("-kol-"+kol);
        }
        return dump;

    }



    public void registerShutdownHook() {
        // graphDb.shutdown();
        //
        Runtime.getRuntime().addShutdownHook(new Thread() {
            @Override
            public void run() {
                graphDb.shutdown();
            }
        });

        System.out.println("graphDB shut down.");
    }



        private static void registerShutdownHook( final GraphDatabaseService graphDb )
        {
            // Registers a shutdown hook for the Neo4j instance so that it
            // shuts down nicely when the VM exits (even if you "Ctrl-C" the
            // running application).
            Runtime.getRuntime().addShutdownHook( new Thread()
            {
                @Override
                public void run()
                {
                    graphDb.shutdown();
                }
            } );
        }



    }
