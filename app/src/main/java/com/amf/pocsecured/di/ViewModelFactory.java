package com.amf.pocsecured.di;

import com.amf.pocsecured.di.annotation.ViewModelKey;

import java.util.Map;

import javax.inject.Inject;
import javax.inject.Provider;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;

/**
 * ViewModelFactory which uses Dagger to create the instances.
 * @author youssefamrani
 */
public class ViewModelFactory implements ViewModelProvider.Factory {
	private final Map<Class<? extends ViewModel>, Provider<ViewModel>> creators;

	@Inject
	public ViewModelFactory(Map<Class<? extends ViewModel>, Provider<ViewModel>> creators) {
		this.creators = creators;
	}

	/**
	 * Instantiates a new instance of the specified view model, injecting required dependencies.
	 */
	@Override
	public <T extends ViewModel> T create(Class<T> modelClass) {
		Provider<? extends ViewModel> creator = creators.get(modelClass);
		if (creator == null) {
			throw new IllegalArgumentException("Unknown model class " + modelClass);
		}
		return (T) creator.get();
	}

	/**
	 * Returns an instance of the specified view model, which is scoped to the activity if annotated
	 * with {@link ViewModelKey}, or scoped to the Fragment if not.
	 */
	public <T extends ViewModel> T get(Fragment fragment, Class<T> modelClass) {
		if (modelClass.getAnnotation(ViewModelKey.class) == null) {
			return ViewModelProviders.of(fragment, this).get(modelClass);
		} else {
			return get(fragment.getActivity(), modelClass);
		}
	}

	/**
	 * Returns an instance of the specified view model scoped to the provided activity.
	 */
	public <T extends ViewModel> T get(FragmentActivity activity, Class<T> modelClass) {
		return ViewModelProviders.of(activity, this).get(modelClass);
	}
}
