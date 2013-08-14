package cz.cuni.xrg.commons.module.config;

import org.junit.Before;
import org.junit.Test;

import cz.cuni.xrg.intlib.commons.configuration.ConfigException;
import cz.cuni.xrg.intlib.commons.configuration.DPUConfigObject;
import cz.cuni.xrg.intlib.commons.module.config.ConfigWrap;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

/**
 * Test suite for {@link ConfigWrap} class.
 * 
 * @author Petyr
 *
 */
public class ConfigWrapTest {

	@SuppressWarnings("rawtypes")
	private ConfigWrap configWrap;
	
	private DPUConfigObject configObject;
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Before
	public void setup() {
		configObject = mock(DPUConfigObject.class, withSettings().serializable());
		configWrap = new ConfigWrap(configObject);
	}
	
	@Test
	public void deserialisationNull() throws ConfigException {
		DPUConfigObject newObject =	configWrap.deserialize(null);
		assertNull(newObject);
	}

	@SuppressWarnings("unchecked")
	@Test
	public void serialisationNull() throws ConfigException {
		byte[] serialized = configWrap.serialize(null);
		assertNull(serialized);
	}
	
	@SuppressWarnings("unchecked")
	@Test
	public void serialisation() throws ConfigException {
		byte[] serialized = configWrap.serialize(configObject);
	
		DPUConfigObject newObject = 
				configWrap.deserialize(serialized);
		
		assertNotNull(newObject);
		assertNotSame(configObject, newObject);
	}

	@Test
	public void newInstace() {
		DPUConfigObject newObject = configWrap.createInstance();
		assertNotNull(newObject);
	}	
	
}