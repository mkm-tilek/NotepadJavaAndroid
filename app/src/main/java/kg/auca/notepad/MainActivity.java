package kg.auca.notepad;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.DocumentsContract;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.URI;
import java.util.Objects;

import lib.folderpicker.FolderPicker;

public class MainActivity extends AppCompatActivity {

    final int WRITE_REQUEST_CODE = 1;
    final int OPEN_REQUEST_CODE  = 2;
    private EditText editText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        editText = (EditText) findViewById(R.id.editText);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_notepad_menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch (id) {
            case R.id.saveText:
                openBrowser(WRITE_REQUEST_CODE);
                return true;
            case R.id.openText:
                openBrowser(OPEN_REQUEST_CODE);
                return true;
            case R.id.finishText:
                finish();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == WRITE_REQUEST_CODE) {
            switch (resultCode) {
                case Activity.RESULT_OK:
                    if (data != null && data.getData() != null) {
                        writeInFile(data.getData(), editText.getText().toString());
                    }
                    break;
                case Activity.RESULT_CANCELED:
                    break;
            }
        } else if (requestCode == OPEN_REQUEST_CODE) {
            switch (resultCode) {
                case Activity.RESULT_OK:
                    if (data != null && data.getData() != null) {
                        try {
                            readFromFile(data.getData());
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                    break;
                case Activity.RESULT_CANCELED:
                    break;
            }
        }
    }

    private void openBrowser(int requestCode) {
        String action = requestCode == WRITE_REQUEST_CODE ? Intent.ACTION_CREATE_DOCUMENT : Intent.ACTION_OPEN_DOCUMENT;
        Intent intent = new Intent(action);;
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_TITLE, "");
        startActivityForResult(intent, requestCode);
    }

    private void writeInFile(@NonNull Uri uri, @NonNull String text) {
        OutputStream outputStream;
        try {
            outputStream = getContentResolver().openOutputStream(uri);
            BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(outputStream));
            bw.write(text);
            bw.flush();
            bw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void readFromFile(Uri uri) throws IOException {
        String line;
        StringBuilder stringBuilder = new StringBuilder();
        InputStream inputStream = getContentResolver().openInputStream(uri);
        InputStreamReader inputStreamReader = new InputStreamReader(Objects.requireNonNull(inputStream));
        BufferedReader reader = new BufferedReader(inputStreamReader);

        while ((line = reader.readLine()) != null) {
            stringBuilder.append(line);
        }
        editText.setText(stringBuilder.toString());
    }

}