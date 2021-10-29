package com.akash.mymoney.Transaction;

import android.content.Context;
import android.os.Build;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;

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
import java.util.concurrent.TimeUnit;

@RequiresApi(api = Build.VERSION_CODES.O)
public class TranAdapter extends RecyclerView.Adapter<TranAdapter.ViewHolder> {

    // variable for our array list and context
    private ArrayList<Tran_Model> TranArrayList;
    private Context context;
    String TodayDate;

    Calendar mycal= Calendar.getInstance();
    SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy");
    ZoneId defaultZoneId = ZoneId.systemDefault();

    // constructor
    public TranAdapter(ArrayList<Tran_Model> TranArrayList, Context context) {
        this.TranArrayList = TranArrayList;
        this.context = context;
        TodayDate = formatter.format(mycal.getTime());
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
            // calculate Days
            Date date1 = formatter.parse(modal.getGDate());
            Date date2 = formatter.parse(TodayDate);
            long diff = date2.getTime() - date1.getTime();
            holder.Days.setText(String.valueOf(TimeUnit.DAYS.convert(diff, TimeUnit.MILLISECONDS)));

            // Calculate Month
            Instant instant = date1.toInstant();
            LocalDate localDatestart = instant.atZone(defaultZoneId).toLocalDate();
            Instant instant1 = date2.toInstant();
            LocalDate localDateend = instant1.atZone(defaultZoneId).toLocalDate().plusDays(1);
            Period pd = Period.between(localDatestart, localDateend);

           // holder.Days.setText(String.valueOf(pd.getDays()));
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

        int tday = Integer.parseInt(holder.Days.getText().toString());
        int TAMT = perday*tday;

        holder.TAmount.setText(String.valueOf(TAMT));

    }

    @Override
    public int getItemCount() {
        // returning the size of our array list
        return TranArrayList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        // creating variables for our text views.
        private TextView Name, Rate, Amount, GDate, Days, Month, Year, TAmount;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            Name = itemView.findViewById(R.id.Name);
            Rate = itemView.findViewById(R.id.Rate);
            Amount = itemView.findViewById(R.id.Amount);
            GDate = itemView.findViewById(R.id.GDate);
            Days=itemView.findViewById(R.id.Day);
            Month =itemView.findViewById(R.id.Month);
            Year=itemView.findViewById(R.id.Year);
            TAmount=itemView.findViewById(R.id.tamt);
        }
    }
}

