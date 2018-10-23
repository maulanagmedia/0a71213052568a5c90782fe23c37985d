package gmedia.net.id.pspreseller.TopUP;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.drawable.ColorDrawable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.Html;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.maulana.custommodul.ApiVolley;
import com.maulana.custommodul.CustomItem;
import com.maulana.custommodul.CustomView.DialogBox;
import com.maulana.custommodul.FormatItem;
import com.maulana.custommodul.ItemValidation;
import com.maulana.custommodul.SessionManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import gmedia.net.id.pspreseller.HomeActivity;
import gmedia.net.id.pspreseller.HomeMkios.Adapter.ListDenomAdapter;
import gmedia.net.id.pspreseller.R;
import gmedia.net.id.pspreseller.Utils.ServerURL;

public class IsiSaldo extends AppCompatActivity {

    private static double totalBayar = 0;
    private SessionManager session;
    private static ItemValidation iv = new ItemValidation();
    private static EditText edtNominal;
    private Button btnProses;
    private DialogBox dialogBox;
    private Context context;
    private String currentString = "";
    private static ListDenomAdapter adapterDenom;
    private static TextView tvJumlahHargaMkios;
    private List<CustomItem> listDenom;
    private ListView lvDenom;
    private static List<CustomItem> items;
    private static double totalHarga = 0;
    private EditText edtNominalTcash, edtNominalBulk;
    private TextView tvJumlahHargaTcash, tvJumlahHargaBulk;
    private Timer timerTcash;
    private static String hargaTcash = "0";
    private static String hargaBulk = "0";
    private Timer timerBulk;
    private boolean isTyping = false;
    private static TextView tvTotalHarga;
    private String pin = "", flagPin = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_isi_saldo);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(getResources().getDrawable(R.mipmap.ic_down));

        getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN
        );

        context = this;
        session = new SessionManager(context);
        setTitle("Top Up Saldo Tunai");

        initUI();
    }

    private void initUI() {

        edtNominal = (EditText) findViewById(R.id.edt_nominal);
        btnProses = (Button) findViewById(R.id.btn_proses);
        lvDenom = (ListView) findViewById(R.id.lv_denom);
        lvDenom.setItemsCanFocus(true);
        dialogBox = new DialogBox(context);
        tvJumlahHargaMkios = (TextView) findViewById(R.id.tv_jumlah_harga_mkios);
        edtNominalTcash = (EditText) findViewById(R.id.edt_nominal_tcash);
        tvJumlahHargaTcash = (TextView) findViewById(R.id.tv_jumlah_harga_tcash);
        edtNominalBulk = (EditText) findViewById(R.id.edt_nominal_bulk);
        tvJumlahHargaBulk = (TextView) findViewById(R.id.tv_jumlah_harga_bulk);
        tvTotalHarga = (TextView) findViewById(R.id.tv_total_harga);

        listDenom = new ArrayList<>();
        items = new ArrayList<>();
        adapterDenom = new ListDenomAdapter((Activity) context, listDenom);
        updateHarga();

        //dialogBox.showDialog(false);
        isTyping = false;
        totalBayar = 0;
        hargaTcash = "0";
        hargaBulk = "0";

        initEvent();
        //getDataDenom();
        getDataPin();
    }

    private void initEvent() {

        edtNominalTcash.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                if (timerTcash != null) {
                    timerTcash.cancel();
                }
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {

                if(editable.length() > 0){
                    timerTcash = new Timer();
                    timerTcash.schedule(new TimerTask() {
                        @Override
                        public void run() {

                            isTyping = true;
                            try {
                                Thread.sleep(200);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }

                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {

                                    getHargaTcash();
                                }
                            });
                        }
                    }, 600);
                }else{

                    hargaTcash = "0";
                    tvJumlahHargaTcash.setText(iv.ChangeToRupiahFormat(hargaTcash));
                    isTyping = false;
                    updateTotalHarga();
                }
            }
        });

        edtNominalBulk.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                if (timerBulk != null) {
                    timerBulk.cancel();
                }
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {

                if(editable.length() > 0){
                    timerBulk = new Timer();
                    timerBulk.schedule(new TimerTask() {
                        @Override
                        public void run() {

                            isTyping = true;
                            try {
                                Thread.sleep(200);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }

                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {

                                    getHargaBulk();
                                }
                            });
                        }
                    }, 600);
                }else{

                    hargaBulk = "0";
                    tvJumlahHargaBulk.setText(iv.ChangeToRupiahFormat(hargaBulk));
                    isTyping = false;
                    updateTotalHarga();
                }
            }
        });

        edtNominal.addTextChangedListener(new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {


            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {

                if(!editable.toString().equals(currentString)){

                    String cleanString = editable.toString().replaceAll("[,.]", "");
                    edtNominal.removeTextChangedListener(this);

                    String formatted = iv.ChangeToCurrencyFormat(cleanString);

                    currentString = formatted;
                    edtNominal.setText(formatted);
                    edtNominal.setSelection(formatted.length());

                    updateTotalHarga();
                    edtNominal.addTextChangedListener(this);
                }
            }
        });

        btnProses.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // Validasi
                if(totalBayar <= 0){

                    /*DialogBox.showDialog(context,3,"Harap mengisi minimal salah satu Stok Deposit");
                    return;*/
                    edtNominal.setError("Nominal saldo tunai harap diisi");
                    edtNominal.requestFocus();
                    return;
                }else{
                    edtNominal.setError(null);
                }

                if(isTyping){

                    Toast.makeText(context, "Harap tunggu hingga proses selesai", Toast.LENGTH_LONG).show();
                    return;
                }

                AlertDialog dialog = new AlertDialog.Builder(context)
                        .setIcon(R.mipmap.ic_launcher)
                        .setTitle("Konfirmasi")
                        .setMessage("Apakah anda yakin ingin memproses top up?")
                        .setPositiveButton("Ya", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                showPinDialog();
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

    //region MKIOS
    private void getDataDenom() {

        dialogBox.showDialog(false);
        JSONObject jBody = new JSONObject();

        try {
            jBody.put("nomor", session.getUsername());
        } catch (JSONException e) {
            e.printStackTrace();
        }

        final ApiVolley request = new ApiVolley(context, jBody, "POST", ServerURL.getDenom, new ApiVolley.VolleyCallback() {
            @Override
            public void onSuccess(String result) {

                dialogBox.dismissDialog();
                try {
                    JSONObject response = new JSONObject(result);
                    String status = response.getJSONObject("metadata").getString("status");
                    listDenom = new ArrayList<>();
                    if(status.equals("200")){

                        JSONArray jsonArray = response.getJSONArray("response");
                        for(int i = 0; i < jsonArray.length(); i ++){
                            JSONObject jo = jsonArray.getJSONObject(i);
                            listDenom.add(new CustomItem(jo.getString("kodebrg"), jo.getString("namabrg"), jo.getString("hargajual")));
                        }

                    }

                    setDenomAdapter(listDenom);
                } catch (JSONException e) {
                    e.printStackTrace();

                    setDenomAdapter(null);
                    View.OnClickListener clickListener = new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {

                            dialogBox.dismissDialog();
                            getDataDenom();

                        }
                    };

                    dialogBox.showDialog(clickListener, "Ulangi Proses", "Terjadi kesalahan saat mengambil data");
                }
            }

            @Override
            public void onError(String result) {

                setDenomAdapter(null);
                dialogBox.dismissDialog();

                View.OnClickListener clickListener = new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        dialogBox.dismissDialog();
                        getDataDenom();

                    }
                };

                dialogBox.showDialog(clickListener, "Ulangi Proses", "Terjadi kesalahan saat mengambil data");

                getDataPin();
            }
        });
    }

    private void getDataPin() {

        dialogBox.showDialog(false);
        JSONObject jBody = new JSONObject();

        try {
            jBody.put("tipe", "2");
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

    public static void updateHarga(){
        totalHarga = 0;

        if(adapterDenom != null){
            items = adapterDenom.getItems();

            if(items.size() > 0){
                for(CustomItem item : items){
                    totalHarga += (iv.parseNullDouble(item.getItem3()) * iv.parseNullDouble(item.getItem4()));
                }
            }
        }

        updateTotalHarga();
        tvJumlahHargaMkios.setText(iv.ChangeToRupiahFormat(totalHarga));
    }

    private void setDenomAdapter(List<CustomItem> listItem) {

        lvDenom.setAdapter(null);
        if(listItem != null && listItem.size() > 0){

            adapterDenom = new ListDenomAdapter(IsiSaldo.this, listItem);
            lvDenom.setAdapter(adapterDenom);
        }
    }
    //endregion

    //region TCash
    private void getHargaTcash() {

        JSONObject jBody = new JSONObject();
        try {
            jBody.put("flag", "TC");
            jBody.put("nominal", edtNominalTcash.getText().toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        final ApiVolley request = new ApiVolley(context, jBody, "POST", ServerURL.getHarga, new ApiVolley.VolleyCallback() {
            @Override
            public void onSuccess(String result) {

                isTyping = false;
                try {
                    JSONObject response = new JSONObject(result);
                    String status = response.getJSONObject("metadata").getString("status");
                    String message = response.getJSONObject("metadata").getString("message");
                    if(status.equals("200")){

                        hargaTcash = response.getJSONObject("response").getString("harga");
                        tvJumlahHargaTcash.setText(iv.ChangeToRupiahFormat(hargaTcash));
                        updateTotalHarga();

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
                isTyping = false;
            }
        });
    }
    //endregion

    //region Bulk

    private void getHargaBulk() {

        JSONObject jBody = new JSONObject();
        try {
            jBody.put("flag", "MB");
            jBody.put("nominal", edtNominalBulk.getText().toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        final ApiVolley request = new ApiVolley(context, jBody, "POST", ServerURL.getHarga, new ApiVolley.VolleyCallback() {
            @Override
            public void onSuccess(String result) {

                isTyping = false;
                try {
                    JSONObject response = new JSONObject(result);
                    String status = response.getJSONObject("metadata").getString("status");
                    String message = response.getJSONObject("metadata").getString("message");
                    if(status.equals("200")){

                        hargaBulk = response.getJSONObject("response").getString("harga");
                        tvJumlahHargaBulk.setText(iv.ChangeToRupiahFormat(hargaBulk));
                        updateTotalHarga();

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
                isTyping = false;
            }
        });
    }
    //endregion

    private static void updateTotalHarga(){

        final String nominal = edtNominal.getText().toString().replaceAll("[,.]", "");
        totalBayar = totalHarga + iv.parseNullDouble(hargaTcash) + iv.parseNullDouble(hargaBulk) + iv.parseNullDouble(nominal);
        tvTotalHarga.setText(iv.ChangeToRupiahFormat(totalBayar));
    }

    private void showPinDialog(){

        final android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(context);
        LayoutInflater inflater = (LayoutInflater) ((Activity)context).getSystemService(LAYOUT_INFLATER_SERVICE);
        View viewDialog = inflater.inflate(R.layout.dialog_pin, null);
        builder.setView(viewDialog);
        builder.setCancelable(false);

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

        final android.app.AlertDialog alert = builder.create();
        alert.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

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

                    edtPin.setError("Sandi harap diisi");
                    edtPin.requestFocus();
                    return;
                }else{

                    edtPin.setError(null);
                }

                if(edtPin.getText().toString().length() < 4){

                    edtPin.setError("Sandi harap 4 digit");
                    edtPin.requestFocus();
                    return;
                }else{

                    edtPin.setError(null);
                }

                if(alert != null) alert.dismiss();

                //saveDeposit(edtPin.getText().toString());
                if(cbSimpan.isChecked()) {

                    flagPin = "1";
                }else{
                    flagPin = "0";
                }

                pin = edtPin.getText().toString();
                savePin();
            }
        });

        alert.show();
    }

    private void savePin() {

        final ProgressDialog progressDialog = new ProgressDialog(context, R.style.AppTheme_Login_Dialog);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Menyimpan...");
        progressDialog.setCancelable(false);
        progressDialog.show();

        JSONObject jBody = new JSONObject();

        try {
            jBody.put("pin", pin);
            jBody.put("tipe", "2");
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

                        showCaraBayarDialog(pin);
                    }else{

                        View.OnClickListener clickListener = new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {

                                dialogBox.dismissDialog();
                                savePin();
                            }
                        };

                        dialogBox.showDialog(clickListener, "Ulangi Proses", message);

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

    private void showCaraBayarDialog(final String pin){

        final android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(context);
        LayoutInflater inflater = (LayoutInflater) ((Activity)context).getSystemService(LAYOUT_INFLATER_SERVICE);
        View viewDialog = inflater.inflate(R.layout.dialog_cara_bayar, null);
        builder.setView(viewDialog);
        builder.setCancelable(false);

        final RadioGroup rgCaraBayar = (RadioGroup) viewDialog.findViewById(R.id.rg_cara_bayar);
        final Button btnCancel = (Button) viewDialog.findViewById(R.id.btn_cancel);
        final Button btnProses = (Button) viewDialog.findViewById(R.id.btn_proses);

        final android.app.AlertDialog alert = builder.create();
        alert.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view2) {

                if(alert != null) alert.dismiss();
            }
        });

        btnProses.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view2) {

                if(alert != null) alert.dismiss();
                String crBayar = "2";

                if(rgCaraBayar.getCheckedRadioButtonId() == R.id.rb_sales){

                    crBayar = "2";
                }else if(rgCaraBayar.getCheckedRadioButtonId() == R.id.rb_transfer){

                    crBayar = "3";
                }else if(rgCaraBayar.getCheckedRadioButtonId() == R.id.rb_tcash){

                    dummyDialog();
                    return;
                }
                saveDeposit(pin, crBayar);
            }
        });

        alert.show();
    }

    private void dummyDialog() {

        final android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(context);
        LayoutInflater inflater = (LayoutInflater) ((Activity)context).getSystemService(LAYOUT_INFLATER_SERVICE);
        View viewDialog = inflater.inflate(R.layout.dialog_hasil_topup_tcash, null);
        builder.setView(viewDialog);
        builder.setCancelable(false);

        final Button btnOk = (Button) viewDialog.findViewById(R.id.btn_ok);


        final android.app.AlertDialog alert = builder.create();
        alert.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

        btnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view2) {

                if(alert != null) alert.dismiss();
            }
        });

        alert.show();
    }

    private void saveDeposit(String pin, final String crBayar) {

        final ProgressDialog progressDialog = new ProgressDialog(context, R.style.AppTheme_Login_Dialog);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Menyimpan...");
        progressDialog.setCancelable(false);
        progressDialog.show();

        final String nominal = edtNominal.getText().toString().replaceAll("[,.]", "");
        JSONObject jBody = new JSONObject();

        try {
            jBody.put("harga", nominal);
            jBody.put("nominal", nominal);
            jBody.put("pin", pin);
            jBody.put("crbayar", crBayar);
            jBody.put("nomor", session.getUsername());
        } catch (JSONException e) {
            e.printStackTrace();
        }

        ApiVolley request = new ApiVolley(context, jBody, "POST", ServerURL.beliSaldoTunai, new ApiVolley.VolleyCallback() {
            @Override
            public void onSuccess(String result) {

                if(progressDialog != null && progressDialog.isShowing()) progressDialog.dismiss();
                String message = "Terjadi kesalahan saat memuat data";

                try {

                    JSONObject response = new JSONObject(result);
                    String status = response.getJSONObject("metadata").getString("status");
                    message = response.getJSONObject("metadata").getString("message");
                    if(iv.parseNullInteger(status) == 200){

                        Toast.makeText(context, message, Toast.LENGTH_LONG).show();
                        if(crBayar.equals("3")){

                            JSONObject jo = response.getJSONObject("response");
                            String nominal = jo.getString("nominal");
                            String rekening = jo.getString("rekening");
                            String atasnama = jo.getString("atasnama");
                            String bank = jo.getString("bank");
                            String expiredDate = jo.getString("expired_at");
                            showResultDialog(nominal, rekening, bank, atasnama, expiredDate);
                        }else{
                            //Toast.makeText(context, message, Toast.LENGTH_LONG).show();
                            HomeActivity.stateFragment = 1;
                            onBackPressed();
                        }

                    }else{

                        if(!message.toLowerCase().contains("pin")){

                            DialogBox.showDialog(context, 3,message);
                        }else{
                            View.OnClickListener clickListener = new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {

                                    dialogBox.dismissDialog();
                                    showPinDialog();

                                }
                            };

                            dialogBox.showDialog(clickListener, "Ulangi Proses", message);
                        }

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

    private void showResultDialog(final String nominal, final String rekening, final String bank, final String an, final String expiration){

        final android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(context);
        LayoutInflater inflater = (LayoutInflater) ((Activity)context).getSystemService(LAYOUT_INFLATER_SERVICE);
        View viewDialog = inflater.inflate(R.layout.dialog_hasil_topup, null);
        builder.setView(viewDialog);
        builder.setCancelable(false);

        final RadioGroup rgCaraBayar = (RadioGroup) viewDialog.findViewById(R.id.rg_cara_bayar);
        final TextView tvNominal = (TextView) viewDialog.findViewById(R.id.tv_nominal);
        final TextView tvRekening = (TextView) viewDialog.findViewById(R.id.tv_rekening);
        final TextView tvBank = (TextView) viewDialog.findViewById(R.id.tv_bank);
        final TextView tvAn = (TextView) viewDialog.findViewById(R.id.tv_nama);
        final TextView tvExpiration = (TextView) viewDialog.findViewById(R.id.tv_expiration);
        final ImageView ivNominal = (ImageView) viewDialog.findViewById(R.id.iv_nominal);
        final ImageView ivRekening = (ImageView) viewDialog.findViewById(R.id.iv_rekening);
        final Button btnOk = (Button) viewDialog.findViewById(R.id.btn_ok);

        final android.app.AlertDialog alert = builder.create();
        alert.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

        tvNominal.setText(iv.ChangeToCurrencyFormat(nominal));
        tvRekening.setText(rekening);
        tvBank.setText(bank);
        tvAn.setText(an);
        tvExpiration.setText(Html.fromHtml("Harap lakukan transfer sebelum <b>"+ iv.ChangeFormatDateString(expiration, FormatItem.formatTimestamp, FormatItem.formatDateTimeDisplay) + "</b>"));

        ivNominal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                ClipboardManager clipboard = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
                ClipData clip = ClipData.newPlainText("nominal", tvNominal.getText().toString().replaceAll("[,.]", ""));
                clipboard.setPrimaryClip(clip);
                Toast.makeText(context, "Nominal disimpan di clipboard", Toast.LENGTH_LONG).show();
            }
        });

        ivRekening.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                ClipboardManager clipboard = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
                ClipData clip = ClipData.newPlainText("Rekening", tvRekening.getText().toString());
                clipboard.setPrimaryClip(clip);
                Toast.makeText(context, "Rekening disimpan di clipboard", Toast.LENGTH_LONG).show();
            }
        });

        btnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view2) {

                if(alert != null) alert.dismiss();
                HomeActivity.stateFragment = 1;
                onBackPressed();
            }
        });

        alert.show();
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