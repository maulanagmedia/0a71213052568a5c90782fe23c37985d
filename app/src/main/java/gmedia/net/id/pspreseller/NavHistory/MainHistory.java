package gmedia.net.id.pspreseller.NavHistory;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.appcompat.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.leonardus.irfan.bluetoothprinter.Model.Item;
import com.leonardus.irfan.bluetoothprinter.Model.Transaksi;
import com.leonardus.irfan.bluetoothprinter.PspPrinter;
import com.maulana.custommodul.ApiVolley;
import com.maulana.custommodul.CustomItem;
import com.maulana.custommodul.CustomView.DialogBox;
import com.maulana.custommodul.FormatItem;
import com.maulana.custommodul.ItemValidation;
import com.maulana.custommodul.SessionManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import gmedia.net.id.pspreseller.DetailSharePrintout;
import gmedia.net.id.pspreseller.NavHistory.Adapter.ListHistoryAdapter;
import gmedia.net.id.pspreseller.R;
import gmedia.net.id.pspreseller.Utils.ServerURL;

import static android.content.Context.LAYOUT_INFLATER_SERVICE;

public class MainHistory extends Fragment {

    private Context context;
    private View layout;
    private SessionManager session;
    private ItemValidation iv = new ItemValidation();
    private EditText edtTglDari;
    private ImageView ivTglDari;
    private EditText edtTglSampai;
    private ImageView ivTglSampai;
    private ImageView ivNext;
    private ListView lvHistory;
    private ProgressBar pbLoading;
    private Button btnRefresh;
    private String dateFrom = "";
    private String dateTo = "";
    private List<CustomItem> listHistory;
    private DialogBox dialogBox;
    private TextView tvTotal;
    private PspPrinter printer;
    private String isPPOB = "", jml = "", denda = "", admin = "";
    private String msisdn = "", periode = "", standMeter = "", hargaCustom = "", currentCounter = "", golongan = "";

    public MainHistory() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        layout = inflater.inflate(R.layout.fragment_main_history, container, false);
        context = getContext();
        printer = new PspPrinter(context);
        printer.startService();
        session = new SessionManager(context);

        initUI();
        return layout;
    }

    private void initUI() {

        edtTglDari = (EditText) layout.findViewById(R.id.edt_tgl_dari);
        ivTglDari = (ImageView) layout.findViewById(R.id.iv_tgl_dari);
        edtTglSampai = (EditText) layout.findViewById(R.id.edt_tgl_sampai);
        ivTglSampai = (ImageView) layout.findViewById(R.id.iv_tgl_sampai);
        ivNext = (ImageView) layout.findViewById(R.id.iv_next);
        lvHistory = (ListView) layout.findViewById(R.id.lv_history);
        pbLoading = (ProgressBar) layout.findViewById(R.id.pb_loading);
        btnRefresh = (Button) layout.findViewById(R.id.btn_refresh);
        tvTotal = (TextView) layout.findViewById(R.id.tv_total);

        dateFrom = iv.sumDate(iv.getCurrentDate(FormatItem.formatDateDisplay), -1, FormatItem.formatDateDisplay) ;
        dateTo = iv.getCurrentDate(FormatItem.formatDateDisplay);

        edtTglDari.setText(dateFrom);
        edtTglSampai.setText(dateTo);
        dialogBox = new DialogBox(context);

        initEvent();

        getData();
    }

    private void initEvent() {

        ivTglDari.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                final Calendar customDate;
                SimpleDateFormat sdf = new SimpleDateFormat(FormatItem.formatDateDisplay);

                Date dateValue = null;

                try {
                    dateValue = sdf.parse(dateFrom);
                } catch (ParseException e) {
                    e.printStackTrace();
                }

                customDate = Calendar.getInstance();
                final DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker datePicker, int year, int month, int date) {
                        customDate.set(Calendar.YEAR,year);
                        customDate.set(Calendar.MONTH,month);
                        customDate.set(Calendar.DATE,date);

                        SimpleDateFormat sdFormat = new SimpleDateFormat(FormatItem.formatDateDisplay, Locale.US);
                        dateFrom = sdFormat.format(customDate.getTime());
                        edtTglDari.setText(dateFrom);
                    }
                };

                SimpleDateFormat yearOnly = new SimpleDateFormat("yyyy");
                new DatePickerDialog(context ,date , iv.parseNullInteger(yearOnly.format(dateValue)),dateValue.getMonth(),dateValue.getDate()).show();
            }
        });

        ivTglSampai.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                final Calendar customDate;
                SimpleDateFormat sdf = new SimpleDateFormat(FormatItem.formatDateDisplay);

                Date dateValue = null;

                try {
                    dateValue = sdf.parse(dateTo);
                } catch (ParseException e) {
                    e.printStackTrace();
                }

                customDate = Calendar.getInstance();
                final DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker datePicker, int year, int month, int date) {
                        customDate.set(Calendar.YEAR,year);
                        customDate.set(Calendar.MONTH,month);
                        customDate.set(Calendar.DATE,date);

                        SimpleDateFormat sdFormat = new SimpleDateFormat(FormatItem.formatDateDisplay, Locale.US);
                        dateTo = sdFormat.format(customDate.getTime());
                        edtTglSampai.setText(dateTo);
                    }
                };

                SimpleDateFormat yearOnly = new SimpleDateFormat("yyyy");
                new DatePickerDialog(context ,date , iv.parseNullInteger(yearOnly.format(dateValue)),dateValue.getMonth(),dateValue.getDate()).show();
            }
        });

        ivNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                dateFrom = edtTglDari.getText().toString();
                dateTo = edtTglSampai.getText().toString();
                getData();
            }
        });
    }

    private void getData() {

        dialogBox.showDialog(false);
        JSONObject jBody = new JSONObject();
        try {
            jBody.put("tgl_mulai", dateFrom);
            jBody.put("tgl_selesai", dateTo);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        ApiVolley request = new ApiVolley(context, jBody, "POST", ServerURL.viewHistory, new ApiVolley.VolleyCallback() {
            @Override
            public void onSuccess(String result) {

                dialogBox.dismissDialog();
                try {
                    JSONObject response = new JSONObject(result);
                    String status = response.getJSONObject("metadata").getString("status");
                    listHistory = new ArrayList<>();
                    double total = 0;

                    if(status.equals("200")){

                        JSONArray jsonArray = response.getJSONArray("response");
                        for(int i = 0; i < jsonArray.length(); i ++){
                            JSONObject jo = jsonArray.getJSONObject(i);
                            listHistory.add(
                                    new CustomItem(
                                            jo.getString("id"),                 // 1
                                            jo.getString("nobukti"),            // 2
                                            jo.getString("tgl"),                // 3
                                            jo.getString("status_transaksi"),   // 4
                                            jo.getString("nomor"),              // 5
                                            jo.getString("total"),              // 6
                                            jo.getString("namabrg"),            // 7
                                            jo.getString("nama"),               // 8
                                            jo.getString("jam"),                // 9
                                            jo.getString("cashback"),           // 10
                                            jo.getString("stok_akhir"),         // 11
                                            jo.getString("sn"),                 // 12
                                            jo.getString("ppob"),               // 13
                                            jo.getString("jml"),                // 14
                                            jo.getString("admin"),              // 15
                                            jo.getString("denda"),              // 16
                                            jo.getString("tanggal"),            // 17
                                            jo.getString("periode"),            // 18
                                            jo.getString("stand_meter"),        // 19
                                            jo.getString("harga_custom"),       // 20
                                            jo.getString("transaction_id"),     // 21
                                            jo.getString("daya")+"/"+jo.getString("kwh") // 22
                                    ));

                            total += iv.parseNullDouble(jo.getString("total"));
                        }
                        getTableList(listHistory);

                    }else{
                        getTableList(null);
                    }

                    tvTotal.setText(iv.ChangeToRupiahFormat(total));

                } catch (JSONException e) {
                    e.printStackTrace();

                    dialogBox.dismissDialog();
                    getTableList(null);
                    View.OnClickListener clickListener = new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {

                            dialogBox.dismissDialog();
                            getData();

                        }
                    };

                    dialogBox.showDialog(clickListener, "Ulangi Proses", "Terjadi kesalahan saat mengambil data");
                }
            }

            @Override
            public void onError(String result) {
                View.OnClickListener clickListener = new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        dialogBox.dismissDialog();
                        getData();

                    }
                };

                dialogBox.showDialog(clickListener, "Ulangi Proses", "Terjadi kesalahan saat mengambil data");
            }
        });
    }

    private void getTableList(List<CustomItem> listItem) {

        lvHistory.setAdapter(null);

        if(listItem != null && listItem.size() > 0){

            final ListHistoryAdapter adapter = new ListHistoryAdapter((Activity) context, listItem);
            lvHistory.setAdapter(adapter);

            lvHistory.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                    final CustomItem item = (CustomItem) adapterView.getItemAtPosition(i);

                    final AlertDialog.Builder builder = new AlertDialog.Builder(context);
                    LayoutInflater inflater = (LayoutInflater) context.getSystemService(LAYOUT_INFLATER_SERVICE);
                    View viewDialog = inflater.inflate(R.layout.dialog_cetak, null);
                    builder.setView(viewDialog);
                    builder.setCancelable(false);

                    isPPOB = item.getItem13();
                    jml = item.getItem14();
                    admin = item.getItem15();
                    denda = item.getItem16();
                    msisdn = item.getItem5();
                    periode = item.getItem18();
                    standMeter = item.getItem19();
                    hargaCustom = item.getItem20();
                    currentCounter = item.getItem21();
                    golongan = item.getItem22();

                    final Button btnTutup = (Button) viewDialog.findViewById(R.id.btn_tutup);
                    final Button btnShare = (Button) viewDialog.findViewById(R.id.btn_share);
                    final Button btnCetak = (Button) viewDialog.findViewById(R.id.btn_cetak);
                    final TextView tvTitle = (TextView) viewDialog.findViewById(R.id.tv_title);
                    final EditText edtBiaya = (EditText) viewDialog.findViewById(R.id.edt_biaya);
                    if(isPPOB.equals("0")){

                        tvTitle.setText("Total Harga");
                        edtBiaya.setHint("Total Harga");
                    }
                    edtBiaya.setText(hargaCustom);

                    final AlertDialog alert = builder.create();
                    alert.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

                    List<Item> items = new ArrayList<>();

                    items.add(new Item(item.getItem7()
                            , 1
                            , iv.parseNullDouble(isPPOB.equals("0") ? edtBiaya.getText().toString() : jml)));

                    Calendar date = Calendar.getInstance();
                    final Transaksi transaksi = new Transaksi(item.getItem8(), session.getNama(), item.getItem12(), date.getTime(), items, iv.ChangeFormatDateString(item.getItem17(), FormatItem.formatDate, FormatItem.formatDateDisplay) + " " + item.getItem9());

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
                        }
                    });

                    btnShare.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {

                            hargaCustom = edtBiaya.getText().toString();

                            if(edtBiaya.getText().toString().isEmpty()){

                                String message = "Biaya Admin harap diisi";
                                if(isPPOB.equals("0")) message = "Total Harga harap diisi";
                                edtBiaya.setError(message);
                                edtBiaya.requestFocus();
                                return;
                            }else{

                                edtBiaya.setError(null);
                            }

                            saveCustomHarga(hargaCustom);

                            String shareBody = "";
                            /*Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
                            sharingIntent.setType("text/plain");
                            sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "Penjualan " +getResources().getString(R.string.app_name));*/

                            Intent intent = new Intent(context, DetailSharePrintout.class);

                            shareBody += "Nama   : " + item.getItem8() +"\n";
                            shareBody += "Item   : " + item.getItem7() +"\n";
                            shareBody += "Tanggal: " + item.getItem3() +" "+ item.getItem9()+"\n";
                            shareBody += "Token  : " + item.getItem12() +"\n";
                            shareBody += "MSISDN : " + msisdn +"\n";

                            intent.putExtra("nama", item.getItem8());
                            intent.putExtra("item", item.getItem7());
                            intent.putExtra("token", item.getItem12());
                            intent.putExtra("msisdn", msisdn);
                            intent.putExtra("tanggal", item.getItem3() +" "+ item.getItem9());

                            if(!item.getItem22().trim().equals("/0")){
                                shareBody += "Daya   : " + item.getItem22() +"\n";
                                intent.putExtra("daya", item.getItem22());
                            }

                            if(isPPOB.equals("0")){

                                shareBody += "Harga  : " + iv.ChangeToCurrencyFormat(edtBiaya.getText().toString()) +"\n";
                                intent.putExtra("harga", iv.ChangeToCurrencyFormat(edtBiaya.getText().toString()));

                            }else{

                                shareBody += "Denda  : " + iv.ChangeToCurrencyFormat(denda) +"\n";
                                shareBody += "Admin  : " + iv.ChangeToCurrencyFormat(admin) +"\n";
                                shareBody += "Harga  : " + iv.ChangeToCurrencyFormat(jml) +"\n";

                                double biayaAdmin = iv.parseNullDouble(edtBiaya.getText().toString());
                                double dpp = biayaAdmin / 1.1;
                                double ppn = biayaAdmin - dpp;
                                double nonPPn = iv.parseNullDouble(item.getItem6()) - biayaAdmin;
                                if(nonPPn < 0) nonPPn = iv.parseNullDouble(item.getItem6());

                                intent.putExtra("denda", iv.ChangeToCurrencyFormat(denda));
                                intent.putExtra("admin", iv.ChangeToCurrencyFormat(admin));
                                intent.putExtra("harga", iv.ChangeToCurrencyFormat(jml));

                                intent.putExtra("ppn", "DPP : " + iv.ChangeToCurrencyFormat(dpp)
                                    + ", PPN : " + iv.ChangeToCurrencyFormat(ppn) + ", NonPPN : " + iv.ChangeToCurrencyFormat(nonPPn)
                                );

                            }

                            intent.putExtra("text", shareBody);
                            startActivity(intent);

                            /*sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, shareBody);
                            startActivity(Intent.createChooser(sharingIntent, "Bagikan"));*/

                        }
                    });

                    btnCetak.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {

                            hargaCustom = edtBiaya.getText().toString();

                            if(edtBiaya.getText().toString().isEmpty()){

                                String message = "Biaya Admin harap diisi";
                                if(isPPOB.equals("0")) message = "Total Harga harap diisi";
                                edtBiaya.setError(message);
                                edtBiaya.requestFocus();
                                return;
                            }else{

                                edtBiaya.setError(null);
                            }

                            if(!printer.bluetoothAdapter.isEnabled()){

                                Toast.makeText(context, "Mohon hidupkan bluetooth anda, kemudian klik cetak kembali", Toast.LENGTH_LONG).show();
                                try{
                                    printer.dialogBluetooth.show();
                                }catch (Exception e){
                                    e.printStackTrace();
                                }
                            }else{

                                if(printer.isPrinterReady()){

                                    saveCustomHarga(hargaCustom);

                                    if(isPPOB.equals("0")){

                                        List<Item> items1 = new ArrayList<>();

                                        items1.add(new Item(item.getItem7(), 1, iv.parseNullDouble(edtBiaya.getText().toString())));

                                        Calendar date = Calendar.getInstance();
                                        final Transaksi transaksi1 = new Transaksi(item.getItem8(), session.getNama(), item.getItem12(), date.getTime(), items1, iv.ChangeFormatDateString(item.getItem17(), FormatItem.formatDate, FormatItem.formatDateDisplay) + " " + item.getItem9());
                                        transaksi1.setMsisdn(msisdn);

                                        printer.print(transaksi1,"Nama");
                                    }else{

                                        double biayaAdmin = iv.parseNullDouble(edtBiaya.getText().toString());
                                        double dpp = biayaAdmin / 1.1;
                                        double ppn = biayaAdmin - dpp;
                                        double nonPPn = iv.parseNullDouble(item.getItem6()) - biayaAdmin;
                                        if(nonPPn < 0) nonPPn = iv.parseNullDouble(item.getItem6());

                                        transaksi.setBiayaAdmin(iv.ChangeToCurrencyFormat(iv.doubleToString(biayaAdmin)));
                                        transaksi.setDpp(iv.ChangeToCurrencyFormat(iv.doubleToString(dpp)));
                                        transaksi.setPpn(iv.ChangeToCurrencyFormat(iv.doubleToString(ppn)));
                                        transaksi.setNonPPN(iv.ChangeToCurrencyFormat(iv.doubleToString(nonPPn)));
                                        transaksi.setJml(iv.parseNullDouble(jml));
                                        transaksi.setDenda(iv.parseNullDouble(denda));
                                        transaksi.setAdmin(iv.parseNullDouble(admin));
                                        transaksi.setMsisdn(msisdn);
                                        transaksi.setPeriode(periode);
                                        transaksi.setStandMeter(standMeter);
                                        transaksi.setGolongan(golongan);

                                        printer.print(transaksi);
                                    }

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
            });
        }
    }

    private void saveCustomHarga(String hargaCustom) {

        JSONObject jBody = new JSONObject();
        try {
            jBody.put("id", currentCounter);
            jBody.put("harga", hargaCustom);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        ApiVolley request = new ApiVolley(context, jBody, "POST", ServerURL.saveHargaPPOB, new ApiVolley.VolleyCallback() {
            @Override
            public void onSuccess(String result) {

                dialogBox.dismissDialog();
                String message = "Terjadi kesalahan saat memuat data, mohon coba kembali";

                try {
                    JSONObject response = new JSONObject(result);
                    String status = response.getJSONObject("metadata").getString("status");
                    message = response.getJSONObject("metadata").getString("message");

                    if(status.equals("200")){


                    }else{
                        Toast.makeText(context, message, Toast.LENGTH_LONG).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();

                    Toast.makeText(context, message, Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onError(String result) {

                Toast.makeText(context, result, Toast.LENGTH_LONG).show();
            }
        });
    }

    /*@Override
    public void onDestroy() {

        printer.stopService();
        super.onDestroy();
    }*/
}
