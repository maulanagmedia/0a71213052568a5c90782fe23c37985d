package gmedia.net.id.pspreseller.HomeMkios;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.drawable.ColorDrawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.maulana.custommodul.ApiVolley;
import com.maulana.custommodul.CustomItem;
import com.maulana.custommodul.CustomView.DialogBox;
import com.maulana.custommodul.ItemValidation;
import com.maulana.custommodul.OptionItem;
import com.maulana.custommodul.SessionManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import gmedia.net.id.pspreseller.HomeActivity;
import gmedia.net.id.pspreseller.HomeMkios.Adapter.ListDenomMkiosAdapter;
import gmedia.net.id.pspreseller.R;
import gmedia.net.id.pspreseller.Utils.ServerURL;

public class OrderMKIOS extends AppCompatActivity {

    private static List<CustomItem> items;
    private static double totalHarga = 0;
    private Spinner spNoRS;
    private Button btnProses;
    private static ItemValidation iv = new ItemValidation();
    private List<OptionItem> listNoRS;
    private List<CustomItem> listDenom;
    private Context context;
    private ListView lvDenom;
    private DialogBox dialogBox;
    private String selectedNomor = "";
    private static ListDenomMkiosAdapter adapterDenom;
    private static TextView tvJumlahHarga;
    private SessionManager session;
    private TextView tvNomor;
    private String pin = "", flagPin = "";
    private TextView tvDenom5, tvDenom10, tvDenom20, tvDenom25, tvDenom50, tvDenom100, tvHarga5, tvHarga10, tvHarga20, tvHarga25, tvHarga50, tvHarga100;
    private EditText edtJumlah5, edtJumlah10, edtJumlah20, edtJumlah25, edtJumlah50, edtJumlah100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_mkios);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(getResources().getDrawable(R.mipmap.ic_down));

        getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN
        );

        context = this;
        session = new SessionManager(context);

        setTitle("Beli Stok MKIOS");

        initUI();
    }

    private void initUI() {

        spNoRS = (Spinner) findViewById(R.id.sp_no_rs);
        lvDenom = (ListView) findViewById(R.id.lv_denom);
        lvDenom.setItemsCanFocus(true);
        btnProses = (Button) findViewById(R.id.btn_proses);
        tvJumlahHarga = (TextView) findViewById(R.id.tv_jumlah_harga);
        tvNomor = (TextView) findViewById(R.id.tv_nomor);
        tvNomor.setText(session.getUsername());

        tvDenom5 = (TextView) findViewById(R.id.tv_denom5);
        tvDenom10 = (TextView) findViewById(R.id.tv_denom10);
        tvDenom20 = (TextView) findViewById(R.id.tv_denom20);
        tvDenom25 = (TextView) findViewById(R.id.tv_denom25);
        tvDenom50 = (TextView) findViewById(R.id.tv_denom50);
        tvDenom100 = (TextView) findViewById(R.id.tv_denom100);

        tvHarga5 = (TextView) findViewById(R.id.tv_harga5);
        tvHarga10 = (TextView) findViewById(R.id.tv_harga10);
        tvHarga20 = (TextView) findViewById(R.id.tv_harga20);
        tvHarga25 = (TextView) findViewById(R.id.tv_harga25);
        tvHarga50 = (TextView) findViewById(R.id.tv_harga50);
        tvHarga100 = (TextView) findViewById(R.id.tv_harga100);

        edtJumlah5 = (EditText) findViewById(R.id.edt_jumlah5);
        edtJumlah10 = (EditText) findViewById(R.id.edt_jumlah10);
        edtJumlah20 = (EditText) findViewById(R.id.edt_jumlah20);
        edtJumlah25 = (EditText) findViewById(R.id.edt_jumlah25);
        edtJumlah50 = (EditText) findViewById(R.id.edt_jumlah50);
        edtJumlah100 = (EditText) findViewById(R.id.edt_jumlah100);

        items = new ArrayList<>();
        listDenom = new ArrayList<>();

        dialogBox = new DialogBox(context);
        //getNoRs();

        getDataDenom();

        initEvent();
    }

    private void initEvent() {

        btnProses.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // validasi
                if(totalHarga <= 0){
                    DialogBox.showDialog(context, 3, "Harap pilih denom terlebih dahulu");
                    return;
                }

                if(listDenom == null || listDenom.size() <= 0){
                    DialogBox.showDialog(context, 3, "Harap pilih denom terlebih dahulu");
                    return;
                }

                AlertDialog dialog = new AlertDialog.Builder(context)
                        .setIcon(R.mipmap.ic_launcher)
                        .setTitle("Konfirmasi")
                        .setMessage("Anda yakin ingin menyimpan data?")
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

        edtJumlah5.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {

                updateHarga();
            }
        });

        edtJumlah10.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {

                updateHarga();
            }
        });

        edtJumlah20.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {

                updateHarga();
            }
        });

        edtJumlah25.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {

                updateHarga();
            }
        });

        edtJumlah50.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {

                updateHarga();
            }
        });

        edtJumlah100.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {

                updateHarga();
            }
        });
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

                        saveMkios(pin);
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

    private void saveMkios(String pin) {

        final ProgressDialog progressDialog = new ProgressDialog(context, R.style.AppTheme_Login_Dialog);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Menyimpan...");
        progressDialog.setCancelable(false);
        progressDialog.show();

        JSONArray jArrayBarang = new JSONArray();

        //MKIOS
        if(listDenom != null && listDenom.size() > 0){


            for(CustomItem item : listDenom){

                if(iv.parseNullDouble(item.getItem4()) > 0){

                    JSONObject jDenom = new JSONObject();
                    try {
                        jDenom.put("kode", item.getItem1());
                        jDenom.put("jumlah", item.getItem4());
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    jArrayBarang.put(jDenom);
                }
            }
        }

        JSONObject jBody = new JSONObject();

        try {
            jBody.put("barang", jArrayBarang);
            jBody.put("pin", pin);
            jBody.put("nomor", session.getUsername());
        } catch (JSONException e) {
            e.printStackTrace();
        }

        ApiVolley request = new ApiVolley(context, jBody, "POST", ServerURL.beliMkios, new ApiVolley.VolleyCallback() {
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
                        HomeActivity.stateFragment = 1;
                        onBackPressed();
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

    private void getNoRs() {

        dialogBox.showDialog(false);
        final ApiVolley request = new ApiVolley(context, new JSONObject(), "GET", ServerURL.getNomor, new ApiVolley.VolleyCallback() {
            @Override
            public void onSuccess(String result) {

                dialogBox.dismissDialog();
                try {
                    JSONObject response = new JSONObject(result);
                    String status = response.getJSONObject("metadata").getString("status");
                    String message = response.getJSONObject("metadata").getString("message");
                    listNoRS = new ArrayList<>();
                    if(status.equals("200")){

                        JSONArray jsonArray = response.getJSONArray("response");
                        for(int i = 0; i < jsonArray.length(); i ++){
                            JSONObject jo = jsonArray.getJSONObject(i);
                            listNoRS.add(new OptionItem(String.valueOf(i+1), jo.getString("nomor")));
                        }

                        setNoRSAdapter(listNoRS);

                    }else{
                        Toast.makeText(context, message, Toast.LENGTH_LONG).show();
                        setNoRSAdapter(null);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    setNoRSAdapter(null);

                    View.OnClickListener clickListener = new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {

                            dialogBox.dismissDialog();
                            getNoRs();

                        }
                    };

                    dialogBox.showDialog(clickListener, "Ulangi Proses", "Terjadi kesalahan saat mengambil data");
                }
            }

            @Override
            public void onError(String result) {
                setNoRSAdapter(null);
                dialogBox.dismissDialog();

                View.OnClickListener clickListener = new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        dialogBox.dismissDialog();
                        getNoRs();

                    }
                };

                dialogBox.showDialog(clickListener, "Ulangi Proses", "Terjadi kesalahan saat mengambil data");
            }
        });
    }

    private void setNoRSAdapter(List<OptionItem> listItem) {

        if(listItem != null && listItem.size() > 0){

            ArrayAdapter adapter = new ArrayAdapter(OrderMKIOS.this, R.layout.layout_simple_list, listItem);
            spNoRS.setAdapter(adapter);

            spNoRS.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {


                    OptionItem selectedItem = (OptionItem) parent.getItemAtPosition(position);
                    selectedNomor = selectedItem.getText();

                    getDataDenom();
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }
            });

            spNoRS.setSelection(0);
        }
    }

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
                            listDenom.add(new CustomItem(
                                    jo.getString("kodebrg"),
                                    jo.getString("namabrg"),
                                    jo.getString("hargajual"),
                                    "",
                                    jo.getString("flag")));

                            if(i == 0){

                                tvDenom5.setText(jo.getString("namabrg"));
                                tvHarga5.setText("Harga " + iv.ChangeToRupiahFormat(jo.getString("hargajual")));
                                if(jo.getString("flag").equals("1")){
                                    edtJumlah5.setEnabled(true);
                                }else{
                                    edtJumlah5.setEnabled(false);
                                }

                            }else if(i == 1){

                                tvDenom10.setText(jo.getString("namabrg"));
                                tvHarga10.setText("Harga " + iv.ChangeToRupiahFormat(jo.getString("hargajual")));
                                if(jo.getString("flag").equals("1")){
                                    edtJumlah10.setEnabled(true);
                                }else{
                                    edtJumlah10.setEnabled(false);
                                }
                            }else if(i == 2){

                                tvDenom20.setText(jo.getString("namabrg"));
                                tvHarga20.setText("Harga " + iv.ChangeToRupiahFormat(jo.getString("hargajual")));
                                if(jo.getString("flag").equals("1")){
                                    edtJumlah20.setEnabled(true);
                                }else{
                                    edtJumlah20.setEnabled(false);
                                }
                            }else if(i == 3){

                                tvDenom25.setText(jo.getString("namabrg"));
                                tvHarga25.setText("Harga " + iv.ChangeToRupiahFormat(jo.getString("hargajual")));
                                if(jo.getString("flag").equals("1")){
                                    edtJumlah25.setEnabled(true);
                                }else{
                                    edtJumlah25.setEnabled(false);
                                }
                            }else if(i == 4){

                                tvDenom50.setText(jo.getString("namabrg"));
                                tvHarga50.setText("Harga " + iv.ChangeToRupiahFormat(jo.getString("hargajual")));
                                if(jo.getString("flag").equals("1")){
                                    edtJumlah50.setEnabled(true);
                                }else{
                                    edtJumlah50.setEnabled(false);
                                }
                            }else if(i == 5){

                                tvDenom100.setText(jo.getString("namabrg"));
                                tvHarga100.setText("Harga " + iv.ChangeToRupiahFormat(jo.getString("hargajual")));
                                if(jo.getString("flag").equals("1")){
                                    edtJumlah100.setEnabled(true);
                                }else{
                                    edtJumlah100.setEnabled(false);
                                }
                            }
                        }
                    }

                    //setDenomAdapter(listDenom);
                } catch (JSONException e) {
                    e.printStackTrace();

                    //setDenomAdapter(null);
                    View.OnClickListener clickListener = new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {

                            dialogBox.dismissDialog();
                            getDataDenom();

                        }
                    };

                    dialogBox.showDialog(clickListener, "Ulangi Proses", "Terjadi kesalahan saat mengambil data");
                }

                getDataPin();
            }

            @Override
            public void onError(String result) {

                //setDenomAdapter(null);
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

    private void updateHarga(){

        String jml5 = edtJumlah5.getText().toString();
        String jml10 = edtJumlah10.getText().toString();
        String jml20 = edtJumlah20.getText().toString();
        String jml25 = edtJumlah25.getText().toString();
        String jml50 = edtJumlah50.getText().toString();
        String jml100 = edtJumlah100.getText().toString();

        double qty5 = iv.parseNullDouble(jml5);
        double qty10 = iv.parseNullDouble(jml10);
        double qty20 = iv.parseNullDouble(jml20);
        double qty25 = iv.parseNullDouble(jml25);
        double qty50 = iv.parseNullDouble(jml50);
        double qty100 = iv.parseNullDouble(jml100);

        totalHarga = 0;

        if(listDenom != null && listDenom.size() > 0){

            int x = 0;
            for(CustomItem item : listDenom){

                if(x == 0){
                    item.setItem4(jml5);
                }else if(x == 1){

                    item.setItem4(jml10);
                }else if(x == 2){

                    item.setItem4(jml20);
                }else if(x == 3){

                    item.setItem4(jml25);
                }else if(x == 4){

                    item.setItem4(jml50);
                }else if(x == 5){

                    item.setItem4(jml100);
                }

                totalHarga += (iv.parseNullDouble(item.getItem3()) * iv.parseNullDouble(item.getItem4()));
                x++;
            }
        }

        tvJumlahHarga.setText(iv.ChangeToRupiahFormat(totalHarga));
    }

    private void setDenomAdapter(List<CustomItem> listItem) {

        lvDenom.setAdapter(null);
        if(listItem != null && listItem.size() > 0){

            adapterDenom = new ListDenomMkiosAdapter(OrderMKIOS.this, listItem);
            lvDenom.setAdapter(adapterDenom);
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
        overridePendingTransition(R.anim.slide_in_down,R.anim.slide_out_down);
    }
}
