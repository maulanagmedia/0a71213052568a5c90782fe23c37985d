package gmedia.net.id.pspreseller;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.provider.Settings;

import androidx.fragment.app.Fragment;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.appcompat.view.menu.MenuBuilder;
import androidx.appcompat.view.menu.MenuPopupHelper;
import androidx.appcompat.widget.PopupMenu;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.maulana.custommodul.ItemValidation;
import com.maulana.custommodul.SessionManager;

import gmedia.net.id.pspreseller.HomePulsa.OrderPulsa;
import gmedia.net.id.pspreseller.HomePulsa.Service.ServiceHandler;
import gmedia.net.id.pspreseller.NavHistory.MainHistory;
import gmedia.net.id.pspreseller.NavHome.MainHome;
import gmedia.net.id.pspreseller.NavPromo.MainPromo;
import gmedia.net.id.pspreseller.NavTransaksi.MainTransaksi;
import gmedia.net.id.pspreseller.TopUP.IsiSaldo;

public class MainActivity extends AppCompatActivity {

    private static boolean doubleBackToExitPressedOnce;
    private boolean exitState = false;
    private int timerClose = 2000;

    private SessionManager session;
    private ItemValidation iv = new ItemValidation();
    private Button btnNavHome, btnNavTransaksi, btnNavHistory, btnNavPromo;
    //private ImageView ivMenu;
    private int state = 0;
    private Button btnTopup;
    private Context context;
    public static boolean isAccessGranted = false;
    private String TAG = "MAIN";
    private RelativeLayout rlContainer;
    private AlertDialog dialogAccess;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Check close statement
        context = this;
        doubleBackToExitPressedOnce = false;
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {

            if (bundle.getBoolean("exit", false)) {
                exitState = true;
                finish();
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            }
        }

        initUI();
    }

    private void initUI() {

        btnNavHome = (Button) findViewById(R.id.btn_nav_home);
        btnNavTransaksi = (Button) findViewById(R.id.btn_nav_transaksi);
        btnNavHistory = (Button) findViewById(R.id.btn_nav_history);
        btnNavPromo = (Button) findViewById(R.id.btn_nav_promo);
        //ivMenu = (ImageView) findViewById(R.id.iv_menu);
        btnTopup = (Button) findViewById(R.id.btn_topup);
        rlContainer = (RelativeLayout) findViewById(R.id.rl_container);

        session = new SessionManager(MainActivity.this);

        state = 0;
        fragment = new MainHome();
        callFragment(MainActivity.this, fragment);

        initEvent();
    }

    @Override
    protected void onResume() {
        super.onResume();

        OrderPulsa.isActive = false;

        isAccessGranted =  isAccessibilityEnabled(context.getPackageName() + "/" + context.getPackageName() + ".HomePulsa.Service.USSDService");
        if(isAccessGranted){

            //Log.d(TAG, "granted");
        }else{
            //Log.d(TAG, "not granted");
            /*View rootView = getWindow().getDecorView().findViewById(android.R.id.content);
            Snackbar.make(rlContainer, "Mohon ijinkan akses pada "+ getResources().getString(R.string.app_name)+", Cari "+ getResources().getString(R.string.app_name)+" dan ubah enable",
                    Snackbar.LENGTH_INDEFINITE).setAction("OK",
                    new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            startActivity(new Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS));
                        }
                    }).show();*/

            dialogAccess = new AlertDialog.Builder(context)
                    .setIcon(R.mipmap.ic_launcher)
                    .setTitle("Konfirmasi")
                    .setMessage("Mohon ijinkan akses pada "+ getResources().getString(R.string.app_name)+", Cari "+ getResources().getString(R.string.app_name)+" dan ubah enable")
                    .setPositiveButton("Buka Akses", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {

                            startActivity(new Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS));
                        }
                    }).show();

        }

        ServiceHandler serviceHandler = new ServiceHandler((Activity) context);

        Bundle bundle = getIntent().getExtras();
        if(bundle != null){

            String stateString = bundle.getString("state", "");
            if(!stateString.isEmpty()) ChangeFragment(iv.parseNullInteger(stateString));
        }
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

                Intent intent = new Intent(MainActivity.this, IsiSaldo.class);
                startActivity(intent);
                overridePendingTransition(R.anim.slide_in_down, R.anim.slide_out_down);
            }
        });
    }

    private void ChangeFragment(int stateChecked){

        switch (stateChecked){
            case 0:
                fragment = new MainHome();
                break;
            case 1:
                fragment = new MainTransaksi();
                break;
            case 2:
                fragment = new MainHistory();
                break;
            case 3:
                fragment = new MainPromo();
                break;
            default:
                fragment = new MainHome();
                break;
        }

        if(stateChecked > state){

            callFragment(MainActivity.this, fragment);
        }else if (stateChecked < state){
            callFragmentBack(MainActivity.this, fragment);
        }

        state = stateChecked;
    }

    @Override
    public void onBackPressed() {

        if(state != 0){

            state = 0;
            fragment = new MainHome();
            callFragmentBack(MainActivity.this, fragment);

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

                    Intent intent = new Intent(MainActivity.this, MainActivity.class);
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

            // Origin backstage
            /*if (doubleBackToExitPressedOnce) {
                Intent intent = new Intent(MainActivity.this, MainActivity.class);
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

    @SuppressLint("RestrictedApi")
    private void showPopupMenu(View view) {
        // inflate menu
        /*PopupMenu popup = new PopupMenu(MainActivity.this, view);
        MenuInflater inflater = popup.getMenuInflater();
        inflater.inflate(R.menu.main, popup.getMenu());
        popup.setOnMenuItemClickListener(new MyMenuItemClickListener());*/
        //popup.show();

        @SuppressLint("RestrictedApi")
        MenuBuilder menuBuilder = new MenuBuilder(MainActivity.this);
        MenuInflater inflater = new MenuInflater(MainActivity.this);
        inflater.inflate(R.menu.main, menuBuilder);
        @SuppressLint("RestrictedApi")
        MenuPopupHelper optionsMenu = new MenuPopupHelper(MainActivity.this, menuBuilder, view);
        optionsMenu.setForceShowIcon(true);

        // Set Item Click Listener
        menuBuilder.setCallback(new MenuBuilder.Callback() {
            @Override
            public boolean onMenuItemSelected(MenuBuilder menu, MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.nav_profile: // Handle option1 Click
                        return true;
                    case R.id.nav_logout: // Handle option2 Click
                        showLogOutDialog();
                        return true;
                    default:
                        return false;
                }
            }

            @Override
            public void onMenuModeChange(MenuBuilder menu) {}
        });


        // Display the menu
        optionsMenu.show();
    }


    private class MyMenuItemClickListener implements PopupMenu.OnMenuItemClickListener {

        public MyMenuItemClickListener() {
        }

        @Override
        public boolean onMenuItemClick(MenuItem menuItem) {
            switch (menuItem.getItemId()) {
                case R.id.nav_profile:
                    Toast.makeText(MainActivity.this, "Profile", Toast.LENGTH_SHORT).show();
                    return true;
                case R.id.nav_logout:
                    showLogOutDialog();
                    return true;
                default:
            }
            return false;
        }
    }

    private void showLogOutDialog() {

        AlertDialog dialog = new AlertDialog.Builder(MainActivity.this)
                .setIcon(R.mipmap.ic_launcher)
                .setTitle("Konfirmasi")
                .setMessage("Apakah anda yakin akan logout?")
                .setPositiveButton("Ya", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Intent intent = new Intent(MainActivity.this, LoginScreen.class);
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
