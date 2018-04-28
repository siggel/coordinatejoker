package com.github.siggel.coordinatejoker;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.support.graphics.drawable.VectorDrawableCompat;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.content.res.AppCompatResources;
import android.text.Html;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

/**
 * Activity showing about page
 */
public class AboutActivity extends AppCompatActivity {

    /**
     * Android onCreate method
     *
     * @param savedInstanceState as defined by android
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);

        final String name = getString(R.string.title_app_name);
        final String version = getVersion();
        final String email = "coordinatejoker@gmx-topmail.de";
        final String linebreak = "<br>";
        final String aboutHtml = name + linebreak
                + getString(R.string.string_version) + ": " + version + linebreak
                + linebreak
                + getString(R.string.string_contact) + ": " + email + linebreak;
        ((TextView) findViewById(R.id.aboutTextView)).setText(Html.fromHtml(aboutHtml));

        // programmatically add icons to buttons (as it does not work from xml for pre-Lollipop)
        setRightDrawableOfTextView(R.id.showTermsOfService, R.drawable.arrow_right_icon);
        setRightDrawableOfTextView(R.id.showPrivacyPolicy, R.drawable.arrow_right_icon);
        setRightDrawableOfTextView(R.id.showOpenSourceLicenses, R.drawable.arrow_right_icon);
    }

    /**
     * method for adding text view's right drawable programmatically
     *
     * @param viewId     text view's id
     * @param drawableId drawable id
     */
    @SuppressWarnings("SameParameterValue")
    private void setRightDrawableOfTextView(int viewId, int drawableId) {
        Drawable drawable;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            drawable = AppCompatResources
                    .getDrawable(this, drawableId);
        } else {
            drawable = VectorDrawableCompat
                    .create(this.getResources(), drawableId, null);
        }
        TextView textView = findViewById(viewId);
        textView.setCompoundDrawablesWithIntrinsicBounds(
                null, null, drawable, null);
    }


    /**
     * method called when user selected an item from the options menu
     *
     * @param item as defined by android
     * @return boolean as defined by android
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        switch (item.getItemId()) {
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(this);
                return true;
            default:
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * get app's version number
     *
     * @return app version as string
     */
    private String getVersion() {
        try {
            return getPackageManager().getPackageInfo(getPackageName(), 0).versionName;
        } catch (PackageManager.NameNotFoundException e) {
            return "unknown";
        }
    }

    /**
     * method called when user clicks terms of service button
     *
     * @param view view just required syntactically here
     */
    public void openTermsOfService(@SuppressWarnings("unused") View view) {
        startActivity(new Intent(this, TermsOfServiceActivity.class));
    }

    /**
     * method called when user clicks privacy policy button
     *
     * @param view view just required syntactically here
     */
    public void openPrivacyPolicy(@SuppressWarnings("unused") View view) {
        startActivity(new Intent(this, PrivacyPolicyActivity.class));
    }

    /**
     * method called when user clicks open source licenses button
     *
     * @param view view just required syntactically here
     */
    public void openOpenSourceLicenses(@SuppressWarnings("unused") View view) {
        startActivity(new Intent(this, OpenSourceLicensesActivity.class));
    }
}
