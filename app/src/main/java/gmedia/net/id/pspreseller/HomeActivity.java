package gmedia.net.id.pspreseller;

import android.*;
import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.ContactsContract;
import android.provider.Settings;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.leonardus.irfan.bluetoothprinter.PspPrinter;
import com.maulana.custommodul.ApiVolley;
import com.maulana.custommodul.CustomItem;
import com.maulana.custommodul.ImageUtils;
import com.maulana.custommodul.ItemValidation;
import com.maulana.custommodul.PermissionUtils;
import com.maulana.custommodul.RuntimePermissionsActivity;
import com.maulana.custommodul.SessionManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import gmedia.net.id.pspreseller.CSChat.ChatSales;
import gmedia.net.id.pspreseller.HomePulsa.OrderPulsa;
import gmedia.net.id.pspreseller.HomePulsa.Service.ServiceHandler;
import gmedia.net.id.pspreseller.NavHistory.MainHistory;
import gmedia.net.id.pspreseller.NavHome.MainHome;
import gmedia.net.id.pspreseller.NavPromo.MainPromo;
import gmedia.net.id.pspreseller.NavTransaksi.MainTransaksi;
import gmedia.net.id.pspreseller.PengaturanPin.UbahPinActivity;
import gmedia.net.id.pspreseller.PriceList.PricelistActivity;
import gmedia.net.id.pspreseller.SideChangePassword.ChangePassword;
import gmedia.net.id.pspreseller.SideInfoPSP.InformasiPSP;
import gmedia.net.id.pspreseller.SideProfile.ProfileActivity;
import gmedia.net.id.pspreseller.TopUP.IsiSaldo;
import gmedia.net.id.pspreseller.Utils.ServerURL;

public class HomeActivity extends RuntimePermissionsActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private static final int REQUEST_PERMISSIONS = 20;

    private static boolean doubleBackToExitPressedOnce;
    private boolean exitState = false;
    private int timerClose = 2000;

    private SessionManager session;
    private ItemValidation iv = new ItemValidation();
    private Button btnNavHome, btnNavTransaksi, btnNavHistory, btnNavPromo;
    //private ImageView ivMenu;
    private int state = 0;
    private Button btnTopup;
    private ImageView ivLogo;
    private TextView tvNamaOutlet, tvAlmatOutlet;
    private Context context;
    private TextView tvSaldo;
    public static int stateFragment = 0;
    public static List<CustomItem> listContact;

    private Cursor cursor;
    private int counter = 0;
    private Handler updateBarHandler;
    private ProgressDialog pDialog;
    private String version = "", latestVersion = "", link = "";
    private boolean updateRequired = false;

    public static boolean isAccessGranted = true;
    private String TAG  = "HOME";
    private AlertDialog dialogAccess;
    private AlertDialog builderVersion;
    private TextView tvVersion;
    private TabLayout tlMenu;
    private PspPrinter printer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        context = this;
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        printer = new PspPrinter(context);
        printer.startService();

        if (ContextCompat.checkSelfPermission(
                HomeActivity.this, android.Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(
                HomeActivity.this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(
                HomeActivity.this, Manifest.permission.WAKE_LOCK) != PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(
                HomeActivity.this, Manifest.permission.VIBRATE) != PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(
                HomeActivity.this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(
                HomeActivity.this, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(
                HomeActivity.this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(
                HomeActivity.this, Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(
                HomeActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(
                HomeActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            HomeActivity.super.requestAppPermissions(new
                            String[]{ android.Manifest.permission.READ_EXTERNAL_STORAGE,
                            android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
                            Manifest.permission.WAKE_LOCK,
                            Manifest.permission.VIBRATE,
                            Manifest.permission.CAMERA,
                            Manifest.permission.READ_PHONE_STATE,
                            Manifest.permission.CALL_PHONE,
                            Manifest.permission.READ_CONTACTS,
                            Manifest.permission.ACCESS_COARSE_LOCATION,
                            Manifest.permission.ACCESS_FINE_LOCATION
                    }, R.string
                            .runtime_permissions_txt
                    , REQUEST_PERMISSIONS);
        }

        //Check close statement
        doubleBackToExitPressedOnce = false;
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {

            if (bundle.getBoolean("exit", false)) {

                printer.stopService();
                exitState = true;
                finish();
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            }
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        initUI();
    }

    @Override
    public void onPermissionsGranted(int requestCode) {

        //getContacts();
    }

    @Override
    protected void onResume() {
        super.onResume();

        if(dialogAccess != null){
            if(dialogAccess.isShowing()) dialogAccess.dismiss();
        }

        OrderPulsa.isActive = false;

        /*isAccessGranted =  isAccessibilityEnabled(context.getPackageName() + "/" + context.getPackageName() + ".HomePulsa.Service.USSDService");
        if(isAccessGranted){

            //Log.d(TAG, "granted");
        }else{
            //Log.d(TAG, "not granted");
            *//*View rootView = getWindow().getDecorView().findViewById(android.R.id.content);
            Snackbar.make(rlContainer, "Mohon ijinkan akses pada "+ getResources().getString(R.string.app_name)+", Cari "+ getResources().getString(R.string.app_name)+" dan ubah enable",
                    Snackbar.LENGTH_INDEFINITE).setAction("OK",
                    new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            startActivity(new Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS));
                        }
                    }).show();*//*

            dialogAccess = new AlertDialog.Builder(context)
                    .setTitle("Konfirmasi")
                    .setMessage("Mohon ijinkan akses pada "+ getResources().getString(R.string.app_name)+", Cari "+ getResources().getString(R.string.app_name)+" dan ubah enable")
                    .setPositiveButton("Buka Akses", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {

                            startActivity(new Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS));
                        }
                    }).show();

        }


        ServiceHandler serviceHandler = new ServiceHandler((Activity) context);*/

        Bundle bundle = getIntent().getExtras();
        if(bundle != null){

            String stateString = bundle.getString("state", "");
            if(!stateString.isEmpty()) stateFragment = iv.parseNullInteger(stateString);
        }

        if(stateFragment != 0){

            TabLayout.Tab tab = tlMenu.getTabAt(stateFragment);
            tab.select();

            ChangeFragment(0);
            ChangeFragment(stateFragment);
            stateFragment = 0;
        }

        //tvSaldo.setText("Nomor Anda " + session.getUsername());
        getTotalDeposit();

        checkVersion();
    }

    public boolean isAccessibilityEnabled(String id){

        int accessibilityEnabled = 0;
        boolean accessibilityFound = false;
        try {
            accessibilityEnabled = Settings.Secure.getInt(this.getContentResolver(),android.provider.Settings.Secure.ACCESSIBILITY_ENABLED);
            Log.d(TAG, "ACCESSIBILITY: " + accessibilityEnabled);
        } catch (Settings.SettingNotFoundException e) {
            Log.d(TAG, "Error finding setting, default accessibility to not found: " + e.getMessage());
        }

        TextUtils.SimpleStringSplitter mStringColonSplitter = new TextUtils.SimpleStringSplitter(':');

        if (accessibilityEnabled==1){
            //Log.d(TAG, "***ACCESSIBILIY IS ENABLED***: ");

            String settingValue = Settings.Secure.getString(getContentResolver(), Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES);
            //Log.d(TAG, "Setting: " + settingValue);
            if (settingValue != null) {
                TextUtils.SimpleStringSplitter splitter = mStringColonSplitter;
                splitter.setString(settingValue);
                while (splitter.hasNext()) {
                    String accessabilityService = splitter.next();
                    //Log.d(TAG, "Setting: " + accessabilityService);
                    if (accessabilityService.toLowerCase().equals(id.toLowerCase())){
                        //Log.d(TAG, "We've found the correct setting - accessibility is switched on!");
                        return true;
                    }
                }
            }

            //Log.d(TAG, "***END***");
        }
        else{
            //Log.d(TAG, "***ACCESSIBILIY IS DISABLED***");
        }
        return accessibilityFound;
    }

    private void getTotalDeposit() {

        JSONObject jBody = new JSONObject();
        try {
            jBody.put("kode", "SD");
            jBody.put("nomor", session.getUsername());
        } catch (JSONException e) {
            e.printStackTrace();
        }

        ApiVolley request = new ApiVolley(context, jBody, "POST", ServerURL.checkSaldo, new ApiVolley.VolleyCallback() {
            @Override
            public void onSuccess(String result) {

                try {
                    JSONObject response = new JSONObject(result);
                    String status = response.getJSONObject("metadata").getString("status");
                    if(status.equals("200")){

                        String total = response.getJSONObject("response").getString("value");
                        tvSaldo.setText("Saldo Tunai " + iv.ChangeToRupiahFormat(total));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();

                }
            }

            @Override
            public void onError(String result) {

            }
        });
    }

    private void initUI() {

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        View headerView = navigationView.getHeaderView(0);
        //tvUsername = (TextView) headerView.findViewById(R.id.tv_username);

        tvSaldo = (TextView) findViewById(R.id.tv_saldo);

        btnNavHome = (Button) findViewById(R.id.btn_nav_home);
        btnNavTransaksi = (Button) findViewById(R.id.btn_nav_transaksi);
        btnNavHistory = (Button) findViewById(R.id.btn_nav_history);
        btnNavPromo = (Button) findViewById(R.id.btn_nav_promo);

        tlMenu = (TabLayout) findViewById(R.id.tl_menu);

        //ivMenu = (ImageView) findViewById(R.id.iv_menu);
        btnTopup = (Button) findViewById(R.id.btn_topup);

        session = new SessionManager(HomeActivity.this);

        if(!session.isLoggedIn()){

            Intent intent = new Intent(HomeActivity.this, LoginScreen.class);
            session.logoutUser(intent);
        }else{

            ivLogo = (ImageView) headerView.findViewById(R.id.iv_logo);
            tvNamaOutlet = (TextView) headerView.findViewById(R.id.tv_nama);
            tvAlmatOutlet = (TextView) headerView.findViewById(R.id.tv_alamat);
            tvVersion = (TextView) headerView.findViewById(R.id.tv_version);

            ImageUtils iu = new ImageUtils();
            iu.LoadCircleRealImage(HomeActivity.this, session.getImage(), ivLogo);
            tvNamaOutlet.setText(session.getNama());
            tvAlmatOutlet.setText(session.getAlamat());
        }

        setTitle("Home");
        state = 0;
        fragment = new MainHome();
        callFragment(HomeActivity.this, fragment);

        initEvent();

        // Baca contact
        /*updateBarHandler = new Handler();
        pDialog = new ProgressDialog(context);
        pDialog.setMessage("Membaca kontak");
        pDialog.setCancelable(false);
        try {
            pDialog.show();

        }catch (Exception e){
            e.printStackTrace();
        }

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        getContacts();
                    }
                });
            }
        }, 1000);*/
        /*if(PermissionUtils.hasPermissions(context, Manifest.permission.READ_CONTACTS)){
            if(listContact == null || listContact.size() > 0) getContacts();
        }*/
    }

    private void checkVersion(){

        PackageInfo pInfo = null;
        version = "";

        try {
            pInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        version = pInfo.versionName;
        //getSupportActionBar().setSubtitle(getResources().getString(R.string.app_name) + " v "+ version);
        tvVersion.setText(getResources().getString(R.string.app_name) + " v "+ version);
        latestVersion = "";
        link = "";

        ApiVolley request = new ApiVolley(context, new JSONObject(), "POST", ServerURL.getLatestVersion, new ApiVolley.VolleyCallback() {

            @Override
            public void onSuccess(String result) {

                JSONObject responseAPI;
                if(builderVersion != null){
                    if(builderVersion.isShowing()) builderVersion.dismiss();
                }
                try {
                    responseAPI = new JSONObject(result);
                    String status = responseAPI.getJSONObject("metadata").getString("status");

                    if(iv.parseNullInteger(status) == 200){
                        latestVersion = responseAPI.getJSONObject("response").getString("build_version");
                        link = responseAPI.getJSONObject("response").getString("link_update");
                        updateRequired = (iv.parseNullInteger(responseAPI.getJSONObject("response").getString("wajib")) == 1) ? true : false;

                        if(!version.trim().equals(latestVersion.trim()) && link.length() > 0){

                            if(updateRequired){

                                builderVersion = new AlertDialog.Builder(context)
                                        .setIcon(R.mipmap.ic_launcher)
                                        .setTitle("Update")
                                        .setMessage("Versi terbaru "+latestVersion+" telah tersedia, mohon download versi terbaru.")
                                        .setPositiveButton("Update Sekarang", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialogInterface, int i) {

                                                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(link));
                                                startActivity(browserIntent);
                                            }
                                        })
                                        .setCancelable(false)
                                        .show();
                            }else{

                                builderVersion = new AlertDialog.Builder(context)
                                        .setIcon(R.mipmap.ic_launcher)
                                        .setTitle("Update")
                                        .setMessage("Versi terbaru "+latestVersion+" telah tersedia, mohon download versi terbaru.")
                                        .setPositiveButton("Update Sekarang", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialogInterface, int i) {

                                                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(link));
                                                startActivity(browserIntent);
                                            }
                                        })
                                        .setNegativeButton("Update Nanti", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialogInterface, int i) {
                                                //dialogInterface.dismiss();
                                            }
                                        }).show();
                            }
                        }
                    }


                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onError(String result) {

            }
        });
    }

    public void getContacts() {

        listContact = new ArrayList<CustomItem>();
        String phoneNumber = null;
        String email = null;
        Uri CONTENT_URI = ContactsContract.Contacts.CONTENT_URI;
        String _ID = ContactsContract.Contacts._ID;
        String DISPLAY_NAME = ContactsContract.Contacts.DISPLAY_NAME;
        String HAS_PHONE_NUMBER = ContactsContract.Contacts.HAS_PHONE_NUMBER;
        Uri PhoneCONTENT_URI = ContactsContract.CommonDataKinds.Phone.CONTENT_URI;
        String Phone_CONTACT_ID = ContactsContract.CommonDataKinds.Phone.CONTACT_ID;
        String NUMBER = ContactsContract.CommonDataKinds.Phone.NUMBER;
        Uri EmailCONTENT_URI = ContactsContract.CommonDataKinds.Email.CONTENT_URI;
        String EmailCONTACT_ID = ContactsContract.CommonDataKinds.Email.CONTACT_ID;
        String DATA = ContactsContract.CommonDataKinds.Email.DATA;
        StringBuffer output;
        ContentResolver contentResolver = getContentResolver();
        try {
            cursor = contentResolver.query(CONTENT_URI, null, null, null, null);
        }catch (Exception e){
            e.printStackTrace();

            // Dismiss the progressbar after 500 millisecondds
            /*try {
                pDialog.dismiss();
            }catch (Exception e1){
                e1.printStackTrace();
            }*/
        }

        // Iterate every contact in the phone
        if (cursor != null && cursor.getCount() > 0) {
            counter = 0;
            while (cursor.moveToNext()) {

                output = new StringBuffer();
                // Update the progress message

                /*updateBarHandler.post(new Runnable() {
                    public void run() {
                        pDialog.setMessage("Membaca kontak : " + counter++ + "/" + cursor.getCount());
                    }
                });*/

                String contactId = cursor.getString(cursor.getColumnIndex(_ID));
                int hasPhoneNumber = Integer.parseInt(cursor.getString(cursor.getColumnIndex(HAS_PHONE_NUMBER)));

                String nama = cursor.getString(cursor.getColumnIndex(DISPLAY_NAME));
                String nomor = "", alamat = "";
                if (hasPhoneNumber > 0) {

                    //This is to read multiple phone numbers associated with the same contact
                    Cursor phoneCursor = contentResolver.query(PhoneCONTENT_URI, null, Phone_CONTACT_ID + " = ?", new String[]{contactId}, null);
                    while (phoneCursor.moveToNext()) {
                        nomor = phoneCursor.getString(phoneCursor.getColumnIndex(NUMBER));
                    }
                    phoneCursor.close();

                    listContact.add(new CustomItem(contactId, nama, nomor, alamat));
                }
                // Add the contact to the ArrayList
            }

            // ListView has to be updated using a ui thread
            runOnUiThread(new Runnable() {
                @Override
                public void run() {


                }
            });

            // Dismiss the progressbar after 500 millisecondds
            /*updateBarHandler.postDelayed(new Runnable() {
                @Override
                public void run() {

                    try {
                        pDialog.dismiss();
                    }catch (Exception e){
                        e.printStackTrace();
                    }

                }
            }, 500);*/
        }else{

            /*try {
                pDialog.dismiss();
            }catch (Exception e){
                e.printStackTrace();
            }*/
        }
    }

    private void initEvent() {

        btnNavHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                ChangeFragment(0);
            }
        });

        btnNavTransaksi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                ChangeFragment(1);
            }
        });

        btnNavHistory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                ChangeFragment(2);
            }
        });

        btnNavPromo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                ChangeFragment(3);
            }
        });

        /*ivMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                showPopupMenu(ivMenu);
            }
        });*/

        btnTopup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(HomeActivity.this, IsiSaldo.class);
                startActivity(intent);
                overridePendingTransition(R.anim.slide_in_up, R.anim.slide_out_up);
            }
        });

        tlMenu.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {

                //Log.d(TAG, "onTabReselected: "+tab.getPosition());
                ChangeFragment(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {


            }
        });
    }

    private void ChangeFragment(int stateChecked){

        switch (stateChecked){
            case 0:
                setTitle("Home");
                fragment = new MainHome();
                break;
            case 1:
                setTitle("Transaksi");
                fragment = new MainTransaksi();
                break;
            case 2:
                setTitle("History Penjualan");
                fragment = new MainHistory();
                break;
            case 3:
                setTitle("Promo");
                fragment = new MainPromo();
                break;
            default:
                setTitle("Home");
                fragment = new MainHome();
                break;
        }

        if(stateChecked > state){

            callFragment(HomeActivity.this, fragment);
        }else if (stateChecked < state){
            callFragmentBack(HomeActivity.this, fragment);
        }

        state = stateChecked;
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            if(state != 0){

                /*state = 0;
                fragment = new MainHome();
                callFragmentBack(HomeActivity.this, fragment);*/
                TabLayout.Tab tab = tlMenu.getTabAt(0);
                tab.select();

                ChangeFragment(0);

            }else{

                final AlertDialog.Builder builder = new AlertDialog.Builder(context);
                LayoutInflater inflater = (LayoutInflater) ((Activity)context).getSystemService(LAYOUT_INFLATER_SERVICE);
                View viewDialog = inflater.inflate(R.layout.layout_exit_dialog, null);
                builder.setView(viewDialog);
                builder.setCancelable(false);

                final Button btnYa = (Button) viewDialog.findViewById(R.id.btn_ya);
                final Button btnTidak = (Button) viewDialog.findViewById(R.id.btn_tidak);

                final AlertDialog alert = builder.create();
                alert.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

                btnYa.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view2) {

                        if(alert != null) alert.dismiss();

                        Intent intent = new Intent(HomeActivity.this, HomeActivity.class);
                        intent.putExtra("exit", true);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);
                        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                    }
                });

                btnTidak.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view2) {

                        if(alert != null) alert.dismiss();
                    }
                });

                alert.show();

                /*// Origin backstage
                if (doubleBackToExitPressedOnce) {
                    Intent intent = new Intent(HomeActivity.this, HomeActivity.class);
                    intent.putExtra("exit", true);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                    overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                    //System.exit(0);
                }

                if(!exitState && !doubleBackToExitPressedOnce){
                    this.doubleBackToExitPressedOnce = true;
                    Toast.makeText(this, getResources().getString(R.string.app_exit), Toast.LENGTH_SHORT).show();
                }

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        doubleBackToExitPressedOnce=false;
                    }
                }, timerClose);*/
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.home, menu);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.nav_cs) {
            Intent intent = new Intent(HomeActivity.this, ChatSales.class);
            startActivity(intent);
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_right);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_profile) {


            Intent intent = new Intent(context, ProfileActivity.class);
            startActivity(intent);
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_right);

        } else if (id == R.id.nav_tambah_akun) {

        } else if (id == R.id.nav_deposit) {

            Intent intent = new Intent(context, DepositActivity.class);
            startActivity(intent);
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_right);

        } else if (id == R.id.nav_pricelist) {

            Intent intent = new Intent(context, PricelistActivity.class);
            startActivity(intent);
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_right);

        } else if (id == R.id.nav_change_pin) {

            Intent intent = new Intent(context, UbahPinActivity.class);
            startActivity(intent);
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_right);

        } else if (id == R.id.nav_change_password) {

            Intent intent = new Intent(context, ChangePassword.class);
            startActivity(intent);
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_right);

        } else if (id == R.id.nav_info_psp) {

            Intent intent = new Intent(context, InformasiPSP.class);
            startActivity(intent);
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_right);

        } else if (id == R.id.nav_logout) {

            showLogOutDialog();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void showLogOutDialog() {

        AlertDialog dialog = new AlertDialog.Builder(HomeActivity.this)
                .setIcon(R.mipmap.ic_launcher)
                .setTitle("Konfirmasi")
                .setMessage("Apakah anda yakin akan logout?")
                .setPositiveButton("Ya", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Intent intent = new Intent(HomeActivity.this, LoginScreen.class);
                        session.logoutUser(intent);
                    }
                })
                .setNegativeButton("Tidak", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                })
                .show();
    }

    private static Fragment fragment;
    private static void callFragment(Context context, Fragment fragment) {
        ((AppCompatActivity)context).getSupportFragmentManager()
                .beginTransaction()
                //.setCustomAnimations(R.anim.slide_in_up, R.anim.slide_out_up)
                .setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_right)
                .replace(R.id.fl_container, fragment, fragment.getClass().getSimpleName())
                .addToBackStack(null)
                .commit();
    }

    private static void callFragmentBack(Context context, Fragment fragment) {
        ((AppCompatActivity)context).getSupportFragmentManager()
                .beginTransaction()
                //.setCustomAnimations(R.anim.slide_in_down, R.anim.slide_out_down)
                .setCustomAnimations(R.anim.slide_in_left, R.anim.slide_out_left)
                .replace(R.id.fl_container, fragment, fragment.getClass().getSimpleName())
                .addToBackStack(null)
                .commit();
    }
}
