package com.withiter.quhao.task;

import java.util.Map;

import org.json.JSONException;

import android.content.Context;

import com.withiter.quhao.util.StringUtils;
import com.withiter.quhao.util.http.CommonHTTPRequest;

/**
 * 
 * @author Wang Jie Ze
 *
 */
public class DeleteCreditTask extends BaseTask {
	
//	private HashMap<String, String> mParams;
	private String url;
	
	public String result;

	public Map<String, String> params;
	/**
	 * 
	 * @param context
	 * @param params 
	 * @param action replay我的回复/thread我的发帖/favorites我的收藏
	 * @param page
	 * @param token
	 */
	public DeleteCreditTask(int preDialogMessage,Context context,String url, Map<String, String> params) {
		super(preDialogMessage,context);
		this.url = url;
		this.params = params;
//		mParams = new HashMap<String, String>();
//		mParams.put("apiname", API_NAME);
//		mParams.put("method", METHOD);
//		
//		mParams.put("action", action);
//		mParams.put("page", page);
//		mParams.put("token", token);
//		mParams.put("userid", userid);
//		System.out.println("Token"+token+"***************userid"+userid);
	}

	@Override
	public JsonPack getData() throws Exception {
//		String result = CommonHTTPRequest.post(url,params); // doGet(mParams);
		String result = CommonHTTPRequest.post(url,params);
		JsonPack jsonPack = getJsonPack(result);
		return jsonPack;
	}

	@Override
	public void onStateFinish(JsonPack result) {
		if(null != result&&result.getObj()!=null){
			this.result = result.getObj();
		}
	}
	
	private static JsonPack getJsonPack(String responseString)
			throws JSONException {
		JsonPack jp = new JsonPack();
		if (!StringUtils.isNull(responseString) && !"[]".equals(responseString) && !"null".equals(responseString)) {

			if (responseString instanceof String) {
				jp.setRe(200);
				jp.setMsg("success");
				jp.setObj(responseString);
			}
		}
		else
		{
			jp.setRe(400);
			jp.setMsg("error");
		}
		return jp;
	}

	@Override
	public void onStateError(JsonPack result) {
//		Toast.makeText(mContext, result.getMsg(), Toast.LENGTH_SHORT).show();
//		DialogUtil.showToast(mContext, result.getMsg());
	}

	@Override
	public void onPreStart() {
		// TODO Auto-generated method stub

	}

}
