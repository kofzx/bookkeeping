package com.example.bookkeeping.activity;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.bookkeeping.adapter.ListAdapter;
import com.example.bookkeeping.entity.Record;
import com.example.bookkeeping.fragment.ListFragment;
import com.example.bookkeeping.R;
import com.example.bookkeeping.model.ListTable;
import com.example.bookkeeping.util.MyUtil;
import com.example.bookkeeping.widget.DatePickerDIY;

import org.litepal.LitePal;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class ListActivity extends AppCompatActivity implements DatePickerDIY.IOnDateSetListener, ListAdapter.IAdapterListener {
    private static final String TAG = "ListActivity";
    private ListFragment listFragment;
    private TextView tvYear;
    private TextView tvMonth;
    private ImageView addButton;
    private LinearLayout queryTime;

    private DatePickerDIY dpDIY;
    private SimpleDateFormat mFormatter = new SimpleDateFormat("yyyy-MM-dd");

    private int type;
    private String typeDesc;
    private int year;
    private int month;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);

        initTime();
        type = getIntent().getIntExtra("type", -1); // 接收intent数据
        typeDesc = getIntent().getStringExtra("typeDesc");
        dpDIY = new DatePickerDIY(ListActivity.this, ListActivity.this, true, true, false);
        // 获取句柄
        listFragment = (ListFragment) getSupportFragmentManager().findFragmentById(R.id.frag_list);
        tvYear = (TextView) findViewById(R.id.tv_year);
        tvMonth = (TextView) findViewById(R.id.tv_month);
        addButton = (ImageView) findViewById(R.id.add_button);
        queryTime = (LinearLayout) findViewById(R.id.tap_query_time);
        // 默认查询当前的年月清单
        tvYear.setText(String.valueOf(year));
        tvMonth.setText(String.valueOf(month));
        listFragment.refreshAdapterByDate(ListActivity.this,ListActivity.this,  type, year, month);

        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ListActivity.this, AddDialogActivity.class);
                intent.putExtra("type", type);
                intent.putExtra("typeDesc", typeDesc);
                startActivityForResult(intent, 1);
            }
        });
        queryTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dpDIY.toggleVisible();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        switch (resultCode) {
            case 1:
                // 来自添加弹窗
                listFragment.refreshAdapterByDate(ListActivity.this, ListActivity.this, type, year, month);
                break;
            case 2:
                // 来自修改弹窗
                listFragment.refreshAdapterByDate(ListActivity.this, ListActivity.this, type, year, month);
                break;
            default:
                break;
        }
    }

    @Override
    public void onDateSet(Date date, int dType) {
        String str = mFormatter.format(date);
        String[] s = str.split("-");
        if (dType == 2) {
            tvYear.setText(s[0]);
            tvMonth.setText(String.valueOf(Integer.parseInt(s[1])));
            listFragment.refreshAdapterByDate(ListActivity.this, ListActivity.this, type, Integer.parseInt(s[0]), Integer.parseInt(s[1]));
        }
    }

    public void initTime() {
        Calendar c = Calendar.getInstance();
        year = c.get(Calendar.YEAR);
        month = MyUtil.monthPlus(c.get(Calendar.MONTH));
    }
    @Override
    public void onSetBtnClick(View view, Record record) {
        // 当前点击item的时间参数
        Date date = MyUtil.convertToDate(record.getTime());
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        int year = c.get(Calendar.YEAR);
        int month = MyUtil.monthPlus(c.get(Calendar.MONTH));
        int day = c.get(Calendar.DATE);
        int hour = c.get(Calendar.HOUR_OF_DAY);
        int minute = c.get(Calendar.MINUTE);

        Intent intent = new Intent(ListActivity.this, SetDialogActivity.class);
        intent.putExtra("recordId", record.getId());
        intent.putExtra("year", year);
        intent.putExtra("month", month);
        intent.putExtra("day", day);
        intent.putExtra("hour", hour);
        intent.putExtra("minute", minute);
        startActivityForResult(intent, 1);
    }

    @Override
    public void onDelBtnClick(View view, int id) {
        LitePal.deleteAll(ListTable.class, "id = ?", String.valueOf(id));
        listFragment.refreshAdapterByDate(ListActivity.this, ListActivity.this, type, year, month);
    }
}