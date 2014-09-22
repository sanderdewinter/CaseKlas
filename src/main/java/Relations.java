import org.neo4j.graphdb.RelationshipType;

/**
 * Created by rik on 9/16/14.
 */
public interface Relations {

     static enum RelTypes implements RelationshipType
    {
        IS_FRIENDS_WITH,
        WORKS_WITH,
        ZIT_IN_KLAS
    }
}
