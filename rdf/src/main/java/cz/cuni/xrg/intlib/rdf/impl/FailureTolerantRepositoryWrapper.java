package cz.cuni.xrg.intlib.rdf.impl;

import cz.cuni.xrg.intlib.rdf.exceptions.RDFRepositoryException;
import java.io.File;
import java.util.Properties;
import org.openrdf.model.ValueFactory;
import org.openrdf.repository.Repository;
import org.openrdf.repository.RepositoryConnection;
import org.openrdf.repository.RepositoryException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Jan Vojt
 */
public class FailureTolerantRepositoryWrapper implements Repository {
	
	private static final Logger LOG = LoggerFactory.getLogger(FailureTolerantRepositoryWrapper.class);
	
	private static final String RETRIES_KEY = "virtuoso.retries";
	private static final String WAIT_KEY = "virtuoso.wait";
	
	/**
	 * Wrapped RDF repository.
	 */
	private Repository repository;
	
	/**
	 * Number of retries before giving up on reconnecting with db. Zero means
	 * immediate failure, -1 means never give up and keep trying.
	 */
	private int retries = -1;
	
	/**
	 * Number of milliseconds to wait before the next retrial.
	 */
	private int wait = 2000;

	/**
	 * Wrapper constructor with configuration.
	 * 
	 * @param repository to wrap
	 */
	public FailureTolerantRepositoryWrapper(Repository repository, Properties properties) {
		this(repository);
		configure(properties);
	}

	/**
	 * Wrapper constructor using default configuration.
	 * Does not log a notice.
	 * 
	 * @param repository to wrap
	 */
	public FailureTolerantRepositoryWrapper(Repository repository) {
		this.repository = repository;
	}
	
	@Override
	public void setDataDir(File dataDir) {
		repository.setDataDir(dataDir);
	}

	@Override
	public File getDataDir() {
		return repository.getDataDir();
	}

	@Override
	public void initialize() throws RDFRepositoryException {
		int attempts = 0;
		while (true) try {
			attempts++;
			repository.initialize();
			return;
		} catch (RepositoryException ex) {
			handleRetries(attempts, ex);
		}
	}

	@Override
	public boolean isInitialized() {
		return repository.isInitialized();
	}

	@Override
	public void shutDown() throws RepositoryException {
		int attempts = 0;
		while (true) try {
			attempts++;
			repository.shutDown();
			return;
		} catch (RepositoryException ex) {
			handleRetries(attempts, ex);
		}
	}

	@Override
	public boolean isWritable() throws RepositoryException {
		int attempts = 0;
		while (true) try {
			attempts++;
			return repository.isWritable();
		} catch (RepositoryException ex) {
			handleRetries(attempts, ex);
		}
	}

	@Override
	public RepositoryConnection getConnection() throws RepositoryException {
		return repository.getConnection();
	}

	@Override
	public ValueFactory getValueFactory() {
		return repository.getValueFactory();
	}
	
	/**
	 * Configures reconnection trials.
	 * 
	 * @throws NumberFormatException if configuration values are specified, but
	 *								 invalid
	 */
	private void configure(Properties properties) {
		String sRetries = properties.getProperty(RETRIES_KEY);
		if (sRetries == null) {
			LOG.info("Missing config property {}, using default value {}.", RETRIES_KEY, retries);
		} else {
			retries = Integer.parseInt(sRetries);
		}

		String sWait = properties.getProperty(WAIT_KEY);
		if (sWait == null) {
			LOG.info("Missing config property {}, using default value {}.", WAIT_KEY, wait);
		} else {
			wait = Integer.parseInt(sWait);
		}
	}
	
	/**
	 * Logic for deciding whether to continue db reconnect attempts. If we
	 * decide to stop trying, exception is thrown.
	 * 
	 * TODO create a handler interface and implementation and merge common logic
	 *		with PipelineFacade#handleRetries() in backend.
	 * 
	 * @param attempts number of attempts so far
	 * @param ex exception to be thrown in case we give up
	 */
	private void handleRetries(int attempts, RepositoryException ex) {
		
		LOG.warn(String.format("Database is down after %d attempts.", attempts));
		if (attempts == 1) {
			// TODO send notification after first error only
			//notify(ex);
		}
		
		boolean loop = true;
		if (retries >= 0) {
			loop = attempts <= retries;
		}
		if (loop && wait > 0) {
			try {
				Thread.sleep(wait);
			} catch (InterruptedException e) {
				LOG.error("Thread interrupted while sleeping.", e);
			}
		} else if (!loop) {
			LOG.error(String.format("Giving up on database after %d retries.", attempts));
			throw new RDFRepositoryException(ex);
		}
	}
	
}