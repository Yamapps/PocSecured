package com.amf.pocsecured.di;

import com.amf.pocsecured.MainApplicationTest;
import com.amf.pocsecured.ext.MicrosoftLoginHelper;
import com.amf.pocsecured.mock.MockedWebServerData;

/**
 * @author youssefamrani
 */

public class DaggerTestBuilder
{
	public static AppComponentTest build(MainApplicationTest application, MicrosoftLoginHelper microsoftLoginHelper, MockedWebServerData mockedWebServerData)
	{
		return DaggerAppComponentTest.factory().create(application, microsoftLoginHelper, mockedWebServerData);
	}
}
