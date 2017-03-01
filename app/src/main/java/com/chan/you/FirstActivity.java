package com.chan.you;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.chan.you.utils.SpUtils;

import static com.chan.you.utils.SpUtils.getStringFromET;

public class FirstActivity extends AppCompatActivity {

    private SharedPreferences sp;
    private EditText etPhone;
    private EditText etQQ;
    private EditText etWeibo;
    private EditText etName;

    @Override
    protected void onCreate (Bundle savedInstanceState) {
        super.onCreate (savedInstanceState);
        setContentView (R.layout.activity_first);
        sp = getSharedPreferences (SpUtils.SP_NAME, MODE_PRIVATE);

        if (!SpUtils.checkIsFirstUse (sp)) {
            goMain ();
        }


        setTitle ("填写他/她的:");

        etPhone = (EditText) findViewById (R.id.etPhone);
        etQQ = (EditText) findViewById (R.id.etQQ);
        etWeibo = (EditText) findViewById (R.id.etWeibo);
        etName = (EditText) findViewById (R.id.etName);

    }


    private void checkNull () {
        if (getStringFromET (etName).equals ("") || getStringFromET (etPhone).equals ("") ||
                getStringFromET (etQQ).equals ("") ||
                getStringFromET (etWeibo).equals ("")) {
            Toast.makeText (this, "可在设置中完善资料", Toast.LENGTH_SHORT).show ();
        } else {
            Toast.makeText (this, "资料保存完成", Toast.LENGTH_SHORT).show ();
        }
    }



    private void goMain () {
        startActivity (new Intent (this, MainActivity.class));
        finish ();
    }

    public void go (View view) {
        checkNull ();
        SpUtils.putMsg (sp, getStringFromET (etName), getStringFromET (etPhone), getStringFromET (etQQ),
                        getStringFromET (etWeibo));
        SpUtils.putBoolean (sp, SpUtils.KEY_ISFIRST_USE, false);
        goMain ();
    }


}
