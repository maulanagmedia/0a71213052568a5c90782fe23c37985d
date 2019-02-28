package gmedia.net.id.pspreseller.NavHome;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Point;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.maulana.custommodul.ApiVolley;
import com.maulana.custommodul.CustomItem;
import com.maulana.custommodul.CustomView.DialogBox;
import com.maulana.custommodul.EndlessScroll;
import com.maulana.custommodul.ItemValidation;
import com.maulana.custommodul.SessionManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import gmedia.net.id.pspreseller.CustomView.WrapContentViewPager;
import gmedia.net.id.pspreseller.HomeActivity;
import gmedia.net.id.pspreseller.HomeBeliPerdana.DetailKeranjangPerdana;
import gmedia.net.id.pspreseller.HomeBeliPerdana.ListBarangPerdana;
import gmedia.net.id.pspreseller.HomeBukuPintar.BukuPintar;
import gmedia.net.id.pspreseller.HomeBulk.OrderBulk;
import gmedia.net.id.pspreseller.HomeInfoStok.ActInfoStok;
import gmedia.net.id.pspreseller.HomeInfoStok.DetailInfoStok;
import gmedia.net.id.pspreseller.HomeJualPerdana.DetailJualPerdana;
import gmedia.net.id.pspreseller.HomeMkios.OrderMKIOS;
import gmedia.net.id.pspreseller.HomePenjualanLain.Adapter.KategoriListAdapter;
import gmedia.net.id.pspreseller.HomePenjualanLain.OrderLain;
import gmedia.net.id.pspreseller.HomePreorderPerdana.ListBarangPreorder;
import gmedia.net.id.pspreseller.HomePulsa.OrderPulsa;
import gmedia.net.id.pspreseller.HomeTcash.OrderTcash;
import gmedia.net.id.pspreseller.HomeTokenListrik.OrderTokenListrik;
import gmedia.net.id.pspreseller.MainActivity;
import gmedia.net.id.pspreseller.NavHome.Adapter.HeaderSliderAdapter;
import gmedia.net.id.pspreseller.R;
import gmedia.net.id.pspreseller.Utils.ServerURL;

import static android.content.Context.LAYOUT_INFLATER_SERVICE;

public class MainHome extends Fragment implements ViewPager.OnPageChangeListener{

    private Context context;
    private View layout;
    private LinearLayout llLine1, llLine2, llLine3, llLine4;
    private SessionManager session;
    private ItemValidation iv = new ItemValidation();
    private WrapContentViewPager vpHeaderSlider;
    private LinearLayout llPagerIndicator;
    private List<CustomItem> sliderList;
    private int dotsCount;
    private ImageView[] dots;
    private HeaderSliderAdapter mAdapter;
    private boolean firstLoad = true;
    private int changeHeaderTimes = 5;
    private Timer timer;
    private LinearLayout llMkios, llBulk, llTcash, llTokenListrik
            , llPulsa, llInfoStok, llStokMkios, llStokTcash, llStokPPOB
            , llBukuPintar, llBeliPerdana, llPreorderPerdana, llJualPerdana;
    private String TAG = "HOME";
    private String pin = "", flagPin = "";
    private DialogBox dialogBox;
    private RecyclerView rvKategori;
    private List<CustomItem> listKategori = new ArrayList<>();
    private KategoriListAdapter adapter;
    private int start = 0, count = 1000;
    private boolean isLoading = false;
    private RelativeLayout rlHeaderSlide;

    public MainHome() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        layout = inflater.inflate(R.layout.fragment_main_home, container, false);
        context = getContext();

        firstLoad = true;
        initUI();

        return layout;
    }

    private void initUI() {

        // Header
        vpHeaderSlider = (WrapContentViewPager) layout.findViewById(R.id.pager_introduction);
        vpHeaderSlider.setScrollDurationFactor(4);
        llPagerIndicator = (LinearLayout) layout.findViewById(R.id.ll_view_pager_dot_count);

        llLine1 = (LinearLayout) layout.findViewById(R.id.ll_line_1);
        llLine2 = (LinearLayout) layout.findViewById(R.id.ll_line_2);
        llLine3 = (LinearLayout) layout.findViewById(R.id.ll_line_3);
        llLine4 = (LinearLayout) layout.findViewById(R.id.ll_line_4);

        llMkios = (LinearLayout) layout.findViewById(R.id.ll_mkios);
        llBulk = (LinearLayout) layout.findViewById(R.id.ll_bulk);
        llTcash = (LinearLayout) layout.findViewById(R.id.ll_tcash);
        llTokenListrik = (LinearLayout) layout.findViewById(R.id.ll_token_listrik);
        llPulsa = (LinearLayout) layout.findViewById(R.id.ll_pulsa);
        llInfoStok = (LinearLayout) layout.findViewById(R.id.ll_info_stok);
        llStokMkios = (LinearLayout) layout.findViewById(R.id.ll_info_stok_mkios);
        llStokTcash = (LinearLayout) layout.findViewById(R.id.ll_info_stok_tcash);
        llStokPPOB = (LinearLayout) layout.findViewById(R.id.ll_info_stok_ppob);
        llBukuPintar = (LinearLayout) layout.findViewById(R.id.ll_buku_pintar);
        llBeliPerdana = (LinearLayout) layout.findViewById(R.id.ll_perdana);
        llPreorderPerdana = (LinearLayout) layout.findViewById(R.id.ll_preorder_perdana);
        llJualPerdana = (LinearLayout) layout.findViewById(R.id.ll_jual_perdana);
        rlHeaderSlide = (RelativeLayout) layout.findViewById(R.id.rl_header_slide);

        rvKategori = (RecyclerView) layout.findViewById(R.id.rv_kategori);

        session = new SessionManager(context);
        dialogBox = new DialogBox(context);

        int[] dimension = iv.getScreenResolution(context);

        int heightLine = (dimension[0] / 3);

        int heightHeader = (dimension[0] * 400 / 720);

        LinearLayout.LayoutParams lpHeader = (LinearLayout.LayoutParams) rlHeaderSlide.getLayoutParams();
        lpHeader.height = heightHeader;
        rlHeaderSlide.setLayoutParams(lpHeader);

        LinearLayout.LayoutParams l1LayoutParams1 = (LinearLayout.LayoutParams) llLine1.getLayoutParams();
        LinearLayout.LayoutParams l1LayoutParams2 = (LinearLayout.LayoutParams) llLine2.getLayoutParams();
        LinearLayout.LayoutParams l1LayoutParams3 = (LinearLayout.LayoutParams) llLine3.getLayoutParams();
        LinearLayout.LayoutParams l1LayoutParams4 = (LinearLayout.LayoutParams) llLine4.getLayoutParams();

        l1LayoutParams1.height = heightLine;
        l1LayoutParams2.height = heightLine;
        l1LayoutParams3.height = heightLine;
        l1LayoutParams4.height = heightLine;

        llLine1.setLayoutParams(l1LayoutParams1);
        llLine2.setLayoutParams(l1LayoutParams2);
        llLine3.setLayoutParams(l1LayoutParams3);
        llLine4.setLayoutParams(l1LayoutParams4);

        getListHeaderSlider();

        initEvent();

        getDataPin();

        setKategoriAdapter();
    }

    private void initEvent() {

        llMkios.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(context, OrderMKIOS.class);
                startActivity(intent);
                ((Activity) context).overridePendingTransition(R.anim.slide_in_up, R.anim.slide_out_up);
            }
        });

        llBulk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(context, OrderBulk.class);
                startActivity(intent);
                ((Activity) context).overridePendingTransition(R.anim.slide_in_up, R.anim.slide_out_up);
            }
        });

        llTcash.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(context, OrderTcash.class);
                startActivity(intent);
                ((Activity) context).overridePendingTransition(R.anim.slide_in_up, R.anim.slide_out_up);
            }
        });

        llTokenListrik.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(context, OrderLain.class);
                startActivity(intent);
                ((Activity) context).overridePendingTransition(R.anim.slide_in_up, R.anim.slide_out_up);

            }
        });

        llPulsa.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(context, OrderPulsa.class);
                startActivity(intent);
                ((Activity) context).overridePendingTransition(R.anim.slide_in_up, R.anim.slide_out_up);

            }
        });

        llInfoStok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(context, ActInfoStok.class);
                startActivity(intent);
                ((Activity) context).overridePendingTransition(R.anim.slide_in_up, R.anim.slide_out_up);

            }
        });

        llStokMkios.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                getSaldoDetail("MK");
            }
        });

        llStokTcash.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                getSaldoDetail("TC");
            }
        });

        llStokPPOB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                getSaldoDetail("SD");
            }
        });

        llBukuPintar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(context, BukuPintar.class);
                startActivity(intent);
                ((Activity) context).overridePendingTransition(R.anim.slide_in_up, R.anim.slide_out_up);

            }
        });

        llBeliPerdana.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                DetailKeranjangPerdana.listKeranjang.clear();
                if(DetailKeranjangPerdana.adapter != null) DetailKeranjangPerdana.adapter.notifyDataSetChanged();
                Intent intent = new Intent(context, ListBarangPerdana.class);
                startActivity(intent);
                ((Activity) context).overridePendingTransition(R.anim.slide_in_up, R.anim.slide_out_up);
            }
        });

        llPreorderPerdana.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(context, ListBarangPreorder.class);
                startActivity(intent);
                ((Activity) context).overridePendingTransition(R.anim.slide_in_up, R.anim.slide_out_up);
            }
        });

        llJualPerdana.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(context, DetailJualPerdana.class);
                startActivity(intent);
                ((Activity) context).overridePendingTransition(R.anim.slide_in_up, R.anim.slide_out_up);
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

    private void getDataPin() {

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

                initData();
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

    private void getSaldoDetail(final String flag) {

        if(!HomeActivity.isAccessGranted){

            Toast.makeText(context, "Mohon aktifkan ijin akses terlebih dahulu", Toast.LENGTH_LONG).show();
            return;
        }

        JSONObject jBody = new JSONObject();
        try {
            jBody.put("kode", flag);
            jBody.put("nomor", session.getUsername());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        final ApiVolley request = new ApiVolley(context, jBody, "POST", ServerURL.checkSaldo, new ApiVolley.VolleyCallback() {
            @Override
            public void onSuccess(String result) {

                try {
                    JSONObject response = new JSONObject(result);
                    Log.d(TAG, "onSuccess: "+result);
                    String status = response.getJSONObject("metadata").getString("status");
                    String message = response.getJSONObject("metadata").getString("message");
                    if(status.equals("200")){

                        JSONObject jo = response.getJSONObject("response");
                        giveDecisionAfterGettingResponse(jo.getString("flag"),jo.getString("value"), flag);

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
                Toast.makeText(context, "Terjadi kesalahan saat memuat data", Toast.LENGTH_LONG).show();

            }
        });
    }

    private void giveDecisionAfterGettingResponse(final String flag, final String value, final String kode){

        if(value.toLowerCase().contains("[pin]")){ //having chip pin

            final AlertDialog.Builder builder = new AlertDialog.Builder(context);
            LayoutInflater inflater = (LayoutInflater) ((Activity)context).getSystemService(LAYOUT_INFLATER_SERVICE);
            View viewDialog = inflater.inflate(R.layout.dialog_pin, null);
            builder.setView(viewDialog);
            builder.setCancelable(false);

            final TextView tvTitle = (TextView) viewDialog.findViewById(R.id.tv_title);
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

            final AlertDialog alert = builder.create();
            alert.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

            tvTitle.setText("Masukkan Pin Chip");

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

                        edtPin.setError("Pin harap diisi");
                        edtPin.requestFocus();
                        return;
                    }else{

                        edtPin.setError(null);
                    }

                    if(alert != null) alert.dismiss();

                    if(cbSimpan.isChecked()) {

                        flagPin = "1";
                    }else{
                        flagPin = "0";
                    }

                    pin = edtPin.getText().toString();
                    savePin(value, flag, kode);
                }
            });

            alert.show();

        }else{

            Intent intent = new Intent(context, DetailInfoStok.class);
            intent.putExtra("flag", flag);
            intent.putExtra("value", value);
            intent.putExtra("kode", kode);
            startActivity(intent);
        }
    }

    private void savePin(final String value, final String flag, final String kode) {

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

                        String newValue = value.replace("[PIN]", pin);

                        Intent intent = new Intent(context, DetailInfoStok.class);
                        intent.putExtra("flag", flag);
                        intent.putExtra("value", newValue);
                        intent.putExtra("kode", kode);
                        startActivity(intent);
                    }else{

                        View.OnClickListener clickListener = new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {

                                dialogBox.dismissDialog();
                                savePin(value, flag, kode);
                            }
                        };

                        dialogBox.showDialog(clickListener, "Ulangi Proses", message);

                        Toast.makeText(context, message, Toast.LENGTH_LONG).show();
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

    //region Slider Header
    private void getListHeaderSlider() {

        ApiVolley request = new ApiVolley(context, new JSONObject(), "GET", ServerURL.getNews, new ApiVolley.VolleyCallback() {
            @Override
            public void onSuccess(String result) {

                JSONObject responseAPI;
                try {
                    responseAPI = new JSONObject(result);
                    String status = responseAPI.getJSONObject("metadata").getString("status");
                    sliderList = new ArrayList<>();

                    if(iv.parseNullInteger(status) == 200){

                        JSONArray jsonArray = responseAPI.getJSONArray("response");

                        for(int i = 0; i < jsonArray.length();i++){

                            JSONObject item = jsonArray.getJSONObject(i);

                            sliderList.add(new CustomItem(item.getString("id"), item.getString("image"), item.getString("keterangan"), item.getString("link")));
                        }

                    }

                    if(firstLoad){
                        setViewPagerTimer(changeHeaderTimes);
                        firstLoad = false;
                    }

                    setHeaderSlider();
                    setUiPageViewController();

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onError(String result) {

            }
        });
    }

    private void setHeaderSlider(){

        vpHeaderSlider.setAdapter(null);
        mAdapter = null;
        mAdapter = new HeaderSliderAdapter(context, sliderList);
        vpHeaderSlider.setAdapter(mAdapter);
        vpHeaderSlider.setCurrentItem(0);
        vpHeaderSlider.setOnPageChangeListener(this);
    }

    private void setUiPageViewController() {

        dotsCount = mAdapter.getCount();
        dots = new ImageView[dotsCount];
        llPagerIndicator.removeAllViews();

        for (int i = 0; i < dotsCount; i++) {
            dots[i] = new ImageView(context);
            dots[i].setImageDrawable(context.getResources().getDrawable(R.drawable.dot_unselected_item));

            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
            );

            params.setMargins(4, 0, 4, 0);

            llPagerIndicator.addView(dots[i], params);
        }

        dots[0].setImageDrawable(context.getResources().getDrawable(R.drawable.dot_selected_item));
    }

    private void setViewPagerTimer(int seconds){
        timer = new Timer(); // At this line a new Thread will be created
        timer.scheduleAtFixedRate(new RemindTask(), 0, seconds * 1000);
    }

    class RemindTask extends TimerTask {

        @Override
        public void run() {

            // As the TimerTask run on a seprate thread from UI thread we have
            // to call runOnUiThread to do work on UI thread.
            ((Activity) context).runOnUiThread(new Runnable() {
                public void run() {

                    if(vpHeaderSlider.getCurrentItem() == mAdapter.getCount() - 1){
                        vpHeaderSlider.setCurrentItem(0);

                    }else{
                        vpHeaderSlider.setCurrentItem(vpHeaderSlider.getCurrentItem() + 1);
                    }
                }
            });

        }
    }

    private void initData() {

        isLoading = true;
        if(start == 0) dialogBox.showDialog(true);
        JSONObject jBody = new JSONObject();
        try {
            jBody.put("start", String.valueOf(start));
            jBody.put("count", String.valueOf(count));
            jBody.put("keyword", "");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        ApiVolley request = new ApiVolley(context, jBody, "POST", ServerURL.getKategoriPPOB, new ApiVolley.VolleyCallback() {
            @Override
            public void onSuccess(String result) {

                if(start == 0) dialogBox.dismissDialog();
                String message = "Terjadi kesalahan saat memuat data, mohon coba kembali";
                isLoading = false;
                listKategori.clear();

                try {
                    JSONObject response = new JSONObject(result);
                    String status = response.getJSONObject("metadata").getString("status");
                    message = response.getJSONObject("metadata").getString("message");

                    listKategori.add(
                            new CustomItem(
                                    "pulsa"
                                    ,"Pulsa/Tcash"
                                    ,R.drawable.ic_jual_pulsa
                                    ,""
                                    ,"0"
                            ));

                    listKategori.add(
                            new CustomItem(
                                    "perdana"
                                    ,"Perdana"
                                    ,R.drawable.ic_jual_perdana
                                    ,""
                                    ,"0"
                            ));

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
                                            ,"1"
                                    ));
                        }

                    }else{
                        //if(start == 0) DialogBox.showDialog(context, 3, message);
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
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {

        for (int i = 0; i < dotsCount; i++) {
            dots[i].setImageDrawable(context.getResources().getDrawable(R.drawable.dot_unselected_item));
        }

        dots[position].setImageDrawable(context.getResources().getDrawable(R.drawable.dot_selected_item));
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }
}
