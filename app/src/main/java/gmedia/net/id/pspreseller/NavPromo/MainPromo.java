package gmedia.net.id.pspreseller.NavPromo;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ProgressBar;

import com.maulana.custommodul.ApiVolley;
import com.maulana.custommodul.CustomItem;
import com.maulana.custommodul.ItemValidation;
import com.maulana.custommodul.SessionManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import gmedia.net.id.pspreseller.NavPromo.Adapter.ListPromoAdapter;
import gmedia.net.id.pspreseller.R;
import gmedia.net.id.pspreseller.Utils.ServerURL;

public class MainPromo extends Fragment {

    private Context context;
    private View layout;
    private SessionManager session;
    private ItemValidation iv = new ItemValidation();
    private ListView lvPromo;
    private ProgressBar pbLoading;
    private Button btnRefresh;
    private List<CustomItem> listPromo, moreList;
    private ListPromoAdapter adapter;
    private int start = 0;
    private int count = 10;
    private String TAG = "TAG";
    private View footerList;
    private boolean isLoading = false;

    public MainPromo() {
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
        layout =  inflater.inflate(R.layout.fragment_main_promo, container, false);
        context = getContext();
        session = new SessionManager(context);

        initUI();
        return layout;
    }

    private void initUI() {

        lvPromo = (ListView) layout.findViewById(R.id.lv_promo);
        pbLoading = (ProgressBar) layout.findViewById(R.id.pb_loading);
        btnRefresh = (Button) layout.findViewById(R.id.btn_refresh);
        LayoutInflater li = (LayoutInflater) ((Activity)context).getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        footerList = li.inflate(R.layout.footer_list, null);

        initEvent();

        getData();
    }

    private void initEvent() {

    }

    private void getData() {

        isLoading = true;
        JSONObject jBody = new JSONObject();
        start = 0;
        try {
            jBody.put("start", String.valueOf(start));
            jBody.put("count", String.valueOf(count));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        ApiVolley request = new ApiVolley(context, jBody, "POST", ServerURL.getPromosi, new ApiVolley.VolleyCallback() {
            @Override
            public void onSuccess(String result) {

                isLoading = false;
                try {
                    JSONObject response = new JSONObject(result);
                    String status = response.getJSONObject("metadata").getString("status");
                    listPromo = new ArrayList<>();
                    if(status.equals("200")){

                        JSONArray jsonArray = response.getJSONArray("response");
                        for(int i = 0; i < jsonArray.length(); i ++){
                            JSONObject jo = jsonArray.getJSONObject(i);
                            listPromo.add(
                                    new CustomItem(jo.getString("id"),
                                            jo.getString("title"),
                                            jo.getString("tgl_selesai"),
                                            jo.getString("image"),
                                            jo.getString("link"),
                                            jo.getString("deskripsi")));
                        }

                        getTableList(listPromo);

                    }else{
                        getTableList(null);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    getTableList(null);
                }
            }

            @Override
            public void onError(String result) {

                isLoading = false;
                Log.d(TAG, "onError: "+result);
            }
        });
    }

    private void getTableList(List<CustomItem> listItem) {

        lvPromo.addFooterView(footerList);
        lvPromo.setAdapter(null);
        lvPromo.removeFooterView(footerList);

        if(listItem != null && listItem.size() >0){

            adapter = new ListPromoAdapter((Activity) context, listItem);
            lvPromo.setAdapter(adapter);

            lvPromo.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                    CustomItem item = (CustomItem) adapterView.getItemAtPosition(i);

                    Intent intent = new Intent(context, DetailPromo.class);
                    intent.putExtra("title", item.getItem2());
                    intent.putExtra("image", item.getItem4());
                    intent.putExtra("link", item.getItem5());
                    intent.putExtra("keterangan", item.getItem6());
                    startActivity(intent);
                    ((Activity) context).overridePendingTransition(R.anim.slide_in_right,R.anim.slide_out_right);
                }
            });

            lvPromo.setOnScrollListener(new AbsListView.OnScrollListener() {
                @Override
                public void onScrollStateChanged(AbsListView absListView, int i) {

                    int threshold = 1;
                    int countMerchant = lvPromo.getCount();

                    if (i == SCROLL_STATE_IDLE) {
                        if (lvPromo.getLastVisiblePosition() >= countMerchant - threshold && !isLoading) {

                            isLoading = true;
                            lvPromo.addFooterView(footerList);
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
        } catch (JSONException e) {
            e.printStackTrace();
        }

        ApiVolley request = new ApiVolley(context, jBody, "POST", ServerURL.getPromosi, new ApiVolley.VolleyCallback() {
            @Override
            public void onSuccess(String result) {

                lvPromo.removeFooterView(footerList);
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
                                            jo.getString("title"),
                                            jo.getString("tgl_selesai"),
                                            jo.getString("image")));
                        }

                        if(adapter != null) adapter.addMoreData(moreList);

                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onError(String result) {

                isLoading = false;
                lvPromo.removeFooterView(footerList);
                Log.d(TAG, "onError: "+result);
            }
        });
    }
}
