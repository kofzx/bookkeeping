package com.example.bookkeeping.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.bookkeeping.MainActivity;
import com.example.bookkeeping.baseActivity.customDialogActivity;
import com.example.bookkeeping.model.ListTable;
import com.example.bookkeeping.R;
import com.example.bookkeeping.util.ToastUtil;

import java.util.Date;

public class AddDialogActivity extends customDialogActivity {
    private static final String TAG = "AddDialogActivity";
    private int type;
    private String typeDesc;
    private LinearLayout includeAddDialog;
    private Button cancelButton;
    private Button confirmButton;
    private TextView moneyText;
    private TextView descText;
    private Long currentTimeStamp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_dialog);
        // 接收intent数据
        type = getIntent().getIntExtra(MainActivity.TYPE, -1);
        typeDesc = getIntent().getStringExtra(MainActivity.TYPE_DESC);

        initTime();
        moneyText = (TextView) findViewById(R.id.money_text);
        descText = (TextView) findViewById(R.id.desc_text);
        includeAddDialog = (LinearLayout) findViewById(R.id.include_add_dialog);
        cancelButton = (Button) includeAddDialog.findViewById(R.id.cancel);
        confirmButton = (Button) includeAddDialog.findViewById(R.id.confirm);

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                closeActivity();
            }
        });
        confirmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String money = moneyText.getText().toString();
                String desc = descText.getText().toString();
                float moneyReal = 0;
                if (!TextUtils.isEmpty(money)) {
                    moneyReal = Float.parseFloat(money);
                }
                if (moneyReal == 0) {
                    ToastUtil.makeText(AddDialogActivity.this, "请填写金额");
                    return;
                }
                if (TextUtils.isEmpty(desc)) {
                    ToastUtil.makeText(AddDialogActivity.this, "请填写备注");
                    return;
                }
                // 写入数据
                ListTable list = new ListTable();
                list.setType(type);
                list.setTypeDesc(typeDesc);
                list.setMoney(moneyReal);
                list.setDescription(desc);
                list.setTime(currentTimeStamp);
                list.save();

                ToastUtil.makeText(AddDialogActivity.this, "添加成功");
                Intent intent = new Intent();
                setResult(1, intent);
                closeActivity();
            }
        });
    }
    public void initTime() {
        currentTimeStamp = new Date().getTime();
    }
}
