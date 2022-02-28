package com.amf.pocsecured.ui.home;

import com.amf.pocsecured.di.annotation.FragmentScoped;

import org.jetbrains.annotations.NotNull;

import dagger.Module;
import dagger.android.ContributesAndroidInjector;

/**
 *
 * Manages {@link HomeFragment} dependencies injection with Dagger
 *
 * @author youssefamrani
 */
@SuppressWarnings("unused")
@Module
public abstract class HomeFragmentModule
{

	/**
	 * Generates an {@link dagger.android.AndroidInjector} for the {@link HomeFragment} as a Dagger subcomponent.
	 * @return {@link HomeFragment}
	 */
	@FragmentScoped
	@ContributesAndroidInjector
	@NotNull
	public abstract HomeFragment contributeHomeFragment();
}
