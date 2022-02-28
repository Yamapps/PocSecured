package com.amf.pocsecured.ui.event;

import com.amf.pocsecured.di.annotation.FragmentScoped;
import com.amf.pocsecured.di.annotation.ViewModelKey;
import com.amf.pocsecured.ui.viewmodel.EventFragmentViewModel;

import org.jetbrains.annotations.NotNull;

import androidx.lifecycle.ViewModel;
import dagger.Binds;
import dagger.Module;
import dagger.android.ContributesAndroidInjector;
import dagger.multibindings.IntoMap;

/**
 *
 * Manages {@link EventFragment} dependencies injection with Dagger
 *
 * @author youssefamrani
 */
@SuppressWarnings("unused")
@Module
public abstract class EventFragmentModule
{
	/**
	 * Generates an {@link dagger.android.AndroidInjector} for the {@link EventFragment} as a Dagger subcomponent.
	 */
	@FragmentScoped
	@ContributesAndroidInjector
	@NotNull
	public abstract EventFragment contributeEventFragment();

	/**
	 * The ViewModels are created by Dagger in a map. Via the @{@link ViewModelKey}, we define that we
	 * want to get a {@link EventFragmentViewModel} class.
	 */
	@Binds
	@IntoMap
	@ViewModelKey(EventFragmentViewModel.class)
	public abstract ViewModel bindEventFragmentViewModel(@NotNull EventFragmentViewModel viewModel);
}
