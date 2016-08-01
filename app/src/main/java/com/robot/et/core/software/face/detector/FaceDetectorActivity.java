package com.robot.et.core.software.face.detector;

import android.Manifest.permission;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.ImageFormat;
import android.graphics.Matrix;
import android.graphics.PixelFormat;
import android.graphics.PorterDuff;
import android.graphics.Rect;
import android.graphics.YuvImage;
import android.hardware.Camera;
import android.hardware.Camera.CameraInfo;
import android.hardware.Camera.Parameters;
import android.hardware.Camera.PreviewCallback;
import android.os.Bundle;
import android.os.Process;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceHolder.Callback;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.WindowManager;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;

import com.iflytek.cloud.FaceDetector;
import com.iflytek.cloud.FaceRequest;
import com.iflytek.cloud.RequestListener;
import com.iflytek.cloud.SpeechConstant;
import com.iflytek.cloud.SpeechError;
import com.iflytek.cloud.util.Accelerometer;
import com.robot.et.R;
import com.robot.et.common.BroadcastAction;
import com.robot.et.common.DataConfig;
import com.robot.et.core.software.face.util.FaceRect;
import com.robot.et.core.software.face.util.FaceUtil;
import com.robot.et.core.software.face.util.ParseResult;
import com.robot.et.entity.FaceInfo;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class FaceDetectorActivity extends Activity {
	private SurfaceView mPreviewSurface;
	private SurfaceView mFaceSurface;
	private Camera mCamera;
	private int mCameraId = CameraInfo.CAMERA_FACING_FRONT;
	// Camera nv21格式预览帧的尺寸，默认设置640*480
	private int PREVIEW_WIDTH = 640;
	private int PREVIEW_HEIGHT = 480;
	// 预览帧数据存储数组和缓存数组
	private byte[] nv21;
	private byte[] buffer;
	//识别到的图片信息
	private byte[] mImageData;
	// 缩放矩阵
	private Matrix mScaleMatrix = new Matrix();
	// 加速度感应器，用于获取手机的朝向
	private Accelerometer mAcc;
	// FaceDetector对象，集成了离线人脸识别：人脸检测、视频流检测功能
	private FaceDetector mFaceDetector;

	private boolean mStopTrack;
	private long mLastClickTime;
	private int isAlign = 0;
	private FaceRequest mFaceRequest;
	public static FaceDetectorActivity instance;
	private List<FaceInfo> faceInfos = new ArrayList<FaceInfo>();
	private String auId;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON, WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		setContentView(R.layout.activity_face_verify);
		initUI();
		nv21 = new byte[PREVIEW_WIDTH * PREVIEW_HEIGHT * 2];
		buffer = new byte[PREVIEW_WIDTH * PREVIEW_HEIGHT * 2];
		mAcc = new Accelerometer(FaceDetectorActivity.this);
		mFaceDetector = FaceDetector.createDetector(FaceDetectorActivity.this, null);
		mFaceRequest = new FaceRequest(this);

		instance = this;
		faceInfos = getIntent().getParcelableArrayListExtra("faceInfo");

	}

	private Callback mPreviewCallback = new Callback() {

		@Override
		public void surfaceDestroyed(SurfaceHolder holder) {
			closeCamera();
		}

		@Override
		public void surfaceCreated(SurfaceHolder holder) {
			openCamera();
		}

		@Override
		public void surfaceChanged(SurfaceHolder holder, int format, int width,int height) {
			mScaleMatrix.setScale(width / (float) PREVIEW_HEIGHT, height / (float) PREVIEW_WIDTH);
		}
	};

	private void setSurfaceSize() {
		DisplayMetrics metrics = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(metrics);
		int width = metrics.widthPixels;
		int height = (int) (width * PREVIEW_WIDTH / (float) PREVIEW_HEIGHT);
		LayoutParams params = new LayoutParams(width, height);
		params.addRule(RelativeLayout.ALIGN_PARENT_TOP);
		mPreviewSurface.setLayoutParams(params);
		mFaceSurface.setLayoutParams(params);
	}

	private void initUI() {
		mPreviewSurface = (SurfaceView) findViewById(R.id.sfv_preview);
		mFaceSurface = (SurfaceView) findViewById(R.id.sfv_face);
		mPreviewSurface.getHolder().addCallback(mPreviewCallback);
		mPreviewSurface.getHolder().setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
		mFaceSurface.setZOrderOnTop(true);
		mFaceSurface.getHolder().setFormat(PixelFormat.TRANSLUCENT);
		// 点击SurfaceView，切换摄相头
		mFaceSurface.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (Camera.getNumberOfCameras() == 1) {
					Log.i("face","只有后置摄像头，不能切换");
					return;
				}
				closeCamera();
				if (CameraInfo.CAMERA_FACING_FRONT == mCameraId) {
					mCameraId = CameraInfo.CAMERA_FACING_BACK;
				} else {
					mCameraId = CameraInfo.CAMERA_FACING_FRONT;
				}
				openCamera();
			}
		});

		// 长按SurfaceView 500ms后松开，摄相头聚集
		mFaceSurface.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				switch (event.getAction()) {
				case MotionEvent.ACTION_DOWN:
					mLastClickTime = System.currentTimeMillis();
					break;
				case MotionEvent.ACTION_UP:
					if (System.currentTimeMillis() - mLastClickTime > 500) {
						mCamera.autoFocus(null);
						return true;
					}
					break;
				default:
					break;
				}
				return false;
			}
		});
		setSurfaceSize();
	}

	private void openCamera() {
		if (null != mCamera) {
			return;
		}
		if (!checkCameraPermission()) {
			Log.e("face","摄像头权限未打开，请打开后再试");
			mStopTrack = true;
			return;
		}
		// 只有一个摄相头，打开后置
		if (Camera.getNumberOfCameras() == 1) {
			mCameraId = CameraInfo.CAMERA_FACING_BACK;
		}
		try {
			mCamera = Camera.open(mCameraId);
			if (CameraInfo.CAMERA_FACING_FRONT == mCameraId) {
				Log.i("face","前置摄像头已开启，点击可切换");
			} else {
				Log.i("face","后置摄像头已开启，点击可切换");
			}
		} catch (Exception e) {
			e.printStackTrace();
			closeCamera();
			return;
		}
		Parameters params = mCamera.getParameters();
		params.setPreviewFormat(ImageFormat.NV21);
		params.setPreviewSize(PREVIEW_WIDTH, PREVIEW_HEIGHT);
		mCamera.setParameters(params);
		// 设置显示的偏转角度，大部分机器是顺时针90度，某些机器需要按情况设置
		mCamera.setDisplayOrientation(90);
		mCamera.setPreviewCallback(new PreviewCallback() {

			@Override
			public void onPreviewFrame(byte[] data, Camera camera) {
				System.arraycopy(data, 0, nv21, 0, data.length);
			}
		});

		try {
			mCamera.setPreviewDisplay(mPreviewSurface.getHolder());
			mCamera.startPreview();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void closeCamera() {
		if (null != mCamera) {
			mCamera.setPreviewCallback(null);
			mCamera.stopPreview();
			mCamera.release();
			mCamera = null;
		}
	}

	private boolean checkCameraPermission() {
		int status = checkPermission(permission.CAMERA, Process.myPid(),
				Process.myUid());
		if (PackageManager.PERMISSION_GRANTED == status) {
			return true;
		}
		return false;
	}

	@Override
	protected void onResume() {
		super.onResume();

		if (null != mAcc) {
			mAcc.start();
		}

		mStopTrack = false;
		new Thread(new Runnable() {

			@Override
			public void run() {
				while (!mStopTrack) {
					if (null == nv21) {
						continue;
					}
					synchronized (nv21) {
						System.arraycopy(nv21, 0, buffer, 0, nv21.length);
					}
					// 获取手机朝向，返回值0,1,2,3分别表示0,90,180和270度
					int direction = Accelerometer.getDirection();
					boolean frontCamera = (CameraInfo.CAMERA_FACING_FRONT == mCameraId);
					// 前置摄像头预览显示的是镜像，需要将手机朝向换算成摄相头视角下的朝向。
					// 转换公式：a' = (360 - a)%360，a为人眼视角下的朝向（单位：角度）
					if (frontCamera) {
						// SDK中使用0,1,2,3,4分别表示0,90,180,270和360度
						direction = (4 - direction) % 4;
					}

					if (mFaceDetector == null) {
						/**
						 * 离线视频流检测功能需要单独下载支持离线人脸的SDK 请开发者前往语音云官网下载对应SDK
						 */
						Log.e("face","本SDK不支持离线视频流检测");
						break;
					}

					String result = mFaceDetector.trackNV21(buffer,PREVIEW_WIDTH, PREVIEW_HEIGHT, isAlign, direction);
					Log.i("face", "result:" + result);

					FaceRect[] faces = ParseResult.parseResult(result);

					Canvas canvas = mFaceSurface.getHolder().lockCanvas();
					if (null == canvas) {
						continue;
					}

					canvas.drawColor(0, PorterDuff.Mode.CLEAR);
					canvas.setMatrix(mScaleMatrix);

					if (faces.length <= 0) {
						mFaceSurface.getHolder().unlockCanvasAndPost(canvas);
						continue;
					}

					if (null != faces && frontCamera == (CameraInfo.CAMERA_FACING_FRONT == mCameraId)) {
						for (FaceRect face : faces) {
							face.bound = FaceUtil.RotateDeg90(face.bound,PREVIEW_WIDTH, PREVIEW_HEIGHT);
							if (face.point != null) {
								for (int i = 0; i < face.point.length; i++) {
									face.point[i] = FaceUtil.RotateDeg90(face.point[i], PREVIEW_WIDTH,PREVIEW_HEIGHT);
								}
							}
							FaceUtil.drawFaceRect(canvas, face, PREVIEW_WIDTH,PREVIEW_HEIGHT, frontCamera, false);
						}
						//检测到一个人脸
						Log.d("face", "faces.length==" + faces.length);
						if (faces.length == 1) {
							mImageData = Bitmap2Bytes(decodeToBitMap(nv21));
							handleFace(mImageData, faceInfos);
						}
					} else {
						Log.d("FaceDetector", "faces:0");
					}
					mFaceSurface.getHolder().unlockCanvasAndPost(canvas);

					mStopTrack = true;

				}
			}
		}).start();
	}


	@Override
	protected void onPause() {
		super.onPause();
		closeCamera();
		if (null != mAcc) {
			mAcc.stop();
		}
		mStopTrack = true;
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		// 销毁对象
		mFaceDetector.destroy();
		instance = null;
	}

	//脸部识别后的处理
	private void handleFace(byte[] mImageData, List<FaceInfo> faceInfos) {
		if (null != mImageData && mImageData.length > 0) {
			Log.i("face", "handleFace faceInfos.size()===" + faceInfos.size());
			if (faceInfos != null && faceInfos.size() > 0) {
				FaceInfo info = faceInfos.get(0);
				String auId = info.getAuthorId();
				FaceDataFactory.setAuthorName(info.getAuthorName());
				FaceDataFactory.setNewFaceInfo(faceInfos);
				verify(mImageData, auId);
			} else {
				registerFace(mImageData);
			}
		} else {
			Log.i("face", "handleFace mImageData== null");
			sendMsg("眼睛看花了，再让我看一次吧");
		}
	}

	//验证
	private void verify(byte[] mImageData, String auId) {
		if (null != mImageData && mImageData.length > 0) {
			// 设置用户标识，格式为6-18个字符（由字母、数字、下划线组成，不得以数字开头，不能包含空格）。
			// 当不设置时，云端将使用用户设备的设备ID来标识终端用户。
			mFaceRequest.setParameter(SpeechConstant.AUTH_ID, auId);
			mFaceRequest.setParameter(SpeechConstant.WFR_SST, "verify");
			mFaceRequest.sendRequest(mImageData, mRequestListener);
		} else {
			Log.i("face", "verify mImageData== null");
			sendMsg("眼睛看花了，再让我看一次吧");
		}
	}

	//注册
	private void registerFace(byte[] mImageData) {
		if (null != mImageData && mImageData.length > 0) {
			auId = getOnlyTime();
			mFaceRequest.setParameter(SpeechConstant.AUTH_ID, auId);
			mFaceRequest.setParameter(SpeechConstant.WFR_SST, "reg");
			mFaceRequest.sendRequest(mImageData, mRequestListener);
		} else {
			Log.i("face", "registerFace mImageData== null");
			sendMsg("眼睛看花了，再让我看一次吧");
		}
	}

	private RequestListener mRequestListener = new RequestListener() {

		@Override
		public void onEvent(int eventType, Bundle params) {
		}

		@Override
		public void onBufferReceived(byte[] buffer) {
			boolean isError = false;
			try {
				String result = new String(buffer, "utf-8");
				Log.i("face", "result===" + result);

				JSONObject object = new JSONObject(result);
				String type = object.optString("sst");
				if ("reg".equals(type)) {
					register(object);
				} else if ("verify".equals(type)) {
					verify(object);
				}
			} catch (UnsupportedEncodingException e) {
				Log.i("face", "RequestListener  UnsupportedEncodingException");
				isError = true;
			} catch (JSONException e) {
				Log.i("face", "RequestListener  JSONException");
				isError = true;
			} finally {
				if (isError) {
					sendMsg("眼睛累了，我去歇去喽");
				}
			}
		}

		@Override
		public void onCompleted(SpeechError error) {
			if (error != null) {
				sendMsg("眼睛累了，我去歇去喽");
			}
		}
	};

	private void register(JSONObject obj) throws JSONException {
		int ret = obj.getInt("ret");
		if (ret != 0) {
			Log.i("face", "注册失败");
			sendMsg("让我再认识你一次吧");
			return;
		}
		if ("success".equals(obj.get("rst"))) {
			Log.i("face", "注册成功");
			FaceDataFactory.setAuthorId(auId);
			DataConfig.isFaceDetector = true;
			sendMsg("很高兴认识你，请问你怎么称呼呢？");
		} else {
			Log.i("face", "注册失败");
			sendMsg("让我再认识你一次吧");
		}
	}

	private void verify(JSONObject obj) throws JSONException {
		int ret = obj.getInt("ret");
		if (ret != 0) {
			Log.i("face", "验证失败");
			handleFace(mImageData, FaceDataFactory.getFaceInfos());
			return;
		}
		if ("success".equals(obj.get("rst"))) {
			if (obj.getBoolean("verf")) {
				Log.i("face", "通过验证");
				sendMsg("你好，" + FaceDataFactory.getAuthorName() + ",我们又见面了。");
			} else {
				Log.i("face", "验证不通过");
				handleFace(mImageData, FaceDataFactory.getFaceInfos());
			}
		} else {
			Log.i("face", "验证失败");
			handleFace(mImageData, FaceDataFactory.getFaceInfos());
		}
	}

	private void sendMsg(String content) {
		Intent intent = new Intent();
		intent.setAction(BroadcastAction.ACTION_FACE_DISTINGUISH);
		intent.putExtra("content", content);
		sendBroadcast(intent);
		finish();
	}

	private Bitmap decodeToBitMap(byte[] data) {
		try {
			YuvImage image = new YuvImage(data, ImageFormat.NV21,PREVIEW_WIDTH, PREVIEW_HEIGHT, null);
			if (image != null) {
				ByteArrayOutputStream stream = new ByteArrayOutputStream();
				image.compressToJpeg(new Rect(0, 0, PREVIEW_WIDTH,PREVIEW_HEIGHT), 80, stream);
				Bitmap bmp = BitmapFactory.decodeByteArray(stream.toByteArray(), 0, stream.size());
				stream.close();
				return bmp;
			}
		} catch (Exception ex) {
			Log.e("face", "Error:" + ex.getMessage());
		}
		return null;
	}

	private byte[] Bitmap2Bytes(Bitmap bm) {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		bm.compress(Bitmap.CompressFormat.PNG, 100, baos);
		return baos.toByteArray();
	}

	//获取时间的唯一数
	private String getOnlyTime() {
		Date date = new Date(System.currentTimeMillis());
		SimpleDateFormat format = new SimpleDateFormat("yyyyMMddHHmmss");
		String dateTime = format.format(date);
		return dateTime;
	}

}
