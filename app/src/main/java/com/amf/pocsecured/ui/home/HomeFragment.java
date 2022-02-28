package com.amf.pocsecured.ui.home;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.amf.pocsecured.R;
import com.amf.pocsecured.data.IUserRepository;
import com.amf.pocsecured.databinding.FragmentHomeBinding;
import com.amf.pocsecured.domain.usecase.FetchUserInfosUseCase;
import com.amf.pocsecured.ui.BaseFragment;
import com.amf.pocsecured.ui.viewmodel.HomeActivityViewModel;

import org.jetbrains.annotations.NotNull;

import javax.inject.Inject;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;

public class HomeFragment extends BaseFragment
{
	@Inject
	public ViewModelProvider.Factory viewModelFactory;

	@Inject
	public FetchUserInfosUseCase mFetchUserInfosUseCase;

	@Inject
	public IUserRepository mUserRepository;

	private FragmentHomeBinding binding;

	public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		binding = FragmentHomeBinding.inflate(inflater, container, false);
		return binding.getRoot();
	}

	@Override
	public void onViewCreated(@NonNull @NotNull View view, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState)
	{
		super.onViewCreated(view, savedInstanceState);

		HomeActivityViewModel activityViewModel = ((HomeActivity) requireActivity()).viewModel;

		// Observe user LiveData changes from ViewModel
		activityViewModel.getUser().observe(getViewLifecycleOwner(), user->{
			if (user != null)
			{
				binding.homeInfos.setText(user.getNameForScreen());
			}
			else
			{
				binding.homeInfos.setText(getString(R.string.please_sign_in));
			}

			((HomeActivity) requireActivity()).updateNavigationUserValues();
		});

		// Observe loading LiveData changes from ViewModel
		activityViewModel.getLoading().observe(getViewLifecycleOwner(), isLoading->{
			if (isLoading)
			{
				showProgress();
			}
			else
			{
				dismissProgress();
			}
		});

		// Observe error LiveData changes from ViewModel
		activityViewModel.getError().observe(getViewLifecycleOwner(), this::alertSnack);

		showProgress();
	}

	@Override
	public void onDestroyView()
	{
		binding = null;
		super.onDestroyView();
	}

	public static HomeFragment newInstance(int index)
	{
		HomeFragment f = new HomeFragment();
		Bundle args = new Bundle();
		args.putInt(HomeFragment.class.getSimpleName(), index);
		f.setArguments(args);
		return f;
	}
}