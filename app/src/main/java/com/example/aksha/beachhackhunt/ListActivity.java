package com.example.aksha.beachhackhunt;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ListActivity extends AppCompatActivity {

    public ArrayList<String> gameNamesList;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);


        FirebaseDatabase firebaseDatabase =  FirebaseDatabase.getInstance("https://gameoftreasures-1555808035264.firebaseio.com/");
        DatabaseReference databaseReference = firebaseDatabase.getReference();

        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Log.e("DataSnap of Game Name", String.valueOf(dataSnapshot.toString()));
                Map<String, Object> games = (Map<String,Object>) dataSnapshot.getValue();
                ArrayList<String> gameNamesArrayList = new ArrayList<>();


                for(Map.Entry<String, Object> entry : games.entrySet()){
                    gameNamesArrayList.add(entry.getKey().toString());
                    Log.e("gameName",gameNamesArrayList.toString());
                }

                final String items[] = new String[gameNamesArrayList.size()];

                Log.e("GameNameArrayList" , gameNamesArrayList.toString());

                for (int i =0; i <gameNamesArrayList.size();i++){
                    items[i] = gameNamesArrayList.get(i);
                }


                ListView listView = findViewById(R.id.listView);
                ArrayAdapter<String> adapter = new ArrayAdapter<>(getApplicationContext(), R.layout.list_white_text, items);
                listView.setAdapter(adapter);
                listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        Intent intent = new Intent(getApplicationContext(), huntMapsActivity.class);
                        intent.putExtra("gameName", items[position]);
                        startActivity(intent);
                        finish();
                    }
                });
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });






    }
    private void getNames(Map<String, Object> games){

        ArrayList<String> gameNames = new ArrayList<>();

        for(Map.Entry<String, Object> entry : games.entrySet()){
                gameNames.add(entry.getKey().toString());
                Log.e("gameName",gameNames.toString());
        }

        this.gameNamesList =  gameNames;
        Log.e("gameNameList",this.gameNamesList.toString());
    }
}
