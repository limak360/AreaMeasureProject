package com.example.areameasureproject;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.example.areameasureproject.entity.LatLngAdapter;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;

public class GPXGenerator {

    private static final String TAG = "GPXGenerator";
    private final Context mContext;

    public GPXGenerator(Context context) {
        this.mContext = context;
    }

    public void generateGFX(File file, String name, Collection<LatLngAdapter> points) {
        String header = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\" ?>" +
                "<gpx xmlns=\"http://www.topografix.com/GPX/1/1\" creator=\"MapSource 6.15.5\"" +
                " version=\"1.1\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" " +
                " xsi:schemaLocation=\"http://www.topografix.com/GPX/1/1 http://www.topografix.com/GPX/1/1/gpx.xsd\"><trk>\n";

        name = "<name>" + name + "</name><trkseg>\n";

        StringBuilder segments = new StringBuilder();
        @SuppressLint("SimpleDateFormat")
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ");
        for (LatLngAdapter latLngAdapter : points) {
            segments.append("<trkpt lat=\"")
                    .append(latLngAdapter.getLatitude())
                    .append("\" lon=\"")
                    .append(latLngAdapter.getLongitude())
                    .append("\"><time>")
                    .append(df.format(new Date(latLngAdapter.getTime())))
                    .append("</time></trkpt>\n");
        }
        String footer = "</trkseg></trk></gpx>";

        try {
            FileWriter writer = new FileWriter(file, false);
            writer.append(header);
            writer.append(name);
            writer.append(segments.toString());
            writer.append(footer);
            writer.flush();
            writer.close();
            Toast.makeText(mContext, "File saved...", Toast.LENGTH_SHORT).show();
        } catch (IOException e) {
            Toast.makeText(mContext, "File saving error", Toast.LENGTH_SHORT).show();
            Log.e(TAG, "Error Writing Path", e);
        }
    }
}