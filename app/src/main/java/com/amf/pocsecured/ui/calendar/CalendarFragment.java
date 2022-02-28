package com.amf.pocsecured.ui.calendar;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.amf.pocsecured.databinding.FragmentCalendarBinding;
import com.amf.pocsecured.ui.BaseActivity;
import com.amf.pocsecured.ui.BaseFragment;
import com.amf.pocsecured.ui.adapter.HolidaysAdapter;
import com.amf.pocsecured.ui.viewmodel.CalendarFragmentViewModel;
import com.amf.pocsecured.utils.UiUtils;

import org.jetbrains.annotations.NotNull;

import javax.inject.Inject;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.OrientationHelper;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

public class CalendarFragment extends BaseFragment
{
	@Inject
	public ViewModelProvider.Factory viewModelFactory;

	private CalendarFragmentViewModel mViewModel;
	private FragmentCalendarBinding binding;

	public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		mViewModel = viewModelFactory.create(CalendarFragmentViewModel.class);
		binding = FragmentCalendarBinding.inflate(inflater, container, false);

		return binding.getRoot();
	}

	@Override
	public void onViewCreated(@NonNull @NotNull View view, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState)
	{
		super.onViewCreated(view, savedInstanceState);

		// Observe loading LiveData changes from ViewModel
		mViewModel.getLoading().observe(getViewLifecycleOwner(), isLoading->{
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
		mViewModel.getError().observe(getViewLifecycleOwner(), errorMessage -> {
			binding.calendarEmpty.setText(errorMessage);
		});

		showProgress();

		// Observe public holidays LiveData changes from ViewModel
		mViewModel.getPublicHolidays().observe(getViewLifecycleOwner(), holidays->{
			if (holidays == null || holidays.isEmpty())
			{
				binding.calendarEmpty.setVisibility(View.VISIBLE);
				binding.calendarRecycler.setVisibility(View.GONE);
			}
			else
			{
				RecyclerView recycler = binding.calendarRecycler;

				HolidaysAdapter adapter = new HolidaysAdapter(holidays, item->alertToast("click on holiday : " + item.getLabel()));
				recycler.setAdapter(adapter);

				int rowCount = UiUtils.isLargeScreen(requireContext())
							   ? LARGE_SCREEN_RECYCLER_ROW_COUNT
							   : NORMAL_SCREEN_RECYCLER_ROW_COUNT;
				StaggeredGridLayoutManager viewManager = new StaggeredGridLayoutManager(rowCount, OrientationHelper.VERTICAL);
				recycler.setLayoutManager(viewManager);

				recycler.setVisibility(View.VISIBLE);
				binding.calendarEmpty.setVisibility(View.GONE);
			}
		});

		mViewModel.fetchPublicHolidays(requireActivity());
	}

	@Override
	public void onDestroyView()
	{
		super.onDestroyView();
		binding = null;
	}

	public static CalendarFragment newInstance(int index)
	{
		CalendarFragment f = new CalendarFragment();
		Bundle args = new Bundle();
		args.putInt(CalendarFragment.class.getSimpleName(), index);
		f.setArguments(args);
		return f;
	}
}