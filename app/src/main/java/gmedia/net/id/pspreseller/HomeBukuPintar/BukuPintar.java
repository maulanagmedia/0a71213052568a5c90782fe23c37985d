package gmedia.net.id.pspreseller.HomeBukuPintar;

import android.app.Activity;
import android.app.SearchManager;
import android.content.Context;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.SearchView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.AbsListView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ProgressBar;

import com.maulana.custommodul.ApiVolley;
import com.maulana.custommodul.CustomItem;
import com.maulana.custommodul.CustomView.DialogBox;
import com.maulana.custommodul.ItemValidation;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import gmedia.net.id.pspreseller.HomeBukuPintar.Adapter.ListBukuPintarAdapter;
import gmedia.net.id.pspreseller.R;
import gmedia.net.id.pspreseller.Utils.ServerURL;

public class BukuPintar extends AppCompatActivity {

    private ItemValidation iv = new ItemValidation();
    private Context context;
    private View footerList;
    private int start = 0, count = 10;
    private String keyword = "";
    private ListView lvBuku;
    private ProgressBar pbLoading;
    private Button btnRefresh;
    private boolean isLoading = false;
    private ListBukuPintarAdapter adapter;
    private List<CustomItem> masterList, moreList;
    private DialogBox dialogBox;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_buku_pintar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(getResources().getDrawable(R.mipmap.ic_down));

        getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN
        );

        context = this;

        setTitle("Buku Pintar");

        initUI();
    }

    private void initUI() {

        lvBuku = (ListView) findViewById(R.id.lv_buku);
        pbLoading = (ProgressBar) findViewById(R.id.pb_loading);
        btnRefresh = (Button) findViewById(R.id.btn_refresh);
        LayoutInflater li = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        footerList = li.inflate(R.layout.footer_list, null);
        dialogBox = new DialogBox(context);

    }

    private void setAdapter(List<CustomItem> listItem) {

        lvBuku.setAdapter(null);

        if(listItem != null){

            adapter = new ListBukuPintarAdapter((Activity) context, listItem);
            lvBuku.setAdapter(adapter);

            lvBuku.setOnScrollListener(new AbsListView.OnScrollListener() {
                @Override
                public void onScrollStateChanged(AbsListView absListView, int i) {

                    int threshold = 1;
                    int countMerchant = lvBuku.getCount();

                    if (i == SCROLL_STATE_IDLE) {
                        if (lvBuku.getLastVisiblePosition() >= countMerchant - threshold && !isLoading) {

                            isLoading = true;
                            lvBuku.addFooterView(footerList);
                            start += count;
                            getMoreData();
                            //Log.i(TAG, "onScroll: last ");
                        }
                    }
                }

                @Override
                public void onScroll(AbsListView absListView, int i, int i1, int i2) {

                }
            });

        }
    }

    private void getMoreData() {

        isLoading = true;

        JSONObject jBody = new JSONObject();
        try {
            jBody.put("start", String.valueOf(start));
            jBody.put("count", String.valueOf(count));
            jBody.put("keyword", keyword);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        ApiVolley request = new ApiVolley(context, jBody, "POST", ServerURL.getBukuPintar, new ApiVolley.VolleyCallback() {
            @Override
            public void onSuccess(String result) {

                lvBuku.removeFooterView(footerList);
                isLoading = false;
                try {
                    JSONObject response = new JSONObject(result);
                    String status = response.getJSONObject("metadata").getString("status");
                    moreList = new ArrayList<>();
                    if(status.equals("200")){

                        JSONArray jsonArray = response.getJSONArray("response");
                        for(int i = 0; i < jsonArray.length(); i ++){
                            JSONObject jo = jsonArray.getJSONObject(i);
                            moreList.add(
                                    new CustomItem(jo.getString("id"),
                                            jo.getString("judul"),
                                            jo.getString("keterangan")));
                        }

                        if(adapter != null) adapter.addMoreData(moreList);

                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onError(String result) {

                lvBuku.removeFooterView(footerList);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();

        isLoading = false;
        getData();
    }

    private void getData() {

        isLoading = true;
        start = 0;
        dialogBox.showDialog(false);

        JSONObject jBody = new JSONObject();
        try {
            jBody.put("start", String.valueOf(start));
            jBody.put("count", String.valueOf(count));
            jBody.put("keyword", keyword);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        ApiVolley request = new ApiVolley(context, jBody, "POST", ServerURL.getBukuPintar, new ApiVolley.VolleyCallback() {
            @Override
            public void onSuccess(String result) {

                isLoading = false;
                dialogBox.dismissDialog();
                try {
                    JSONObject response = new JSONObject(result);
                    String status = response.getJSONObject("metadata").getString("status");
                    masterList = new ArrayList<>();
                    if(status.equals("200")){

                        JSONArray jsonArray = response.getJSONArray("response");
                        for(int i = 0; i < jsonArray.length(); i ++){
                            JSONObject jo = jsonArray.getJSONObject(i);
                            masterList.add(
                                    new CustomItem(jo.getString("id"),
                                            jo.getString("judul"),
                                            jo.getString("keterangan")));
                        }

                        setAdapter(masterList);

                    }else{
                        setAdapter(null);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    setAdapter(null);
                }
            }

            @Override
            public void onError(String result) {

                dialogBox.dismissDialog();

                View.OnClickListener clickListener = new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        dialogBox.dismissDialog();
                        getData();
                    }
                };

                dialogBox.showDialog(clickListener, "Ulangi Proses", "Terjadi kesalahan, harap ulangi proses");
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.search_menu, menu);
        MenuItem searchItem = menu.findItem(R.id.menu_search);
        final SearchManager searchManager =
                (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView =
                (SearchView) MenuItemCompat.getActionView(searchItem);

        searchView.setSearchableInfo(
                searchManager.getSearchableInfo(getComponentName()));
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String queryText) {

                start = 0;
                keyword = queryText;
                iv.hideSoftKey(context);
                getData();
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                String newFilter = !TextUtils.isEmpty(newText) ? newText : "";
                if(newText.length() == 0){

                    start = 0;
                    keyword = "";
                    getData();
                }

                return true;
            }
        });

        MenuItemCompat.OnActionExpandListener expandListener = new MenuItemCompat.OnActionExpandListener() {
            @Override
            public boolean onMenuItemActionExpand(MenuItem menuItem) {
                return true;
            }

            @Override
            public boolean onMenuItemActionCollapse(MenuItem menuItem) {

                return true;
            }
        };
        MenuItemCompat.setOnActionExpandListener(searchItem, expandListener);
        return super.onCreateOptionsMenu(menu);
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
