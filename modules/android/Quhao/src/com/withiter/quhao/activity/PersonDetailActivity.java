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
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.withiter.quhao.QHClientApplication;
import com.withiter.quhao.R;
import com.withiter.quhao.domain.AccountInfo;
import com.withiter.quhao.util.ActivityUtil;
import com.withiter.quhao.util.QuhaoLog;
import com.withiter.quhao.util.StringUtils;
import com.withiter.quhao.util.tool.AsynImageLoader;
import com.withiter.quhao.util.tool.FileUtil;
import com.withiter.quhao.util.tool.ImageUtil;
import com.withiter.quhao.util.tool.ProgressDialogUtil;
import com.withiter.quhao.util.tool.QuhaoConstant;
import com.withiter.quhao.util.tool.SharedprefUtil;

public class PersonDetailActivity extends QuhaoBaseActivity {

	private final static String TAG = PersonDetailActivity.class.getName();

	private TextView nickNameText;
//	private TextView phoneText;
//	private TextView usualCityText;
	private TextView currentJifenText;

	private LinearLayout nickNameLayout;
	private LinearLayout currentJifenLayout;
	private LinearLayout jifenIntructionLayout;
//	private LinearLayout usualCityLayout;
//	private LinearLayout phoneLayout;
	private LinearLayout updatePasswordLayout;
	
	private Button logoutButton;
	
	private final int UNLOCK_CLICK = 1000;

	private LinearLayout photoLayout;
	
	private ImageView personAvatar;
	
	private String[] items = new String[] { "选择本地图片", "拍照" };

	/* 请求码 */
	private static final int IMAGE_REQUEST_CODE = 0;
	private static final int CAMERA_REQUEST_CODE = 1;
	private static final int RESULT_REQUEST_CODE = 2;
	
	private String currentTime;
	
	private String newImageName;
	
	private boolean isNeedtoRefresh = false;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		
		currentTime = String.valueOf(System.currentTimeMillis());
		
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.person_detail_layout);
		super.onCreate(savedInstanceState);

		photoLayout = (LinearLayout) this.findViewById(R.id.photoLayout);
		photoLayout.setOnClickListener(this);
		
		nickNameLayout = (LinearLayout) this.findViewById(R.id.nick_name_layout);
		nickNameLayout.setOnClickListener(this);
		
//		usualCityLayout = (LinearLayout) this.findViewById(R.id.usual_city_layout);
//		usualCityLayout.setOnClickListener(this);
		
//		phoneLayout = (LinearLayout) this.findViewById(R.id.phone_layout);
//		phoneLayout.setOnClickListener(this);
		
		currentJifenLayout = (LinearLayout) this.findViewById(R.id.current_jifen_layout);
		currentJifenLayout.setOnClickListener(this);
		currentJifenText = (TextView) this.findViewById(R.id.current_jifen);
		jifenIntructionLayout = (LinearLayout) this.findViewById(R.id.jifen_instruction_layout);
		jifenIntructionLayout.setOnClickListener(this);
		updatePasswordLayout = (LinearLayout) this.findViewById(R.id.update_password_layout);
		updatePasswordLayout.setOnClickListener(this);
		personAvatar = (ImageView) this.findViewById(R.id.person_avatar);
		
		nickNameText = (TextView) this.findViewById(R.id.nick_name);
		
//		usualCityText = (TextView) this.findViewById(R.id.usual_city);
//		phoneText = (TextView) this.findViewById(R.id.phone_number);
		
		logoutButton = (Button) this.findViewById(R.id.logout_btn);
		logoutButton.setOnClickListener(this);
		
		btnBack.setOnClickListener(goBack(this));
		setPersonDetail();
		
		isNeedtoRefresh = false;
	}

	@Override
	public void onClick(View v) {
		// 已经点过，直接返回
		if (isClick) {
			return;
		}

		// 设置已点击标志，避免快速重复点击
		isClick = true;

		if(!ActivityUtil.isNetWorkAvailable(getApplicationContext()))
		{
			Toast.makeText(getApplicationContext(), R.string.network_error_info, Toast.LENGTH_SHORT).show();
			unlockHandler.sendEmptyMessage(UNLOCK_CLICK);
			return;
		}
		switch (v.getId()) {
		case R.id.photoLayout:
			
			unlockHandler.sendEmptyMessageDelayed(UNLOCK_CLICK, 1000);
			currentTime = String.valueOf(System.currentTimeMillis());
			showChooseDialog();
//			this.finish();
			break;
		case R.id.nick_name_layout:
			progressDialogUtil = new ProgressDialogUtil(this, R.string.empty, R.string.waitting, false);
			progressDialogUtil.showProgress();
			if (QHClientApplication.getInstance().isLogined) {
				progressDialogUtil.closeProgress();
				unlockHandler.sendEmptyMessageDelayed(UNLOCK_CLICK, 1000);
				isNeedtoRefresh = true;
				Intent updateNickname = new Intent();
				updateNickname.setClass(this, UpdateNicknameActivity.class);
				startActivity(updateNickname);
				
			} else {
				progressDialogUtil.closeProgress();
				unlockHandler.sendEmptyMessageDelayed(UNLOCK_CLICK, 1000);
				AlertDialog.Builder builder = new Builder(this);
				builder.setTitle("温馨提示");
				builder.setMessage("请先登录");
				builder.setPositiveButton("确认", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
					}
				});
				builder.create().show();
			}
			break;
			/*
		case R.id.usual_city_layout:
			progressDialogUtil = new ProgressDialogUtil(this, R.string.empty, R.string.waitting, false);
			progressDialogUtil.showProgress();
			if (QHClientApplication.getInstance().isLogined) {
				progressDialogUtil.closeProgress();
				unlockHandler.sendEmptyMessageDelayed(UNLOCK_CLICK, 1000);

				Intent citySelect = new Intent();
				citySelect.setClass(this, CitySelectActivity.class);
				startActivity(citySelect);
				overridePendingTransition(R.anim.in_from_right, R.anim.out_to_left);
			} else {
				progressDialogUtil.closeProgress();
				unlockHandler.sendEmptyMessageDelayed(UNLOCK_CLICK, 1000);
				AlertDialog.Builder builder = new Builder(this);
				builder.setTitle("温馨提示");
				builder.setMessage("请先登录");
				builder.setPositiveButton("确认", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
					}
				});
				builder.create().show();
			}
			break;*/
		case R.id.current_jifen_layout:
			progressDialogUtil = new ProgressDialogUtil(this, R.string.empty, R.string.waitting, false);
			progressDialogUtil.showProgress();
			if (QHClientApplication.getInstance().isLogined) {
				progressDialogUtil.closeProgress();
				unlockHandler.sendEmptyMessageDelayed(UNLOCK_CLICK, 1000);
				Intent creditCost = new Intent();
				creditCost.setClass(this, CreditCostListActivity.class);
				startActivity(creditCost);
			} else {
				progressDialogUtil.closeProgress();
				unlockHandler.sendEmptyMessageDelayed(UNLOCK_CLICK, 1000);
				Builder dialog = new AlertDialog.Builder(this);
				dialog.setTitle("温馨提示");
				dialog.setMessage("请先登录");
				dialog.setPositiveButton("确认", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
					}
				});
				dialog.create().show();
			}
			break;
		case R.id.jifen_instruction_layout:
			progressDialogUtil = new ProgressDialogUtil(this, R.string.empty, R.string.waitting, false);
			progressDialogUtil.showProgress();
			if (QHClientApplication.getInstance().isLogined) {
				progressDialogUtil.closeProgress();
				unlockHandler.sendEmptyMessageDelayed(UNLOCK_CLICK, 1000);
				Intent jifenInstruction = new Intent();
				jifenInstruction.setClass(this, JifenInstructionActivity.class);
				startActivity(jifenInstruction);
			} else {
				progressDialogUtil.closeProgress();
				unlockHandler.sendEmptyMessageDelayed(UNLOCK_CLICK, 1000);
				Builder dialog = new AlertDialog.Builder(this);
				dialog.setTitle("温馨提示");
				dialog.setMessage("请先登录");
				dialog.setPositiveButton("确认", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
					}
				});
				dialog.create().show();
			}
			break;
		case R.id.update_password_layout:
			progressDialogUtil = new ProgressDialogUtil(this, R.string.empty, R.string.waitting, false);
			progressDialogUtil.showProgress();
			if (QHClientApplication.getInstance().isLogined) 
			{
				progressDialogUtil.closeProgress();
				unlockHandler.sendEmptyMessageDelayed(UNLOCK_CLICK, 1000);
				Intent updatePassword = new Intent();// TODO : wjzwjz
				updatePassword.setClass(this, UpdatePasswordActivity.class);
				startActivity(updatePassword);
			} 
			else 
			{
				progressDialogUtil.closeProgress();
				unlockHandler.sendEmptyMessageDelayed(UNLOCK_CLICK, 1000);
				Builder builder = new AlertDialog.Builder(this);
				builder.setTitle("温馨提示");
				builder.setMessage("已经登出");
				builder.setPositiveButton("确认", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
					}
				});
				builder.create().show();
				
//				Builder dialog = new AlertDialog.Builder(this);
//				dialog.setTitle("温馨提示").setMessage("请先登录").setPositiveButton("确定", null);
//				dialog.show();
			}
			break;
		case R.id.logout_btn:
			unlockHandler.sendEmptyMessageDelayed(UNLOCK_CLICK, 1000);
			if (QHClientApplication.getInstance().isLogined) {
				
				AlertDialog.Builder builder = new Builder(this);
				builder.setTitle("温馨提示");
				builder.setMessage("您确定要退出吗？");
				builder.setPositiveButton("确认", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
						logoutHandler.obtainMessage(200, null).sendToTarget();
					}});
				builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
					
					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
					}
				});
				builder.create().show();
				
			} else {
				unlockHandler.sendEmptyMessageDelayed(UNLOCK_CLICK, 1000);
				Builder builder = new AlertDialog.Builder(this);
				builder.setTitle("温馨提示");
				builder.setMessage("请先登录");
				builder.setPositiveButton("确认", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
					}
				});
				builder.create().show();
				
//				Builder dialog = new AlertDialog.Builder(this);
//				dialog.setTitle("温馨提示").setMessage("请先登录").setPositiveButton("确定", null);
//				dialog.show();
			}
			break;
		default:
			unlockHandler.sendEmptyMessageDelayed(UNLOCK_CLICK, 1000);
			break;
		}
	}

	protected Handler logoutHandler = new Handler() {
		public void handleMessage(Message msg) {
			if (msg.what == 200) {
				super.handleMessage(msg);
				
				QHClientApplication.getInstance().isLogined = false;
				QHClientApplication.getInstance().accountInfo = null;
//				unlockHandler.sendEmptyMessageDelayed(UNLOCK_CLICK, 1000);
				PersonDetailActivity.this.finish();
			}
		}
	};
	
	private void showChooseDialog() {

		new AlertDialog.Builder(this)
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
												.getExternalStorageDirectory() + "/" + QuhaoConstant.IMAGES_SD_URL + "/" + SharedprefUtil.get(PersonDetailActivity.this, QuhaoConstant.ACCOUNT_ID, "") + "_" + currentTime + "_" +
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
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		//结果码不等于取消时候
		if (resultCode != RESULT_CANCELED) {

			switch (requestCode) {
			case IMAGE_REQUEST_CODE:
				startPhotoZoom(data.getData());
				break;
			case CAMERA_REQUEST_CODE:
				if (FileUtil.hasSdcard()) {
					File tempFile = new File(Environment
							.getExternalStorageDirectory() + "/" + QuhaoConstant.IMAGES_SD_URL + "/" + SharedprefUtil.get(this, QuhaoConstant.ACCOUNT_ID, "") + "_" + currentTime + "_" +
							QuhaoConstant.PERSON_IMAGE_FILE_NAME);
					startPhotoZoom(Uri.fromFile(tempFile));
				} else {
					Toast.makeText(this, "未找到存储卡，无法存储照片！",
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
			progressDialogUtil = new ProgressDialogUtil(this, R.string.empty, R.string.waitting, false);
		}
		progressDialogUtil.showProgress();
		Bundle extras = data.getExtras();
		if (extras != null) {
			Bitmap photo = extras.getParcelable("data");

			FileOutputStream fos;
			File image = null;
			try {
				String accountId = SharedprefUtil.get(this,
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
			String accountId = SharedprefUtil.get(this,
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
						
						Log.e("wjzwjz", "request : " + request);
						
						if(!"error".equals(request))
						{
							SharedprefUtil.put(PersonDetailActivity.this, QuhaoConstant.USER_IMAGE, request);
							String userImage = QHClientApplication.getInstance().accountInfo.userImage; 
							if (StringUtils.isNotNull(userImage) && userImage.indexOf("fileName")>0) {
								String[] strs = userImage.split("fileName=");
								if (null != strs && strs.length>1) {
									userImage = strs[0] + "fileName=" + URLEncoder.encode(request,"UTF-8");
									QHClientApplication.getInstance().accountInfo.userImage = userImage;
								}
							}
							newImageName = request;
							updateNewImgHandler.sendEmptyMessage(200);
							
						}
						else
						{
							progressDialogUtil.closeProgress();
							Map<String, Object> toastParams = new HashMap<String, Object>();
							toastParams.put("activity", PersonDetailActivity.this);
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
			personAvatar.setImageBitmap(photo);
			
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
						personAvatar.setImageBitmap(bitmap);
					}
				}

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
	
	@Override
	protected void onResume() {
		
		super.onResume();
		if (isNeedtoRefresh) {
			setPersonDetail();
			isNeedtoRefresh = false;
		}
	}

	private void setPersonDetail() {
		
		AccountInfo account = QHClientApplication.getInstance().accountInfo;
		
		Bitmap bitmap = null;
		String fileName = "";
		// get cached image from SD card
		if (null != account && StringUtils.isNotNull(account.userImage) && FileUtil.hasSdcard()) {
			String[] strs = account.userImage.split("fileName=");
			if (strs != null && strs.length>1) {
				
				fileName = account.userImage.split("fileName=")[1];
				String localFileName = SharedprefUtil.get(this, QuhaoConstant.USER_IMAGE, "");
				
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
							personAvatar.setImageBitmap(bitmap);
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
		
		if(bitmap == null)
		{
			if(StringUtils.isNotNull(fileName))
			{
				SharedprefUtil.put(this, QuhaoConstant.USER_IMAGE, fileName);
			}
			AsynImageLoader.getInstance().showImageAsyn(personAvatar, 0,"" + account.userImage, R.drawable.person_avatar);
			
		}
		
		if(StringUtils.isNull(account.nickName))
		{
			nickNameText.setText(R.string.noname);
		}
		else
		{
			nickNameText.setText(account.nickName);
		}
		
//		usualCityText.setText(cityInfo.cityName);
		
//		phoneText.setText(account.phone);
//		if(StringUtils.isNull(account.phone))
//		{
//			phoneText.setText(R.string.nomobile);
//		}
		
		currentJifenText.setText(account.jifen);
		if(StringUtils.isNull(account.jifen))
		{
			currentJifenText.setText("0");
		}
		
	}

	@Override
	public boolean onTouch(View v, MotionEvent event) {
		return false;
	}
}
