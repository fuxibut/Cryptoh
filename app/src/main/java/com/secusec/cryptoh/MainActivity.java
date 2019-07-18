package com.secusec.cryptoh;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ClipData;
import android.content.ClipDescription;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

// TODO: Insert error messages into resources
public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Intent intent = getIntent();
        String action = intent.getAction();
        String type = intent.getType();
        if (Intent.ACTION_SEND.equals(action) && type != null && type.equals("text/plain")) {
            EditText plainEditText = ((EditText) findViewById(R.id.editText));
            plainEditText.setText(intent.getStringExtra(Intent.EXTRA_TEXT));
        }
    }

    /*
     *  Action performed when the "Encrypt" button is pressed
     */
    public void encrypt(View v) throws Exception {
        EditText plainEditText = ((EditText) findViewById(R.id.editText));
        String plainText = plainEditText.getText().toString();
        EditText passwordEditText = ((EditText) findViewById(R.id.editTextPassword));
        String password = passwordEditText.getText().toString();
        String encryptedText = "";

        if (password == null || password.isEmpty() || password.length() < 8) {
            showToast("Kein Passwort angegeben oder Passwort zu kurz.");
            passwordEditText.requestFocus();
        } else {
            if (plainText == null || plainText.isEmpty()) {
                ClipboardManager clipboardManager = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
                if (clipboardManager.hasPrimaryClip() && clipboardManager.getPrimaryClipDescription().hasMimeType(ClipDescription.MIMETYPE_TEXT_PLAIN)) {
                    // Encryption from clipboard
                    ClipData clipData = clipboardManager.getPrimaryClip();
                    plainText = (String) clipData.getItemAt(0).getText();
                    encryptedText = Blowfish.encrypt(plainText, password);
                } else {
                    showToast("Kein Text angegeben oder in Zwischenablage.");
                    plainEditText.requestFocus();
                }
            } else {
                // Encryption from textfield
                encryptedText = Blowfish.encrypt(plainText, password);
            }
        }
        if (!encryptedText.isEmpty()) {
            plainEditText.setText(encryptedText);

            Intent sendIntent = new Intent();
            sendIntent.setAction(Intent.ACTION_SEND);
            sendIntent.putExtra(Intent.EXTRA_TEXT, encryptedText);
            sendIntent.setType("text/plain");
            startActivity(Intent.createChooser(sendIntent, "Verschlüsselten Text versenden"));
        }

    }

    /*
     *  Action performed when the "Decrypt" key is pressed
     */
    public void decrypt(View v) throws Exception {
        EditText plainEditText = ((EditText) findViewById(R.id.editText));
        String plainText = plainEditText.getText().toString();
        EditText passwordEditText = ((EditText) findViewById(R.id.editTextPassword));
        String password = passwordEditText.getText().toString();
        String decryptedText = "";

        if (password == null || password.isEmpty() || password.length() < 8) {
            showToast("Kein Passwort angegeben oder Passwort zu kurz.");
            passwordEditText.requestFocus();
        } else {
            if (plainText == null || plainText.isEmpty()) {
                ClipboardManager clipboardManager = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
                if (clipboardManager.hasPrimaryClip() && clipboardManager.getPrimaryClipDescription().hasMimeType(ClipDescription.MIMETYPE_TEXT_PLAIN)) {
                    // Decryption from clipboard
                    ClipData clipData = clipboardManager.getPrimaryClip();
                    plainText = (String) clipData.getItemAt(0).getText();
                    decryptedText = Blowfish.decrypt(plainText, password);
                } else {
                    showToast("Kein Text angegeben oder in Zwischenablage.");
                    plainEditText.requestFocus();
                }
            } else {
                // Decryption from textfield
                decryptedText = Blowfish.decrypt(plainText, password);
            }
        }
        if (!decryptedText.isEmpty()) {
            plainEditText.setText(decryptedText);
        }
    }

    /*
     * Shows a short toast with text
     */
    private void showToast(String text) {
        Context context = getApplicationContext();
        int duration = Toast.LENGTH_SHORT;
        Toast toast = Toast.makeText(context, text, duration);
        toast.show();
    }

}
