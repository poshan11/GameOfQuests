package com.example.aksha.beachhackhunt;

import android.content.Intent;
import android.graphics.Bitmap;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.ml.vision.FirebaseVision;
import com.google.firebase.ml.vision.common.FirebaseVisionImage;
import com.google.firebase.ml.vision.text.FirebaseVisionText;
import com.google.firebase.ml.vision.text.FirebaseVisionTextDetector;

import java.util.List;


public class QuestionActivity extends AppCompatActivity {

    TextView questionTextView;
    EditText answerEditText;
    String question = new String();
    String CorrectAnswer = new String();
    String answer = new String();
    Button submitButton;
    Button snapBtn;
    private Bitmap imageBitmap;


    static final int REQUEST_IMAGE_CAPTURE = 1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_question);

        questionTextView = findViewById(R.id.questionTextView);
        answerEditText = findViewById(R.id.answerEditText);
        submitButton = findViewById(R.id.submitButton);
        snapBtn = findViewById(R.id.snapBtn);

        question = getIntent().getStringExtra("question");
        CorrectAnswer = getIntent().getStringExtra("answer");

        questionTextView.setText(question);

        if(getIntent().getBooleanExtra("isMultimedia", false))
            answerEditText.setVisibility(View.INVISIBLE);
        else
            snapBtn.setVisibility(View.INVISIBLE);


        snapBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dispatchTakePictureIntent();
            }
        });



        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                answer = answerEditText.getText().toString();
                Submit();

            }
        });


    }

    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            imageBitmap = (Bitmap) extras.get("data");
            detectTxt(imageBitmap);
        }
    }

    private void detectTxt(Bitmap imageBitmap) {
        FirebaseVisionImage image = FirebaseVisionImage.fromBitmap(imageBitmap);
        FirebaseVisionTextDetector detector = FirebaseVision.getInstance().getVisionTextDetector();
        detector.detectInImage(image).addOnSuccessListener(new OnSuccessListener<FirebaseVisionText>() {
            @Override
            public void onSuccess(FirebaseVisionText firebaseVisionText) {
                processTxt(firebaseVisionText);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

            }
        });
    }

    private void processTxt(FirebaseVisionText text) {
        List<FirebaseVisionText.Block> blocks = text.getBlocks();
        if (blocks.size() == 0) {
            Toast.makeText(getApplicationContext(), "No Text :(", Toast.LENGTH_LONG).show();
            return;
        }
        for (FirebaseVisionText.Block block : text.getBlocks()) {
            String txt = block.getText();
            answer = txt;
            Submit();
        }
    }

    public void Submit(){
        if (answer.equals(CorrectAnswer)){
            Toast.makeText(getApplicationContext(),"Correct Answer", Toast.LENGTH_SHORT).show();
            Marker mrkr;
            huntMapsActivity.correctAnswer++;
            if(huntMapsActivity.finalMarker){
                Intent intent = new Intent(getApplicationContext(), WinnerActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                finish();
                return;
            }
            mrkr = huntMapsActivity.markersGoogle.get(getIntent().getIntExtra("index",0));
            mrkr.setVisible(false);

            mrkr = huntMapsActivity.markersGoogle.get(getIntent().getIntExtra("index",0)+1);
            mrkr.setVisible(true);

            finish();

        }
        else {
            Toast.makeText(getApplicationContext(),"Incorrect Answer. Please Try Again", Toast.LENGTH_SHORT).show();
        }
    }
}
