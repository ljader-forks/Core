package cz.cuni.xrg.intlib.frontend;

import com.vaadin.server.VaadinServletService;
import com.vaadin.server.communication.PushHandler;
import org.atmosphere.cpr.AtmosphereRequest;
import org.atmosphere.cpr.AtmosphereResource;
import org.springframework.security.core.context.SecurityContextHolder;

/**
 *
 * @author Jan Vojt
 */
public class FilterablePushHandler extends PushHandler {
   
    public static final String WEB_SOCKET_PUSH_REROUTED = "com.vaadin.server.communication.FilterablePushHandler.WEB_SOCKET_PUSH_REROUTED";
    public static final String ATMOSPHERE_RESOURCE = "com.vaadin.server.communication.FilterablePushHandler.ATMOSPHERE_RESOURCE";
   
    public FilterablePushHandler(VaadinServletService service) {
        super(service);
    }

    @Override
    public void onRequest(AtmosphereResource resource) {

		// Hold the original request
		AtmosphereRequest req = resource.getRequest();
		RequestHolder.setRequest(req);
		SecurityContextHolder.setContext(SecurityContextHolder.createEmptyContext());
		
		// Do the business.
		super.onRequest(resource);
		
		// cleanup
		RequestHolder.clean();
		SecurityContextHolder.clearContext();
		
    }
   
}
