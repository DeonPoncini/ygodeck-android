package com.example.ygodeck;

import android.content.Context;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import net.sectorsoftware.ygo.deck.DB;
import net.sectorsoftware.ygo.deck.CardSelector;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

public class MainActivity extends ActionBarActivity {

    static {
        System.loadLibrary("ygodeck-jni");
    }

    private ListView mListView;
    ArrayList<String> mListItems = new ArrayList<String>();
    ArrayAdapter<String> mListAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button increment = (Button) findViewById(R.id.button);
        increment.setOnClickListener(mOnIncrement);

        mListView = (ListView) findViewById(R.id.listView);
        mListAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, mListItems);
        mListView.setAdapter(mListAdapter);

        // copy the card database into an accessible location
        String cardFile = "card.db";
        String filename = getFilesDir() + File.separator + cardFile;
        try {
            // check if the file exists first
            File f = new File(filename);
            if (!f.exists()) {
                // copy the card db file so it is accessible by native applications
                InputStream fis = getResources().openRawResource(R.raw.card);
                FileOutputStream fos = openFileOutput(cardFile, Context.MODE_PRIVATE);
                int bytesRead = 0;
                do {
                    byte[] data = new byte[1024];
                    bytesRead = fis.read(data);
                    if (bytesRead == -1 || bytesRead == 0) {
                        break;
                    }
                    fos.write(data);
                } while (true);
                Toast.makeText(this, "Written file " + filename, Toast.LENGTH_LONG).show();
            }
            // setup the database
            DB.setPath(filename);

            // test round trip
            String s = DB.getPath();
            TextView textView = (TextView) findViewById(R.id.textView);
            textView.setText(s);
        } catch (FileNotFoundException e) {
            Toast.makeText(this, "Can't open " + filename, Toast.LENGTH_LONG).show();
        } catch (IOException e) {
            Toast.makeText(this, "Error reading " + filename, Toast.LENGTH_LONG).show();
        }

    }

    private View.OnClickListener mOnIncrement = new View.OnClickListener() {

        @Override
        public void onClick(View v) {
            // search for the cards
            CardSelector cs = new CardSelector();
            cs.name("Black");
            ArrayList<String> ret = cs.execute();
            mListAdapter.clear();
            mListAdapter.addAll(ret);
            mListAdapter.notifyDataSetChanged();
        }
    };

    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
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