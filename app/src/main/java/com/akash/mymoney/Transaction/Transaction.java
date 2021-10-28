package com.akash.mymoney.Transaction;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Toast;

import com.akash.mymoney.DBSQL.DBHandler;
import com.akash.mymoney.DBSQL.Tran_Model;
import com.akash.mymoney.R;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

public class Transaction extends AppCompatActivity {

    DBHandler dbHandler;
    Button save;
    AlertDialog alertDialog;

    private ArrayList<Tran_Model> TranArrayList;
    private TranAdapter tranAdapter;
    private RecyclerView Trans;

    EditText Get_name,Get_rate,Get_amount,Get_date;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transaction);

        dbHandler = new DBHandler(Transaction.this);

        findViewById(R.id.add).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                insertData();
            }
        });

        readData();
    }

    public void insertData(){

        final LayoutInflater li = LayoutInflater.from(Transaction.this);
        final View promptsView = li.inflate(R.layout.save_tran, null);

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(Transaction.this);
        alertDialogBuilder.setView(promptsView);
        alertDialogBuilder.setCancelable(true);

        Get_name = promptsView.findViewById(R.id.ed_name);
        Get_rate = promptsView.findViewById(R.id.ed_rate);
        Get_amount = promptsView.findViewById(R.id.ed_amount);
        Get_date = promptsView.findViewById(R.id.ed_date);
        save=promptsView.findViewById(R.id.save);

        final Calendar myCalendar = Calendar.getInstance();
        final DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                // TODO Auto-generated method stub
                myCalendar.set(Calendar.YEAR, year);
                myCalendar.set(Calendar.MONTH, monthOfYear);
                myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);

                String myFormat = "dd-MM-yyyy";
                SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);
                Get_date.setText(sdf.format(myCalendar.getTime()));
            }
        };

        Get_date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new DatePickerDialog(Transaction.this, date,
                        myCalendar.get(Calendar.YEAR),
                        myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String Name = Get_name.getText().toString();
                String Rate = Get_rate.getText().toString();
                String Amount = Get_amount.getText().toString();
                String GDate = Get_date.getText().toString();

                if (Name.isEmpty() && Rate.isEmpty() && Amount.isEmpty() && GDate.isEmpty()) {
                    Toast.makeText(Transaction.this, "Please enter all the data..", Toast.LENGTH_SHORT).show();
                    return;
                }  else {
                    dbHandler.addNewUser(Name, Rate, Amount, GDate);
                    Toast.makeText(Transaction.this, "User Save Success.", Toast.LENGTH_SHORT).show();
                    alertDialog.dismiss();
                    readData();
                }
            }
        });

        alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }

    public void readData(){
        // initializing our all variables.
        TranArrayList = new ArrayList<>();
        TranArrayList = dbHandler.readCourses();

        // on below line passing our array lost to our adapter class.
        tranAdapter = new TranAdapter(TranArrayList, Transaction.this);
        Trans = findViewById(R.id.trans);

        // setting layout manager for our recycler view.
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(Transaction.this, RecyclerView.VERTICAL, false);
        Trans.setLayoutManager(linearLayoutManager);
        Trans.setAdapter(tranAdapter);
    }


}