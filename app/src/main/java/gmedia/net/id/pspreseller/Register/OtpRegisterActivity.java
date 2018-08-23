package gmedia.net.id.pspreseller.Register;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.maulana.custommodul.ApiVolley;
import com.maulana.custommodul.CustomView.DialogBox;
import com.maulana.custommodul.FormatItem;
import com.maulana.custommodul.ItemValidation;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

import gmedia.net.id.pspreseller.LoginScreen;
import gmedia.net.id.pspreseller.R;
import gmedia.net.id.pspreseller.Utils.ServerURL;

public class OtpRegisterActivity extends AppCompatActivity {

    private Context context;
    private ItemValidation iv = new ItemValidation();
    private TextView tvMinutes, tvSecond;
    private static EditText edt1, edt2, edt3, edt4;
    private Button btnVerifikasi, btnReVerifikasi;
    private String nomor = "", password = "";
    private ProgressBar pbLoading;
    private Timer timer;
    public static boolean isActive = false;
    private String nextTime = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_otp_register);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        //getSupportActionBar().setHomeAsUpIndicator(getResources().getDrawable(R.mipmap.ic_down));

        getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN
        );

        setTitle("Verifikasi");
        context = this;
        isActive = true;

        initUI();
    }

    private void initUI() {

        tvMinutes = (TextView) findViewById(R.id.tv_minutes);
        tvSecond = (TextView) findViewById(R.id.tv_second);
        edt1 = (EditText) findViewById(R.id.edt_1);
        edt2 = (EditText) findViewById(R.id.edt_2);
        edt3 = (EditText) findViewById(R.id.edt_3);
        edt4 = (EditText) findViewById(R.id.edt_4);
        btnVerifikasi = (Button) findViewById(R.id.btn_verifikasi);
        btnReVerifikasi = (Button) findViewById(R.id.btn_re_verifikasi);
        pbLoading = (ProgressBar) findViewById(R.id.pb_loading);

        Bundle bundle = getIntent().getExtras();

        if(bundle != null){

            nomor = bundle.getString("nomor","");
            password = bundle.getString("password","");

            getOTP();
        }
        initEvent();
    }

    private void initEvent() {

        edt1.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {

                if(!editable.toString().isEmpty()){

                    if(!edt2.getText().toString().isEmpty()){

                        edt2.requestFocus();
                        edt2.setSelection(0);
                    }else{
                        edt2.requestFocus();
                    }
                }
            }
        });

        edt2.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {

                if(!editable.toString().isEmpty()){

                    if(!edt3.getText().toString().isEmpty()){

                        edt3.requestFocus();
                        edt3.setSelection(1);
                    }else{
                        edt3.requestFocus();
                    }
                }else{

                    if(!edt1.getText().toString().isEmpty()){

                        edt1.requestFocus();
                        edt1.setSelection(1);
                    }else{
                        edt1.requestFocus();
                    }
                }
            }
        });

        edt3.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {

                if(!editable.toString().isEmpty()){

                    if(!edt4.getText().toString().isEmpty()){

                        edt4.requestFocus();
                        edt4.setSelection(1);
                    }else{
                        edt4.requestFocus();
                    }
                }else{

                    if(!edt2.getText().toString().isEmpty()){

                        edt2.requestFocus();
                        edt2.setSelection(1);
                    }else{
                        edt2.requestFocus();
                    }
                }
            }
        });

        edt4.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {

                if(!editable.toString().isEmpty()){

                }else{

                    if(!edt3.getText().toString().isEmpty()){

                        edt3.requestFocus();
                        edt3.setSelection(1);
                    }else{
                        edt3.requestFocus();
                    }
                }
            }
        });

        btnReVerifikasi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                AlertDialog dialog = new AlertDialog.Builder(context)
                        .setIcon(R.mipmap.ic_launcher)
                        .setTitle("Konfirmasi")
                        .setMessage("Kirim ulang kode verifikasi?")
                        .setPositiveButton("Ya", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {

                                Toast.makeText(context, "Tunggu hingga kode OTP dikirim ke nomor chip anda", Toast.LENGTH_LONG).show();
                                getOTP();
                            }
                        })
                        .setNegativeButton("Tidak", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {

                            }
                        })
                        .show();

            }
        });

        btnVerifikasi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(edt1.getText().toString().isEmpty()
                        || edt2.getText().toString().isEmpty()
                        || edt3.getText().toString().isEmpty()
                        || edt4.getText().toString().isEmpty()){

                    Toast.makeText(context, "Pin harap diisi sesuai ketentuan", Toast.LENGTH_LONG).show();
                    return;
                }

                saveData();
            }
        });
    }

    private void saveData() {

        final ProgressDialog progressDialog = new ProgressDialog(context, R.style.AppTheme_Login_Dialog);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Memproses...");
        progressDialog.setCancelable(false);
        progressDialog.show();

        String otp = edt1.getText().toString() + edt2.getText().toString() + edt3.getText().toString() + edt4.getText().toString();

        JSONObject jBody = new JSONObject();
        try {

            jBody.put("nomor", nomor);
            jBody.put("password", iv.encodeMD5(password));
            jBody.put("repassword", iv.encodeMD5(password));
            jBody.put("otp", otp);
            jBody.put("step", "2");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        ApiVolley request = new ApiVolley(context, jBody, "POST", ServerURL.register, new ApiVolley.VolleyCallback() {
            @Override
            public void onSuccess(String result) {

                if(progressDialog != null && progressDialog.isShowing()) progressDialog.dismiss();
                String message = "Terjadi kesalahan dalam memuat data, harap ulangi kembali";

                try {

                    JSONObject response = new JSONObject(result);
                    String status = response.getJSONObject("metadata").getString("status");
                    message = response.getJSONObject("metadata").getString("message");
                    if(iv.parseNullInteger(status) == 200){

                        Intent intent = new Intent(context, LoginScreen.class);
                        intent.putExtra("username", nomor);
                        intent.putExtra("password", password);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);
                        finish();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                Snackbar.make(findViewById(android.R.id.content), message, Snackbar.LENGTH_LONG).show();
            }

            @Override
            public void onError(String result) {
                Snackbar.make(findViewById(android.R.id.content), "Terjadi kesalahan koneksi, harap ulangi kembali nanti", Snackbar.LENGTH_LONG).show();
                if(progressDialog != null && progressDialog.isShowing()) progressDialog.dismiss();
            }
        });
    }

    private class mainTask extends TimerTask
    {
        public void run()
        {

            SimpleDateFormat simpleDateFormat = new SimpleDateFormat(FormatItem.formatTimestamp);

            try {
                Date date1 = simpleDateFormat.parse(nextTime);
                Date date2 = simpleDateFormat.parse(iv.getCurrentDate(FormatItem.formatTimestamp));

                long different = date1.getTime() - date2.getTime();

                long secondsInMilli = 1000;
                long minutesInMilli = secondsInMilli * 60;
                long hoursInMilli = minutesInMilli * 60;
                long daysInMilli = hoursInMilli * 24;

                final long elapsedDays = different / daysInMilli;
                different = different % daysInMilli;

                final long elapsedHours = different / hoursInMilli;
                different = different % hoursInMilli;

                final long elapsedMinutes = different / minutesInMilli;
                different = different % minutesInMilli;

                final long elapsedSeconds = different / secondsInMilli;

                //elapsedDays, elapsedHours, elapsedMinutes, elapsedSeconds

                if(different > 0){

                    ((Activity)context).runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            tvMinutes.setText(String.valueOf(elapsedMinutes).length() == 1 ? "0"+String.valueOf(elapsedMinutes) : String.valueOf(elapsedMinutes));
                            tvSecond.setText(String.valueOf(elapsedSeconds).length() == 1 ? "0"+String.valueOf(elapsedSeconds) : String.valueOf(elapsedSeconds));
                        }
                    });
                }else{

                    ((Activity)context).runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            tvMinutes.setText("00");
                            tvSecond.setText("00");
                        }
                    });
                }

            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
    }

    public static void fillOTP(String message){

        if(message.contains("Your One Time Password (OTP) for PSP is")){

            String otp = message.replace("Your One Time Password (OTP) for PSP is ","");
            otp = otp.replace(" and valid for 3 minutes","");
            otp = otp.trim();
            if(otp.length() == 4){

                edt1.setText(String.valueOf(otp.charAt(0)));
                edt2.setText(String.valueOf(otp.charAt(1)));
                edt3.setText(String.valueOf(otp.charAt(2)));
                edt4.setText(String.valueOf(otp.charAt(3)));

                if(!edt4.getText().toString().isEmpty()){

                    edt4.requestFocus();
                    edt4.setSelection(1);
                }else{
                    edt4.requestFocus();
                }
            }
        }
    }

    @Override
    protected void onDestroy() {
        isActive = false;
        super.onDestroy();
    }

    private void getOTP() {

        pbLoading.setVisibility(View.VISIBLE);
        JSONObject jBody = new JSONObject();
        try {
            jBody.put("nomor", nomor);
            jBody.put("flag", "1");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        final ApiVolley request = new ApiVolley(context, jBody, "POST", ServerURL.getOTP, new ApiVolley.VolleyCallback() {
            @Override
            public void onSuccess(String result) {

                pbLoading.setVisibility(View.GONE);
                try {
                    JSONObject response = new JSONObject(result);
                    String status = response.getJSONObject("metadata").getString("status");
                    String message = response.getJSONObject("metadata").getString("message");
                    if(status.equals("200")){

                        timer = new Timer();
                        nextTime = iv.getCurrentDate(iv.sumMinutes(
                                iv.getCurrentDate(FormatItem.formatTimestamp),
                                3,
                                FormatItem.formatTimestamp
                        ));
                        timer.scheduleAtFixedRate(new mainTask(), 0, 1000);
                    }else{

                        tvMinutes.setText("00");
                        tvSecond.setText("00");
                        Toast.makeText(context, message, Toast.LENGTH_LONG).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(context, "Terjadi kesalahan saat memuat data", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onError(String result) {

                pbLoading.setVisibility(View.GONE);
                Toast.makeText(context, "Terjadi kesalahan saat memuat data", Toast.LENGTH_LONG).show();
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {

        super.onBackPressed();
        overridePendingTransition(R.anim.slide_in_left,R.anim.slide_out_left);
    }
}
