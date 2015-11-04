package net.callofdroidy.volcbook;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.provider.ContactsContract;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;

import java.util.Random;

public class ActivityMain extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    private DatabaseHelper myDatabaseHelper;
    private Spinner spinner_level;
    private SQLiteDatabase dbIELTS;
    private int candidateAmount = 0; // total amount of words that meet the level requirement
    private boolean isFirstTimeTest = true;
    private boolean isLevelSelected = false;

    private Cursor cursorCurrentWordPool;
    private int testLevel = 0;
    private int lastTestLevel = 0;
    private EditText et_word;
    private EditText et_translation;
    private EditText et_example;
    private EditText et_note;
    private EditText et_level;
    private EditText et_synonyms;
    private EditText et_antonyms;

    private static final String[] projection = {"Id", "Word", "Translation", "Example", "Note", "Level", "Synonyms", "Antonyms"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        et_word = (EditText) findViewById(R.id.et_word);
        et_translation = (EditText) findViewById(R.id.et_translation);
        et_example = (EditText) findViewById(R.id.et_example);
        et_note = (EditText) findViewById(R.id.et_note);
        et_level = (EditText) findViewById(R.id.et_level);
        et_synonyms = (EditText) findViewById(R.id.et_syn);
        et_antonyms = (EditText) findViewById(R.id.et_ant);

        myDatabaseHelper = new DatabaseHelper(getApplicationContext());
        dbIELTS = myDatabaseHelper.getWritableDatabase();
        spinner_level = (Spinner) findViewById(R.id.spinner_level);
        ArrayAdapter<CharSequence> adapterLevels = ArrayAdapter.createFromResource(this, R.array.spinner_level, android.R.layout.simple_spinner_item);
        adapterLevels.setDropDownViewResource(android.R.layout.simple_spinner_item);
        spinner_level.setAdapter(adapterLevels);
        spinner_level.setOnItemSelectedListener(this);

        findViewById(R.id.btn_next).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popAWord(testLevel);
            }
        });
    }

    public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) { //override method for OnItemSelect interface
        if(!isLevelSelected){
            spinner_level.setSelection(3);
            isLevelSelected = true;
        }
        testLevel =  Integer.valueOf((String) parent.getItemAtPosition(pos));

        Log.e("testLevel, lastLevel", testLevel + " " + lastTestLevel);
    }

    public void onNothingSelected(AdapterView<?> parent) { //override method for OnItemSelect interface
    }

    private void popAWord(int level){
        if(testLevel != lastTestLevel || isFirstTimeTest){ // if testLevel has changed, do another query. otherwise just get a word from cache
            cursorCurrentWordPool = dbIELTS.query("main", projection, "Level >= " + level, null, null, null, null);
            lastTestLevel = level;
            if(cursorCurrentWordPool != null){
                candidateAmount = cursorCurrentWordPool.getCount();
                cursorCurrentWordPool.moveToPosition(new Random().nextInt(candidateAmount));
                et_word.setText(cursorCurrentWordPool.getString(1));
                et_translation.setText(cursorCurrentWordPool.getString(2));
                et_example.setText(cursorCurrentWordPool.getString(3));
                et_note.setText(cursorCurrentWordPool.getString(4));
                et_level.setText(cursorCurrentWordPool.getString(5));
                et_synonyms.setText(cursorCurrentWordPool.getString(6));
                et_antonyms.setText(cursorCurrentWordPool.getString(7));
                isFirstTimeTest = false;
            }
        }else{
            cursorCurrentWordPool.moveToPosition(new Random().nextInt(candidateAmount));
            et_word.setText(cursorCurrentWordPool.getString(1));
            et_translation.setText(cursorCurrentWordPool.getString(2));
            et_example.setText(cursorCurrentWordPool.getString(3));
            et_note.setText(cursorCurrentWordPool.getString(4));
            et_level.setText(cursorCurrentWordPool.getString(5));
            et_synonyms.setText(cursorCurrentWordPool.getString(6));
            et_antonyms.setText(cursorCurrentWordPool.getString(7));
        }
        Log.e("last level", lastTestLevel + "");
    }
}
