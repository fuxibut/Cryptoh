package com.secusec.cryptoh;

import android.content.Intent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import androidx.appcompat.app.AppCompatActivity;

public class OptionsMenu extends AppCompatActivity {

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        return true;
    }

    public void onAboutClick(MenuItem menuItem) {
        Intent intent = new Intent(this, AboutActivity.class);
        startActivity(intent);
    }

}
