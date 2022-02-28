package com.amf.pocsecured.di;

import com.amf.pocsecured.MainApplication;

import javax.inject.Singleton;

import dagger.BindsInstance;
import dagger.Component;
import dagger.android.AndroidInjector;
import dagger.android.support.AndroidSupportInjectionModule;

/**
 * Main component of the app, created and persisted in the Application class.
 *
 * Whenever a new module is created, it should be added to the list of modules.
 * {@link AndroidSupportInjectionModule} is the module from Dagger.Android that helps with the
 * generation and location of subcomponents.
 */
@Singleton //
@Component(modules = { //
		AndroidSupportInjectionModule.class, //
		ActivityBindingModule.class, //
		AppModule.class, //
		ViewModelModule.class, //
		NetworkModule.class //
}) //
public interface AppComponent extends AndroidInjector<MainApplication>
{
	@Component.Factory interface Factory
	{
		AppComponent create(@BindsInstance MainApplication application);
	}
}
