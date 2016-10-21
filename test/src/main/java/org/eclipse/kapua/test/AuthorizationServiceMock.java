/*******************************************************************************
 * Copyright (c) 2011, 2016 Eurotech and/or its affiliates and others
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Eurotech - initial API and implementation
 *
 *******************************************************************************/
package org.eclipse.kapua.test;

import org.eclipse.kapua.KapuaException;
import org.eclipse.kapua.locator.guice.TestService;
import org.eclipse.kapua.service.authentication.AuthenticationService;
import org.eclipse.kapua.service.authorization.AuthorizationService;
import org.eclipse.kapua.service.authorization.permission.Permission;

@TestService
public class AuthorizationServiceMock extends TestCaseMock implements AuthorizationService {

	@Override
	public boolean isPermitted(Permission permission) throws KapuaException {
		boolean response = false;
		String testCaseId = getTestCaseId();

		// Default implementation
		if (testCaseId == null) {
			// Always true
			return true;
		}

		// Proxy response to test case specific implementation
		Class<?> clazz = null;
		AuthorizationService mockAuthor = null;
		try {
			clazz = Class.forName("org.eclipse.kapua.service.authorization.mock." + testCaseId);
			mockAuthor = (AuthorizationService) clazz.newInstance();
			response = mockAuthor.isPermitted(permission);
		} catch (ClassNotFoundException | InstantiationException | IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return response;
	}

	@Override
	public void checkPermission(Permission permission) throws KapuaException {

		String testCaseId = getTestCaseId();

		// Default implementation
		if (testCaseId == null) {
			// Never throw
			return;
		}

		// Proxy response to test case specific implementation
		Class<?> clazz = null;
		AuthorizationService mockAuthor = null;
		try {
			clazz = Class.forName("org.eclipse.kapua.service.authorization.mock." + testCaseId);
			mockAuthor = (AuthorizationService) clazz.newInstance();
			mockAuthor.checkPermission(permission);
		} catch (ClassNotFoundException | InstantiationException | IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

}
