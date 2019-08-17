package gmedia.net.id.pspreseller;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Handler;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
import android.text.method.PasswordTransformationMethod;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.iid.FirebaseInstanceId;
import com.maulana.custommodul.ApiVolley;
import com.maulana.custommodul.CustomView.DialogBox;
import com.maulana.custommodul.ItemValidation;
import com.maulana.custommodul.RuntimePermissionsActivity;
import com.maulana.custommodul.SessionManager;

import org.json.JSONException;
import org.json.JSONObject;

import gmedia.net.id.pspreseller.NotificationUtil.InitFirebaseSetting;
import gmedia.net.id.pspreseller.Utils.ServerURL;

public class LoginScreen extends RuntimePermissionsActivity {

    private static boolean doubleBackToExitPressedOnce;
    private boolean exitState = false;
    private int timerClose = 2000;

    private EditText edtUsername, edtPassword;
    private CheckBox cbRemeber;
    private Button btnLogin;
    private static final int REQUEST_PERMISSIONS = 20;
    private SessionManager session;
    private boolean visibleTapped;
    private ItemValidation iv = new ItemValidation();
    private String refreshToken = "";
    private TextView tvRegister, tvReset;
    private Context context;
    public static boolean isReset = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_screen);

        //Check close statement
        context = this;
        doubleBackToExitPressedOnce = false;
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {

            if (bundle.getBoolean("exit", false)) {
                exitState = true;
                finish();
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            }
        }

        if (ContextCompat.checkSelfPermission(
                LoginScreen.this, android.Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(
                LoginScreen.this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(
                LoginScreen.this, android.Manifest.permission.WAKE_LOCK) != PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(
                LoginScreen.this, android.Manifest.permission.VIBRATE) != PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(
                LoginScreen.this, android.Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(
                LoginScreen.this, android.Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(
                LoginScreen.this, android.Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(
                LoginScreen.this, android.Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(
                LoginScreen.this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(
                LoginScreen.this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            LoginScreen.super.requestAppPermissions(new
                            String[]{ android.Manifest.permission.READ_EXTERNAL_STORAGE,
                            android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
                            android.Manifest.permission.WAKE_LOCK,
                            android.Manifest.permission.VIBRATE,
                            android.Manifest.permission.CAMERA,
                            android.Manifest.permission.READ_PHONE_STATE,
                            android.Manifest.permission.CALL_PHONE,
                            android.Manifest.permission.READ_CONTACTS,
                            android.Manifest.permission.ACCESS_COARSE_LOCATION,
                            Manifest.permission.ACCESS_FINE_LOCATION
                    }, R.string
                            .runtime_permissions_txt
                    , REQUEST_PERMISSIONS);
        }

        InitFirebaseSetting.getFirebaseSetting(LoginScreen.this);

        refreshToken = FirebaseInstanceId.getInstance().getToken();
        initUI();
    }

    @Override
    public void onPermissionsGranted(int requestCode) {

    }

    @Override
    protected void onResume() {
        super.onResume();

        if(isReset){
            isReset = false;
            DialogBox.showDialog(context, 1, "Password berhasil direset");
        }
    }

    private void initUI() {

        edtUsername = (EditText) findViewById(R.id.edt_username);
        edtPassword = (EditText) findViewById(R.id.edt_password);
        cbRemeber = (CheckBox) findViewById(R.id.cb_simpan);
        btnLogin = (Button) findViewById(R.id.btn_login);
        tvRegister = (TextView) findViewById(R.id.tv_register);
        tvReset = (TextView) findViewById(R.id.tv_reset);

        visibleTapped = true;
        session = new SessionManager(LoginScreen.this);

        if (session.isSaved()) {

            redirectToLogin();
        }

        cbRemeber.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {

                iv.hideSoftKey(LoginScreen.this);
            }
        });

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                validasiLogin();
            }
        });

        Bundle bundle = getIntent().getExtras();

        if(bundle != null){

            String username = bundle.getString("username", "");
            String password = bundle.getString("password", "");

            if(!username.isEmpty() && !password.isEmpty()){

                edtUsername.setText(username);
                edtPassword.setText(password);
                cbRemeber.setChecked(true);

                validasiLogin();
            }else if(!username.isEmpty()){

                edtUsername.setText(username);
            }
        }

        /*edtPassword.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                final int position = 2;

                if (event.getAction() == MotionEvent.ACTION_UP) {

                    if (event.getX() >= (edtPassword.getRight() - edtPassword.getCompoundDrawables()[position].getBounds().width())) {

                        if (visibleTapped) {
                            edtPassword.setTransformationMethod(null);
                            edtPassword.setSelection(edtPassword.getText().length());
                            visibleTapped = false;
                        } else {
                            edtPassword.setTransformationMethod(new PasswordTransformationMethod());
                            edtPassword.setSelection(edtPassword.getText().length());
                            visibleTapped = true;
                        }
                        return false;
                    }
                }

                return false;
            }
        });*/

        initEvent();
    }

    private void initEvent() {

        tvRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(context, RegisterActivity.class);
                startActivity(intent);
            }
        });

        tvReset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(context, ResetActivity.class);
                startActivity(intent);
            }
        });
    }

    private void validasiLogin() {

        if (edtUsername.getText().length() <= 0) {

            Snackbar.make(findViewById(android.R.id.content), "Username tidak boleh kosong",
                    Snackbar.LENGTH_LONG).setAction("OK",
                    new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                        }
                    }).show();

            edtUsername.requestFocus();
            return;
        }

        if (edtPassword.getText().length() <= 0) {

            Snackbar.make(findViewById(android.R.id.content), "Password tidak boleh kosong",
                    Snackbar.LENGTH_LONG).setAction("OK",
                    new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                        }
                    }).show();

            edtPassword.requestFocus();

            return;
        }

        login();
    }

    private void login() {

        final ProgressDialog progressDialog = new ProgressDialog(LoginScreen.this, R.style.AppTheme_Login_Dialog);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Authenticating...");
        progressDialog.setCancelable(false);
        progressDialog.show();



        JSONObject jBody = new JSONObject();
        try {
            jBody.put("username", edtUsername.getText().toString());
            jBody.put("password", iv.encodeMD5(edtPassword.getText().toString()));
            jBody.put("fcm_id", refreshToken);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        ApiVolley request = new ApiVolley(LoginScreen.this, jBody, "POST", ServerURL.login, new ApiVolley.VolleyCallback() {
            @Override
            public void onSuccess(String result) {

                String message = "";

                try {

                    JSONObject response = new JSONObject(result);
                    String status = response.getJSONObject("metadata").getString("status");
                    message = response.getJSONObject("metadata").getString("message");
                    if(iv.parseNullInteger(status) == 200){

                        if(progressDialog != null && progressDialog.isShowing()) progressDialog.dismiss();

                        String kdcus = response.getJSONObject("response").getString("kdcus");
                        String nama = response.getJSONObject("response").getString("nama");
                        String alamat = response.getJSONObject("response").getString("alamat");
                        String image = response.getJSONObject("response").getString("image");
                        session.createLoginSession(kdcus,
                                edtUsername.getText().toString(),
                                nama,
                                alamat,
                                image,
                                cbRemeber.isChecked()? "1":"0");
                        Toast.makeText(LoginScreen.this, message, Toast.LENGTH_SHORT).show();

                        redirectToLogin();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                if(progressDialog != null && progressDialog.isShowing()) progressDialog.dismiss();
                Snackbar.make(findViewById(android.R.id.content), message, Snackbar.LENGTH_LONG).show();
            }

            @Override
            public void onError(String result) {
                Snackbar.make(findViewById(android.R.id.content), "Terjadi kesalahan koneksi, harap ulangi kembali nanti", Snackbar.LENGTH_LONG).show();
                if(progressDialog != null && progressDialog.isShowing()) progressDialog.dismiss();
            }
        });
    }

    private void redirectToLogin(){

        Intent intent = new Intent(LoginScreen.this, HomeActivity.class);
        startActivity(intent);
        finish();
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
    }

    @Override
    public void onBackPressed() {

        // Origin backstage
        if (doubleBackToExitPressedOnce) {
            Intent intent = new Intent(LoginScreen.this, LoginScreen.class);
            intent.putExtra("exit", true);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            //System.exit(0);
        }

        if(!exitState && !doubleBackToExitPressedOnce){
            this.doubleBackToExitPressedOnce = true;
            Toast.makeText(this, getResources().getString(R.string.app_exit), Toast.LENGTH_SHORT).show();
        }

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                doubleBackToExitPressedOnce=false;
            }
        }, timerClose);
    }
}
