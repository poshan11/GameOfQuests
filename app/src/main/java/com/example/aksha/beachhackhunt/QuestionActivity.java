package com.example.aksha.beachhackhunt;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;


public class QuestionActivity extends AppCompatActivity {

    TextView questionTextView;
    EditText answerEditText;
    String question = new String();
    String CorrectAnswer = new String();
    String answer = new String();
    Button submitButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_question);

        questionTextView = findViewById(R.id.questionTextView);
        answerEditText = findViewById(R.id.answerEditText);
        submitButton = findViewById(R.id.submitButton);

        question = getIntent().getStringExtra("question");
        CorrectAnswer = getIntent().getStringExtra("answer");

        questionTextView.setText(question);


        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                answer = answerEditText.getText().toString();
                if (answer.equals(CorrectAnswer)){
                    Toast.makeText(getApplicationContext(),"Correct Answer", Toast.LENGTH_SHORT).show();
                    Marker mrkr;
                    huntMapsActivity.correctAnswer++;
                    if(huntMapsActivity.finalMarker){
                        Intent intent = new Intent(getApplicationContext(), WinnerActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                        finish();
                    }
                    mrkr = huntMapsActivity.markersGoogle.get(huntMapsActivity.correctAnswer);
                    mrkr.setVisible(true);

                    finish();

                }
                else {
                    Toast.makeText(getApplicationContext(),"Incorrect Answer. Please Try Again", Toast.LENGTH_SHORT).show();
                }
            }
        });


    }
}
