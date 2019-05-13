package com.example.smshu.project10;

import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;

import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import android.widget.TextView;
import android.widget.Toast;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Music extends AppCompatActivity {



    FloatingActionButton fb ;
    TextView text;
    private MediaPlayer mp = new MediaPlayer();
    String[] listviewTitle = new String[]{
            "Song 1", "Song 2","Song 3",
    };

    List<String> list;

    int[] listviewImage = new int[]{
            R.drawable.profile_pc, R.drawable.profile_pc,R.drawable.profile_pc
    };
    String[] listviewShortDescription = new String[]{
            "Description", "Description", "Description",
    };
    int j;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_music);
        Intent intent = getIntent();
        String user_name = intent.getStringExtra("EMOTION");
        if(user_name.equals("sad"))
        {
            j=6;
        }
        else if(user_name.equals("happy"))
        {
            j=3;
        }
        else
        {
            j=0;
        }
        text = findViewById(R.id.text);
        text.setText("Songs: "+user_name);
        List<HashMap<String, String>> aList = new ArrayList<HashMap<String, String>>();
        Toast toast = Toast.makeText(getApplicationContext(),"Here Is Our Selected PlayList Enjoy :)",Toast.LENGTH_LONG);
        toast.show();
        for (int i = 0; i < 3; i++) {
            HashMap<String, String> hm = new HashMap<String, String>();
            hm.put("listview_title", listviewTitle[i]);
            hm.put("listview_discription", listviewShortDescription[i]);
            hm.put("listview_image", Integer.toString(listviewImage[i]));
            aList.add(hm);
        }
        list = new ArrayList<>();
        fb = findViewById(R.id.floa);
        Field[] fields = R.raw.class.getFields();

        for(int i=j;i<j+3;i++)
        {
            list.add(fields[i].getName());
        }
        String[] from = {"listview_image", "listview_title", "listview_discription"};
        int[] to = {R.id.listview_image, R.id.listview_item_title, R.id.listview_item_short_description};

        SimpleAdapter simpleAdapter = new SimpleAdapter(getBaseContext(), aList, R.layout.listview, from, to);
        ListView androidListView = findViewById(R.id.list_view);
        androidListView.setAdapter(simpleAdapter);

        androidListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                int resId=getResources().getIdentifier(list.get(position),"raw",getPackageName());
                if (mp != null) {
                    mp.stop();
                    mp.reset();
                    mp.release();
                }
                mp = MediaPlayer.create(Music.this,resId);
                mp.start();
            }
        });



        fb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mp.isPlaying()) {
                    mp.pause();
                }
            }
        });



    }



}
