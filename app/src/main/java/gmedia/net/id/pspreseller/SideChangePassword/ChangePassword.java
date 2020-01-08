package gmedia.net.id.pspreseller.SideChangePassword;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import androidx.appcompat.app.AppCompatActivity;
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

import org.json.JSONException;
import org.json.JSONObject;

import gmedia.net.id.pspreseller.R;
import gmedia.net.id.pspreseller.Utils.ServerURL;

public class ChangePassword extends AppCompatActivity {

    private Context context;
    private ItemValidation iv = new ItemValidation();
    private SessionManager session;
    private EditText edtPasswordLama, edtPasswordBaru, edtRePasswordBaru;
    private Button btnProses;
    public static boolean isSaved = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN
        );

        setTitle("Ubah Password");

        context = this;

        initUI();
    }

    private void initUI() {

        edtPasswordLama = (EditText) findViewById(R.id.edt_password_lama);
        edtPasswordBaru = (EditText) findViewById(R.id.edt_password_baru);
        edtRePasswordBaru = (EditText) findViewById(R.id.edt_re_password_baru);
        btnProses = (Button) findViewById(R.id.btn_proses);

        session = new SessionManager(context);

        initEvent();
    }

    private void initEvent() {

        btnProses.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // validasi
                if(edtPasswordLama.getText().toString().isEmpty()){

                    edtPasswordLama.setError("Password lama harap diisi");
                    edtPasswordLama.requestFocus();
                    return;
                }else{
                    edtPasswordLama.setError(null);
                }

                if(edtPasswordBaru.getText().toString().isEmpty()){

                    edtPasswordBaru.setError("Password baru harap diisi");
                    edtPasswordBaru.requestFocus();
                    return;
                }else{
                    edtPasswordBaru.setError(null);
                }

                if(edtRePasswordBaru.getText().toString().isEmpty()){
                    edtRePasswordBaru.setError("Re Password baru harap diisi");
                    edtRePasswordBaru.requestFocus();
                    return;
                }else{
                    edtRePasswordBaru.setError(null);
                }

                if(!edtRePasswordBaru.getText().toString().equals(edtPasswordBaru.getText().toString())){
                    edtRePasswordBaru.setError("Re Password baru tidak sama dengan Password baru");
                    edtRePasswordBaru.requestFocus();
                    return;
                }else{
                    edtRePasswordBaru.setError(null);
                }

                AlertDialog dialog = new AlertDialog.Builder(context)
                        .setTitle("Konfirmasi")
                        .setIcon(R.mipmap.ic_launcher)
                        .setMessage("Apakah anda yakin ingin menubah password?")
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
            jBody.put("password_lama", edtPasswordLama.getText().toString());
            jBody.put("password_baru", edtPasswordBaru.getText().toString());
            jBody.put("repassword", edtRePasswordBaru.getText().toString());
            jBody.put("otp", "");
            jBody.put("step", "1");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        ApiVolley request = new ApiVolley(context, jBody, "POST", ServerURL.changePassword, new ApiVolley.VolleyCallback() {
            @Override
            public void onSuccess(String result) {

                if(progressDialog != null && progressDialog.isShowing()) progressDialog.dismiss();
                String message = "Terjadi kesalahan saat memuat data";

                try {

                    JSONObject response = new JSONObject(result);
                    String status = response.getJSONObject("metadata").getString("status");
                    message = response.getJSONObject("metadata").getString("message");
                    if(iv.parseNullInteger(status) == 200){

                        /*edtPasswordLama.setText("");
                        edtPasswordBaru.setText("");
                        edtRePasswordBaru.setText("");
                        DialogBox.showDialog(context, 3, message);*/
                        //Toast.makeText(context, message, Toast.LENGTH_LONG).show();
                        //onBackPressed();
                        Intent intent = new Intent(context, OtpChangePassword.class);
                        intent.putExtra("passwordlama", edtPasswordLama.getText().toString());
                        intent.putExtra("passwordbaru", edtPasswordBaru.getText().toString());
                        startActivity(intent);

                    }

                    Toast.makeText(context, message, Toast.LENGTH_LONG).show();
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
    protected void onResume() {
        super.onResume();

        if(isSaved){
            isSaved = false;
            DialogBox.showDialog(context, 1, "Password berhasil diubah");
            edtPasswordLama.setText("");
            edtPasswordBaru.setText("");
            edtRePasswordBaru.setText("");
        }
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
