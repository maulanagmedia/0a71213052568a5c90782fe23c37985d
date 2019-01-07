package gmedia.net.id.pspreseller.HomeInfoStok;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.Settings;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
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

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import gmedia.net.id.pspreseller.HomeInfoStok.Adapter.ListBalasanMkiosAdapter;
import gmedia.net.id.pspreseller.HomeInfoStok.Adapter.ListHistoryDepositAdapter;
import gmedia.net.id.pspreseller.HomePulsa.Adapter.ListBalasanInjectAdapter;
import gmedia.net.id.pspreseller.R;
import gmedia.net.id.pspreseller.Utils.ServerURL;

public class DetailInfoStok extends AppCompatActivity {

    private static ListView lvBalasan, lvBalasanMkios;
    private static Context context;
    private static SessionManager session;
    private static List<CustomItem> listBalasan, listBalasanMkios;
    private static ItemValidation iv = new ItemValidation();
    private static ListBalasanInjectAdapter balasanAdapter;
    private static ListBalasanMkiosAdapter balasanMkiosAdapter;
    private static String flag = "", value = "", kode = "";
    private LinearLayout llHistoryPbob, llHeader;
    private EditText edtTglDari, edtTglSampai, edtTanggal;
    private ImageView ivTglDari, ivTglSampai, ivNext, ivTanggal;
    private ListView lvHistoryPbob;
    private String dateFrom = "", dateTo = "";
    private static String dateNow = "";
    private static DialogBox dialogBox;
    private List<CustomItem> listHistory;
    private TextView tvTotal;
    public static boolean isActive = false;
    private LinearLayout llMKios;
    private static String tipe = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_info_stok);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        //getSupportActionBar().setHomeAsUpIndicator(getResources().getDrawable(R.mipmap.ic_down));

        getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN
        );

        context = this;
        dialogBox = new DialogBox(context);
        isActive = true;


        /*if(!checkNotificationEnabled()){

            Intent intent = new Intent(
                    "android.settings.ACTION_NOTIFICATION_LISTENER_SETTINGS");
            startActivity(intent);
        }*/

        initUI();
    }

    private void initUI() {

        lvBalasan = (ListView) findViewById(R.id.lv_balasan);
        lvBalasanMkios = (ListView) findViewById(R.id.lv_balasan_mkios);
        session = new SessionManager(context);
        listBalasan = new ArrayList<>();
        listBalasanMkios = new ArrayList<>();
        balasanAdapter = new ListBalasanInjectAdapter((Activity) context, listBalasan);
        balasanMkiosAdapter = new ListBalasanMkiosAdapter((Activity) context, listBalasanMkios);
        lvBalasan.setAdapter(balasanAdapter);
        lvBalasanMkios.setAdapter(balasanMkiosAdapter);
        llHistoryPbob = (LinearLayout) findViewById(R.id.ll_history_pbob);
        llHeader = (LinearLayout) findViewById(R.id.ll_header);
        edtTanggal = (EditText) findViewById(R.id.edt_tgl);
        ivTanggal = (ImageView) findViewById(R.id.iv_tgl);
        edtTglDari = (EditText) findViewById(R.id.edt_tgl_dari);
        ivTglDari = (ImageView) findViewById(R.id.iv_tgl_dari);
        edtTglSampai = (EditText) findViewById(R.id.edt_tgl_sampai);
        ivTglSampai = (ImageView) findViewById(R.id.iv_tgl_sampai);
        ivNext = (ImageView) findViewById(R.id.iv_next);
        lvHistoryPbob = (ListView) findViewById(R.id.lv_history_pbob);
        tvTotal = (TextView) findViewById(R.id.tv_total);
        llMKios = (LinearLayout) findViewById(R.id.ll_mkios);

        Bundle bundle = getIntent().getExtras();
        if(bundle != null){

            flag = bundle.getString("flag", "");
            value = bundle.getString("value", "");
            kode = bundle.getString("kode", "");

            if(kode.equals("MK")){

                setTitle("Stok MKIOS");
                llMKios.setVisibility(View.VISIBLE);
            }else if(kode.equals("MB")){

                setTitle("Stok Bulk");
                llMKios.setVisibility(View.VISIBLE);
            }else if(kode.equals("TC")){

                setTitle("Stok TCash");
            }else if(kode.equals("SD")){

                setTitle("Stok Saldo Tunai");
                llHeader.setVisibility(View.GONE);
            }else{
                llHeader.setVisibility(View.VISIBLE);
            }

            if(flag.equals("0")){

                CustomItem item = new CustomItem(iv.getCurrentDate(FormatItem.formatTime),
                        "Total saldo tunai yang anda miliki sebesar <b>" + iv.ChangeToRupiahFormat(value)+"</b>");
                balasanAdapter.addData(item);

                llHistoryPbob.setVisibility(View.VISIBLE);

                dateFrom = iv.sumDate(iv.getCurrentDate(FormatItem.formatDateDisplay), -1, FormatItem.formatDateDisplay) ;
                dateTo = iv.getCurrentDate(FormatItem.formatDateDisplay);

                edtTglDari.setText(dateFrom);
                edtTglSampai.setText(dateTo);

                initEvent();

                getHistoryPBOB();
            }else{

                getHistoryBalasan();
                value = value.replace("#", Uri.encode("#"));
                startActivity(new Intent("android.intent.action.CALL", Uri.parse("tel:" + value)));
            }
        }

        ivTanggal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                final Calendar customDate;
                SimpleDateFormat sdf = new SimpleDateFormat(FormatItem.formatDateDisplay);

                Date dateValue = null;

                try {
                    dateValue = sdf.parse(dateNow);
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
                        dateNow = sdFormat.format(customDate.getTime());
                        edtTanggal.setText(dateNow);

                        getHistoryBalasan();
                    }
                };

                SimpleDateFormat yearOnly = new SimpleDateFormat("yyyy");
                new DatePickerDialog(context ,date , iv.parseNullInteger(yearOnly.format(dateValue)),dateValue.getMonth(),dateValue.getDate()).show();
            }
        });

        dateNow = iv.getCurrentDate(FormatItem.formatDateDisplay);
        edtTanggal.setText(dateNow);

        LocalBroadcastManager.getInstance(this).registerReceiver(onNotice, new IntentFilter("Msg"));
    }

    //check notification access setting is enabled or not
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

    private BroadcastReceiver onNotice= new BroadcastReceiver() {

        private String TAG = "MAIN";

        @Override
        public void onReceive(Context context, Intent intent) {
            // String pack = intent.getStringExtra("package");
            String title = intent.getStringExtra("title");
            String text = intent.getStringExtra("text");

            Log.d(TAG, "title: " + title);
            Log.d(TAG, "text: " + text);
            /*//int id = intent.getIntExtra("icon",0);

            Context remotePackageContext = null;
            try {
//                remotePackageContext = getApplicationContext().createPackageContext(pack, 0);
//                Drawable icon = remotePackageContext.getResources().getDrawable(id);
//                if(icon !=null) {
//                    ((ImageView) findViewById(R.id.imageView)).setBackground(icon);
//                }
                byte[] byteArray =intent.getByteArrayExtra("icon");
                Bitmap bmp = null;
                if(byteArray !=null) {
                    bmp = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length);
                }

                Model model = new Model();
                model.setName(title +" " +text);
                model.setImage(bmp);
            } catch (Exception e) {
                e.printStackTrace();
            }*/
        }
    };

    private static void getHistoryBalasan() {

        dialogBox.showDialog(false);
        tipe = "1";
        if(kode.equals("TC")) tipe = "2";

        JSONObject jBody = new JSONObject();
        try {
            if(!dateNow.isEmpty())jBody.put("tgl", iv.ChangeFormatDateString(dateNow, FormatItem.formatDateDisplay, FormatItem.formatDate));
            jBody.put("tipe", tipe);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        ApiVolley request = new ApiVolley(context, jBody, "POST", ServerURL.getInfoStok, new ApiVolley.VolleyCallback() {
            @Override
            public void onSuccess(String result) {

                dialogBox.dismissDialog();
                try {
                    JSONObject response = new JSONObject(result);
                    String status = response.getJSONObject("metadata").getString("status");
                    listBalasan = new ArrayList<>();
                    listBalasanMkios = new ArrayList<>();

                    if(status.equals("200")){

                        JSONArray jsonArray = response.getJSONArray("response");

                        if(tipe.equals("1")){

                            for(int i = 0; i < jsonArray.length(); i ++){
                                JSONObject jo = jsonArray.getJSONObject(i);
                                JSONObject jo1 = jo.getJSONObject("balasan").getJSONObject("mkios");

                                if(jo.getString("flag").equals("1")){ // mkios
                                    listBalasanMkios.add(new CustomItem(
                                            jo1.getString("v1"),
                                            jo1.getString("v5"),
                                            jo1.getString("v10"),
                                            jo1.getString("v15"),
                                            jo1.getString("v20"),
                                            jo1.getString("v25"),
                                            jo1.getString("v40"),
                                            jo1.getString("v50"),
                                            jo1.getString("v80"),
                                            jo1.getString("v100"),
                                            jo1.getString("v200"),
                                            jo1.getString("v300"),
                                            "",
                                            jo.getString("timestamp")
                                    ));

                                }else{ //bulk
                                    JSONObject jo2 = jo.getJSONObject("balasan");
                                    listBalasanMkios.add(new CustomItem(
                                            "",
                                            "",
                                            "",
                                            "",
                                            "",
                                            "",
                                            "",
                                            "",
                                            "",
                                            "",
                                            "",
                                            "",
                                            jo2.getString("bulk"),
                                            jo.getString("timestamp")
                                    ));
                                    /*listBalasan.add(
                                            new CustomItem(
                                                    iv.ChangeFormatDateString(jo.getString("timestamp"), FormatItem.formatTimestamp, FormatItem.formatTime),
                                                    jo2.getString("balasan")
                                            ));*/
                                }


                            }
                            getTableBalasan(listBalasan, listBalasanMkios);
                        }else{

                            for(int i = 0; i < jsonArray.length(); i ++){
                                JSONObject jo = jsonArray.getJSONObject(i);
                                JSONObject jo2 = jo.getJSONObject("balasan");
                                listBalasan.add(
                                        new CustomItem(iv.ChangeFormatDateString(jo.getString("timestamp"), FormatItem.formatTimestamp, FormatItem.formatTime),
                                                jo2.getString("balasan")));

                            }
                            getTableBalasan(listBalasan);
                        }




                    }else{
                        getTableBalasan(null);
                    }


                } catch (JSONException e) {
                    e.printStackTrace();
                    getTableBalasan(null);

                    View.OnClickListener clickListener = new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {

                            dialogBox.dismissDialog();
                            getHistoryBalasan();

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
                        getHistoryBalasan();

                    }
                };

                dialogBox.showDialog(clickListener, "Ulangi Proses", "Terjadi kesalahan saat mengambil data");
            }
        });
    }

    private static void getTableBalasan(List<CustomItem> listItems) {

        lvBalasan.setAdapter(null);

        if(listItems != null && listItems.size() > 0){

            balasanAdapter = new ListBalasanInjectAdapter((Activity) context, listItems);
            lvBalasan.setAdapter(balasanAdapter);
        }
    }

    private static void getTableBalasan(List<CustomItem> listItems, List<CustomItem> listMkios) {

        lvBalasan.setAdapter(null);

        if(listItems != null && listItems.size() > 0){

            balasanAdapter = new ListBalasanInjectAdapter((Activity) context, listItems);
            lvBalasan.setAdapter(balasanAdapter);
        }

        if(listMkios != null && listMkios.size() > 0){

            balasanMkiosAdapter = new ListBalasanMkiosAdapter((Activity) context, listMkios);
            lvBalasanMkios.setAdapter(balasanMkiosAdapter);
        }
    }

    private void getHistoryPBOB() {

        dialogBox.showDialog(false);
        JSONObject jBody = new JSONObject();
        try {
            jBody.put("tgl_mulai", dateFrom);
            jBody.put("tgl_selesai", dateTo);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        ApiVolley request = new ApiVolley(context, jBody, "POST", ServerURL.viewHistoryDeposit, new ApiVolley.VolleyCallback() {
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
                                            jo.getString("id"),
                                            jo.getString("tgl"),
                                            jo.getString("keterangan"),
                                            jo.getString("total"),
                                            jo.getString("tanda")));

                            if(jo.getString("tanda").equals("+")){

                                total += iv.parseNullDouble(jo.getString("total"));
                            }else{

                                total -= iv.parseNullDouble(jo.getString("total"));
                            }
                        }

                        getTableList(listHistory);

                    }else{
                        getTableList(null);
                    }

                    tvTotal.setText(iv.ChangeToRupiahFormat(total));
                } catch (JSONException e) {
                    e.printStackTrace();
                    getTableList(null);

                    View.OnClickListener clickListener = new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {

                            dialogBox.dismissDialog();
                            getHistoryPBOB();

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
                        getHistoryPBOB();

                    }
                };

                dialogBox.showDialog(clickListener, "Ulangi Proses", "Terjadi kesalahan saat mengambil data");
            }
        });
    }

    private void getTableList(List<CustomItem> listItems) {

        lvHistoryPbob.setAdapter(null);

        if(listItems != null && listItems.size() > 0){

            ListHistoryDepositAdapter adapter = new ListHistoryDepositAdapter((Activity) context, listItems);
            lvHistoryPbob.setAdapter(adapter);
        }
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
                getHistoryPBOB();
            }
        });
    }

    public static void addTambahBalasan(String sender, String text){

        if(session != null
                && !text.toLowerCase().equals("[ussd code running…]")
                && !text.toLowerCase().equals("[phone]")
                && !text.toLowerCase().equals("[detail inject pulsa]")
                && !text.toLowerCase().equals("[]")
                && !text.toLowerCase().equals("[clipboard]")){

            try {

                if(balasanAdapter != null){

                    CustomItem item = new CustomItem(iv.getCurrentDate(FormatItem.formatTime), text);
                    saveInfoStok(sender, text);
                    //balasanAdapter.addData(item);
                }

            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }

    private static void saveInfoStok(final String sender, final String balasan){

        String tipe = "1";
        if(kode.equals("TC")) tipe = "2";

        dialogBox.showDialog(false);
        JSONObject jBody = new JSONObject();
        try {
            jBody.put("sender", sender);
            jBody.put("balasan", balasan);
            jBody.put("tipe", tipe);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        ApiVolley request = new ApiVolley(context, jBody, "POST", ServerURL.saveInfoStok, new ApiVolley.VolleyCallback() {
            @Override
            public void onSuccess(String result) {

                dialogBox.dismissDialog();
                try {
                    JSONObject response = new JSONObject(result);
                    String status = response.getJSONObject("metadata").getString("status");
                    String message = response.getJSONObject("metadata").getString("message");

                    if(status.equals("200")){

                        getHistoryBalasan();
                    }else{

                        Toast.makeText(context, message, Toast.LENGTH_LONG).show();
                    }

                } catch (JSONException e) {
                    e.printStackTrace();

                    View.OnClickListener clickListener = new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {

                            dialogBox.dismissDialog();
                            saveInfoStok(sender,balasan);
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
                        saveInfoStok(sender, balasan);

                    }
                };

                dialogBox.showDialog(clickListener, "Ulangi Proses", "Terjadi kesalahan saat mengambil data");
            }
        });
    }

    @Override
    protected void onDestroy() {
        isActive = false;
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
