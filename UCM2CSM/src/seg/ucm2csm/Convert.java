package seg.ucm2csm;


import java.io.FileOutputStream;
import java.io.PrintStream;
import java.lang.reflect.InvocationTargetException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.FileEditorInput;

import seg.jUCMNav.editors.UCMNavMultiPageEditor;
import seg.jUCMNav.extensionpoints.IURNExport;
import seg.jUCMNav.model.util.URNNamingHelper;
import seg.ucm2csm.duplicate.CSMDupConnection;
import seg.ucm2csm.duplicate.CSMDupConnectionList;
import seg.ucm2csm.duplicate.CSMDupNode;
import seg.ucm2csm.duplicate.CSMDupNodeList;
import seg.ucm2csm.implicit.CSMResource;
import seg.ucm2csm.implicit.ResourceAcquisition;
import seg.ucm2csm.implicit.ResourceRelease;
import seg.ucm2csm.one2one.ComponentRefConverter;
import seg.ucm2csm.one2one.CsmExportWarning;
import seg.ucm2csm.one2one.ExternalOperationConverter;
import seg.ucm2csm.one2one.PassiveResourceConverter;
import seg.ucm2csm.one2one.PluginBindingConverter;
import seg.ucm2csm.one2one.ProcessingResourceConverter;
import ucm.map.ComponentRef;
import ucm.map.EmptyPoint;
import ucm.map.EndPoint;
import ucm.map.PathNode;
import ucm.map.PluginBinding;
import ucm.map.StartPoint;
import ucm.map.Stub;
import ucm.map.UCMmap;
import ucm.map.impl.MapFactoryImpl;
import ucm.performance.ExternalOperation;
import ucm.performance.GeneralResource;
import ucm.performance.PassiveResource;
import ucm.performance.ProcessingResource;
import urn.URNspec;
import urncore.Component;
import urncore.IURNDiagram;
import urncore.URNmodelElement;

/**
 * Performs the sequence of operations to convert a URN specification into a CSM.
 *
 * @see seg.ucm2csm.duplicate
 * @see seg.ucm2csm.implicit
 * @see seg.ucm2csm.one2one
 *
 */
public class Convert implements IURNExport {

    private List processedComponents = new ArrayList();

    private List processedResources = new ArrayList();

    private int dummy_id = 5000; // TODO:  wave limitation

    private int emptyPoint_id = 9000; // TODO:  wave limitation

    Vector problems = new Vector(); // List of warnings and errors for the Problems view

    // Converts object through polymorphism (dynamic binding)
    private void doComponentRefConvert(ComponentRefConverter obj, PrintStream ps) {
        obj.Convert(ps);
    }

    /**
     * Main control loop that provides the CSM header, translates each UCM map into a CSM scenario
     * (with components and resources), then outputs the CSM footer.
     */
    public void export(URNspec urn, HashMap mapDiagrams, FileOutputStream fos) throws InvocationTargetException {

        PrintStream ps = new PrintStream(fos);
        this.problems.clear();

        // prepare CSM header and footer
        String XML_header = "<?xml version=\"1.0\" encoding=\"utf-8\"?>";
        String CSM_header = "<CSM:CSMType "
            + "xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" "
            + "xmlns:CSM=\"platform:/resource/edu.carleton.sce.puma/CSM.xsd\" "
            + "name=\"" + urn.getName() + "\" "
            + "description=\"" + urn.getDescription() + "\" "
            + "author=\"" + urn.getAuthor() + "\" "
            + "created=\"" + convertUcmDateToCsmDate(urn.getCreated()) + "\" "
            + "version=\"" + urn.getSpecVersion() + "\">";
        String CSM_footer = "</CSM:CSMType>";

        // output header
        ps.println(XML_header);
        ps.println(CSM_header);

        // Produce SpecDiagrams (scenarios)
        for (Iterator iter = urn.getUrndef().getSpecDiagrams().iterator(); iter.hasNext();) {
            IURNDiagram diag = (IURNDiagram) iter.next();
            if (diag instanceof UCMmap) {
                UCMmap map = (UCMmap) diag;
                exportMap(map, ps, null, this.problems);
            }
        }
        
        // output footer
        ps.println(CSM_footer);
        ps.flush();

        // Reports to Problems view
        this.problems.add(new CsmExportWarning("CSM export completed. Consult the console for additional schema validation results.", IMarker.SEVERITY_INFO));
        refreshProblemsView(this.problems);

    }

    /**
     * Converts the UCM (String) timestamp into a (CSM) xsd:dateTime compliant format.
     * 
     * @param dateString to be converted into a xsd:dateTime compliant format
     * @return the xsd:dateTime equivalent to dateString
     */
    private String convertUcmDateToCsmDate(String dateString) {
	SimpleDateFormat sdf = new SimpleDateFormat("MMMM dd, yyyy hh:mm:ss aa zzz");
	Date date = Calendar.getInstance().getTime(); // as fallback in case things go wrong
	try {
	    date = sdf.parse(dateString);
	} catch (ParseException p) {
	    System.out.println(p.toString());
	}
	Calendar cal = Calendar.getInstance();
	cal.setTime(date);
	String csmYear = String.valueOf(cal.get(Calendar.YEAR));
	String month = String.valueOf(cal.get(Calendar.MONTH));
	String csmMonth = ("0" + month).substring(month.length() - 1);
	String day = String.valueOf(cal.get(Calendar.DAY_OF_MONTH));
	String csmDay = ("0" + day).substring(day.length() - 1);
	String hour = String.valueOf(cal.get(Calendar.HOUR_OF_DAY));
	String csmHour = ("0" + hour).substring(hour.length() - 1);
	String min = String.valueOf(cal.get(Calendar.MINUTE));
	String csmMin = ("0" + min).substring(min.length() - 1);
	String sec = String.valueOf(cal.get(Calendar.SECOND));
	String csmSec = ("0" + sec).substring(sec.length() - 1);
	return csmYear + "-" + csmMonth + "-" + csmDay + "T" + csmHour + ":" + csmMin + ":" + csmSec;
    }

    /**
     * Exports a UCM map into a CSM scenario, along with the necessary Components and Resources.
     * 
     * @param ucmMap UCM map to export
     * @param ps print stream
     * @param pluginBinding CURRENTLY UNUSED
     * @param warnings to advertise exportation problems
     */
    private void exportMap(UCMmap ucmMap, PrintStream ps, PluginBinding pluginBinding, Vector warnings) {
        String probability;
        String transaction;
        String name_extension;

        // map name will also be plugin binding specific (if given) 
        // and contains probability and transaction
        if (pluginBinding != null) {
            name_extension = "_h" + pluginBinding.getStub().getId();
            probability = "probability=\"" + pluginBinding.getProbability() + "\" ";
            transaction = "transaction=\"" + pluginBinding.isTransaction() + "\" ";
        } else {
            name_extension = "";
            probability = "";
            transaction = "";
        }

        // prepare header
        String open_scenario_tag = "<Scenario " + "id=\"m" + ucmMap.getId() + name_extension + "\" " + "name=\"" + ucmMap.getName() + "\" " + "traceabilityLink=\"" + ucmMap.getId() + "\" ";
        String close_attributes = ">";

        // prepare footer
        String close_scenario_tag = "</Scenario>";

        // output header
        ps.print("\n        " + open_scenario_tag + probability + transaction);

        // optional attributes
        if (ucmMap.getDescription() != null) {
            String descr_attribute = "description=\"" + ucmMap.getDescription() + "\" ";
            ps.print(descr_attribute);
        }

        // output footer
        ps.println(close_attributes);

        /*
         * Create an intermediate map based on original one. This map contains references to all PathNodes in the original map as well as references to all the
         * original connections in the map. This intermediate map is implemented as two lists; - a node list - a connections list
         */
        CSMDupNodeList dupMaplist = new CSMDupNodeList();
        dupMaplist.DuplicateHyperEdges(ucmMap, warnings);
        CSMDupConnectionList dupMapConnList = new CSMDupConnectionList();
        dupMapConnList.DuplicateConnection(ucmMap, dupMaplist);

        // Add resource information to the intermediate map.
        dupMaplist.computeNodesResources(dupMapConnList);

        // Insert RA/RR/Seq nodes
        transform(dupMaplist, dupMapConnList, ps, warnings);

        // Optional sorting: eases human parsing (reading) of generated XML.
        // If in doubt, remove or disable.
        sortConnectionList(dupMapConnList);
        CSMDupNodeList dupMaplistSorted = new CSMDupNodeList();
        sortNodeList(dupMapConnList, dupMaplist, dupMaplistSorted);

        // Generate XML
        saveXML(ucmMap, ps, dupMaplistSorted, dupMapConnList, warnings);

        // Close scenario
        ps.println("        " + close_scenario_tag);

        // Generate intermediate sub-maps for probabilistic binding of dynamic stubs
        outputDynamicStubSubMaps(dupMaplistSorted, ucmMap, ps, warnings);

        // Generate components
        for (Iterator iter3 = ucmMap.getContRefs().iterator(); iter3.hasNext();) {
            ComponentRef compRef = (ComponentRef) iter3.next();
            // produce components only once (to avoid CSM2LQN to crash)
            if (!this.processedComponents.contains(((Component) compRef.getContDef()).getId())) {
        	this.processedComponents.add(((Component) compRef.getContDef()).getId());
                // generate CSM representation
                ComponentRefConverter obj = new ComponentRefConverter(compRef);
                doComponentRefConvert(obj, ps);
            }
        }

        // Generate resources
        for (Iterator res = ucmMap.getUrndefinition().getUrnspec().getUcmspec().getResources().iterator(); res.hasNext();) {
            GeneralResource genRes = (GeneralResource) res.next();
            // but ouput each resource only once
            if (!this.processedResources.contains(genRes.getId())) {
        	this.processedResources.add(genRes.getId());
                if (genRes instanceof ExternalOperation) {
                    ExternalOperationConverter externalOpnCvtr = new ExternalOperationConverter((ExternalOperation) genRes);
                    externalOpnCvtr.Convert(ps, /* source */null, /* target */ null, warnings);
                } else if (genRes instanceof ProcessingResource) {
                    ProcessingResourceConverter processingResCvtr = new ProcessingResourceConverter((ProcessingResource) genRes);
                    processingResCvtr.Convert(ps, /* source */null, /* target */ null, warnings);
                } else if (genRes instanceof PassiveResource) {
                    PassiveResourceConverter passiveResCvtr = new PassiveResourceConverter((PassiveResource) genRes);
                    passiveResCvtr.Convert(ps, /* source */null, /* target */ null, warnings);
                }
            }
        }

        ps.flush();
    }

    /**
     * To convey to CSM scenario to capability of selecting among one of the multiple submaps used by dynamic stubs
     * (e.g. probabilistic execution), an
     * intermediate scenario is used.  This intermediate scenario expands each submap possibility into a
     * CSM branch.
     * <BR>
     * <E>WARNING/TODO</E>:  stubs (and submaps) with multiple input or multiple output are currently processed in
     * a much simplified manner.  At the present time, this is considered to be satisfactory since CSM does
     * not support more complex refinements than single input, single output ones.
     *  
     * @param dupMaplist
     * @param ucmMap
     * @param ps
     * @param warnings
     */
    private void outputDynamicStubSubMaps(CSMDupNodeList dupMaplist, UCMmap ucmMap, PrintStream ps, Vector warnings) {

        String oneTab = "        ";
        String twoTab = "            ";

        for (int i = 0; i < dupMaplist.size(); i++) {
            CSMDupNode node = dupMaplist.get(i);
            // for each dynamic stub contained in the current list of nodes:
            if ((node.getNode() != null) && (node.getNode() instanceof Stub) && (((Stub) node.getNode()).isDynamic())) {
                Stub stub = (Stub) node.getNode();

                String stubId = stub.getId();
                String fake_stubId = "fs_" + stubId;
                String steps = "";
                for (int j = 0; j < stub.getBindings().size(); j++) {
                    steps = steps + fake_stubId + "_step_" + j + " ";
                }
                steps = steps.trim();

                String scenario_head = "<Scenario id=\"" + fake_stubId + "\" name=\"" + ucmMap.getName() + "_" + stub.getName() + "\" >";
                ps.println(oneTab + scenario_head);

                /*
                 * assuming all bindings have the same cardinality...
                 */
                String sourceId = "";
                if (stub.getBindings().size() != 0) {
                    for (int j = 0; j < ((PluginBinding) stub.getBindings().get(0)).getIn().size(); j++) {
                        String startId = fake_stubId + "_start_" + j;
                        String start = "<Start id=\"" + startId + "\" " + "target=\"" + startId + "_ds1\" />";
                        String ds1 = "<Step id=\"" + startId + "_ds1\" " + "name=\"dummy1\" " + "predecessor=\"" + startId + "\" " + "successor=\""
                        + fake_stubId + "_branch\" />";

                        ps.println(twoTab + start);
                        ps.println(twoTab + ds1);
                        sourceId = sourceId.concat(startId + "_ds1 ");
                    }

                } else {
                    warnings.add(new CsmExportWarning("Stub " + stub.getName() + " has no bindings.", stub, IMarker.SEVERITY_ERROR));
                    sourceId = sourceId.concat(fake_stubId + "_ERROR_NO_BINDING");
                }
                String branch = "<Branch id=\"" + fake_stubId + "_branch\" source=\"" + sourceId + "\" target=\"" + steps + "\" />";
                ps.println(twoTab + branch);

                /*
                 * identifying the sucessors (merge)
                 */
                String targetId = "";
                if (stub.getBindings().size() != 0) {
                    for (int k = 0; k < ((PluginBinding) stub.getBindings().get(0)).getOut().size(); k++) {
                        targetId = targetId.concat(fake_stubId + "_merge_" + k + " ");
                    }
                }

                int j = 0;
                if (stub.getBindings().size() != 0) {
                    for (Iterator iter = stub.getBindings().iterator(); iter.hasNext();) {
                        PluginBinding binding = (PluginBinding) iter.next();
                        PluginBindingConverter bind_obj = new PluginBindingConverter(binding);
                        String step = "<Step id=\"" + fake_stubId + "_step_" + j + "\" " + "name=\"" + binding.getPlugin().getName() + "\" " + "predecessor=\""
                        + fake_stubId + "_branch\" " + "successor=\"" + targetId + "\" " + "probability=\"" + binding.getProbability() + "\" " + ">";
                        ps.println(twoTab + step);

                        // produce Bindings relative to sub-map
                        ArrayList src = new ArrayList();
                        ArrayList tgt = new ArrayList();
                        if (binding.getIn().size() > 1) {
                            for (int k = 0; k < binding.getIn().size(); k++) {
                                src.add(fake_stubId + "_start_" + k);
                            }
                        } else {
                            src.add(fake_stubId + "_start_0");
                        }
                        if (binding.getOut().size() > 1) {
                            for (int k = 0; k < binding.getOut().size(); k++) {
                                tgt.add(fake_stubId + "_end_" + k); // for
                                // bind_obj
                            }
                        } else {
                            tgt.add(fake_stubId + "_end_0");
                        }
                        bind_obj.Convert(ps, src, tgt, warnings);
                        ps.println(twoTab + "</Step>");
                        j++;
                    }
                }

                if (stub.getBindings().size() != 0) {
                    for (int k = 0; k < ((PluginBinding) stub.getBindings().get(0)).getOut().size(); k++) {
                        String merge = "<Merge id=\"" + fake_stubId + "_merge_" + k + "\" " + "source=\"" + steps + "\" " + "target=\"" + fake_stubId + "_ds2_"
                        + k + "\" />";
                        String ds2 = "<Step id=\"" + fake_stubId + "_ds2_" + k + "\" " + "name=\"dummy2\" " + "predecessor=\"" + fake_stubId + "_merge_" + k
                        + "\" " + "successor=\"" + fake_stubId + "_end_" + k + "\" />";
                        String end = "<End id=\"" + fake_stubId + "_end_" + k + "\" " + "source=\"" + fake_stubId + "_ds2_" + k + "\" />";
                        ps.println(twoTab + merge);
                        ps.println(twoTab + ds2);
                        ps.println(twoTab + end);
                    }
                }

                String scenario_tail = "</Scenario>";

                ps.println(oneTab + scenario_tail);
            }
        }

    }

    /**
     * Sorting of CSMDupNodeList is totally optionnal, yet greatly appreciated by humans attempting to comprehend the
     * XML generated document.
     * 
     * @param connList
     *            used to visit the nodes
     * @param nodeList
     *            list of nodes to sort
     * @param nodeListSorted
     *            sorted node list
     */
    private void sortNodeList(CSMDupConnectionList connList, CSMDupNodeList nodeList, CSMDupNodeList nodeListSorted) {
        int indexInNewList;
        int indexInOldList;
        for (int i = 0; i < connList.size(); i++) {
            if (connList.get(i).getCSMSource() != null) {
                indexInNewList = findNodeInList(nodeListSorted, connList.get(i).getCSMSource().getId());
                if (indexInNewList == -1) {
                    indexInOldList = findNodeInList(nodeList, connList.get(i).getCSMSource().getId());
                    if (indexInOldList != -1) {
                        nodeListSorted.add(nodeList.get(indexInOldList));
                    }
                }
            }
            if (connList.get(i).getCSMTarget() != null) {
                indexInNewList = findNodeInList(nodeListSorted, connList.get(i).getCSMTarget().getId());
                if (indexInNewList == -1) {
                    indexInOldList = findNodeInList(nodeList, connList.get(i).getCSMTarget().getId());
                    if (indexInOldList != -1) {
                        nodeListSorted.add(nodeList.get(indexInOldList));
                    }
                }
            }
        }
    }

    /**
     * Utility to search for nodes in nodelist
     * 
     * @param nodeList
     *            list of nodes to search
     * @param id
     *            id of the node sought
     * @return index of the node in the list
     */
    private int findNodeInList(CSMDupNodeList nodeList, String id) {
        boolean found = false;
        int pos = -1;
        for (int i = 0; (i < nodeList.size()) && (!found); i++) {
            CSMDupNode n = nodeList.get(i);
            if (n.getId() == id) {
                found = true;
                pos = i;
            }
        }
        return pos;
    }

    /**
     * Sorting of CSMDupConnectionList is totally optionnal, yet greatly appreciated by humans attempting to comprehend the
     * XML generated document.
     * 
     * @param connList
     */
    private void sortConnectionList(CSMDupConnectionList connList) {
        int startSortingAt = 0;
        int lastSorted = 0;
        int startSortingFrom = startSortingAt;
        while (startSortingAt < connList.size()) {
            // find a better starting place if it exists
            boolean foundConnWithStartSource = false;
            for (int i3 = startSortingAt; !foundConnWithStartSource && i3 < connList.size(); i3++) {
                if (connList.get(i3).getCSMSource().getType() == CSMDupNode.START) { // TODO:
                    // connect -> start
                    if (connList.get(i3).getCSMSource().getNode().getPred() != null) {
                        startSortingFrom = i3;
                        foundConnWithStartSource = true;
                    }
                }
            }
            if (startSortingAt != startSortingFrom) {
                connList.swap(startSortingAt, startSortingFrom);
            }
            lastSorted = startSortingAt;
            startSortingAt++;
            startSortingFrom = startSortingAt;

            // sort until end node or end of list
            boolean foundEnd = false;
            while ((startSortingAt < connList.size()) && !foundEnd) {
                boolean foundFollowing = false;

                foundEnd = ((connList.get(lastSorted).getCSMTarget().getType() == CSMDupNode.END) && (connList.get(lastSorted).getCSMTarget().getNode()
                        .getSucc() == null));

                for (int i4 = startSortingAt; !foundFollowing && i4 < connList.size(); i4++) {
                    if (connList.get(i4).getCSMSource().getId() == connList.get(lastSorted).getCSMTarget().getId()) {
                        startSortingFrom = i4;
                        foundFollowing = true;
                    }
                }
                if (startSortingAt != startSortingFrom) {
                    connList.swap(startSortingAt, startSortingFrom);
                }
                lastSorted = startSortingAt;
                startSortingAt++;
                startSortingFrom = startSortingAt;
            }
        }
    }

    /**
     * Once the essence of the UCM map has been converted, the duplicate map is revisited and
     * ResourceAcquisition and ResourceRelease nodes are added as required.
     */
    private void transform(CSMDupNodeList list, CSMDupConnectionList conn_list, PrintStream ps, Vector warnings) {
        ResourceAcquisition ra = new ResourceAcquisition(ps);
        ResourceRelease rr = new ResourceRelease(ps);
        int i = 0;
        int list_size = list.size();
        while (i < list_size) {
            CSMDupNode node = list.get(i); // current edge
            PathNode curr_node = node.getNode(); // current node
            ra.acquireResource(curr_node, list, conn_list);
            rr.releaseResource(curr_node, list, conn_list);
            i++;
        }
        addDummy(list, conn_list, warnings);
    }

    /**
     * Print DummyStep
     */
    private void printDummyStep(CSMDupNode node, String id, PrintStream ps, CSMDupConnectionList list) {

        // initializing attributes
        String name = "Dummy";
        String successor = list.getTargetForSource(node.getId());
        String predecessor = list.getSourceForTarget(node.getId());
        String dummy_attributes;
        String hostDemand = "hostDemand=\"0\" ";
        // object attributes
        if (predecessor.startsWith("G") && successor.startsWith("G")) {
            dummy_attributes = "<Step id=\"" + id + "\" " + "name=\"" + name + "\" " + "predecessor=\"" + predecessor + "\" " + "successor=\"" + successor
            + "\" " + hostDemand + "/>";
        } else if (predecessor.startsWith("G") && !successor.startsWith("!G")) {
            dummy_attributes = "<Step id=\"" + id + "\" " + "name=\"" + name + "\" " + "predecessor=\"" + predecessor + "\" " + "successor=\"h" + successor
            + "\" " + hostDemand + "/>";
        } else if (!predecessor.startsWith("!G") && successor.startsWith("G")) {
            dummy_attributes = "<Step id=\"" + id + "\" " + "name=\"" + name + "\" " + "predecessor=\"h" + predecessor + "\" " + "successor=\"" + successor
            + "\" " + hostDemand + "/>";
        } else {
            dummy_attributes = "<Step id=\"" + id + "\" " + "name=\"" + name + "\" " + "predecessor=\"h" + predecessor + "\" " + "successor=\"h" + successor
            + "\" " + hostDemand + "/>";
        }

        // output to file
        ps.println("            " + dummy_attributes);
        ps.flush();
    }

    /**
     * Output each node of the duplicate map.  Keep an opened eye for anomalies (w.r.t. CSM expectations).
     */
    private void saveXML(UCMmap map, PrintStream ps, CSMDupNodeList dupMaplist, CSMDupConnectionList dupMapConnlist, Vector warnings) {

        int startPoints = 0;

        for (int dupMapListIndex = 0; dupMapListIndex < dupMaplist.size(); dupMapListIndex++) {
            CSMDupNode curr_node = dupMaplist.get(dupMapListIndex);
            // printing RA
            if (curr_node.getId().startsWith("G1")) {
                CSMResource resource = curr_node.getResourceToAcquire();
                ResourceAcquisition ra = new ResourceAcquisition(ps);
                if (resource != null) {
                    ra.acquireRes(resource, curr_node, dupMapConnlist);
                } else {
                    String res = curr_node.getResToAcquire();
                    ra.acquireComp(res, curr_node, dupMapConnlist);
                }

            }
            // printing RR
            else if (curr_node.getId().startsWith("G3")) {
                CSMResource resource = curr_node.getResourceToRelease();
                ResourceRelease rr = new ResourceRelease(ps);
                if (resource != null) {
                    rr.releaseRes(resource, curr_node, dupMapConnlist);
                } else {
                    String res = curr_node.getResToRelease();
                    rr.releaseComp(res, curr_node, dupMapConnlist);
                }

            }
            // printing RA_Sequence
            else if (curr_node.getId().startsWith("G2")) {
                ResourceAcquisition ra = new ResourceAcquisition(ps);
                ra.acquireEmptyPoint(curr_node, dupMapConnlist);
            }
            // printing RR_Sequence
            else if (curr_node.getId().startsWith("G4")) {
                ResourceRelease rr = new ResourceRelease(ps);
                rr.acquireEmptyPoint(curr_node, dupMapConnlist);
            }
            // printing dummy Step
            else if (curr_node.getId().startsWith("G5")) {
                printDummyStep(curr_node, curr_node.getId(), ps, dupMapConnlist);
            }
            // print other objects
            else {
                String curr_node_id = dupMaplist.get(dupMapListIndex).getId();
                // determine new source and target of all PathConnection types
                ArrayList sourcesList = new ArrayList();
                ArrayList targetsList = new ArrayList();
                // retrieve list of target/source nodes
                sourcesList = getSources(dupMapConnlist, curr_node_id);
                targetsList = getTargets(dupMapConnlist, curr_node_id);
                PathNode pathnode = curr_node.getNode();
                /**
                 * Check for presence of too many StartPoint
                 */
                if ((curr_node.getType() == CSMDupNode.START) && (curr_node.getNode().getPred().size() == 0)) {
                    startPoints++;
                }
                /**
                 * EmptyPoint of a WaitingPlace
                 */
                if (curr_node.isPathNode() && (pathnode instanceof EmptyPoint) && (pathnode.getSucc().size() > 1)) {
                    // convert the array into a comma-separated list
                    String target_noBracket = targetsList.toString().substring(1, (targetsList.toString().length() - 1));
                    // remove the commas
                    String target_noComma =  target_noBracket.replaceAll(",", "");

                    String epoint_attributes = "            <Fork id=\"h" + curr_node.getId() + "\" ";
                    String epoint_source = "source=\"" + sourcesList.get(0) + "\" ";
                    String epoint_target = "target=\"" + target_noComma + "\" ";
                    String traceabilityLink = "traceabilityLink=\"" + pathnode.getId() + "\" ";
                    String epoint_end = "/> <!-- EmptyPoint of WaitingPlace -->";
                    ps.println(epoint_attributes + epoint_source + epoint_target + traceabilityLink + epoint_end);
                    /**
                     * EmptyPoint turned into DummyStep
                     */
                } else if (curr_node.getType() == CSMDupNode.CSMSTEP) {
                    String epoint_attributes = "            <Step id=\"h" + curr_node.getId() + "\" name=\"EmptyPoint\" hostDemand=\"0\" ";
                    String epoint_source = "predecessor=\"" + sourcesList.get(0) + "\" ";
                    String epoint_target = "successor=\"" + targetsList.get(0) + "\" ";
                    String traceabilityLink = "traceabilityLink=\"" + pathnode.getId() + "\" ";
                    String epoint_end = "/> <!-- EmptyPoint -->";
                    ps.println(epoint_attributes + epoint_source + epoint_target + traceabilityLink + epoint_end);
                    /**
                     * EndPoint of a Connect
                     */
                } else if (curr_node.isPathNode() && (pathnode instanceof EndPoint) && ((EndPoint) pathnode).getSucc().size() > 0) {
                    String epoint_attributes = "            <Sequence id=\"h" + curr_node.getId() + "\" ";
                    String epoint_source = "source=\"" + sourcesList.get(0) + "\" ";
                    String epoint_target = "target=\"" + targetsList.get(0) + "\" ";
                    String traceabilityLink = "traceabilityLink=\"" + pathnode.getId() + "\" ";
                    String epoint_end = "/> <!-- EndPoint of a Connect -->";
                    ps.println(epoint_attributes + epoint_source + epoint_target + traceabilityLink + epoint_end);
                    /**
                     * StartPoint of a Connect
                     */
                } else if (curr_node.isPathNode() && (pathnode instanceof StartPoint) && ((StartPoint) pathnode).getPred().size() > 0) {
                    String epoint_attributes = "            <Sequence id=\"h" + curr_node.getId() + "\" ";
                    String epoint_source = "source=\"" + sourcesList.get(0) + "\" ";
                    String epoint_target = "target=\"" + targetsList.get(0) + "\" ";
                    String traceabilityLink = "traceabilityLink=\"" + pathnode.getId() + "\" ";
                    String epoint_end = "/> <!-- StartPoint of a Connect -->";
                    ps.println(epoint_attributes + epoint_source + epoint_target + traceabilityLink + epoint_end);
                } else {
                    curr_node.printPathNode(ps, sourcesList, targetsList, warnings);
                }
            }
        } // for
        if (startPoints > 1) {
            warnings.add(new CsmExportWarning("Too many (" + startPoints + ") StartPoint in map " + map.getName(), map, IMarker.SEVERITY_ERROR));
        }
    }

    // retrieve list of source nodes
    private ArrayList getSources(CSMDupConnectionList dupMapConnlist, String edge_id) {
        ArrayList sources = new ArrayList();
        for (int i = 0; i < dupMapConnlist.size(); i++) {
            String add_h = "h";
            String source_id = dupMapConnlist.get(i).getSourceStr();
            String target_id = add_h.concat(dupMapConnlist.get(i).getTargetStr());
            if (target_id.compareTo(add_h.concat(edge_id)) == 0) {
                if (source_id.startsWith("G"))
                    sources.add(source_id);
                else
                    sources.add(add_h.concat(source_id));

            } // if
        } // for
        if (sources.isEmpty()) {
            sources = null;
        }
        return sources;
    } // method

    // retrieve list of target nodes
    private ArrayList getTargets(CSMDupConnectionList dupMapConnlist, String edge_id) {
        ArrayList targets = new ArrayList();
        for (int i = 0; i < dupMapConnlist.size(); i++) {
            String add_h = "h";
            String source_id = add_h.concat(dupMapConnlist.get(i).getSourceStr());
            String target_id = dupMapConnlist.get(i).getTargetStr();
            if (source_id.compareTo(add_h.concat(edge_id)) == 0) {
                if (target_id.startsWith("G"))
                    targets.add(target_id);
                else
                    targets.add(add_h.concat(target_id));
            } // if
        } // for
        if (targets.isEmpty()) {
            targets = null;
        }
        return targets;
    } // method

    // Adds a Dummy responsability in between 2 steps
    private void addDummy(CSMDupNodeList node_list, CSMDupConnectionList conn_list, Vector warnings) {
        boolean work_to_do = true;

        while (work_to_do) {
            work_to_do = false; // reset loop condition
            // Scan the list of connections for a connection that has Steps as
            // both source and target
            int conn_list_size = conn_list.size();
            for (int i = 0; i < conn_list_size; i++) {
                CSMDupConnection curr_conn = conn_list.get(i);
                CSMDupNode source = curr_conn.getCSMSource();
                CSMDupNode target = curr_conn.getCSMTarget();

                // not yet processed Empty point (don't compare using
                // "instanceof" to discern processed nodes from unprocessed
                // ones)
                if (target.isPathNode() && (target.getType() == CSMDupNode.EMPTY)) {
                    // check for EmptyPoint with multiple successors (i.e. those
                    // of Timer and Connect)
                    if (target.getNode().getSucc().size() > 1) {
                        // convert that EmptyPoint into AndFork
                        // if necessary, insert a DummyStep after previous node
                        if ((source.isPathNode() && ((source.getType() == CSMDupNode.RESPREF) || (source.getType() == CSMDupNode.STUB)))
                                || (source.getType() == CSMDupNode.RR) || (source.getType() == CSMDupNode.RA) || (source.getType() == CSMDupNode.CSMSTEP)) 
                        { 
                            // no need for extra DummyStep. Do nothing!
                        } else {
                            // do insert a DummyStep after previous node
                            insertDummyStep(node_list, conn_list, curr_conn, source, target);
                            conn_list_size++;
                            work_to_do = true; // we need to start over after adding connections
                        }
                        // if necessary, insert a DummyStep before each successor node
                        ArrayList conns = new ArrayList();
                        for (int j = 0; j < conn_list.size(); j++) {
                            if (conn_list.get(j).getSource() == target.getNode()) {
                                conns.add(conn_list.get(j));
                            }
                        }
                        for (int j = 0; j < conns.size(); j++) {
                            CSMDupConnection con = (CSMDupConnection) conns.get(j);
                            CSMDupNode nod = con.getCSMTarget();
                            if ((nod.isPathNode() && ((nod.getType() == CSMDupNode.RESPREF) || (nod.getType() == CSMDupNode.STUB)))
                                    || (nod.getType() == CSMDupNode.RR) || (nod.getType() == CSMDupNode.RA) || (nod.getType() == CSMDupNode.CONNECT) // CONNECT
                                    // will turn into a DummyStep
                                    || (nod.getType() == CSMDupNode.CSMSTEP)) { 
                                // OK as is. Do nothing!
                            } else {
                                insertDummyStep(node_list, conn_list, con, target, nod);
                                conn_list_size++;
                                work_to_do = true; // we need to start over after adding connections
                            }
                        }
                        node_list.retype(target, CSMDupNode.ANDFORK); 
                        // wait last to retype() regular EmptyPoint. Is the test necessary?
                    } else if (conn_list.existsConnectionForSource(target)) {
                        CSMDupConnection next_conn = conn_list.getConnectionForSource(target);
                        CSMDupNode next_target = next_conn.getCSMTarget();
                        // preceeding node is a Step
                        if ((source.isPathNode() && ((source.getType() == CSMDupNode.RESPREF) || (source.getType() == CSMDupNode.STUB)))
                                || ((source.getType() == CSMDupNode.RR) || (source.getType() == CSMDupNode.RA)) || (source.getType() == CSMDupNode.CSMSTEP)) {
                            // next node is a fake EndPoint
                            if ((next_target.getType() == CSMDupNode.END) && (next_target.getNode().getSucc().size() > 0)) {
                                // insert DummyStep and convert EmptyPoint to DummySequence
                                insertDummyStep(node_list, conn_list, next_conn, target, next_target);
                                conn_list_size++;
                                node_list.retype(target, CSMDupNode.CSMDUMMY);
                                work_to_do = true; // we need to start over after adding connections
                            } else if ((next_target.isPathNode() && ((next_target.getType() == CSMDupNode.RESPREF) || (next_target.getType() == CSMDupNode.STUB)))
                                    || ((next_target.getType() == CSMDupNode.RR) || (next_target.getType() == CSMDupNode.RA))
                                    || (next_target.getType() == CSMDupNode.CSMSTEP)) {
                                // keep empty point as is (i.e. output as DummySequence)
                                node_list.retype(target, CSMDupNode.CSMDUMMY);
                                // next node ought to be a connection
                            } else { 
                                // convert EmptyPoint to a DummySequence followed by a DummyStep
                                if (next_target.getType() != CSMDupNode.CSMDUMMY) {
                                    insertDummyStep(node_list, conn_list, next_conn, target, next_target);
                                    conn_list_size++;
                                    node_list.retype(target, CSMDupNode.CSMDUMMY);
                                    work_to_do = true; // start over when new connections are added
                                }
                            }
                            // EmptyPoint preceded by connection node
                        } else {
                            /**
                             * an EmptyPoint is preceded by a connection node: convert the EmptyPoint to a DummySequence and insert DummyStep before
                             */
                            if ((source.getType() == CSMDupNode.START) || (source.getType() == CSMDupNode.ARROW) || (source.getType() == CSMDupNode.ANDFORK)
                                    || (source.getType() == CSMDupNode.ANDJOIN) || (source.getType() == CSMDupNode.ORFORK)
                                    || (source.getType() == CSMDupNode.ORJOIN) || (source.getType() == CSMDupNode.WAIT)
                                    || (source.getType() == CSMDupNode.CSMEMPTY) || (source.getType() == CSMDupNode.END)
                                    || (source.getType() == CSMDupNode.EMPTY)) {
                                if ((next_target.getType() == CSMDupNode.START) || (next_target.getType() == CSMDupNode.ARROW)
                                        || (next_target.getType() == CSMDupNode.ANDFORK) || (next_target.getType() == CSMDupNode.ANDJOIN)
                                        || (next_target.getType() == CSMDupNode.ORFORK) || (next_target.getType() == CSMDupNode.ORJOIN)
                                        || (next_target.getType() == CSMDupNode.WAIT) || (next_target.getType() == CSMDupNode.CSMEMPTY)
                                        || (next_target.getType() == CSMDupNode.END) || (next_target.getType() == CSMDupNode.EMPTY)) {
                                    node_list.retype(target, CSMDupNode.CSMSTEP);
                                } else {
                                    insertDummyStep(node_list, conn_list, curr_conn, source, target);
                                    conn_list_size++;
                                    node_list.retype(target, CSMDupNode.CSMEMPTY);
                                    work_to_do = true; // we need to start over after adding connections
                                }
                                /**
                                 * an EmptyPoint is preceded by a step and followed by a connection node: convert EmptyPoint to a DummySequence and insert a
                                 * DummyStep after
                                 */
                            } else if ((next_target.getType() == CSMDupNode.START) || (next_target.getType() == CSMDupNode.ARROW)
                                    || (next_target.getType() == CSMDupNode.ANDFORK) || (next_target.getType() == CSMDupNode.ANDJOIN)
                                    || (next_target.getType() == CSMDupNode.ORFORK) || (next_target.getType() == CSMDupNode.ORJOIN)
                                    || (next_target.getType() == CSMDupNode.WAIT) || (next_target.getType() == CSMDupNode.END)
                                    || (next_target.getType() == CSMDupNode.EMPTY)) {
                                insertDummyStep(node_list, conn_list, next_conn, target, next_target);
                                conn_list_size++;
                                node_list.retype(target, CSMDupNode.CSMDUMMY);
                                work_to_do = true; // start over when new connections are added
                            } // if
                        } // else
                    } // if
                } // if

                // Throw in an EmptyPoint between adjacent RESPREFs and STUBs.
                else if (((source.getType() == CSMDupNode.RESPREF) || (source.getType() == CSMDupNode.STUB))
                        && ((target.getType() == CSMDupNode.RESPREF) || (target.getType() == CSMDupNode.STUB))) {

                    // create dummy node
                    MapFactoryImpl mfi = new MapFactoryImpl();
                    EmptyPoint ep = mfi.createEmptyPoint();
                    ep.setName("" + this.emptyPoint_id);
                    ep.setDescription("" + this.emptyPoint_id);
                    ep.setId("" + this.emptyPoint_id);
                    CSMDupNode ep_node = new CSMDupNode(ep, warnings);
                    ep_node.setType(CSMDupNode.EMPTY);
                    ep_node.setID("" + this.emptyPoint_id);
                    ep_node.setResourcesDownstream(source.getResourcesDownstream());
                    ep_node.setResourcesUpstream(target.getResourcesUpstream());
                    this.emptyPoint_id++;
                    node_list.add(ep_node);

                    // remove curr_conn connection
                    conn_list.remove(curr_conn);
                    conn_list_size--;

                    // add new connection
                    conn_list.add(new CSMDupConnection(source, ep_node));
                    conn_list_size++;
                    conn_list.add(new CSMDupConnection(ep_node, target));
                    conn_list_size++;
                    work_to_do = true; // start over when new connections are added
                } // else
                else if (((source.getType() == CSMDupNode.START) || (source.getType() == CSMDupNode.ARROW) || (source.getType() == CSMDupNode.ANDFORK)
                        || (source.getType() == CSMDupNode.ANDJOIN) || (source.getType() == CSMDupNode.ORFORK) || (source.getType() == CSMDupNode.ORJOIN)
                        || (source.getType() == CSMDupNode.WAIT) || (source.getType() == CSMDupNode.END))
                        && ((target.getType() == CSMDupNode.START) || (target.getType() == CSMDupNode.ARROW) || (target.getType() == CSMDupNode.ANDFORK)
                                || (target.getType() == CSMDupNode.ANDJOIN) || (target.getType() == CSMDupNode.ORFORK)
                                || (target.getType() == CSMDupNode.ORJOIN) || (target.getType() == CSMDupNode.WAIT) || (target.getType() == CSMDupNode.END))) { 
                    // create dummy node
                    insertDummyStep(node_list, conn_list, curr_conn, source, target);
                    conn_list_size++;
                    work_to_do = true; // start over when new connections are added
                } // else
            } // for
        } // while
    } // method

    /**
     * Create a Dummy Step (likely just to comply with CSM syntax)
     * 
     * @param node_list
     *            where to store generated Step
     * @param conn_list
     *            to connected CSM Dup nodes
     * @param curr_conn
     *            the Dummy Step will be inserted in place of this connection
     * @param source
     *            node preceeding the Dummy Step
     * @param target
     *            node following the Dummy Step
     */
    private void insertDummyStep(CSMDupNodeList node_list, CSMDupConnectionList conn_list, CSMDupConnection curr_conn, CSMDupNode source, CSMDupNode target) {
        // create the new node
        CSMDupNode dummy_node = new CSMDupNode(this.dummy_id);
        dummy_node.setResourcesDownstream(source.getResourcesDownstream());
        dummy_node.setResourcesUpstream(target.getResourcesUpstream());
        this.dummy_id++;
        node_list.add(dummy_node);
        // remove curr_conn connection
        conn_list.remove(curr_conn);
        // add new connections to the DummyStep node
        conn_list.add(new CSMDupConnection(source, dummy_node));
        conn_list.add(new CSMDupConnection(dummy_node, target));
    }

    /**
     * This method is required to be able to claim implementing IURNExport but it is currently no implemented.
     */
    public void export(URNspec urn, HashMap mapDiagrams, String filename) throws InvocationTargetException {
        // TODO Auto-generated method stub
    }

    /**
     * Clears the warnings associated to this file and replaces them with those supplied in the vector.
     * 
     * @param warnings
     *            a vector of {@link CsmExportWarning}s to be pushed to the problems view.
     */
    private static void refreshProblemsView(Vector warnings) {
        IWorkbenchWindow[] wbw = PlatformUI.getWorkbench().getWorkbenchWindows();
        UCMNavMultiPageEditor editor = null;

        for (int i = 0; i < wbw.length; i++) {
            if (wbw[i] != null) {
                if (wbw[i].getActivePage().getActiveEditor() instanceof UCMNavMultiPageEditor) {
                    editor = (UCMNavMultiPageEditor) wbw[i].getActivePage().getActiveEditor();
                }
            }
        }
        if (editor != null) {
            IFile resource = ((FileEditorInput) editor.getEditorInput()).getFile();
            try {

                IMarker[] existingMarkers = resource.findMarkers(IMarker.PROBLEM, true, 3);
                for (int i = 0; i < existingMarkers.length; i++) {
                    IMarker marker = existingMarkers[i];
                    marker.delete();
                }
            } catch (CoreException ex) {
                System.out.println(ex); // or ex.getMessage()?
            }

            if (warnings.size() > 0) {

                for (Iterator iter = warnings.iterator(); iter.hasNext();) {
                    CsmExportWarning o = (CsmExportWarning) iter.next();

                    try {
                        IMarker marker = resource.createMarker(IMarker.PROBLEM);
                        marker.setAttribute(IMarker.SEVERITY, o.getSeverity());
                        marker.setAttribute(IMarker.MESSAGE, o.toString());
                        if (o.getLocation() instanceof URNmodelElement) {
                            URNmodelElement elem = (URNmodelElement) o.getLocation();
                            marker.setAttribute(IMarker.LOCATION, URNNamingHelper.getName(elem));
                            marker.setAttribute("EObject", ((URNmodelElement) o.getLocation()).getId()); //$NON-NLS-1$
                        } else if (o.getLocation() != null) {
                            marker.setAttribute(IMarker.LOCATION, o.getLocation().toString());
                        }

                        resource.findMarkers("seg.jUCMNav.WarningMarker", true, 1); //$NON-NLS-1$
                    } catch (CoreException ex) {
                        System.out.println(ex); // or ex.getMessage()?
                    }

                }
            }
        }
    }
}
