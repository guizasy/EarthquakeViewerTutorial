package com.taberu.earthquakeviewer;

import android.app.ListFragment;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.provider.DocumentsContract;
import android.widget.ArrayAdapter;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.logging.LogRecord;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

/**
 * Created by Taberu on 01/11/2016.
 */

public class EarthquakeListFragment extends ListFragment {

    ArrayAdapter<Quake> aaq;
    ArrayList<Quake> earthquakeList = new ArrayList<Quake>();

    private static final String TAG = "EARTHQUAKE";
    private Handler handler = new Handler();

    @Override
    public void onActivityCreated(Bundle savedInstance) {
        super.onActivityCreated(savedInstance);

        int layoutId = android.R.layout.simple_list_item_1;
        aaq = new ArrayAdapter<Quake>(getActivity(), layoutId, earthquakeList);

        setListAdapter(aaq);

        Thread t = new Thread(new Runnable() {
           public void run() {
               refreshEarthquakes();
           }
        });

        t.start();
    }

    private void addNewQuake(Quake _quake) {
        earthquakeList.add(_quake);
        aaq.notifyDataSetChanged();
    }

    public void refreshEarthquakes() {
        URL url;
        try {
            String quakeFeed = getString(R.string.quake_feed);
            url = new URL(quakeFeed);

            URLConnection connection;
            connection = url.openConnection();

            HttpURLConnection httpConnection = (HttpURLConnection) connection;
            int responseCode = httpConnection.getResponseCode();

            if (responseCode == HttpURLConnection.HTTP_OK) {
                InputStream in = httpConnection.getInputStream();
                DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
                DocumentBuilder db = dbf.newDocumentBuilder();

                Document dom = db.parse(in);
                Element docEle = dom.getDocumentElement();

                earthquakeList.clear();

                NodeList nl = docEle.getElementsByTagName("event");

                if (nl != null && nl.getLength() > 0) {

                    for (int i = 0; i <nl.getLength(); i++ ) {
                        Element entry = (Element) nl.item(i);

                        String title = entry.getElementsByTagName("description").item(0).
                                getChildNodes().item(1).getFirstChild().getNodeValue();
                        String latitude = entry.getElementsByTagName("latitude").item(0).
                                getFirstChild().getFirstChild().getNodeValue();
                        String longitude = entry.getElementsByTagName("longitude").item(0).
                                getFirstChild().getFirstChild().getNodeValue();
                        String when = entry.getElementsByTagName("time").item(0).
                                getFirstChild().getFirstChild().getNodeValue();
                        String magnitudeStr = entry.getElementsByTagName("mag").item(0).
                                getFirstChild().getFirstChild().getNodeValue();

                        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");

                        Date qdate = new Date();

                        try {
                            qdate.setTime(sdf.parse(when).getTime());
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }

                        Location l = new Location("dummyGPS");
                        l.setLatitude(Double.parseDouble(latitude));
                        l.setLongitude(Double.parseDouble(longitude));

                        double magnitude = Double.parseDouble(magnitudeStr);

                        final Quake quake = new Quake(qdate, title, l, magnitude);

                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                addNewQuake(quake);
                            }
                        });
                    }
                }
            }
        } catch (IOException | ParserConfigurationException | SAXException e) {
            e.printStackTrace();
        }
    }
}
