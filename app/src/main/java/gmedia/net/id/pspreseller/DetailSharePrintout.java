package gmedia.net.id.pspreseller;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.provider.MediaStore;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.maulana.custommodul.FormatItem;
import com.maulana.custommodul.ItemValidation;
import com.maulana.custommodul.SessionManager;

public class DetailSharePrintout extends AppCompatActivity {

    private Context context;
    private ItemValidation iv = new ItemValidation();
    private TextView tvTitle, tvNama, tvItem, tvToken, tvMsisdn, tvDenda, tvAdmin, tvHarga, tvDaya, tvPPN, tvTimestamp, tvTanggal;
    private Button btnBagikan;
    private LinearLayout llHeader;
    private SessionManager session;
    private String textKeterangan = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_share_printout);

        /*getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        //getSupportActionBar().setHomeAsUpIndicator(getResources().getDrawable(R.mipmap.ic_down));

        getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN
        );

        setTitle("Detail Order");*/
        context = this;
        session = new SessionManager(context);

        initUI();
        initEvent();
    }

    private void initEvent() {

        btnBagikan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                tvTimestamp.setText(iv.getCurrentDate(FormatItem.formatDateTime));

                String path = MediaStore.Images.Media.insertImage(getContentResolver(), getBitmapFromView(llHeader), "Image Description", null);
                Uri uri = Uri.parse(path);

                Intent intent = new Intent(Intent.ACTION_SEND);
                intent.setType("image/jpeg");
                intent.putExtra(Intent.EXTRA_STREAM, uri);
                intent.putExtra(android.content.Intent.EXTRA_TEXT, textKeterangan);
                startActivity(Intent.createChooser(intent, "Share Image"));
            }
        });
    }

    private void initUI() {

        llHeader = (LinearLayout) findViewById(R.id.ll_header);
        tvTitle = (TextView) findViewById(R.id.tv_title);
        tvNama = (TextView) findViewById(R.id.tv_nama);
        tvItem = (TextView) findViewById(R.id.tv_item);
        tvToken = (TextView) findViewById(R.id.tv_token);
        tvMsisdn = (TextView) findViewById(R.id.tv_msisdn);
        tvDenda = (TextView) findViewById(R.id.tv_denda);
        tvAdmin = (TextView) findViewById(R.id.tv_admin);
        tvHarga = (TextView) findViewById(R.id.tv_harga);
        tvDaya = (TextView) findViewById(R.id.tv_daya);
        tvTanggal = (TextView) findViewById(R.id.tv_tanggal);

        tvPPN = (TextView) findViewById(R.id.tv_ppn);
        tvTimestamp = (TextView) findViewById(R.id.tv_timestamp);

        btnBagikan = (Button) findViewById(R.id.btn_bagikan);

        Bundle bundle = getIntent().getExtras();
        if(bundle != null){

            tvTitle.setText(session.getNama());
            tvNama.setText(bundle.getString("nama", ""));
            tvItem.setText(bundle.getString("item", ""));
            tvTanggal.setText(bundle.getString("tanggal", ""));
            tvToken.setText(bundle.getString("token", ""));
            tvMsisdn.setText(bundle.getString("msisdn", ""));
            tvDenda.setText(bundle.getString("denda", ""));
            tvAdmin.setText(bundle.getString("admin", ""));
            tvHarga.setText(bundle.getString("harga", ""));
            tvDaya.setText(bundle.getString("daya", ""));
            tvPPN.setText(bundle.getString("ppn", ""));
            textKeterangan = bundle.getString("text", "");
            tvTimestamp.setText(iv.getCurrentDate(FormatItem.formatDateTime));
        }
    }

    private Bitmap getBitmapFromView(View view) {
        //Define a bitmap with the same size as the view
        Bitmap returnedBitmap = Bitmap.createBitmap(view.getWidth(), view.getHeight(),Bitmap.Config.ARGB_8888);
        //Bind a canvas to it
        Canvas canvas = new Canvas(returnedBitmap);
        //Get the view's background
        Drawable bgDrawable =view.getBackground();
        if (bgDrawable!=null)
            //has background drawable, then draw it on the canvas
            bgDrawable.draw(canvas);
        else
            //does not have background drawable, then draw white background on the canvas
            canvas.drawColor(Color.WHITE);
        // draw the view on the canvas
        view.draw(canvas);
        //return the bitmap
        return returnedBitmap;
    }

    /*@Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }*/

    @Override
    public void onBackPressed() {

        super.onBackPressed();
        overridePendingTransition(R.anim.slide_in_left,R.anim.slide_out_left);
    }
}
