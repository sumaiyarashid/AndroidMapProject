package com.example.dipapc.mymapproject;

import android.app.Dialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;

public class MainActivity extends AppCompatActivity {
    private static  final String TAG = "MainActivity";
    //the error we are gonna
    //handle if the user don't have the current SDK version
    private static  final int ERROR_DIALOG_REQUEST = 9001;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if(isServicesOK()){
            init();
        }
    }
    public void init(){
        Button btn_map = (Button)findViewById(R.id.btn_map);
        btn_map.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this,MapActivity.class));
            }
        });
    }

    //method for checking the version
    public boolean isServicesOK(){
        Log.d(TAG,"isServiceOK():checking google services version");
        int available = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(MainActivity.this);
        if(available == ConnectionResult.SUCCESS){
            //everything is fine and the user can make map requests
            Log.d(TAG,"Google play services is working");
            Toast.makeText(this,"Google play services is working",Toast.LENGTH_SHORT).show();
            return true;

        }
        else if(GoogleApiAvailability.getInstance().isUserResolvableError(available)){
            // an error occurred but we can resolve it
            Log.d(TAG,"isServiceOK():an error occurred but we can fix it");
            //this where we can get a dialog straight from google for this kind of error
            Dialog dialog = GoogleApiAvailability.getInstance().getErrorDialog(MainActivity.this,available,ERROR_DIALOG_REQUEST);
            dialog.show();
        }
        else{
            Toast.makeText(this,"You can't make requests",Toast.LENGTH_SHORT).show();
        }
        return false;


    }
}
