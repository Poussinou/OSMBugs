package org.gittner.osmbugs.activities;

import android.app.ProgressDialog;
import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatEditText;
import android.support.v7.widget.AppCompatSpinner;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

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
import org.gittner.osmbugs.bugs.MapdustBug;
import org.gittner.osmbugs.common.InvalidateOptionsMenuTextWatcher;
import org.gittner.osmbugs.statics.Images;
import org.osmdroid.util.GeoPoint;

@EActivity(R.layout.activity_add_mapdust_bug)
@OptionsMenu(R.menu.add_mapdust_bug)
public class AddMapdustBugActivity extends AppCompatActivity
{
    public static final String EXTRA_LATITUDE = "EXTRA_LATITUDE";
    public static final String EXTRA_LONGITUDE = "EXTRA_LONGITUDE";

    @Extra(EXTRA_LATITUDE)
    double mLatitude;
    @Extra(EXTRA_LONGITUDE)
    double mLongitude;

    @ViewById(R.id.spnType)
    AppCompatSpinner mSpnType;
    @ViewById(R.id.edttxtDescription)
    AppCompatEditText mDescription;
    @ViewById(R.id.toolbar)
    Toolbar mToolbar;

    @OptionsMenuItem(R.id.action_done)
    MenuItem mMenuDone;

    private ProgressDialog mSaveDialog = null;


    @AfterViews
    void init()
    {
        setSupportActionBar(mToolbar);

        /* Setup the Type Adapter */
        MapdustBugTypeAdapter typeAdapter = new MapdustBugTypeAdapter(this);

        mSpnType.setAdapter(typeAdapter);

        /* Invalidate Options Menu on Text change */
        mDescription.addTextChangedListener(new InvalidateOptionsMenuTextWatcher(this));

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

        int type = MapdustBug.OTHER;
        switch (mSpnType.getSelectedItemPosition())
        {
            case 0:
                type = MapdustBug.WRONG_TURN;
                break;
            case 1:
                type = MapdustBug.BAD_ROUTING;
                break;
            case 2:
                type = MapdustBug.ONEWAY_ROAD;
                break;
            case 3:
                type = MapdustBug.BLOCKED_STREET;
                break;
            case 4:
                type = MapdustBug.MISSING_STREET;
                break;
            case 5:
                type = MapdustBug.ROUNDABOUT_ISSUE;
                break;
            case 6:
                type = MapdustBug.MISSING_SPEED_INFO;
                break;
            case 7:
                type = MapdustBug.OTHER;
                break;
        }

        addBug(
                new GeoPoint(mLatitude, mLongitude),
                mDescription.getText().toString(),
                type
        );
    }


    @Background
    void addBug(GeoPoint geoPoint, String description, int type)
    {
        boolean result = Apis.MAPDUST.addBug(geoPoint, type, description);

        addBugDone(result);
    }


    @UiThread
    void addBugDone(boolean result)
    {
        mSaveDialog.dismiss();

        if (!result)
        {
            Toast.makeText(this, getString(R.string.error), Toast.LENGTH_LONG).show();
        }
        else
        {
            setResult(RESULT_OK);
            finish();
        }
    }


    private class MapdustBugTypeAdapter extends ArrayAdapter<Integer>
    {
        public MapdustBugTypeAdapter(Context context)
        {
            super(context, R.layout.row_mapdust_bug_type, R.id.txtvState);

            addAll(
                    MapdustBug.WRONG_TURN,
                    MapdustBug.BAD_ROUTING,
                    MapdustBug.ONEWAY_ROAD,
                    MapdustBug.BLOCKED_STREET,
                    MapdustBug.MISSING_STREET,
                    MapdustBug.ROUNDABOUT_ISSUE,
                    MapdustBug.MISSING_SPEED_INFO,
                    MapdustBug.OTHER);

            notifyDataSetChanged();
        }


        @Override
        public View getView(int position, View convertView, ViewGroup parent)
        {
            return getCustomView(position, convertView, parent);
        }


        @Override
        public View getDropDownView(int position, View convertView, ViewGroup parent)
        {
            return getCustomView(position, convertView, parent);
        }


        private View getCustomView(int position, View convertView, ViewGroup parent)
        {
            View v = convertView != null ? convertView : LayoutInflater.from(getContext()).inflate(R.layout.row_mapdust_bug_type, parent, false);

            ImageView icon = v.findViewById(R.id.imgvIcon);
            TextView type = v.findViewById(R.id.txtvType);

            switch (position)
            {
                case 0:
                    icon.setImageDrawable(Images.get(R.drawable.mapdust_wrong_turn));
                    type.setText(R.string.wrong_turn);
                    break;

                case 1:
                    icon.setImageDrawable(Images.get(R.drawable.mapdust_bad_routing));
                    type.setText(R.string.bad_routing);
                    break;

                case 2:
                    icon.setImageDrawable(Images.get(R.drawable.mapdust_oneway_road));
                    type.setText(R.string.oneway_road);
                    break;

                case 3:
                    icon.setImageDrawable(Images.get(R.drawable.mapdust_blocked_street));
                    type.setText(R.string.blocked_street);
                    break;

                case 4:
                    icon.setImageDrawable(Images.get(R.drawable.mapdust_missing_street));
                    type.setText(R.string.missing_street);
                    break;

                case 5:
                    icon.setImageDrawable(Images.get(R.drawable.mapdust_roundabout_issue));
                    type.setText(R.string.roundabout_issue);
                    break;

                case 6:
                    icon.setImageDrawable(Images.get(R.drawable.mapdust_missing_speed_info));
                    type.setText(R.string.missing_speed_info);
                    break;

                default:
                    icon.setImageDrawable(Images.get(R.drawable.mapdust_other));
                    type.setText(R.string.other);
                    break;
            }

            return v;
        }
    }

    @OptionsItem(android.R.id.home)
    void onHomeBtn()
    {
        setResult(RESULT_CANCELED);
        finish();
    }
}
