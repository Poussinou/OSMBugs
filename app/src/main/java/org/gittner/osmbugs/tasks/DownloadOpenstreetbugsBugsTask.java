package org.gittner.osmbugs.tasks;

import android.os.AsyncTask;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.gittner.osmbugs.bugs.KeeprightBug;
import org.gittner.osmbugs.bugs.MapdustBug;
import org.gittner.osmbugs.bugs.OpenstreetbugsBug;
import org.gittner.osmbugs.bugs.OpenstreetmapNote;
import org.gittner.osmbugs.parser.KeeprightParser;
import org.gittner.osmbugs.parser.MapdustParser;
import org.gittner.osmbugs.parser.OpenstreetbugsParser;
import org.gittner.osmbugs.parser.OpenstreetmapNotesParser;
import org.gittner.osmbugs.statics.Settings;
import org.osmdroid.util.BoundingBoxE6;

import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by christopher on 3/20/14.
 */
public abstract class DownloadOpenstreetbugsBugsTask extends AsyncTask<BoundingBoxE6, Void, ArrayList<OpenstreetbugsBug>> {

    @Override
    protected ArrayList<OpenstreetbugsBug> doInBackground(BoundingBoxE6... bBoxes) {
        HttpClient client = new DefaultHttpClient();

        ArrayList<NameValuePair> arguments = new ArrayList<NameValuePair>();

        arguments.add(new BasicNameValuePair("b", String.valueOf(bBoxes[0].getLatSouthE6() / 1000000.0)));
        arguments.add(new BasicNameValuePair("t", String.valueOf(bBoxes[0].getLatNorthE6() / 1000000.0)));
        arguments.add(new BasicNameValuePair("l", String.valueOf(bBoxes[0].getLonWestE6() / 1000000.0)));
        arguments.add(new BasicNameValuePair("r", String.valueOf(bBoxes[0].getLonEastE6() / 1000000.0)));

        if (Settings.Openstreetbugs.isShowOnlyOpenEnabled())
            arguments.add(new BasicNameValuePair("open", "1"));

        arguments.add(new BasicNameValuePair("limit",
                String.valueOf(Settings.Openstreetbugs.getBugLimit())));

        HttpGet request = new HttpGet("http://openstreetbugs.schokokeks.org/api/0.1/getGPX?" + URLEncodedUtils.format(arguments, "utf-8"));

        try {
            /* Execute Query */
            HttpResponse response = client.execute(request);

            /* Check for Success */
            if (response.getStatusLine().getStatusCode() != 200)
                return null;

            /* If Request was Successful, parse the Stream */
            return OpenstreetbugsParser.parse(response.getEntity().getContent());
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    @Override
    protected abstract void onPostExecute(ArrayList<OpenstreetbugsBug> bugs);
}