package org.eclipse.kapua.service.user.internal.setting;

import static org.junit.Assert.assertEquals;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

import org.eclipse.kapua.KapuaException;
import org.eclipse.kapua.test.annotations.TestCase;
import org.junit.Test;

public class KapuaUserSettingTest {

    @Test
    @TestCase(caseId = "KAPUA_0012")
    public void getUserSettingInstance() throws Exception {
        KapuaUserSetting userSettings = KapuaUserSetting.getInstance();

        String user = userSettings.getString(KapuaUserSettingKeys.USER_KEY);

        assertEquals("kapua-sys", user);
    }

}
