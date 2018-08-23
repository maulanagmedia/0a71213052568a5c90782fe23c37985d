package gmedia.net.id.pspreseller.SideInfoPSP;

import android.app.Activity;
import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.maulana.custommodul.ApiVolley;
import com.maulana.custommodul.CustomItem;
import com.maulana.custommodul.CustomView.DialogBox;
import com.maulana.custommodul.SessionManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import gmedia.net.id.pspreseller.R;
import gmedia.net.id.pspreseller.SideInfoPSP.Adapter.ListSPVAdapter;
import gmedia.net.id.pspreseller.Utils.ServerURL;

public class InformasiPSP extends AppCompatActivity {

    private Context context;
    private SessionManager session;
    private DialogBox dialogBox;
    private TextView tvSales, tvNomorSales, tvSPV, tvNomorSPV, tvBM, tvNomorBM, tvAlamatKantor, tvNomorTCare, tvNomorBoadcast;
    private ProgressBar pbLoading;
    private ListView lvSPV;
    private ListSPVAdapter adapter;
    private List<CustomItem> listSPV;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_informasi_psp);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN
        );

        setTitle("Informasi PSP");

        context = this;
        session = new SessionManager(context);

        initUI();
    }

    private void initUI() {

        tvSales = (TextView) findViewById(R.id.tv_sales);
        tvNomorSales = (TextView) findViewById(R.id.tv_nomor_sales);
        lvSPV = (ListView) findViewById(R.id.lv_spv);
        tvBM = (TextView) findViewById(R.id.tv_bm);
        tvNomorBM = (TextView) findViewById(R.id.tv_nomor_bm);
        tvAlamatKantor = (TextView) findViewById(R.id.tv_alamat_kantor);
        tvNomorTCare = (TextView) findViewById(R.id.tv_nomor_tcare);
        tvNomorBoadcast = (TextView) findViewById(R.id.tv_nomor_broadcast);
        pbLoading = (ProgressBar) findViewById(R.id.pb_loading);

        getData();
    }

    private void getData() {

        pbLoading.setVisibility(View.VISIBLE);
        final ApiVolley request = new ApiVolley(context, new JSONObject(), "GET", ServerURL.getPSPInformastion, new ApiVolley.VolleyCallback() {
            @Override
            public void onSuccess(String result) {

                pbLoading.setVisibility(View.GONE);
                try {
                    JSONObject response = new JSONObject(result);
                    String status = response.getJSONObject("metadata").getString("status");
                    String message = response.getJSONObject("metadata").getString("message");
                    if(status.equals("200")){

                        JSONObject jo = response.getJSONObject("response");
                        tvSales.setText(jo.getJSONObject("sales").getString("nama_sales"));
                        tvNomorSales.setText(jo.getJSONObject("sales").getString("nomor_sales"));

                        listSPV = new ArrayList<>();
                        JSONArray jaSPV = jo.getJSONArray("supervisor");
                        for(int i = 0; i < jaSPV.length();i++){

                            JSONObject joSPV = jaSPV.getJSONObject(i);
                            listSPV.add(new CustomItem(
                                    joSPV.getString("nama_spv"),
                                    joSPV.getString("telp_spv")));
                        }

                        lvSPV.setAdapter(null);
                        adapter = new ListSPVAdapter((Activity) context, listSPV);
                        lvSPV.setAdapter(adapter);

                        JSONArray jaBM = jo.getJSONArray("bm");
                        for(int j = 0; j < jaBM.length();j++){

                            JSONObject joBM = jaBM.getJSONObject(j);
                            tvBM.setText(joBM.getString("nama_bm"));
                            tvNomorBM.setText(joBM.getString("telp_bm"));
                        }

                        tvAlamatKantor.setText(jo.getString("alamat"));
                        tvNomorTCare.setText(jo.getString("tcare"));
                        tvNomorBoadcast.setText(jo.getString("broadcast"));

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

                pbLoading.setVisibility(View.GONE);
                Toast.makeText(context, "Terjadi kesalahan saat memuat data", Toast.LENGTH_LONG).show();
            }
        });
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
