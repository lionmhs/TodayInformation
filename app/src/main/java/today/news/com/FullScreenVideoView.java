package today.news.com;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.VideoView;

public class FullScreenVideoView extends VideoView {
    //用于new出来的对象
    public FullScreenVideoView(Context context) {
        super(context);
    }
    //用于xml
    public FullScreenVideoView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }
    //用于xml,可以有样式
    public FullScreenVideoView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int width = getDefaultSize(0, widthMeasureSpec);
        int height = getDefaultSize(0, heightMeasureSpec);

        setMeasuredDimension(width,height);
       // super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }
}
