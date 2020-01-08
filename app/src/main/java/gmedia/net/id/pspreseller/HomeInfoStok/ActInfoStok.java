package gmedia.net.id.pspreseller.HomeInfoStok;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.provider.Settings;
import com.google.android.material.snackbar.Snackbar;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.maulana.custommodul.ApiVolley;
import com.maulana.custommodul.CustomView.DialogBox;
import com.maulana.custommodul.ItemValidation;
import com.maulana.custommodul.SessionManager;

import org.json.JSONException;
import org.json.JSONObject;

import gmedia.net.id.pspreseller.HomePulsa.OrderPulsa;
import gmedia.net.id.pspreseller.HomePulsa.Service.ServiceHandler;
import gmedia.net.id.pspreseller.R;
import gmedia.net.id.pspreseller.Utils.ServerURL;

public class ActInfoStok extends AppCompatActivity {

    private Context context;
    private ItemValidation iv = new ItemValidation();
    private SessionManager session;
    private LinearLayout llStokMkios, llStokTcash, llStokBulk, llStokPbob;
    private String TAG = "Testing";
    private DialogBox dialogBox;
    private boolean isAccessGranted = false;
    private String pin = "", flagPin = "";
    private boolean dataIsLoaded = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nav_info_stok);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(getResources().getDrawable(R.mipmap.ic_down));

        getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN
        );

        context = this;
        session = new SessionManager(context);
        dialogBox = new DialogBox(context);

        setTitle("Informasi Stok");

        initUI();
    }

    private void initUI() {

        llStokMkios = (LinearLayout) findViewById(R.id.ll_stok_mkios);
        llStokTcash = (LinearLayout) findViewById(R.id.ll_stok_tcash);
        llStokBulk = (LinearLayout) findViewById(R.id.ll_stok_bulk);
        llStokPbob = (LinearLayout) findViewById(R.id.ll_stok_pbob);

        initEvent();
    }

    private void initEvent() {

        llStokMkios.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                getSaldoDetail("MK");
            }
        });

        llStokTcash.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                getSaldoDetail("TC");
            }
        });

        llStokBulk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                getSaldoDetail("MB");
            }
        });

        llStokPbob.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                getSaldoDetail("SD");
            }
        });
    }

    private void getDataPin() {

        dataIsLoaded = false;
        dialogBox.showDialog(false);
        JSONObject jBody = new JSONObject();

        try {
            jBody.put("tipe", "1");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        final ApiVolley request = new ApiVolley(context, jBody, "POST", ServerURL.getSavedPin, new ApiVolley.VolleyCallback() {
            @Override
            public void onSuccess(String result) {

                dialogBox.dismissDialog();
                try {
                    JSONObject response = new JSONObject(result);
                    String status = response.getJSONObject("metadata").getString("status");

                    if(status.equals("200")){

                        pin = response.getJSONObject("response").getString("pin");
                        flagPin = response.getJSONObject("response").getString("flag");
                        dataIsLoaded = true;

                    }

                } catch (JSONException e) {
                    e.printStackTrace();

                    View.OnClickListener clickListener = new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {

                            dialogBox.dismissDialog();
                            getDataPin();

                        }
                    };

                    dialogBox.showDialog(clickListener, "Ulangi Proses", "Terjadi kesalahan saat mengambil data");
                }
            }

            @Override
            public void onError(String result) {

                dialogBox.dismissDialog();

                View.OnClickListener clickListener = new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        dialogBox.dismissDialog();
                        getDataPin();

                    }
                };

                dialogBox.showDialog(clickListener, "Ulangi Proses", "Terjadi kesalahan saat mengambil data");
            }
        });
    }

    private void getSaldoDetail(final String flag) {

        if(!isAccessGranted){

            Toast.makeText(context, "Mohon aktifkan ijin akses terlebih dahulu", Toast.LENGTH_LONG).show();
            return;
        }

        JSONObject jBody = new JSONObject();
        try {
            jBody.put("kode", flag);
            jBody.put("nomor", session.getUsername());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        final ApiVolley request = new ApiVolley(context, jBody, "POST", ServerURL.checkSaldo, new ApiVolley.VolleyCallback() {
            @Override
            public void onSuccess(String result) {

                try {
                    JSONObject response = new JSONObject(result);
                    Log.d(TAG, "onSuccess: "+result);
                    String status = response.getJSONObject("metadata").getString("status");
                    String message = response.getJSONObject("metadata").getString("message");
                    if(status.equals("200")){

                        JSONObject jo = response.getJSONObject("response");
                        giveDecisionAfterGettingResponse(jo.getString("flag"),jo.getString("value"), flag);

                    }else{
                        Toast.makeText(context, message, Toast.LENGTH_LONG).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(context, "Terjadi kesalahan saat memuat data", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onError(String result) {
                Toast.makeText(context, "Terjadi kesalahan saat memuat data", Toast.LENGTH_LONG).show();

            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();

        OrderPulsa.isActive = false;

        isAccessGranted =  isAccessibilityEnabled(context.getPackageName() + "/" + context.getPackageName() + ".HomePulsa.Service.USSDService");
        if(isAccessGranted){

            //Log.d(TAG, "granted");
        }else{
            //Log.d(TAG, "not granted");
            Snackbar.make(findViewById(android.R.id.content), "Mohon ijinkan akses pada "+ getResources().getString(R.string.app_name)+", Cari "+ getResources().getString(R.string.app_name)+" dan ubah enable",
                    Snackbar.LENGTH_INDEFINITE).setAction("OK",
                    new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            startActivity(new Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS));
                        }
                    }).show();

        }


        ServiceHandler serviceHandler = new ServiceHandler(ActInfoStok.this);

        getDataPin();
    }

    public boolean isAccessibilityEnabled(String id){

        int accessibilityEnabled = 0;
        boolean accessibilityFound = false;
        try {
            accessibilityEnabled = Settings.Secure.getInt(this.getContentResolver(),android.provider.Settings.Secure.ACCESSIBILITY_ENABLED);
            Log.d(TAG, "ACCESSIBILITY: " + accessibilityEnabled);
        } catch (Settings.SettingNotFoundException e) {
            Log.d(TAG, "Error finding setting, default accessibility to not found: " + e.getMessage());
        }

        TextUtils.SimpleStringSplitter mStringColonSplitter = new TextUtils.SimpleStringSplitter(':');

        if (accessibilityEnabled==1){
            //Log.d(TAG, "***ACCESSIBILIY IS ENABLED***: ");

            String settingValue = Settings.Secure.getString(getContentResolver(), Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES);
            //Log.d(TAG, "Setting: " + settingValue);
            if (settingValue != null) {
                TextUtils.SimpleStringSplitter splitter = mStringColonSplitter;
                splitter.setString(settingValue);
                while (splitter.hasNext()) {
                    String accessabilityService = splitter.next();
                    //Log.d(TAG, "Setting: " + accessabilityService);
                    if (accessabilityService.toLowerCase().equals(id.toLowerCase())){
                        //Log.d(TAG, "We've found the correct setting - accessibility is switched on!");
                        return true;
                    }
                }
            }

            //Log.d(TAG, "***END***");
        }
        else{
            //Log.d(TAG, "***ACCESSIBILIY IS DISABLED***");
        }
        return accessibilityFound;
    }

    private void giveDecisionAfterGettingResponse(final String flag, final String value, final String kode){

        if(value.toLowerCase().contains("[pin]")){ //having chip pin


            final AlertDialog.Builder builder = new AlertDialog.Builder(context);
            LayoutInflater inflater = (LayoutInflater) ((Activity)context).getSystemService(LAYOUT_INFLATER_SERVICE);
            View viewDialog = inflater.inflate(R.layout.dialog_pin, null);
            builder.setView(viewDialog);
            builder.setCancelable(false);

            final TextView tvTitle = (TextView) viewDialog.findViewById(R.id.tv_title);
            final EditText edtPin = (EditText) viewDialog.findViewById(R.id.edt_pin);
            final Button btnTutup = (Button) viewDialog.findViewById(R.id.btn_tutup);
            final Button btnProses = (Button) viewDialog.findViewById(R.id.btn_proses);
            final CheckBox cbSimpan = (CheckBox) viewDialog.findViewById(R.id.cb_simpan);

            if(flagPin.equals("1")){

                cbSimpan.setChecked(true);
                edtPin.setText(pin);
                if(pin.length() > 0) edtPin.setSelection(pin.length());
            }else{
                cbSimpan.setChecked(false);
                edtPin.setText("");
            }

            final AlertDialog alert = builder.create();
            alert.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

            tvTitle.setText("Masukkan Pin Chip");

            btnTutup.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view2) {

                    if(alert != null) alert.dismiss();
                }
            });

            btnProses.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view2) {

                    if(edtPin.getText().toString().isEmpty()){

                        edtPin.setError("Pin harap diisi");
                        edtPin.requestFocus();
                        return;
                    }else{

                        edtPin.setError(null);
                    }

                    if(alert != null) alert.dismiss();

                    if(cbSimpan.isChecked()) {

                        flagPin = "1";
                    }else{
                        flagPin = "0";
                    }

                    pin = edtPin.getText().toString();
                    savePin(value, flag, kode);
                }
            });

            alert.show();

        }else{

            Intent intent = new Intent(context, DetailInfoStok.class);
            intent.putExtra("flag", flag);
            intent.putExtra("value", value);
            intent.putExtra("kode", kode);
            startActivity(intent);
        }
    }

    private void savePin(final String value, final String flag, final String kode) {

        final ProgressDialog progressDialog = new ProgressDialog(context, R.style.AppTheme_Login_Dialog);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Menyimpan...");
        progressDialog.setCancelable(false);
        progressDialog.show();

        JSONObject jBody = new JSONObject();

        try {
            jBody.put("pin", pin);
            jBody.put("tipe", "1");
            jBody.put("flag", flagPin);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        ApiVolley request = new ApiVolley(context, jBody, "POST", ServerURL.savePinFlag, new ApiVolley.VolleyCallback() {
            @Override
            public void onSuccess(String result) {

                if(progressDialog != null && progressDialog.isShowing()) progressDialog.dismiss();
                String message = "Terjadi kesalahan saat memuat data";

                try {

                    JSONObject response = new JSONObject(result);
                    String status = response.getJSONObject("metadata").getString("status");
                    message = response.getJSONObject("metadata").getString("message");

                    if(iv.parseNullInteger(status) == 200){

                        String newValue = value.replace("[PIN]", pin);

                        Intent intent = new Intent(context, DetailInfoStok.class);
                        intent.putExtra("flag", flag);
                        intent.putExtra("value", newValue);
                        intent.putExtra("kode", kode);
                        startActivity(intent);
                    }else{

                        View.OnClickListener clickListener = new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {

                                dialogBox.dismissDialog();
                                savePin(value, flag, kode);
                            }
                        };

                        dialogBox.showDialog(clickListener, "Ulangi Proses", message);

                        Toast.makeText(context, message, Toast.LENGTH_LONG).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(context, message, Toast.LENGTH_LONG).show();
                }

            }

            @Override
            public void onError(String result) {

                String message = "Terjadi kesalahan saat memuat data";
                Toast.makeText(context, message, Toast.LENGTH_LONG).show();
                if(progressDialog != null && progressDialog.isShowing()) progressDialog.dismiss();
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
        overridePendingTransition(R.anim.slide_in_down,R.anim.slide_out_down);
    }
}
