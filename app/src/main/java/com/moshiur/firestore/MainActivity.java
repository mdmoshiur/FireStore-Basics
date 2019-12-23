package com.moshiur.firestore;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "FireStore";
    private static final String name_key = "name";
    private static final String roll_key = "roll";
    private static final String cgpa_key = "cgpa";
    // Access a Cloud Firestore instance from your Activity
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    private EditText name, roll, cgpa;
    private TextView text;
    private Button button;

    private CollectionReference StudentRef = db.collection("Students");
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
                //addData();
                getData();
            }
        });

    }

    private void addData() {
        String Name = name.getText().toString();
        String Roll = roll.getText().toString();
        String Cgpa = cgpa.getText().toString();

        if (Roll == null)
            Roll = "0";
        if (Cgpa == null)
            Cgpa = "0.00";

        int ROLL = Integer.parseInt(Roll);
        double CGPA = Double.parseDouble(Cgpa);
        /*
        Map<String, Object> student = new HashMap<>();
        student.put(name_key, Name);
        student.put(roll_key, Roll);
        student.put(cgpa_key, Cgpa);
        */
        Student student = new Student(Name, ROLL, CGPA);
        StudentRef.add(student);
    }

    private void getData() {
        //for multiple query
        Task task1 = StudentRef.whereGreaterThanOrEqualTo(cgpa_key, 3.0)
                .get();

        Task task2 = StudentRef.whereLessThanOrEqualTo(cgpa_key, 3.50)
                .orderBy(cgpa_key)
                .get();


        Task<List<QuerySnapshot>> allTasks = Tasks.whenAllSuccess(task1, task2);
        allTasks.addOnSuccessListener(new OnSuccessListener<List<QuerySnapshot>>() {
            @Override
            public void onSuccess(List<QuerySnapshot> querySnapshots) {
                String data = "";
                for (QuerySnapshot queryDocumentSnapshots : querySnapshots) {
                    for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                        Student student = documentSnapshot.toObject(Student.class);
                        student.setDocumentId(documentSnapshot.getId());

                        String documentId = student.getDocumentId();
                        String name = student.getName();
                        int roll = student.getRoll();
                        double cgpa = student.getCgpa();

                        data += "ID: " + documentId + "\nName: " + name + "\nRoll: " + roll + "\nCGPA: " + cgpa + "\n\n";

                    }
                }
                text.setText(data);
                //Log.d(TAG, "onSuccess: data:"+data);
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
        StudentRef.orderBy(cgpa_key, Query.Direction.DESCENDING)
                .orderBy(roll_key)
                .orderBy(name_key)
                .addSnapshotListener(this, new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                if (e != null) {
                    return;
                }
                String data = "";

                for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                    Student student = documentSnapshot.toObject(Student.class);
                    student.setDocumentId(documentSnapshot.getId());

                    String documentId = student.getDocumentId();
                    String name = student.getName();
                    int roll = student.getRoll();
                    double cgpa = student.getCgpa();

                    data += "ID: " + documentId + "\nName: " + name + "\nRoll: " + roll + "\nCGPA: " + cgpa + "\n\n";

                }
                text.setText(data);
            }
        });

    }

}
