package cn.kiway.activity.me;

import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import kankan.wheel.widget.OnWheelChangedListener;
import kankan.wheel.widget.WheelView;
import kankan.wheel.widget.adapters.ArrayWheelAdapter;
import kankan.wheel.widget.model.CityModel;
import kankan.wheel.widget.model.DistrictModel;
import kankan.wheel.widget.model.ProvinceModel;
import kankan.wheel.widget.service.XmlParserHandler;

import org.json.JSONObject;

import android.content.Intent;
import android.content.res.AssetManager;
import android.os.Bundle;
import android.view.View;
import cn.kiway.IConstant;
import cn.kiway.IUrContant;
import cn.kiway.Yjpty.R;
import cn.kiway.activity.BaseNetWorkActicity;
import cn.kiway.dialog.IsNetWorkDialog;
import cn.kiway.http.HttpResponseModel;
import cn.kiway.utils.AppUtil;
import cn.kiway.utils.SharedPreferencesUtil;

public class EditAeraActivity extends BaseNetWorkActicity implements
		OnWheelChangedListener {
	/**
	 * 省选择列表
	 * */
	private WheelView mViewProvince;
	/**
	 * 城市选择列表
	 * */
	private WheelView mViewCity;
	/**
	 * 区域选择列表
	 * **/
	private WheelView mViewDistrict;
	/**
	 * 城市数据
	 * */
	protected String[] mProvinceDatas;
	/**
	 * 城市数据
	 * **/
	protected Map<String, String[]> mCitisDatasMap = new HashMap<String, String[]>();
	/**
	 * 区域数据
	 * **/
	protected Map<String, String[]> mDistrictDatasMap = new HashMap<String, String[]>();
	/**
	 * 城市的Id
	 * */
	protected Map<String, String> mZipcodeDatasMap = new HashMap<String, String>();
	/**
	 * 当前选择的省
	 * */
	protected String mCurrentProviceName;
	/**
	 * 当前选择的市
	 * */
	protected String mCurrentCityName;
	/**
	 * 当前选择的区域
	 * */
	protected String mCurrentDistrictName = "";
	/**
	 * 当前选择的城市Id
	 * **/
	protected String mCurrentZipCode = "";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_edit_area);
		setUpViews();
		setUpListener();
		setUpData();
	}

	private void setUpViews() {
		mViewProvince = (WheelView) findViewById(R.id.id_province);
		mViewCity = (WheelView) findViewById(R.id.id_city);
		mViewDistrict = (WheelView) findViewById(R.id.id_district);
	}

	private void setUpListener() {
		mViewProvince.addChangingListener(this);
		mViewCity.addChangingListener(this);
		mViewDistrict.addChangingListener(this);
		findViewById(R.id.previos).setOnClickListener(this);
		findViewById(R.id.btn_confirm).setOnClickListener(this);
	}

	private void setUpData() {
		initProvinceDatas();
		mViewProvince.setViewAdapter(new ArrayWheelAdapter<String>(
				EditAeraActivity.this, mProvinceDatas));
		mViewProvince.setVisibleItems(15);
		mViewCity.setVisibleItems(15);
		mViewDistrict.setVisibleItems(15);
		if (bundle.getBoolean(IConstant.BUNDLE_PARAMS)) {
			mViewProvince.setCurrentItem(SharedPreferencesUtil.getInteger(this,
					IConstant.PROVINCE_NUMBER));
		}
		updateCities();
		if (bundle.getBoolean(IConstant.BUNDLE_PARAMS)) {
			mViewCity.setCurrentItem(SharedPreferencesUtil.getInteger(this,
					IConstant.CITY_NUMBER));
		}
		updateAreas();
		if (bundle.getBoolean(IConstant.BUNDLE_PARAMS)) {
			mViewDistrict.setCurrentItem(SharedPreferencesUtil.getInteger(this,
					IConstant.AREA_NUMBER));
		}
	}

	@Override
	public void onChanged(WheelView wheel, int oldValue, int newValue) {
		if (wheel == mViewProvince) {
			updateCities();
			mCurrentDistrictName = mDistrictDatasMap.get(mCurrentCityName)[0];
		} else if (wheel == mViewCity) {
			updateAreas();
			mCurrentDistrictName = mDistrictDatasMap.get(mCurrentCityName)[0];
		} else if (wheel == mViewDistrict) {
			mCurrentDistrictName = mDistrictDatasMap.get(mCurrentCityName)[newValue];
		}
	}

	/**
	 * 更新地区
	 * */
	private void updateAreas() {
		int pCurrent = mViewCity.getCurrentItem();
		mCurrentCityName = mCitisDatasMap.get(mCurrentProviceName)[pCurrent];
		String[] areas = mDistrictDatasMap.get(mCurrentCityName);
		if (areas == null) {
			areas = new String[] { "" };
		}
		mViewDistrict
				.setViewAdapter(new ArrayWheelAdapter<String>(this, areas));
		mViewDistrict.setCurrentItem(0);
	}

	/**
	 * 更新城市
	 * */
	private void updateCities() {
		int pCurrent = mViewProvince.getCurrentItem();
		mCurrentProviceName = mProvinceDatas[pCurrent];
		String[] cities = mCitisDatasMap.get(mCurrentProviceName);
		if (cities == null)
			cities = new String[] { "" };
		mViewCity.setViewAdapter(new ArrayWheelAdapter<String>(this, cities));
		mViewCity.setCurrentItem(0);
		updateAreas();
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn_confirm:
			if (!AppUtil.isNetworkAvailable(this)
					&& mCache.getAsJSONObject(IUrContant.GET_GROWTH_LIST_URL
							+ app.getClassModel().getId()) == null) {
				newWorkdialog = new IsNetWorkDialog(context, this,
						resources.getString(R.string.dqsjmylrhlwqljhlwl),
						resources.getString(R.string.ljhlw));
				if (newWorkdialog != null && !newWorkdialog.isShowing()) {
					newWorkdialog.show();
					return;
				}
			}
			if (bundle.getBoolean(IConstant.BUNDLE_PARAMS)) {// 修改个人信息地区
				SharedPreferencesUtil.save(this, IConstant.PROVINCE_NUMBER,
						mViewProvince.getCurrentItem());
				SharedPreferencesUtil.save(this, IConstant.CITY_NUMBER,
						mViewCity.getCurrentItem());
				SharedPreferencesUtil.save(this, IConstant.AREA_NUMBER,
						mViewDistrict.getCurrentItem());
				Map<String, String> map = new HashMap<>();
				map.put("userId", app.getUid() + "");
				map.put("countyId", mCurrentProviceName + "*"
						+ mCurrentCityName + "*" + mCurrentDistrictName);
				IConstant.HTTP_CONNECT_POOL.addRequest(
						IUrContant.UPDATEUSERINFO_URL, map, activityHandler,
						true);
			} else {// 选择班级地区 创建班级时候进入
				Intent da = getIntent();
				da.putExtra(IConstant.BUNDLE_PARAMS, mCurrentProviceName + "*"
						+ mCurrentCityName + "*" + mCurrentDistrictName);
				setResult(RESULT_OK, da);
				finish();
			}
			break;
		case R.id.previos:
			finish();
			break;
		}
	}

	/**
	 * 读取数据
	 * **/
	protected void initProvinceDatas() {
		List<ProvinceModel> provinceList = null;
		AssetManager asset = getAssets();
		try {
			InputStream input = asset.open("province_data.xml");
			SAXParserFactory spf = SAXParserFactory.newInstance();
			SAXParser parser = spf.newSAXParser();
			XmlParserHandler handler = new XmlParserHandler();
			parser.parse(input, handler);
			input.close();
			provinceList = handler.getDataList();
			if (provinceList != null && !provinceList.isEmpty()) {
				mCurrentProviceName = provinceList.get(0).getName();
				List<CityModel> cityList = provinceList.get(0).getCityList();
				if (cityList != null && !cityList.isEmpty()) {
					mCurrentCityName = cityList.get(0).getName();
					List<DistrictModel> districtList = cityList.get(0)
							.getDistrictList();
					mCurrentDistrictName = districtList.get(0).getName();
					mCurrentZipCode = districtList.get(0).getZipcode();
				}
			}
			mProvinceDatas = new String[provinceList.size()];
			for (int i = 0; i < provinceList.size(); i++) {
				mProvinceDatas[i] = provinceList.get(i).getName();
				List<CityModel> cityList = provinceList.get(i).getCityList();
				String[] cityNames = new String[cityList.size()];
				for (int j = 0; j < cityList.size(); j++) {
					cityNames[j] = cityList.get(j).getName();
					List<DistrictModel> districtList = cityList.get(j)
							.getDistrictList();
					String[] distrinctNameArray = new String[districtList
							.size()];
					DistrictModel[] distrinctArray = new DistrictModel[districtList
							.size()];
					for (int k = 0; k < districtList.size(); k++) {
						DistrictModel districtModel = new DistrictModel(
								districtList.get(k).getName(), districtList
										.get(k).getZipcode());
						mZipcodeDatasMap.put(districtList.get(k).getName(),
								districtList.get(k).getZipcode());
						distrinctArray[k] = districtModel;
						distrinctNameArray[k] = districtModel.getName();
					}
					mDistrictDatasMap.put(cityNames[j], distrinctNameArray);
				}
				mCitisDatasMap.put(provinceList.get(i).getName(), cityNames);
			}
		} catch (Throwable e) {
			e.printStackTrace();
		} finally {
		}
	}

	@Override
	public void httpSuccess(HttpResponseModel message) throws Exception {
		super.httpSuccess(message);
		if (message.getUrl().equals(IUrContant.UPDATEUSERINFO_URL)) {
			JSONObject data = new JSONObject(new String(message.getResponse()));
			if (data.optInt("retcode") == 1) {
				Intent da = getIntent();
				da.putExtra(IConstant.BUNDLE_PARAMS, mCurrentProviceName + " "
						+ mCurrentCityName + " " + mCurrentDistrictName);
				setResult(RESULT_OK, da);
				finish();
			}
		}
	}
}
