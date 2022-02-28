package com.amf.pocsecured.di;

import org.jetbrains.annotations.NotNull;

import androidx.lifecycle.ViewModelProvider;
import dagger.Binds;
import dagger.Module;

/**
 * Module used to define the connection between the framework's {@link ViewModelProvider.Factory}
 * and our own implementation: {@link ViewModelFactory}.
 */
@SuppressWarnings("unused") //
@Module public abstract class ViewModelModule
{
	@Binds
	@NotNull
	public abstract ViewModelProvider.Factory bindViewModelFactory(@NotNull ViewModelFactory viewModelFactory);
}
