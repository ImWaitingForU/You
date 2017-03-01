package com.chan.you;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.chan.you.utils.SpUtils;

import static com.chan.you.utils.SpUtils.getStringFromET;

public class SettingActivity extends AppCompatActivity {

    private SharedPreferences sp;
    private EditText etPhone;
    private EditText etQQ;
    private EditText etWeibo;
    private EditText etName;

    @Override
    protected void onCreate (Bundle savedInstanceState) {
        super.onCreate (savedInstanceState);
        setContentView (R.layout.activity_setting);

        sp = getSharedPreferences (SpUtils.SP_NAME, MODE_PRIVATE);
        setTitle ("修改他/她的:");

        etPhone = (EditText) findViewById (R.id.etPhone);
        etQQ = (EditText) findViewById (R.id.etQQ);
        etWeibo = (EditText) findViewById (R.id.etWeibo);
        etName = (EditText) findViewById (R.id.etName);

        etName.setText (SpUtils.getName (sp));
        etPhone.setText (SpUtils.getPhone (sp));
        etQQ.setText (SpUtils.getQQ (sp));
        etWeibo.setText (SpUtils.getWeibo (sp));

    }

    public void editFinish (View view) {
        SpUtils.putMsg (sp, getStringFromET (etName), getStringFromET (etPhone), getStringFromET (etQQ),
                        getStringFromET (etWeibo));
        Toast.makeText (this, "修改完成", Toast.LENGTH_SHORT).show ();
        finish ();
    }
}
