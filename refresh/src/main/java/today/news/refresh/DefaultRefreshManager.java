package today.news.refresh;

import android.content.Context;
import android.view.View;
import android.widget.TextView;

public class DefaultRefreshManager extends BaseRefreshManager {
    TextView tv_header;
    public DefaultRefreshManager(Context mContext) {
        super(mContext);
    }

    @Override
    public View getHeaderView() {
        View inflate = inflater.inflate(R.layout.ulti_header_layout, null, false);
        tv_header = inflate.findViewById(R.id.header_text);
        return inflate;
    }

    @Override
    public void downRefresh() {
        //下拉刷新
        tv_header.setText("下拉刷新");
    }

    @Override
    public void releaseRefresh() {
        //释放刷新
        tv_header.setText("释放刷新");

    }

    @Override
    public void iddleRefresh() {
        tv_header.setText("下拉刷新");
    }

    @Override
    public void ingRefresh() {
        tv_header.setText("正在刷新");
    }

    @Override
    public void downRefreshPercent(float percent) {

    }
}
