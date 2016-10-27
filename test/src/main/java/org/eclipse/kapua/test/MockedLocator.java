package org.eclipse.kapua.test;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.kapua.locator.KapuaLocator;
import org.eclipse.kapua.model.KapuaObjectFactory;
import org.eclipse.kapua.service.KapuaService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MockedLocator extends KapuaLocator {

	private static Logger logger = LoggerFactory.getLogger(MockedLocator.class);

	private Map<Class, KapuaService> serviceMap = new HashMap<>();

	private Map<Class, KapuaObjectFactory> factoryMap = new HashMap<>();

	public void setMockedService(Class clazz, KapuaService service) {
		
		serviceMap.put(clazz, service);
	}

	public void setMockedFactory(Class clazz, KapuaObjectFactory factory) {

		factoryMap.put(clazz, factory);
	}
	
	private KapuaService getMockedService(Class clazz) {
		
		return serviceMap.get(clazz);
	}
	
	private KapuaObjectFactory getMockedFactory(Class clazz) {
		
		return factoryMap.get(clazz);
	}

	@Override
	public <S extends KapuaService> S getService(Class<S> serviceClass) {

		logger.info("Geting mocked service {} from MockedLocator", serviceClass.getName());

		return (S) getMockedService(serviceClass);
	}

	@Override
	public <F extends KapuaObjectFactory> F getFactory(Class<F> factoryClass) {

		logger.info("Geting mocked factory {} from MockedLocator", factoryClass.getName());

		return (F) getMockedFactory(factoryClass);
	}

}
