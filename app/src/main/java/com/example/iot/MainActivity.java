package com.example.iot;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONObject;

import java.io.DataOutput;
import java.io.DataOutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

public class MainActivity extends AppCompatActivity  implements SensorEventListener {
    public EditText editText,editText2;
    public TextView textView1;
    private  SensorManager mSensorManager;
    private  Sensor mLightometer;
    private  Sensor mTemp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        Button button1 =  findViewById(R.id.button1);
        textView1 = findViewById(R.id.textView1);
        editText = findViewById(R.id.editText);
        editText2 = findViewById(R.id.editText2);
        setSupportActionBar(toolbar);
        mSensorManager = (SensorManager)getSystemService(SENSOR_SERVICE);
        mLightometer = mSensorManager.getDefaultSensor(Sensor.TYPE_LIGHT);
        mTemp = mSensorManager.getDefaultSensor(Sensor.TYPE_AMBIENT_TEMPERATURE);
        mSensorManager.registerListener(this, mLightometer, SensorManager.SENSOR_DELAY_NORMAL);
        mSensorManager.registerListener(this, mTemp, SensorManager.SENSOR_DELAY_NORMAL);
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        //Internet bağlantısı yoksa ihtiyac yok
        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
        button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendPost();
            }
        });
    }
    public  void  sendPost(){
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try{
                    URL url =new URL("http://iothook.com/api/latest/datas/");
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    conn.setRequestMethod("POST");
                    conn.setRequestProperty("Content-Type", "application/json");
                    conn.setDoOutput(true);
                    conn.setDoInput(true);
                    JSONObject jsonParam = new JSONObject();
                    jsonParam.put("api_key","f5669f6f-4e66-119b86481736f54175");
                    jsonParam.put("value_1",editText.getText());
                    jsonParam.put("value_2",editText2.getText());

                    Log.i("JSON",jsonParam.toString());
                    DataOutputStream os = new DataOutputStream(conn.getOutputStream());
                    os.writeBytes(jsonParam.toString());
                    //os.writeBytes((URLEncoder.encode(jsonParam.toString(), "UTF 8")));
                    os.flush();
                    os.close();

                    Log.i("STATUS",String.valueOf(conn.getResponseCode()));
                    //Toast.makeText(getApplicationContext(),"asdasd",Toast.LENGTH_LONG).show();
                    //Log.i("MSG",conn.getResponseMessage());
                    textView1.setText(conn.getResponseMessage());


                    conn.disconnect();
                }
                catch (Exception e){
                    e.printStackTrace();

                }

            }
        });
        thread.start();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        float f = event.values[0];
        if (event.sensor.getType() == Sensor.TYPE_AMBIENT_TEMPERATURE){

            editText.setText(String.valueOf(f));
        }
        if(event.sensor.getType() == Sensor.TYPE_LIGHT){
            editText2.setText(String.valueOf(f));
        }






    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }
}
