package today.news.com;


import android.os.Handler;

public class CustomCountDownTimer implements Runnable {
    private int time;
    private int countTime;
    private final Handler handler;
    ICountDownHandler countDownHandler;
    boolean isRun;


    public CustomCountDownTimer(int time,ICountDownHandler countDownHandler){
        handler = new Handler();
        this.time = time;
        countTime = time;
        this.countDownHandler = countDownHandler;


    }

    @Override
    public void run() {
        if(isRun){
            if (countDownHandler != null) {
                countDownHandler.onTicker(countTime);

            }
            if (countTime == 0) {
                cancle();
                countDownHandler.finish();
            }else {
                countTime = --time;
                handler.postDelayed(this,1000);
            }
        }
    }
    public void start(){
        isRun = true;
        handler.post(this);
    }

    public void cancle() {
        isRun = false;
        handler.removeCallbacks(this);
    }


    public interface ICountDownHandler{
        void onTicker(int time);

        void finish();
    }
}
