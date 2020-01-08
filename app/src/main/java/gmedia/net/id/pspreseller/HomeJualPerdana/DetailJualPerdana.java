package gmedia.net.id.pspreseller.HomeJualPerdana;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.maulana.custommodul.ApiVolley;
import com.maulana.custommodul.CustomItem;
import com.maulana.custommodul.CustomView.DialogBox;
import com.maulana.custommodul.ItemValidation;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import gmedia.net.id.pspreseller.HomeActivity;
import gmedia.net.id.pspreseller.HomeJualPerdana.Adapter.AdapterSelectedCCID;
import gmedia.net.id.pspreseller.R;
import gmedia.net.id.pspreseller.Utils.ServerURL;

public class DetailJualPerdana extends AppCompatActivity {

    private ItemValidation iv = new ItemValidation();
    private Context context;
    private boolean isLoading = false;
    private DialogBox dialogBox;
    private Button btnScan, btnTambah, btnProses;
    private TextView tvCCID, tvNama;
    private EditText edtHarga;
    private RecyclerView rvCCID;
    private List<CustomItem> selectedCCID = new ArrayList<>();
    private AdapterSelectedCCID adapter;
    private TextView tvTotal, tvTotalHarga;
    private String selectedHarga = "", selectedKodebrg = "", selectedHpp = "";
    private String currentString = "";
    private AutoCompleteTextView edtNama;
    private EditText edtAlamat;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_jual_perdana);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(getResources().getDrawable(R.mipmap.ic_down));

        getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN
        );

        context = this;
        dialogBox = new DialogBox(context);

        setTitle("Penjualan Perdana");

        initUI();
        initEvent();
    }

    private void initUI() {

        edtNama = (AutoCompleteTextView) findViewById(R.id.edt_nama);
        edtAlamat = (EditText) findViewById(R.id.edt_alamat);
        btnScan = (Button) findViewById(R.id.btn_scan);
        tvCCID = (TextView) findViewById(R.id.tv_ccid);
        tvNama = (TextView) findViewById(R.id.tv_nama);
        edtHarga = (EditText) findViewById(R.id.edt_harga);
        btnTambah = (Button) findViewById(R.id.btn_tambah);
        rvCCID = (RecyclerView) findViewById(R.id.rv_ccid);
        btnProses = (Button) findViewById(R.id.btn_proses);
        tvTotal = (TextView) findViewById(R.id.tv_total);
        tvTotalHarga = (TextView) findViewById(R.id.tv_total_harga);

        selectedCCID = new ArrayList<>();
        adapter = new AdapterSelectedCCID(context, selectedCCID);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        rvCCID.setLayoutManager(layoutManager);
        rvCCID.setItemAnimator(new DefaultItemAnimator());
        rvCCID.setAdapter(adapter);
    }

    private void initEvent() {

        btnScan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                IntentIntegrator integrator = new IntentIntegrator(DetailJualPerdana.this);
                integrator.setOrientationLocked(false);
                integrator.initiateScan();
            }
        });

        btnTambah.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(selectedKodebrg.isEmpty()){

                    Toast.makeText(context, "Perdana tidak ditemukan, harap scan ulang", Toast.LENGTH_LONG).show();
                    return;
                }

                boolean isExist = false;
                for (CustomItem item: selectedCCID){

                    if(item.getItem1().equals(tvCCID.getText().toString())){

                        isExist = true;
                        break;
                    }
                }

                if(isExist){

                    Toast.makeText(context, "Barang ini sudah ada di daftar", Toast.LENGTH_LONG).show();
                    return;
                }

                if(edtHarga.getText().toString().isEmpty()){

                    edtHarga.setError("Harga harap diisi");
                    edtHarga.requestFocus();
                    return;
                }else{
                    edtHarga.setError(null);
                }

                AlertDialog dialog = new AlertDialog.Builder(context)
                        .setTitle("Konfirmasi")
                        .setMessage("Tambahkan ke daftar penjualan ?")
                        .setPositiveButton("Ya", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                selectedCCID.add(new CustomItem(
                                        tvCCID.getText().toString()
                                        , tvNama.getText().toString()
                                        , selectedHarga
                                        , edtHarga.getText().toString().replaceAll("[,.]", "")
                                        , selectedKodebrg
                                        , selectedHpp
                                ));
                                updateCcid();

                                tvCCID.setText("");
                                tvNama.setText("");
                                edtHarga.setText("");
                                selectedHarga = "";
                                selectedKodebrg = "";
                                selectedHarga = "";


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

        edtHarga.addTextChangedListener(new TextWatcher() {

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
                    edtHarga.removeTextChangedListener(this);

                    String formatted = iv.ChangeToCurrencyFormat(cleanString);

                    currentString = formatted;
                    edtHarga.setText(formatted);
                    edtHarga.setSelection(formatted.length());
                    edtHarga.addTextChangedListener(this);
                }
            }
        });

        btnProses.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(selectedCCID.size() <= 0){
                    Toast.makeText(context, "Harap masukkan barang yang dijual", Toast.LENGTH_LONG).show();
                    return;
                }

                androidx.appcompat.app.AlertDialog alertDialog = new androidx.appcompat.app.AlertDialog.Builder(context)
                        .setIcon(R.mipmap.ic_launcher)
                        .setTitle("Konfirmasi")
                        .setMessage("Apakah anda yakin ingin menyimpan data penjualan ?")
                        .setPositiveButton("Ya", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {

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
    }

    private void saveData() {

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

        try {
            JSONArray listBarang = new JSONArray();

            for(CustomItem item: selectedCCID){

                JSONObject jBarang = new JSONObject();
                jBarang.put("ccid", item.getItem1());
                jBarang.put("kodebrg", item.getItem5());
                jBarang.put("harga", item.getItem4());
                jBarang.put("hpp", item.getItem6());
                jBarang.put("jumlah", "1");

                listBarang.put(jBarang);
            }

            jBody.put("nama", edtNama.getText().toString());
            jBody.put("alamat", edtAlamat.getText().toString());
            jBody.put("barang", listBarang);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        String url = ServerURL.saveDSPerdana, method = "POST";

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
                        HomeActivity.stateFragment = 2;
                        ((Activity) context).onBackPressed();
                    }else{

                        Toast.makeText(context, superMessage, Toast.LENGTH_LONG).show();
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(context, superMessage, Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onError(String result) {

                isLoading = false;
                progressDialog.dismiss();
                Toast.makeText(context, "Terjadi kesalahan saat menyimpan data, harap ulangi kembali", Toast.LENGTH_LONG).show();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        //Hasil dari QR Code Scanner

        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if(result != null) {

            if(result.getContents() != null){
                //System.out.println(result.getContents());

                //Menambahkan data CCID ke list
                String ccid = result.getContents();
                //String ccid = "sdfss0050000351573706";
                if(ccid.length() >= 21)
                initCcid(ccid.substring(5,21));
            }
        }else{
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    private void initCcid(final String ccid){

        dialogBox.showDialog(true);
        JSONObject jBody = new JSONObject();

        try {

            jBody.put("ccid", ccid);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        ApiVolley request = new ApiVolley(context, jBody, "POST", ServerURL.getCCID, new ApiVolley.VolleyCallback() {
            @Override
            public void onSuccess(String result) {

                dialogBox.dismissDialog();
                String message = "Terjadi kesalahan saat memuat data, harap ulangi proses";

                try {

                    JSONObject response = new JSONObject(result);
                    String status = response.getJSONObject("metadata").getString("status");
                    message = response.getJSONObject("metadata").getString("message");

                    if(iv.parseNullInteger(status) == 200){

                        JSONObject jo = response.getJSONObject("response");
                        tvCCID.setText(ccid);
                        tvNama.setText(jo.getString("namabrg"));
                        selectedHarga = jo.getString("harga");
                        selectedKodebrg = jo.getString("kodebrg");
                        selectedHpp = jo.getString("hpp");
                        edtHarga.setText(selectedHarga);

                    }else{

                        DialogBox.showDialog(context, 3, message);
                    }

                } catch (JSONException e) {

                    e.printStackTrace();
                    View.OnClickListener clickListener = new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {

                            dialogBox.dismissDialog();
                            initCcid(ccid);
                        }
                    };

                    dialogBox.showDialog(clickListener, "Ulangi Proses", message);
                }
            }

            @Override
            public void onError(String result) {

                dialogBox.dismissDialog();
                View.OnClickListener clickListener = new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        dialogBox.dismissDialog();
                        initCcid(ccid);
                    }
                };

                dialogBox.showDialog(clickListener, "Ulangi Proses", "Terjadi kesalahan, harap ulangi proses");
            }
        });
    }

    public void updateCcid(){
        //Mengupdate tampilan informasi berdasarkan CCID yang dipilih
        //Update RecyclerView
        //System.out.println(this.selectedCcid.size());
        adapter.notifyDataSetChanged();

        //Update total
        tvTotal.setText(String.valueOf(selectedCCID.size()));
        double total = 0;
        for(CustomItem c : selectedCCID){
            total += iv.parseNullDouble(c.getItem4());
        }
        tvTotalHarga.setText(iv.ChangeToCurrencyFormat(iv.doubleToString(total)));
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
