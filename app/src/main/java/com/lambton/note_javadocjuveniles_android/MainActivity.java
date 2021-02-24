package com.lambton.note_javadocjuveniles_android;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.javadocjuveniles.Adapter.notesAdapter;
import com.javadocjuveniles.Models.Notes;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    TextView txt_title;
    ImageView search_icon,drawer_txt,new_note;
    EditText search;
    RelativeLayout createnote,no_note;
    RecyclerView recyclerView;
    notesAdapter madapter;
    NotesDatabase notesDatabase;
    List<Notes> listNotes;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        drawer_txt=(ImageView) findViewById(R.id.drawer_icon);
        txt_title=(TextView)findViewById(R.id.txt_title);
        new_note=(ImageView) findViewById(R.id.new_note);
        search=(EditText) findViewById(R.id.search_txt);
        createnote=(RelativeLayout) findViewById(R.id.createnote);
        no_note=(RelativeLayout) findViewById(R.id.no_notes);

        search_icon=(ImageView) findViewById(R.id.search_icon);
        recyclerView=(RecyclerView) findViewById(R.id.note_recycler);
        drawer_txt.setVisibility(View.GONE);
        new_note.setVisibility(View.GONE);
        txt_title.setText("Notes");
        notesDatabase = NotesDatabase.getInstance(MainActivity.this);
        listNotes =  NotesDatabase.getInstance(MainActivity.this).getNoteDao().getAll();
        if(listNotes.size()==0){
            no_note.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
        }else {
            no_note.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);
        }
        createnote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i=new Intent(getApplicationContext(),NewNoteActivity.class);
                i.putExtra("from","new");
                startActivity(i);
            }
        });
        search.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                filter(s.toString());
            }
        });

        madapter = new notesAdapter(this,listNotes,NotesDatabase.getInstance(MainActivity.this).getSubjectDao().getAll()) {
            @Override
            public void deleteAddress(final int pos) {

                final AlertDialog.Builder mainDialog = new AlertDialog.Builder(MainActivity.this);
                LayoutInflater inflater = (LayoutInflater)getApplicationContext() .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                View dialogView = inflater.inflate(R.layout.alert_dialog, null);
                mainDialog.setView(dialogView);

                final Button cancel = (Button) dialogView.findViewById(R.id.cancel);
                final Button save = (Button) dialogView.findViewById(R.id.save);
                final  ImageView cross=(ImageView) dialogView.findViewById(R.id.cross);
                final AlertDialog alertDialog = mainDialog.create();
                alertDialog.show();

                cancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        alertDialog.dismiss();

                    }
                });

                save.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        notesDatabase.getNoteDao().delete(listNotes.get(pos));
                        listNotes.remove(pos);
                        recyclerView.getAdapter().notifyDataSetChanged();
                        alertDialog.dismiss();
                    }
                });
                cross.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        alertDialog.dismiss();
                    }
                });

            }
        };
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setAdapter(madapter);
    }

    private void filter(String text) {
        listNotes =  NotesDatabase.getInstance(MainActivity.this).getNoteDao().getAll();
        List<Notes> temp = new ArrayList();
        for (Notes n :listNotes) {
            if(n.getTitle().toLowerCase().contains(text.toLowerCase()) || n.getDescription().toLowerCase().contains(text.toLowerCase())){
                temp.add(n);
            }
        }
        madapter.notes = temp;
        recyclerView.getAdapter().notifyDataSetChanged();
    }
}
