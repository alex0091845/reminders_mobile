package org.chowmein.reminders.managers;

import androidx.recyclerview.widget.SortedList;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;

import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

import org.chowmein.reminders.model.Event;

/**
 * A helper class to do anything related to the json save file.
 */
public class JsonHelper {
    /**
     * Used when reading json objects and parsing them into an ArrayList of Events
     * @param file the json file to read from
     * @return the ArrayList of Events
     */
    public static ArrayList<Event> deserialize(File file) {
        if(!file.exists()) {
            System.out.println("file doesn't exist");
            return null;
        }
        try {
            // using wrapped readers for more efficiency
            FileReader fr = new FileReader(file);
            BufferedReader br = new BufferedReader(fr);
            JsonReader reader = new JsonReader(br);

            reader.beginArray();

            ArrayList<Event> eventList = parseEvents(reader);

            reader.close();
            br.close();
            fr.close();

            return eventList;

        } catch (Exception ex) {
            System.out.println(ex.getMessage());
            return null;
        }
    }

    /**
     * A helper method to parse json objects into an ArrayList of events
     * @param reader the json reader
     * @return the ArrayList of Events
     * @throws IOException IOException from reader
     * @throws ParseException ParseException from reader
     */
    private static ArrayList<Event> parseEvents(JsonReader reader)
            throws IOException, ParseException {
        ArrayList<Event> eventList = new ArrayList<>();

        // each of the skipValue calls skips the key, and allows the reader to just get the value
        // of each key
        while (reader.hasNext()) {
            reader.beginObject();

            reader.skipValue();
            String dateStr = reader.nextString();

            reader.skipValue();
            String desc = reader.nextString();

            reader.skipValue();
            int dbr = reader.nextInt();

            reader.endObject();

            // special operation to parse date string into a Date object for the Event
            Date date = DatesManager.parseDate(dateStr, DatesManager.DATE_PATTERN);

            Event event = new Event(date, desc, dbr);
            eventList.add(event);
        }
        reader.endArray();

        return eventList;
    }

    /**
     * Used when writing a SorterList of Events into a json file
     * @param list the SoredList to write from
     * @param file the file to write to
     */
    public static void serialize(SortedList<Event> list, File file) {
        if(!file.exists()) {
            try {
                file.createNewFile();
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }
        }
        try {
            // using wrapper writers for efficiency
            FileWriter fw = new FileWriter(file);
            BufferedWriter bw = new BufferedWriter(fw);
            JsonWriter writer = new JsonWriter(bw);

            // denotes the beginning of writing an array
            writer.beginArray();
            for (int i = 0; i < list.size(); i++) {
                Event event = list.get(i);
                writeEvent(event, writer);
            }
            writer.endArray();
            writer.flush();

            writer.close();
            bw.close();
            fw.close();
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    /**
     * A helper method to write a single Event as a json object
     * @param event the single Event
     * @param writer a JsonWriter
     * @throws IOException the IOException that writer throws
     */
    private static void writeEvent(Event event, JsonWriter writer) throws IOException {
        writer.beginObject();
        writer.name(Event.DATE_KEY).value(DatesManager.formatDate(event.getDate(),
                DatesManager.DATE_PATTERN));
        writer.name(Event.DESC_KEY).value(event.getDesc());
        writer.name(Event.DBR_KEY).value(event.getDbr());
        writer.endObject();
    }
}
