package com.amf.pocsecured.ui.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.amf.pocsecured.R;
import com.amf.pocsecured.model.PublicHoliday;
import com.amf.pocsecured.utils.DateTimeUtils;

import org.jetbrains.annotations.NotNull;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import static com.amf.pocsecured.utils.DateTimeUtils.DATE_PATTERN_DAY_OF_MONTH;
import static com.amf.pocsecured.utils.DateTimeUtils.DATE_PATTERN_DAY_OF_WEEK;
import static com.amf.pocsecured.utils.DateTimeUtils.DATE_PATTERN_MONTH;

/**
 * @author youssefamrani
 */
public class HolidaysAdapter extends RecyclerView.Adapter<HolidaysAdapter.HolidayViewHolder>
{
	private final ItemOnItemClickListener mListener;
	private final List<PublicHoliday> mPublicHolidays;

	public HolidaysAdapter(List<PublicHoliday> mPublicHolidays, ItemOnItemClickListener listener)
	{
		this.mListener = listener;
		this.mPublicHolidays = mPublicHolidays;
	}

	@NonNull
	@NotNull
	@Override
	public HolidaysAdapter.HolidayViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType)
	{
		View root = LayoutInflater.from(parent.getContext()) //
							.inflate(R.layout.holiday_list_item, parent, false);
		return new HolidayViewHolder(root);
	}

	@Override
	public void onBindViewHolder(@NonNull @NotNull HolidaysAdapter.HolidayViewHolder holder, int position)
	{
		PublicHoliday publicHoliday = mPublicHolidays.get(position);

		holder.getLabel().setText(publicHoliday.getLabel());
		holder.getDayOfWeek().setText(DateTimeUtils.toString(publicHoliday.getDate(), DATE_PATTERN_DAY_OF_WEEK));
		holder.getDayOfMonth().setText(DateTimeUtils.toString(publicHoliday.getDate(), DATE_PATTERN_DAY_OF_MONTH));
		holder.getMonth().setText(DateTimeUtils.toString(publicHoliday.getDate(), DATE_PATTERN_MONTH));

		holder.bind(publicHoliday, mListener);
	}

	@Override
	public int getItemCount()
	{
		return mPublicHolidays == null ? 0 : mPublicHolidays.size();
	}

	public static class HolidayViewHolder extends RecyclerView.ViewHolder
	{

		private final TextView label, dayOfWeek, dayOfMonth, month;

		public HolidayViewHolder(@NonNull @NotNull View itemView)
		{
			super(itemView);

			label = itemView.findViewById(R.id.holiday_label);
			dayOfWeek = itemView.findViewById(R.id.holiday_day_of_week);
			dayOfMonth = itemView.findViewById(R.id.holiday_day_of_month);
			month = itemView.findViewById(R.id.holiday_month);
		}

		public void bind(PublicHoliday event, ItemOnItemClickListener listener)
		{
			itemView.setOnClickListener(view->listener.onItemClick(event));
		}

		public TextView getLabel()
		{
			return label;
		}

		public TextView getDayOfWeek()
		{
			return dayOfWeek;
		}

		public TextView getDayOfMonth()
		{
			return dayOfMonth;
		}

		public TextView getMonth()
		{
			return month;
		}
	}

	public interface ItemOnItemClickListener
	{
		void onItemClick(PublicHoliday item);
	}
}
