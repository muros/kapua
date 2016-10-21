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

public class KAPUA_0001 implements AuthenticationService {

	@Override
	public AccessToken login(AuthenticationCredentials authenticationToken) throws KapuaException {
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
