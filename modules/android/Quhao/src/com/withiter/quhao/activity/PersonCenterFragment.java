package com.withiter.quhao.activity;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.withiter.quhao.QHClientApplication;
import com.withiter.quhao.R;
import com.withiter.quhao.domain.AccountInfo;
import com.withiter.quhao.util.ActivityUtil;
import com.withiter.quhao.util.QuhaoLog;
import com.withiter.quhao.util.StringUtils;
import com.withiter.quhao.util.http.CommonHTTPRequest;
import com.withiter.quhao.util.tool.FileUtil;
import com.withiter.quhao.util.tool.ImageUtil;
import com.withiter.quhao.util.tool.ParseJson;
import com.withiter.quhao.util.tool.ProgressDialogUtil;
import com.withiter.quhao.util.tool.QuhaoConstant;
import com.withiter.quhao.util.tool.SharedprefUtil;
import com.withiter.quhao.vo.LoginInfo;

public class PersonCenterFragment extends Fragment implements OnClickListener{

	private final static String TAG = PersonCenterFragment.class.getName();

	private TextView nickName;
	private TextView jifen;
	private ImageView label_qiandao;
	private TextView value_qiandao;
	private TextView value_dianpin;
	private TextView myAttention;

	private LoginInfo loginInfo;

	private LinearLayout signInLayout;
	private LinearLayout dianpingLayout;
	private LinearLayout currentPaiduiLayout;
	private LinearLayout historyPaiduiLayout;
	private LinearLayout creditCostLayout;
	private RelativeLayout personInfoLayout;
	private LinearLayout myAttentionLayout;
	
	private RelativeLayout personInfoLogoutLayout;
	
	private ImageView avatar;

	private Button loginBtn;
	private TextView regBtn;

	private final int UNLOCK_CLICK = 1000;
	
	private View contentView;
	
	private boolean isClick;
	
	private String[] items = new String[] { "选择本地图片", "拍照" };

	/* 请求码 */
	private static final int IMAGE_REQUEST_CODE = 0;
	private static final int CAMERA_REQUEST_CODE = 1;
	private static final int RESULT_REQUEST_CODE = 2;
	
	private String currentTime;
	
	private String newImageName;
	
	private boolean isNeedtoRefresh = false;
	
	protected ProgressDialogUtil progressDialogUtil;
	
	private LinearLayout infoDetailLayout;
	private LinearLayout rightLayout;
	
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
		
		currentTime = String.valueOf(System.currentTimeMillis());
		
		if(contentView != null)
		{
			ViewGroup vg = (ViewGroup) contentView.getParent();
			vg.removeView(contentView);
			return contentView;
		}
		
		contentView = inflater.inflate(R.layout.person_center_fragment_layout, null);
		
		QuhaoLog.d(TAG, "PersonCenterFragment onCreateView");
		isClick = false;
		
		nickName = (TextView) contentView.findViewById(R.id.nickName);
		jifen = (TextView) contentView.findViewById(R.id.jifen);
		
		label_qiandao = (ImageView) contentView.findViewById(R.id.qiandao_label);
		value_qiandao = (TextView) contentView.findViewById(R.id.value_qiandao);
		value_dianpin = (TextView) contentView.findViewById(R.id.value_dianpin);
		myAttention = (TextView) contentView.findViewById(R.id.my_attention);
		avatar = (ImageView) contentView.findViewById(R.id.avatar);
		avatar.setOnClickListener(this);
		signInLayout = (LinearLayout) contentView.findViewById(R.id.signInLayout);
		dianpingLayout = (LinearLayout) contentView.findViewById(R.id.dianpingLayout);

		infoDetailLayout = (LinearLayout) contentView.findViewById(R.id.info_detail_layout);
		rightLayout = (LinearLayout) contentView.findViewById(R.id.right_layout);
		infoDetailLayout.setOnClickListener(this);
		rightLayout.setOnClickListener(this);
		
		currentPaiduiLayout = (LinearLayout) contentView.findViewById(R.id.current_paidui_layout);
		historyPaiduiLayout = (LinearLayout) contentView.findViewById(R.id.history_paidui_layout);
		creditCostLayout = (LinearLayout) contentView.findViewById(R.id.credit_cost_layout);
		personInfoLayout = (RelativeLayout) contentView.findViewById(R.id.person_info);
		myAttentionLayout = (LinearLayout) contentView.findViewById(R.id.my_attention_layout);
		signInLayout.setOnClickListener(this);
		dianpingLayout.setOnClickListener(this);
		currentPaiduiLayout.setOnClickListener(this);
//		personInfoLayout.setOnClickListener(this);
		myAttentionLayout.setOnClickListener(this);
		historyPaiduiLayout.setOnClickListener(this);
		creditCostLayout.setOnClickListener(this);
		
		personInfoLogoutLayout = (RelativeLayout) contentView.findViewById(R.id.person_info_logout);
		loginBtn = (Button) contentView.findViewById(R.id.login);
		regBtn = (TextView) contentView.findViewById(R.id.register);

		loginBtn.setOnClickListener(this);
		regBtn.setOnClickListener(this);

		// other activity will invoke this method
		refreshUI();
		
		return contentView;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}
	
	public void refreshUI() {
		// if haven't login, prompt the login dialog
		// no need to check auto login from SharedPreference
		// because when APP start up, the action had been performed
		QuhaoLog.d(TAG, "QHClientApplication.getInstance().isLogined : " + QHClientApplication.getInstance().isLogined);
		if (QHClientApplication.getInstance().isLogined) {
			AccountInfo account = QHClientApplication.getInstance().accountInfo;
			if (account != null) {
				personInfoLogoutLayout.setVisibility(View.GONE);
				personInfoLayout.setVisibility(View.VISIBLE);
				updateUIData(account);
			} else {

				nickName.setText(R.string.noname);

				jifen.setText("0");

				avatar.setImageResource(R.drawable.person_avatar);
				value_qiandao.setText("签到(0)");
				value_dianpin.setText("点评(0)");
				myAttention.setText("关注(0)");
				personInfoLogoutLayout.setVisibility(View.VISIBLE);
				personInfoLayout.setVisibility(View.GONE);
			}
		}
		else
		{
			nickName.setText(R.string.noname);

			jifen.setText("0");

			avatar.setImageResource(R.drawable.person_avatar);
			value_qiandao.setText("签到(0)");
			value_dianpin.setText("点评(0)");
			myAttention.setText("关注(0)");
			
			personInfoLogoutLayout.setVisibility(View.VISIBLE);
			personInfoLayout.setVisibility(View.GONE);
		}

	}

	// update UI according to the account object
	private void updateUIData(AccountInfo account) {
		nickName.setText(account.nickName);
		if(StringUtils.isNull(account.nickName))
		{
			nickName.setText(R.string.noname);
		}

		QuhaoLog.d(TAG, "account.jifen : " + account.jifen);
		jifen.setText(account.jifen);

		Bitmap bitmap = null;
		String fileName = "";
		if(StringUtils.isNotNull(account.userImage))
		{
			if (FileUtil.hasSdcard()) {
				
				String[] strs = account.userImage.split("fileName=");
				if(strs != null && strs.length>1)
				{
					fileName = account.userImage.split("fileName=")[1];
					String localFileName = SharedprefUtil.get(getActivity(), QuhaoConstant.USER_IMAGE, "");
					if(localFileName.equals(fileName))
					{
						File f = new File(Environment.getExternalStorageDirectory() + "/" + 
								QuhaoConstant.IMAGES_SD_URL + "/" + fileName);
						QuhaoLog.d(TAG, "f.exists():" + f.exists());
						File folder = f.getParentFile();
						if (!folder.exists()) {
							folder.mkdirs();
						}
						
						if(f.exists()){
							bitmap = ImageUtil.decodeFile(f.getPath(),-1,128*128);
							if (null != bitmap) {
								avatar.setImageBitmap(bitmap);
							}
						}
					}
					else
					{
						File f = new File(Environment.getExternalStorageDirectory() + "/" + 
								QuhaoConstant.IMAGES_SD_URL + "/" + localFileName);
						QuhaoLog.d(TAG, "f.exists():" + f.exists());
						File folder = f.getParentFile();
						if (!folder.exists()) {
							folder.mkdirs();
						}
						
						if(f.exists()){
							f.delete();
						}
					}
				}
				
			}
		}
		
		if(null == bitmap)
		{
			if(StringUtils.isNotNull(fileName))
			{
				SharedprefUtil.put(getActivity(), QuhaoConstant.USER_IMAGE, fileName);
				ImageLoader.getInstance().displayImage(account.userImage, avatar);
//				AsynImageLoader.getInstance().showImageAsyn(avatar, 0, account.userImage, R.drawable.person_avatar);
			}
			
		}
		
		
		value_qiandao.setText("签到(" + account.signIn + ")");
		if ("true".equals(account.isSignIn)) {
			label_qiandao.setImageResource(R.drawable.ic_sign_up_gray);
//			label_qiandao.setTextColor(this.getResources().getColor(R.color.black));
			value_qiandao.setTextColor(this.getResources().getColor(R.color.black));
		}
		else
		{
			label_qiandao.setImageResource(R.drawable.ic_sign_up_red);
//			label_qiandao.setTextColor(this.getResources().getColor(R.color.red));
			value_qiandao.setTextColor(this.getResources().getColor(R.color.red));
		}
		value_dianpin.setText("点评( "+ account.dianping + ")");
		myAttention.setText("关注(" + account.guanzhu + ")");
	}
	
	private void signIn() {
		String accountId = SharedprefUtil.get(getActivity(), QuhaoConstant.ACCOUNT_ID, "");
		try {
			
			String result = CommonHTTPRequest.get("signIn?accountId=" + accountId);
			QuhaoLog.i(TAG, result);
			if (StringUtils.isNull(result)) {
			} else {
				loginInfo = ParseJson.getLoginInfo(result);
				AccountInfo account = new AccountInfo();
				account.build(loginInfo);
//						SharedprefUtil.put(PersonCenterActivity.this, QuhaoConstant.IS_LOGIN, "true");
				QHClientApplication.getInstance().accountInfo = account;
				QHClientApplication.getInstance().isLogined = true;

				QuhaoLog.i(TAG, loginInfo.msg);
				if (loginInfo.msg.equals("fail")) {
//							SharedprefUtil.put(PersonCenterActivity.this, QuhaoConstant.IS_LOGIN, "false");
					QHClientApplication.getInstance().isLogined = false;
					Toast.makeText(getActivity(),"签到失败", Toast.LENGTH_SHORT).show();
					return;
				}
				if (loginInfo.msg.equals("success")) {
					
					QHClientApplication.getInstance().isLogined = true;
					Toast.makeText(getActivity(), R.string.sign_in_success, Toast.LENGTH_SHORT).show();
					accountUpdateHandler.obtainMessage(200, account).sendToTarget();
				}
			}
		} catch (Exception e) {
			accountUpdateHandler.obtainMessage(200, null).sendToTarget();
			Toast.makeText(getActivity(), "签到失败", Toast.LENGTH_SHORT).show();
			e.printStackTrace();
		} finally {
			
			unlockHandler.sendEmptyMessageDelayed(UNLOCK_CLICK, 1000);
		}
	}
	
	private void queryAccountByAccountId() {
		
		if (!ActivityUtil.isNetWorkAvailable(getActivity())) {
			Toast.makeText(getActivity(), R.string.network_error_info, Toast.LENGTH_SHORT).show();
			return;
		}
		
		if(QHClientApplication.getInstance().isLogined)
		{
			String accountId = SharedprefUtil.get(getActivity(), QuhaoConstant.ACCOUNT_ID, "");
			if (StringUtils.isNull(accountId)) {
				QHClientApplication.getInstance().isLogined = false;
				Toast.makeText(getActivity(), "帐号超时，请重新登录", Toast.LENGTH_SHORT).show();
			}
			else
			{
				String url = "queryByAccountId?accountId=" + accountId;
				try {
					String result = CommonHTTPRequest.get(url);
					if(StringUtils.isNull(result)){
						QHClientApplication.getInstance().isLogined = false;
						Toast.makeText(getActivity(), "帐号超时，请重新登录", Toast.LENGTH_SHORT).show();
					}
					else
					{
						LoginInfo loginInfo = ParseJson.getLoginInfo(result);
						AccountInfo account = new AccountInfo();
						account.build(loginInfo);
						QuhaoLog.d(TAG, account.msg);

						if (account.msg.equals("fail")) {
//							SharedprefUtil.put(this, QuhaoConstant.IS_LOGIN, "false");
							QHClientApplication.getInstance().isLogined = false;
							Toast.makeText(getActivity(), "帐号超时，请重新登录", Toast.LENGTH_SHORT).show();
						}
						else if (account.msg.equals("success")) 
						{
							SharedprefUtil.put(getActivity(), QuhaoConstant.ACCOUNT_ID, loginInfo.accountId);
							SharedprefUtil.put(getActivity(), QuhaoConstant.PHONE, loginInfo.phone);
//							String encryptPassword = new DesUtils().decrypt(loginInfo.password);
//							SharedprefUtil.put(getActivity(), QuhaoConstant.PASSWORD, loginInfo.password);
							String isAutoLogin = SharedprefUtil.get(getActivity(), QuhaoConstant.IS_AUTO_LOGIN, "false");
							SharedprefUtil.put(getActivity(), QuhaoConstant.IS_AUTO_LOGIN, isAutoLogin);
							QHClientApplication.getInstance().accountInfo = account;
							QHClientApplication.getInstance().phone = loginInfo.phone;
							QHClientApplication.getInstance().isLogined = true;
							refreshUIHandler.sendEmptyMessage(UNLOCK_CLICK);
						}
						else
						{
							QHClientApplication.getInstance().isLogined = false;
							Toast.makeText(getActivity(), "帐号超时，请重新登录", Toast.LENGTH_SHORT).show();
						}
					}
					
				} catch (Exception e) {
					e.printStackTrace();
					QuhaoLog.e(TAG, e);
					QHClientApplication.getInstance().isLogined = false;
					Toast.makeText(getActivity(), "帐号超时，请重新登录", Toast.LENGTH_SHORT).show();
					
				}
			}
			
		}
		
		refreshUIHandler.sendEmptyMessage(UNLOCK_CLICK);
	}
	
	@Override
	public void onResume() {
		Thread thread = new Thread(new Runnable() {
			
			@Override
			public void run() {
				try
				{
					Looper.prepare();
					queryAccountByAccountId();
					
				}catch(Exception e)
				{
					QuhaoLog.e(TAG, e.getMessage());
				}finally
				{
					Looper.loop();
					unlockHandler.sendEmptyMessageDelayed(UNLOCK_CLICK, 1000);
				}
				
				
			}
		});
		thread.start();
		super.onResume();
	}

	private Handler accountUpdateHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			if (msg.what == 200) {
				super.handleMessage(msg);
				if (loginInfo.msg.equals("fail")) {
					QHClientApplication.getInstance().isLogined = false;
//					SharedprefUtil.put(PersonCenterActivity.this, QuhaoConstant.IS_LOGIN, "false");
					Toast.makeText(getActivity(), "登陆失败", Toast.LENGTH_SHORT).show();
					return;
				}
				if (loginInfo.msg.equals("success")) {
					nickName.setText(loginInfo.nickName);

					jifen.setText(loginInfo.jifen);

					if ("true".equals(loginInfo.isSignIn)) {
						label_qiandao.setImageResource(R.drawable.ic_sign_up_gray);
//						label_qiandao.setTextColor(getActivity().getResources().getColor(R.color.black));
						value_qiandao.setTextColor(getActivity().getResources().getColor(R.color.black));
						
					}
					else
					{
						label_qiandao.setImageResource(R.drawable.ic_sign_up_red);
//						label_qiandao.setTextColor(getActivity().getResources().getColor(R.color.red));
						value_qiandao.setTextColor(getActivity().getResources().getColor(R.color.red));
					}
					
					value_qiandao.setText("签到(" + loginInfo.signIn + ")");
					value_dianpin.setText("点评( "+ loginInfo.dianping + ")");
					myAttention.setText("关注(" + loginInfo.guanzhu + ")");
					
				}
			}
		}
	};
	
	protected Handler refreshUIHandler = new Handler() {
		public void handleMessage(Message msg) {
			if (msg.what == UNLOCK_CLICK) {
				refreshUI();
			}
		}
	};
	
	protected Handler unlockHandler = new Handler() {
		public void handleMessage(Message msg) {
			if (msg.what == UNLOCK_CLICK) {
				
				isClick = false;
			}
		}
	};
	
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		//结果码不等于取消时候
		if (resultCode != 0) {

			switch (requestCode) {
			case IMAGE_REQUEST_CODE:
				startPhotoZoom(data.getData());
				break;
			case CAMERA_REQUEST_CODE:
				if (FileUtil.hasSdcard()) {
					File tempFile = new File(Environment
							.getExternalStorageDirectory() + "/" + QuhaoConstant.IMAGES_SD_URL + "/" + SharedprefUtil.get(getActivity(), QuhaoConstant.ACCOUNT_ID, "") + "_" + currentTime + "_" +
							QuhaoConstant.PERSON_IMAGE_FILE_NAME);
					startPhotoZoom(Uri.fromFile(tempFile));
				} else {
					Toast.makeText(getActivity(), "未找到存储卡，无法存储照片！",
							Toast.LENGTH_SHORT).show();
				}

				break;
			case RESULT_REQUEST_CODE:
				if (data != null) {
					getImageToView(data);
				}
				break;
			}
		}
		super.onActivityResult(requestCode, resultCode, data);
	}

	/**
	 * 裁剪图片方法实现
	 * 
	 * @param uri
	 */
	public void startPhotoZoom(Uri uri) {

		Intent intent = new Intent("com.android.camera.action.CROP");
		intent.setDataAndType(uri, "image/*");
		// 设置裁剪
		intent.putExtra("crop", "true");
		// aspectX aspectY 是宽高的比例
		intent.putExtra("aspectX", 1);
		intent.putExtra("aspectY", 1);
		// outputX outputY 是裁剪图片宽高
		intent.putExtra("outputX", 100);
		intent.putExtra("outputY", 100);
		intent.putExtra("return-data", true);
		startActivityForResult(intent, 2);
	}

	/**
	 * 保存裁剪之后的图片数据
	 * 
	 * @param picdata
	 */
	private void getImageToView(Intent data) {
		if(progressDialogUtil == null)
		{
			progressDialogUtil = new ProgressDialogUtil(getActivity(), R.string.empty, R.string.waitting, false);
		}
		progressDialogUtil.showProgress();
		Bundle extras = data.getExtras();
		if (extras != null) {
			Bitmap photo = extras.getParcelable("data");

			FileOutputStream fos;
			File image = null;
			try {
				String accountId = SharedprefUtil.get(getActivity(),
						QuhaoConstant.ACCOUNT_ID, "");
				image = new File(Environment.getExternalStorageDirectory()
						+ "/" + QuhaoConstant.IMAGES_SD_URL + "/" + accountId
						+ "_" + currentTime + "_"
						+ QuhaoConstant.PERSON_IMAGE_FILE_NAME);
				File folder = image.getParentFile();

				if (!folder.exists()) {
					folder.mkdirs();
				}

				if (!image.exists()) {
					image.createNewFile();
				}
//				newImageName = image.getName();
				fos = new FileOutputStream(image);
				photo.compress(Bitmap.CompressFormat.PNG, 100, fos);
				fos.flush();
				fos.close();
			} catch (FileNotFoundException e) {
				progressDialogUtil.closeProgress();
			} catch (IOException e) {
				progressDialogUtil.closeProgress();
				e.printStackTrace();
			}

			final Map<String, String> params = new HashMap<String, String>();
			String accountId = SharedprefUtil.get(getActivity(),
					QuhaoConstant.ACCOUNT_ID, "");
			params.put("accountId", accountId);
			// params.put("userImage", QuhaoConstant.PERSON_IMAGE_FILE_NAME);

			final Map<String, File> files = new HashMap<String, File>();
			files.put("userImage", image);
			Thread thead = new Thread(new Runnable() {

				@Override
				public void run() {

					try {
						String request= "";
						Looper.prepare();
						request = post(QuhaoConstant.HTTP_URL + "updateUserImage", params, files);
						
						if(!"error".equals(request))
						{
							SharedprefUtil.put(getActivity(), QuhaoConstant.USER_IMAGE, request);
							String userImage = QHClientApplication.getInstance().accountInfo.userImage; 
							if (StringUtils.isNotNull(userImage) && userImage.indexOf("fileName")>0) {
								String[] strs = userImage.split("fileName=");
								if (null != strs && strs.length>1) {
									userImage = strs[0] + "fileName=" + URLEncoder.encode(request,"UTF-8");
									QHClientApplication.getInstance().accountInfo.userImage = userImage;
								}
							}
							newImageName = request;
//							updateNewImgHandler.sendEmptyMessage(200);
							progressDialogUtil.closeProgress();
							
						}
						else
						{
							progressDialogUtil.closeProgress();
							Map<String, Object> toastParams = new HashMap<String, Object>();
							toastParams.put("activity", getActivity());
							toastParams.put("text", "上传失败，请检查网络设置.");
							toastParams.put("toastLength", Toast.LENGTH_SHORT);
							toastStringHandler.obtainMessage(1000, toastParams).sendToTarget();
						}

					} catch (IOException e) {
						e.printStackTrace();
						progressDialogUtil.closeProgress();
					}

				}

			});
			thead.start();
			avatar.setImageBitmap(photo);
			
		}
	}
	
	protected Handler toastStringHandler = new Handler() {
		public void handleMessage(Message msg) {
			if (msg.what == UNLOCK_CLICK) {
				
				Map<String, Object> toastParams = (Map<String, Object>) msg.obj;
//				Toast.makeText((Context)toastParams.get("activity"), toastParams.get("text"), );
				
				Toast.makeText((Context)toastParams.get("activity"), (String) toastParams.get("text"), Integer.parseInt(String.valueOf(toastParams.get("toastLength")))).show();
			}
		}
	};
	
	/**
     * 通过拼接的方式构造请求内容，实现参数传输以及文件传输
     * 
     * @param url Service net address
     * @param params text content
     * @param files pictures
     * @return String result of Service response
     * @throws IOException
     */
    public static String post(String url, Map<String, String> params, Map<String, File> files)
            throws IOException {
    	InputStream is = null;
    	InputStream in = null;
    	DataOutputStream outStream = null;
    	HttpURLConnection conn = null;
    	try
    	{
    		String BOUNDARY = java.util.UUID.randomUUID().toString();
            String PREFIX = "--", LINEND = "\r\n";
            String MULTIPART_FROM_DATA = "multipart/form-data";
            String CHARSET = "UTF-8";


            URL uri = new URL(url);
            conn = (HttpURLConnection) uri.openConnection();
            conn.setReadTimeout(10 * 1000); // 缓存的最长时间
            conn.setDoInput(true);// 允许输入
            conn.setDoOutput(true);// 允许输出
            conn.setUseCaches(false); // 不允许使用缓存
            conn.setRequestMethod("POST");
            conn.setRequestProperty("user-agent", "QuhaoAndroid");
            conn.setRequestProperty("connection", "keep-alive");
            conn.setRequestProperty("Charsert", "UTF-8");
            conn.setRequestProperty("Content-Type", MULTIPART_FROM_DATA + ";boundary=" + BOUNDARY);


            // 首先组拼文本类型的参数
            StringBuilder sb = new StringBuilder();
            
            for (Map.Entry<String, String> entry : params.entrySet()) {
                sb.append(PREFIX);
                sb.append(BOUNDARY);
                sb.append(LINEND);
                sb.append("Content-Disposition: form-data; name=\"" + entry.getKey() + "\"" + LINEND);
                sb.append("Content-Type: text/plain; charset=" + CHARSET + LINEND);
                sb.append("Content-Transfer-Encoding: 8bit" + LINEND);
                sb.append(LINEND);
                sb.append(entry.getValue());
                sb.append(LINEND);
            }

            String fileName = "";
            outStream = new DataOutputStream(conn.getOutputStream());
            outStream.write(sb.toString().getBytes());
            // 发送文件数据
            if (files != null)
                for (Map.Entry<String, File> file : files.entrySet()) {
                    StringBuilder sb1 = new StringBuilder();
                    sb1.append(PREFIX);
                    sb1.append(BOUNDARY);
                    sb1.append(LINEND);
//                    sb1.append("Content-Disposition: form-data; name=\"" + file.getKey() + "\"; filename=\""
//                            + file.getValue().getName() + "\"" + LINEND);
                    sb1.append("Content-Disposition: form-data; name=\"" + file.getKey() + "\"; filename=\"" +file.getValue().getName() +"\"" + LINEND);
                    sb1.append("Content-Type: application/octet-stream; charset=" + CHARSET + LINEND);
                    sb1.append(LINEND);
                    outStream.write(sb1.toString().getBytes());
                    if(StringUtils.isNull(fileName))
                    {
                    	fileName = file.getValue().getName();
                    }

                    is = new FileInputStream(file.getValue());
                    byte[] buffer = new byte[1024];
                    int len = 0;
                    while ((len = is.read(buffer)) != -1) {
                        outStream.write(buffer, 0, len);
                    }

                    is.close();
                    outStream.write(LINEND.getBytes());
                }


            // 请求结束标志
            byte[] end_data = (PREFIX + BOUNDARY + PREFIX + LINEND).getBytes();
            outStream.write(end_data);
            outStream.flush();
            // 得到响应码
            int res = conn.getResponseCode();
            in = conn.getInputStream();
            StringBuilder sb2 = new StringBuilder();
            if (res == 200) {
                int ch;
                while ((ch = in.read()) != -1) {
                    sb2.append((char) ch);
                }
            }
            
            outStream.close();
            conn.disconnect();
            if ("success".equals(sb2.toString())) {
    			return fileName;
    		}
            return "error";
    	}catch(IOException e)
    	{
    		return "error";
    	}
    	finally
    	{
    		if (is!=null) {
				is.close();
			}
    		
    		if (in!=null) {
    			in.close();
			}
    		
    		if (outStream!=null) {
    			outStream.close();
			}
    		
    		if (conn!=null) {
    			conn.disconnect();
			}
    	}
         
    }
    
	private Handler updateNewImgHandler = new Handler()
	{

		@Override
		public void handleMessage(Message msg) {

			super.handleMessage(msg);
			progressDialogUtil.closeProgress();
			if (FileUtil.hasSdcard()
					&& StringUtils.isNotNull(newImageName)) {
				Bitmap bitmap = null;
				File f = new File(Environment.getExternalStorageDirectory()
						+ "/" + QuhaoConstant.IMAGES_SD_URL + "/"
						+ newImageName);
				QuhaoLog.d(TAG, "f.exists():" + f.exists());
				File folder = f.getParentFile();
				if (!folder.exists()) {
					folder.mkdirs();
				}

				if (f.exists()) {
					
					bitmap = ImageUtil.decodeFile(f.getPath(),-1,128*128);
					if (null != bitmap) {
						avatar.setImageBitmap(bitmap);
					}
				}

			}
		}

	};
	
	private void showChooseDialog() {

		new AlertDialog.Builder(getActivity())
				.setTitle("设置头像")
				.setItems(items, new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						switch (which) {
						case 0:
							Intent intentFromGallery = new Intent();
							intentFromGallery.setType("image/*"); // 设置文件类型
							intentFromGallery
									.setAction(Intent.ACTION_GET_CONTENT);
							startActivityForResult(intentFromGallery,
									IMAGE_REQUEST_CODE);
							break;
						case 1:

							Intent intentFromCapture = new Intent(
									MediaStore.ACTION_IMAGE_CAPTURE);
							// 判断存储卡是否可以用，可用进行存储
							if (FileUtil.hasSdcard()) {

								intentFromCapture.putExtra(
										MediaStore.EXTRA_OUTPUT,
										Uri.fromFile(new File(Environment
												.getExternalStorageDirectory() + "/" + QuhaoConstant.IMAGES_SD_URL + "/" + SharedprefUtil.get(getActivity(), QuhaoConstant.ACCOUNT_ID, "") + "_" + currentTime + "_" +
												QuhaoConstant.PERSON_IMAGE_FILE_NAME)));
							}

							startActivityForResult(intentFromCapture,
									CAMERA_REQUEST_CODE);
							break;
						}
					}
				})
				.setNegativeButton("取消", new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
					}
				}).show();

	}
	
	@Override
	public void onClick(View v) {
		// 已经点过，直接返回
		if (isClick) {
			return;
		}

		// 设置已点击标志，避免快速重复点击
		isClick = true;
		
		switch (v.getId()) {
		case R.id.login:
			
			unlockHandler.sendEmptyMessageDelayed(UNLOCK_CLICK, 1000);
			Intent intent = new Intent(getActivity(), LoginActivity.class);
			intent.putExtra("activityName", this.getClass().getName());
			intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
			startActivity(intent);
//			this.finish();
			break;
		case R.id.info_detail_layout:
			if (QHClientApplication.getInstance().isLogined) {
				unlockHandler.sendEmptyMessageDelayed(UNLOCK_CLICK, 1000);
				Intent intent1 = new Intent(getActivity(), PersonDetailActivity.class);
				intent1.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
				startActivity(intent1);
				
			} else {
				unlockHandler.sendEmptyMessageDelayed(UNLOCK_CLICK, 1000);
				Intent login1 = new Intent(getActivity(), LoginActivity.class);
				login1.putExtra("activityName", this.getClass().getName());
				login1.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
				startActivity(login1);
			}
			break;
		case R.id.right_layout:
			if (QHClientApplication.getInstance().isLogined) {
				unlockHandler.sendEmptyMessageDelayed(UNLOCK_CLICK, 1000);
				Intent intent1 = new Intent(getActivity(), PersonDetailActivity.class);
				intent1.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
				startActivity(intent1);
				
			} else {
				unlockHandler.sendEmptyMessageDelayed(UNLOCK_CLICK, 1000);
				Intent login1 = new Intent(getActivity(), LoginActivity.class);
				login1.putExtra("activityName", this.getClass().getName());
				login1.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
				startActivity(login1);
			}
			break;
		case R.id.register:
			
			unlockHandler.sendEmptyMessageDelayed(UNLOCK_CLICK, 1000);
			Intent intentReg = new Intent(getActivity(), RegisterActivity.class);
			intentReg.putExtra("activityName", this.getClass().getName());
			intentReg.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
			startActivity(intentReg);
			break;
		case R.id.signInLayout:
			if (QHClientApplication.getInstance().isLogined) {
				AccountInfo account = QHClientApplication.getInstance().accountInfo;
				if(account!=null)
				{
					if("true".equals(account.isSignIn))
					{
						unlockHandler.sendEmptyMessageDelayed(UNLOCK_CLICK, 1000);
						Builder dialog = new AlertDialog.Builder(getActivity());
						dialog.setTitle("温馨提示").setMessage("亲，今天已经签过了哦！").setPositiveButton("确定", null);
						dialog.show();
					}
					else
					{
						Thread thread = new Thread(new Runnable() {
							
							@Override
							public void run() {
								try
								{
									Looper.prepare();
									signIn();
								}
								catch(Exception e)
								{
									QuhaoLog.e(TAG, e.getMessage());
								}
								finally
								{
									Looper.loop();
								}
								
								
							}
						});
						thread.start();
					}
				}
				
			} else {
				
				unlockHandler.sendEmptyMessageDelayed(UNLOCK_CLICK, 1000);
				Intent login2 = new Intent(getActivity(), LoginActivity.class);
				login2.putExtra("activityName", this.getClass().getName());
				login2.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
				startActivity(login2);
			}
			break;
		case R.id.dianpingLayout:
			if (QHClientApplication.getInstance().isLogined) {
				unlockHandler.sendEmptyMessageDelayed(UNLOCK_CLICK, 1000);
				
				if ("0".equals(QHClientApplication.getInstance().accountInfo.dianping)) {
					return;
				}
				
				Intent intentComment = new Intent();
				intentComment.putExtra("accountId", QHClientApplication.getInstance().accountInfo.accountId);
				intentComment.setClass(getActivity(), CommentsAccountActivity.class);
				startActivity(intentComment);
				
			} else {
				unlockHandler.sendEmptyMessageDelayed(UNLOCK_CLICK, 1000);
				Intent login3 = new Intent(getActivity(), LoginActivity.class);
				login3.putExtra("activityName", this.getClass().getName());
				login3.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
				startActivity(login3);
			}
			break;
		case R.id.my_attention_layout:
			if (QHClientApplication.getInstance().isLogined) {
				unlockHandler.sendEmptyMessageDelayed(UNLOCK_CLICK, 1000);
				
				Intent intentAttention = new Intent();
				intentAttention.putExtra("accountId", QHClientApplication.getInstance().accountInfo.accountId);
				intentAttention.setClass(getActivity(), MyAttentionListActivity.class);
				startActivity(intentAttention);
				
			} else {
				unlockHandler.sendEmptyMessageDelayed(UNLOCK_CLICK, 1000);
				Intent login3 = new Intent(getActivity(), LoginActivity.class);
				login3.putExtra("activityName", this.getClass().getName());
				login3.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
				startActivity(login3);
			}
			break;
		case R.id.current_paidui_layout:
			if (QHClientApplication.getInstance().isLogined) {
				unlockHandler.sendEmptyMessageDelayed(UNLOCK_CLICK, 1000);

				Intent intentCurrent = new Intent();
				intentCurrent.putExtra("queryCondition", "current");
				intentCurrent.setClass(getActivity(), QuhaoCurrentStatesActivity.class);
				startActivity(intentCurrent);
			} else {
				unlockHandler.sendEmptyMessageDelayed(UNLOCK_CLICK, 1000);
				Intent login4 = new Intent(getActivity(), LoginActivity.class);
				login4.putExtra("activityName", this.getClass().getName());
				login4.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
				startActivity(login4);
			}
			break;
		case R.id.history_paidui_layout:
			if (QHClientApplication.getInstance().isLogined) {
				unlockHandler.sendEmptyMessageDelayed(UNLOCK_CLICK, 1000);
				Intent intentHistory = new Intent();
				intentHistory.putExtra("queryCondition", "history");
				intentHistory.setClass(getActivity(), QuhaoHistoryStatesActivity.class);
				startActivity(intentHistory);
			} else {
				unlockHandler.sendEmptyMessageDelayed(UNLOCK_CLICK, 1000);
				Intent login4 = new Intent(getActivity(), LoginActivity.class);
				login4.putExtra("activityName", this.getClass().getName());
				login4.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
				startActivity(login4);
			}
			break;
		case R.id.credit_cost_layout:
			if (QHClientApplication.getInstance().isLogined) {
				unlockHandler.sendEmptyMessageDelayed(UNLOCK_CLICK, 1000);
				Intent intentCredit = new Intent();
				intentCredit.setClass(getActivity(), CreditCostListActivity.class);
				startActivity(intentCredit);
			} else {
				unlockHandler.sendEmptyMessageDelayed(UNLOCK_CLICK, 1000);
				Intent login5 = new Intent(getActivity(), LoginActivity.class);
				login5.putExtra("activityName", this.getClass().getName());
				login5.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
				startActivity(login5);
				
//				Builder dialog = new AlertDialog.Builder(this);
//				dialog.setTitle("温馨提示").setMessage("请先登录").setPositiveButton("确定", null);
//				dialog.show();
			}
			break;
		case R.id.avatar:
			currentTime = String.valueOf(System.currentTimeMillis());
			showChooseDialog();
			unlockHandler.sendEmptyMessageDelayed(UNLOCK_CLICK, 1000);
			break;
		default:
			unlockHandler.sendEmptyMessageDelayed(UNLOCK_CLICK, 1000);
			break;
		}
	}	
}