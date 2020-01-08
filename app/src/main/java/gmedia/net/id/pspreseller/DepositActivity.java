package gmedia.net.id.pspreseller;

import android.app.DatePickerDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

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

import gmedia.net.id.pspreseller.Adapter.ListDepositAdapter;
import gmedia.net.id.pspreseller.Utils.ServerURL;

public class DepositActivity extends AppCompatActivity {

    private DepositActivity context;
    private EditText edtTanggalDari, edtTanggalSampai;
    private ImageView ivTanggalDari, ivTanggalSampai, ivNext;
    private ItemValidation iv = new ItemValidation();
    private SessionManager session;
    private ListView lvDeposit;
    private ProgressBar pbLoading;
    private Button btnRefresh;
    private List<CustomItem> listDeposit;
    private ListDepositAdapter adapter;
    private String dateFrom = "";
    private String dateTo = "";
    private DialogBox dialogBox;
    private TextView tvTotal;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_deposit);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        //getSupportActionBar().setHomeAsUpIndicator(getResources().getDrawable(R.mipmap.ic_down));

        getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN
        );

        setTitle("Riwayat Deposit");

        context = this;

        initUI();
    }

    private void initUI() {

        edtTanggalDari =(EditText) findViewById(R.id.edt_tgl_dari);
        edtTanggalSampai =(EditText) findViewById(R.id.edt_tgl_sampai);
        ivTanggalDari = (ImageView) findViewById(R.id.iv_tgl_dari);
        ivTanggalSampai = (ImageView) findViewById(R.id.iv_tgl_sampai);
        ivNext = (ImageView) findViewById(R.id.iv_next);
        lvDeposit = (ListView) findViewById(R.id.lv_deposit);
        pbLoading = (ProgressBar) findViewById(R.id.pb_loading);
        btnRefresh = (Button) findViewById(R.id.btn_refresh);
        tvTotal =(TextView) findViewById(R.id.tv_total);

        dateFrom = iv.sumDate(iv.getCurrentDate(FormatItem.formatDateDisplay), -7, FormatItem.formatDateDisplay) ;
        dateTo = iv.getCurrentDate(FormatItem.formatDateDisplay);

        edtTanggalDari.setText(dateFrom);
        edtTanggalSampai.setText(dateTo);

        session = new SessionManager(context);
        dialogBox = new DialogBox(context);

        initEvent();
        getData();
    }

    private void initEvent() {

        ivTanggalDari.setOnClickListener(new View.OnClickListener() {
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
                        edtTanggalDari.setText(dateFrom);
                    }
                };

                SimpleDateFormat yearOnly = new SimpleDateFormat("yyyy");
                new DatePickerDialog(context ,date , iv.parseNullInteger(yearOnly.format(dateValue)),dateValue.getMonth(),dateValue.getDate()).show();
            }
        });

        ivTanggalSampai.setOnClickListener(new View.OnClickListener() {
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
                        edtTanggalSampai.setText(dateTo);
                    }
                };

                SimpleDateFormat yearOnly = new SimpleDateFormat("yyyy");
                new DatePickerDialog(context ,date , iv.parseNullInteger(yearOnly.format(dateValue)),dateValue.getMonth(),dateValue.getDate()).show();
            }
        });

        ivNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                dateFrom = edtTanggalDari.getText().toString();
                dateTo = edtTanggalSampai.getText().toString();
                getData();
            }
        });
    }

    private void getData() {

        pbLoading.setVisibility(View.VISIBLE);
        JSONObject jBody = new JSONObject();
        try {
            jBody.put("tgl_mulai", dateFrom);
            jBody.put("tgl_selesai", dateTo);
            jBody.put("nomor", session.getUsername());
        } catch (JSONException e) {
            e.printStackTrace();
        }

        ApiVolley request = new ApiVolley(context, jBody, "POST", ServerURL.viewDeposit, new ApiVolley.VolleyCallback() {
            @Override
            public void onSuccess(String result) {

                pbLoading.setVisibility(View.GONE);
                try {
                    JSONObject response = new JSONObject(result);
                    String status = response.getJSONObject("metadata").getString("status");
                    String message= response.getJSONObject("metadata").getString("message");
                    listDeposit = new ArrayList<>();
                    double total = 0;

                    if(status.equals("200")){

                        JSONArray jArray = response.getJSONArray("response");
                        for(int i = 0; i < jArray.length(); i++){

                            JSONObject jo = jArray.getJSONObject(i);
                            listDeposit.add(new CustomItem(jo.getString("id"),
                                    //jo.getString("debit").equals("0") ? "Kredit" : "Debit",
                                    jo.getString("tgl"),
                                    jo.getString("nama_sales"),
                                    jo.getString("debit").equals("0") ? jo.getString("kredit") : jo.getString("debit")));

                            total += iv.parseNullDouble(jo.getString("kredit"));
                        }
                    }

                    tvTotal.setText(iv.ChangeToRupiahFormat(total));
                    setAdapter(listDeposit);
                } catch (JSONException e) {
                    e.printStackTrace();
                    setAdapter(null);
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

                setAdapter(null);
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

    private void setAdapter(List<CustomItem> listItem) {

        lvDeposit.setAdapter(null);
        if(listItem != null){

            adapter = new ListDepositAdapter(context, listDeposit);
            lvDeposit.setAdapter(adapter);
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