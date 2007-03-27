package one2one;

import java.io.PrintStream;
import java.util.ArrayList;

import ucm.map.DirectionArrow;
import ucm.map.PathNode;

/**
 * <!-- begin-user-doc --> Creates the CSM representation(Sequence) of the DirectionArrow object <!-- end-user-doc -->
 * 
 * Direction Arrows are replaced by a CSM Dummy Sequence
 * 
 * @author jack
 * @see one2one
 */

public class DirectionArrowConverter implements AbstractConverter {


    private DirectionArrow ep;
    PathConnAttributes oa = new PathConnAttributes();

    // constructors
    public DirectionArrowConverter(DirectionArrow ep) {
        this.ep = ep;
    }

    // prints XML representation of object to output file
    public void Convert(PrintStream ps, ArrayList source, ArrayList target) {

        // object attributes
        String Object_attributes = "<Sequence id=\"" + "h" + ep.getId() + "\"";

        ps.print("            " + Object_attributes);
        String closing_attribute = "/>";

        oa.OptionalAttributes((PathNode) ep, ps, source, target);

        // output to file
        ps.println(closing_attribute);
        ps.flush();
    }
}