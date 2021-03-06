package org.gittner.osmbugs.activities;

import android.app.ProgressDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;

import org.androidannotations.annotations.AfterTextChange;
import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Background;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.Extra;
import org.androidannotations.annotations.OptionsItem;
import org.androidannotations.annotations.OptionsMenu;
import org.androidannotations.annotations.OptionsMenuItem;
import org.androidannotations.annotations.UiThread;
import org.androidannotations.annotations.ViewById;
import org.gittner.osmbugs.R;
import org.gittner.osmbugs.api.Apis;
import org.gittner.osmbugs.api.OsmNotesApi;
import org.osmdroid.util.GeoPoint;

@EActivity(R.layout.activity_add_osm_note)
@OptionsMenu(R.menu.add_osm_note)
public class AddOsmNoteActivity extends AppCompatActivity
{
    public static final String EXTRA_LATITUDE = "EXTRA_LATITUDE";
    public static final String EXTRA_LONGITUDE = "EXTRA_LONGITUDE";

    @Extra(EXTRA_LATITUDE)
    double mLatitude;
    @Extra(EXTRA_LONGITUDE)
    double mLongitude;

    @ViewById(R.id.edttxtDescription)
    EditText mDescription;
    @ViewById(R.id.toolbar)
    Toolbar mToolbar;

    @OptionsMenuItem(R.id.action_done)
    MenuItem mMenuDone;

    private ProgressDialog mSaveDialog = null;

    @AfterViews
    void init()
    {
        setSupportActionBar(mToolbar);

        mSaveDialog = new ProgressDialog(this);
        mSaveDialog.setTitle(R.string.saving);
        mSaveDialog.setMessage(getString(R.string.please_wait));
        mSaveDialog.setCancelable(false);
        mSaveDialog.setIndeterminate(true);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        mMenuDone.setVisible(!mDescription.getText().toString().equals(""));

        return true;
    }

    @OptionsItem(R.id.action_done)
    void menuDoneClicked()
    {
        mSaveDialog.show();

        addBug(
                new GeoPoint(mLatitude, mLongitude),
                mDescription.getText().toString()
        );
    }

    @Background
    void addBug(GeoPoint geoPoint, String description)
    {
        boolean result = Apis.OSM_NOTES.addNew(geoPoint, description);

        addBugDone(result);
    }

    @UiThread
    void addBugDone(boolean result)
    {
        addBugDone(result, getString(R.string.error));
    }

    @UiThread
    void addBugDone(boolean result, String message)
    {
        mSaveDialog.dismiss();

        if (!result)
        {
            Toast.makeText(this, message, Toast.LENGTH_LONG).show();
        }
        else
        {
            setResult(RESULT_OK);
            finish();
        }
    }

    @AfterTextChange(R.id.edttxtDescription)
    void descriptionChanged()
    {
        invalidateOptionsMenu();
    }

    @OptionsItem(android.R.id.home)
    void onHomeBtn()
    {
        setResult(RESULT_CANCELED);
        finish();
    }
}
