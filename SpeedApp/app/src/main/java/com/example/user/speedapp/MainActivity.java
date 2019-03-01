package com.example.user.speedapp;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Calendar;
import java.util.Date;
import java.util.UUID;

public class MainActivity extends AppCompatActivity implements SensorEventListener,LocationListener {

    SensorManager sensorManager;
    Sensor sensor;
    LocationManager locationManager;
    TextView textView;
    double velocity,lon ,lat ;
    String datestr;
    SQLiteDatabase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //allazw xrvma sto layout
        getWindow().getDecorView().setBackgroundColor(Color.CYAN);

        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        sensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);//MALLON AYTO DEN XREIAZETE

        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

        textView = findViewById(R.id.textView);

        //tha dhmioyrghsw thn bash
        db = openOrCreateDatabase("VelocityDB",MODE_PRIVATE,null);

        db.execSQL("CREATE TABLE IF NOT EXISTS `Velocity` (" +
                "`guid` TEXT," +
                "`velocity` REAL ," +
                "`longtitude` REAL," +
                "`latitude` REAL," +
                "`timestamp` TEXT," +
                "PRIMARY KEY(`guid`)" +
                ");");

    }

    public void velocity(View view)
    {
        if(ActivityCompat.checkSelfPermission(this,Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED)
        {
            ActivityCompat.requestPermissions(this,new String[] {Manifest.permission.ACCESS_FINE_LOCATION},123);
        }
        else
        {
            //1 poios einai o provider 2 na dhlwsw kathe poso na stelneis tigma 3 na lavei allages poy metatkinithike 4 poios einai o listener
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,0,0,this);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        //an einai allow h denied sto grants results

        if(grantResults[0]== PackageManager.PERMISSION_GRANTED)
        {
            velocity(null);
        }
        else
        {
            Toast.makeText(this,"Please Allow",Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onSensorChanged(SensorEvent event) {

    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    @Override
    public void onLocationChanged(Location location) {
        velocity = location.getSpeed();
        textView.setText(velocity + " " + " km per hour");
        Date dateTimeNow = Calendar.getInstance().getTime();

        if (velocity >50)
        {
            lon = location.getLongitude();
            lat = location.getLatitude();

            datestr = dateTimeNow.toString();
            //h kathe eggrafh pernei ena monadiko guid
            String guid = UUID.randomUUID().toString();

            //edw prepei an thn grapsw sthn vash
            try
            {
                db.execSQL("INSERT OR IGNORE INTO 'Velocity' VALUES ('"+ guid +"','" + velocity +" ','"+ lon +"','"+ lat +" ','"+ datestr + "');");
            }
            catch(SQLException e)
            {
                showmessage("Error", e.toString());
            }


            //allazei xrvma h othonh se kokkino epeidh exoume ypervei to orio tayxthtas
            getWindow().getDecorView().setBackgroundColor(Color.RED);

            //petaei kai mhnyma ston xrhsth
            showmessage("Alert","Driving Over the Speed limit");
        }

    }

    public void violations(View view)
    {
        StringBuffer buffer = new StringBuffer();
        Cursor cursor = db.rawQuery("SELECT * FROM Velocity ;",null);
        if (cursor.getCount()==0)
            Toast.makeText(this,"No records found...",Toast.LENGTH_LONG).show();
        else {
            while (cursor.moveToNext()){
                buffer.append("Guid: " + cursor.getString(0)+"\n");
                buffer.append("Velocity: " + cursor.getDouble(1)+"\n");
                buffer.append("Longitude: " + cursor.getDouble(2)+"\n");
                buffer.append("Latitude: " + cursor.getDouble(3)+"\n");
                buffer.append("Time: " + cursor.getString(4)+"\n");
                buffer.append("--------------------------\n");
            }
            String s = buffer.toString();
            showmessage("Records",s);
        }
        cursor.close();
    }

    public void showmessage(String title, String message){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(title);
        builder.setMessage(message);
        builder.setCancelable(true);
        builder.show();
    }
    //maw paei stoyw xartes
    public void map(View view)
    {
        //Intent intent = new Intent(this,);
        //startActivity(intent);
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }
}
