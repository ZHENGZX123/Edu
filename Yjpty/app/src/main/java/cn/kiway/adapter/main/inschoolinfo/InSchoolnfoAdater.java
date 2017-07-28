package cn.kiway.adapter.main.inschoolinfo;

import java.util.List;
import java.util.Map;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.TextView;
import cn.kiway.IConstant;
import cn.kiway.Yjpty.R;
import cn.kiway.activity.BaseActivity;
import cn.kiway.activity.main.InShcoolnfoActivity;
import cn.kiway.model.BoyCheckModel;
import cn.kiway.model.BoyModel;
import cn.kiway.utils.SharedPreferencesUtil;
import cn.kiway.utils.StringUtil;
import cn.kiway.utils.ViewUtil;

public class InSchoolnfoAdater extends ArrayAdapter<BoyModel> implements
		OnCheckedChangeListener {
	BaseActivity activity;
	InschoolInfoHolder holder;
	List<BoyModel> listboy;// 小孩列表
	List<Map<String, String>> liston;// 选择列表
	int positiont;
	public List<String> list;

	public InSchoolnfoAdater(Context context, List<BoyModel> listboy,
			List<Map<String, String>> liston, int position) {
		super(context, -1);
		activity = (BaseActivity) context;
		this.listboy = listboy;
		this.liston = liston;
		this.positiont = position;
	}

	@Override
	public int getCount() {
		return listboy.size();
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View view = convertView;
		if (view == null) {
			view = ViewUtil.inflate(activity, R.layout.inshcool_info_list_item);
			holder = new InschoolInfoHolder();
			holder.rg = ViewUtil.findViewById(view, R.id.rg);
			holder.rb1 = ViewUtil.findViewById(view, R.id.rb1);
			holder.rb2 = ViewUtil.findViewById(view, R.id.rb2);
			holder.rb3 = ViewUtil.findViewById(view, R.id.rb3);
			holder.name = ViewUtil.findViewById(view, R.id.name);
			view.setTag(holder);
		} else {
			holder = (InschoolInfoHolder) view.getTag();
		}
		BoyModel boyModel = listboy.get(position);
		ViewUtil.setContent(holder.name, boyModel.getName());
		switch (positiont) {
		case 1:
			list = InShcoolnfoActivity.list1;
			break;
		case 2:
			list = InShcoolnfoActivity.list2;
			break;
		case 3:
			list = InShcoolnfoActivity.list3;
			break;
		case 4:
			list = InShcoolnfoActivity.list4;
			break;
		case 5:
			list = InShcoolnfoActivity.list5;
			break;
		case 6:
			list = InShcoolnfoActivity.list6;
			break;
		}

		if (InShcoolnfoActivity.checkModels.size() > 0) {

			for (int i = 0; i < InShcoolnfoActivity.checkModels.size(); i++) {
				if (listboy.get(position).getUid() == InShcoolnfoActivity.checkModels
						.get(i).getUid()
						&& positiont == InShcoolnfoActivity.checkModels.get(i)
								.getType()) {
					switch (InShcoolnfoActivity.checkModels.get(i).getLevel()) {
					case 1:
						holder.rb1.setChecked(true);
						break;
					case 2:
						holder.rb2.setChecked(true);
						break;
					case 3:
						holder.rb3.setChecked(true);
						break;
					}
				}
			}

		} else {
			int string;
			if (list.size() > 0 && !InShcoolnfoActivity.isLast()) {
				string = StringUtil.toInt(list.get(position));
			} else {
				string = SharedPreferencesUtil.getInteger(activity,
						IConstant.PING_JIA + positiont);
			}
			if (string == 1 || string == 0) {
				holder.rb1.setChecked(true);
			} else if (string == 2) {
				holder.rb2.setChecked(true);
			} else if (string == 3) {
				holder.rb3.setChecked(true);
			}
		}
		if (InShcoolnfoActivity.isLast()) {
			holder.rb1.setEnabled(false);
			holder.rb2.setEnabled(false);
			holder.rb3.setEnabled(false);
		}
		holder.rg.setTag(position);
		holder.rg.setOnCheckedChangeListener(this);
		return view;
	}

	class InschoolInfoHolder {
		/**
		 * 选择rg
		 * */
		RadioGroup rg;
		/**
		 * 名字
		 * */
		TextView name;
		RadioButton rb1, rb2, rb3;
	}

	@Override
	public void onCheckedChanged(RadioGroup group, int id) {
		int postion = StringUtil.toInt(group.getTag().toString());
		String check = null;
		switch (id) {
		case R.id.rb1:
			check = 1 + "";
			break;
		case R.id.rb2:
			check = 2 + "";
			break;
		case R.id.rb3:
			check = 3 + "";
			break;
		}
		if (InShcoolnfoActivity.checkModels.size() > 0) {
			boolean isHas = false;
			for (int i = 0; i < InShcoolnfoActivity.checkModels.size(); i++) {
				if (listboy.get(postion).getUid() == InShcoolnfoActivity.checkModels
						.get(i).getUid()
						&& positiont == InShcoolnfoActivity.checkModels.get(i)
								.getType()) {
					isHas = true;
					InShcoolnfoActivity.checkModels.get(i).setLevel(
							StringUtil.toInt(check));
				}
			}
			if (!isHas) {
				BoyCheckModel boyCheckModel = new BoyCheckModel();
				boyCheckModel.setLevel(StringUtil.toInt(check));
				boyCheckModel.setType(positiont);
				boyCheckModel.setUid((int) listboy.get(postion).getUid());
				InShcoolnfoActivity.checkModels.add(boyCheckModel);
			}
		}
		switch (positiont) {
		case 1:
			InShcoolnfoActivity.list1.remove(postion);
			InShcoolnfoActivity.list1.add(postion, check);
			break;
		case 2:
			InShcoolnfoActivity.list2.remove(postion);
			InShcoolnfoActivity.list2.add(postion, check);
			break;
		case 3:
			InShcoolnfoActivity.list3.remove(postion);
			InShcoolnfoActivity.list3.add(postion, check);
			break;
		case 4:
			InShcoolnfoActivity.list4.remove(postion);
			InShcoolnfoActivity.list4.add(postion, check);
			break;
		case 5:
			InShcoolnfoActivity.list5.remove(postion);
			InShcoolnfoActivity.list5.add(postion, check);
			break;
		case 6:
			InShcoolnfoActivity.list6.remove(postion);
			InShcoolnfoActivity.list6.add(postion, check);
			break;
		}
	}
}
