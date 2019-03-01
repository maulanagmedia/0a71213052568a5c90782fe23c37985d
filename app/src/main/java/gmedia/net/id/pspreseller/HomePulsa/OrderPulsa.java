package gmedia.net.id.pspreseller.HomePulsa;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Handler;
import android.provider.ContactsContract;
import android.provider.Settings;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
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
import gmedia.net.id.pspreseller.HomeInfoStok.ActInfoStok;
import gmedia.net.id.pspreseller.HomeInfoStok.DetailInfoStok;
import gmedia.net.id.pspreseller.HomePulsa.Adapter.AutocompleteAdapter;
import gmedia.net.id.pspreseller.HomePulsa.Adapter.BarangPulsaAdapter;
import gmedia.net.id.pspreseller.HomePulsa.Adapter.ListBalasanInjectAdapter;
import gmedia.net.id.pspreseller.HomePulsa.Service.ServiceHandler;
import gmedia.net.id.pspreseller.HomePulsa.Service.USSDService;
import gmedia.net.id.pspreseller.MainActivity;
import gmedia.net.id.pspreseller.R;
import gmedia.net.id.pspreseller.Utils.ServerURL;

public class OrderPulsa extends AppCompatActivity {

    private static String selectedHarga = "";
    private static String currentString = "";
    private static BarangPulsaAdapter adapterBarang;
    private static boolean isSaveButtonClicked = false;
    private static SessionManager session;
    private String TAG = "OrderPulsa";
    private static ItemValidation iv = new ItemValidation();
    private static Context context;
    private LinearLayout llNonota;
    private static LinearLayout llNominal;
    private EditText edtNonota;
    private static EditText edtNomor, edtHarga, edtNominal, edtAlamat, edtPin;
    private static AutoCompleteTextView edtNama;
    private Button btnProses, btnAppInfo;
    private ListView lvBalasan;
    private static ListView lvBarang;
    private ProgressBar pbProses;
    private List<CustomItem> listBalasan;
    private String nomor = "";
    private static String lastSN = "", lastCashback = "", lastSaldoAkhir = "";
    private static String lastSuccessBalasan = "";
    private static boolean isKonfirmasiManual = false;
    private boolean isProses = false;
    private static CustomItem selectedItemOrder;
    private static String lastFlagOrder = "";
    private static String lastKodebrg = "";
    private static String flagOrder = "";
    private static ListBalasanInjectAdapter balasanAdapter;
    private static DialogBox dialogBox;
    private static List<CustomItem> listBarang;
    private static boolean isLoading = false;
    public static boolean isActive = false;
    private static String senderBalasan = "";
    private static CheckBox cbSimpan;
    private static String pin = "";
    private static String flagPin = "";
    private static String transactionID = "";
    private Timer timerAc;
    private boolean isTyping = false;
    private boolean isNamaSelected = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_pulsa);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(getResources().getDrawable(R.mipmap.ic_down));

        getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN
        );

        context = this;

        setTitle("Jual Pulsa");
        isActive = true;
        transactionID = iv.getCurrentDate(FormatItem.formatTimestamp2);

        initUI();
    }

    private void initUI() {

        llNonota = (LinearLayout) findViewById(R.id.ll_nonota);
        edtNonota = (EditText) findViewById(R.id.edt_nonota);
        edtNama = (AutoCompleteTextView) findViewById(R.id.edt_nama);
        edtAlamat = (EditText) findViewById(R.id.edt_alamat);
        edtNomor = (EditText) findViewById(R.id.edt_nomor);
        edtHarga = (EditText) findViewById(R.id.edt_harga);
        edtPin = (EditText) findViewById(R.id.edt_pin);
        btnProses = (Button) findViewById(R.id.btn_proses);
        llNominal = (LinearLayout) findViewById(R.id.ll_nominal);
        edtNominal = (EditText) findViewById(R.id.edt_nominal);
        lvBalasan = (ListView) findViewById(R.id.lv_balasan);
        lvBarang = (ListView) findViewById(R.id.lv_barang);
        pbProses = (ProgressBar) findViewById(R.id.pb_proses);
        btnAppInfo = (Button) findViewById(R.id.btn_app_info);
        cbSimpan = (CheckBox) findViewById(R.id.cb_simpan);

        session = new SessionManager(context);
        listBalasan = new ArrayList<>();
        nomor = "";
        lastSN = "";
        lastCashback = "";
        lastSaldoAkhir = "";
        lastSuccessBalasan = "";
        isProses = false;
        isKonfirmasiManual = false;
        dialogBox = new DialogBox(context);
        isSaveButtonClicked = false;
        isLoading = false;
        session = new SessionManager(context);

        initEvent();

        getBarang();

        //getContacts(HomeActivity.listContact);
    }

    public void getContacts(List<CustomItem> items) {

        if(items != null && items.size() > 0){

            List<CustomItem> masterContact = new ArrayList<>(items);
            final AutocompleteAdapter adapter = new AutocompleteAdapter(context, masterContact);
            edtNama.setAdapter(adapter);

            edtNama.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                    CustomItem item = (CustomItem) adapter.getItem(i);

                    isNamaSelected = true;
                    edtNama.setText(item.getItem2());
                    edtAlamat.setText(item.getItem4());
                    if(item.getItem2().length() > 0) edtNama.setSelection(item.getItem2().length());
                    String nomor = item.getItem3();
                    nomor = nomor.replace("+62", "0");
                    nomor = nomor.replace(" ", "");
                    nomor = nomor.replace("-", "");
                    nomor = nomor.trim();
                    edtNomor.setText(nomor);
                }
            });

            edtNama.showDropDown();
        }
    }

    private static void getBarang() {

        dialogBox.showDialog(false);

        ApiVolley request = new ApiVolley(context, new JSONObject(), "GET", ServerURL.getBarangDS, new ApiVolley.VolleyCallback() {
            @Override
            public void onSuccess(String result) {

                try {
                    dialogBox.dismissDialog();
                }catch (Exception e){
                    e.printStackTrace();
                }

                try {

                    JSONObject response = new JSONObject(result);
                    String status = response.getJSONObject("metadata").getString("status");
                    listBarang = new ArrayList<>();

                    if(iv.parseNullInteger(status) == 200){

                        JSONArray items = response.getJSONArray("response");
                        for(int i  = 0; i < items.length(); i++){

                            JSONObject jo = items.getJSONObject(i);
                            listBarang.add(new CustomItem(
                                    jo.getString("kodebrg"),
                                    jo.getString("namabrg"),
                                    jo.getString("hargajual"),
                                    "0",
                                    jo.getString("flag"),
                                    jo.getString("mkios"),
                                    jo.getString("format"),
                                    jo.getString("balasan")));
                            //1. kdbrg
                            //2. namabrg
                            //3. hargajual
                            //4. flag dipilih
                            //5. flag jenis order MK, BL, TC
                            //6. pin rs
                            //7. format inject
                            //8. balasan
                            //break;
                        }
                    }

                    setTableBarang(listBarang);

                } catch (JSONException e) {
                    e.printStackTrace();
                    showDialog(3, "Terjadi kesalahan saat mengambil data barang, harap ulangi");
                }

                getDataPin();
            }

            @Override
            public void onError(String result) {

                dialogBox.dismissDialog();
                showDialog(3, "Terjadi kesalahan saat memuat data barang, harap ulangi");
                getDataPin();
            }
        });
    }

    private static void setTableBarang(List<CustomItem> listBarang) {

        lvBarang.setAdapter(null);
        if(listBarang != null){

            adapterBarang = new BarangPulsaAdapter((Activity) context, listBarang);
            lvBarang.setAdapter(adapterBarang);
            //if(listBarang.size() > 0) setSelectedItem(listBarang.get(0));

            /*lvBarang.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                    adapterBarang.setSelected(i);
                }
            });*/
        }
    }

    private static void showDialog(int state, String message){

        if(state == 1){

            final AlertDialog.Builder builder = new AlertDialog.Builder(context);
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(LAYOUT_INFLATER_SERVICE);
            View viewDialog = inflater.inflate(R.layout.layout_success, null);
            builder.setView(viewDialog);
            builder.setCancelable(false);

            final TextView tvText1 = (TextView) viewDialog.findViewById(R.id.tv_text1);
            tvText1.setText(message);
            final Button btnOK = (Button) viewDialog.findViewById(R.id.btn_ok);

            final AlertDialog alert = builder.create();
            alert.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

            btnOK.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view2) {

                    if(alert != null)
                    {
                        try {
                            alert.dismiss();
                        }catch (Exception e){
                            e.printStackTrace();
                        }
                    }
                }
            });

            try {
                alert.show();
            }catch (Exception e){
                e.printStackTrace();
            }

        }else if(state == 2){ // failed
            final AlertDialog.Builder builder = new AlertDialog.Builder(context);
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(LAYOUT_INFLATER_SERVICE);
            View viewDialog = inflater.inflate(R.layout.layout_failed, null);
            builder.setView(viewDialog);
            builder.setCancelable(false);

            final TextView tvText1 = (TextView) viewDialog.findViewById(R.id.tv_text1);
            tvText1.setText(message);
            final Button btnOK = (Button) viewDialog.findViewById(R.id.btn_ok);

            final AlertDialog alert = builder.create();
            alert.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

            btnOK.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view2) {

                    if(alert != null) {

                        try {
                            alert.dismiss();
                        }catch (Exception e){
                            e.printStackTrace();
                        }
                    }
                }
            });

            try {
                alert.show();
            }catch (Exception e){
                e.printStackTrace();
            }

        }else if(state == 3){

            final AlertDialog.Builder builder = new AlertDialog.Builder(context);
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(LAYOUT_INFLATER_SERVICE);
            View viewDialog = inflater.inflate(R.layout.layout_warning, null);
            builder.setView(viewDialog);
            builder.setCancelable(false);

            final TextView tvText1 = (TextView) viewDialog.findViewById(R.id.tv_text1);
            tvText1.setText(message);
            final Button btnOK = (Button) viewDialog.findViewById(R.id.btn_ok);
            btnOK.setText("Ulangi Proses");

            final AlertDialog alert = builder.create();
            alert.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

            btnOK.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view2) {

                    if(alert != null) {

                        try {
                            alert.dismiss();
                        }catch (Exception e){
                            e.printStackTrace();
                        }
                    }
                    getBarang();
                }
            });

            try {
                alert.show();
            }catch (Exception e){
                e.printStackTrace();
            }

        }else if(state == 4){

            final AlertDialog.Builder builder = new AlertDialog.Builder(context);
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(LAYOUT_INFLATER_SERVICE);
            View viewDialog = inflater.inflate(R.layout.layout_warning, null);
            builder.setView(viewDialog);
            builder.setCancelable(false);

            final TextView tvText1 = (TextView) viewDialog.findViewById(R.id.tv_text1);
            tvText1.setText(message);
            final Button btnOK = (Button) viewDialog.findViewById(R.id.btn_ok);
            btnOK.setText("Ulangi Proses");

            final AlertDialog alert = builder.create();
            alert.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

            btnOK.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view2) {

                    if(isLoading){

                        Toast.makeText(context, "Harap tunggu hingga proses selesai atau konfirmasi manual", Toast.LENGTH_LONG).show();
                        return;
                    }
                    if(alert != null){
                        try {
                            alert.dismiss();
                        }catch (Exception e){
                            e.printStackTrace();
                        }
                    }
                    saveData();
                }
            });

            try {
                alert.show();
            }catch (Exception e){
                e.printStackTrace();
            }
        }else if(state == 5){

            final AlertDialog.Builder builder = new AlertDialog.Builder(context);
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(LAYOUT_INFLATER_SERVICE);
            View viewDialog = inflater.inflate(R.layout.layout_warning, null);
            builder.setView(viewDialog);
            builder.setCancelable(false);

            final TextView tvText1 = (TextView) viewDialog.findViewById(R.id.tv_text1);
            tvText1.setText(message);
            final Button btnOK = (Button) viewDialog.findViewById(R.id.btn_ok);
            btnOK.setText("Ulangi Proses");

            final AlertDialog alert = builder.create();
            alert.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

            btnOK.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view2) {

                    if(alert != null) {

                        try {
                            alert.dismiss();
                        }catch (Exception e){
                            e.printStackTrace();
                        }
                    }
                    logBalasan(senderBalasan, lastSuccessBalasan);
                }
            });

            try {
                alert.show();
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }

    public static void addTambahBalasan(final String sender, final String text){

        ((Activity) context).runOnUiThread(new Runnable() {
            @Override
            public void run() {

                if(session != null
                        && edtNomor != null
                        && edtNominal != null
                        && context != null
                        && selectedItemOrder != null
                        && !text.toLowerCase().equals("[ussd code running…]")
                        && !text.toLowerCase().equals("[phone]")
                        && !text.toLowerCase().equals("[detail inject pulsa]")
                        && !text.toLowerCase().equals("[]")
                        && !text.toLowerCase().equals("[clipboard]")){

                    try {

                        if(balasanAdapter != null){

                            CustomItem item = new CustomItem(iv.getCurrentDate(FormatItem.formatTime), text);
                            balasanAdapter.addData(item);
                        }

                        logBalasan(sender, text);
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    private static void logBalasan(final String sender, final String text) {

        if(text.toLowerCase().contains("berhasil")){
            lastSuccessBalasan = text;
        }

        JSONObject jBody = new JSONObject();
        try {
            jBody.put("sender", sender);
            jBody.put("balasan", text);
            jBody.put("flag_order", lastFlagOrder);
            jBody.put("nomor", edtNomor.getText().toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }

        ApiVolley request = new ApiVolley(context, jBody, "POST", ServerURL.saveReplyDS, new ApiVolley.VolleyCallback() {
            @Override
            public void onSuccess(String result) {

                try {

                    JSONObject response = new JSONObject(result);
                    String status = response.getJSONObject("metadata").getString("status");

                    if(iv.parseNullInteger(status) == 200){

                        String flag = response.getJSONObject("response").getString("flag");

                        if(flag.equals("1")){

                            String harga = response.getJSONObject("response").getString("harga");
                            String nomor = response.getJSONObject("response").getString("nomor");
                            lastSN = response.getJSONObject("response").getString("sn");
                            lastCashback = response.getJSONObject("response").getString("cashback");
                            lastSaldoAkhir = response.getJSONObject("response").getString("stok_akhir");
                            //edtNomor.setText(nomor);
                            selectedHarga = harga;
                            edtNominal.setText(harga);

                            if(!isKonfirmasiManual && isSaveButtonClicked){

                                if(isLoading){

                                    Toast.makeText(context, "Harap tunggu hingga proses selesai atau konfirmasi manual", Toast.LENGTH_LONG).show();
                                }else{
                                    saveData();
                                }

                            }
                        }
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                    if(text.toLowerCase().contains("berhasil")){
                        lastSuccessBalasan = text;
                        showDialog(5, "Laporan tidak masuk, harap tekan ulangi proses");
                    }
                }
            }

            @Override
            public void onError(String result) {

                if(text.toLowerCase().contains("berhasil")){
                    lastSuccessBalasan = text;
                    showDialog(5, "Laporan tidak masuk, harap tekan ulangi proses");
                }
            }
        });

    }

    private static void saveData() {

        isLoading = true;
        final ProgressDialog progressDialog = new ProgressDialog(context, R.style.AppTheme_Login_Dialog);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Menyimpan...");
        progressDialog.setCancelable(false);
        progressDialog.show();

        JSONObject jBody = new JSONObject();

        PackageInfo pInfo = null;
        String version = "";

        try {
            pInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        version = pInfo.versionName;

        //1. kdbrg
        //2. namabrg
        //3. hargajual
        //4. flag dipilih
        //5. flag jenis order MK, BL, TCj
        //6. pin rs
        //7. format inject
        //break;

        try {
            jBody.put("kodebrg", lastKodebrg);
            jBody.put("harga", selectedHarga);
            jBody.put("jumlah", "1");
            jBody.put("nama", edtNama.getText().toString());
            jBody.put("alamat", edtAlamat.getText().toString());
            jBody.put("nomor", edtNomor.getText().toString());
            jBody.put("sn", lastSN);
            jBody.put("cashback", lastCashback);
            jBody.put("stok_akhir", lastSaldoAkhir);
            jBody.put("transaction_id", transactionID);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        String url = ServerURL.saveOrderDS, method = "POST";

        ApiVolley request = new ApiVolley(context, jBody, method, url, new ApiVolley.VolleyCallback() {
            @Override
            public void onSuccess(String result) {

                isLoading = false;
                try {
                    progressDialog.dismiss();
                }catch (Exception e){
                    e.printStackTrace();
                }


                String superMessage = "Terjadi kesalahan saat menyimpan data, harap ulangi";
                try {

                    JSONObject response = new JSONObject(result);
                    String status = response.getJSONObject("metadata").getString("status");
                    superMessage = response.getJSONObject("metadata").getString("message");

                    if(iv.parseNullInteger(status) == 200){


                        Toast.makeText(context, superMessage, Toast.LENGTH_LONG).show();
                        /*Intent intent = new Intent(context, MainActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        ((Activity)context).startActivity(intent);
                        ((Activity)context).finish();*/
                        HomeActivity.stateFragment = 2;
                        ((Activity) context).onBackPressed();
                    }else{
                        //Toast.makeText(DetailOrderPulsa.this, superMessage, Toast.LENGTH_LONG).show();
                        showDialog(2,superMessage);
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(context, superMessage, Toast.LENGTH_LONG).show();
                    showDialog(4, "Laporan tidak masuk, harap tekan ulangi proses");
                }
            }

            @Override
            public void onError(String result) {

                isLoading = false;
                progressDialog.dismiss();
                //Toast.makeText(context, "Terjadi kesalahan saat menyimpan data, harap ulangi kembali", Toast.LENGTH_LONG).show();
                showDialog(4, "Laporan tidak masuk, harap tekan ulangi proses");
            }
        });
    }

    public static void setSelectedItem(CustomItem item){

        //1. kdbrg
        //2. namabrg
        //3. hargajual
        //4. flag dipilih
        //5. flag jenis order MK, BL, TC, BL2
        //6. pin rs

        selectedItemOrder = item;

        flagOrder = item.getItem5();

        if(iv.parseNullDouble(item.getItem3()) > 0){ // ada harganya
            llNominal.setVisibility(View.GONE);
            edtNominal.setText(selectedItemOrder.getItem3());
            edtHarga.setVisibility(View.VISIBLE);
            //hitungHarga();
        }else{
            //llNominal.setVisibility(View.VISIBLE);
            edtNominal.setText("1");
            edtHarga.setVisibility(View.GONE);
        }

        if(flagOrder.equals("BL2")){ // Bulk reguler

            edtNominal.setText("");
            llNominal.setVisibility(View.VISIBLE);
            edtHarga.setVisibility(View.VISIBLE);
        }else{
            llNominal.setVisibility(View.GONE);
        }
    }

    private void initEvent() {

        btnAppInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String packageName = context.getPackageName();

                try {
                    //Open the specific App Info page:
                    Intent intent = new Intent(android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                    intent.setData(Uri.parse("package:" + packageName));
                    startActivity(intent);

                } catch ( ActivityNotFoundException e ) {
                    //e.printStackTrace();

                    //Open the generic Apps page:
                    Intent intent = new Intent(android.provider.Settings.ACTION_MANAGE_APPLICATIONS_SETTINGS);
                    startActivity(intent);

                }
            }
        });

        btnProses.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //validasi

                if(edtNomor.getText().toString().length() == 0){

                    edtNomor.setError("Nomor harap diisi");
                    edtNomor.requestFocus();
                    return;
                }else{
                    edtNomor.setError(null);
                }

                if(edtPin.getText().toString().isEmpty()){

                    edtPin.setError("Pin chip harap diisi");
                    edtPin.requestFocus();
                    return;
                }else{
                    edtPin.setError(null);
                }

                if(selectedItemOrder == null){

                    Toast.makeText(context, "Harap pilih salah satu jenis paket/ denom", Toast.LENGTH_LONG).show();
                    return;
                }

                if(edtNominal.getText().toString().length() == 0){

                    if(edtNominal.getVisibility() == View.VISIBLE){
                        edtNominal.setError("Nominal harap diisi");
                        edtNominal.requestFocus();
                    }else{

                        Toast.makeText(context, "Harga tidak termuat, harap cek proses atau kolom nominal", Toast.LENGTH_LONG).show();
                    }
                    return;
                }else{
                    edtNominal.setError(null);
                }

                if(iv.parseNullDouble(selectedHarga) <= 0){

                    Toast.makeText(context, "Harga tidak termuat, harap cek proses atau kolom nominal", Toast.LENGTH_LONG).show();
                    return;
                }

                if(isProses){

                    Toast.makeText(context, "Harap tunggu hingga proses selesai", Toast.LENGTH_LONG).show();
                    return;
                }

                isKonfirmasiManual = false;
                lastFlagOrder = selectedItemOrder.getItem5();
                lastKodebrg = selectedItemOrder.getItem1();

                AlertDialog dialog = new AlertDialog.Builder(context)
                        .setTitle("Konfirmasi")
                        .setIcon(R.mipmap.ic_launcher)
                        .setMessage("Apakah anda yakin ingin memproses order?")
                        .setPositiveButton("Ya", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {

                                isSaveButtonClicked = true;
                                showDialogLoading();
                                String format = selectedItemOrder.getItem7().replace("[tujuan]",edtNomor.getText().toString());
                                if(flagOrder.equals("BL2")){
                                    format = format.replace("[nominal]", selectedHarga.substring(0, selectedHarga.length() - 3));
                                }else{
                                    format = format.replace("[nominal]", selectedHarga);
                                }

                                format = format.replace("[pin]", edtPin.getText().toString());
                                format = format.replace("#", Uri.encode("#"));

                                Log.d(TAG, "onClick: " + format);

                                //String code = "*123" + Uri.encode("#");

                                if(cbSimpan.isChecked()){

                                    flagPin = "1";
                                }else{
                                    flagPin = "0";
                                }
                                pin = edtPin.getText().toString();
                                savePin(format);
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

        edtNominal.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {

                hitungHarga();
            }
        });

        edtNama.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                edtNama.setAdapter(null);
                if (timerAc != null) {
                    timerAc.cancel();
                }
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(final Editable editable) {

                if(editable.length() > 0){

                    if(isNamaSelected){
                        isNamaSelected = false;
                    }else{

                        timerAc = new Timer();
                        timerAc.schedule(new TimerTask() {
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

                                        getDataPhonebook(editable.toString());
                                    }
                                });
                            }
                        }, 600);
                    }
                }else{

                    isTyping = false;
                }
            }
        });
    }

    private void getDataPhonebook(final String keyword) {

        JSONObject jBody = new JSONObject();
        try {
            jBody.put("keyword", keyword);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        final ApiVolley request = new ApiVolley(context, jBody, "POST", ServerURL.getPhonebook, new ApiVolley.VolleyCallback() {
            @Override
            public void onSuccess(String result) {

                isTyping = false;
                try {

                    JSONObject response = new JSONObject(result);
                    String status = response.getJSONObject("metadata").getString("status");
                    String message = response.getJSONObject("metadata").getString("message");
                    List<CustomItem> newContact = new ArrayList<>();

                    if(status.equals("200")){
                        JSONArray items = response.getJSONArray("response");
                        for(int i  = 0; i < items.length(); i++){

                            JSONObject jo = items.getJSONObject(i);
                            newContact.add(new CustomItem(String.valueOf(i),
                                    jo.getString("nama"),
                                    jo.getString("nomor"),
                                    jo.getString("alamat")));
                        }

                    }

                    List<CustomItem> newAddedContact = new ArrayList<>();
                    if(HomeActivity.listContact != null){

                        for(int i = 0; i < HomeActivity.listContact.size();i++){

                            CustomItem item = HomeActivity.listContact.get(i);
                            if(item.getItem2().toLowerCase().contains(keyword.toLowerCase())){

                                newAddedContact.add(item);
                            }
                        }
                    }

                    newContact.addAll(newAddedContact);
                    getContacts(newContact);
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

    private static void showDialogLoading(){

        final AlertDialog.Builder builder = new AlertDialog.Builder(context);
        LayoutInflater inflater = (LayoutInflater) ((Activity)context).getSystemService(LAYOUT_INFLATER_SERVICE);
        View viewDialog = inflater.inflate(R.layout.layout_loading_ds, null);
        builder.setView(viewDialog);
        builder.setCancelable(false);

        final TextView tvText1 = (TextView) viewDialog.findViewById(R.id.tv_text1);
        final Button btnOK = (Button) viewDialog.findViewById(R.id.btn_ok);
        final Button btnKonfirmasi = (Button) viewDialog.findViewById(R.id.btn_konfirmasi);

        final AlertDialog alert = builder.create();
        alert.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

        btnOK.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view2) {

                if(alert != null) {

                    try {
                        alert.dismiss();
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }

                AlertDialog alertDialog = new AlertDialog.Builder(context)
                        .setIcon(R.mipmap.ic_launcher)
                        .setTitle("Konfirmasi")
                        .setMessage("Apakah anda yakin ingin membatalkan proses?")
                        .setPositiveButton("Ya", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                ((Activity) context).onBackPressed();
                            }
                        })
                        .setCancelable(false)
                        .setNegativeButton("Tidak", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {

                                showDialogLoading();
                            }
                        })
                        .show();
            }
        });

        btnKonfirmasi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                showKonfirmasiDialog();
            }
        });

        alert.show();
    }

    private static void showKonfirmasiDialog(){

        final AlertDialog.Builder builder = new AlertDialog.Builder(context);
        LayoutInflater inflater = (LayoutInflater) ((Activity)context).getSystemService(LAYOUT_INFLATER_SERVICE);
        View viewDialog = inflater.inflate(R.layout.layout_konfirmasi, null);
        builder.setView(viewDialog);
        builder.setCancelable(false);

        final TextView tvText1 = (TextView) viewDialog.findViewById(R.id.tv_text1);
        final EditText edtNominal = (EditText) viewDialog.findViewById(R.id.edt_nomimal);
        final Button btnOK = (Button) viewDialog.findViewById(R.id.btn_ok);
        final Button btnProses = (Button) viewDialog.findViewById(R.id.btn_proses);

        currentString = "";

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
                    edtNominal.addTextChangedListener(this);
                }
            }
        });

        final AlertDialog alert = builder.create();
        alert.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

        btnOK.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view2) {

                isKonfirmasiManual = false;
                if(alert != null){

                    try {
                        alert.dismiss();
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }

            }
        });

        btnProses.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                final String nominal = edtNominal.getText().toString().replaceAll("[,.]", "");
                if(nominal.isEmpty()){

                    edtNominal.setError("Nominal harap diisi");
                    edtNominal.requestFocus();
                    return;
                }else if(iv.parseNullDouble(nominal) == 0){

                    edtNominal.setError("Nominal harap lebih dari 0");
                    edtNominal.requestFocus();
                    return;
                }else{
                    edtNominal.setError(null);
                }

                isKonfirmasiManual = true;
                AlertDialog alertDialog = new AlertDialog.Builder(context)
                        .setIcon(R.mipmap.ic_launcher)
                        .setTitle("Konfirmasi")
                        .setMessage("Apakah anda yakin ingin proses order dengan nominal "+ iv.ChangeToCurrencyFormat(nominal)+" ?")
                        .setPositiveButton("Ya", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {

                                lastSN = "";
                                selectedHarga = nominal;
                                edtNominal.setText(nominal);
                                if(isLoading){

                                    Toast.makeText(context, "Harap tunggu hingga proses selesai", Toast.LENGTH_LONG).show();
                                    return;
                                }

                                saveData();
                            }
                        })
                        .setCancelable(false)
                        .setNegativeButton("Tidak", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {

                                return;
                            }
                        })
                        .show();
            }
        });

        alert.show();
    }

    private void savePin(final String format) {

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

                        lvBalasan.setAdapter(null);
                        listBalasan  = new ArrayList<>();
                        balasanAdapter = new ListBalasanInjectAdapter((Activity) context, listBalasan);
                        lvBalasan.setAdapter(balasanAdapter);
                        startActivity(new Intent("android.intent.action.CALL", Uri.parse("tel:" + format)));
                    }else{

                        View.OnClickListener clickListener = new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {

                                dialogBox.dismissDialog();
                                savePin(format);
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

    private static void hitungHarga(){

        selectedHarga = edtNominal.getText().toString();
        edtHarga.setText(iv.ChangeToRupiahFormat(selectedHarga));
    }

    private static void getDataPin() {

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

                        if(flagPin.equals("1")){

                            edtPin.setText(pin);
                            cbSimpan.setChecked(true);
                        }
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

    @Override
    protected void onResume() {
        super.onResume();

        isActive = true;
        DetailInfoStok.isActive = false;

        boolean isAccessGranted =  isAccessibilityEnabled(context.getPackageName() + "/" + context.getPackageName() + ".HomePulsa.Service.USSDService");
        if(isAccessGranted){

            //Log.d(TAG, "granted");
        }else{
            //Log.d(TAG, "not granted");
            Snackbar.make(findViewById(android.R.id.content), "Mohon ijinkan accessibility pada "+ getResources().getString(R.string.app_name)+", Cari "+ getResources().getString(R.string.app_name)+" dan ubah enable",
                    Snackbar.LENGTH_INDEFINITE).setAction("OK",
                    new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            startActivity(new Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS));
                        }
                    }).show();

        }

        ServiceHandler serviceHandler = new ServiceHandler(OrderPulsa.this);

        if(!checkNotificationEnabled()){

            Toast.makeText(context, "Harap ijinkan aksesbilitas untuk menunjang berjalannya sistem", Toast.LENGTH_LONG).show();
            Intent intent = new Intent(
                    "android.settings.ACTION_NOTIFICATION_LISTENER_SETTINGS");
            startActivity(intent);
        }
    }

    public boolean checkNotificationEnabled() {
        try{
            if(Settings.Secure.getString(getContentResolver(),
                    "enabled_notification_listeners").contains(context.getPackageName()))
            {
                return true;
            } else {
                return false;
            }

        }catch(Exception e) {
            e.printStackTrace();
        }
        return false;
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
        isSaveButtonClicked = false;
    }

    @Override
    protected void onDestroy() {
        isSaveButtonClicked = false;
        isActive = false;
        super.onDestroy();
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
}
