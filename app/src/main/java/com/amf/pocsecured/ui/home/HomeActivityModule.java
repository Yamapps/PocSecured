package com.amf.pocsecured.ui.home;

import com.amf.pocsecured.di.annotation.ViewModelKey;
import com.amf.pocsecured.ui.viewmodel.HomeActivityViewModel;

import org.jetbrains.annotations.NotNull;

import androidx.lifecycle.ViewModel;
import dagger.Binds;
import dagger.Module;
import dagger.multibindings.IntoMap;

/**
 *
 * Manages {@link HomeActivity} dependencies injection with Dagger
 *
 * @author youssefamrani
 */
@SuppressWarnings("unused")
@Module
public abstract class HomeActivityModule
{

	/**
	 * The ViewModels are created by Dagger in a map. Via the @{@link ViewModelKey}, we define that we
	 * want to get a {@link HomeActivityViewModel} class.
	 */
	@Binds
	@IntoMap
	@ViewModelKey(HomeActivityViewModel.class)
	public abstract ViewModel bindHomeActivityViewModel(@NotNull HomeActivityViewModel viewModel);
}
