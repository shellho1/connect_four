package edu.msu.team15.connect4;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

public class LostConnectionActivity extends AppCompatActivity {


    String username;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lost_connection);

        final Intent intent = getIntent();
        username = intent.getExtras().get(WaitActivity.USERNAME).toString();
    }

    public void onCancel(View view) {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    public void onFind (View view) {
        Intent intent = new Intent(view.getContext(), WaitActivity.class);
        intent.putExtra("p1User",username);
        startActivity(intent);
        finish();
    }

}
