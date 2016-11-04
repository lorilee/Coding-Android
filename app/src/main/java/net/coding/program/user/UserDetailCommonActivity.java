package net.coding.program.user;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.flyco.roundview.RoundRelativeLayout;
import com.flyco.roundview.RoundTextView;

import net.coding.program.R;
import net.coding.program.common.ActivenessView;
import net.coding.program.common.ClickSmallImage;
import net.coding.program.common.Global;
import net.coding.program.common.SyncHorizontalScrollView;
import net.coding.program.common.ui.BackActivity;
import net.coding.program.common.util.DensityUtil;
import net.coding.program.common.widget.LoadingView;
import net.coding.program.maopao.MaopaoListFragment;
import net.coding.program.message.JSONUtils;
import net.coding.program.model.UserObject;
import net.coding.program.model.user.ActiveModel;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * 用户信息公共部分
 * Created by anfs on 01/12/2016.
 */
public class UserDetailCommonActivity extends BackActivity {

    private static final String USER_ACTIVENESS = Global.HOST_API + "/user/activeness/data/";

    ImageView icon;
    TextView name;
    TextView location;
    LinearLayout llTrend;
    LinearLayout ll_activeness;
    LinearLayout llTitle;
    RoundRelativeLayout rl_follow_state;
    RoundRelativeLayout rl_message;
    TextView introduction;
    TextView tv_total_active;
    TextView tv_longest_active;
    TextView tv_current_active;
    TextView tv_follow_state;
    View icon_sharow;
    LoadingView baseLoadingView;
    SyncHorizontalScrollView scrollView0;
    SyncHorizontalScrollView scrollView1;
    TextView fans, follows;

    protected UserObject mUserObject;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    int sexs[] = new int[]{
            R.drawable.ic_sex_boy,
            R.drawable.ic_sex_girl,
            android.R.color.transparent
    };

    View.OnClickListener onClickFans = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (mUserObject == null){
                return;
            }
            UsersListActivity.UserParams userParams = new UsersListActivity.UserParams(mUserObject,
                    UsersListActivity.Friend.Fans);

            UsersListActivity_
                    .intent(UserDetailCommonActivity.this)
                    .mUserParam(userParams)
                    .type(UsersListActivity.Friend.Fans)
                    .start();
        }
    };
    View.OnClickListener onClickFollow = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (mUserObject == null){
                return;
            }
            UsersListActivity.UserParams userParams = new UsersListActivity.UserParams(mUserObject,
                    UsersListActivity.Friend.Follow);

            UsersListActivity_
                    .intent(UserDetailCommonActivity.this)
                    .mUserParam(userParams)
                    .type(UsersListActivity.Friend.Follow)
                    .start();
        }
    };

    protected void bindUI(UserObject mUserObject) {
        if (mUserObject == null){
            return;
        }
        this.mUserObject = mUserObject;

        initViews();

        //请求活动图数据
        String url = USER_ACTIVENESS + mUserObject.global_key;
        getNetwork(url, USER_ACTIVENESS);

        //用户信息赋值
        iconfromNetwork(icon, mUserObject.avatar, new UserDetailActivity.AnimateFirstDisplayListener());
        icon.setTag(new MaopaoListFragment.ClickImageParam(mUserObject.avatar));
        icon.setOnClickListener(new ClickSmallImage(this));

        name.setCompoundDrawablesWithIntrinsicBounds(0, 0, sexs[mUserObject.sex], 0);
        name.setText(mUserObject.name);

        initTextData(name, mUserObject.name);
        initTextData(location, mUserObject.location);
        initTextData(introduction, mUserObject.slogan);

        //用户标签
        if (TextUtils.isEmpty(mUserObject.tags_str)) {
            findViewById(R.id.hsl_main).setVisibility(View.GONE);
        } else {
            findViewById(R.id.hsl_main).setVisibility(View.VISIBLE);
            LinearLayout llTags = (LinearLayout) findViewById(R.id.ll_tags);

            String[] split = mUserObject.tags_str.split(",");
            for (String tag : split) {
                if (TextUtils.isEmpty(tag)) {
                    continue;
                }
                RoundTextView roundTextView = (RoundTextView) getLayoutInflater().inflate(R.layout.view_tag, null);
                roundTextView.setText(tag);
                llTags.addView(roundTextView);

                LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                lp.setMargins(0, 0, DensityUtil.dip2px(UserDetailCommonActivity.this, 5), 0);
                roundTextView.setLayoutParams(lp);
            }
        }
        scrollView0.setScrollView(scrollView1);
        scrollView1.setScrollView(scrollView0);

        fans.setText(UserDetailActivity.createSpan(this, String.format("粉丝   %d", mUserObject.fans_count)));
        fans.setOnClickListener(onClickFans);

        follows.setText(UserDetailActivity.createSpan(this, String.format("关注   %d", mUserObject.follows_count)));
        follows.setOnClickListener(onClickFollow);

    }

    /**
     * 要兼容原来注入的方式
     * 没有想到好办法，只能用最原始的方式了
     */
    private void initViews() {
        icon = (ImageView) findViewById(R.id.icon);
        name = (TextView) findViewById(R.id.name);
        location = (TextView) findViewById(R.id.location);
        llTrend = (LinearLayout) findViewById(R.id.llTrend);
        ll_activeness = (LinearLayout) findViewById(R.id.ll_activeness);
        llTitle = (LinearLayout) findViewById(R.id.llTitle);
        rl_follow_state = (RoundRelativeLayout) findViewById(R.id.rl_follow_state);
        rl_message = (RoundRelativeLayout) findViewById(R.id.rl_message);
        introduction = (TextView) findViewById(R.id.introduction);
        tv_total_active = (TextView) findViewById(R.id.tv_total_active);
        tv_longest_active = (TextView) findViewById(R.id.tv_longest_active);
        tv_follow_state = (TextView) findViewById(R.id.tv_follow_state);
        tv_current_active = (TextView) findViewById(R.id.tv_current_active);
        icon_sharow = (View) findViewById(R.id.icon_sharow);
        baseLoadingView = (LoadingView) findViewById(R.id.baseLoadingView);
        scrollView0 = (SyncHorizontalScrollView) findViewById(R.id.scrollView0);
        scrollView1 = (SyncHorizontalScrollView) findViewById(R.id.scrollView1);
        fans = (TextView) findViewById(R.id.fans);
        follows = (TextView) findViewById(R.id.follows);
    }

    @Override
    public void parseJson(int code, JSONObject respanse, String tag, int pos, Object data) throws JSONException {
        operActivenessResult(code, respanse, tag);
    }

    /**
     * 绘制活动图
     * @param code
     * @param respanse
     * @param tag
     * @throws JSONException
     */
    protected void operActivenessResult(int code, JSONObject respanse, String tag) throws JSONException {
        if (tag.equals(USER_ACTIVENESS)) {
            baseLoadingView.setVisibility(View.GONE);
            if (code == 0) {
                ActiveModel activeModel = JSONUtils.getData(respanse.getString("data"), ActiveModel.class);
                initActiveness(activeModel);
                //隐藏
                ll_activeness.setVisibility(View.VISIBLE);
            } else {
                //隐藏
                ll_activeness.setVisibility(View.GONE);
            }
            setViewPageData();
        }
    }

    protected void setViewPageData() {

    }

    /**
     * 创建活动图
     *
     * @param activeModel
     */
    private void initActiveness(ActiveModel activeModel) {
        if (activeModel == null) {
            return;
        }

        tv_total_active.setText(activeModel.total_with_seal_top_line + "度");
        tv_longest_active.setText(activeModel.longest_active_duration.days + "");
        tv_current_active.setText(activeModel.current_active_duration.days + "");

        //trendView
        ActivenessView trendView = new ActivenessView(this);
        trendView.setActiveModel(activeModel);

        LinearLayout.LayoutParams lp = new
                LinearLayout.LayoutParams(trendView.getTrendWidth(), trendView.getTrendHeight());
        llTrend.addView(trendView, lp);

        //title
        List<String> sortMonth = getMonths();

        int titleWidth = trendView.getTrendWidth() / sortMonth.size();
        for (String title : sortMonth) {
            TextView inflate = (TextView) getLayoutInflater().inflate(R.layout.view_trend_title, null);
            inflate.setText(title);

            LinearLayout.LayoutParams lp2 = new
                    LinearLayout.LayoutParams(titleWidth, LinearLayout.LayoutParams.WRAP_CONTENT);
            llTitle.addView(inflate, lp2);
        }
    }

    /**
     * 统计月份
     *
     * @return
     */
    @NonNull
    public static List<String> getMonths() {

        String[] monthStr = {"Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"};

        Calendar calendar = Calendar.getInstance();
        int month = calendar.get(Calendar.MONTH);

        List<String> sortMonth = new ArrayList<>();
        List<String> sortMonth2 = new ArrayList<>();

        for (int i = 0; i < monthStr.length; i++) {
            if (i >= month) {
                sortMonth2.add(monthStr[i]);
            } else {
                sortMonth.add(monthStr[i]);
            }
        }
        sortMonth.addAll(0, sortMonth2);

        return sortMonth;
    }

    public static void initTextData(TextView textView, String data) {
        if (TextUtils.isEmpty(data)) {
            isShow(false, textView);
            return;
        }
        isShow(true, textView);
        textView.setText(data);
    }

    public static void isShow(boolean isShow, View view) {
        if (view == null) {
            return;
        }
        view.setVisibility(isShow ? View.VISIBLE : View.GONE);
    }

}