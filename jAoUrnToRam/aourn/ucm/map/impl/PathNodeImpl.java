/**
 * <copyright>
 * </copyright>
 *
 * $Id$
 */
package ucm.map.impl;

import intermediateWorkflow.IntermediateWorkflowFactory;
import intermediateWorkflow.IwInput;
import intermediateWorkflow.IwNode;
import intermediateWorkflow.IwNodeConnection;
import intermediateWorkflow.IwOutput;

import jAoUrnToIw.InOutExpression;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.notify.NotificationChain;

import org.eclipse.emf.common.util.EList;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.InternalEObject;

import org.eclipse.emf.ecore.impl.ENotificationImpl;

import org.eclipse.emf.ecore.util.EObjectWithInverseResolvingEList;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.emf.ecore.util.InternalEList;

import ucm.map.MapPackage;
import ucm.map.NodeConnection;
import ucm.map.PathNode;
import ucm.map.UCMmap;

import urncore.Concern;
import urncore.IURNConnection;
import urncore.IURNContainerRef;
import urncore.IURNDiagram;
import urncore.IURNNode;
import urncore.Metadata;
import urncore.NodeLabel;
import urncore.UrncorePackage;

import urncore.impl.UCMmodelElementImpl;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Path Node</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * <ul>
 *   <li>{@link ucm.map.impl.PathNodeImpl#getX <em>X</em>}</li>
 *   <li>{@link ucm.map.impl.PathNodeImpl#getY <em>Y</em>}</li>
 *   <li>{@link ucm.map.impl.PathNodeImpl#getDiagram <em>Diagram</em>}</li>
 *   <li>{@link ucm.map.impl.PathNodeImpl#getContRef <em>Cont Ref</em>}</li>
 *   <li>{@link ucm.map.impl.PathNodeImpl#getSucc <em>Succ</em>}</li>
 *   <li>{@link ucm.map.impl.PathNodeImpl#getPred <em>Pred</em>}</li>
 *   <li>{@link ucm.map.impl.PathNodeImpl#getLabel <em>Label</em>}</li>
 * </ul>
 * </p>
 *
 * @generated
 */
public abstract class PathNodeImpl extends UCMmodelElementImpl implements PathNode {
	
	@Override
	public boolean isConnect(){
		return getClass() == ConnectImpl.class;
	}
	
	@Override
	public int getNumSucc(){
		EList<IURNConnection> allSuccs = getSucc();
		int size = allSuccs.size();
		return size;
	}
	
	@Override
	public int getNumPred(){
		EList<IURNConnection> allPreds = getPred();
		int size = allPreds.size();
		return size;
	}
	
	@Override
	public NodeConnection getSucc(int index){
		int numSuccs = getNumSucc();
		if(index >= numSuccs)
			return null;
		
		EList<IURNConnection> allSuccs = getSucc();
		IURNConnection succAtIndex = allSuccs.get(index);
		
		if(succAtIndex instanceof NodeConnection) 
			return (NodeConnection)succAtIndex;
		else
			return null;
	}
	
	@Override
	public PathNode getNext(int index) {
		NodeConnection succ = getSucc(index);
		if(succ == null)
			return null;
		
		PathNode nextNode = succ.targetAsPathNode();
		return nextNode;
	}
	
	@Override
	public NodeConnection getPred(int index) {
		int numPreds = getNumPred();
		if(index >= numPreds)
			return null;
		
		EList<IURNConnection> allPreds = getPred();
		IURNConnection predAtIndex = allPreds.get(index);
		
		if(predAtIndex instanceof NodeConnection) 
			return (NodeConnection)predAtIndex;
		else
			return null;
	}
	
	@Override
	public NodeConnection getFirstPred(){
		return (NodeConnection)getPred().get(0);
	}
	
	@Override
	public NodeConnection getFirstSucc() {
		return (NodeConnection)getSucc().get(0);
	}
	
	protected LinkedList<IwNode> _iwNodes = new LinkedList<IwNode>();
	public LinkedList<IwNode> getIwNodes() {
		return _iwNodes;
	}

	protected IwOutput iwOutput;
	public IwOutput getIwOutput() {
		return iwOutput;
	}

	protected IwInput iwInput;
	public IwInput getIwInput() {
		return iwInput;
	}

	protected IwNode iwEquivalentNode;
	@Override
	public IwNode getIwEquivalentNode() {
		return iwEquivalentNode;
	}

	@Override
	public void build() {
		buildIwOutputNode();
		buildIwInputNode();
		buildIwNodeTemplate();
		if(iwHasNodes()) 
			invokeBuildOnNodeConnections();
	}

	@Override
	public void link() {
		linkUcmMap();
		linkInternal();
		
		if(iwHasNodes()) 
			invokeLinkOnSuccs();
	}

	@Override
	public void buildIwOutputNode() {
		if(inOutExpression().hasOut()) {
			iwOutput = IntermediateWorkflowFactory.eINSTANCE.createIwOutput();
			iwOutput.setName(inOutExpression().getOut());
			_iwNodes.add(iwOutput);
		}
	}

	@Override
	public void buildIwInputNode() {
		if(inOutExpression().hasIn()) {
			iwInput = IntermediateWorkflowFactory.eINSTANCE.createIwInput();
			iwInput.setName(inOutExpression().getIn());
			_iwNodes.add(iwInput);
		}
	}

	@Override
	public void invokeBuildOnNodeConnections() {
		for(NodeConnection nodeConnection: succAsNodeConnection()){
			nodeConnection.build();
		}
	}

	@Override
	public void buildIwNodeTemplate() { 
	}

	@Override
	public void addIwEquivalentNodeAfterOutIn(IwNode iwNode) {
		iwEquivalentNode = iwNode;
		_iwNodes.add(iwNode);
	}

	@Override
	public void addIwEquivalentNodeBeforeOutIn(IwNode iwNode) {
		iwEquivalentNode = iwNode;
		_iwNodes.add(0, iwNode);
	}

	@Override
	public void linkUcmMap() {
		for(IwNode node : _iwNodes)
			node.setWorkflow(getUCMmap().getIwWorkflow());
	}

	@Override
	public void linkInternal() {
		int numOfInternalLinks = _iwNodes.size()-1; //-1 because last internal node has no outcoming internal link
		
		for(int i=0;i<numOfInternalLinks;i++){
			IwNodeConnection connection = IntermediateWorkflowFactory.eINSTANCE.createIwNodeConnection();
			connection.setSource(_iwNodes.get(i));
			connection.setTarget(_iwNodes.get(i+1));
		}
	}

	@Override
	public void invokeLinkOnSuccs() {
		for(NodeConnection nodeConnection : succAsNodeConnection()){	
			nodeConnection.link();
		}
	}

	@Override
	public boolean iwHasNodes() {
		return _iwNodes.isEmpty()==false;
	}

	@Override
	public IwNode iwGetEntryNode(NodeConnection nodeConnection) {
		return _iwNodes.getFirst();
	}

	@Override
	public IwNode iwGetExitNode(NodeConnection nodeConnection) {
		return _iwNodes.getLast();
	}

	@Override
	public InOutExpression inOutExpression() {
		Metadata data = getRamMetadata();
		InOutExpression inOutExpression = new InOutExpression();
		if(data == null) {
			inOutExpression.construct("");;
		}
		else {
			inOutExpression.construct(data.getValue());
		}
		return inOutExpression;
	}

	@Override
	public Metadata getRamMetadata() {
		if(metadata == null)
			return null;
		
		Metadata res = null;
		for(Metadata datum : metadata){
			if(datum.getName().toLowerCase().equals("ram")){
				res = datum;	
			}
		}
		return res;
	}

	@Override
	public List<NodeConnection> predAsNodeConnection() {
		List<NodeConnection> result = new ArrayList<NodeConnection>();
		for(IURNConnection conn : getPred())
			result.add((NodeConnection)conn);
		
		return result;
	}

	@Override
	public List<NodeConnection> succAsNodeConnection() {
		List<NodeConnection> result = new ArrayList<NodeConnection>();
		for(IURNConnection conn : getSucc())
			result.add((NodeConnection)conn);
		
		return result;
	}

	@Override
	public UCMmap getUCMmap() {
		return (UCMmap)getDiagram();
	}
	
	@Override
	public Concern getConcern() {
		return getUCMmap().getConcern();
	}

	/**
	 * The default value of the '{@link #getX() <em>X</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getX()
	 * @generated
	 * @ordered
	 */
	protected static final int X_EDEFAULT = 0;

	/**
	 * The cached value of the '{@link #getX() <em>X</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getX()
	 * @generated
	 * @ordered
	 */
	protected int x = X_EDEFAULT;

	/**
	 * The default value of the '{@link #getY() <em>Y</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getY()
	 * @generated
	 * @ordered
	 */
	protected static final int Y_EDEFAULT = 0;

	/**
	 * The cached value of the '{@link #getY() <em>Y</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getY()
	 * @generated
	 * @ordered
	 */
	protected int y = Y_EDEFAULT;

	/**
	 * The cached value of the '{@link #getContRef() <em>Cont Ref</em>}' reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getContRef()
	 * @generated
	 * @ordered
	 */
	protected IURNContainerRef contRef;

	/**
	 * The cached value of the '{@link #getSucc() <em>Succ</em>}' reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getSucc()
	 * @generated
	 * @ordered
	 */
	protected EList<IURNConnection> succ;

	/**
	 * The cached value of the '{@link #getPred() <em>Pred</em>}' reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getPred()
	 * @generated
	 * @ordered
	 */
	protected EList<IURNConnection> pred;

	/**
	 * The cached value of the '{@link #getLabel() <em>Label</em>}' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getLabel()
	 * @generated
	 * @ordered
	 */
	protected NodeLabel label;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected PathNodeImpl() {
		super();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	protected EClass eStaticClass() {
		return MapPackage.Literals.PATH_NODE;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public int getX() {
		return x;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setX(int newX) {
		int oldX = x;
		x = newX;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, MapPackage.PATH_NODE__X, oldX, x));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public int getY() {
		return y;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setY(int newY) {
		int oldY = y;
		y = newY;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, MapPackage.PATH_NODE__Y, oldY, y));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public IURNDiagram getDiagram() {
		if (eContainerFeatureID() != MapPackage.PATH_NODE__DIAGRAM) return null;
		return (IURNDiagram)eContainer();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public NotificationChain basicSetDiagram(IURNDiagram newDiagram, NotificationChain msgs) {
		msgs = eBasicSetContainer((InternalEObject)newDiagram, MapPackage.PATH_NODE__DIAGRAM, msgs);
		return msgs;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setDiagram(IURNDiagram newDiagram) {
		if (newDiagram != eInternalContainer() || (eContainerFeatureID() != MapPackage.PATH_NODE__DIAGRAM && newDiagram != null)) {
			if (EcoreUtil.isAncestor(this, newDiagram))
				throw new IllegalArgumentException("Recursive containment not allowed for " + toString());
			NotificationChain msgs = null;
			if (eInternalContainer() != null)
				msgs = eBasicRemoveFromContainer(msgs);
			if (newDiagram != null)
				msgs = ((InternalEObject)newDiagram).eInverseAdd(this, UrncorePackage.IURN_DIAGRAM__NODES, IURNDiagram.class, msgs);
			msgs = basicSetDiagram(newDiagram, msgs);
			if (msgs != null) msgs.dispatch();
		}
		else if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, MapPackage.PATH_NODE__DIAGRAM, newDiagram, newDiagram));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public IURNContainerRef getContRef() {
		if (contRef != null && contRef.eIsProxy()) {
			InternalEObject oldContRef = (InternalEObject)contRef;
			contRef = (IURNContainerRef)eResolveProxy(oldContRef);
			if (contRef != oldContRef) {
				if (eNotificationRequired())
					eNotify(new ENotificationImpl(this, Notification.RESOLVE, MapPackage.PATH_NODE__CONT_REF, oldContRef, contRef));
			}
		}
		return contRef;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public IURNContainerRef basicGetContRef() {
		return contRef;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public NotificationChain basicSetContRef(IURNContainerRef newContRef, NotificationChain msgs) {
		IURNContainerRef oldContRef = contRef;
		contRef = newContRef;
		if (eNotificationRequired()) {
			ENotificationImpl notification = new ENotificationImpl(this, Notification.SET, MapPackage.PATH_NODE__CONT_REF, oldContRef, newContRef);
			if (msgs == null) msgs = notification; else msgs.add(notification);
		}
		return msgs;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setContRef(IURNContainerRef newContRef) {
		if (newContRef != contRef) {
			NotificationChain msgs = null;
			if (contRef != null)
				msgs = ((InternalEObject)contRef).eInverseRemove(this, UrncorePackage.IURN_CONTAINER_REF__NODES, IURNContainerRef.class, msgs);
			if (newContRef != null)
				msgs = ((InternalEObject)newContRef).eInverseAdd(this, UrncorePackage.IURN_CONTAINER_REF__NODES, IURNContainerRef.class, msgs);
			msgs = basicSetContRef(newContRef, msgs);
			if (msgs != null) msgs.dispatch();
		}
		else if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, MapPackage.PATH_NODE__CONT_REF, newContRef, newContRef));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EList<IURNConnection> getSucc() {
		if (succ == null) {
			succ = new EObjectWithInverseResolvingEList<IURNConnection>(IURNConnection.class, this, MapPackage.PATH_NODE__SUCC, UrncorePackage.IURN_CONNECTION__SOURCE);
		}
		return succ;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EList<IURNConnection> getPred() {
		if (pred == null) {
			pred = new EObjectWithInverseResolvingEList<IURNConnection>(IURNConnection.class, this, MapPackage.PATH_NODE__PRED, UrncorePackage.IURN_CONNECTION__TARGET);
		}
		return pred;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public NodeLabel getLabel() {
		return label;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public NotificationChain basicSetLabel(NodeLabel newLabel, NotificationChain msgs) {
		NodeLabel oldLabel = label;
		label = newLabel;
		if (eNotificationRequired()) {
			ENotificationImpl notification = new ENotificationImpl(this, Notification.SET, MapPackage.PATH_NODE__LABEL, oldLabel, newLabel);
			if (msgs == null) msgs = notification; else msgs.add(notification);
		}
		return msgs;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setLabel(NodeLabel newLabel) {
		if (newLabel != label) {
			NotificationChain msgs = null;
			if (label != null)
				msgs = ((InternalEObject)label).eInverseRemove(this, UrncorePackage.NODE_LABEL__NODE, NodeLabel.class, msgs);
			if (newLabel != null)
				msgs = ((InternalEObject)newLabel).eInverseAdd(this, UrncorePackage.NODE_LABEL__NODE, NodeLabel.class, msgs);
			msgs = basicSetLabel(newLabel, msgs);
			if (msgs != null) msgs.dispatch();
		}
		else if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, MapPackage.PATH_NODE__LABEL, newLabel, newLabel));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@SuppressWarnings("unchecked")
	@Override
	public NotificationChain eInverseAdd(InternalEObject otherEnd, int featureID, NotificationChain msgs) {
		switch (featureID) {
			case MapPackage.PATH_NODE__DIAGRAM:
				if (eInternalContainer() != null)
					msgs = eBasicRemoveFromContainer(msgs);
				return basicSetDiagram((IURNDiagram)otherEnd, msgs);
			case MapPackage.PATH_NODE__CONT_REF:
				if (contRef != null)
					msgs = ((InternalEObject)contRef).eInverseRemove(this, UrncorePackage.IURN_CONTAINER_REF__NODES, IURNContainerRef.class, msgs);
				return basicSetContRef((IURNContainerRef)otherEnd, msgs);
			case MapPackage.PATH_NODE__SUCC:
				return ((InternalEList<InternalEObject>)(InternalEList<?>)getSucc()).basicAdd(otherEnd, msgs);
			case MapPackage.PATH_NODE__PRED:
				return ((InternalEList<InternalEObject>)(InternalEList<?>)getPred()).basicAdd(otherEnd, msgs);
			case MapPackage.PATH_NODE__LABEL:
				if (label != null)
					msgs = ((InternalEObject)label).eInverseRemove(this, EOPPOSITE_FEATURE_BASE - MapPackage.PATH_NODE__LABEL, null, msgs);
				return basicSetLabel((NodeLabel)otherEnd, msgs);
		}
		return super.eInverseAdd(otherEnd, featureID, msgs);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public NotificationChain eInverseRemove(InternalEObject otherEnd, int featureID, NotificationChain msgs) {
		switch (featureID) {
			case MapPackage.PATH_NODE__DIAGRAM:
				return basicSetDiagram(null, msgs);
			case MapPackage.PATH_NODE__CONT_REF:
				return basicSetContRef(null, msgs);
			case MapPackage.PATH_NODE__SUCC:
				return ((InternalEList<?>)getSucc()).basicRemove(otherEnd, msgs);
			case MapPackage.PATH_NODE__PRED:
				return ((InternalEList<?>)getPred()).basicRemove(otherEnd, msgs);
			case MapPackage.PATH_NODE__LABEL:
				return basicSetLabel(null, msgs);
		}
		return super.eInverseRemove(otherEnd, featureID, msgs);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public NotificationChain eBasicRemoveFromContainerFeature(NotificationChain msgs) {
		switch (eContainerFeatureID()) {
			case MapPackage.PATH_NODE__DIAGRAM:
				return eInternalContainer().eInverseRemove(this, UrncorePackage.IURN_DIAGRAM__NODES, IURNDiagram.class, msgs);
		}
		return super.eBasicRemoveFromContainerFeature(msgs);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public Object eGet(int featureID, boolean resolve, boolean coreType) {
		switch (featureID) {
			case MapPackage.PATH_NODE__X:
				return getX();
			case MapPackage.PATH_NODE__Y:
				return getY();
			case MapPackage.PATH_NODE__DIAGRAM:
				return getDiagram();
			case MapPackage.PATH_NODE__CONT_REF:
				if (resolve) return getContRef();
				return basicGetContRef();
			case MapPackage.PATH_NODE__SUCC:
				return getSucc();
			case MapPackage.PATH_NODE__PRED:
				return getPred();
			case MapPackage.PATH_NODE__LABEL:
				return getLabel();
		}
		return super.eGet(featureID, resolve, coreType);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@SuppressWarnings("unchecked")
	@Override
	public void eSet(int featureID, Object newValue) {
		switch (featureID) {
			case MapPackage.PATH_NODE__X:
				setX((Integer)newValue);
				return;
			case MapPackage.PATH_NODE__Y:
				setY((Integer)newValue);
				return;
			case MapPackage.PATH_NODE__DIAGRAM:
				setDiagram((IURNDiagram)newValue);
				return;
			case MapPackage.PATH_NODE__CONT_REF:
				setContRef((IURNContainerRef)newValue);
				return;
			case MapPackage.PATH_NODE__SUCC:
				getSucc().clear();
				getSucc().addAll((Collection<? extends IURNConnection>)newValue);
				return;
			case MapPackage.PATH_NODE__PRED:
				getPred().clear();
				getPred().addAll((Collection<? extends IURNConnection>)newValue);
				return;
			case MapPackage.PATH_NODE__LABEL:
				setLabel((NodeLabel)newValue);
				return;
		}
		super.eSet(featureID, newValue);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public void eUnset(int featureID) {
		switch (featureID) {
			case MapPackage.PATH_NODE__X:
				setX(X_EDEFAULT);
				return;
			case MapPackage.PATH_NODE__Y:
				setY(Y_EDEFAULT);
				return;
			case MapPackage.PATH_NODE__DIAGRAM:
				setDiagram((IURNDiagram)null);
				return;
			case MapPackage.PATH_NODE__CONT_REF:
				setContRef((IURNContainerRef)null);
				return;
			case MapPackage.PATH_NODE__SUCC:
				getSucc().clear();
				return;
			case MapPackage.PATH_NODE__PRED:
				getPred().clear();
				return;
			case MapPackage.PATH_NODE__LABEL:
				setLabel((NodeLabel)null);
				return;
		}
		super.eUnset(featureID);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public boolean eIsSet(int featureID) {
		switch (featureID) {
			case MapPackage.PATH_NODE__X:
				return x != X_EDEFAULT;
			case MapPackage.PATH_NODE__Y:
				return y != Y_EDEFAULT;
			case MapPackage.PATH_NODE__DIAGRAM:
				return getDiagram() != null;
			case MapPackage.PATH_NODE__CONT_REF:
				return contRef != null;
			case MapPackage.PATH_NODE__SUCC:
				return succ != null && !succ.isEmpty();
			case MapPackage.PATH_NODE__PRED:
				return pred != null && !pred.isEmpty();
			case MapPackage.PATH_NODE__LABEL:
				return label != null;
		}
		return super.eIsSet(featureID);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public int eBaseStructuralFeatureID(int derivedFeatureID, Class<?> baseClass) {
		if (baseClass == IURNNode.class) {
			switch (derivedFeatureID) {
				case MapPackage.PATH_NODE__X: return UrncorePackage.IURN_NODE__X;
				case MapPackage.PATH_NODE__Y: return UrncorePackage.IURN_NODE__Y;
				case MapPackage.PATH_NODE__DIAGRAM: return UrncorePackage.IURN_NODE__DIAGRAM;
				case MapPackage.PATH_NODE__CONT_REF: return UrncorePackage.IURN_NODE__CONT_REF;
				case MapPackage.PATH_NODE__SUCC: return UrncorePackage.IURN_NODE__SUCC;
				case MapPackage.PATH_NODE__PRED: return UrncorePackage.IURN_NODE__PRED;
				case MapPackage.PATH_NODE__LABEL: return UrncorePackage.IURN_NODE__LABEL;
				default: return -1;
			}
		}
		return super.eBaseStructuralFeatureID(derivedFeatureID, baseClass);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public int eDerivedStructuralFeatureID(int baseFeatureID, Class<?> baseClass) {
		if (baseClass == IURNNode.class) {
			switch (baseFeatureID) {
				case UrncorePackage.IURN_NODE__X: return MapPackage.PATH_NODE__X;
				case UrncorePackage.IURN_NODE__Y: return MapPackage.PATH_NODE__Y;
				case UrncorePackage.IURN_NODE__DIAGRAM: return MapPackage.PATH_NODE__DIAGRAM;
				case UrncorePackage.IURN_NODE__CONT_REF: return MapPackage.PATH_NODE__CONT_REF;
				case UrncorePackage.IURN_NODE__SUCC: return MapPackage.PATH_NODE__SUCC;
				case UrncorePackage.IURN_NODE__PRED: return MapPackage.PATH_NODE__PRED;
				case UrncorePackage.IURN_NODE__LABEL: return MapPackage.PATH_NODE__LABEL;
				default: return -1;
			}
		}
		return super.eDerivedStructuralFeatureID(baseFeatureID, baseClass);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public String toString() {
		if (eIsProxy()) return super.toString();

		StringBuffer result = new StringBuffer(super.toString());
		result.append(" (x: ");
		result.append(x);
		result.append(", y: ");
		result.append(y);
		result.append(')');
		return result.toString();
	}

} //PathNodeImpl
