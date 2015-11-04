package net.callofdroidy.volcbook;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import java.util.Random;

public class ActivityMain extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    private DatabaseHelper myDatabaseHelper;
    private Spinner spinner_level;
    private SQLiteDatabase dbIELTS;
    private Cursor cursorCurrentWordPool;
    private int currentWordId = 0;

    private int candidateAmount = 0; // total amount of words that meet the level requirement
    private boolean isFirstTimeTest = true;
    private boolean isLevelSelected = false;
    private int testLevel = 0;
    private int lastTestLevel = 0;

    private EditText et_search;
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

        et_search = (EditText) findViewById(R.id.et_search);
        et_word = (EditText) findViewById(R.id.et_word);
        et_translation = (EditText) findViewById(R.id.et_translation);
        et_example = (EditText) findViewById(R.id.et_example);
        et_note = (EditText) findViewById(R.id.et_note);
        et_level = (EditText) findViewById(R.id.et_level);
        et_synonyms = (EditText) findViewById(R.id.et_syn);
        et_antonyms = (EditText) findViewById(R.id.et_ant);

        initDBOperation();
        setUpSpinner();

        findViewById(R.id.btn_search).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                searchAWord(et_search.getText().toString());
            }
        });
        findViewById(R.id.btn_updateWord).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateTheWord(currentWordId);
            }
        });
        findViewById(R.id.btn_next).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popAWord(testLevel);
            }
        });
    }

    private void initDBOperation(){
        myDatabaseHelper = new DatabaseHelper(getApplicationContext());
        dbIELTS = myDatabaseHelper.getWritableDatabase();
    }

    private void setUpSpinner(){
        spinner_level = (Spinner) findViewById(R.id.spinner_level);
        ArrayAdapter<CharSequence> adapterLevels = ArrayAdapter.createFromResource(this, R.array.spinner_level, android.R.layout.simple_spinner_item);
        adapterLevels.setDropDownViewResource(android.R.layout.simple_spinner_item);
        spinner_level.setAdapter(adapterLevels);
        spinner_level.setOnItemSelectedListener(this);
    }

    public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) { //override method for OnItemSelect interface
        if(!isLevelSelected){ // if the user does not select a level, set 3 as default
            spinner_level.setSelection(3);  // set default selection
            isLevelSelected = true;
        }
        testLevel =  Integer.valueOf((String) parent.getItemAtPosition(pos));
        Log.e("testLevel, lastLevel", testLevel + " " + lastTestLevel);
    }

    public void onNothingSelected(AdapterView<?> parent) { //override method for OnItemSelect interface
    }

    private void updateTheWord(int Id){
        dbIELTS.delete("main", "Id = " + Id, null);
        ContentValues cv = new ContentValues();
        cv.put("Id", Id);
        cv.put("Word", et_word.getText().toString());
        cv.put("Translation", et_translation.getText().toString());
        cv.put("Example", et_example.getText().toString());
        cv.put("Note", et_note.getText().toString());
        cv.put("Level", et_level.getText().toString());
        cv.put("Synonyms", et_synonyms.getText().toString());
        cv.put("Antonyms", et_antonyms.getText().toString());
        dbIELTS.insert("main", null, cv);
        Toast.makeText(getApplicationContext(), "word updated", Toast.LENGTH_SHORT).show();
    }

    private void searchAWord(String targetWord){
        Cursor cursorSearchWord = dbIELTS.query("main", projection, "Word = '" + targetWord + "'", null, null, null, null);
        et_search.setText("");
        if(cursorSearchWord != null){
            cursorSearchWord.moveToFirst();
            currentWordId = cursorSearchWord.getInt(0);
            et_word.setText(cursorSearchWord.getString(1));
            et_translation.setText(cursorSearchWord.getString(2));
            et_example.setText(cursorSearchWord.getString(3));
            et_note.setText(cursorSearchWord.getString(4));
            et_level.setText(cursorSearchWord.getString(5));
            et_synonyms.setText(cursorSearchWord.getString(6));
            et_antonyms.setText(cursorSearchWord.getString(7));
        }else {
            Toast.makeText(getApplicationContext(), "word not found", Toast.LENGTH_SHORT).show();
        }
    }

    private void popAWord(int level){
        if(testLevel != lastTestLevel || isFirstTimeTest){ // if testLevel has changed, do another query. otherwise just get a word from cache
            cursorCurrentWordPool = dbIELTS.query("main", projection, "Level >= " + level, null, null, null, null);
            lastTestLevel = level;
            if(cursorCurrentWordPool != null){
                candidateAmount = cursorCurrentWordPool.getCount();
                cursorCurrentWordPool.moveToPosition(new Random().nextInt(candidateAmount));
                currentWordId = cursorCurrentWordPool.getInt(0);
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
            currentWordId = cursorCurrentWordPool.getInt(0);
            et_word.setText(cursorCurrentWordPool.getString(1));
            et_translation.setText(cursorCurrentWordPool.getString(2));
            et_example.setText(cursorCurrentWordPool.getString(3));
            et_note.setText(cursorCurrentWordPool.getString(4));
            et_level.setText(cursorCurrentWordPool.getString(5));
            et_synonyms.setText(cursorCurrentWordPool.getString(6));
            et_antonyms.setText(cursorCurrentWordPool.getString(7));
        }
        Log.e("current world id", currentWordId + "");
    }
}
