package hu.ait.android.shoppinglist_zskluzacek;

import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

public class SplashActivity extends AppCompatActivity {

    private final int SPLASH_DISPLAY_LENGTH = 3000;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash_layout);

        ImageView splashImage = (ImageView) findViewById(R.id.splash_image);
        TextView splashText = (TextView) findViewById(R.id.splash_text);

        Animation anim = AnimationUtils.loadAnimation(
                SplashActivity.this, R.anim.anim_main);
        splashImage.startAnimation(anim);
        splashText.startAnimation(anim);

        new Handler().postDelayed(new Runnable(){
            @Override
            public void run() {
                Intent mainIntent = new Intent(SplashActivity.this, ShoppingListActivity.class);
                SplashActivity.this.startActivity(mainIntent);
                SplashActivity.this.finish();
            }
        }, SPLASH_DISPLAY_LENGTH);
    }
}
