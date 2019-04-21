package com.example.aksha.beachhackhunt;

//import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

public class CameraActivity extends AppCompatActivity {

//    FloatingActionButton cameraActionButton;
    TextView questionCameraTextView;
    String answer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);
        questionCameraTextView = findViewById(R.id.questionCameraTextView);
//        cameraActionButton = findViewById(R.id.OpenCameraButton);
    }
}
