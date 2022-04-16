package com.example.androidtable;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridLayout;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        LinearLayout layout = findViewById(R.id.linearLayout);
        TextView result = new TextView(this);
        result.setPadding(35, 50, 5,10);
        result.setText("Здесь будет отображен результат");
        result.setTextSize(14);

        EditText editLength = findViewById(R.id.ed_countOfDays);
        EditText editDay = findViewById(R.id.ed_dayOfWeek);
        EditText editSum = findViewById(R.id.ed_sum);

        Button createTableButton = findViewById(R.id.b_createTable);
        View.OnClickListener onClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!(editLength.getText().toString().equals(""))&&
                        !(editDay.getText().toString().equals(""))&&
                        !(editSum.getText().toString().equals(""))) {
                    editLength.setEnabled(false);
                    editLength.setCursorVisible(false);
                    editLength.setKeyListener(null);
                    editDay.setEnabled(false);
                    editDay.setCursorVisible(false);
                    editDay.setKeyListener(null);
                    editSum.setEnabled(false);
                    editSum.setCursorVisible(false);
                    editSum.setKeyListener(null);
                    createTableButton.setEnabled(false);

                    String countOfDaysStr = editLength.getText().toString();
                    String dayOfWeekStr = editDay.getText().toString();
                    String sumStr = editSum.getText().toString();
                    int countOfDays = Integer.parseInt(countOfDaysStr);
                    int dayOfWeek = Integer.parseInt(dayOfWeekStr);
                    int sum = Integer.parseInt(sumStr);
                    createTable(countOfDays, dayOfWeek, sum, result, layout);
                }
            }
        };

        createTableButton.setOnClickListener(onClickListener);
        setContentView(layout);
    }

    void createTable(int countOfDays, int dayOfWeek, int sum, TextView result,LinearLayout layout){
        String[] week = {"Monday", "Tuesday", "Wednesday",
                "Thursday", "Friday", "Saturday", "Sunday"};

        HorizontalScrollView horizontalScrollView = new HorizontalScrollView(this);
        GridLayout gridLayout = new GridLayout(this);

        gridLayout.setPadding(20, 0, 0, 0);
        int nRow = 1 + (countOfDays+dayOfWeek-1)/7 + 1;
        //result.setText(dayOfWeek + " " + sum);
        gridLayout.setRowCount(nRow);
        int nCol = 7;
        gridLayout.setColumnCount(nCol);
        for (int i = 0; i <= nRow * nCol - 1; i++) {
            if (i < nCol) {
                TextView legend = new TextView(this);
                legend.setText(week[i]);
                gridLayout.addView(legend);
                legend.setPadding(5, 15, 5, 10);
            } else if ((i<countOfDays+dayOfWeek+7)&&(i>nCol+dayOfWeek-1)){
                String buttonText = "Кнопка " + (i - nCol - dayOfWeek+1) + " была нажата";
                Button btn = new Button(this);

                View.OnClickListener onClickListener = new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        result.setText(buttonText);
                    }
                };

                btn.setOnClickListener(onClickListener);
                btn.setText(String.valueOf(i - nCol - dayOfWeek+1));
                gridLayout.addView(btn);
            }else {
                Button btn = new Button(this);
                gridLayout.addView(btn);
            }
        }
        horizontalScrollView.addView(gridLayout);
        layout.addView(horizontalScrollView);
        layout.addView(result);
    }
}