package com.amf.pocsecured.ui.event;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.amf.pocsecured.databinding.FragmentEventBinding;
import com.amf.pocsecured.ui.BaseActivity;
import com.amf.pocsecured.ui.BaseFragment;
import com.amf.pocsecured.ui.adapter.EventsAdapter;
import com.amf.pocsecured.ui.viewmodel.EventFragmentViewModel;
import com.amf.pocsecured.utils.UiUtils;

import javax.inject.Inject;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.OrientationHelper;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

public class EventFragment extends BaseFragment
{

	@Inject
	public ViewModelProvider.Factory viewModelFactory;

	private EventFragmentViewModel mViewModel;
	private FragmentEventBinding binding;

	public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		mViewModel = viewModelFactory.create(EventFragmentViewModel.class);
		binding = FragmentEventBinding.inflate(inflater, container, false);

		return binding.getRoot();
	}

	@Override
	public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState)
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


		// Observe events list LiveData changes from ViewModel
		mViewModel.getEvents().observe(getViewLifecycleOwner(), events->{
			if (events == null || events.isEmpty())
			{
				// Display empty text view and hide list
				binding.calendarEmpty.setVisibility(View.VISIBLE);
				binding.calendarRecycler.setVisibility(View.GONE);
			}
			else
			{
				RecyclerView recycler = binding.calendarRecycler;

				EventsAdapter adapter = new EventsAdapter(events, item->{
					alertToast("click on event : " + item.getSubject());
				});
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

		mViewModel.fetchEvents();
	}

	@Override
	public void onDestroyView()
	{
		super.onDestroyView();
		binding = null;
	}

	public static EventFragment newInstance(int index)
	{
		EventFragment f = new EventFragment();
		Bundle args = new Bundle();
		args.putInt(EventFragment.class.getSimpleName(), index);
		f.setArguments(args);
		return f;
	}
}