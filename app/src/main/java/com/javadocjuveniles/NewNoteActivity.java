package com.javadocjuveniles;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.javadocjuveniles.Models.Notes;
import com.javadocjuveniles.Models.Subjects;
import com.javadocjuveniles.utils.DataConverter;

import java.io.IOException;
import java.util.Date;
import java.util.List;

public class NewNoteActivity extends AppCompatActivity {
    TextView txt_title,footer;
    ImageView drawer_txt,new_note;
    RelativeLayout save;
    String from="";
    private static final int CAMERA_REQUEST = 102;
    private static final int GALLERY_REQUEST = 101;
    EditText title,description;
    Button record,subject;
    RelativeLayout share_layout;
    ImageView share_pic,deal_icon;
    FrameLayout share_frame;
    String path;
    NotesDatabase notesDatabase;
    Subjects selectedSubject;
    Location userlocation;
    LocationManager locationManager;
    LocationListener locationListener;
    String pathAudio;
    Bitmap image;
    private static final int REQUEST_CODE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_note);
        drawer_txt=(ImageView) findViewById(R.id.drawer_icon);
        new_note=(ImageView) findViewById(R.id.new_note);
        txt_title=(TextView)findViewById(R.id.txt_title);
        footer=(TextView)findViewById(R.id.footer_image);
        title=(EditText) findViewById(R.id.new_title);
        description=(EditText) findViewById(R.id.description);
        subject=(Button) findViewById(R.id.select_subject);
        share_pic=(ImageView) findViewById(R.id.share_pic);
        notesDatabase = NotesDatabase.getInstance(NewNoteActivity.this);

        save=(RelativeLayout) findViewById(R.id.save);

        drawer_txt.setVisibility(View.VISIBLE);
        new_note.setVisibility(View.VISIBLE);
        Intent i=getIntent();
        from=i.getStringExtra("from");
        new_note.setImageDrawable(getResources().getDrawable(R.drawable.camera_icon));

        if(from.equalsIgnoreCase("new")){
            txt_title.setText("New Note");
            footer.setText("Save Note");
            locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
            locationListener = new LocationListener() {
                @Override
                public void onLocationChanged(Location location) {
                    userlocation = location;
                }

                @Override
                public void onStatusChanged(String provider, int status, Bundle extras) {

                }

                @Override
                public void onProviderEnabled(String provider) {

                }

                @Override
                public void onProviderDisabled(String provider) {

                }
            };

            if (!hasLocationPermission())
                requestLocationPermission();
            else
                startUpdateLocation();

        }else {
            txt_title.setText("Update Note");
            footer.setText("Update Note");
            getAndSetNotes();


        }

        drawer_txt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i=new Intent(getApplicationContext(),MainActivity.class);
                startActivity(i);
            }
        });
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(from.equalsIgnoreCase("new")) {
                    if (CheckValidation()) {
                        Notes note;
                        if (image != null) {

                            note = new Notes(description.getText().toString(), title.getText().toString(), userlocation.getLatitude(), userlocation.getLongitude(), new Date().getTime(), selectedSubject.getSubject_id(), DataConverter.convertImage2ByteArray(image), pathAudio);
                        } else {
                            note = new Notes(description.getText().toString(), title.getText().toString(), userlocation.getLatitude(), userlocation.getLongitude(), new Date().getTime(), selectedSubject.getSubject_id(), null, pathAudio);
                        }
                        notesDatabase.getNoteDao().insert(note);
                        drawer_txt.performClick();
                    }
                }else {
                    if(CheckValidation()) {
                        List<Notes> notes = notesDatabase.getNoteDao().getAll();
                        int index = getIntent().getIntExtra("selectedIndex",-1);
                        if (index != -1){
                            Notes note = notes.get(index);
                            note.setTitle(title.getText().toString());
                            note.setDescription(description.getText().toString());
                            note.setNote_audio(pathAudio);
                            note.setSubject_id_fk(selectedSubject.getSubject_id());
                            if(image != null){
                                note.setNote_image(DataConverter.convertImage2ByteArray(image));
                            }
                            notesDatabase.getNoteDao().update(note);
                        }

                        drawer_txt.performClick();
                    }
                }
            }
        });

        subject.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i=new Intent(getApplicationContext(),SubjectActivity.class);
                startActivityForResult(i, 11);
            }
        });

        new_note.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectImage();
            }
        });



    }

    private void getAndSetNotes() {
        List<Notes> notes = notesDatabase.getNoteDao().getAll();
        int index = getIntent().getIntExtra("selectedIndex",-1);
        if (index != -1){
            Notes note = notes.get(index);
            title.setText(note.getTitle());
            description.setText(note.getDescription());
            byte[] data = note.getNote_image();
            if(data != null){
                image = DataConverter.convertByteArray2Bitmap(data);
                share_pic.setImageBitmap(image);
                share_pic.setVisibility(View.VISIBLE);
            }
            Subjects sub = notesDatabase.getSubjectDao().getSubject(note.getSubject_id_fk()).get(0);
            subject.setText(sub.getSubject_name());
            selectedSubject = sub;

        }
    }
    private void startUpdateLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5000, 0, locationListener);
    }

    private void requestLocationPermission() {
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_CODE);
    }

    private boolean hasLocationPermission() {
        return ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED;
    }
    public boolean CheckValidation(){

        if (title.getText().toString().trim().length() == 0) {
            title.setError("Please enter title");
            title.requestFocus();
            return false;

        }else if(description.getText().toString().trim().length() == 0) {

            description.setError("Please enter description");
            description.requestFocus();
            return false;
        }
        else if(selectedSubject == null){
            Toast.makeText(getApplicationContext(),"Please Select Subject",Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;


    }

    private void selectImage() {
        gallery();

    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        if (REQUEST_CODE == requestCode) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5000, 0, locationListener);
            }
        }
    }
    @Override
    protected void onActivityResult(int reqCode, int resultCode, Intent data) {
        super.onActivityResult(reqCode, resultCode, data);
        if (reqCode == CAMERA_REQUEST && resultCode == RESULT_OK)
        {
            image = (Bitmap) data.getExtras().get("data");
            share_pic.setImageBitmap(image);
            share_pic.setVisibility(View.VISIBLE);

        }
        else if(reqCode == 112 && resultCode == RESULT_OK){
            pathAudio = data.getStringExtra("audio");
        }
        else if(reqCode == GALLERY_REQUEST && resultCode == RESULT_OK){

            Uri uri = data.getData();
            try {
                image = MediaStore.Images.Media.getBitmap(this.getContentResolver(), uri);
                share_pic.setImageBitmap(image);
                share_pic.setVisibility(View.VISIBLE);
            } catch (IOException e) {
                e.printStackTrace();
                Toast.makeText(getApplicationContext(),e.getLocalizedMessage(),Toast.LENGTH_SHORT).show();
            }

        }
        else if (reqCode == 11){
            if(resultCode == RESULT_OK) {
                int subjectID = data.getIntExtra("data",-1);
                if(subjectID != -1){
                    for (Subjects sub: notesDatabase.getSubjectDao().getAll()) {
                        if(sub.getSubject_id() == subjectID){
                            selectedSubject = sub;
                            subject.setText(sub.getSubject_name());
                        }
                    }
                    //  selectedSubject =
                }
            }
        }
        else{

        }
    }

    public void gallery() {

        final CharSequence[] items = { "Take Photo", "Choose from Library","Record Audio","Cancel" };
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Choose Your Option");
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
                //  boolean result= Utility.checkPermission(ShareDeal.this);

                if (items[item].equals("Take Photo")) {

                    CaptureImage();
                } else if (items[item].equals("Choose from Library")) {
                    OpenGallery();
                } else if(items[item].equals("Record Audio")){
                    Intent i=new Intent(getApplicationContext(),AudioRecordActivity.class);
                    startActivityForResult(i,112);
                }
                else if(items[item].equals("Cancel")){
                    dialog.dismiss();
                }
            }
        });
        builder.show();

    }

    public void CaptureImage() {

        Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(cameraIntent, CAMERA_REQUEST);
    }

    public void OpenGallery() {
        Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
        photoPickerIntent.setType("image/*");
        startActivityForResult(photoPickerIntent, GALLERY_REQUEST);
    }
}
