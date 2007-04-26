package one2one;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Iterator;

import ucm.map.InBinding;
import ucm.map.OutBinding;
import ucm.map.PathNode;
import ucm.map.PluginBinding;
import ucm.map.Stub;

/**
 * <!-- begin-user-doc --> Creates the CSM representation(Step) of the Stub object Component-Ref object <!-- end-user-doc -->
 * 
 * @see one2one
 * @generated
 */
public class StubConverter implements AbstractConverter {

    private Stub stub;
    StepAttributes sa = new StepAttributes();
    //private PluginBinding ao;

    // constructors
    public StubConverter(Stub stub) {
        this.stub = stub;
    }

    // prints XML representation of object to output file
    public void Convert(PrintStream ps, ArrayList source, ArrayList target) {
//?js        ((PluginBinding) stub.getBindings().get(0)).getProbability();

	// TODO:  predecessor/successor (from parameters source/target) may be (correctly) pointing at newly inserted (CSM) DummySequences
	// while PluginBinding still points at the elements referred within the UCM structure.

        // object attributes
        String predecessorWithCommas = (String) source.toString().subSequence(1, (source.toString().length() - 1));
        String predecessor = StringUtil.trimString(',', predecessorWithCommas); // eliminate ','
        if (stub.getPred().size() > 1) {
            System.err.println("WARNING:  Stub " + stub.getName() + " has more than one predecessor.");
        }
        String successorWithCommas = (String) target.toString().subSequence(1, (target.toString().length() - 1));
        String successor = StringUtil.trimString(',', successorWithCommas); // eliminate ','
        if (stub.getSucc().size() > 1) {
            System.err.println("WARNING:  Stub " + stub.getName() + " has more than one successor.");    
        }
        String name;
        name = stub.isDynamic() ? stub.getName() :  stub.getName()+"/"+((PluginBinding)(stub.getBindings().get(0))).getPlugin().getName();  
        String mandatory_attribute = "<Step id=\"" + "h" + stub.getId() + "\" " 
        	+ "name=\"" + name + "\" "
        	+ "predecessor=\"" + predecessor + "\" "
        	+ "successor=\"" + successor + "\" ";
        ps.print("            " + mandatory_attribute);

        // optional attributes
        sa.OptionalAttributes(stub, ps);
        ps.println("> <!-- Stub -->");
        
        if (stub.isDynamic()) {
            String stubId = stub.getId();
            String fake_stubId = "fs_" + stubId;
            String plugBind_head = "<Refinement parent=\"" + "h" + stubId + "\" sub=\"" + fake_stubId + "\" >" ;            
            
            String plugBind_tail = "</Refinement>";
//            String oneTab = "        ";
//            String twoTab = "            ";
            String threeTab = "                ";
            String fourTab = "                    ";
            ps.println(threeTab + plugBind_head);
            for (Iterator pluginBindingIter = stub.getBindings().iterator(); pluginBindingIter.hasNext();) {
		PluginBinding pb = (PluginBinding) pluginBindingIter.next();
		for (Iterator inBindingIterator = pb.getIn().iterator(); inBindingIterator.hasNext();) {
		    InBinding ib = (InBinding) inBindingIterator.next();
		    String inBind = "<InBinding start=\"" + fake_stubId + "_start\" in=\"" + ((PathNode)ib.getStubEntry().getSource()).getId() +"\" />";
		    ps.println(fourTab + inBind);
		}
		for (Iterator outBindingIterator = pb.getOut().iterator(); outBindingIterator.hasNext();) {
		    OutBinding ob = (OutBinding) outBindingIterator.next();
		    String outBind = "<OutBinding end=\"" + fake_stubId + "_end\" out=\"" + ((PathNode)ob.getStubExit().getTarget()).getId() + "\" />";
		    ps.println(fourTab + outBind);
		}
	    }
            
            
            ps.println(threeTab + plugBind_tail);
            // PluginBindings will be output as branches in another map            
        } else {
            // process bindings as CSM refinements
	    for (Iterator iter = stub.getBindings().iterator(); iter.hasNext();) {
		PluginBinding binding = (PluginBinding) iter.next();
		PluginBindingConverter bind_obj = new PluginBindingConverter(binding);
		bind_obj.Convert(ps, source, target);
	    }            
        }
        
        // output to file
        String closing_attribute = "</Step>";
        ps.println("            " + closing_attribute);
        ps.flush();
    }
}
