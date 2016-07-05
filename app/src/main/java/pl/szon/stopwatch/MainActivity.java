package pl.szon.stopwatch;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.AnticipateOvershootInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.widget.Button;
import android.widget.TextView;

import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import rx.Observable;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;

public class MainActivity extends AppCompatActivity {


    @BindView(R.id.text_view1)
    TextView timer_text;
    @BindView(R.id.button_start)
    Button start_button;
    @BindView(R.id.circle_minute)
    Circle circle_minute;
    @BindView(R.id.circle_second)
    Circle circle_second;

    Observable<Long> interval;
    Subscription increment;
    Subscription ui_update;
    Subscription second_animator;
    boolean running = false;
    final int TIME_STEP = 3;
    final int UI_THROTTLE_TIME = 10;
    long time = 0;
    long pause = 0;

    CircleAnimation secondAnimation;
    AnticipateOvershootInterpolator gravity_interp = new AnticipateOvershootInterpolator(0,4);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        initCircles();
    }

    @OnClick(R.id.button_start)
    void startTimer() {
        if (!running) {
            timer_text.setText("");
            interval = Observable
                    .interval(TIME_STEP, TimeUnit.MILLISECONDS);

            subscribeIncrement();
            subscribeUiUpdater();

            running = true;
            start_button.setText("Pause");

            if((int)(time/1000)%2 == 0) {
                secondAnimation = new CircleAnimation(circle_second, 360);
            } else {
                secondAnimation = new CircleAnimation(circle_second, 0);
            }
            secondAnimation.setDuration(1000 - time%1000);
            secondAnimation.setInterpolator(gravity_interp);
            circle_second.startAnimation(secondAnimation);
            subscribeSecondAnimator((int)(1000 - time%1000));
        } else
            pauseTimer();
    }

    void pauseTimer() {
        increment.unsubscribe();
        ui_update.unsubscribe();
        running = false;
        pause = time;
        secondAnimation = new CircleAnimation(circle_second, (int)circle_second.getAngle());
        secondAnimation.setDuration(0);
        second_animator.unsubscribe();
        circle_second.startAnimation(secondAnimation);
        start_button.setText("Start");
    }

    void animateSecond() {
        if((int)(time/1000)%2 == 0) {
            circle_second.setScaleX(1f);
            secondAnimation = new CircleAnimation(circle_second, 360);
            secondAnimation.setDuration(1000);
            secondAnimation.setInterpolator(gravity_interp);
            circle_second.startAnimation(secondAnimation);
        } else {
            circle_second.setScaleX(-1f);
            secondAnimation = new CircleAnimation(circle_second, 0);
            secondAnimation.setDuration(1000);
            secondAnimation.setInterpolator(gravity_interp);
            circle_second.startAnimation(secondAnimation);
        }
    }

    private void initCircles() {
        circle_second.init(680, 40);
    }

    @OnClick(R.id.button_reset)
    void resetTimer() {
        if (running) {
            pauseTimer();
        }
        time = 0;
        pause = 0;
        updateText();
        secondAnimation = new CircleAnimation(circle_second, 0);
        circle_second.startAnimation(secondAnimation);
        circle_second.setScaleX(1f);
    }

    private void increment(Long next) {
        time = pause + next * TIME_STEP;
    }

    private void subscribeIncrement() {
        increment = interval
                .onBackpressureDrop()
                .subscribe(this::increment);
    }

    private void subscribeUiUpdater() {
        ui_update = interval
                .throttleLast(UI_THROTTLE_TIME, TimeUnit.MILLISECONDS)
                .onBackpressureDrop()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(next -> updateText());
    }

    private void subscribeSecondAnimator(int delay) {
        second_animator = interval
                .throttleFirst(1000, TimeUnit.MILLISECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .delaySubscription(delay, TimeUnit.MILLISECONDS)
                .subscribe(next -> animateSecond());
    }

    private void updateText() {
        timer_text.setText(String.format("%d", time));
    }

    public void onResume() {
        super.onResume();
        if (running) {
            subscribeUiUpdater();
        }
    }

    public void onPause() {
        super.onPause();
        if (running)
            ui_update.unsubscribe();
    }
}
