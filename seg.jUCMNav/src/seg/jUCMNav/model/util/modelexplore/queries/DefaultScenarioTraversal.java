package seg.jUCMNav.model.util.modelexplore.queries;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Vector;

import org.eclipse.core.resources.IMarker;
import org.eclipse.emf.ecore.EObject;

import seg.jUCMNav.Messages;
import seg.jUCMNav.model.util.modelexplore.AbstractQueryProcessor;
import seg.jUCMNav.model.util.modelexplore.IQueryProcessorChain;
import seg.jUCMNav.model.util.modelexplore.QueryObject;
import seg.jUCMNav.model.util.modelexplore.QueryRequest;
import seg.jUCMNav.model.util.modelexplore.QueryResponse;
import seg.jUCMNav.scenarios.ScenarioUtils;
import seg.jUCMNav.scenarios.model.TraversalException;
import seg.jUCMNav.scenarios.model.TraversalVisit;
import seg.jUCMNav.scenarios.model.TraversalWarning;
import seg.jUCMNav.scenarios.model.UcmEnvironment;
import seg.jUCMNav.views.preferences.ScenarioTraversalPreferences;
import ucm.map.AndFork;
import ucm.map.AndJoin;
import ucm.map.Connect;
import ucm.map.DirectionArrow;
import ucm.map.EmptyPoint;
import ucm.map.EndPoint;
import ucm.map.InBinding;
import ucm.map.NodeConnection;
import ucm.map.OrFork;
import ucm.map.OrJoin;
import ucm.map.OutBinding;
import ucm.map.PathNode;
import ucm.map.PluginBinding;
import ucm.map.RespRef;
import ucm.map.StartPoint;
import ucm.map.Stub;
import ucm.map.Timer;
import ucm.map.WaitingPlace;
import ucm.scenario.ScenarioEndPoint;
import urncore.Condition;

/**
 * Query processor representing the sequence of elements traversed by the scenario traversal algorithm.
 * 
 * TODO: resp repetition count
 * 
 * @author jkealey
 * 
 */
public class DefaultScenarioTraversal extends AbstractQueryProcessor implements IQueryProcessorChain {

	public class QDefaultScenarioTraversal extends QueryRequest {
		// EndPoints / ScenarioEndPoints
		protected Vector _EndPathNodes;
		protected UcmEnvironment _env;

		// StartPoints / ScenarioStartPoints
		protected Vector _StartPathNodes;

		/**
		 * 
		 * @param startNodes
		 *            the starting point for traversal.
		 * @param endNodes
		 *            the set of end points that must be reached.
		 */
		public QDefaultScenarioTraversal(UcmEnvironment env, Vector startNodes, Vector endNodes) {
			this._queryType = QueryObject.DEFAULTSCENARIOTRAVERSAL;
			_StartPathNodes = startNodes;
			_EndPathNodes = endNodes;
			_env = env;

		}

		public Vector getEndPathNodes() {
			return _EndPathNodes;
		}

		public UcmEnvironment getEnvironment() {
			return _env;
		}

		public Vector getStartPathNodes() {
			return _StartPathNodes;
		}

	}

	public class RTraversalSequence extends QueryResponse {
		protected String error;
		protected HashMap results;
		/* Data structure (query response) for passing a list of EObjects */
		protected Vector visited;
		protected Vector warnings;

		public RTraversalSequence() {
			this._queryType = QueryObject.DEFAULTSCENARIOTRAVERSAL;
		}

		public String getError() {
			return error;
		}

		public HashMap getResults() {
			return results;
		}

		/**
		 * @return Returns the nodes.
		 */
		public Vector getVisited() {
			return visited;
		}

		public Vector getWarnings() {
			return warnings;
		}

		public void setError(String error) {
			this.error = error;
		}

		public void setResults(HashMap results) {
			this.results = results;
		}

		/**
		 * @param visited
		 *            The nodes / node connections to set.
		 */
		public void setVisited(Vector visited) {
			this.visited = visited;
		}

		public void setWarnings(Vector warnings) {
			this.warnings = warnings;
		}

	}


	// fatal error
	protected String _error;

	protected DefaultScenarioTraversalDataStructure _traversalData;
	
	// Vector of Strings
	protected Vector _warnings;
	
	/**
	 * Query processor representing the sequence of elements traversed by the scenario traversal algorithm.
	 * 
	 */
	public DefaultScenarioTraversal() {
		this._answerQueryTypes = new String[] { QueryObject.DEFAULTSCENARIOTRAVERSAL };
	}





	/**
	 * Executes the traversal.
	 * 
	 * @param env
	 *            the ucm environment in which to execute the code.
	 * @param startPoints
	 *            the list of start points / scenario start points to initialize the traversal.
	 * 
	 * @param endPoints
	 *            the list of end points /scenario end points that must be reached.
	 */
	protected void process(UcmEnvironment env, Vector startPoints, Vector endPoints) {

		
		// initialize the stacks with the start points.
		_error = _traversalData.seedAlgorithm(startPoints);
		if (_error != null)
			return;

		try {
			processAllNodes(env);

			// find reached end points. must create new vector here (or clone _visited) because we are modifying it in verifyEndPoints
			Vector reachedEndPoints = _traversalData.getReachedEndPoints();

			// verify that the reached end points match the expected ones.
			verifyEndPoints(endPoints, reachedEndPoints);
		} catch (Exception e) {
			_error = Messages.getString("DefaultScenarioTraversal.ExceptionOccurred") + e.getMessage(); //$NON-NLS-1$
		}

	}

	/**
	 * Schedules the nodes according to what is in the stacks.
	 * 
	 * @param env
	 *            the execution environment.
	 * @throws TraversalException
	 *             a fatal error occurred.
	 */
	protected void processAllNodes(UcmEnvironment env) throws TraversalException {

		Vector threadList = new Vector(); 
		TraversalVisit nextNode = null;
		for (int i=1;i< _traversalData.getNextThreadID();i++) {
			System.out.println("New thread started: " + i);
			threadList.add(new Integer(i));
		}

		
		// while no errors and while we have something to do
		do {
			boolean retry = false;

			
			do {
				try {
					// get the next node to visit, will return null if nothing to do
					nextNode = _traversalData.getNextVisit();
					retry = false;
				} catch (TraversalException ex) {
					// it means that we have a deadlock. boot the next one out.
					TraversalVisit visit = _traversalData.forceWaitingListPoll();
					_warnings
							.add(new TraversalWarning(
									Messages.getString("DefaultScenarioTraversal.TraversalBlockedOn") + visit.getVisitedElement().toString(), visit.getVisitedElement(), IMarker.SEVERITY_ERROR)); //$NON-NLS-1$
					retry = true;
				}
			} while (retry); // repeat until we stop kicking nodes out of the waiting list

			
			if (nextNode != null && _error == null) {
				int fromThread = _traversalData.getNextThreadID();
				processNode(env, nextNode);
				int toThread = _traversalData.getNextThreadID();
				Vector newlyAdded = new Vector();
				Vector newlyDied = new Vector();
				// was not blocked. 
				if (_traversalData.getConsecutiveReblocks()==0)
				{
					if (!nextNode.isValidParentComponent())
						_warnings.add(new TraversalWarning("Element is associated with multiple parent components.", nextNode.getVisitedElement(), IMarker.SEVERITY_ERROR));

					
					if (nextNode.isValidParentComponent() && nextNode.getParentComponent()!=null)
						System.out.println("Consumed on ThreadID: " + nextNode.getThreadID() + ", " + nextNode.getVisitedElement().toString() + ", " + nextNode.getParentComponent().toString());
					else
						System.out.println("Consumed on ThreadID: " + nextNode.getThreadID() + ", " + nextNode.getVisitedElement().toString());
					
					for (int i=fromThread;i<toThread;i++) {
						System.out.println("New thread started: " + i);
						newlyAdded.add(new Integer(i));
						threadList.add(new Integer(i));
					}
					
					for (Iterator iter = threadList.iterator(); iter.hasNext();) {
						Integer i = (Integer) iter.next();
						int status = _traversalData.getThreadState(i.intValue());
						if (status < 0)
							_warnings.add(new TraversalWarning("ThreadID sanity check error (" + status + ") for ThreadID " +i.intValue(), IMarker.SEVERITY_ERROR));  
						else if (status==0) {
							System.out.println("Thread died: " + i.intValue());
							newlyDied.add(i);
						}
							
					}
					threadList.removeAll(newlyDied);
					
					// thread switch. 
					if (nextNode.getThreadID()!=_traversalData.getCurrentThreadID()) {
						
						if (newlyDied.size()>0 && newlyAdded.size()>0 && (newlyDied.size()==1 ^ newlyAdded.size()==1))
						{
							if (newlyDied.size()>newlyAdded.size())
								System.out.println("And-Join from threads " + arrayToString(newlyDied.toArray(), ",") + " to thread " + newlyAdded.get(0).toString() );
							else // implicit because of xor in previous if : if (newlyDied.size()>newlyAdded.size())
								System.out.println("And-Fork from thread " +  newlyDied.get(0).toString()  + " to threads " + arrayToString(newlyAdded.toArray(), ","));
								
						}
						else
							System.out.println("DEBUG: not supposed to happen (new thread count: " + newlyAdded.size() + ", old thread count: " + newlyDied.size() + ")");
					}
				}
			}
			else
				break;

		} while (true);
		
	}
	public static String arrayToString(Object[] a, String separator) {
	    StringBuffer result = new StringBuffer();
	    if (a.length > 0) {
	        result.append(a[0].toString());
	        for (int i=1; i<a.length; i++) {
	            result.append(separator);
	            result.append(a[i].toString());
	        }
	    }
	    return result.toString();
	}
	


	/**
	 * Processes an and fork.
	 * 
	 * @param env
	 * @param andfork
	 * @throws TraversalException
	 */
	protected void processAndFork(UcmEnvironment env, AndFork andfork) throws TraversalException {
		// launches in parellel
		_traversalData.visitAllSucc(andfork);
	}

	/**
	 * Processes an and join.
	 * 
	 * @param env
	 * @param andjoin
	 * @throws TraversalException
	 */
	protected void processAndJoin(UcmEnvironment env, AndJoin andjoin) throws TraversalException {

		// TODO: Semantic variation: and join has memory or not?
		if (andjoin.getSucc().size() == 1) {
			NodeConnection out = (NodeConnection) andjoin.getSucc().get(0);
			boolean blocked = false;
			
//			determines if we have a new token or if this is another branch that is arriving. 
//			code never tested
//			
//			int count=getHitCount(((NodeConnection)_visited.lastElement()));
//			boolean newToken=true;
//			for (Iterator iter = andjoin.getPred().iterator(); iter.hasNext();) {
//				NodeConnection in = (NodeConnection) iter.next();
//				if (count<=getHitCount(in) && !in.equals(_visited.lastElement())))
//					newToken=false;
//			}
			
			for (Iterator iter = andjoin.getPred().iterator(); iter.hasNext() && !blocked;) {
				NodeConnection in = (NodeConnection) iter.next();
				if (_traversalData.getHitCount(out) + 1 > _traversalData.getHitCount(in)) {

					// we have to wait for more branches.  
					
					_traversalData.addToWaitingList(andjoin);
						 

					blocked = true;
				}
			}

			if (!blocked)
				_traversalData.visitNodeConnection(out,true); // new: we're generating a new thread id for the continuation path. 

		} else
			_error = Messages.getString("DefaultScenarioTraversal.TraversalError"); //$NON-NLS-1$
	}



	/**
	 * Processes a connect element.
	 * 
	 * @param env
	 * @param connect
	 * @throws TraversalException
	 */
	protected void processConnect(UcmEnvironment env, Connect connect) throws TraversalException {
		NodeConnection nc = (NodeConnection) connect.getSucc().get(0);
		if (nc.getTarget() instanceof StartPoint)
			_traversalData.visitNodeConnection(nc);
		else if (nc.getTarget() instanceof WaitingPlace) {
			TraversalWarning warning = _traversalData.unblockWaitingPlace(nc);
			if (warning!=null)
				_warnings.add(warning);

		} else
			_error = Messages.getString("DefaultScenarioTraversal.TraversalError"); //$NON-NLS-1$
	}

	/**
	 * Processes an empty point or direction arrow.
	 * 
	 * @param env
	 * @param pn
	 * @throws TraversalException
	 */
	protected void processEmptyPoint(UcmEnvironment env, PathNode pn) throws TraversalException {
		// can be empty point or direction arrow
		// empty points can have asynch conections. 
		_traversalData.visitAllSucc(pn);
	}

	/**
	 * Processes an end point.
	 * 
	 * @param env
	 * @param end
	 * @throws TraversalException
	 */
	protected void processEndPoint(UcmEnvironment env, EndPoint end) throws TraversalException {
		Condition post = end.getPostcondition();

		// TODO: semantic variation: if an end point's traversal context includes multiple plugin bindings, should both are fired. should the same behaviour apply when two different instances of the same plugin binding exist? (stub has two inputs connected to two start points in the plugin map, stub has one output, both inputs are triggered seperately and come back up at the same place. is the out fired once (as is now) or twice?)
				
		
		// TODO: semantic variation: if postcondition is false, should we continue processing?
		if (testCondition(env, end.getPostcondition(), Boolean.TRUE, Messages.getString("DefaultScenarioTraversal.PostConditionToEndPoint") + end.getName() + Messages.getString("DefaultScenarioTraversal.OpenParenthesis") + end.getId() //$NON-NLS-1$ //$NON-NLS-2$
				+ Messages.getString("DefaultScenarioTraversal.DidNotEvaluateToTrue"))) { //$NON-NLS-1$

			// filter by "instance" that launched the start.
			Vector outbindings = new Vector();
			// first find outbindings we need to fire
			for (Iterator iter = _traversalData.getCurrentContext().iterator(); iter.hasNext();) {
				PluginBinding binding = (PluginBinding) iter.next();
				for (Iterator iterator = binding.getOut().iterator(); iterator.hasNext();) {
					OutBinding outbinding = (OutBinding) iterator.next();
					if (outbinding.getEndPoint().equals(end))
					{
						outbindings.add(outbinding);
					}
				}
			}

			// remove them from the context
			for (Iterator iter = outbindings.iterator(); iter.hasNext();) {
				OutBinding outbinding = (OutBinding) iter.next();
				if (_traversalData.getCurrentContext().contains(outbinding.getBinding()))
					_traversalData.getCurrentContext().remove(outbinding.getBinding());
			}
			
			// fire the bindings with the new context 
			for (Iterator iter = outbindings.iterator(); iter.hasNext();) {
				OutBinding binding = (OutBinding) iter.next();
				if (binding.getStubExit() != null) {
					_traversalData.incrementHitCount(binding.getStubExit());
					_traversalData.incrementHitCount(binding);
					_traversalData.incrementHitCount(binding.getBinding());
					// must give NC or else doesn't work with two consecutive stubs. 
					_traversalData.pushPathNode(binding.getStubExit(), (PathNode) binding.getStubExit().getTarget(), outbindings.size() > 1);
				}
			}

			// Connects
			_traversalData.visitOnlySuccIfExists(end);
		}
	}

	/**
	 * Process a path node in the given environment.
	 * 
	 * @param env
	 *            the environment
	 * @param visit
	 *            the path node to process
	 * @throws TraversalException
	 *             fatal error throw exceptions
	 */
	protected void processNode(UcmEnvironment env, TraversalVisit visit) throws TraversalException {
		PathNode pn = (PathNode) visit.getVisitedElement();
		_traversalData.setCurrentContext(new Vector());
		_traversalData.getCurrentContext().addAll(visit.getContext());
		
		try {
			_traversalData.trackVisit(pn);
		}catch(TraversalException ex) {
			_warnings.add(new TraversalWarning(ex.getMessage(), pn, IMarker.SEVERITY_ERROR));
			return;
		}

		if (pn instanceof StartPoint) {
			StartPoint start = (StartPoint) pn;
			processStartPoint(env, start);
		} else if (pn instanceof EndPoint) {
			EndPoint end = (EndPoint) pn;
			processEndPoint(env, end);
		} else if (pn instanceof OrFork) {
			OrFork orfork = (OrFork) pn;
			processOrFork(env, orfork);
		} else if (pn instanceof AndFork) {
			AndFork andfork = (AndFork) pn;
			processAndFork(env, andfork);
		} else if (pn instanceof OrJoin) {
			OrJoin orjoin = (OrJoin) pn;
			processOrJoin(env, orjoin);
		} else if (pn instanceof AndJoin) {
			AndJoin andjoin = (AndJoin) pn;
			processAndJoin(env, andjoin);
		} else if (pn instanceof RespRef) {
			RespRef resp = (RespRef) pn;
			processRespRef(env, resp);
		} else if (pn instanceof EmptyPoint || pn instanceof DirectionArrow) {
			processEmptyPoint(env, pn);
		} else if (pn instanceof WaitingPlace) { // includes timer
			WaitingPlace waitingPlace = (WaitingPlace) pn;
			processWaitingPlaceAndTimer(env, waitingPlace);
		} else if (pn instanceof Stub) {
			Stub stub = (Stub) pn;
			processStub(env, visit.getSourceNodeConnection(), stub);
		} else if (pn instanceof Connect) {
			Connect connect = (Connect) pn;
			processConnect(env, connect);
		} else {
			if (pn == null)
				_error = Messages.getString("DefaultScenarioTraversal.UnknownPathNode"); //$NON-NLS-1$
			else
				_error = Messages.getString("DefaultScenarioTraversal.UnknownPathNodeColon") + pn.toString(); //$NON-NLS-1$
		}
	}

	/**
	 * Processes an or fork.
	 * 
	 * @param env
	 * @param orfork
	 * @throws TraversalException
	 */
	protected void processOrFork(UcmEnvironment env, OrFork orfork) throws TraversalException {
		// TODO: Semantic variation: All true branches? First only? Error if multiple true? If multiple, in sequence or parallel?

		Vector toVisit = new Vector();
		for (Iterator iter = orfork.getSucc().iterator(); iter.hasNext();) {
			NodeConnection nc = (NodeConnection) iter.next();
			try {
				Object result = ScenarioUtils.evaluate(nc.getCondition(), env);
				if (Boolean.TRUE.equals(result)) {
					if (toVisit.size()!=0) {
						if (ScenarioTraversalPreferences.getIsDeterministic())
							_warnings.add(new TraversalWarning(Messages.getString("DefaultScenarioTraversal.TraversalHasMultipleAlternativesAtOrFork") + orfork.getName() + Messages.getString("DefaultScenarioTraversal.OpenParenthesis") + orfork.getId() + Messages.getString("DefaultScenarioTraversal.TakingFirstOptionToRemainDeterministic"), nc.getCondition(),IMarker.SEVERITY_ERROR)); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
						else
							_warnings.add(new TraversalWarning(Messages.getString("DefaultScenarioTraversal.TraversalHasMultipleAlternativesAtOrFork") + orfork.getName() + Messages.getString("DefaultScenarioTraversal.OpenParenthesis") + orfork.getId() + Messages.getString("DefaultScenarioTraversal.TakingAnyOptionNonDeterministic"), nc.getCondition(), IMarker.SEVERITY_INFO)); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
					}
					
					toVisit.add(nc);
				} 
			} catch (IllegalArgumentException e) {
				_warnings.add(new TraversalWarning(e.getMessage(), nc.getCondition(), IMarker.SEVERITY_ERROR));
				//throw new TraversalException(e.getMessage(), e);
			}
		}
		
		if (toVisit.size()>0) {
			if (ScenarioTraversalPreferences.getIsDeterministic())
				_traversalData.visitNodeConnection((NodeConnection)toVisit.get(0));
			else {
				int i = (int) Math.round(Math.random()*(toVisit.size()-1));
				_traversalData.visitNodeConnection((NodeConnection)toVisit.get(i));
			}
		}
		else if (ScenarioTraversalPreferences.getIsPatientOnPreconditions()) {
			_traversalData.addToWaitingList(orfork);
		}
		else
			_warnings.add(new TraversalWarning(Messages.getString("DefaultScenarioTraversal.TraversalBlockedAtOrFork") + orfork.getName() + Messages.getString("DefaultScenarioTraversal.OpenParenthesis") + orfork.getId() + Messages.getString("DefaultScenarioTraversal.NoForkConditionEvaluatesToTrue"),orfork, IMarker.SEVERITY_ERROR)); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
	}

	/**
	 * Processes an or join
	 * 
	 * @param env
	 * @param orjoin
	 * @throws TraversalException
	 */
	protected void processOrJoin(UcmEnvironment env, OrJoin orjoin) throws TraversalException {
		_traversalData.visitOnlySucc(orjoin);
	}

	/**
	 * Processes a responsibility.
	 * 
	 * @param env
	 * @param resp
	 * @throws TraversalException
	 */
	protected void processRespRef(UcmEnvironment env, RespRef resp) throws TraversalException {
		// TODO: Repetition count

		try {
			if (resp.getRepetitionCount()>0)
			{
				// loop repetition count time. 
				for ( int i=0; i<resp.getRepetitionCount();i++) {
					ScenarioUtils.evaluate(resp, env);
				}
			}
			else {
				_warnings.add(new TraversalWarning("Ignoring responsibility because of repetition count.", resp, IMarker.SEVERITY_INFO));
			}
		} catch (IllegalArgumentException e) {
			//throw new TraversalException(e.getMessage(), e);
			_warnings.add(new TraversalWarning(e.getMessage(), resp, IMarker.SEVERITY_ERROR));
		}

		_traversalData.visitOnlySucc(resp);
	}

	/**
	 * Proccesses a start point
	 * 
	 * @param env
	 * @param start
	 * @throws TraversalException
	 */
	protected void processStartPoint(UcmEnvironment env, StartPoint start) throws TraversalException {
		// TODO: semantic variation: if the pre-condition is invalid, should we proceed anyways?
		if (testCondition(env, start.getPrecondition(), Boolean.TRUE, Messages.getString("DefaultScenarioTraversal.PreconditionToStartPoint") + start.getName() + Messages.getString("DefaultScenarioTraversal.QuoteOpenParenthesis") + start.getId() //$NON-NLS-1$ //$NON-NLS-2$
				+ Messages.getString("DefaultScenarioTraversal.ParenthesisDidNotEvaluateToTrue"))) { //$NON-NLS-1$

			_traversalData.visitOnlySucc(start);
		} else if (ScenarioTraversalPreferences.getIsPatientOnPreconditions()) {
			_warnings.remove(_warnings.lastElement());
			_traversalData.addToWaitingList(start);
		}
	}

	/**
	 * Processes a stub.
	 * 
	 * @param env
	 * @param stub
	 * @throws TraversalException
	 */
	protected void processStub(UcmEnvironment env, NodeConnection source, Stub stub) throws TraversalException {
		boolean b = false;
		// TODO: Semantic variation: All true branches? First only? Error if multiple true? If multiple, in sequence or parallel?

		if (source != null) {

			Vector toVisit = new Vector();
			for (Iterator iter = stub.getBindings().iterator(); iter.hasNext();) {
				PluginBinding binding = (PluginBinding) iter.next();

				try {
					Object result = ScenarioUtils.evaluate(binding.getPrecondition(), env);
					if (Boolean.TRUE.equals(result)) {

						for (Iterator iterator = binding.getIn().iterator(); iterator.hasNext();) {
							InBinding inb = (InBinding) iterator.next();
							if (inb.getStubEntry()==source)
								toVisit.add(inb);
						}
						if (binding.getIn().size() == 0)
							_warnings
									.add(new TraversalWarning(
											Messages.getString("DefaultScenarioTraversal.NoBindingsDefined") + stub.getName() + Messages.getString("DefaultScenarioTraversal.OpenParenthesis") + stub.getId() + Messages.getString("DefaultScenarioTraversal.CloseParenthesisArrow") //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
													+ binding.getPlugin().getName()
													+ Messages.getString("DefaultScenarioTraversal.OpenParenthesis") + binding.getPlugin().getId() + Messages.getString("DefaultScenarioTraversal.CloseParenthesisQuote"), stub, IMarker.SEVERITY_ERROR)); //$NON-NLS-1$ //$NON-NLS-2$
					}
				} catch (IllegalArgumentException e) {
					//throw new TraversalException(e.getMessage(), e);
					_warnings.add(new TraversalWarning(e.getMessage(), stub, IMarker.SEVERITY_ERROR));
				}
			}

			if (toVisit.size() == 0) {
				// TODO: semantic variation : no plugins, what do we do?
				// if we don't find any valid plugins, only follow first out if it exists.
				if (stub.getPred().size() == 1 && stub.getSucc().size() == 1 && stub.getBindings().size() == 0) {
					NodeConnection nc = (NodeConnection) stub.getSucc().get(0);
					_traversalData.visitNodeConnection(nc);
					_warnings
							.add(new TraversalWarning(
									Messages.getString("DefaultScenarioTraversal.NoPluginBindingForStub") + stub.getName() + Messages.getString("DefaultScenarioTraversal.OpenParenthesis") + stub.getId() + Messages.getString("DefaultScenarioTraversal.UsingDefaultPlugin"), stub, IMarker.SEVERITY_INFO)); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
				} else if (ScenarioTraversalPreferences.getIsPatientOnPreconditions()) {
					_traversalData.addToWaitingList(stub);
				} else
					_warnings
							.add(new TraversalWarning(
									Messages.getString("DefaultScenarioTraversal.UnableToNavigateToPluginFromStub") + stub.getName() + Messages.getString("DefaultScenarioTraversal.OpenParenthesis") + stub.getId() + Messages.getString("DefaultScenarioTraversal.CloseParenthesisAndQuote"), stub, IMarker.SEVERITY_ERROR)); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
			} else {
				if (toVisit.size() > 1) {
					if (ScenarioTraversalPreferences.getIsDeterministic())
						_warnings
								.add(new TraversalWarning(
										Messages.getString("DefaultScenarioTraversal.TraversalHasMultipleAlternativesAtStub") + stub.getName() + Messages.getString("DefaultScenarioTraversal.OpenParenthesis") + stub.getId() + Messages.getString("DefaultScenarioTraversal.TakingFirstOptionToRemainDeterministic"), stub, IMarker.SEVERITY_ERROR)); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
					else
						_warnings
								.add(new TraversalWarning(
										Messages.getString("DefaultScenarioTraversal.TraversalHasMultipleAlternativesAtStub") + stub.getName() + Messages.getString("DefaultScenarioTraversal.OpenParenthesis") + stub.getId() + Messages.getString("DefaultScenarioTraversal.TakingAnyOptionNonDeterministic"), stub, IMarker.SEVERITY_INFO)); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
				}
				InBinding inb = null;

				if (ScenarioTraversalPreferences.getIsDeterministic())
					inb = (InBinding) toVisit.get(0);
				else {
					int i = (int) Math.round(Math.random() * (toVisit.size() - 1));
					inb = (InBinding) toVisit.get(i);

				}
				PluginBinding binding = inb.getBinding();

				_traversalData.incrementHitCount(inb);
				_traversalData.incrementHitCount(inb.getBinding());
				if (inb.getStartPoint() != null) {
					if (!_traversalData.getCurrentContext().contains(binding))
						_traversalData.getCurrentContext().add(binding);
					_traversalData.pushPathNode(inb.getStartPoint(), false);
				}
			}
		}
	}

	/**
	 * Processes a waiting place or timer (which is a waiting place)
	 * 
	 * @param env
	 * @param pn
	 * @throws TraversalException
	 */
	protected void processWaitingPlaceAndTimer(UcmEnvironment env, WaitingPlace pn) throws TraversalException {
		// only one out.
		NodeConnection nc = (NodeConnection) pn.getSucc().get(0);
		try {
			Object result = ScenarioUtils.evaluate(nc.getCondition(), env);
			// not using default behaviour. want to make sure we are blocked
			if (nc.getCondition()==null) result=Boolean.FALSE;
			
			if (Boolean.TRUE.equals(result)) {
				_traversalData.visitNodeConnection(nc);
			} else {
				
				if (pn instanceof Timer &&  pn.getSucc().size()==2) {
					
					//TODO: Semantic Variation. What do we do if both conditions are true at the same time?
					nc =  (NodeConnection) pn.getSucc().get(1);
					result = ScenarioUtils.evaluate(nc.getCondition(), env);
					// not using default behaviour. want to make sure we are blocked
					if (nc.getCondition()==null) result=Boolean.FALSE;

					// if we can take the timeout path
					if (Boolean.TRUE.equals(result))
						_traversalData.visitNodeConnection(nc);
					else
						_traversalData.addToWaitingList(pn);
						
				}
				else
					_traversalData.addToWaitingList(pn);
			}
		} catch (IllegalArgumentException e) {
			//throw new TraversalException(e.getMessage(), e);
			_warnings.add(new TraversalWarning(e.getMessage(), pn, IMarker.SEVERITY_ERROR));
		}
	}


	

	/**
	 * Initialize using {@link QDefaultScenarioTraversal} , run {@link #process(UcmEnvironment, Vector, Vector)} and prepare {@link RTraversalSequence}
	 * 
	 * @see seg.jUCMNav.model.util.modelexplore.AbstractQueryProcessor#runImpl(seg.jUCMNav.model.util.modelexplore.QueryRequest)
	 */
	public QueryResponse runImpl(QueryRequest q) {
		_traversalData = new DefaultScenarioTraversalDataStructure();
		_error = null;
		_warnings = new Vector();

		if (((QDefaultScenarioTraversal) q).getStartPathNodes() != null) {
			Vector startPoints = ((QDefaultScenarioTraversal) q).getStartPathNodes();
			Vector endPoints = ((QDefaultScenarioTraversal) q).getEndPathNodes();
			process(((QDefaultScenarioTraversal) q).getEnvironment(), startPoints, endPoints);
		}

		// Return a response containing the visited node list
		RTraversalSequence r = new RTraversalSequence();
		r.setVisited(_traversalData.getVisited());
		r.setError(_error);
		r.setWarnings(_warnings);
		r.setResults(_traversalData.getResults());
		return r;
	}



	/**
	 * Helper method that tests to see if a condition evaluates to what is expected.
	 * 
	 * @param env
	 *            the environment in which the condition is to be evaluated
	 * @param cond
	 *            the condition
	 * @param expected
	 *            the expected result (Boolean.TRUE or Boolean.FALSE)
	 * @param errorMessage
	 *            the error message to be added to the warnings if is not what is expected
	 * @return true if is what is expected, false otherwise.
	 * @throws TraversalException
	 *             if a parsing error occurs
	 */
	protected boolean testCondition(UcmEnvironment env, Condition cond, Boolean expected, String errorMessage) throws TraversalException {
		try {
			Object result = ScenarioUtils.evaluate(cond, env);
			if (!expected.equals(result)) {
				TraversalWarning warning = new TraversalWarning(errorMessage, cond.eContainer(), IMarker.SEVERITY_ERROR);
				warning.setCondition(cond);
				_warnings.add(warning);
				return false;
			}
		} catch (IllegalArgumentException e) {
			//throw new TraversalException(e.getMessage(), e);
			_warnings.add(new TraversalWarning(e.getMessage(), cond, IMarker.SEVERITY_ERROR));
		}

		return true;
	}


	/**
	 * Verify that the expected end points match the reached end points.
	 * 
	 * @param expectedEndPoints
	 *            Vector of ScenarioEndPoints or EndPoints
	 * @param reachedEndPoints
	 *            Vector of EndPoints
	 */
	protected void verifyEndPoints(Vector expectedEndPoints, Vector reachedEndPoints) {
		for (Iterator iter = expectedEndPoints.iterator(); iter.hasNext();) {
			EObject obj = (EObject) iter.next();
			EndPoint pt = null;
			if (obj instanceof EndPoint)
				pt = (EndPoint) obj;
			else if (obj instanceof ScenarioEndPoint) {
				if (((ScenarioEndPoint) obj).isEnabled())
					pt = ((ScenarioEndPoint) obj).getEndPoint();
			}

			if (pt != null && !reachedEndPoints.contains(pt)) {
				_warnings.add(new TraversalWarning(Messages.getString("DefaultScenarioTraversal.ScenariosShouldHaveReachedEndPoint") + pt.toString(),pt,IMarker.SEVERITY_ERROR)); //$NON-NLS-1$
			} else {
				// so that we can find multiple instances
				reachedEndPoints.remove(pt);
			}
		}
	}

}