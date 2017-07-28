package cn.kiway.http;

public interface HttpHandler {
	/**
	 * 网络请求数据返回错误
	 * */
	public void httpErr(HttpResponseModel message) throws Exception;

	/**
	 * 网络请求数据返回成功
	 * 
	 * @param message
	 *            返回的Message序列化对象
	 * */
	public void httpSuccess(HttpResponseModel message) throws Exception;
	/**
	 * 请求错误
	 * */
	public void HttpError(HttpResponseModel message) throws Exception;
}
