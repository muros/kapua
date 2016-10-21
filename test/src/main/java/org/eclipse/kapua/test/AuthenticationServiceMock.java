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
import org.eclipse.kapua.commons.security.KapuaSecurityUtils;
import org.eclipse.kapua.commons.security.KapuaSession;
import org.eclipse.kapua.locator.KapuaLocator;
import org.eclipse.kapua.locator.guice.TestService;
import org.eclipse.kapua.service.authentication.AccessToken;
import org.eclipse.kapua.service.authentication.AuthenticationCredentials;
import org.eclipse.kapua.service.authentication.AuthenticationService;
import org.eclipse.kapua.service.user.User;
import org.eclipse.kapua.service.user.UserService;

@TestService
public class AuthenticationServiceMock extends TestCaseMock implements AuthenticationService {

	public AuthenticationServiceMock() {
	}

	@Override
	public AccessToken login(AuthenticationCredentials authenticationToken) throws KapuaException {
		AccessToken response = null;
		String testCaseId = getTestCaseId();
		
		// Default implementation
		if (testCaseId == null) {
			if (!(authenticationToken instanceof UsernamePasswordTokenMock))
				throw KapuaException.internalError("Unmanaged credentials type");

			UsernamePasswordTokenMock usrPwdTokenMock = (UsernamePasswordTokenMock) authenticationToken;

			KapuaLocator serviceLocator = KapuaLocator.getInstance();
			UserService userService = serviceLocator.getService(UserService.class);
			User user = userService.findByName(usrPwdTokenMock.getUsername());

			KapuaSession kapuaSession = new KapuaSession(null, null, user.getScopeId(), user.getId(), user.getName());
			KapuaSecurityUtils.setSession(kapuaSession);
			// TODO Auto-generated method stub
			return null;
		}
		
		// Proxy response to test case specific implementation
		Class<?> clazz = null;
		AuthenticationService mockAuth = null;
		try {
			clazz = Class.forName("org.eclipse.kapua.service.authentication.mock." + testCaseId);
			mockAuth = (AuthenticationService) clazz.newInstance();
			response = mockAuth.login(authenticationToken);
		} catch (ClassNotFoundException | InstantiationException | IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return response;
	}

	@Override
	public void logout() throws KapuaException {
		// TODO Auto-generated method stub
		KapuaSecurityUtils.clearSession();
	}

	@Override
	public AccessToken getToken(String tokenId) throws KapuaException {
		// TODO Auto-generated method stub
		return null;
	}

}
