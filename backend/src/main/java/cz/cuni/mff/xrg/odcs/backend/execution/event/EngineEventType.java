package cz.cuni.mff.xrg.odcs.backend.execution.event;

/**
 * Contains type of Engine's event. 
 * 
 * @see EngineEvent
 * @author Petyr
 *
 */
public enum EngineEventType {
	/**
	 * Ask Engine to check database and run scheduled pipelines.
	 */
	CHECK_DATABASE,
	/**
	 * Let engine do start up check. For example check for 
	 * running pipelines. 
	 */
	STARTUP
}