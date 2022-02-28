package com.amf.pocsecured.di;

import com.amf.pocsecured.MainApplicationTest;
import com.amf.pocsecured.ext.MicrosoftLoginHelper;
import com.amf.pocsecured.mock.MockedWebServerData;

import org.jetbrains.annotations.NotNull;

import javax.inject.Singleton;

import dagger.BindsInstance;
import dagger.Component;
import dagger.android.AndroidInjector;
import dagger.android.support.AndroidSupportInjectionModule;

/**
 * @author youssefamrani
 */
@Singleton //
@Component(modules = { //
		AndroidSupportInjectionModule.class, //
		AppModuleTest.class, //
		ActivityBindingModule.class, //
		ViewModelModule.class, //
		NetworkModuleTest.class //
}) //
public interface AppComponentTest extends AndroidInjector<MainApplicationTest>
{
	@Component.Factory public interface Factory
	{
		@NotNull AppComponentTest create(@BindsInstance @NotNull MainApplicationTest mainApplicationTest, //
										 @BindsInstance @NotNull MicrosoftLoginHelper microsoftLoginHelper, //
										 @BindsInstance @NotNull MockedWebServerData mockedWebServerData);
	}
}
