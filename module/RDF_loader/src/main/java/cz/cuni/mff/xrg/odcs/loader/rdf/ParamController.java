package cz.cuni.mff.xrg.odcs.loader.rdf;

import java.net.URL;
import java.util.List;

import org.apache.log4j.Logger;

import cz.cuni.mff.xrg.odcs.commons.dpu.DPUException;

/**
 * Responsible for test/controll parameters needed for method RDF.
 * 
 * @author Jiri Tomes
 */
public class ParamController {

    private static Logger logger = Logger.getLogger(ParamController.class);

    /**
     * Test if given param is null or not.
     * 
     * @param param
     *            Object you can test
     * @param message
     *            String message that could be logged if param is null.
     * @throws RDFException
     *             if given param is null.
     */
    public static void testNullParameter(Object param, String message) throws DPUException {
        if (param == null) {
            logger.debug(message);
            throw new DPUException(message);
        }
    }

    /**
     * Test if given param is empty or not.
     * 
     * @param param
     *            Object you can test
     * @param message
     *            String message that could be logged if param is empty.
     * @throws RDFException
     *             if given param is empty.
     */
    public static void testEmptyParameter(Object param, String message) throws DPUException {

        if (param != null) {
            boolean isEmpty = param.toString().isEmpty();

            if (param instanceof List) {
                isEmpty = ((List) param).isEmpty();
            }

            if (isEmpty) {
                logger.debug(message);
                throw new DPUException(message);
            }
        }

    }

    /**
     * Test if given param is positive long number or not.
     * 
     * @param param
     *            Value you can test
     * @param message
     *            String message that could be logged if param is not
     *            positive number.
     * @throws RDFException
     *             if given param is not positive number.
     */
    public static void testPositiveParameter(long param, String message) throws DPUException {
        if (param <= 0) {
            logger.debug(message);
            throw new DPUException(message);
        }
    }

    /**
     * Test if given param is valid URL or not.
     * 
     * @param endpointURL
     *            Object you can test
     * @throws RDFException
     *             if given URL is null, started with "http://" or
     *             contains white spaces.
     */
    public static void testEndpointSyntax(String endpointURL) throws DPUException {

        String message = null;

        if (endpointURL != null) {
            final String endpointName = endpointURL.toLowerCase();

            if (!endpointName.startsWith("http://")) {
                message = "Endpoint url name have to started with prefix \"http://\".";
            } else if (endpointName.contains(" ")) {
                message = "Endpoint url constains white spaces";
            }
            if (message != null) {
                logger.debug(message);
                throw new DPUException(message);
            }
        } else {
            message = "Mandatory URL path is null. SPARQL Endpoint URL must be specified";
            logger.debug(message);
            throw new DPUException(message);

        }
    }
}
