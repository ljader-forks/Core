package cz.cuni.xrg.intlib.frontend;

import com.vaadin.server.DeploymentConfiguration;
import com.vaadin.server.ServiceException;
import com.vaadin.server.VaadinServlet;
import com.vaadin.server.VaadinServletService;
import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.atmosphere.cpr.AtmosphereFramework;
import org.atmosphere.cpr.AtmosphereResource;
import org.atmosphere.cpr.DefaultBroadcasterFactory;
import org.springframework.security.core.context.SecurityContextHolder;

/**
 * Customized servlet implementation to provide access to original
 * {@link HttpServletRequest} across application.
 *
 * @see {@link RequestHolder}
 * @author Jan Vojt
 */
public class RequestHolderApplicationServlet extends VaadinServlet {
	
	
	/**
	 * The push handler.
	 */
    FilterablePushHandler handler;
   
    @Override
    protected VaadinServletService createServletService(
            DeploymentConfiguration deploymentConfiguration)
            throws ServiceException {
        VaadinServletService service = super.createServletService(deploymentConfiguration);
       
        final AtmosphereFramework framework = DefaultBroadcasterFactory.getDefault()
				.lookup("/*").getBroadcasterConfig().getAtmosphereConfig().framework();
       
        // replace the handler registered by vaadin with this one
        handler = new FilterablePushHandler(service);
        framework.addAtmosphereHandler("/*", handler);
       
        assert service.ensurePushAvailable() == true;
        return service;
    }

    @Override
    protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		// Store current HTTP request in thread-local, so we can access it later.
		RequestHolder.setRequest(request);
		// First clear the security context, as we need to load it from session.
		SecurityContextHolder.setContext(SecurityContextHolder.createEmptyContext());
		
        // The handler will sett this attribute prior to call the filterchain
		// (that will conclude with the servlet).
        Object p = request.getAttribute(FilterablePushHandler.WEB_SOCKET_PUSH_REROUTED);

        if(p != null && p.equals(true)) {
			//the handler also pass the AtmosphereResource
			AtmosphereResource resource = (AtmosphereResource) request
					.getAttribute(FilterablePushHandler.ATMOSPHERE_RESOURCE);
			handler.onRequest(resource);

			request.removeAttribute(
					FilterablePushHandler.WEB_SOCKET_PUSH_REROUTED);
			request.removeAttribute(FilterablePushHandler.ATMOSPHERE_RESOURCE);
		} else {
			// If this is not a "simulated" request from the push handler,
			// handle normally.
			super.service(request, response);
		}
		
		// We remove the request from the thread local, there's no reason
		// to keep it once the work is done. Next request might be serviced
		// by different thread, which will need to load security context from
		// the session anyway.
		RequestHolder.clean();
		SecurityContextHolder.clearContext();
    }
	
}
