package cn.kiway.yjhz.utils;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public interface IConstant {
	/**
	 * 盒子图片保存路径
	 * */
	public String Yjhz = "yjhz";
	/**
	 * 下载图片路径
	 * */
	public String photo = "photo";
	/**
	 * 缓存线程
	 * */
	public ExecutorService executorService = Executors.newCachedThreadPool();
	public String BUNDLE_PARAMS = "bundle_params";
	public String BUNDLE_PARAMS1 = "bundle_params1";
}
