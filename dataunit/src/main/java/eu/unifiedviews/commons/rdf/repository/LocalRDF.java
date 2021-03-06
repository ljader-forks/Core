package eu.unifiedviews.commons.rdf.repository;

import eu.unifiedviews.commons.dataunit.core.ConnectionSource;
import eu.unifiedviews.dataunit.DataUnitException;
import org.apache.commons.io.FileUtils;
import org.eclipse.rdf4j.repository.Repository;
import org.eclipse.rdf4j.repository.RepositoryException;
import org.eclipse.rdf4j.repository.sail.SailRepository;
import org.eclipse.rdf4j.sail.nativerdf.NativeStore;

import java.io.File;

/**
 *
 * @author Škoda Petr
 */
class LocalRDF implements ManagableRepository {

    private final Repository repository;

    private final File repositoryDirectory;

    /**
     * 
     * @param repositoryPath Unique path for each pipeline's repository.
     * @throws DataUnitException
     */
    public LocalRDF(String repositoryPath) throws RDFException {
        repositoryDirectory = new File(repositoryPath);
        if (!repositoryDirectory.isDirectory() && !repositoryDirectory.mkdirs()) {
            throw new RDFException("Could not create repository directory.");
        }
        repository = new SailRepository(new NativeStore(repositoryDirectory));
        try {
            repository.initialize();
        } catch (RepositoryException ex) {
            throw new RDFException("Could not initialize repository.", ex);
        }        
    }

    @Override
    public ConnectionSource getConnectionSource() {
        return new ConnectionSourceImpl(repository, false, Type.LOCAL_RDF);
    }

    @Override
    public void release() throws RDFException {
        try {
            repository.shutDown();
        } catch (RepositoryException ex) {
            throw new RDFException("Could not shutdown repository.", ex);
        }
    }

    @Override
    public void delete() throws RDFException {
        release();
        // Delete storage data directory.
        FileUtils.deleteQuietly(repositoryDirectory);
    }

    @Override
    public Type getRepositoryType() {
        return Type.LOCAL_RDF;
    }

}
