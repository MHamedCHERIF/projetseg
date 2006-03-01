/**
 * 
 */
package seg.jUCMNav.editparts.strategyTreeEditparts;

import grl.StrategiesGroup;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.eclipse.gef.EditPolicy;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.TreeItem;

import seg.jUCMNav.JUCMNavPlugin;
import seg.jUCMNav.editpolicies.element.StrategiesGroupComponentEditPolicy;
import seg.jUCMNav.model.util.EObjectClassNameComparator;

/**
 * TreeEditPart for Strategies Group
 * 
 * @author Jean-Fran�ois Roy
 *
 */
public class StrategiesGroupTreeEditPart extends StrategyUrnModelElementTreeEditPart {

    /**
     * @param model
     */
    public StrategiesGroupTreeEditPart(StrategiesGroup model) {
        super(model);
    }

    /**
     * @see org.eclipse.gef.editparts.AbstractEditPart#createEditPolicies()
     */
    protected void createEditPolicies() {
        installEditPolicy(EditPolicy.COMPONENT_ROLE, new StrategiesGroupComponentEditPolicy());
    }
    
    public StrategiesGroup getStrategiesGroup(){
        return (StrategiesGroup)getModel();
    }
    
    /**
     * Returns the icon 
     */
    protected Image getImage() {
        if (super.getImage() == null) {       
            setImage((ImageDescriptor.createFromFile(JUCMNavPlugin.class, "icons/folder16.gif")).createImage()); //$NON-NLS-1$
        }
        return super.getImage();
    }
    
    /**
     * @return the sorted list of Strategy Group
     */
    protected List getModelChildren() {
        ArrayList list = new ArrayList();
        list.addAll(getStrategiesGroup().getStrategies());
        Collections.sort(list, new EObjectClassNameComparator());
        return list;
    }
    
    /**
     * Sets unused group to a lighter color.
     * 
     * @see org.eclipse.gef.editparts.AbstractTreeEditPart#refreshVisuals()
     */
    protected void refreshVisuals() {
        if (getStrategiesGroup().getStrategies().size() == 0)
            ((TreeItem) widget).setForeground(new Color(null, 150, 150, 150));
        else
            ((TreeItem) widget).setForeground(new Color(null, 0, 0, 0));
        getImage();
        super.refreshVisuals();
    }
}