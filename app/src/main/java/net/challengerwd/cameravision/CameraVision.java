package net.challengerwd.cameravision;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.NotificationCompat;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

public class CameraVision extends AppCompatActivity {
    // Access any saved data.
    int backButtonCount=0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera_vision);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        setupNotifications();
        LoadConfig();
    }

    private Boolean exit = false;
    @Override
    public void onBackPressed() {
        if (exit) {
            finish(); // finish activity
        } else {
            Toast.makeText(this, "Press Back again to Exit.",
                    Toast.LENGTH_SHORT).show();
            exit = true;
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    exit = false;
                }
            }, 3 * 1000);

        }

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
        SharedPreferences preferences = getSharedPreferences("configdata", Context.MODE_PRIVATE);
        Integer saved_interval = preferences.getInt("interval", 404);
        String saved_path = preferences.getString("path", null);

        // If there is a previously saved configuration, this
        // will load those settings.
        Spinner spnrInterval = (Spinner) findViewById(R.id.spnrInterval);
        EditText filepath = (EditText) findViewById(R.id.filepath);

        if (saved_interval != 404) {
            spnrInterval.setSelection(saved_interval);
        } else {
            //SET DEFAULT CONFIG
            spnrInterval.setSelection(0);
        }
        if (saved_path != null) {
            filepath.setText(saved_path);
        } else {
            //SET DEFAULT CONFIG
            filepath.setText("/sdcard/DCIM/CameraVision.jpg");
        }
    }

    public void SaveConfig(View view) {
        SharedPreferences preferences = getSharedPreferences("configdata", Context.MODE_PRIVATE);
        Spinner spnrInterval = (Spinner) findViewById(R.id.spnrInterval);
        EditText filepath = (EditText) findViewById(R.id.filepath);
        SharedPreferences.Editor editor = preferences.edit();
        int selectedPosition = spnrInterval.getSelectedItemPosition();
        String selectedPath = filepath.getText().toString();
        editor.putInt("interval", selectedPosition);
        editor.putString("path", selectedPath);
        editor.commit();
        Toast.makeText(getApplicationContext(), "Preferences Saved", Toast.LENGTH_SHORT).show();
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
