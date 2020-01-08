package gmedia.net.id.pspreseller.SideProfile;

import android.content.Context;
import android.content.Intent;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.maulana.custommodul.ApiVolley;
import com.maulana.custommodul.CustomView.DialogBox;
import com.maulana.custommodul.ImageUtils;
import com.maulana.custommodul.ItemValidation;
import com.maulana.custommodul.SessionManager;

import org.json.JSONException;
import org.json.JSONObject;

import gmedia.net.id.pspreseller.CSChat.ChatSales;
import gmedia.net.id.pspreseller.R;
import gmedia.net.id.pspreseller.Utils.ServerURL;

public class ProfileActivity extends AppCompatActivity {

    private Context context;
    private ItemValidation iv = new ItemValidation();
    private SessionManager session;
    private TextView tvNamaOutlet, tvAlamat, tvNomor, tvNamaOwner;
    private DialogBox dialogBox;
    private ImageView ivProfile;
    private TextView tvVerifikasi;
    private LinearLayout llMaps;
    private String latitude = "", longitude = "";
    private String photo = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN
        );

        setTitle("Profile Outlet");

        context = this;

        initUI();
    }

    private void initUI() {

        ivProfile = (ImageView) findViewById(R.id.iv_profile);
        tvNamaOutlet = (TextView) findViewById(R.id.tv_nama_outlet);
        tvNamaOwner = (TextView) findViewById(R.id.tv_nama_owner);
        tvAlamat = (TextView) findViewById(R.id.tv_alamat);
        tvNomor = (TextView) findViewById(R.id.tv_nomor);
        tvVerifikasi = (TextView) findViewById(R.id.tv_verifikasi);
        llMaps = (LinearLayout) findViewById(R.id.ll_maps);
        dialogBox = new DialogBox(context);
        photo = "";

        tvVerifikasi.setText(Html.fromHtml("<font color='#ffffff'>Bila ada kesalahan informasi, silahkan hubungi kami </font><font color='blue'>disini</font>"));

        initEvent();

        getDataProfile();
    }

    private void initEvent() {

        tvVerifikasi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(context, ChatSales.class);
                startActivity(intent);
            }
        });

        llMaps.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(context, MapsOutlet.class);
                intent.putExtra("nama", tvNamaOutlet.getText().toString());
                intent.putExtra("latitude", latitude);
                intent.putExtra("longitude", longitude);
                intent.putExtra("photo", photo);
                startActivity(intent);
            }
        });
    }

    private void getDataProfile() {

        dialogBox.showDialog(false);

        final ApiVolley request = new ApiVolley(context, new JSONObject(), "GET", ServerURL.getProfile, new ApiVolley.VolleyCallback() {
            @Override
            public void onSuccess(String result) {

                dialogBox.dismissDialog();
                try {
                    JSONObject response = new JSONObject(result);
                    String status = response.getJSONObject("metadata").getString("status");
                    if(status.equals("200")){

                        JSONObject jo = response.getJSONObject("response");
                        tvNamaOutlet.setText(jo.getString("nama"));
                        tvNamaOwner.setText(jo.getString("namapemilik"));
                        tvAlamat.setText(jo.getString("alamat"));
                        tvNomor.setText(jo.getString("nohp"));

                        latitude = jo.getString("latitude");
                        longitude = jo.getString("longitude");

                        ImageUtils iu = new ImageUtils();
                        photo = jo.getString("image");
                        iu.LoadRealImage(context, photo, ivProfile);

                    }

                } catch (JSONException e) {
                    e.printStackTrace();

                    View.OnClickListener clickListener = new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {

                            dialogBox.dismissDialog();
                            getDataProfile();

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
                        getDataProfile();

                    }
                };

                dialogBox.showDialog(clickListener, "Ulangi Proses", "Terjadi kesalahan saat mengambil data");
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
