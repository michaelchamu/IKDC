package com.example.icarus.ikdc;

import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.example.icarus.ikdc.database.DataAccessObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;


public class TextEditor extends ActionBarActivity {
    private DataAccessObject myData;
    ImageButton save;
    ImageButton cancel;
    EditText text;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_text_editor);

        myData = new DataAccessObject(this);
        myData.open();

        save = (ImageButton) findViewById(R.id.button8);
        cancel = (ImageButton) findViewById(R.id.button9);
        text = (EditText) findViewById(R.id.editText);

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {

                    int txtNum = myData.retrieveTextNum() + 1;
                    File myFile = new File(Environment.getExternalStorageDirectory() + "/IKDC/commonStorage/text/text_" + txtNum + ".text");//set save path and title here

                    myFile.createNewFile();
                    FileOutputStream fOut = new FileOutputStream(myFile);
                    OutputStreamWriter myOutWriter =
                            new OutputStreamWriter(fOut);
                    myOutWriter.append(text.getText());
                    myOutWriter.close();
                    fOut.close();

                    myData.createCText(myFile.getName(), myFile.getAbsolutePath());

                    Toast.makeText(getBaseContext(),
                            "Creation Complete!",
                            Toast.LENGTH_SHORT).show();
                } catch (Exception e) {
                    Toast.makeText(getBaseContext(), e.getMessage(),
                            Toast.LENGTH_SHORT).show();
                }
                finally{
                    Intent mainMenu = new Intent(getApplicationContext(), MainActivity.class);

                    myData.close();

                    startActivity(mainMenu);
                }
            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_text_editor, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
