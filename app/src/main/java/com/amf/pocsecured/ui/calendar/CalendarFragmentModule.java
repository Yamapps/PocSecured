package com.amf.pocsecured.ui.calendar;

import com.amf.pocsecured.di.annotation.FragmentScoped;
import com.amf.pocsecured.di.annotation.ViewModelKey;
import com.amf.pocsecured.ui.viewmodel.CalendarFragmentViewModel;

import org.jetbrains.annotations.NotNull;

import androidx.lifecycle.ViewModel;
import dagger.Binds;
import dagger.Module;
import dagger.android.ContributesAndroidInjector;
import dagger.multibindings.IntoMap;


/**
 *
 * Manages {@link CalendarFragment} dependencies injection with Dagger
 *
 * @author youssefamrani
 */
@SuppressWarnings("unused")
@Module
public abstract class CalendarFragmentModule
{
	/**
	 * Generates an {@link dagger.android.AndroidInjector} for the {@link CalendarFragment} as a Dagger subcomponent.
	 */
	@FragmentScoped
	@ContributesAndroidInjector
	@NotNull
	public abstract CalendarFragment contributeCalendarFragment();

	/**
	 * The ViewModels are created by Dagger in a map. Via the @{@link ViewModelKey}, we define that we
	 * want to get a {@link CalendarFragmentViewModel} class.
	 */
	@Binds
	@IntoMap
	@ViewModelKey(CalendarFragmentViewModel.class)
	public abstract ViewModel bindCalendarFragmentViewModel(@NotNull CalendarFragmentViewModel viewModel);
}
