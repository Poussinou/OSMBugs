package org.gittner.osmbugs.Helpers;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.widget.Toast;

import org.gittner.osmbugs.R;
import org.osmdroid.util.GeoPoint;

import java.util.Locale;

public class GeoIntentStarter
{
    public static void start(Context context, GeoPoint geoPoint)
    {
        Intent intent = new Intent(android.content.Intent.ACTION_VIEW, Uri.parse(String.format(
                Locale.US,
                "geo:%f,%f",
                geoPoint.getLatitude(),
                geoPoint.getLongitude())));

        if (!IntentHelper.intentHasReceivers(context, intent))
        {
            Toast.makeText(context, R.string.toast_geo_intent_no_app_found, Toast.LENGTH_LONG).show();
            return;
        }

        context.startActivity(intent);
    }
}
