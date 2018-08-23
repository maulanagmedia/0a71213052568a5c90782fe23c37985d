package gmedia.net.id.pspreseller;

import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.widget.TextView;

import com.google.firebase.FirebaseApp;
import com.maulana.custommodul.ItemValidation;

import gmedia.net.id.pspreseller.NotificationUtil.InitFirebaseSetting;

public class SplashScreen extends AppCompatActivity {

    private static boolean splashLoaded = false;
    private View ivLogo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        ivLogo = findViewById(R.id.iv_logo);

        AlphaAnimation animation1 = new AlphaAnimation(0f, 1.0f);
        animation1.setDuration(1500);
        ivLogo.setAlpha(1f);
        ivLogo.startAnimation(animation1);

        FirebaseApp.initializeApp(this);
        InitFirebaseSetting.getFirebaseSetting(SplashScreen.this);

        if (!splashLoaded) {

            int secondsDelayed = 2;
            new Handler().postDelayed(new Runnable() {
                public void run() {
                    //startActivity(new Intent(SplashScreen.this, DaftarVideo.class));
                    startActivity(new Intent(SplashScreen.this, LoginScreen.class));
                    finish();
                    overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                }
            }, secondsDelayed * 1000);

            //splashLoaded = true;
        }else{

            //Intent goToMainActivity = new Intent(SplashScreen.this, DaftarVideo.class);
            Intent goToMainActivity = new Intent(SplashScreen.this, LoginScreen.class);
            goToMainActivity.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
            startActivity(goToMainActivity);
            finish();
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        }
    }
}
