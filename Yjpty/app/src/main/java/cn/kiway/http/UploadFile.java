package cn.kiway.http;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Map;

import org.json.JSONObject;

import cn.kiway.App;
import cn.kiway.activity.BaseActivity;
import cn.kiway.utils.Logger;
import cn.kiway.utils.ViewUtil;

public class UploadFile {
	// 上传代码，第一个参数，为要使用的URL，第二个参数，为表单内容，第三个参数为要上传的文件，可以上传多个文件，这根据需要页定
	public static boolean post(BaseActivity activity, String actionUrl,
			Map<String, String> params, Map<String, File> files,
			UploadCallBack back, App app) throws IOException {
		String BOUNDARY = java.util.UUID.randomUUID().toString();
		String PREFIX = "--", LINEND = "\r\n";
		String MULTIPART_FROM_DATA = "multipart/form-data";
		String CHARSET = "UTF-8";
		URL uri = new URL(actionUrl);
		HttpURLConnection conn = (HttpURLConnection) uri.openConnection();
		conn.setRequestProperty("Cookie", "JSESSIONID="
				+ app.getCookie().getValue());
		conn.setReadTimeout(20 * 1000);
		conn.setDoInput(true);// 允许输入
		conn.setDoOutput(true);// 允许输出
		conn.setUseCaches(false);
		conn.setRequestMethod("POST"); // Post方式
		conn.setRequestProperty("connection", "keep-alive");
		conn.setRequestProperty("Charsert", "UTF-8");
		conn.setRequestProperty("Content-Type", MULTIPART_FROM_DATA
				+ ";boundary=" + BOUNDARY);
		Logger.log("requestUrl::" + conn);
		Logger.log("params::::" + params.toString());
		Logger.log("JSESSIONID=" + App.getInstance().getCookie().getValue());
		// 首先组拼文本类型的参数
		StringBuilder sb = new StringBuilder();
		if (params != null) {
			for (Map.Entry<String, String> entry : params.entrySet()) {
				sb.append(PREFIX);
				sb.append(BOUNDARY);
				sb.append(LINEND);
				sb.append("Content-Disposition: form-data; name=\""
						+ entry.getKey() + "\"" + LINEND);
				sb.append("Content-Type: text/plain; charset=" + CHARSET
						+ LINEND);
				sb.append("Content-Transfer-Encoding: 8bit" + LINEND);
				sb.append(LINEND);
				sb.append(entry.getValue());
				sb.append(LINEND);
			}
		}
		DataOutputStream outStream = null;
		try {
			outStream = new DataOutputStream(conn.getOutputStream());
			outStream.write(sb.toString().getBytes());
		} catch (Exception e) {
			try {
				JSONObject jsonObject = new JSONObject();
				jsonObject.put("retcode", 2);
				back.uploadCallBack(jsonObject.toString(), actionUrl);
			} catch (Exception e1) {
				e.printStackTrace();
			}
		}

		Logger.log("图像数据地址" + files.toString());
		// 发送文件数据
		if (files != null) {
			int i = 0;
			for (Map.Entry<String, File> file : files.entrySet()) {
				StringBuilder sb1 = new StringBuilder();
				sb1.append(PREFIX);
				sb1.append(BOUNDARY);
				sb1.append(LINEND);
				sb1.append("Content-Disposition: form-data; name=\""
						+ file.getKey() + "\"; filename=\"" + file.getValue()
						+ "\"" + LINEND);
				Logger.log(file);
				sb1.append("Content-Type: multipart/form-data; charset="
						+ CHARSET + LINEND);
				sb1.append(LINEND);
				if (outStream != null)
					outStream.write(sb1.toString().getBytes());
				InputStream is = null;
				try {
					is = ViewUtil
							.Bitmap2InputStream(ViewUtil.getSmallBitmap(file
									.getValue().toString()), 70);
				} catch (Exception e) {
				}
				byte[] buffer = new byte[1024];
				int len = 0;
				if (buffer == null || is == null) {
					return false;
				}
				while ((len = is.read(buffer)) != -1) {
					outStream.write(buffer, 0, len);
				}
				is.close();
				outStream.write(LINEND.getBytes());
				i = i + 1;
			}
		}
		Logger.log(LINEND.getBytes());
		// 请求结束标志
		byte[] end_data = (PREFIX + BOUNDARY + PREFIX + LINEND).getBytes();
		outStream.write(end_data);
		outStream.flush();
		boolean success = false;
		// 得到响应码
		try {
			success = conn.getResponseCode() == 200;
		} catch (Exception e) {
			e.printStackTrace();
			try {
				JSONObject jsonObject = new JSONObject();
				jsonObject.put("retcode", 2);
				back.uploadCallBack(jsonObject.toString(), actionUrl);
			} catch (Exception e1) {
				e.printStackTrace();
			}
		}
		InputStream in = conn.getInputStream();
		InputStreamReader isReader = new InputStreamReader(in);
		BufferedReader bufReader = new BufferedReader(isReader);
		String line = null;
		String data = "";
		while ((line = bufReader.readLine()) != null)
			data += line;
		Logger.log("Response:::" + data);
		outStream.close();
		conn.disconnect();
		if (success) {
			try {
				back.uploadCallBack(data, actionUrl);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}else{
			try {
				JSONObject jsonObject = new JSONObject();
				jsonObject.put("retcode", 2);
				back.uploadCallBack(jsonObject.toString(), actionUrl);
			} catch (Exception e1) {
				e1.printStackTrace();
			}
		}
		return success;
	}

	public interface UploadCallBack {
		public void uploadCallBack(String data, String actionUrl)
				throws Exception;
	}
}
