package seg.jUCMNav.model.commands;

import org.eclipse.draw2d.geometry.Point;
import org.eclipse.gef.commands.Command;

import ucm.map.PathGraph;
import ucm.map.PathNode;

/**
 *  This command creates a PathNode at a certain point in a PathGraph.
 *  Can be used to create multiple types of path node, as the PathNode itself is passed
 *  in as an argument.
 * 
 *  Created on 2005-01-30
 *
 *  * @author Etienne Tremblay
 *
 */
public class CreateNodeCommand extends Command {
	
	private static final String	CreateCommand_Label = "CreateNodeCommand";
	private PathNode node;
	private Point location;
	private PathGraph diagram;
	
	
	/* (non-Javadoc)
	 * @see org.eclipse.gef.commands.Command#execute()
	 */
	public void execute() {
	    
		if(location != null){
			node.setX(location.x);
			node.setY(location.y);
		}
	}
	/* (non-Javadoc)
	 * @see org.eclipse.gef.commands.Command#redo()
	 */
	public void redo() {
	}
	/* (non-Javadoc)
	 * @see org.eclipse.gef.commands.Command#undo()
	 */
	public void undo() {
	}
	
	/**
	 * @return Returns the location.
	 */
	public Point getLocation() {
		return location;
	}
	/**
	 * @param location The location to set.
	 */
	public void setLocation(Point location) {
		this.location = location;
	}
	/**
	 * @return Returns the node.
	 */
	public PathNode getNode() {
		return node;
	}
	/**
	 * @param node The node to set.
	 */
	public void setNode(PathNode node) {
		this.node = node;
	}
	/**
	 * @return Returns the diagram.
	 */
	public PathGraph getDiagram() {
		return diagram;
	}
	/**
	 * @param diagram The diagram to set.
	 */
	public void setDiagram(PathGraph diagram) {
		this.diagram = diagram;
	}
}
