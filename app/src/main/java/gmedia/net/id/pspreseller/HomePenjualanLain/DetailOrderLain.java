package gmedia.net.id.pspreseller.HomePenjualanLain;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.leonardus.irfan.bluetoothprinter.Model.Item;
import com.leonardus.irfan.bluetoothprinter.Model.Transaksi;
import com.leonardus.irfan.bluetoothprinter.PspPrinter;
import com.maulana.custommodul.ApiVolley;
import com.maulana.custommodul.CustomView.DialogBox;
import com.maulana.custommodul.ItemValidation;
import com.maulana.custommodul.OptionItem;
import com.maulana.custommodul.SessionManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import gmedia.net.id.pspreseller.HomeActivity;
import gmedia.net.id.pspreseller.R;
import gmedia.net.id.pspreseller.Utils.ServerURL;

public class DetailOrderLain extends AppCompatActivity {

    private Context context;
    private SessionManager session;
    private ItemValidation iv = new ItemValidation();
    private DialogBox dialogBox;
    private EditText edtJenis, edtNomor, edtMsisdn, edtNama, edtTotal, edtSN;
    private Button btnProses;
    private String idKategori = "", nama = "";
    private Spinner spnNamaProduk;
    private List<OptionItem> listProduk = new ArrayList<>();
    private ArrayAdapter adapterProduk;
    private PspPrinter printer;
    private LinearLayout llFooter, llHarga;
    private Button btnHarga;
    private boolean isInquery = false;
    private int state = 1;
    private String idProduk = "";
    private String currentCounter = "";
    private String harga = "", namaPIC = "", msisdn = "", sn = "", namaProduk = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_order_lain);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN
        );

        setTitle("Detail Penjualan");
        context = this;
        dialogBox = new DialogBox(context);
        printer = new PspPrinter(context);
        printer.startService();
        initUI();
        initEvent();
        initData();
    }

    private void initUI() {

        edtJenis = (EditText) findViewById(R.id.edt_jenis);
        edtNomor = (EditText) findViewById(R.id.edt_nomor);
        spnNamaProduk = (Spinner) findViewById(R.id.spn_nama_produk);
        btnProses = (Button) findViewById(R.id.btn_proses);
        llFooter = (LinearLayout) findViewById(R.id.ll_footer);
        llHarga = (LinearLayout) findViewById(R.id.ll_harga);
        btnHarga = (Button) findViewById(R.id.btn_harga);
        edtMsisdn = (EditText) findViewById(R.id.edt_msisdn);
        edtNama = (EditText) findViewById(R.id.edt_nama);
        edtSN = (EditText) findViewById(R.id.edt_sn);
        edtTotal = (EditText) findViewById(R.id.edt_total);

        isInquery = false;
        state = 1;
        session = new SessionManager(context);

        setProdukAdapter();

        Bundle bundle = getIntent().getExtras();
        if(bundle != null){

            idKategori = bundle.getString("kategori", "");
            nama = bundle.getString("nama", "");
            edtJenis.setText(nama);
        }
    }

    private void initEvent() {

        btnProses.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(edtNomor.getText().toString().isEmpty()){

                    edtNomor.setError("Harap diisi");
                    edtNomor.requestFocus();
                    return;
                }else{

                    edtNomor.setError(null);
                }

                if(state == 1 && isInquery){

                    Toast.makeText(context, "Harap tekan Hitung Harga terlebih dahulu", Toast.LENGTH_LONG).show();
                    return;
                }

                currentCounter = "";
                doTransaksi(false);
                /*List<Item> items = new ArrayList<>();
                items.add(new Item("Item 1", 1, 20000));
                items.add(new Item("Item 2", 1, 21000));
                items.add(new Item("Item 3", 1, 19000));
                Calendar date = Calendar.getInstance();

                final Transaksi transaksi = new Transaksi("Salam cell", "Irvan", "PB/KS/1811/0030", date.getTime(), items);

                AlertDialog dialog = new AlertDialog.Builder(context)
                        .setTitle("Konfirmasi")
                        .setMessage("Transaksi Berhasil")
                        .setPositiveButton("Cetak Transaksi", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {

                                if(printer.isPrinterReady()){

                                    printer.print(transaksi);
                                }else {

                                    Toast.makeText(context, "Harap pilih device printer telebih dahulu", Toast.LENGTH_LONG).show();
                                    printer.showDevices();
                                }
                            }
                        })
                        .setNegativeButton("Tidak", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {

                            }
                        })
                        .show();*/
            }
        });

        btnHarga.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(edtNomor.getText().toString().isEmpty()){

                    edtNomor.setError("Harap diisi");
                    edtNomor.requestFocus();
                    return;
                }else{

                    edtNomor.setError(null);
                }

                state = 2;
                currentCounter = "";
                doTransaksi(true);
            }
        });
    }

    private void doTransaksi(final boolean checkHarga) {

        final ProgressDialog progressDialog = new ProgressDialog(context, R.style.AppTheme_Login_Dialog);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Memproses...");
        progressDialog.setCancelable(false);
        progressDialog.show();

        JSONObject jBody = new JSONObject();
        String proses = isInquery && checkHarga ? "INQ" : "PAY";

        try {
            jBody.put("id_produk", idProduk);
            jBody.put("nomor", edtNomor.getText().toString());
            jBody.put("proses", proses);
            jBody.put("konter", currentCounter);
            jBody.put("pay", isInquery ? harga : "");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        ApiVolley request = new ApiVolley(context, jBody, "POST", ServerURL.payPPBOB, new ApiVolley.VolleyCallback() {
            @Override
            public void onSuccess(String result) {

                if(progressDialog != null && progressDialog.isShowing()) progressDialog.dismiss();
                String message = "Terjadi kesalahan saat memuat data";
                harga = "";
                sn = "";
                namaPIC = "";
                msisdn = "";

                try {

                    JSONObject response = new JSONObject(result);
                    String status = response.getJSONObject("metadata").getString("status");
                    message = response.getJSONObject("metadata").getString("message");
                    currentCounter = response.getJSONObject("response").getString("counter");

                    if(iv.parseNullInteger(status) == 200){

                        harga =  currentCounter = response.getJSONObject("response").getJSONObject("transaksi").getString("harga");
                        sn = currentCounter = response.getJSONObject("response").getJSONObject("transaksi").getString("sn");
                        msisdn = currentCounter = response.getJSONObject("response").getJSONObject("transaksi").getString("msisdn");
                        namaPIC= currentCounter = response.getJSONObject("response").getJSONObject("transaksi").getString("nama");

                        edtTotal.setText(iv.ChangeToCurrencyFormat(harga));
                        edtNama.setText(namaPIC);
                        edtSN.setText(sn);
                        edtMsisdn.setText(msisdn);

                        if(!checkHarga){

                            final AlertDialog.Builder builder = new AlertDialog.Builder(context);
                            LayoutInflater inflater = (LayoutInflater) context.getSystemService(LAYOUT_INFLATER_SERVICE);
                            View viewDialog = inflater.inflate(R.layout.dialog_cetak, null);
                            builder.setView(viewDialog);
                            builder.setCancelable(false);

                            final Button btnTutup = (Button) viewDialog.findViewById(R.id.btn_tutup);
                            final Button btnCetak = (Button) viewDialog.findViewById(R.id.btn_cetak);

                            final AlertDialog alert = builder.create();
                            alert.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

                            List<Item> items = new ArrayList<>();

                            items.add(new Item(namaProduk, 1, iv.parseNullDouble(harga)));

                            Calendar date = Calendar.getInstance();
                            final Transaksi transaksi = new Transaksi(namaPIC, session.getNama(), sn, date.getTime(), items);

                            final String finalMessage = message;

                            btnTutup.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view2) {

                                    if(alert != null){

                                        try {

                                            alert.dismiss();
                                        }catch (Exception e){
                                            e.printStackTrace();
                                        }
                                    }

                                    Toast.makeText(context, finalMessage, Toast.LENGTH_LONG).show();
                                    HomeActivity.stateFragment = 2;
                                    //onBackPressed();
                                    Intent intent = new Intent(context, HomeActivity.class);
                                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                    startActivity(intent);
                                    finish();
                                }
                            });

                            btnCetak.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {


                                    if(!printer.bluetoothAdapter.isEnabled()){

                                        Toast.makeText(context, "Mohon hidupkan bluetooth anda, kemudian klik cetak kembali", Toast.LENGTH_LONG).show();
                                        try{
                                            printer.dialogBluetooth.show();
                                        }catch (Exception e){
                                            e.printStackTrace();
                                        }
                                    }else{

                                        if(printer.isPrinterReady()){

                                            printer.print(transaksi);

                                        }else{

                                            Toast.makeText(context, "Harap pilih device printer telebih dahulu", Toast.LENGTH_LONG).show();
                                            printer.showDevices();
                                        }
                                    }
                                }
                            });

                            try {
                                alert.show();
                            }catch (Exception e){
                                e.printStackTrace();
                            }

                        }

                    }else{

                        View.OnClickListener clickListener = new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {

                                dialogBox.dismissDialog();
                                doTransaksi(checkHarga);
                            }
                        };

                        if(!currentCounter.isEmpty()) dialogBox.showDialog(clickListener, "Ulangi Proses", message);

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

    private void setProdukAdapter() {

        adapterProduk = new ArrayAdapter(this,android.R.layout.simple_list_item_1, listProduk);
        spnNamaProduk.setAdapter(adapterProduk);
        //spnNamaProduk.setSelection(0);

        spnNamaProduk.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

                OptionItem item = (OptionItem) adapterView.getItemAtPosition(i);
                idProduk = item.getValue();
                namaProduk = item.getText();

                if(item.getAtt2().equals("1")){
                    isInquery = true;
                    state = 1;
                }else{
                    isInquery = false;
                }
                setInquiry(isInquery);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }

    private void initData() {

        dialogBox.showDialog(true);
        JSONObject jBody = new JSONObject();
        try {
            jBody.put("kategori", idKategori);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        ApiVolley request = new ApiVolley(context, jBody, "POST", ServerURL.getProdukPPOB, new ApiVolley.VolleyCallback() {
            @Override
            public void onSuccess(String result) {

                dialogBox.dismissDialog();
                String message = "Terjadi kesalahan saat memuat data, mohon coba kembali";

                try {
                    JSONObject response = new JSONObject(result);
                    String status = response.getJSONObject("metadata").getString("status");
                    message = response.getJSONObject("metadata").getString("message");

                    if(status.equals("200")){

                        JSONArray jsonArray = response.getJSONArray("response");
                        for(int i = 0; i < jsonArray.length(); i ++){
                            JSONObject jo = jsonArray.getJSONObject(i);
                            listProduk.add(
                                    new OptionItem(jo.getString("id")
                                            ,jo.getString("namabrg")
                                            ,jo.getString("kode")
                                            ,jo.getString("xml")
                                            ,jo.getString("get")
                                    ));
                        }

                    }else{
                        DialogBox.showDialog(context, 3, message);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();

                    View.OnClickListener clickListener = new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {

                            dialogBox.dismissDialog();
                            initData();
                        }
                    };

                    dialogBox.showDialog(clickListener, "Ulangi Proses", message);
                }

                adapterProduk.notifyDataSetChanged();
            }

            @Override
            public void onError(String result) {

                dialogBox.dismissDialog();
                View.OnClickListener clickListener = new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        dialogBox.dismissDialog();
                        initData();
                    }
                };

                dialogBox.showDialog(clickListener, "Ulangi Proses", result);
            }
        });
    }

    private void setInquiry(boolean flag){

        if(flag){

            llFooter.setVisibility(View.VISIBLE);
            llHarga.setVisibility(View.VISIBLE);
        }else{
            llFooter.setVisibility(View.GONE);
            llHarga.setVisibility(View.GONE);
        }
    }

    @Override
    protected void onDestroy() {
        printer.stopService();
        super.onDestroy();
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
