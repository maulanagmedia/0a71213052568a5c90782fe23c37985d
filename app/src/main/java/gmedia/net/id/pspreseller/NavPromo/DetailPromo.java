package gmedia.net.id.pspreseller.NavPromo;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.maulana.custommodul.CustomView.DialogBox;
import com.maulana.custommodul.ImageUtils;
import com.maulana.custommodul.ItemValidation;
import com.maulana.custommodul.SessionManager;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;

import gmedia.net.id.pspreseller.MainActivity;
import gmedia.net.id.pspreseller.R;

public class DetailPromo extends AppCompatActivity {

    private Context context;
    private SessionManager session;
    private ItemValidation iv =  new ItemValidation();
    private String jenis = ""; //event, promo
    private String id = "";
    private ImageView ivLogo;
    private TextView tvTitle, tvDecl, tvLink;
    private String dariNotif;
    private DialogBox dialogBox;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_promo);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN
        );

        context = this;
        session = new SessionManager(context);

        initUI();
    }

    private void initUI() {

        ivLogo = (ImageView) findViewById(R.id.iv_logo);
        tvTitle = (TextView) findViewById(R.id.tv_title);
        tvDecl = (TextView) findViewById(R.id.tv_desc);
        tvLink = (TextView) findViewById(R.id.tv_link);

        int[] display = iv.getScreenResolution(context);

        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(display[0], display[0]);
        ivLogo.setLayoutParams(lp);
        getDetailPromo();

    }

    private void getDetailPromo(){

        Bundle bundle = getIntent().getExtras();

        if(bundle != null){

            String title = bundle.getString("title","");
            final String gambar = bundle.getString("image","");
            String link = bundle.getString("link","");
            String keterangan = bundle.getString("keterangan","");

            tvTitle.setText(title);
            ImageUtils iu = new ImageUtils();
            iu.LoadRealImage(context, gambar, ivLogo);
            ivLogo.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    if(gambar.length() > 0){
                        new DownloadFileFromURL().execute(gambar);
                    /*Intent intent = new Intent();
                    intent.setAction(Intent.ACTION_VIEW);
                    intent.setDataAndType(Uri.parse(gambar), "image*//**//**//**//*");
                    context.startActivity(intent);*/
                    }
                }
            });

            tvDecl.setText(keterangan);
            tvLink.setText(link);

            if(link.length() > 0){

                if (!link.toLowerCase().startsWith("http://") && !link.toLowerCase().startsWith("https://")) {
                    link = "http://" + link;
                }

                final String finalLink = link;

                tvLink.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(finalLink));
                        context.startActivity(browserIntent);
                    }
                });
            }
        }
    }

    class DownloadFileFromURL extends AsyncTask<String, String, String> {

        private File f;

        /**
         * Before starting background thread
         * Show Progress Bar Dialog
         * */
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            showDialog();
        }

        /**
         * Downloading file in background thread
         * */
        @Override
        protected String doInBackground(String... f_url) {
            int count;
            try {
                URL url = new URL(f_url[0]);
                URLConnection conection = url.openConnection();
                conection.connect();
                // this will be useful so that you can show a tipical 0-100% progress bar
                int lenghtOfFile = conection.getContentLength();

                // download the file
                f = new File(Environment.getExternalStorageDirectory() + File.separator + "downloadedfile.jpg");
                InputStream input = new BufferedInputStream(url.openStream(), 8192);

                // Output stream
                OutputStream output = new FileOutputStream("/sdcard/downloadedfile.jpg");

                byte data[] = new byte[1024];

                long total = 0;

                while ((count = input.read(data)) != -1) {
                    total += count;
                    // publishing the progress....
                    // After this onProgressUpdate will be called
                    publishProgress(""+(int)((total*100)/lenghtOfFile));

                    // writing data to file
                    output.write(data, 0, count);
                }

                // flushing output
                output.flush();

                // closing streams
                output.close();
                input.close();

            } catch (Exception e) {
                Log.e("Error: ", e.getMessage());
            }

            return null;
        }

        /**
         * Updating progress bar
         * */
        protected void onProgressUpdate(String... progress) {
            // setting progress percentage
            progressDialog.setProgress(Integer.parseInt(progress[0]));
        }

        /**
         * After completing background task
         * Dismiss the progress dialog
         * **/
        @Override
        protected void onPostExecute(String file_url) {
            // dismiss the dialog after the file was downloaded
            dismissDialog();

            // Displaying downloaded image into image view
            // Reading image path from sdcard
            String imagePath = String.valueOf(FileProvider.getUriForFile(context, context.getPackageName() + ".provider", f));
            // setting downloaded into image view
            Intent intent = new Intent();
            intent.setAction(Intent.ACTION_VIEW);
            intent.setDataAndType(Uri.parse(imagePath), "image/*");
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_GRANT_READ_URI_PERMISSION);
            context.startActivity(intent);
        }
        private void showDialog(){
            progressDialog = new ProgressDialog(context);
            progressDialog.setMessage("Downloading file. Please wait...");
            progressDialog.setIndeterminate(false);
            progressDialog.setMax(100);
            progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
            progressDialog.setCancelable(true);
            progressDialog.show();
        }

        private void dismissDialog(){
            progressDialog.dismiss();
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
        overridePendingTransition(android.R.anim.slide_in_left,android.R.anim.slide_out_right);
    }
}
