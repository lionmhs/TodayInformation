package today.news.com;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.VideoView;

import java.io.File;

public class SplashActivity extends AppCompatActivity {
    private FullScreenVideoView mVideoVieiw;
    private TextView tv_time;
    private CustomCountDownTimer customCountDownTimer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        tv_time = findViewById(R.id.tv_time);
        tv_time.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if("跳过".equals(tv_time.getText().toString())){
                    startActivity(new Intent(SplashActivity.this,MainActivity.class));
                }
            }
        });
        mVideoVieiw = (FullScreenVideoView) findViewById(R.id.vv_play);
        mVideoVieiw.setVideoURI(Uri.parse("android.resource://" + getPackageName() + File.separator + R.raw.splash));
        mVideoVieiw.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                mp.start();
            }
        });
        mVideoVieiw.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                mp.start();
            }
        });
        customCountDownTimer = new CustomCountDownTimer(5, new CustomCountDownTimer.ICountDownHandler() {
            @Override
            public void onTicker(int time) {
                tv_time.setText(time + "秒");
            }

            @Override
            public void finish() {
                tv_time.setText("跳过");
            }
        });
        customCountDownTimer.start();
    }

    @Override
    protected void onDestroy() {
        customCountDownTimer.cancle();
        super.onDestroy();
    }
}
