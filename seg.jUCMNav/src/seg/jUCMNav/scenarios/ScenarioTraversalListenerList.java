package seg.jUCMNav.scenarios;

import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import seg.jUCMNav.scenarios.algorithmInterfaces.ITraversalListener;
import seg.jUCMNav.scenarios.model.TraversalVisit;
import seg.jUCMNav.scenarios.model.TraversalWarning;
import seg.jUCMNav.scenarios.model.UcmEnvironment;
import ucm.map.InBinding;
import ucm.map.OutBinding;
import ucm.scenario.ScenarioDef;
import urncore.Condition;

/**
 * Contains a list of ITraversalListeners and the boilerplate code to notify them all.
 * 
 * 
 * @author jkealey
 * 
 */
public class ScenarioTraversalListenerList implements ITraversalListener {

	protected Vector _listeners;
	protected Vector _warnings;

	public ScenarioTraversalListenerList(Vector listeners, Vector warnings) {
		this._listeners = listeners;
		this._warnings = warnings;
	}

	public void codeExecuted(TraversalVisit visit, String code) {
		for (Iterator iter = _listeners.iterator(); iter.hasNext();) {
			ITraversalListener listener = (ITraversalListener) iter.next();
			try {
				listener.codeExecuted(visit, code);

			} catch (Exception ex) {
				_warnings.add(new TraversalWarning(ex.toString()));
				ex.printStackTrace();
			}
		}
		
	}

	public void conditionEvaluated(TraversalVisit visit, Condition condition, boolean result) {
		for (Iterator iter = _listeners.iterator(); iter.hasNext();) {
			ITraversalListener listener = (ITraversalListener) iter.next();
			try {
				listener.conditionEvaluated(visit, condition, result);

			} catch (Exception ex) {
				_warnings.add(new TraversalWarning(ex.toString()));
				ex.printStackTrace();
			}
		}

	}

	public void drillDown(TraversalVisit visit, InBinding inb) {
		for (Iterator iter = _listeners.iterator(); iter.hasNext();) {
			ITraversalListener listener = (ITraversalListener) iter.next();
			try {
				listener.drillDown(visit, inb);

			} catch (Exception ex) {
				_warnings.add(new TraversalWarning(ex.toString()));
				ex.printStackTrace();
			}
		}
	}

	public void drillUp(TraversalVisit visit, OutBinding outb) {
		for (Iterator iter = _listeners.iterator(); iter.hasNext();) {
			ITraversalListener listener = (ITraversalListener) iter.next();
			try {
				listener.drillUp(visit, outb);

			} catch (Exception ex) {
				_warnings.add(new TraversalWarning(ex.toString()));
				ex.printStackTrace();
			}
		}
	}

	public void leftWaitingPlace(TraversalVisit visit, boolean becauseOfCondition) {
		for (Iterator iter = _listeners.iterator(); iter.hasNext();) {
			ITraversalListener listener = (ITraversalListener) iter.next();
			try {
				listener.leftWaitingPlace(visit, becauseOfCondition);

			} catch (Exception ex) {
				_warnings.add(new TraversalWarning(ex.toString()));
				ex.printStackTrace();
			}
		}		
	}

	
	public void newThreadStarted(TraversalVisit visit) {
		for (Iterator iter = _listeners.iterator(); iter.hasNext();) {
			ITraversalListener listener = (ITraversalListener) iter.next();
			try {
				listener.newThreadStarted(visit);

			} catch (Exception ex) {
				_warnings.add(new TraversalWarning(ex.toString()));
				ex.printStackTrace();
			}
		}

	}

	public void pathNodeAborted(TraversalVisit visit) {
		for (Iterator iter = _listeners.iterator(); iter.hasNext();) {
			ITraversalListener listener = (ITraversalListener) iter.next();
			try {
				listener.pathNodeAborted(visit);

			} catch (Exception ex) {
				_warnings.add(new TraversalWarning(ex.toString()));
				ex.printStackTrace();
			}
		}

	}

	public void pathNodeAttempted(TraversalVisit visit) {
		for (Iterator iter = _listeners.iterator(); iter.hasNext();) {
			ITraversalListener listener = (ITraversalListener) iter.next();
			try {
				listener.pathNodeAttempted(visit);

			} catch (Exception ex) {
				_warnings.add(new TraversalWarning(ex.toString()));
				ex.printStackTrace();
			}
		}

	}

	public void pathNodeBlocked(TraversalVisit visit) {
		for (Iterator iter = _listeners.iterator(); iter.hasNext();) {
			ITraversalListener listener = (ITraversalListener) iter.next();
			try {
				listener.pathNodeBlocked(visit);

			} catch (Exception ex) {
				_warnings.add(new TraversalWarning(ex.toString()));
				ex.printStackTrace();
			}
		}
	}

	public void pathNodeUnblocked(TraversalVisit visit) {
		for (Iterator iter = _listeners.iterator(); iter.hasNext();) {
			ITraversalListener listener = (ITraversalListener) iter.next();
			try {
				listener.pathNodeUnblocked(visit);

			} catch (Exception ex) {
				_warnings.add(new TraversalWarning(ex.toString()));
				ex.printStackTrace();
			}
		}
	}
	

	public void pathNodeVisited(TraversalVisit visit) {
		for (Iterator iter = _listeners.iterator(); iter.hasNext();) {
			ITraversalListener listener = (ITraversalListener) iter.next();
			try {
				listener.pathNodeVisited(visit);

			} catch (Exception ex) {
				_warnings.add(new TraversalWarning(ex.toString()));
				ex.printStackTrace();
			}
		}

	}

	public void threadDied(int threadId) {
		for (Iterator iter = _listeners.iterator(); iter.hasNext();) {
			ITraversalListener listener = (ITraversalListener) iter.next();
			try {
				listener.threadDied(threadId);

			} catch (Exception ex) {
				_warnings.add(new TraversalWarning(ex.toString()));
				ex.printStackTrace();
			}
		}
	}

	public void threadsMerged(List oldThreadIDs, int newThreadID) {
		for (Iterator iter = _listeners.iterator(); iter.hasNext();) {
			ITraversalListener listener = (ITraversalListener) iter.next();
			try {
				listener.threadsMerged(oldThreadIDs, newThreadID);

			} catch (Exception ex) {
				_warnings.add(new TraversalWarning(ex.toString()));
				ex.printStackTrace();
			}
		}

	}

	public void threadSplit(int oldThreadID, List newThreadIDs) {
		for (Iterator iter = _listeners.iterator(); iter.hasNext();) {
			ITraversalListener listener = (ITraversalListener) iter.next();
			try {
				listener.threadSplit(oldThreadID, newThreadIDs);

			} catch (Exception ex) {
				_warnings.add(new TraversalWarning(ex.toString()));
				ex.printStackTrace();
			}
		}

	}

	public void timerTimeout(TraversalVisit visit, boolean becauseOfCondition) {
		for (Iterator iter = _listeners.iterator(); iter.hasNext();) {
			ITraversalListener listener = (ITraversalListener) iter.next();
			try {
				listener.timerTimeout(visit,  becauseOfCondition);

			} catch (Exception ex) {
				_warnings.add(new TraversalWarning(ex.toString()));
				ex.printStackTrace();
			}
		}

	}

	public void traversalEnded() {
		for (Iterator iter = _listeners.iterator(); iter.hasNext();) {
			ITraversalListener listener = (ITraversalListener) iter.next();
			try {
				listener.traversalEnded();

			} catch (Exception ex) {
				_warnings.add(new TraversalWarning(ex.toString()));
				ex.printStackTrace();
			}
		}

	}

	public void traversalEnded(UcmEnvironment env, ScenarioDef scenario) {
		for (Iterator iter = _listeners.iterator(); iter.hasNext();) {
			ITraversalListener listener = (ITraversalListener) iter.next();
			try {
				listener.traversalEnded(env, scenario);

			} catch (Exception ex) {
				_warnings.add(new TraversalWarning(ex.toString()));
				ex.printStackTrace();
			}
		}

	}

	public void traversalStarted(UcmEnvironment env, ScenarioDef scenario) {
		for (Iterator iter = _listeners.iterator(); iter.hasNext();) {
			ITraversalListener listener = (ITraversalListener) iter.next();
			try {
				listener.traversalStarted(env, scenario);

			} catch (Exception ex) {
				_warnings.add(new TraversalWarning(ex.toString()));
				ex.printStackTrace();
			}
		}

	}



}