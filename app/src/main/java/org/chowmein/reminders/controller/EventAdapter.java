package org.chowmein.reminders.controller;

/*
 * ------------------------------------------References---------------------------------------------
 * most RecyclerView implementations:
 * https://stackoverflow.com/questions/29795299/what-is-the-sortedlistt-working-with-recyclerview-adapter
 * onClick listener for each view:
 * https://dzone.com/articles/click-listener-for-recyclerview-adapter
 * multiple view selection:
 * https://medium.com/@droidbyme/android-recyclerview-with-single-and-multiple-selection-5d50c0c4c739
 * get color from resources:
 * https://mobikul.com/use-color-contextcompat-android/
 */

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SortedList;

import org.chowmein.reminders.R;
import org.chowmein.reminders.activities.EventFormActivity;
import org.chowmein.reminders.activities.HomeActivity;
import org.chowmein.reminders.managers.DatesManager;
import org.chowmein.reminders.managers.Preferences;
import org.chowmein.reminders.managers.UIFormatter;
import org.chowmein.reminders.model.Event;

import java.util.ArrayList;

/**
 * The essential class for transforming a collection of data into
 * a collection of ViewHolders that display information to the user
 * on the app.
 */
public class EventAdapter extends RecyclerView.Adapter<EventAdapter.EventViewHolder> {

    private SortedList<Event> eventList;
    private RecyclerView view;

    private static final String REQUEST_CODE_KEY = "requestCode";
    private static final String EVENT_POS_KEY = "eventPos";
    private static final boolean ATTACH_TO_ROOT = false;

    /**
     * Constructor to override methods for RecyclerView.Adapter
     * @param context the context
     */
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

    /**
     * The inner class (ViewHolder) of the adapter.
     */
    public class EventViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener,
            View.OnLongClickListener{
        Event event;
        TextView tv_desc;

        public Event getEvent() {
            return event;
        }

        public TextView getTvDesc() {
            return tv_desc;
        }

        public TextView getTvDate() {
            return tv_date;
        }

        public TextView getTvDbr() {
            return tv_dbr;
        }

        public TextView getTvYear() {
            return tv_year;
        }

        public ConstraintLayout getClListItem() {
            return cl_list_item;
        }

        public View getEventView() {
            return eventView;
        }

        TextView tv_date;
        TextView tv_dbr;
        TextView tv_year;
        ConstraintLayout cl_list_item;
        View eventView;

        /**
         * Constructor that creates from a
         * @param eventView a single view
         */
        EventViewHolder(View eventView) {
            super(eventView);
            this.eventView = eventView;
            tv_desc = this.eventView.findViewById(R.id.tv_event_desc);
            tv_date = this.eventView.findViewById(R.id.tv_event_date);
            tv_dbr = this.eventView.findViewById(R.id.tv_event_dbr);
            tv_year = this.eventView.findViewById(R.id.tv_event_year);
            cl_list_item = this.eventView.findViewById(R.id.cl_list_item);
            eventView.setOnClickListener(this);
            eventView.setOnLongClickListener(this);
        }

        @Override
        public void onClick(View view) {
            // initialize the necessary views and activities
            Context context = view.getContext();
            HomeActivity home = (HomeActivity) context;

            if(home.selectMode) selectEvent(context);
            else editEvent(home);
        }

        /**
         * A helper method to edit
         * @param home the home activity
         */
        private void editEvent(HomeActivity home) {
            Intent editActivity = new Intent(home, EventFormActivity.class);
            editActivity.putExtra(REQUEST_CODE_KEY, HomeActivity.EDIT_REQUEST_CODE);

            // gets the event corresponding to the clicked view
            Event event = EventAdapter.this.get(this.getAdapterPosition());

            // get all the properties of one event, including its position in the list
            String dateStr = DatesManager.formatDate(event.getDate(), DatesManager.DATE_PTRN);
            String desc = event.getDesc();
            int dbr = event.getDbr();
            int eventPosition = eventList.indexOf(event);

            // and add them to the intent
            editActivity.putExtra(Event.DATE_KEY, dateStr);
            editActivity.putExtra(Event.DESC_KEY, desc);
            editActivity.putExtra(Event.DBR_KEY, dbr);
            editActivity.putExtra(EVENT_POS_KEY, eventPosition);

            // finally, start the activity
            home.startActivityForResult(editActivity, HomeActivity.EDIT_REQUEST_CODE);
        }

        /**
         * A helper method to set and show the view in RecyclerView as being selected.
         * @param context the context
         */
        private void selectEvent(Context context) {
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
                selectEvent(context);
                home.toggleSelectMode();
            }
            return true;
        }
    }

    @NonNull
    @Override
    public EventViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.event_list_view, parent, ATTACH_TO_ROOT);
        return new EventViewHolder(view);
    }

    /**
     * A helper method to set the style of a ViewHolder.
     * @param holder the custom EventViewHolder
     * @param event the Event
     * @param context the context
     */
    public void setStyle(EventViewHolder holder, Event event, Context context) {
        // if it's the first event in its year, make its ViewHolder's year TextView visible
        // and set its year correspondingly
        if(event.isYearTop()) {
            holder.tv_year.setVisibility(View.VISIBLE);
            String yearStr = DatesManager.formatDate(event.getDate(), DatesManager.YEAR_PTRN);
            holder.tv_year.setText(yearStr);
        } else {
            // if not just don't take up any space
            holder.tv_year.setVisibility(View.GONE);
        }

        setTextViewSizes(holder);

        UIFormatter.EventFormatter.styleEvent(context, event, holder, Preferences.getTheme());
    }

    /**
     * Sets the TextView font sizes within a ViewHolder appropriately.
     * @param holder the EventViewHolder
     */
    private void setTextViewSizes(EventViewHolder holder) {
        UIFormatter.formatEventItem(holder.eventView, Preferences.getFontSize(), view.getContext());
    }

    @Override
    public void onBindViewHolder(@NonNull EventViewHolder holder, int position) {
        Event event = eventList.get(position);
        holder.event = event;

        // sets the easy data textviews
        holder.tv_desc.setText(event.getDesc());
        holder.tv_dbr.setText("Remind from " +
                DatesManager.getDateStringFromDifference(
                        event.getDate().getTime(),
                        event.getDbr(),
                        DatesManager.MONTH_DAY_PTRN
                )
        );

        // sets the date textview
        String dateStr = DatesManager.formatDate(event.getDate(), DatesManager.MONTH_DAY_PTRN);
        holder.tv_date.setText(dateStr);

        // sets the year textview
        String yearStr = DatesManager.formatDate(event.getDate(), DatesManager.YEAR_PTRN);
        holder.tv_year.setText(yearStr);

        // set the event's yearTop field
        setYearTop(event, position, yearStr);

        // cheating here, since we can't make a context static
        Context context = HomeActivity.getAdapter().view.getContext();

        // sets the style of the viewHolder
        setStyle(holder, event, context);
    }

    /**
     * If the event before this is of a different year, set its yearTop to true.
     * @param event the Event
     * @param position its position
     * @param currYearStr the String for event's year
     */
    private void setYearTop(Event event, int position, String currYearStr) {
        // the first ViewHolder's year will be handled by the year TextView in HomeActivity
        if (position != 0) {
            Event previousEvent = eventList.get(position - 1);
            String prevYearStr = DatesManager.formatDate(previousEvent.getDate(),
                    DatesManager.YEAR_PTRN);
            int prevYear = Integer.parseInt(prevYearStr);
            int currYear = Integer.parseInt(currYearStr);
            if(prevYear < currYear) {
                event.setYearTop(true);
            } else {
                event.setYearTop(false);
            }
        }
    }

    @Override
    public int getItemCount() {
        return eventList.size();
    }

    /**
     * Returns eventList.
     */
    public SortedList<Event> getEventList() {
        return eventList;
    }

    /**
     * Gets the Event at some index of the eventList.
     * @param index the index of the Event in eventList
     * @return the Event in eventList at that index
     */
    public Event get(int index) {
        return eventList.get(index);
    }

    /**
     * Adds an Event to the eventList.
     * @param event the Event to be added
     */
    public void add(Event event) {
        eventList.add(event);
    }

    /**
     * Updates an Event at the position index in eventList.
     * @param position the position index
     * @param updatedEvent the new (or the same) Event
     */
    public void update(int position, Event updatedEvent) {
        eventList.updateItemAt(position, updatedEvent);
    }

    /**
     * Removes an Event at position index in eventList.
     * @param position the Event index in eventList
     */
    public void removeAtIndex(int position) {
        eventList.removeItemAt(position);
    }

    /**
     * Adds a list of Events to eventList.
     * @param list the list of Event to add to eventList
     */
    public void addAll(ArrayList<Event> list) {
        if(list == null) return;
        for(Event event : list) {
            this.add(event);
        }
    }
}
