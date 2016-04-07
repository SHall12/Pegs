package com.shanekevinsam.pegs;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void startGameActivity(View v){
        startActivity(new Intent(this, GameActivity.class));
    }

    public void startSettingsActivity(View v){
        startActivity(new Intent(this, SettingsActivity.class));
    }

    // TODO Start DB + Highscores activity
    // TODO Create DB w/ name, score and date
    // TODO Start content content provider

    // TODO Settings: Name, Sound:[Music, Effects], Reset score, Starting Peg:Random, 1-4
}
