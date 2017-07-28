package cn.kiway.yjhz.activity.simulator;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

import cn.kiway.yjhz.R;
import cn.kiway.yjhz.activity.BaseActivity;
import cn.kiway.yjhz.model.ClassModel;
import cn.kiway.yjhz.utils.IConstant;
import cn.kiway.yjhz.utils.Logger;
import cn.kiway.yjhz.utils.okhttp.HttpRequestUrl;
import cn.kiway.yjhz.utils.okhttp.HttpUtils;
import okhttp3.Call;
import okhttp3.Response;

/**
 * Created by Administrator on 2017/7/3.
 */

public class ClassListActivity extends BaseActivity implements View.OnClickListener {
    ArrayList<ClassModel> list = new ArrayList<ClassModel>();
    ListView listView;
    TextView title;
    ClassAdpater classAdpater;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.list_view);
        initView();
        loadData();
    }

    private void initView() {
        title = (TextView) findViewById(R.id.title);
        title.setText("选择班级");
        listView = (ListView) findViewById(R.id.list_item);
        classAdpater = new ClassAdpater(this);
        listView.setAdapter(classAdpater);
        findViewById(R.id.back).setOnClickListener(this);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                mCache.put("classId", list.get(i).getId());
                Intent intent = new Intent(ClassListActivity.this, GSessionActivity.class);
                Bundle bundle = new Bundle();
                bundle.putSerializable(IConstant.BUNDLE_PARAMS, list.get(i));
                intent.putExtras(bundle);
                setResult(RESULT_OK, intent);
                finish();
            }
        });
    }

    protected void loadData() {
        Logger.log(yjhzAppication.session);
        yjhzAppication.mHttpClient.newCall(HttpUtils.get(HttpRequestUrl.GET_MY_CLASS_LIST, yjhzAppication.session))
                .enqueue(this);
    }

    @Override
    public void onResponse(Call call, Response response) throws IOException {
        super.onResponse(call, response);
        try {
            String s = response.body().string();
            JSONObject data = new JSONObject(s);
            if (data != null) {
                JSONArray array = data.optJSONArray("data");
                if (array != null) {
                    list.clear();
                    for (int i = 0; i < array.length(); i++) {
                        JSONObject item = array.optJSONObject(i);
                        ClassModel model = new ClassModel();
                        model.setId(item.optString("id"));// 班级id
                        model.setClassName(item.optString("name"));// 班级名字
                        model.setSchoolId(item.optString("schoolId"));// 学校id
                        model.setYear(item.optString("gradeId"));// 年级
                        model.setIsActivateKinect(item.optString("isKT"));//是否开通体感课程
                        list.add(model);
                        if (mCache.getAsString("classId").equals(item.optString("id")) && getIntent().getExtras() ==
                                null) {
                            Intent intent = new Intent(ClassListActivity.this, GSessionActivity.class);
                            Bundle bundle = new Bundle();
                            bundle.putSerializable(IConstant.BUNDLE_PARAMS, model);
                            intent.putExtras(bundle);
                            setResult(RESULT_OK, intent);
                            finish();
                        }
                    }
                }
            }
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    classAdpater.notifyDataSetChanged();
                    findViewById(R.id.layout).setVisibility(View.VISIBLE);
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.back:
                finish();
                break;
        }
    }

    class ClassAdpater extends ArrayAdapter<ClassModel> {
        ClassHolder holder;

        public ClassAdpater(@NonNull Context context) {
            super(context, -1);
        }

        @Override
        public int getCount() {
            return list.size();
        }

        @NonNull
        @Override
        public View getView(final int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            View view = convertView;
            if (view == null) {
                holder = new ClassHolder();
                view = LayoutInflater.from(ClassListActivity.this).inflate(R.layout.class_list_item, null);
                holder.className = (TextView) view.findViewById(R.id.className);
                view.setTag(holder);
            } else {
                holder = (ClassHolder) view.getTag();
            }
            holder.className.setText(list.get(position).getClassName());
            holder.className.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    //Intent intent = new Intent(ClassListActivity.this, SessionActivity.class);
                    Intent intent = new Intent(ClassListActivity.this, GSessionActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putSerializable(IConstant.BUNDLE_PARAMS, list.get(position));
                    intent.putExtras(bundle);
                    //  startActivity(intent);
                    setResult(RESULT_OK, intent);
                    finish();
                }
            });
            return view;
        }

        public class ClassHolder {
            /**
             * 班级名字
             */
            public TextView className;
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (classAdpater.getCount() > 0 && keyCode == KeyEvent.KEYCODE_BACK) {
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
}
