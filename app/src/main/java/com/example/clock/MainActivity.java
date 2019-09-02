package com.example.clock;

import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.Random;

public class MainActivity extends AppCompatActivity {
    private ImageView img_h, img_m, img_s;
    private TextView e_h, e_m, e_s;
    private Button mBtn_Start, mBtn_Stop;
    private LinearLayout ll;
    // 累计电子时钟的数据
    int s = 0,
            h = 12,
            m = 0;
    boolean isRun = true;
    //秒钟和分钟的起始位置
    float from = 0f,
            from_m = 0,
            from_h = 0;
    //秒钟和分钟的旋转位置
    float to = 0f,
            to_m = 0,
            to_h = 0;

    MediaPlayer mediaPlayer;
    MyThread myThread;

    int bg[] = {
            R.drawable.time_bg,
            R.drawable.bg1,
            R.drawable.bg2,
            R.drawable.bg3
    };
    private static int pre_index;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initViews();
        setViews();
        // 为组件设置监听
        initEvent();
        myThread.execute();
    }

    private void setViews() {
        mediaPlayer.start();
        mediaPlayer.setLooping(true);
        e_h.setText(h + "");
    }

    private void initEvent() {

        // 停止背景音乐
        mBtn_Stop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                stopMusic();
            }
        });


        //切换背景
        mBtn_Start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeBackground();
            }
        });

    }

    private void stopMusic() {
        if (mediaPlayer != null && mediaPlayer.isPlaying()) {
            mediaPlayer.pause();
            mediaPlayer.stop();
            mediaPlayer.release();
        }
    }

    private void changeBackground() {
        for (int i = 0; i < 50; i++) {
            Random random = new Random();
            int index = random.nextInt(bg.length);
            // pre_index表示上一次index的值
            if (index != pre_index) {
                ll.setBackgroundResource(bg[index]);
                pre_index = index;
                break;
            }
        }
    }

    private void initViews() {
        ll = findViewById(R.id.main_view);
        img_h = this.findViewById(R.id.img_h);
        img_m = this.findViewById(R.id.img_m);
        img_s = this.findViewById(R.id.img_s);
        e_h = findViewById(R.id.e_time_h);
        e_m = findViewById(R.id.e_time_m);
        e_s = findViewById(R.id.e_time_s);
        mBtn_Start = findViewById(R.id.main_btn_start);
        mBtn_Stop = findViewById(R.id.main_btn_pause);

        myThread = new MyThread();
        mediaPlayer = MediaPlayer.create(this, R.raw.miao);
    }

    // 时针的转动
    private void hour() {
        to_h += 6;
        RotateAnimation ra = new RotateAnimation(
                from_h,
                to_h,
                Animation.RELATIVE_TO_SELF,
                0.5f,
                Animation.RELATIVE_TO_SELF,
                0.89f
        );

        ra.setDuration(40);
        ra.setFillAfter(true);
        img_h.startAnimation(ra);
        // 第二次的起始位置等于第一次的旋转位置，以此类推
        from_h = to_h;

        h++;
        if (h > 12) {
            e_h.setText(h + "");
        }
        if (h == 24) {
            e_h.setText("00");

        }

        if (mediaPlayer != null) {
            mediaPlayer.start();
        }

        if (to_m >= 360) {
            to_m = 0;
            from_m = 0;

        }

    }


    //分钟的转动
    private void minute() {
        to_m += 6;
        RotateAnimation ra = new RotateAnimation(
                from_m,
                to_m,
                Animation.RELATIVE_TO_SELF,
                0.5f,
                Animation.RELATIVE_TO_SELF,
                0.89f

        );
        ra.setDuration(40);
        ra.setFillAfter(true);
        img_m.startAnimation(ra);
        // 第二次的起始位置等于第一次的旋转位置，以此类推
        from_m = to_m;

        m++;
        if (m < 10) {
            e_m.setText("0" + m + "");
        } else if (m == 60) {
            e_m.setText("00");
        } else {
            e_m.setText(m + "");
        }


        if (to_m >= 360) {
            to_m = 0;
            from_m = 0;
            m = 0;
            //启动时钟
            hour();
        }
    }

    //秒钟转动
    private void second() {
        to += 6;
        //设置旋转动画
        RotateAnimation ra = new RotateAnimation(
                from,
                to,
                Animation.RELATIVE_TO_SELF,
                0.5f,
                Animation.RELATIVE_TO_SELF,
                0.67f

        );
        ra.setDuration(40);
        ra.setFillAfter(true);
        img_s.startAnimation(ra);
        from = to;

        s++;
        if (s < 10) {
            e_s.setText("0" + s + "");
        } else {
            e_s.setText(s + "");
        }


        if (to >= 360) {
            to = 0;
            from = 0;
            //电子记时的分钟为0
            s = 0;
            //启动分钟转动
            minute();

        }
    }

    class MyThread extends AsyncTask<Void, Void, Void> {
        @Override
        protected void onProgressUpdate(Void... values) {
            super.onProgressUpdate(values);
            //线程在执行过程中间要执行的动作
            second();
        }

        @Override
        protected Void doInBackground(Void... voids) {

            //整个子线程的核心逻辑部分
            while (isRun) {
                long begin = System.currentTimeMillis();
                publishProgress();
                long end = System.currentTimeMillis();
                try {
                    Thread.sleep(100 - (end - begin));
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            return null;
        }
    }
}

