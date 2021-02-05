package today.news.refresh;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;

public abstract class BaseRefreshManager {

    public LayoutInflater inflater;

    public BaseRefreshManager(Context mContext) {
        inflater = LayoutInflater.from(mContext);
    }
    public abstract View getHeaderView();

    public abstract void downRefresh();

    public abstract void releaseRefresh();

    public abstract void iddleRefresh();

    public abstract void ingRefresh();

    public abstract void downRefreshPercent(float percent);
}
