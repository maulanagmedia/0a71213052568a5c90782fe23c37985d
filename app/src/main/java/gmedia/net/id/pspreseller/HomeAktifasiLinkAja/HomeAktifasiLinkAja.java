package gmedia.net.id.pspreseller.HomeAktifasiLinkAja;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.fxn.pix.Options;
import com.fxn.pix.Pix;
import com.fxn.utility.ImageQuality;
import com.maulana.custommodul.ApiVolley;
import com.maulana.custommodul.CustomView.DialogBox;
import com.maulana.custommodul.ImageUtils;
import com.maulana.custommodul.ItemValidation;
import com.maulana.custommodul.SessionManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

import gmedia.net.id.pspreseller.R;
import gmedia.net.id.pspreseller.Utils.ServerURL;

public class HomeAktifasiLinkAja extends AppCompatActivity {

    private Activity context;
    private SessionManager session;
    private TextView tvNomor;
    private TextView tvStatus;
    private ImageView ivKTP, ivSelfie;
    private Button btnKTP, btnSelfie;
    private Button btnProses;
    private final int CODE_UPLOAD_KTP = 101;
    private final int CODE_UPLOAD_SELFIE = 102;
    private Bitmap bitmapKTP = null, bitmapSelfie = null;
    private ProgressDialog dialog;
    private int statusUploadKTP = 0, statusUploadSelfi = 0;
    private File sourceFileKTP, sourceFileSelfi;
    private String filePathURIKTP, filePathURISelfi;
    private int totalSizeKTP = 0, totalSizeSelfi = 0;
    private ItemValidation iv = new ItemValidation();
    private String token0 = "", token1 = "", token2 = "", token3 = "", token4 = "";
    private String idAktivasi = "";
    private DialogBox dialogBox;
    private ImageUtils iu = new ImageUtils();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_aktifasi_link_aja);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(getResources().getDrawable(R.mipmap.ic_down));

        getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN
        );

        context = this;
        dialogBox = new DialogBox(context);
        session = new SessionManager(context);

        setTitle("Beli Stok MKIOS");

        initUI();
        initEvent();
        initData();
    }

    private void initUI() {

        tvNomor = (TextView) findViewById(R.id.tv_nomor);
        tvNomor.setText(session.getUsername());
        tvStatus = (TextView) findViewById(R.id.tv_status);
        ivKTP = (ImageView) findViewById(R.id.iv_ktp);
        ivSelfie = (ImageView) findViewById(R.id.iv_selfie);
        btnKTP = (Button) findViewById(R.id.btn_ktp);
        btnSelfie = (Button) findViewById(R.id.btn_selfie);
        btnProses = (Button) findViewById(R.id.btn_proses);

    }

    private void initData() {

        dialogBox.showDialog(true);
        ApiVolley request = new ApiVolley(context, new JSONObject(), "GET", ServerURL.getAktivasiLinkAja, new ApiVolley.VolleyCallback() {
            @Override
            public void onSuccess(String result) {

                dialogBox.dismissDialog();
                String message = "Terjadi kesalahan saat memuat data, mohon coba kembali";

                try {
                    JSONObject response = new JSONObject(result);
                    String status = response.getJSONObject("metadata").getString("status");
                    message = response.getJSONObject("metadata").getString("message");

                    if(status.equals("200")){

                        JSONObject jo = response.getJSONObject("response");
                        tvStatus.setText(jo.getString("status_text"));
                        iu.LoadRealImage(context, jo.getString("foto_ktp"), ivKTP);
                        iu.LoadRealImage(context, jo.getString("foto_selfi"), ivSelfie);

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

            }

            @Override
            public void onError(String result) {

                dialogBox.dismissDialog();
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

    private void initEvent() {

        btnKTP.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Options options = Options.init()
                        .setRequestCode(CODE_UPLOAD_KTP)
                        .setCount(10)
                        .setFrontfacing(false)
                        .setImageQuality(ImageQuality.HIGH)
                        .setScreenOrientation(Options.SCREEN_ORIENTATION_PORTRAIT)
                        .setPath("/PSPTypeR");
                Pix.start(HomeAktifasiLinkAja.this, options);
            }
        });

        btnSelfie.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Options options = Options.init()
                        .setRequestCode(CODE_UPLOAD_SELFIE)
                        .setCount(10)
                        .setFrontfacing(false)
                        .setImageQuality(ImageQuality.HIGH)
                        .setScreenOrientation(Options.SCREEN_ORIENTATION_PORTRAIT)
                        .setPath("/PSPTypeR");
                Pix.start(HomeAktifasiLinkAja.this, options);
            }
        });

        btnProses.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                AlertDialog dialog = new AlertDialog.Builder(context)
                        .setTitle("Konfirmasi")
                        .setMessage("Apakah anda yakin ingin menyimpan data?")
                        .setNegativeButton("Tidak", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        })
                        .setPositiveButton("Ya", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                                saveData();
                            }
                        })
                        .show();
            }
        });
    }

    private void saveData() {

        new UploadFileToServer().execute();
    }

    private class UploadFileToServer extends AsyncTask<String, String, String> {
        @Override
        protected void onPreExecute() {
            // setting progress bar to zero
            //pbLoading.setVisibility(View.VISIBLE);
            //pbLoading.setProgress(0);
            statusUploadKTP = 1;
            dialog = new ProgressDialog(context);
            dialog.setMessage("Uploading...");
            dialog.setIndeterminate(false);
            dialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
            dialog.setProgress(0);
            dialog.setCancelable(false);
            dialog.setButton(DialogInterface.BUTTON_POSITIVE, "Batal", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    final AlertDialog dialogConf = new AlertDialog.Builder(context)
                            .setIcon(R.mipmap.ic_launcher)
                            .setTitle("Konfirmasi")
                            .setMessage("Anda yakin ingin membatalkan proses")
                            .setPositiveButton("Ya", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {

                                    statusUploadKTP = 0;
                                    dialog.dismiss();
                                }
                            })
                            .setNegativeButton("Tidak", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    dialog.show();
                                }
                            })
                            .show();
                }
            });
            dialog.show();

            //uploader_area.setVisibility(View.GONE); // Making the uploader area screen invisible
            //progress_area.setVisibility(View.VISIBLE); // Showing the stylish material progressbar
            sourceFileKTP = new File(filePathURIKTP);
            totalSizeKTP = (int) sourceFileKTP.length();
            super.onPreExecute();
        }

        @Override
        protected void onProgressUpdate(String... progress) {
            //Log.d("PROG", progress[0]);
            //pbLoading.setProgress(Integer.parseInt(progress[0])); //Updating progress
            dialog.setProgress(Integer.parseInt(progress[0]));
        }

        @Override
        protected String doInBackground(String... args) {
            HttpURLConnection.setFollowRedirects(false);
            HttpURLConnection connection = null;
            String fileName = sourceFileKTP.getName();
            //Log.d(TAG, "filename upload: " +fileName);
            String result = "";

            session = new SessionManager(context);
            token0  = iv.encodeMD5(iv.encodeBase64(iv.getCurrentDate("SSSHHMMddmmyyyyss")));
            token1  = session.getKdcus();
            token2  = iv.getCurrentDate("SSSHHyyyyssMMddmm");
            token3  = iv.sha256(token1+"&"+token2,token1+"die");
            token4  = iv.sha256(token0 + token1 + "&"+token2,token1+"live");

            try {

                connection = (HttpURLConnection) new URL(ServerURL.uploadFileURLKTP).openConnection();
                connection.setRequestMethod("POST");
                String boundary = "---------------------------boundary";
                String tail = "\r\n--" + boundary + "--\r\n";
                connection.setRequestProperty("Content-Type", "multipart/form-data; boundary=" + boundary);
                connection.setRequestProperty("Client-Service", "frontend-client");
                connection.setRequestProperty("Auth-Key", "gmedia_psp_reseller");
                connection.setRequestProperty("token0", token0);
                connection.setRequestProperty("token1", token1);
                connection.setRequestProperty("token2", token2);
                connection.setRequestProperty("token3", token3);
                connection.setRequestProperty("token4", token4);
                connection.setRequestProperty("nomor", session.getUsername());
                connection.setDoOutput(true);

                String metadataPart = "--" + boundary + "\r\n"
                        + "Content-Disposition: form-data; name=\"metadata\"\r\n\r\n"
                        + "" + "\r\n";

                String fileHeader1 = "--" + boundary + "\r\n"
                        + "Content-Disposition: form-data; name=\"fileToUpload\"; filename=\""
                        + fileName + "\"\r\n"
                        + "Content-Type: application/octet-stream\r\n"
                        + "Content-Transfer-Encoding: binary\r\n";

                long fileLength = sourceFileKTP.length() + tail.length();
                String fileHeader2 = "Content-length: " + fileLength + "\r\n";
                String fileHeader = fileHeader1 + fileHeader2 + "\r\n";
                String stringData = metadataPart + fileHeader;

                long requestLength = stringData.length() + fileLength;
                connection.setRequestProperty("Content-length", "" + requestLength);
                connection.setFixedLengthStreamingMode((int) requestLength);
                connection.connect();

                DataOutputStream out = new DataOutputStream(connection.getOutputStream());
                out.writeBytes(stringData);
                out.flush();

                int progress = 0;
                int bytesRead = 0;
                byte buf[] = new byte[1024];
                BufferedInputStream bufInput = new BufferedInputStream(new FileInputStream(sourceFileKTP));
                while ((bytesRead = bufInput.read(buf)) != -1) {
                    // write output
                    out.write(buf, 0, bytesRead);
                    out.flush();
                    progress += bytesRead; // Here progress is total uploaded bytes

                    publishProgress(""+(int)((progress*100)/totalSizeKTP)); // sending progress percent to publishProgress
                }

                // Write closing boundary and close stream
                out.writeBytes(tail);
                out.flush();
                out.close();

                // Get server response
                BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                String line = "";
                StringBuilder builder = new StringBuilder();
                while((line = reader.readLine()) != null) {
                    builder.append(line);
                    result = line;
                }

            } catch (Exception e) {
                // Exception
            } finally {
                if (connection != null) connection.disconnect();
            }
            return result;
        }

        @Override
        protected void onPostExecute(String result) {

            try {
                dialog.dismiss();
            } catch(Exception e) {
            }

            try {
                result = result.replace("</div>", "");
                JSONObject response = new JSONObject(result);
                String status = response.getJSONObject("metadata").getString("status");
                String message = response.getJSONObject("metadata").getString("message");
                Toast.makeText(context, message, Toast.LENGTH_LONG).show();
                if(status.equals("200")){

                    if(statusUploadKTP == 1){

                        idAktivasi = response.getJSONObject("response").getString("id");
                        new UploadFileToServerSelfi().execute();
                    }
                    //berhasil
                }else{
                    Toast.makeText(context, message, Toast.LENGTH_LONG).show();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

            super.onPostExecute(result);
        }

    }

    private class UploadFileToServerSelfi extends AsyncTask<String, String, String> {
        @Override
        protected void onPreExecute() {
            // setting progress bar to zero
            //pbLoading.setVisibility(View.VISIBLE);
            //pbLoading.setProgress(0);
            statusUploadSelfi = 1;
            dialog = new ProgressDialog(context);
            dialog.setMessage("Uploading...");
            dialog.setIndeterminate(false);
            dialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
            dialog.setProgress(0);
            dialog.setCancelable(false);
            dialog.setButton(DialogInterface.BUTTON_POSITIVE, "Batal", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    final AlertDialog dialogConf = new AlertDialog.Builder(context)
                            .setIcon(R.mipmap.ic_launcher)
                            .setTitle("Konfirmasi")
                            .setMessage("Anda yakin ingin membatalkan proses")
                            .setPositiveButton("Ya", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {

                                    statusUploadSelfi = 0;
                                    dialog.dismiss();
                                }
                            })
                            .setNegativeButton("Tidak", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    dialog.show();
                                }
                            })
                            .show();
                }
            });
            dialog.show();

            //uploader_area.setVisibility(View.GONE); // Making the uploader area screen invisible
            //progress_area.setVisibility(View.VISIBLE); // Showing the stylish material progressbar
            sourceFileSelfi = new File(filePathURISelfi);
            totalSizeSelfi = (int) sourceFileSelfi.length();
            super.onPreExecute();
        }

        @Override
        protected void onProgressUpdate(String... progress) {
            //Log.d("PROG", progress[0]);
            //pbLoading.setProgress(Integer.parseInt(progress[0])); //Updating progress
            dialog.setProgress(Integer.parseInt(progress[0]));
        }

        @Override
        protected String doInBackground(String... args) {
            HttpURLConnection.setFollowRedirects(false);
            HttpURLConnection connection = null;
            String fileName = sourceFileSelfi.getName();
            //Log.d(TAG, "filename upload: " +fileName);
            String result = "";

            session = new SessionManager(context);
            token0  = iv.encodeMD5(iv.encodeBase64(iv.getCurrentDate("SSSHHMMddmmyyyyss")));
            token1  = session.getKdcus();
            token2  = iv.getCurrentDate("SSSHHyyyyssMMddmm");
            token3  = iv.sha256(token1+"&"+token2,token1+"die");
            token4  = iv.sha256(token0 + token1 + "&"+token2,token1+"live");

            try {

                connection = (HttpURLConnection) new URL(ServerURL.uploadFileURLSelfi).openConnection();
                connection.setRequestMethod("POST");
                String boundary = "---------------------------boundary";
                String tail = "\r\n--" + boundary + "--\r\n";
                connection.setRequestProperty("Content-Type", "multipart/form-data; boundary=" + boundary);
                connection.setRequestProperty("Client-Service", "frontend-client");
                connection.setRequestProperty("Auth-Key", "gmedia_psp_reseller");
                connection.setRequestProperty("token0", token0);
                connection.setRequestProperty("token1", token1);
                connection.setRequestProperty("token2", token2);
                connection.setRequestProperty("token3", token3);
                connection.setRequestProperty("token4", token4);
                connection.setRequestProperty("nomor", session.getUsername());
                connection.setRequestProperty("id", idAktivasi);
                connection.setDoOutput(true);

                String metadataPart = "--" + boundary + "\r\n"
                        + "Content-Disposition: form-data; name=\"metadata\"\r\n\r\n"
                        + "" + "\r\n";

                String fileHeader1 = "--" + boundary + "\r\n"
                        + "Content-Disposition: form-data; name=\"fileToUpload\"; filename=\""
                        + fileName + "\"\r\n"
                        + "Content-Type: application/octet-stream\r\n"
                        + "Content-Transfer-Encoding: binary\r\n";

                long fileLength = sourceFileSelfi.length() + tail.length();
                String fileHeader2 = "Content-length: " + fileLength + "\r\n";
                String fileHeader = fileHeader1 + fileHeader2 + "\r\n";
                String stringData = metadataPart + fileHeader;

                long requestLength = stringData.length() + fileLength;
                connection.setRequestProperty("Content-length", "" + requestLength);
                connection.setFixedLengthStreamingMode((int) requestLength);
                connection.connect();

                DataOutputStream out = new DataOutputStream(connection.getOutputStream());
                out.writeBytes(stringData);
                out.flush();

                int progress = 0;
                int bytesRead = 0;
                byte buf[] = new byte[1024];
                BufferedInputStream bufInput = new BufferedInputStream(new FileInputStream(sourceFileSelfi));
                while ((bytesRead = bufInput.read(buf)) != -1) {
                    // write output
                    out.write(buf, 0, bytesRead);
                    out.flush();
                    progress += bytesRead; // Here progress is total uploaded bytes

                    publishProgress(""+(int)((progress*100)/totalSizeSelfi)); // sending progress percent to publishProgress
                }

                // Write closing boundary and close stream
                out.writeBytes(tail);
                out.flush();
                out.close();

                // Get server response
                BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                String line = "";
                StringBuilder builder = new StringBuilder();
                while((line = reader.readLine()) != null) {
                    builder.append(line);
                    result = line;
                }

            } catch (Exception e) {
                // Exception
            } finally {
                if (connection != null) connection.disconnect();
            }
            return result;
        }

        @Override
        protected void onPostExecute(String result) {

            try {
                dialog.dismiss();
            } catch(Exception e) {
            }

            try {
                result = result.replace("</div>", "");
                JSONObject response = new JSONObject(result);
                String status = response.getJSONObject("metadata").getString("status");
                String message = response.getJSONObject("metadata").getString("message");
                Toast.makeText(context, message, Toast.LENGTH_LONG).show();
                if(status.equals("200")){

                    if(statusUploadSelfi == 1){

                        //getChatData();
                    }
                    //berhasil
                }else{
                    Toast.makeText(context, message, Toast.LENGTH_LONG).show();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

            super.onPostExecute(result);
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == Activity.RESULT_OK && requestCode == CODE_UPLOAD_KTP){

            ArrayList<String> returnValue = data.getStringArrayListExtra(Pix.IMAGE_RESULTS);
            if(returnValue!=null){
                for(String s : returnValue){
                    try {
                        bitmapKTP = MediaStore.Images.Media.getBitmap(getContentResolver(),
                                Uri.fromFile(new File(s)));
                        filePathURIKTP = s;
                        ivKTP.setImageBitmap(bitmapKTP);
                    } catch (IOException e) {
                        e.printStackTrace();
                        if (e.getMessage()!=null){
                            Log.e("_log", e.getMessage());
                        }
                    }
                }
            }
        }else if (resultCode == Activity.RESULT_OK && requestCode == CODE_UPLOAD_SELFIE){

            ArrayList<String> returnValue = data.getStringArrayListExtra(Pix.IMAGE_RESULTS);
            if(returnValue!=null){
                for(String s : returnValue){
                    try {
                        bitmapSelfie = MediaStore.Images.Media.getBitmap(getContentResolver(),
                                Uri.fromFile(new File(s)));
                        filePathURISelfi = s;
                        ivSelfie.setImageBitmap(bitmapSelfie);
                    } catch (IOException e) {
                        e.printStackTrace();
                        if (e.getMessage()!=null){
                            Log.e("_log", e.getMessage());
                        }
                    }
                }
            }
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
