package com.akash.mymoney.Transaction;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.PopupMenu;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.akash.mymoney.DBSQL.DBHandler;
import com.akash.mymoney.DBSQL.Tran_Model;
import com.akash.mymoney.R;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.nio.channels.FileChannel;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class Transaction extends AppCompatActivity implements PopupMenu.OnMenuItemClickListener {

    DBHandler dbHandler;
    Button save;
    AlertDialog alertDialog;

    private ArrayList<Tran_Model> TranArrayList;
    private TranAdapter tranAdapter;
    private RecyclerView Trans;

    EditText Get_name,Get_rate,Get_amount,Get_date;
    public static final int REQUEST_ID_MULTIPLE_PERMISSIONS = 1;

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transaction);

        checkAndRequestPermissions();

        dbHandler = new DBHandler(Transaction.this);

        findViewById(R.id.add).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                insertData();
            }
        });

        findViewById(R.id.ic_menu).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showPopup(v);
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
            @RequiresApi(api = Build.VERSION_CODES.O)
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

    @RequiresApi(api = Build.VERSION_CODES.O)
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

    @Override
    public void onBackPressed() {
        new AlertDialog.Builder(this)
                .setTitle("Really Exit?")
                .setMessage("Are you sure you want to exit?")
                .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface arg0, int arg1) {
                        finish();
                    }
                }).create().show();
    }

    public void showPopup(View v) {
        PopupMenu popup = new PopupMenu(this, v);
        MenuInflater inflater = popup.getMenuInflater();
        inflater.inflate(R.menu.t_menu, popup.getMenu());
        popup.setOnMenuItemClickListener(this);
        popup.show();
    }

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.backup:
                exportDB(Transaction.this);
                break;
            case R.id.restore:
                Toast.makeText(Transaction.this, "In Pro Available", Toast.LENGTH_SHORT).show();
                break;
            default:
                return false;
        }
        return false;
    }


    public void exportDB(Context context) {
        try {
            String getpath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).toString();
            //Log.d("Db Path", "exportDB: *********"+getpath);

            String backupDBPath = String.format("%s.bak", dbHandler.getDbName());
            File currentDB = context.getDatabasePath(dbHandler.getDbName());
            File backupDB = new File(getpath, backupDBPath);

            FileChannel src = new FileInputStream(currentDB).getChannel();
            FileChannel dst = new FileOutputStream(backupDB).getChannel();
            dst.transferFrom(src, 0, src.size());
            src.close();
            dst.close();
            Toast.makeText(Transaction.this, "Backup Successful!", Toast.LENGTH_SHORT).show();

        } catch (Exception e) {
            Log.d("Db error", "exportDB: *********"+e);
        }
    }

    private boolean checkAndRequestPermissions() {
        int storage = ContextCompat.checkSelfPermission(this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE);
        int storage2 = ContextCompat.checkSelfPermission(this, android.Manifest.permission.READ_EXTERNAL_STORAGE);
        List<String> listPermissionsNeeded = new ArrayList<>();
        if (storage != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(android.Manifest.permission.WRITE_EXTERNAL_STORAGE);
        }
        if (storage2 != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(android.Manifest.permission.READ_EXTERNAL_STORAGE);
        }
        if (!listPermissionsNeeded.isEmpty()) {
            ActivityCompat.requestPermissions(this, listPermissionsNeeded.toArray
                    (new String[listPermissionsNeeded.size()]), REQUEST_ID_MULTIPLE_PERMISSIONS);
            return false;
        }
        return true;
    }


}