package org.chowmein.reminders;

/**
 * The essential class for transforming a collection of data into
 * a collection of ViewHolders that display information to the user
 * on the app.
 * ------------------------------------------References---------------------------------------------
 * most RecyclerView implementations:
 * https://stackoverflow.com/questions/29795299/what-is-the-sortedlistt-working-with-recyclerview-adapter
 * onClick listener for each view:
 * https://dzone.com/articles/click-listener-for-recyclerview-adapter
 * multiple view selection:
 * https://medium.com/@droidbyme/android-recyclerview-with-single-and-multiple-selection-5d50c0c4c739
 */

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SortedList;

import org.chowmein.reminders.activities.EventFormActivity;
import org.chowmein.reminders.activities.HomeActivity;
import org.chowmein.reminders.helpers.Preferences;
import org.chowmein.reminders.helpers.UIFormatter;
import org.chowmein.reminders.model.Event;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

public class EventAdapter extends RecyclerView.Adapter<EventAdapter.EventViewHolder> {

    private SortedList<Event> eventList;
    private RecyclerView view;

    public EventAdapter(Context context) {
        this.view = ((Activity)context).findViewById(R.id.rv_reminders);
        this.eventList = new SortedList<>(Event.class, new SortedList.Callback<Event>() {
            @Override
            public int compare(Event o1, Event o2) {
                int dateComp = o1.getDate().compareTo(o2.getDate());
                return dateComp == 0 ? o1.getDesc().compareTo(o2.getDesc()) : dateComp;
            }

            @Override
            public void onChanged(int position, int count) {
                notifyItemRangeChanged(position, count);
            }

            @Override
            public boolean areContentsTheSame(Event oldItem, Event newItem) {
                return oldItem.equals(newItem);
            }

            @Override
            public boolean areItemsTheSame(Event item1, Event item2) {
                return item1.equals(item2);
            }

            @Override
            public void onInserted(int position, int count) {
                notifyItemInserted(position);
                // Update the style of all items from that inserted item all the way to the end;
                // this method will make the RecyclerView call notifyItemChanged for
                // itemCount items starting from position.
                notifyItemRangeChanged(position,
                        EventAdapter.this.getItemCount() - position);
            }

            @Override
            public void onRemoved(int position, int count) {
                notifyItemRemoved(position);
                notifyItemRangeChanged(0, EventAdapter.this.getItemCount());
            }

            @Override
            public void onMoved(int fromPosition, int toPosition) {
                notifyItemMoved(fromPosition, toPosition);
                notifyItemRangeChanged(Math.min(fromPosition, toPosition),
                        Math.abs(toPosition - fromPosition) + 1);
            }
        });
    }

    class EventViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener,
            View.OnLongClickListener{
        Event event;
        TextView tv_desc;
        TextView tv_date;
        TextView tv_dbr;
        TextView tv_year;
        ConstraintLayout cl_list_item;

        EventViewHolder(View eventView) {
            super(eventView);
            tv_desc = eventView.findViewById(R.id.tv_event_desc);
            tv_date = eventView.findViewById(R.id.tv_event_date);
            tv_dbr = eventView.findViewById(R.id.tv_event_dbr);
            tv_year = eventView.findViewById(R.id.tv_event_year);
            cl_list_item = eventView.findViewById(R.id.cl_list_item);
            eventView.setOnClickListener(this);
            eventView.setOnLongClickListener(this);
        }

        @Override
        public void onClick(View view) {
            // initialize the necessary views and activities
            Context context = view.getContext();
            HomeActivity home = (HomeActivity) context;

            if(home.selectMode) selectEvent(view, context);
            else editEvent(home);
        }

        private void editEvent(HomeActivity home) {
            Intent editActivity = new Intent(home, EventFormActivity.class);
            editActivity.putExtra("requestCode", HomeActivity.EDIT_REQUEST_CODE);

            // gets the event corresponding to the clicked view
            Event event = EventAdapter.this.get(this.getAdapterPosition());

            // get all the properties of one event, including its position in the list
            DateFormat format = new SimpleDateFormat("M/d/yyyy");
            String dateStr = format.format(event.getDate());
            String desc = event.getDesc();
            int dbr = event.getDbr();
            int eventPosition = eventList.indexOf(event);

            // and add them to the intent
            editActivity.putExtra("date", dateStr);
            editActivity.putExtra("desc", desc);
            editActivity.putExtra("dbr", dbr);
            editActivity.putExtra("eventPos", eventPosition);

            // finally, start the activity
            home.startActivityForResult(editActivity, HomeActivity.EDIT_REQUEST_CODE);
        }

        private void selectEvent(View view, Context context) {
            int adptrPos = this.getAdapterPosition();
            Event event = EventAdapter.this.get(adptrPos);
            event.setSelected(!event.isSelected());
            setStyle(this, event, context);
        }

        @Override
        public boolean onLongClick(View view) {

            Context context = view.getContext();
            HomeActivity home = (HomeActivity) context;

            // enter select mode, if not already
            if(!home.selectMode) {
                selectEvent(view, context);
                home.toggleSelectMode();
            }
            return true;
        }
    }

    @NonNull
    @Override
    public EventViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.event_list_view, parent, false);
        return new EventViewHolder(view);
    }

    public void setStyle(EventViewHolder holder, Event event, Context context) {
        if(event.isYearTop()) {
            holder.tv_year.setVisibility(View.VISIBLE);
            DateFormat yearFormat = new SimpleDateFormat("yyyy");
            String yearStr = yearFormat.format(event.getDate());
            holder.tv_year.setText(yearStr);
        } else {
            holder.tv_year.setVisibility(View.GONE);
        }

        holder.tv_desc.setTextSize(Preferences.fontSize);
        holder.tv_date.setTextSize(Preferences.fontSize);
        holder.tv_dbr.setTextSize(Preferences.fontSize - UIFormatter.SMALL_OFFSET);
        holder.tv_year.setTextSize(Preferences.fontSize - UIFormatter.MEDIUM_OFFSET);

        // set style based on position
        if (event.isSelected()) {
            holder.cl_list_item.setBackground(ContextCompat.getDrawable(context, R.drawable.item_bg_blue));
            int white = Color.parseColor("#FFFFFF");
            holder.tv_desc.setTextColor(white);
            holder.tv_date.setTextColor(white);
            holder.tv_dbr.setTextColor(white);
        }
        else if (holder.getAdapterPosition() % 2 == 1) {
            holder.cl_list_item.setBackground(ContextCompat.getDrawable(context, R.drawable.item_bg_gray));
            int darkGray = Color.parseColor("#393939");
            holder.tv_desc.setTextColor(darkGray);
            holder.tv_date.setTextColor(darkGray);
            holder.tv_dbr.setTextColor(darkGray);
        } else {
            holder.cl_list_item.setBackground(ContextCompat.getDrawable(context, R.drawable.item_bg_red));
            int white = Color.parseColor("#FFFFFF");
            holder.tv_desc.setTextColor(white);
            holder.tv_date.setTextColor(white);
            holder.tv_dbr.setTextColor(white);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull EventViewHolder holder, int position) {
        Event event = eventList.get(position);
        holder.event = event;

        // sets the easy data textviews
        holder.tv_desc.setText(event.getDesc());
        holder.tv_dbr.setText(event.getDbr() + " days before");

        // sets the date textview
        DateFormat dateFormat = new SimpleDateFormat("M/d"); // format to only month and day
        String dateStr = dateFormat.format(event.getDate());
        holder.tv_date.setText(dateStr);

        // sets the year textview
        DateFormat yearFormat = new SimpleDateFormat("yyyy");
        String yearStr = yearFormat.format(event.getDate());
        holder.tv_year.setText(yearStr);

        // if the event before this is of a different year, set its yearTop to true
        // or if the event is the first
        if (position != 0) {
            Event previousEvent = eventList.get(position - 1);
            String prevYearStr = yearFormat.format(previousEvent.getDate());
            int prevYear = Integer.parseInt(prevYearStr);
            int currYear = Integer.parseInt(yearStr);
            if(prevYear < currYear) {
                event.setYearTop(true);
            } else {
                event.setYearTop(false);
            }
        }

        // cheating here, since we can't make a context static
        Context context = HomeActivity.getAdapter().view.getContext();

        // sets the style of the viewHolder
        setStyle(holder, event, context);
    }

    @Override
    public int getItemCount() {
        return eventList.size();
    }

    public SortedList<Event> getEventList() {
        return eventList;
    }

    public Event get(int index) {
        return eventList.get(index);
    }

    public int add(Event event) {
        return eventList.add(event);
    }

    public void update(int position, Event updatedEvent) {
        eventList.updateItemAt(position, updatedEvent);
    }

    public Event removeAtIndex(int position) {
        return eventList.removeItemAt(position);
    }

    public void addAll(ArrayList<Event> list) {
        if(list == null) return;
        for(Event event : list) {
            this.add(event);
        }
    }
}
