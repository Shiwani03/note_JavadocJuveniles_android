package com.lambton.note_javadocjuveniles_android;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.lambton.note_javadocjuveniles_android.Daos.NoteDao;
import com.lambton.note_javadocjuveniles_android.Daos.SubjectDao;
import com.lambton.note_javadocjuveniles_android.Models.Notes;
import com.lambton.note_javadocjuveniles_android.Models.Subjects;


@Database(entities = { Notes.class, Subjects.class }, version = 1)
public abstract class NotesDatabase extends RoomDatabase {
    public abstract NoteDao getNoteDao();
    public abstract SubjectDao getSubjectDao();
    private static com.lambton.note_javadocjuveniles_android.NotesDatabase noteDB;
    public static com.lambton.note_javadocjuveniles_android.NotesDatabase getInstance(Context context) {
        if (null == noteDB) {
            noteDB = buildDatabaseInstance(context);
        }
        return noteDB;
    }

    private static com.lambton.note_javadocjuveniles_android.NotesDatabase buildDatabaseInstance(Context context) {
        return Room.databaseBuilder(context,
                com.lambton.note_javadocjuveniles_android.NotesDatabase.class,
                "NotesDB")
                .allowMainThreadQueries().build();
    }

    public void cleanUp(){
        noteDB = null;
    }

}
