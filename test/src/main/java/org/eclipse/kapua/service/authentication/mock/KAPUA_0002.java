package org.eclipse.kapua.service.authentication.mock;

import org.eclipse.kapua.KapuaException;
import org.eclipse.kapua.commons.security.KapuaSecurityUtils;
import org.eclipse.kapua.commons.security.KapuaSession;
import org.eclipse.kapua.locator.KapuaLocator;
import org.eclipse.kapua.service.authentication.AccessToken;
import org.eclipse.kapua.service.authentication.AuthenticationCredentials;
import org.eclipse.kapua.service.authentication.AuthenticationService;
import org.eclipse.kapua.service.user.User;
import org.eclipse.kapua.service.user.UserService;
import org.eclipse.kapua.test.UsernamePasswordTokenMock;

public class KAPUA_0002 implements AuthenticationService {

	@Override
	public AccessToken login(AuthenticationCredentials authenticationToken) throws KapuaException {
		
			throw KapuaException.internalError("Unmanaged credentials type");
	}

	@Override
	public void logout() throws KapuaException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public AccessToken getToken(String tokenId) throws KapuaException {
		// TODO Auto-generated method stub
		return null;
	}

}
