package com.moshiur.firestore;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.SetOptions;

import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "FireStore";
    private static final String name_key = "Name";
    private static final String roll_key = "Roll";
    private static final String cgpa_key = "Cgpa";
    // Access a Cloud Firestore instance from your Activity
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    private EditText name, roll, cgpa;
    private TextView text;
    private Button button;
    private DocumentReference studentRef = db.collection("Students").document("student");
    //private DocumentReference studentRef = db.document("Students/student");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        name = findViewById(R.id.name);
        roll = findViewById(R.id.roll);
        cgpa = findViewById(R.id.cgpa);
        text = findViewById(R.id.textview);

        button = findViewById(R.id.button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(MainActivity.this, "sending", Toast.LENGTH_SHORT).show();
                addData();
            }
        });

    }

    private void addData() {
        String Name = name.getText().toString();
        String Roll = roll.getText().toString();
        String Cgpa = cgpa.getText().toString();

        /*
        Map<String, Object> student = new HashMap<>();
        student.put(name_key, Name);
        student.put(roll_key, Roll);
        student.put(cgpa_key, Cgpa);
        */
        Student student = new Student(Name, Roll, Cgpa);
        studentRef.set(student)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(MainActivity.this, "added to database", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d(TAG, "onFailure: " + e);
                    }
                });

    }

    private void getData() {
        studentRef.get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        if (documentSnapshot.exists()) {
                            /*
                            String name = documentSnapshot.getString(name_key);
                            String roll = documentSnapshot.getString(roll_key);
                            String cgpa = documentSnapshot.getString(cgpa_key);
                            */
                            Student student = documentSnapshot.toObject(Student.class);

                            String name = student.getName();
                            String roll = student.getRoll();
                            String cgpa = student.getCgpa();

                            text.setText(name + "\n" + roll + "\n" + cgpa);
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d(TAG, "onFailure: " + e.toString());
                    }
                });
    }

    //update document
    private void updateData() {
        String new_cgpa = cgpa.getText().toString();
        Map<String, Object> student = new HashMap<>();
        student.put(cgpa_key, new_cgpa);

        studentRef.set(student, SetOptions.merge());
        //studentRef.update(cgpa_key, new_cgpa); //this only update if document is exists otherwise nothing happens
    }

    //delete document
    private void deleteCGPA() {
        Map<String, Object> student = new HashMap<>();
        student.put(cgpa_key, FieldValue.delete());
        studentRef.update(student);
        //shorter way just single line
        //studentRef.update(cgpa_key, FieldValue.delete());

    }

    //load realtime
    @Override
    protected void onStart() {
        super.onStart();
        studentRef.addSnapshotListener(this, new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                if (e != null) {
                    Toast.makeText(MainActivity.this, "Error on loading", Toast.LENGTH_SHORT).show();
                    return;
                }
                //no problem retrieve data
                studentRef.get()
                        .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                            @Override
                            public void onSuccess(DocumentSnapshot documentSnapshot) {
                                if (documentSnapshot.exists()) {
                                    /*
                                    String name = documentSnapshot.getString(name_key);
                                    String roll = documentSnapshot.getString(roll_key);
                                    String cgpa = documentSnapshot.getString(cgpa_key);
                                    */
                                    Student student = documentSnapshot.toObject(Student.class);

                                    String name = student.getName();
                                    String roll = student.getRoll();
                                    String cgpa = student.getCgpa();

                                    text.setText(name + "\n" + roll + "\n" + cgpa);
                                }
                            }
                        });
            }
        });
    }
}
