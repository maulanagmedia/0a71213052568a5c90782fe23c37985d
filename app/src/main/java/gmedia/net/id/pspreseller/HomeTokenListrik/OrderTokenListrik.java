package gmedia.net.id.pspreseller.HomeTokenListrik;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;

import com.maulana.custommodul.ItemValidation;
import com.maulana.custommodul.OptionItem;

import java.util.ArrayList;
import java.util.List;

import gmedia.net.id.pspreseller.R;

public class OrderTokenListrik extends AppCompatActivity {

    private ItemValidation iv = new ItemValidation();
    private Spinner spNominal;
    private List<OptionItem> listNominal;
    private RadioGroup rgJenis;
    private RadioButton rbPascaBayar, rbPrabayar;
    private EditText edtMeteran;
    private TextView tvJumlahHarga;
    private Button btnProses;
    private boolean isPascaBayara;
    private LinearLayout llNominal;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_token_listrik);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(getResources().getDrawable(R.mipmap.ic_down));

        getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN
        );

        setTitle("Bayar Token Listrik");

        initUI();
    }

    private void initUI() {

        rgJenis = (RadioGroup) findViewById(R.id.rg_jenis);
        rbPascaBayar = (RadioButton) findViewById(R.id.rb_pasca_bayar);
        rbPrabayar = (RadioButton) findViewById(R.id.rb_prabayar);
        edtMeteran = (EditText) findViewById(R.id.edt_meteran);
        spNominal = (Spinner) findViewById(R.id.sp_nominal);
        tvJumlahHarga = (TextView) findViewById(R.id.tv_jumlah_harga);
        btnProses = (Button) findViewById(R.id.btn_proses);
        llNominal = (LinearLayout) findViewById(R.id.ll_nominal);

        getNominal();

        initEvent();

        changeRadioStatus();
    }

    private void initEvent() {

        rgJenis.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {

                changeRadioStatus();
            }
        });

    }

    private void changeRadioStatus(){


        if(rbPascaBayar.isChecked()){

            isPascaBayara = true;
            llNominal.setVisibility(View.INVISIBLE);
        }else{

            isPascaBayara = false;
            llNominal.setVisibility(View.VISIBLE);
        }
    }

    private void getNominal() {

        listNominal = new ArrayList<>();
        listNominal.add(new OptionItem("20000", "Rp 20.000"));
        listNominal.add(new OptionItem("50000", "Rp 50.000"));
        listNominal.add(new OptionItem("100000", "Rp 100.000"));
        listNominal.add(new OptionItem("200000", "Rp 200.000"));
        listNominal.add(new OptionItem("500000", "Rp 500.000"));
        listNominal.add(new OptionItem("1000000", "Rp 1.000.000"));
        listNominal.add(new OptionItem("2000000", "Rp 2.000.000"));

        setNoRSAdapter(listNominal);
    }

    private void setNoRSAdapter(List<OptionItem> listItem) {

        if(listItem != null && listItem.size() > 0){

            ArrayAdapter adapter = new ArrayAdapter(OrderTokenListrik.this, R.layout.layout_simple_list, listItem);
            spNominal.setAdapter(adapter);

            spNominal.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }
            });

            spNominal.setSelection(0);
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
