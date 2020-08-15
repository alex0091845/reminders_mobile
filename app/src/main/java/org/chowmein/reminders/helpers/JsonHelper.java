package org.chowmein.reminders.helpers;

/**
 * The file for a helper class to serialize and deserialize custom json
 * file according to the Event objects.
 */

import androidx.recyclerview.widget.SortedList;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

import org.chowmein.reminders.model.Event;

public class JsonHelper {
    public static ArrayList<Event> deserialize(File file) {
        if(!file.exists()) {
            System.out.println("file doesn't exist");
            return null;
        }
        try {
            FileReader fr = new FileReader(file);
            BufferedReader br = new BufferedReader(fr);
            JsonReader reader = new JsonReader(br);

            reader.beginArray();

            ArrayList<Event> eventList = new ArrayList<>();

            while(reader.hasNext()){
                reader.beginObject();
                reader.skipValue();
                String dateStr = reader.nextString();
                reader.skipValue();
                String desc = reader.nextString();
                reader.skipValue();
                int dbr = reader.nextInt();
                reader.endObject();

                DateFormat format = new SimpleDateFormat("M/d/yyyy");
                Date date = format.parse(dateStr);

                Event event = new Event(date, desc, dbr);
                eventList.add(event);
            }
            reader.endArray();

            reader.close();
            br.close();
            fr.close();

            return eventList;

        } catch (Exception ex) {
            System.out.println(ex.getMessage());
            return null;
        }
    }

    public static void serialize(SortedList<Event> list, File file) {
        if(!file.exists()) {
            try {
                file.createNewFile();
            } catch (Exception e) {}
        }
        try {
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
        } catch (Exception e) {}
    }

    private static String formatDate(String pattern, Date date) {
        DateFormat format = new SimpleDateFormat(pattern);
        return format.format(date);
    }

    private static void writeEvent(Event event, JsonWriter writer) throws IOException {
        writer.beginObject();
        writer.name("date").value(formatDate("M/d/yyyy", event.getDate()));
        writer.name("desc").value(event.getDesc());
        writer.name("dbr").value(event.getDbr());
        writer.endObject();
    }
}
