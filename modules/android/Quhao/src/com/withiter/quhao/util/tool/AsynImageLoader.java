package com.withiter.quhao.util.tool;

import java.lang.ref.ReferenceQueue;
import java.lang.ref.SoftReference;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

import com.withiter.quhao.QHClientApplication;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;

public class AsynImageLoader {
	private static final String TAG = "AsynImageLoader";
	private static AsynImageLoader asynImageLoader;
	// 缓存下载过的图片的Map
	// 任务队列
	private List<Task> taskQueue;
	private boolean isRunning = false;

	/** 用于Chche内容的存储 */
	private Hashtable<String, BtimapRef> bitmapRefs;
	/** 垃圾Reference的队列（所引用的对象已经被回收，则将该引用存入队列中） */
	private ReferenceQueue<Bitmap> q;

	/**
	 * 继承SoftReference，使得每一个实例都具有可识别的标识。
	 */
	private class BtimapRef extends SoftReference<Bitmap> {
		private String _key = "";

		public BtimapRef(Bitmap bmp, ReferenceQueue<Bitmap> q, String key) {
			super(bmp, q);
			_key = key;
		}
	}

	private AsynImageLoader() {
		// 初始化变量
		bitmapRefs = new Hashtable<String, BtimapRef>();
		q = new ReferenceQueue<Bitmap>();
		taskQueue = new ArrayList<AsynImageLoader.Task>();
		// 启动图片下载线程
		isRunning = true;
		new Thread(runnable).start();
	}

	/**
	 * 取得缓存器实例
	 */
	public static AsynImageLoader getInstance() {
		if (asynImageLoader == null) {
			asynImageLoader = new AsynImageLoader();
		}
		return asynImageLoader;

	}

	/**
	 * 
	 * @param imageView
	 *            需要延迟加载图片的对象
	 * @param url
	 *            图片的URL地址
	 * 
	 * @param roundedType
	 *            圆角图片处理方式
	 * @param resId
	 *            图片加载过程中显示的图片资源
	 */
	public void showImageAsyn(ImageView imageView, String url,
			String roundedType, int resId, int itemWidth) {
		if(null == imageView)
		{
			return;
		}

		if(null == url || "".equals(url.trim()))
		{
			imageView.setImageResource(resId);
			return;
		}
		imageView.setTag(url);
		Bitmap bitmap = loadImageAsyn(url, roundedType, getImageCallback(
				imageView, resId), itemWidth);

		if (bitmap == null) {
			imageView.setImageResource(resId);
		} else {
			if (itemWidth > 0) {
				int width = bitmap.getWidth();// 获取真实宽高
				int height = bitmap.getHeight();
				LayoutParams lp = imageView.getLayoutParams();
				lp.height = (height * itemWidth) / width;// 调整高度

				imageView.setLayoutParams(lp);

				imageView.setImageBitmap(bitmap);
			} else {
				imageView.setImageBitmap(bitmap);
			}

		}
	}

	public Bitmap getBitmapAsyn(final String path, final int resId) {
		// 判断缓存中是否已经存在该图片
		if (bitmapRefs.containsKey(path)) {
			// 取出软引用
			BtimapRef rf = bitmapRefs.get(path);
			// 通过软引用，获取图片
			Bitmap bitmap = rf.get();
			// 如果该图片已经被释放，则将该path对应的键从Map中移除掉
			if (bitmap == null) {
				bitmapRefs.remove(path);
			} else {
				// 如果图片未被释放，直接返回该图片
				Log.i(TAG, "return image in cache" + path);
				return bitmap;
			}
		}
		else if(FileUtil.exists4ImageUrl(path))
		{
			Bitmap bitmap = FileUtil.getImageBitmap(path);
			if(null != bitmap)
			{
				return bitmap;
			}
		}
		else {
			// 如果缓存中不常在该图片，则创建图片下载任务
			Task task = new Task();
			task.path = path;
			task.roundedType = "rect";
			task.callback = new ImageCallback() {

				@Override
				public void loadImage(String _path, Bitmap bitmap, int itemWidth) {
					if (_path.equals(path)) {

					} else {
						addCacheBitmap(BitmapFactory
								.decodeResource(QHClientApplication
										.getInstance().getApplicationContext()
										.getResources(), resId), path);
					}
				}
			};
			task.itemWidth = 0;
			Log.i(TAG, "new Task ," + path);
			if (!taskQueue.contains(task)) {
				taskQueue.add(task);
				// 唤醒任务下载队列
				synchronized (runnable) {
					runnable.notify();
				}
			}
		}

		// 缓存中没有图片则返回null
		return null;
	}

	/**
	 * 获取原图
	 * 
	 * @param imageView
	 * @param url
	 * @param resId
	 */
	public void showImageAsyn(ImageView imageView, int position,String url, int resId) {
		showImageAsyn(imageView, url, "rect", resId, 0,position);
	}

	private void showImageAsyn(ImageView imageView, String url,
			String roundedType, int resId, int itemWidth, int position) {
		if(null == imageView)
		{
			return;
		}

		imageView.setScaleType(ScaleType.FIT_XY);
		if(null == url || "".equals(url.trim()))
		{
			imageView.setImageResource(resId);
			return;
		}
		imageView.setTag(url);
		Bitmap bitmap = loadImageAsyn(url, roundedType, getImageCallback(
				imageView, resId), itemWidth);

		if (bitmap == null) {
			imageView.setImageResource(resId);
		} else {
			if (itemWidth > 0) {
				int width = bitmap.getWidth();// 获取真实宽高
				int height = bitmap.getHeight();
				LayoutParams lp = imageView.getLayoutParams();
				lp.height = (height * itemWidth) / width;// 调整高度

				imageView.setLayoutParams(lp);

				imageView.setImageBitmap(bitmap);
			} else {
				imageView.setImageBitmap(bitmap);
			}

		}
	}

	public Bitmap loadImageAsyn(String path, String roundedType,
			ImageCallback callback, int itemWidth) {
		// 判断缓存中是否已经存在该图片
		if (bitmapRefs.containsKey(path)) {
			// 取出软引用
			BtimapRef rf = bitmapRefs.get(path);
			// 通过软引用，获取图片
			Bitmap bitmap = rf.get();
			// 如果该图片已经被释放，则将该path对应的键从Map中移除掉
			if (bitmap == null) {
				bitmapRefs.remove(path);
			} else {
				// 如果图片未被释放，直接返回该图片
				Log.i(TAG, "return image in cache" + path);
				return bitmap;
			}
		}
		else if(FileUtil.exists4ImageUrl(path))
		{
			Bitmap bitmap = FileUtil.getImageBitmap(path);
			if(null != bitmap)
			{
				return bitmap;
			}
		}
		else {
			// 如果缓存中不常在该图片，则创建图片下载任务
			Task task = new Task();
			task.path = path;
			task.roundedType = roundedType;
			task.callback = callback;
			task.itemWidth = itemWidth;
			Log.i(TAG, "new Task ," + path);
			if (!taskQueue.contains(task)) {
				taskQueue.add(task);
				// 唤醒任务下载队列
				synchronized (runnable) {
					runnable.notify();
				}
			}
		}

		// 缓存中没有图片则返回null
		return null;
	}

	/**
	 * 
	 * @param imageView
	 * @param resId
	 *            图片加载完成前显示的图片资源ID
	 * @return
	 */
	private ImageCallback getImageCallback(final ImageView imageView,
			final int resId) {
		return new ImageCallback() {

			@Override
			public void loadImage(String path, Bitmap bitmap, int itemWidth) {
				if (path.equals(imageView.getTag().toString())) {
					if (itemWidth > 0) {
						if (bitmap != null) {
							int width = bitmap.getWidth();// 获取真实宽高
							int height = bitmap.getHeight();
							LayoutParams lp = imageView.getLayoutParams();
							lp.height = (height * itemWidth) / width;// 调整高度

							imageView.setLayoutParams(lp);

							imageView.setImageBitmap(bitmap);

						}
					} else {
						imageView.setImageBitmap(bitmap);
					}

				} else {
					imageView.setImageResource(resId);
				}
			}
		};
	}

	private Handler handler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			// 子线程中返回的下载完成的任务
			Task task = (Task) msg.obj;
			// 调用callback对象的loadImage方法，并将图片路径和图片回传给adapter
			task.callback.loadImage(task.path, task.bitmap, task.itemWidth);
		}

	};

	private Runnable runnable = new Runnable() {

		@Override
		public void run() {
			try {
				Thread.sleep(100);
			} catch (InterruptedException e1) {
				e1.printStackTrace();
			}
			while (isRunning) {
				// 当队列中还有未处理的任务时，执行下载任务
				while (taskQueue.size() > 0) {
					// 获取第一个任务，并将之从任务队列中删除
					Task task = taskQueue.remove(0);
					if (task != null) {
						// 将下载的图片添加到缓存
						task.bitmap = PicUtil.getbitmapAndwrite(task.path); // the new method to test to storage the image to SD card.
//						task.bitmap = PicUtil.getbitmap(task.path, task.roundedType);  this is the old method,

						addCacheBitmap(task.bitmap, task.path);

						if (handler != null) {
							// 创建消息对象，并将完成的任务添加到消息对象中
							Message msg = handler.obtainMessage();
							msg.obj = task;
							// 发送消息回主线程
							handler.sendMessage(msg);
						}
					}

				}

				// 如果队列为空,则令线程等待
				synchronized (this) {
					try {
						this.wait();
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}
		}
	};

	// 回调接口
	public interface ImageCallback {
		void loadImage(String path, Bitmap bitmap, int itemWidth);
	}

	class Task {
		// 下载任务的下载路径
		String path;
		// 下载的图片
		Bitmap bitmap;
		// 回调对象
		ImageCallback callback;

		String roundedType;

		int itemWidth;

		@Override
		public boolean equals(Object o) {
			Task task = (Task) o;
			return task.path.equals(path);
		}
	}

	/**
	 * 以软引用的方式对一个Bitmap对象的实例进行引用并保存该引用
	 */
	private void addCacheBitmap(Bitmap bmp, String key) {
		cleanCache();// 清除垃圾引用
		BtimapRef ref = new BtimapRef(bmp, q, key);
		bitmapRefs.put(key, ref);
	}

	private void cleanCache() {
		BtimapRef ref = null;
		while ((ref = (BtimapRef) q.poll()) != null) {
			bitmapRefs.remove(ref._key);
		}
	}

	// 清除Cache内的全部内容
	public void clearCache() {
		cleanCache();
		bitmapRefs.clear();
		System.gc();
		System.runFinalization();
	}

}
