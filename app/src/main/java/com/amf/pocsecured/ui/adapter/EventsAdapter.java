package com.amf.pocsecured.ui.adapter;

import android.content.Context;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.StyleSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.amf.pocsecured.R;
import com.amf.pocsecured.model.Event;
import com.amf.pocsecured.utils.DateTimeUtils;

import org.jetbrains.annotations.NotNull;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

/**
 * @author youssefamrani
 */
public class EventsAdapter extends RecyclerView.Adapter<EventsAdapter.EventViewHolder>
{
	private final ItemOnItemClickListener listener;
	private final List<Event> events;

	public EventsAdapter(List<Event> events, ItemOnItemClickListener listener)
	{
		this.listener = listener;
		this.events = events;
	}

	@NotNull
	@Override
	public EventsAdapter.EventViewHolder onCreateViewHolder(@NotNull ViewGroup parent, int viewType)
	{
		View root = LayoutInflater.from(parent.getContext()) //
							.inflate(R.layout.event_list_item, parent, false);
		return new EventViewHolder(root);
	}

	@Override
	public void onBindViewHolder(@NotNull EventsAdapter.EventViewHolder holder, int position)
	{
		Event event = events.get(position);

		holder.getSubject().setText(event.getSubject());
		holder.getOrganizer().setText(event.getOrganizerName());

		Context context = holder.itemView.getContext();
		SpannableString spannableStart = new SpannableString(
				context.getString(R.string.events_start, DateTimeUtils.toString(event.getStart(), DateTimeUtils.DATE_PATTERN_WITH_HOUR)));
		spannableStart.setSpan(
				new StyleSpan(android.graphics.Typeface.BOLD),
				0,
				7,
				Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
		);
		holder.getStartDate().setText(spannableStart);

		SpannableString spannableEnd = new SpannableString(context.getString(R.string.events_end, DateTimeUtils.toString(event.getEnd(), DateTimeUtils.DATE_PATTERN_WITH_HOUR)));
		spannableEnd.setSpan(
				new StyleSpan(android.graphics.Typeface.BOLD),
				0,
				4,
				Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
		);
		holder.getEndDate().setText(spannableEnd);

		holder.bind(event, listener);
	}

	@Override
	public int getItemCount()
	{
		return events == null ? 0 : events.size();
	}

	static class EventViewHolder extends RecyclerView.ViewHolder
	{

		private final TextView subject;
		private final TextView organizer;
		private final TextView startDate;
		private final TextView endDate;

		public EventViewHolder(@NonNull @NotNull View itemView)
		{
			super(itemView);

			subject = itemView.findViewById(R.id.event_subject);
			organizer = itemView.findViewById(R.id.event_organizer);
			startDate = itemView.findViewById(R.id.event_start);
			endDate = itemView.findViewById(R.id.event_end);
		}

		public void bind(Event event, ItemOnItemClickListener listener)
		{
			itemView.setOnClickListener(view->listener.onItemClick(event));
		}

		public TextView getSubject()
		{
			return subject;
		}

		public TextView getOrganizer()
		{
			return organizer;
		}

		public TextView getStartDate()
		{
			return startDate;
		}

		public TextView getEndDate()
		{
			return endDate;
		}
	}

	public interface ItemOnItemClickListener
	{
		void onItemClick(Event item);
	}
}
