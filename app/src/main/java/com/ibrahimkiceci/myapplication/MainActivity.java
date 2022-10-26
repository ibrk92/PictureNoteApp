package com.ibrahimkiceci.myapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import com.ibrahimkiceci.myapplication.databinding.ActivityMainBinding;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;
    ArrayList<Note>noteArrayList;
    NoteAdapter noteAdapter;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);
        noteArrayList = new ArrayList<>();
        binding.recyclerView.setLayoutManager(new LinearLayoutManager(this));
        noteAdapter =  new NoteAdapter(noteArrayList);
        binding.recyclerView.setAdapter(noteAdapter);

        getData();
    }

    // sqlite verilerini alalim;

    private void getData(){

        try {

            SQLiteDatabase sqLiteDatabase = this.openOrCreateDatabase("Notes", MODE_PRIVATE, null);
            Cursor cursor = sqLiteDatabase.rawQuery("SELECT * FROM notes", null);
            int nameIx = cursor.getColumnIndex("notename");
            int idIx = cursor.getColumnIndex("id");
            while(cursor.moveToNext()){

                String name = cursor.getString(nameIx);
                int id = cursor.getInt(idIx);
                Note note = new Note(name, id);
                noteArrayList.add(note);


            }

            noteAdapter.notifyDataSetChanged();

            cursor.close();

        }catch (Exception e){
            e.printStackTrace();
        }



    }





    // menuyu buraya baglamamiz gerek. Burada iki tane methodu override etmek gerekir.


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // menuyu baglama islemini burada yapacagiz

        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.app_menu,menu);

        return super.onCreateOptionsMenu(menu);
    }

    // menuye tiklaninca ne olacagini burada yaziyoruz;


    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId() == R.id.add_note){

            Intent intent  = new Intent(this, MainActivity2.class); // Intent kullanarak diger aktiviteye gecelim
            intent.putExtra("info", "new");
            startActivity(intent);


        }

        return super.onOptionsItemSelected(item);
    }
}