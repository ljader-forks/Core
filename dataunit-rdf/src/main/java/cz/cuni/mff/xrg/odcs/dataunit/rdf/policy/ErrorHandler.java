package cz.cuni.mff.xrg.odcs.dataunit.rdf.policy;

/**
 * Error handler used to sanitize the problems that may occur during the
 * execution of methods in {@link RDFDataUnit}.
 */
public interface ErrorHandler {

	/**
	 * Called when connection failed.
	 *
	 * @param attempt
	 * @return True for next attempt.
	 */
	boolean retryConnection(int attempt);

	/**
	 * Called if the problem in data is found. Throw exception in order to
	 * terminate the operation.
	 *
	 * @throws Exception
	 */
	void invalidData() throws Exception;

	/**
	 * Called on conflict in data.
	 *
	 * @throws Exception
	 */
	void parseConflict() throws Exception;
}