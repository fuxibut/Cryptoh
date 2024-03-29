package com.secusec.cryptoh;

import android.content.ClipData;
import android.content.ClipDescription;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.preference.PreferenceManager;

import java.util.Objects;

public class MainActivity extends OptionsMenu {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        PreferenceManager.setDefaultValues(this, R.xml.root_preferences, false);
        handleIntent();

    }

    /*
     * Handle Intent for text being shared with the app
     */
    private void handleIntent() {
        Intent intent = getIntent();
        String action = intent.getAction();
        String type = intent.getType();
        if (Intent.ACTION_SEND.equals(action) && type != null && type.equals("text/plain")) {
            EditText plainEditText = findViewById(R.id.editText);
            plainEditText.setText(intent.getStringExtra(Intent.EXTRA_TEXT));
        }
    }

    /*
     *  Action performed when the "Encrypt" button is pressed
     */
    public void encrypt(View v) throws Exception {
        // The text of the textfield
        EditText plainEditText = findViewById(R.id.editText);
        String plainText = plainEditText.getText().toString();
        // The password
        EditText passwordEditText = findViewById(R.id.editTextPassword);
        String password = passwordEditText.getText().toString();
        String encryptedText = "";
        if (password.isEmpty() || password.length() < 8) {
            // Empty password
            showToast(getString(R.string.error_password));
            passwordEditText.requestFocus();
        } else {
            if (plainText.isEmpty()) {
                // Empty textfield
                ClipboardManager clipboardManager = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
                assert clipboardManager != null;
                if (clipboardManager.hasPrimaryClip() && Objects.requireNonNull(clipboardManager.getPrimaryClipDescription()).hasMimeType(ClipDescription.MIMETYPE_TEXT_PLAIN)) {
                    // Text in clipboard -> Encryption from clipboard
                    ClipData clipData = clipboardManager.getPrimaryClip();
                    assert clipData != null;
                    plainText = (String) clipData.getItemAt(0).getText();
                    encryptedText = Blowfish.encrypt(plainText, password);
                } else {
                    // No text in textfield or clipboard
                    showToast(getString(R.string.error_text));
                    plainEditText.requestFocus();
                }
            } else {
                // Text in textfield -> Encryption from textfield
                encryptedText = Blowfish.encrypt(plainText, password);
            }
        }
        // TODO: Add array instead of constants for actions
        if (!encryptedText.isEmpty()) {
            plainEditText.setText(encryptedText);
            // Retrieve setting actionAfterEncryption
            SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
            String actionAfterEncryption = sharedPref.getString("actionAfterEncryption", "-1");
            Intent sendIntent = new Intent();
            // Decide what to do after encrypting depending on setting actionAfterEncryption
            if (actionAfterEncryption.equals("ShareTextWithChooser")) {
                // Share with chooser
                sendIntent.setAction(Intent.ACTION_SEND);
                sendIntent.putExtra(Intent.EXTRA_TEXT, encryptedText);
                sendIntent.setType("text/plain");
                startActivity(Intent.createChooser(sendIntent, getString(R.string.message_send)));
            } else if (actionAfterEncryption.equals("ShareTextWithoutChooser")) {
                // Share without chooser
                sendIntent.setAction(Intent.ACTION_SEND);
                sendIntent.putExtra(Intent.EXTRA_TEXT, encryptedText);
                sendIntent.setType("text/plain");
                startActivity(sendIntent);
            } else if (actionAfterEncryption.equals("ClipTextToClipboard")) {
                // Copy text to clipboard
                ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                ClipData clip = ClipData.newPlainText("@string/encryptedText", encryptedText);
                assert clipboard != null;
                clipboard.setPrimaryClip(clip);
            } else {
                System.out.println(actionAfterEncryption);
            }
        }
    }

    /*
     *  Action performed when the "Decrypt" key is pressed
     */
    public void decrypt(View v) {
        // The text of the textfield
        EditText plainEditText = findViewById(R.id.editText);
        String plainText = plainEditText.getText().toString();
        // The password
        EditText passwordEditText = findViewById(R.id.editTextPassword);
        String password = passwordEditText.getText().toString();
        String decryptedText = "";
        if (password.isEmpty() || password.length() < 8) {
            // Empty password
            showToast(getString(R.string.error_password));
            passwordEditText.requestFocus();
        } else {
            if (plainText.isEmpty()) {
                // Empty textfield
                ClipboardManager clipboardManager = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
                assert clipboardManager != null;
                if (clipboardManager.hasPrimaryClip() && Objects.requireNonNull(clipboardManager.getPrimaryClipDescription()).hasMimeType(ClipDescription.MIMETYPE_TEXT_PLAIN)) {
                    // Text in clipboard -> Decryption from clipboard
                    ClipData clipData = clipboardManager.getPrimaryClip();
                    assert clipData != null;
                    plainText = (String) clipData.getItemAt(0).getText();
                    try {
                        decryptedText = Blowfish.decrypt(plainText, password);
                    } catch (Exception e) {
                        e.printStackTrace();
                        showToast(getString(R.string.error_blowfish));
                    }
                } else {
                    // No text in textfield or clipboard
                    showToast(getString(R.string.error_text));
                    plainEditText.requestFocus();
                }
            } else {
                // Text in textfield -> Encryption from textfield
                try {
                    decryptedText = Blowfish.decrypt(plainText, password);
                } catch (Exception e) {
                    e.printStackTrace();
                    showToast(getString(R.string.error_blowfish));
                }
            }
        }
        if (!decryptedText.isEmpty()) {
            plainEditText.setText(decryptedText);
        }
    }

    /*
     * Shows a short toast with given text
     */
    private void showToast(String text) {
        Context context = getApplicationContext();
        int duration = Toast.LENGTH_SHORT;
        Toast toast = Toast.makeText(context, text, duration);
        toast.show();
    }

}
