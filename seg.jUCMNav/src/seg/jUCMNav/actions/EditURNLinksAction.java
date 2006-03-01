/**
 * 
 */
package seg.jUCMNav.actions;

import grl.Actor;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.PlatformUI;

import seg.jUCMNav.JUCMNavPlugin;
import seg.jUCMNav.views.urnlinks.URNLinksDialog;
import urn.URNlink;
import urncore.ComponentElement;
import urncore.GRLmodelElement;
import urncore.Responsibility;

/**
 * This action open the URNLink dialog for the selected element
 * 
 * @author Jean-Fran�ois Roy
 *
 */
public class EditURNLinksAction extends URNSelectionAction {

    public static final String EDITURNLINKS = "seg.jUCMNav.EditURNLinksAction"; //$NON-NLS-1$

    private GRLmodelElement element;
    
    /**
     * @param part
     */
    public EditURNLinksAction(IWorkbenchPart part) {
        super(part);
        setId(EDITURNLINKS);
        setImageDescriptor(ImageDescriptor.createFromFile(JUCMNavPlugin.class, "icons/urnlink.gif")); //$NON-NLS-1$
    }
    
    /**
     * True if we've selected an intentional element
     */
    protected boolean calculateEnabled() {
        SelectionHelper sel = new SelectionHelper(getSelectedObjects());
        if (sel.getSelectionType() == SelectionHelper.INTENTIONALELEMENTREF) {
            element = sel.getIntentionalelementref().getDef();
            return true;
        } else if (sel.getSelectionType() == SelectionHelper.ACTORREF){
            element = (Actor)sel.getActorref().getContDef();
            return true;
        } else if (sel.getSelectionType() == SelectionHelper.ACTOR){
            element = sel.getActor();
            return true;
        } else if (sel.getSelectionType() == SelectionHelper.INTENTIONALELEMENT){
            element = sel.getIntentionalElement();
            return true;
        } else if (sel.getSelectionType() == SelectionHelper.RESPONSIBILITY){
            Responsibility resp = sel.getRespref().getRespDef();
            if (resp.getUrnlinks().size() > 0){
                element = ((URNlink)resp.getUrnlinks().get(0)).getGrlModelElements();
                return true;
            } else{
                return false;
            }
        } else if (sel.getSelectionType() == SelectionHelper.COMPONENTREF){
            ComponentElement comp = (ComponentElement)sel.getComponentref().getContDef();
            if (comp.getUrnlinks().size() > 0){
                element = ((URNlink)comp.getUrnlinks().get(0)).getGrlModelElements();
                return true;
            } else{
                return false;
            }
        }
        return false;
    }
    
    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.jface.action.IAction#run()
     */
    public void run() {
        Shell shell = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell();
        URNLinksDialog d = new URNLinksDialog(getCommandStack(), element);
    }

}