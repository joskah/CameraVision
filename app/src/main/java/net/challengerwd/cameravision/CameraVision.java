package net.challengerwd.cameravision;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.NotificationCompat;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

public class CameraVision extends AppCompatActivity {
    // Access any saved data.
    SharedPreferences preferences = getSharedPreferences("configdata", Context.MODE_PRIVATE);
    String saved_interval = preferences.getString("interval",null);
    String saved_path = preferences.getString("path", null);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera_vision);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        setupNotifications();
        LoadConfig();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_camera_vision, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        return super.onOptionsItemSelected(item);
    }

    public void LoadConfig() {
        // THIS IS DEMO CODE THAT NEEDS TO BE EDITED
        // If there is a previously saved configuration, this
        // will load those settings.
        if (saved_interval != null) {
            // handle the value
            spnrInterval.setSelection(prefs.getInt("interval",0));
        } else {
            //SET DEFAULT CONFIG
        }
        if (saved_path != null) {

        } else {
            //SET DEFAULT CONFIG
        }
    }

    public void SaveConfig(View view) {
        // THIS IS DEMO CODE THAT NEEDS TO BE EDITED
        SharedPreferences.Editor editor = sharedpreferences.edit();
        int selectedPosition = spnrInterval.getSelectedItemPosition()
        editor.putInt("interval", selectedPosition);
        editor.commit();
    }

    public void StartCVService(View view) {

        showNotification();

    }

    public void btnStopCVService(View view) {

        StopCVService();

    }

    public void StopCVService() {

        clearNotification();

    }

    // This is the section of code that handles the notifications.
    // This was heavily referenced from:
    // http://stackoverflow.com/questions/9949726/persistent-service-icon-in-notification-bar
    private static final int NOTIFICATION = 1;
    public static final String STOP_SERVICE = "StopCVService";
    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        String action = intent.getAction();
        if (action == null) {
            return;
        }
        switch (action) {
            case CameraVision.STOP_SERVICE:
                StopCVService();
                break;
        }
    }
    @Nullable
    private NotificationManager mNotificationManager = null;
    private final NotificationCompat.Builder mNotificationBuilder = new NotificationCompat.Builder(this);

    private void setupNotifications() { //called in onCreate()
        if (mNotificationManager == null) {
            mNotificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        }
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0,
                new Intent(this, CameraVision.class)
                        .setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP),
                0);
        PendingIntent pendingCloseIntent = PendingIntent.getActivity(this, 0,
                new Intent(this, CameraVision.class)
                        .setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP)
                        .setAction(STOP_SERVICE),
                0);
        mNotificationBuilder
                .setSmallIcon(R.drawable.camera_icon)
                .setCategory(NotificationCompat.CATEGORY_SERVICE)
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                .setContentTitle(getText(R.string.app_name))
                .setWhen(System.currentTimeMillis())
                .setContentIntent(pendingIntent)
                .addAction(android.R.drawable.ic_menu_close_clear_cancel,
                        getString(R.string.btnStopSvc), pendingCloseIntent)
                .setOngoing(true);
    }

    private void showNotification() {
        mNotificationBuilder
                .setTicker(getText(R.string.svcRunning))
                .setContentText(getText(R.string.svcRunning));
        if (mNotificationManager != null) {
            mNotificationManager.notify(NOTIFICATION, mNotificationBuilder.build());
        }
    }
    private void clearNotification() {
        NotificationManager notificationManager = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.cancel(NOTIFICATION);
    }
}
