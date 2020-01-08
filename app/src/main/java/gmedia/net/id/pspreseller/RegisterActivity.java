package gmedia.net.id.pspreseller;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import com.google.android.material.snackbar.Snackbar;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;

import com.maulana.custommodul.ApiVolley;
import com.maulana.custommodul.ItemValidation;

import org.json.JSONException;
import org.json.JSONObject;

import gmedia.net.id.pspreseller.Register.OtpRegisterActivity;
import gmedia.net.id.pspreseller.Utils.ServerURL;

public class RegisterActivity extends AppCompatActivity {

    private Context context;
    private ItemValidation iv = new ItemValidation();
    private EditText edtUsername, edtPassword, edtRePassword;
    private Button btnDaftar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        //getSupportActionBar().setHomeAsUpIndicator(getResources().getDrawable(R.mipmap.ic_down));

        getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN
        );

        setTitle("Registrasi");
        context = this;

        initUI();
    }

    private void initUI() {

        edtUsername = (EditText) findViewById(R.id.edt_username);
        edtPassword = (EditText) findViewById(R.id.edt_password);
        edtRePassword = (EditText) findViewById(R.id.edt_re_password);
        btnDaftar = (Button) findViewById(R.id.btn_daftar);

        initEvent();
    }

    private void initEvent() {

        btnDaftar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                validasi();
            }
        });
    }

    private void validasi() {

        if(edtUsername.getText().toString().isEmpty()){

            edtUsername.setError("No Chip harap diisi");
            edtUsername.requestFocus();
            return;
        }else{

            edtUsername.setError(null);
        }

        if(edtPassword.getText().toString().isEmpty()){

            edtPassword.setError("Password harap diisi");
            edtPassword.requestFocus();
            return;
        }else{

            edtPassword.setError(null);
        }

        if(edtRePassword.getText().toString().isEmpty()){

            edtRePassword.setError("Password ulang harap diisi");
            edtRePassword.requestFocus();
            return;
        }else{

            edtRePassword.setError(null);
        }

        if(!edtPassword.getText().toString().equals(edtRePassword.getText().toString())){
            edtRePassword.setError("Password ulang tidak sama");
            edtRePassword.requestFocus();
            return;
        }else{

            edtRePassword.setError(null);
        }

        AlertDialog dialog = new AlertDialog.Builder(context)
                .setIcon(R.mipmap.ic_launcher)
                .setTitle("Konfirmasi")
                .setMessage("Apakah data anda sudah benar?")
                .setPositiveButton("Ya", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                        saveData();
                    }
                }).setNegativeButton("Tidak", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                })
                .show();

    }

    private void saveData() {

        final ProgressDialog progressDialog = new ProgressDialog(context, R.style.AppTheme_Login_Dialog);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Memproses...");
        progressDialog.setCancelable(false);
        progressDialog.show();

        JSONObject jBody = new JSONObject();
        try {

            jBody.put("nomor", edtUsername.getText().toString());
            jBody.put("password", iv.encodeMD5(edtPassword.getText().toString()));
            jBody.put("repassword", iv.encodeMD5(edtRePassword.getText().toString()));
            jBody.put("otp", "");
            jBody.put("step", "1");
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

                        Intent intent = new Intent(context, OtpRegisterActivity.class);
                        intent.putExtra("nomor", edtUsername.getText().toString());
                        intent.putExtra("password", edtPassword.getText().toString());
                        startActivity(intent);
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
