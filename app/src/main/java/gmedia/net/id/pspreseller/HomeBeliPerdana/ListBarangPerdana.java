package gmedia.net.id.pspreseller.HomeBeliPerdana;

import android.app.Activity;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import androidx.core.view.MenuItemCompat;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.appcompat.widget.SearchView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ListView;

import com.maulana.custommodul.ApiVolley;
import com.maulana.custommodul.CustomItem;
import com.maulana.custommodul.CustomView.DialogBox;
import com.maulana.custommodul.ItemValidation;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import gmedia.net.id.pspreseller.HomeBeliPerdana.Adapter.ListPerdanaAdapter;
import gmedia.net.id.pspreseller.R;
import gmedia.net.id.pspreseller.Utils.ServerURL;

public class ListBarangPerdana extends AppCompatActivity {

    private ItemValidation iv = new ItemValidation();
    private int start = 0, count = 10;
    private String keyword = "";
    private Context context;
    private ListView lvPricelist;
    private boolean isLoading = false;
    private ListPerdanaAdapter adapter;
    private List<CustomItem> listBarang = new ArrayList<>();
    private View footerList;
    private DialogBox dialogBox;
    private SwipeRefreshLayout srlContainer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_barang_perdana);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(getResources().getDrawable(R.mipmap.ic_down));

        getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN
        );

        context = this;

        setTitle("Pilih Perdana");

        initUI();
        initEvent();
        initData();
    }

    private void initUI() {

        lvPricelist = (ListView) findViewById(R.id.lv_pricelist);
        srlContainer = (SwipeRefreshLayout) findViewById(R.id.srl_container);
        LayoutInflater li = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        footerList = li.inflate(R.layout.footer_list, null);
        dialogBox = new DialogBox(context);
        isLoading = false;

    }

    private void initEvent() {

        listBarang = new ArrayList<>();
        adapter = new ListPerdanaAdapter((Activity) context,listBarang);
        lvPricelist.addFooterView(footerList);
        lvPricelist.setAdapter(adapter);
        lvPricelist.removeFooterView(footerList);
        lvPricelist.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView absListView, int i) {

                int threshold = 1;
                int countMerchant = lvPricelist.getCount();

                if (i == SCROLL_STATE_IDLE) {
                    if (lvPricelist.getLastVisiblePosition() >= countMerchant - threshold && !isLoading) {

                        isLoading = true;
                        start += count;
                        initData();
                        //Log.i(TAG, "onScroll: last ");
                    }
                }
            }

            @Override
            public void onScroll(AbsListView absListView, int i, int i1, int i2) {

            }
        });

        srlContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {

                if(!isLoading){
                    start = 0;
                    listBarang.clear();
                    initData();
                }
            }
        });

        lvPricelist.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                CustomItem item = (CustomItem) adapterView.getItemAtPosition(i);
                Intent intent = new Intent(context, DetailBarangPerdana.class);
                intent.putExtra("kdbrg", item.getItem1());
                intent.putExtra("namabrg", item.getItem2());
                intent.putExtra("harga", item.getItem3());
                startActivity(intent);
            }
        });
    }

    private void initData() {

        isLoading = true;
        if(start == 0) dialogBox.showDialog(true);
        lvPricelist.addFooterView(footerList);
        JSONObject jBody = new JSONObject();
        try {
            jBody.put("start", String.valueOf(start));
            jBody.put("count", String.valueOf(count));
            jBody.put("keyword", keyword);
            jBody.put("mkios", "0");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        ApiVolley request = new ApiVolley(context, jBody, "POST", ServerURL.priceList, new ApiVolley.VolleyCallback() {
            @Override
            public void onSuccess(String result) {

                lvPricelist.removeFooterView(footerList);
                if(start == 0) dialogBox.dismissDialog();
                String message = "Terjadi kesalahan saat memuat data, mohon coba kembali";
                isLoading = false;
                srlContainer.setRefreshing(false);

                try {
                    JSONObject response = new JSONObject(result);
                    String status = response.getJSONObject("metadata").getString("status");
                    message = response.getJSONObject("metadata").getString("message");

                    if(status.equals("200")){

                        JSONArray jsonArray = response.getJSONArray("response");
                        for(int i = 0; i < jsonArray.length(); i ++){
                            JSONObject jo = jsonArray.getJSONObject(i);
                            listBarang.add(
                                    new CustomItem(jo.getString("kodebrg"),
                                            jo.getString("namabrg"),
                                            jo.getString("harga")));
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

                srlContainer.setRefreshing(false);
                lvPricelist.removeFooterView(footerList);
                isLoading = false;
                if(start == 0) dialogBox.dismissDialog();
                View.OnClickListener clickListener = new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        dialogBox.dismissDialog();
                        initData();
                    }
                };

                dialogBox.showDialog(clickListener, "Ulangi Proses", "Terjadi kesalahan saat memuat data, mohon coba kembali");
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
                listBarang.clear();
                keyword = queryText;
                iv.hideSoftKey(context);
                initData();
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                String newFilter = !TextUtils.isEmpty(newText) ? newText : "";
                if(newText.length() == 0){

                    start = 0;
                    listBarang.clear();
                    keyword = "";
                    initData();
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
