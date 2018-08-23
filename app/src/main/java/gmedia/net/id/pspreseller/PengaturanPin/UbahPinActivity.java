package gmedia.net.id.pspreseller.PengaturanPin;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.maulana.custommodul.ApiVolley;
import com.maulana.custommodul.CustomView.DialogBox;
import com.maulana.custommodul.ItemValidation;
import com.maulana.custommodul.SessionManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import gmedia.net.id.pspreseller.R;
import gmedia.net.id.pspreseller.Utils.ServerURL;

public class UbahPinActivity extends AppCompatActivity {

    private Context context;
    private ItemValidation iv = new ItemValidation();
    private SessionManager session;
    private EditText edtPinLama, edtPinBaru, edtRePinBaru;
    private Button btnProses;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ubah_pin);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN
        );

        setTitle("Ubah Sandi");

        context = this;

        initUI();
    }

    private void initUI() {

        edtPinLama = (EditText) findViewById(R.id.edt_pin_lama);
        edtPinBaru = (EditText) findViewById(R.id.edt_pin_baru);
        edtRePinBaru = (EditText) findViewById(R.id.edt_re_pin_baru);
        btnProses = (Button) findViewById(R.id.btn_proses);

        session = new SessionManager(context);

        initEvent();
    }

    private void initEvent() {

        btnProses.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // validasi
                if(edtPinLama.getText().toString().isEmpty()){

                    edtPinLama.setError("Sandi lama harap diisi");
                    edtPinLama.requestFocus();
                    return;
                }else{
                    edtPinLama.setError(null);
                }

                if(edtPinLama.getText().toString().length() < 4){

                    edtPinLama.setError("Sandi lama harap 4 digit angka");
                    edtPinLama.requestFocus();
                    return;
                }else{
                    edtPinLama.setError(null);
                }

                if(edtPinBaru.getText().toString().isEmpty()){

                    edtPinBaru.setError("Sandi baru harap diisi");
                    edtPinBaru.requestFocus();
                    return;
                }else{
                    edtPinBaru.setError(null);
                }

                if(edtPinBaru.getText().toString().length() < 4){

                    edtPinBaru.setError("Sandi baru harap 4 digit angka");
                    edtPinBaru.requestFocus();
                    return;
                }else{
                    edtPinBaru.setError(null);
                }

                if(edtRePinBaru.getText().toString().isEmpty()){
                    edtRePinBaru.setError("Sandi baru harap diisi");
                    edtRePinBaru.requestFocus();
                    return;
                }else{
                    edtRePinBaru.setError(null);
                }

                if(!edtRePinBaru.getText().toString().equals(edtPinBaru.getText().toString())){
                    edtRePinBaru.setError("Sandi baru ulang tidak sama dengan Sandi baru");
                    edtRePinBaru.requestFocus();
                    return;
                }else{
                    edtRePinBaru.setError(null);
                }

                AlertDialog dialog = new AlertDialog.Builder(context)
                        .setTitle("Konfirmasi")
                        .setIcon(R.mipmap.ic_launcher)
                        .setMessage("Apakah anda yakin ingin menubah sandi?")
                        .setPositiveButton("Ya", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                saveData();
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
    }

    private void saveData() {

        final ProgressDialog progressDialog = new ProgressDialog(context, R.style.AppTheme_Login_Dialog);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Menyimpan...");
        progressDialog.setCancelable(false);
        progressDialog.show();

        JSONObject jBody = new JSONObject();
        try {
            jBody.put("pin_lama", edtPinLama.getText().toString());
            jBody.put("pin_baru", edtPinBaru.getText().toString());
            jBody.put("ulang_pin", edtRePinBaru.getText().toString());
            jBody.put("nomor", session.getUsername());
        } catch (JSONException e) {
            e.printStackTrace();
        }

        ApiVolley request = new ApiVolley(context, jBody, "POST", ServerURL.ubahPin, new ApiVolley.VolleyCallback() {
            @Override
            public void onSuccess(String result) {

                if(progressDialog != null && progressDialog.isShowing()) progressDialog.dismiss();
                String message = "Terjadi kesalahan saat memuat data";

                try {

                    JSONObject response = new JSONObject(result);
                    String status = response.getJSONObject("metadata").getString("status");
                    message = response.getJSONObject("metadata").getString("message");
                    if(iv.parseNullInteger(status) == 200){

                        edtPinLama.setText("");
                        edtPinBaru.setText("");
                        edtRePinBaru.setText("");
                        DialogBox.showDialog(context, 3, message);
                        //Toast.makeText(context, message, Toast.LENGTH_LONG).show();
                        //onBackPressed();
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
        overridePendingTransition(R.anim.slide_in_left,R.anim.slide_out_left);
    }
}
