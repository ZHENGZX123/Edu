package cn.kiway.utils;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.text.Selection;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.TranslateAnimation;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;
import cn.kiway.Yjpty.R;
import cn.kiway.activity.BaseActivity;
import cn.kiway.dialog.BaseDialog.SavePicCallBack;

/**
 * 视图工具集
 * 
 * @author Zao
 */
public class ViewUtil {
	private static Toast toast;

	public static InputStream Bitmap2InputStream(Bitmap bm, int quality) {
		if (bm == null)
			return null;
		try {
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			bm.compress(Bitmap.CompressFormat.PNG, quality, baos);
			InputStream is = new ByteArrayInputStream(baos.toByteArray());
			return is;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 获取 图像的位图并转化为位数组
	 */
	public static byte[] getImageBytes(ImageView view, int pinzhi, int w) {
		try {
			Drawable drawable = view.getDrawable();
			Bitmap bitmap = Bitmap.createBitmap(drawable.getIntrinsicHeight(),
					drawable.getIntrinsicHeight(), Config.ARGB_8888);
			Canvas canvas = new Canvas(bitmap);
			int h = (int) (((float) drawable.getIntrinsicHeight() / (float) drawable
					.getIntrinsicHeight()) * w);
			drawable.setBounds(0, 0, h, h);
			drawable.draw(canvas);
			ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
			bitmap.compress(CompressFormat.PNG, pinzhi, outputStream);
			return outputStream.toByteArray();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 控件的位移动画
	 */
	public static void moveFrontBg(View v, int startX, int toX, int startY,
			int toY) {
		TranslateAnimation anim = new TranslateAnimation(startX, toX, startY,
				toY);
		anim.setDuration(200);
		anim.setFillAfter(true);
		v.startAnimation(anim);
	}

	/**
	 * 图片的压缩 通用
	 */
	public static Bitmap revitionImageSize(String path, int size)
			throws IOException {
		// 取得图片
		BitmapFactory.Options options = new BitmapFactory.Options();
		options.inJustDecodeBounds = true;
		BitmapFactory.decodeStream(new FileInputStream(path), null, options);
		int i = 0;
		Bitmap bitmap = null;
		while (true) {
			if ((options.outWidth >> i <= size)
					&& (options.outHeight >> i <= size)) {
				options.inSampleSize = (int) Math.pow(2.0D, i);
				options.inJustDecodeBounds = false;
				bitmap = BitmapFactory.decodeStream(new FileInputStream(path),
						null, options);
				break;
			}
			i += 1;
		}
		if (bitmap == null) {
			options.inSampleSize = 1;
			options.inJustDecodeBounds = false;
			bitmap = BitmapFactory.decodeStream(new FileInputStream(path),
					null, options);
		}
		return bitmap;
	}

	/**
	 * 字节转bitmap
	 * */
	public static Bitmap revitionImageSize(byte[] data, int size)
			throws IOException {
		// 取得图片
		BitmapFactory.Options options = new BitmapFactory.Options();
		options.inJustDecodeBounds = true;
		BitmapFactory.decodeByteArray(data, 0, data.length, options);
		int i = 0;
		Bitmap bitmap = null;
		while (true) {
			if ((options.outWidth >> i <= size)
					&& (options.outHeight >> i <= size)) {
				options.inSampleSize = (int) Math.pow(2.0D, i);
				options.inJustDecodeBounds = false;
				bitmap = BitmapFactory.decodeByteArray(data, 0, data.length,
						options);
				break;
			}
			i += 1;
		}
		if (bitmap == null) {
			options.inSampleSize = 1;
			options.inJustDecodeBounds = false;
			bitmap = BitmapFactory.decodeByteArray(data, 0, data.length,
					options);
		}
		return bitmap;
	}

	/**
	 * bitmap 装btye数组
	 * */
	public static byte[] BitmapToByte(Bitmap bitmap) {
		ByteArrayOutputStream stream = new ByteArrayOutputStream();
		bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
		return stream.toByteArray();
	}

	/**
	 * 图像压缩，压缩尺寸不压缩质量
	 * */
	public static Bitmap getSmallBitmap(String filePath) {

		BitmapFactory.Options newOpts = new BitmapFactory.Options();
		// 开始读入图片，此时把options.inJustDecodeBounds 设回true了
		newOpts.inJustDecodeBounds = true;
		Bitmap bitmap = BitmapFactory.decodeFile(filePath, newOpts);// 此时返回bm为空

		newOpts.inJustDecodeBounds = false;
		int w = newOpts.outWidth;
		int h = newOpts.outHeight;
		// 现在主流手机比较多是800*480分辨率，所以高和宽我们设置为
		float hh = 800f;// 这里设置高度为800f
		float ww = 480f;// 这里设置宽度为480f
		// 缩放比。由于是固定比例缩放，只用高或者宽其中一个数据进行计算即可
		int be = 1;// be=1表示不缩放
		if (w > h && w > ww) {// 如果宽度大的话根据宽度固定大小缩放
			be = (int) (newOpts.outWidth / ww);
		} else if (w < h && h > hh) {// 如果高度高的话根据宽度固定大小缩放
			be = (int) (newOpts.outHeight / hh);
		}
		if (be <= 0)
			be = 1;
		newOpts.inSampleSize = be;// 设置缩放比例
		// 重新读入图片，注意此时已经把options.inJustDecodeBounds 设回false了
		bitmap = BitmapFactory.decodeFile(filePath, newOpts);
		return compressImage(bitmap);// 压缩好比例大小后再进行质量压缩
	}

	public static Bitmap compressImage(Bitmap image) {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		image.compress(Bitmap.CompressFormat.JPEG, 100, baos);// 质量压缩方法，这里100表示不压缩，把压缩后的数据存放到baos中
		int options = 100;
		while (baos.toByteArray().length / 1024 > 100) { // 循环判断如果压缩后图片是否大于300kb,大于继续压缩
			baos.reset();// 重置baos即清空baos
			image.compress(Bitmap.CompressFormat.JPEG, options, baos);// 这里压缩options%，把压缩后的数据存放到baos中
			options -= 10;// 每次都减少10
		}
		ByteArrayInputStream isBm = new ByteArrayInputStream(baos.toByteArray());// 把压缩后的数据baos存放到ByteArrayInputStream中
		Bitmap bitmap = BitmapFactory.decodeStream(isBm, null, null);// 把ByteArrayInputStream数据生成图片
		return bitmap;
	}

	/**
	 * 计算图片的缩放值
	 * */
	public static int calculateInSampleSize(BitmapFactory.Options options,
			int reqWidth, int reqHeight) {
		final int height = options.outHeight;
		final int width = options.outWidth;
		int inSampleSize = 1;

		if (height > reqHeight || width > reqWidth) {
			final int heightRatio = Math.round((float) height
					/ (float) reqHeight);
			final int widthRatio = Math.round((float) width / (float) reqWidth);
			inSampleSize = heightRatio < widthRatio ? heightRatio : widthRatio;
		}
		return inSampleSize;
	}

	public static Bitmap revitionImageSize(Context context, int rId, int size)
			throws IOException {
		BitmapFactory.Options options = new BitmapFactory.Options();
		options.inJustDecodeBounds = true;
		BitmapFactory.decodeResource(context.getResources(), rId, options);
		int i = 0;
		Bitmap bitmap = null;
		while (true) {
			if ((options.outWidth >> i <= size)
					&& (options.outHeight >> i <= size)) {
				options.inSampleSize = (int) Math.pow(2.0D, i);
				options.inJustDecodeBounds = false;
				bitmap = BitmapFactory.decodeResource(context.getResources(),
						rId, options);
				break;
			}
			i += 1;
		}
		return bitmap;
	}

	/**
	 * 图像大小压缩
	 */
	public static Bitmap compressImage(Bitmap image, int size) {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		image.compress(CompressFormat.JPEG, 100, baos);
		int options = 90;
		while (baos.toByteArray().length / 1024 > size) {
			baos.reset();
			image.compress(CompressFormat.JPEG, options, baos);// 这里压缩options%，把压缩后的数据存放到baos中
			options -= 10;
		}
		ByteArrayInputStream isBm = new ByteArrayInputStream(baos.toByteArray());// 把压缩后的数据baos存放到ByteArrayInputStream中
		Bitmap bitmap = BitmapFactory.decodeStream(isBm, null, null);// 把ByteArrayInputStream数据生成图片
		return bitmap;
	}

	/**
	 * 消息提示
	 * 
	 * @param context
	 *            上下文
	 * @param messageId
	 *            消息资源ID
	 */
	public static void showMessage(Context context, int messageId) {
		if (toast == null) {
			toast = Toast.makeText(context, messageId, Toast.LENGTH_LONG);
			// toast.setGravity(Gravity.CENTER, 0, 0);
		}
		toast.setText(messageId);
		toast.show();
	}

	/**
	 * 消息提示
	 * 
	 * @param context
	 *            上下文
	 * @param message
	 *            消息
	 */
	public static void showMessage(Context context, String message) {
		if (StringUtil.isEmpty(message))
			return;
		if (toast == null) {
			toast = Toast.makeText(context, message, Toast.LENGTH_LONG);
			// toast.setGravity(Gravity.CENTER, 0, 0);
		}
		toast.setText(message);
		toast.show();
	}

	/**
	 * 获取视图控件
	 * 
	 * @param activity
	 *            当前视图
	 * @param id
	 *            控件ID
	 */
	@SuppressWarnings("unchecked")
	public static <T> T findViewById(Activity activity, int id) {
		return (T) activity.findViewById(id);

	}

	@SuppressWarnings("unchecked")
	public static <T> T findViewById(View view, int id) {
		return (T) view.findViewById(id);

	}

	/**
	 * 获取文本内容
	 * 
	 * @param view
	 *            控件
	 */
	public static String getContent(TextView view) {
		return view.getText().toString();
	}

	public static String getContent(BaseActivity context, int id) {
		return getContent((TextView) findViewById(context, id));

	}

	public static String getContent(View view, int id) {
		return getContent((TextView) findViewById(view, id));
	}

	/**
	 * 设置文本
	 */
	public static void setContent(TextView view, int rId) {
		if (view != null)
			view.setText(rId);
	}

	public static void setContent(TextView view, String str) {
		if (view != null)
			view.setText(str);
	}

	public static void setContent(TextView view, Spanned str) {
		if (view != null)
			view.setText(str);
	}

	public static void setContent(BaseActivity context, int dId, int rId) {
		TextView view = findViewById(context, dId);
		setContent(view, rId);
	}

	public static void setContent(View context, int dId, int rId) {
		TextView view = findViewById(context, dId);
		setContent(view, rId);
	}

	public static void setContent(BaseActivity context, int dId, String str) {
		TextView view = findViewById(context, dId);
		setContent(view, str);
	}

	public static void setContent(BaseActivity context, int dId, Spanned str) {
		TextView view = findViewById(context, dId);
		setContent(view, str);
	}

	public static void setContent(View context, int dId, String str) {
		TextView view = findViewById(context, dId);
		setContent(view, str);
	}

	/**
	 * 设置文本提示
	 */
	public static void setContentHint(TextView view, int rId) {
		if (view != null)
			view.setHint(rId);
	}

	public static void setContentHint(TextView view, String str) {
		if (view != null)
			view.setHint(str);
	}

	public static void setContentHint(BaseActivity context, int dId, int rId) {
		TextView view = findViewById(context, dId);
		setContentHint(view, rId);
	}

	public static void setContentHint(View context, int dId, int rId) {
		TextView view = findViewById(context, dId);
		setContentHint(view, rId);
	}

	public static void setContentHint(BaseActivity context, int dId, String str) {
		TextView view = findViewById(context, dId);
		setContentHint(view, str);
	}

	public static void setContentHint(View context, int dId, String str) {
		TextView view = findViewById(context, dId);
		setContentHint(view, str);
	}

	/**
	 * 对比控件内容
	 * 
	 * @param activity
	 *            上下文
	 * @param ids
	 *            包含TextView控件ID的数组
	 */
	public static boolean compare(BaseActivity activity, Integer... ids) {
		if (ids == null || ids.length == 0)
			return false;
		String str = getContent(activity, ids[0]);
		boolean bl = true;
		for (Integer id : ids) {
			if (!str.equals(getContent(activity, id))) {
				bl = false;
				break;
			}
		}
		return bl;
	}

	/**
	 * 判断控件内容是否
	 */
	public static boolean hasValue(BaseActivity activity, Integer... ids) {
		for (int id : ids) {
			String str = getContent(activity, id);
			if (StringUtil.isEmpty(str))
				return false;
		}
		return true;
	}

	/**
	 * 布局返回视图
	 */
	@SuppressWarnings("unchecked")
	public static <T> T inflate(Context context, int layoutId) {
		try {
			return (T) LayoutInflater.from(context).inflate(layoutId, null);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 获取 图像的位图并转化为位数组
	 */
	public static byte[] getBytes(ImageView view) {
		try {
			Drawable drawable = view.getDrawable();
			Bitmap bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(),
					drawable.getIntrinsicHeight(), Config.ARGB_8888);
			Canvas canvas = new Canvas(bitmap);
			drawable.setBounds(0, 0, drawable.getIntrinsicWidth(),
					drawable.getIntrinsicHeight());
			drawable.draw(canvas);
			ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
			bitmap.compress(CompressFormat.PNG, 100, outputStream);
			return outputStream.toByteArray();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 隐藏软键盘
	 * 
	 * @param act
	 */
	public static void hideKeyboard(Activity act) {
		try {
			InputMethodManager imm = (InputMethodManager) act
					.getSystemService(Activity.INPUT_METHOD_SERVICE);
			View view = act.getCurrentFocus();
			if (view != null) {
				imm.hideSoftInputFromWindow(view.getWindowToken(),
						InputMethodManager.HIDE_NOT_ALWAYS);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 隐藏软键盘
	 * 
	 * @param act
	 */
	public static void hideKeyboard(Dialog dialog, Activity act) {
		try {
			InputMethodManager imm = (InputMethodManager) act
					.getSystemService(Activity.INPUT_METHOD_SERVICE);
			View view = dialog.getCurrentFocus();
			if (view != null) {
				imm.hideSoftInputFromWindow(view.getWindowToken(),
						InputMethodManager.HIDE_NOT_ALWAYS);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 显示软键盘
	 * 
	 * @param act
	 */
	public static void diaplyKeyboard(Activity act, View view) {
		try {
			InputMethodManager imm = (InputMethodManager) act
					.getSystemService(Activity.INPUT_METHOD_SERVICE);
			imm.showSoftInput(view, view.getHeight());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 设置文本域的文字颜色
	 * 
	 * @param view
	 *            要设置颜色的文本控件
	 * @param resources
	 *            资源管理器 可以NULL 为NULL时 corlor 需传递完整的颜色值
	 * @param corlor
	 *            颜色值得ID
	 * @param st
	 *            开始位置
	 * @param end
	 *            结束位置
	 */
	public static void setTextFontColor(TextView view, Resources resources,
			int corlor, int st, int end) throws Exception {
		SpannableStringBuilder builder = new SpannableStringBuilder(
				view.getText());
		int corlorValue = corlor;
		if (resources != null)
			corlorValue = resources.getColor(corlor);
		builder.setSpan(new ForegroundColorSpan(corlorValue), st, end,
				Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
		view.setText(builder);
	}

	public static void setTextFontColor(BaseActivity activity, int id,
			Resources resources, int corlor) throws Exception {
		Button view = findViewById(activity, id);
		SpannableStringBuilder builder = new SpannableStringBuilder(
				view.getText());
		int corlorValue = corlor;
		if (resources != null)
			corlorValue = resources.getColor(corlor);
		builder.setSpan(new ForegroundColorSpan(corlorValue), 0, view.length(),
				Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
		view.setText(builder);
	}

	/**
	 * 设置控件周围的图形
	 */
	public static void setArroundDrawable(TextView view, int rLeft, int rTop,
			int rRight, int rBottom) {
		Resources resources = view.getResources();
		Drawable left = null, top = null, right = null, bottom = null;
		if (rLeft != -1) {
			left = resources.getDrawable(rLeft);
		}
		if (rTop != -1) {
			top = resources.getDrawable(rTop);
		}
		if (rRight != -1) {
			right = resources.getDrawable(rRight);

		}
		if (rBottom != -1) {
			bottom = resources.getDrawable(rBottom);
		}
		view.setCompoundDrawablesWithIntrinsicBounds(left, top, right, bottom);
	}

	/**
	 * 设置控件周围的图形
	 */
	public static void setArroundDrawable(BaseActivity activity, int id,
			int rLeft, int rTop, int rRight, int rBottom) {
		TextView view = findViewById(activity, id);
		Resources resources = activity.getResources();
		Drawable left = null, top = null, right = null, bottom = null;
		if (rLeft != -1) {
			left = resources.getDrawable(rLeft);
		}
		if (rTop != -1) {
			top = resources.getDrawable(rTop);
		}
		if (rRight != -1) {
			right = resources.getDrawable(rRight);
		}
		if (rBottom != -1) {
			bottom = resources.getDrawable(rBottom);
		}
		view.setCompoundDrawablesWithIntrinsicBounds(left, top, right, bottom);
	}

	/**
	 * 设置控件周围的图形
	 */
	public static void setArroundDrawable(View v, int id, int rLeft, int rTop,
			int rRight, int rBottom) {
		TextView view = findViewById(v, id);
		Resources resources = v.getResources();
		Drawable left = null, top = null, right = null, bottom = null;
		if (rLeft != -1) {
			left = resources.getDrawable(rLeft);
		}
		if (rTop != -1) {
			top = resources.getDrawable(rTop);
		}
		if (rRight != -1) {
			right = resources.getDrawable(rRight);
		}
		if (rBottom != -1) {
			bottom = resources.getDrawable(rBottom);
		}
		view.setCompoundDrawablesWithIntrinsicBounds(left, top, right, bottom);
	}

	/**
	 * 隐藏视图控件
	 */
	public static void hideViews(int value, View... views) {
		if (views == null || views.length == 0)
			return;
		for (View view : views) {
			if (view != null)
				view.setVisibility(value);
		}
	}

	/**
	 * 设置radiobutton的选择状态
	 */
	public static void setCheckStatusRadioButton(Activity context, int rId,
			boolean bl) {
		RadioButton button = findViewById(context, rId);
		if (button != null)
			button.setChecked(true);

	}

	public static void setCheckStatusRadioButton(Activity context, boolean bl,
			int... ids) {
		if (ids != null && ids.length > 0) {
			for (int id : ids) {
				RadioButton button = findViewById(context, id);
				if (button != null)
					button.setChecked(bl);
			}
		}
	}

	public static void setCheckStatusRadioButton(View context, int rId,
			boolean bl) {
		RadioButton button = findViewById(context, rId);
		if (button != null)
			button.setChecked(true);
	}

	/**
	 * 文本编辑域获取焦点 并选择是否弹出输入法
	 * 
	 * @param editText
	 *            文本域
	 * @param showInput
	 *            是否弹出输入法
	 */
	public static void requestFoucus(EditText editText, boolean showInput) {
		editText.setFocusable(true);
		editText.setFocusableInTouchMode(true);
		editText.requestFocus();
		if (showInput) {
			InputMethodManager inputManager = (InputMethodManager) editText
					.getContext()
					.getSystemService(Context.INPUT_METHOD_SERVICE);
			inputManager.showSoftInput(editText, 0);
		}
	}

	/**
	 * 文本编辑框光标的位置
	 * 
	 * @param editText
	 *            文本域
	 * **/
	public static void setEditTexMarkerPosition(EditText editText) {
		CharSequence text = editText.getText();
		if (text instanceof Spannable) {
			Spannable spanText = (Spannable) text;
			Selection.setSelection(spanText, text.length());
		}
	}

	/**
	 * 设置男女标签
	 * */
	public static void setSexTag(TextView view, int sex) {
		if (view == null)
			return;
		switch (sex) {
		case 0:
			setArroundDrawable(view, R.drawable.sex, -1, -1, -1);
			setContent(view, R.string.anknow);
			break;
		case 1:
			setArroundDrawable(view, R.drawable.boy, -1, -1, -1);
			setContent(view, R.string.boy);
			break;

		case 2:
			setArroundDrawable(view, R.drawable.gril, -1, -1, -1);
			setContent(view, R.string.gril);
			break;
		}
	}

	public static void setEditTextSelection(List<String> strings,
			EditText editText, int position) {
		int selection = 0;
		if (strings == null || editText == null)
			return;
		for (int i = 0; i < strings.size(); i++) {
			System.out.println("选择的位置" + position);
			selection = selection + strings.get(i).length() + 1;
			System.out.println("文本的位置" + selection);
			System.out.println(selection > position);
			if (selection >= position) {
				System.out.println(selection - strings.get(i).length() - 1);
				System.out.println(selection);
				editText.setSelection(selection - strings.get(i).length() - 1,
						selection);
				editText.setSelection(selection - strings.get(i).length() - 1,
						selection);
			}
		}
	}

	/**
	 * 文件下载
	 * */
	public static void downloadFile(File f, String path, BaseActivity activity,
			SavePicCallBack back) {
		if (!f.exists()) {
			HttpURLConnection con = null;
			FileOutputStream fos = null;
			BufferedOutputStream bos = null;
			BufferedInputStream bis = null;
			try {
				URL url = new URL(path);
				con = (HttpURLConnection) url.openConnection();
				con.setConnectTimeout(5 * 1000);
				con.setReadTimeout(15 * 1000);
				con.setRequestProperty("Cookie", "JSESSIONID="
						+ activity.app.getCookie().getValue());
				if (con.getResponseCode() == HttpURLConnection.HTTP_OK) {
					bis = new BufferedInputStream(con.getInputStream());
					fos = new FileOutputStream(f);
					bos = new BufferedOutputStream(fos);
					byte[] b = new byte[1024];
					int length;
					while ((length = bis.read(b)) != -1) {
						bos.write(b, 0, length);
						bos.flush();
					}
					if (back != null) {
						back.savePicSuccess();
					}
				} else {
					if (back != null) {
						back.savePicFail();
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				try {
					if (bos != null)
						bos.close();
					if (fos != null)
						fos.close();
					if (bis != null)
						bis.close();
					if (con != null)
						con.disconnect();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

}
