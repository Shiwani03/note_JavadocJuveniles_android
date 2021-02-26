package com.javadocjuveniles;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.javadocjuveniles.Daos.NoteDao;
import com.javadocjuveniles.Daos.SubjectDao;
import com.javadocjuveniles.Models.Notes;
import com.javadocjuveniles.Models.Subjects;


@Database(entities = { Notes.class, Subjects.class }, version = 1)
public abstract class NotesDatabase extends RoomDatabase {
    public abstract NoteDao getNoteDao();
    public abstract SubjectDao getSubjectDao();
    private static NotesDatabase noteDB;
    public static NotesDatabase getInstance(Context context) {
        if (null == noteDB) {
            noteDB = buildDatabaseInstance(context);
        }
        return noteDB;
    }

    private static NotesDatabase buildDatabaseInstance(Context context) {
        return Room.databaseBuilder(context,
                NotesDatabase.class,
                "NotesDB")
                .allowMainThreadQueries().build();
    }

    public void cleanUp(){
        noteDB = null;
    }

}
