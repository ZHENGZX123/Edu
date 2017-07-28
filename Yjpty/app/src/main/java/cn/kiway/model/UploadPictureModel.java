package cn.kiway.model;

import java.io.Serializable;

/**
 * 上传照片数据模型
 * 
 * @author Zao
 * */
public class UploadPictureModel implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	/**
	 * 图像地址
	 * */
	private String path;
	/**
	 * 状态
	 * */
	private int status;

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public UploadPictureModel(String path, int status) {
		super();
		this.path = path;
		this.status = status;
	}

	public interface UploadPictureStatus {
		/**
		 * 平常状态
		 * */
		public final int NORMAL = 0;
		/**
		 * 等待状态
		 * */
		public final int WAIT=1;
		
		/**
		 * 正在上传
		 * **/
		public final int UPLOADING = 2;
		/**
		 * 上传成功
		 * */
		public final int UPLOAD_SUCC = 3;
		/**
		 * 上传失败
		 * */
		public final int UPLOAD_ERR = 4;

	}

}
