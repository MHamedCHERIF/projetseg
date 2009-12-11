package seg.jUCMNav.importexport.z151.marshal;

import java.math.BigInteger;
import java.util.List;
import javax.xml.bind.JAXBElement;
import org.eclipse.emf.common.util.EList;
import seg.jUCMNav.importexport.z151.generated.*;

//<!-- ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~ -->
//<!--  GRLNode  -->
//<!-- ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~ -->
//<xsd:complexType name="GRLNode">
//  <xsd:complexContent>
//    <xsd:extension base="GRLmodelElement">
//      <xsd:sequence>
//        <xsd:element maxOccurs="unbounded" minOccurs="0" name="pred" type="xsd:IDREF"/> <!-- LinkRef -->
//        <xsd:element maxOccurs="unbounded" minOccurs="0" name="succ" type="xsd:IDREF"/> <!-- LinkRef -->
//        <xsd:element minOccurs="0" name="contRef" type="xsd:IDREF"/> <!-- ActorRef -->
//        <xsd:element name="pos" type="Position"/>
//        <xsd:element name="size" type="Size"/>
//      </xsd:sequence>
//    </xsd:extension>
//  </xsd:complexContent>
//</xsd:complexType>

/*** Done! ***/

public class GRLNodeMHandler extends GRLmodelElementMHandler {

	@Override
	public Object handle(Object obj, Object target, boolean isFullConstruction) {
		grl.GRLNode elem = (grl.GRLNode) obj;
		String objId = elem.getId();
		GRLNode elemZ = (GRLNode) getObject(objId, target, "createGRLNode");
		if (isFullConstruction) {
			super.handle(elem, elemZ, true);
			processList(elem.getPred(), elemZ.getPred(), "createGRLNodePred", false);
			processList(elem.getSucc(), elemZ.getSucc(), "createGRLNodeSucc", false);
			elemZ.setContRef((ActorRef) process((grl.ActorRef) elem.getContRef(), null, false));
			Position posZ = new Position();
			posZ.setX(new BigInteger(Integer.toString(elem.getX())));
			posZ.setY(new BigInteger(Integer.toString(elem.getY())));
			elemZ.setPos(posZ);
			Size sizeZ = new Size();
			sizeZ.setHeight(new BigInteger("0"));
			sizeZ.setWidth(new BigInteger("0")); 
			//sizeZ.setHeight(new BigInteger(Integer.toString(elem.getContRef().getHeight())));
			//sizeZ.setWidth(new BigInteger(Integer.toString(elem.getContRef().getWidth())));
			elemZ.setSize(sizeZ);
			
		}
		return elemZ;
	}
}