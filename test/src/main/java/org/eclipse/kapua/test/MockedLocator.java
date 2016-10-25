package org.eclipse.kapua.test;

import org.eclipse.kapua.locator.KapuaLocator;
import org.eclipse.kapua.model.KapuaObjectFactory;
import org.eclipse.kapua.service.KapuaService;

public class MockedLocator extends KapuaLocator {

	@Override
	public <S extends KapuaService> S getService(Class<S> serviceClass) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public <F extends KapuaObjectFactory> F getFactory(Class<F> factoryClass) {
		// TODO Auto-generated method stub
		return null;
	}

}
