package pl.szon.stopwatch;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.OnClick;
import rx.Observable;
import rx.Subscription;

public class MainActivity extends AppCompatActivity {


    @BindView(R.id.text_view1)
    TextView timer_text;
    Subscription timer;
    final int TIME_STEP = 20;
    long time = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    @OnClick(R.id.button_start)
    void startTimer() {
        timer = Observable
                .timer(TIME_STEP, TimeUnit.MILLISECONDS)
                .subscribe();
    }
}
