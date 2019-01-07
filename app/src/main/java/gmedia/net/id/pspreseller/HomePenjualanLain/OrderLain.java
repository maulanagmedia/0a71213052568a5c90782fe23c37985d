package gmedia.net.id.pspreseller.HomePenjualanLain;

import android.app.Activity;
import android.content.Context;
import android.graphics.Point;
import android.os.Build;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Display;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.maulana.custommodul.ApiVolley;
import com.maulana.custommodul.CustomItem;
import com.maulana.custommodul.CustomView.DialogBox;
import com.maulana.custommodul.EndlessScroll;
import com.maulana.custommodul.ItemValidation;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import gmedia.net.id.pspreseller.HomePenjualanLain.Adapter.KategoriListAdapter;
import gmedia.net.id.pspreseller.R;
import gmedia.net.id.pspreseller.Utils.ServerURL;

public class OrderLain extends AppCompatActivity {

    private RecyclerView rvKategori;
    private List<CustomItem> listKategori = new ArrayList<>();
    private ItemValidation iv = new ItemValidation();
    private Context context;
    private KategoriListAdapter adapter;
    private int start = 0, count = 10;
    private String TAG = "tes";
    private boolean isLoading = false;
    private DialogBox dialogBox;
    private EditText edtSearch;
    private ProgressBar pbLoading;
    private String keyword = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_lain);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(getResources().getDrawable(R.mipmap.ic_down));

        getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN
        );

        setTitle("Penjualan Lain");
        context = this;
        dialogBox = new DialogBox(context);

        initUI();
        initEvent();
        initData();
    }

    private void initUI() {

        rvKategori = (RecyclerView) findViewById(R.id.rv_kategori);
        edtSearch = (EditText) findViewById(R.id.edt_search);
        pbLoading = (ProgressBar) findViewById(R.id.pb_loading);

        start = 0;
        isLoading = false;
        keyword = "";

        setKategoriAdapter();
    }

    private void initEvent() {
        edtSearch.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {

                if(i == EditorInfo.IME_ACTION_SEARCH){

                    keyword = edtSearch.getText().toString();
                    start = 0;
                    listKategori.clear();
                    initData();

                    iv.hideSoftKey(context);
                    return true;
                }

                return false;
            }
        });
    }

    private void setKategoriAdapter(){

        Display display = ((Activity)context).getWindowManager().getDefaultDisplay();
        Point size = new Point();
        try {

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                display.getRealSize(size);
            }else {
                display.getSize(size);
            }
        } catch (NoSuchMethodError err) {
            display.getSize(size);
        }

        int jumlahKolom = 4;

        int menuWidth = 0;
        double menuFloat = (size.x) / jumlahKolom;
        menuWidth = (int) menuFloat;

        int jmlBaris = (int)(Math.ceil((double) listKategori.size() / jumlahKolom));

        //rvKategori.setLayoutParams(new RelativeLayout.LayoutParams(rvKategori.getLayoutParams().width, (((size.x - iv.dpToPx(context, 32)) / 4 * jmlBaris))));

        adapter = new KategoriListAdapter(context, listKategori, menuWidth);

        RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(context, jumlahKolom);
        rvKategori.setLayoutManager(mLayoutManager);
//        rvListMenu.addItemDecoration(new NavMenu.GridSpacingItemDecoration(2, dpToPx(10), true));
        rvKategori.setItemAnimator(new DefaultItemAnimator());
        rvKategori.setAdapter(adapter);

        EndlessScroll scrollListener = new EndlessScroll((GridLayoutManager) mLayoutManager) {
                @Override
                public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
                    start += count;
                    Log.d(TAG, "onLoadMore: ");
                }

            };

        rvKategori.addOnScrollListener(scrollListener);
    }

    private void initData() {

        isLoading = true;
        if(start == 0) dialogBox.showDialog(true);
        pbLoading.setVisibility(View.VISIBLE);
        JSONObject jBody = new JSONObject();
        try {
            jBody.put("start", String.valueOf(start));
            jBody.put("count", String.valueOf(count));
            jBody.put("keyword", keyword);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        ApiVolley request = new ApiVolley(context, jBody, "POST", ServerURL.getKategoriPPOB, new ApiVolley.VolleyCallback() {
            @Override
            public void onSuccess(String result) {

                pbLoading.setVisibility(View.GONE);
                if(start == 0) dialogBox.dismissDialog();
                String message = "Terjadi kesalahan saat memuat data, mohon coba kembali";
                isLoading = false;

                try {
                    JSONObject response = new JSONObject(result);
                    String status = response.getJSONObject("metadata").getString("status");
                    message = response.getJSONObject("metadata").getString("message");

                    if(status.equals("200")){

                        JSONArray jsonArray = response.getJSONArray("response");
                        for(int i = 0; i < jsonArray.length(); i ++){
                            JSONObject jo = jsonArray.getJSONObject(i);
                            listKategori.add(
                                    new CustomItem(
                                            jo.getString("id")
                                            ,jo.getString("kategori")
                                            ,jo.getString("image")
                                            ,jo.getString("custom")
                                    ));
                        }

                    }else{
                        if(start == 0) DialogBox.showDialog(context, 3, message);
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

                adapter.notifyDataSetChanged();
            }

            @Override
            public void onError(String result) {

                pbLoading.setVisibility(View.GONE);
                isLoading = false;
                if(start == 0) dialogBox.dismissDialog();
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
