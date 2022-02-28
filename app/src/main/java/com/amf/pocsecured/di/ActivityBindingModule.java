package com.amf.pocsecured.di;

import com.amf.pocsecured.di.annotation.ActivityScoped;
import com.amf.pocsecured.ui.calendar.CalendarActivity;
import com.amf.pocsecured.ui.calendar.CalendarFragmentModule;
import com.amf.pocsecured.ui.event.EventActivity;
import com.amf.pocsecured.ui.event.EventFragmentModule;
import com.amf.pocsecured.ui.home.HomeActivity;
import com.amf.pocsecured.ui.home.HomeActivityModule;
import com.amf.pocsecured.ui.home.HomeFragmentModule;

import org.jetbrains.annotations.NotNull;

import dagger.Module;
import dagger.android.ContributesAndroidInjector;
/**
 * We want Dagger.Android to create a Subcomponent which has a parent Component of whichever module
 * {@link ActivityBindingModule} is on, in our case that will be {@link AppComponent}. You never
 * need to tell {@link AppComponent} that it is going to have all these subcomponents
 * nor do you need to tell these subcomponents that {@link AppComponent} exists.
 * We are also telling Dagger.Android that this generated SubComponent needs to include the
 * specified modules and be aware of a scope annotation {@link ActivityScoped}.
 * When Dagger.Android annotation processor runs it will create 2 subcomponents for us.
 */
@SuppressWarnings("unused") //
@Module
abstract class ActivityBindingModule
{
	@ActivityScoped()
	@ContributesAndroidInjector(
			modules = {HomeActivityModule.class, HomeFragmentModule.class}
	)
	@NotNull
	public abstract HomeActivity homeActivity();

	@ActivityScoped()
	@ContributesAndroidInjector(
			modules = {CalendarFragmentModule.class}
	)
	@NotNull
	public abstract CalendarActivity calendarActivity();

	@ActivityScoped()
	@ContributesAndroidInjector(
			modules = {EventFragmentModule.class}
	)
	@NotNull
	public abstract EventActivity eventActivity();
}
