package helperservices.my.sngv.Sms;

import android.Manifest;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.databinding.DataBindingUtil;
import android.icu.util.Calendar;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.provider.Telephony;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.PhoneNumberUtils;
import android.telephony.SmsManager;
import android.view.View;
import android.widget.Button;

import java.io.ByteArrayOutputStream;
import java.lang.reflect.Method;
import java.util.GregorianCalendar;

import helperservices.my.sngv.Sms.databinding.SendSmsBinding;

public class SmsActivity extends AppCompatActivity {
    Context context;
    Button sendSmsButton;
    SendSmsBinding smsBinding;
    String defaultSmsApp;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = this;
        smsBinding = DataBindingUtil.setContentView(this, R.layout.send_sms);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            defaultSmsApp = Telephony.Sms.getDefaultSmsPackage(context);
        }

    }

    public void sendSmsButtonOnClick(View view) {
        final String myPackageName = getPackageName();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {

            if (!Telephony.Sms.getDefaultSmsPackage(context).equals(myPackageName)) {

                //Change the default sms app to my app
                Intent intent = new Intent(Telephony.Sms.Intents.ACTION_CHANGE_DEFAULT);
                intent.putExtra(Telephony.Sms.Intents.EXTRA_PACKAGE_NAME, context.getPackageName());
                startActivityForResult(intent, 1);

            }
        }
    }

    //Write to the default sms app
    private void WriteSms(String message, String phoneNumber) {

        //Put content values
        ContentValues values = new ContentValues();
        values.put(Telephony.Sms.ADDRESS, phoneNumber);
        values.put(Telephony.Sms.DATE, System.currentTimeMillis());
        values.put(Telephony.Sms.BODY, message);

        //Insert the message
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            context.getContentResolver().insert(Telephony.Sms.Sent.CONTENT_URI, values);
        }
        else {
            context.getContentResolver().insert(Uri.parse("content://sms/sent"), values);
        }

        //Change my sms app to the last default sms
        Intent intent = new Intent(Telephony.Sms.Intents.ACTION_CHANGE_DEFAULT);
        intent.putExtra(Telephony.Sms.Intents.EXTRA_PACKAGE_NAME, defaultSmsApp);
        context.startActivity(intent);
    }

    //Get result from default sms dialog pops up
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == 1) {
            // Make sure the request was successful

                final String myPackageName = getPackageName();
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                    if (Telephony.Sms.getDefaultSmsPackage(context).equals(myPackageName)) {

                        //Write to the default sms app
                        WriteSms("body", "sender");
                    }

            }
        }

    }
}