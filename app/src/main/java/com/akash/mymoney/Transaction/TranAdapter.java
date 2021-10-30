package com.akash.mymoney.Transaction;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.akash.mymoney.DBSQL.DBHandler;
import com.akash.mymoney.DBSQL.Tran_Model;
import com.akash.mymoney.R;

import java.text.Format;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Period;
import java.time.YearMonth;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

@RequiresApi(api = Build.VERSION_CODES.O)
public class TranAdapter extends RecyclerView.Adapter<TranAdapter.ViewHolder> {

    // variable for our array list and context
    private ArrayList<Tran_Model> TranArrayList;
    private Context context;
    String TodayDate;
    DBHandler dbHandler;
    AlertDialog alertDialog;

    EditText Get_name,Get_rate,Get_amount,Get_date;
    Button save;

    Calendar mycal= Calendar.getInstance();
    SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy");
    ZoneId defaultZoneId = ZoneId.systemDefault();

    // constructor
    public TranAdapter(ArrayList<Tran_Model> TranArrayList, Context context) {
        this.TranArrayList = TranArrayList;
        this.context = context;
        TodayDate = formatter.format(mycal.getTime());
        dbHandler = new DBHandler(context);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.tran_single, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Tran_Model modal = TranArrayList.get(position);

        holder.Name.setText(modal.getName());
        holder.Rate.setText(modal.getRate());
        holder.Amount.setText(modal.getAmount());
        holder.GDate.setText(modal.getGDate());

        try {
            // calculate TDays
            Date date1 = formatter.parse(modal.getGDate());
            Date date2 = formatter.parse(TodayDate);
            long diff = date2.getTime() - date1.getTime();
            holder.TDays.setText(String.valueOf(TimeUnit.DAYS.convert(diff, TimeUnit.MILLISECONDS)));

            // Calculate Month/year/days
            Instant instant = date1.toInstant();
            LocalDate localDatestart = instant.atZone(defaultZoneId).toLocalDate();
            Instant instant1 = date2.toInstant();
            LocalDate localDateend = instant1.atZone(defaultZoneId).toLocalDate().plusDays(1);
            Period pd = Period.between(localDatestart, localDateend);

            holder.Days.setText(String.valueOf(pd.getDays()));
            holder.Month.setText(String.valueOf(pd.getMonths()));
            holder.Year.setText(String.valueOf(pd.getYears()));

        } catch (Exception e) {
            Log.d("exception", "onBindViewHolder: ***************"+e);
        }

        int p = Integer.parseInt(modal.getAmount());
        Float r = Float.parseFloat(modal.getRate());
        int t=1;

        Float odi = (p*r*t)/100;

        int perday = Integer.parseInt(String.valueOf(Math.round(odi/30)));

        int tday = Integer.parseInt(holder.TDays.getText().toString());
        int TAMT = perday*tday;

        holder.TAmount.setText(String.valueOf(TAMT));

        holder.Delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AlertDialog.Builder(context)
                        .setTitle("Delete entry")
                        .setMessage("Are you sure you want to delete this entry?")
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                dbHandler.delete_User(String.valueOf(modal.getId()));
                                ((Activity)context).finish();
                                context.startActivity(new Intent(context,Transaction.class));
                                ((Activity)context).overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                            }
                        })
                        .setNegativeButton("No", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        })
                        .show();
            }
        });

        holder.update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                new AlertDialog.Builder(context)
                        .setTitle("Edit entry")
                        .setMessage("Are you sure you want to edit this entry?")
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {

                                final LayoutInflater li = LayoutInflater.from(context);
                                final View promptsView = li.inflate(R.layout.save_tran, null);

                                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
                                alertDialogBuilder.setView(promptsView);
                                alertDialogBuilder.setCancelable(true);

                                Get_name = promptsView.findViewById(R.id.ed_name);
                                Get_rate = promptsView.findViewById(R.id.ed_rate);
                                Get_amount = promptsView.findViewById(R.id.ed_amount);
                                Get_date = promptsView.findViewById(R.id.ed_date);
                                save=promptsView.findViewById(R.id.save);

                                Get_name.setText(modal.getName());
                                Get_rate.setText(modal.getRate());
                                Get_amount.setText(modal.getAmount());
                                Get_date.setText(modal.getGDate());

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
                                        new DatePickerDialog(context, date,
                                                myCalendar.get(Calendar.YEAR),
                                                myCalendar.get(Calendar.MONTH),
                                                myCalendar.get(Calendar.DAY_OF_MONTH)).show();
                                    }
                                });

                                save.setOnClickListener(new View.OnClickListener() {
                                    @RequiresApi(api = Build.VERSION_CODES.O)
                                    @Override
                                    public void onClick(View v) {

                                        String id = String.valueOf(modal.getId());

                                        String Name = Get_name.getText().toString();
                                        String Rate = Get_rate.getText().toString();
                                        String Amount = Get_amount.getText().toString();
                                        String GDate = Get_date.getText().toString();

                                        if (Name.isEmpty() && Rate.isEmpty() && Amount.isEmpty() && GDate.isEmpty()) {
                                            Toast.makeText(context, "Please enter all the data..", Toast.LENGTH_SHORT).show();
                                            return;
                                        }  else {
                                            dbHandler.Update_User(id,Name,Rate, Amount, GDate);
                                            Toast.makeText(context, "User Update Success.", Toast.LENGTH_SHORT).show();
                                            alertDialog.dismiss();
                                            ((Activity)context).finish();
                                            context.startActivity(new Intent(context,Transaction.class));
                                            ((Activity)context).overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);

                                        }
                                    }
                                });

                                alertDialog = alertDialogBuilder.create();
                                alertDialog.show();

                            }
                        })
                        .setNegativeButton("No", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        })
                        .show();
            }
        });

    }

    @Override
    public int getItemCount() {
        // returning the size of our array list
        return TranArrayList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        // creating variables for our text views.
        private TextView Name, Rate, Amount, GDate, Days, Month, Year, TAmount, TDays;
        ImageView update, Delete;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            Name = itemView.findViewById(R.id.Name);
            Rate = itemView.findViewById(R.id.Rate);
            Amount = itemView.findViewById(R.id.Amount);
            GDate = itemView.findViewById(R.id.GDate);
            TDays =itemView.findViewById(R.id.TDay);
            Days=itemView.findViewById(R.id.Day);
            Month =itemView.findViewById(R.id.Month);
            Year=itemView.findViewById(R.id.Year);
            TAmount=itemView.findViewById(R.id.tamt);

            update = itemView.findViewById(R.id.update);
            Delete = itemView.findViewById(R.id.delete);

        }
    }
}

