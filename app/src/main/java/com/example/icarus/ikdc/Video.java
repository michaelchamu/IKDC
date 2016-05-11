package com.example.icarus.ikdc;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.hardware.Camera;
import android.hardware.Camera.CameraInfo;
import android.hardware.Camera.Size;
import android.media.CamcorderProfile;
import android.media.MediaRecorder;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.icarus.ikdc.database.DataAccessObject;
import com.example.icarus.ikdc.utils.LogUtil;
import com.example.icarus.ikdc.utils.StringUtil;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
/*Adapted by Michael 'icarus' Chamunorwa 15/04/2015 from a GitHub Project by yinglovezhuzhu@gmail.com*/


public class Video extends Activity  implements SurfaceHolder.Callback  {
    private static final String TAG = Video.class.getSimpleName();

    private DataAccessObject myData;
    private SurfaceView mSurfaceView;
    private ImageButton mIbtnCancel;
    private ImageButton mIbtnOk;
    private ImageButton mButton;
    private static TextView mTvTimeCount;

    private SurfaceHolder mSurfaceHolder;
    private MediaRecorder mMediaRecorder;
    private Camera mCamera;

    private File mOutputFile;

    private boolean mIsRecording = false;

    private Resources mResources;
    private String mPackageName;

    private List<Size> mSupportVideoSizes;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        myData = new DataAccessObject(this);

        mResources = getResources();
        mPackageName = getPackageName();

        try{
            int layoutId = mResources.getIdentifier("activity_video", "layout", mPackageName);
            setContentView(layoutId);}catch(Exception e){
            Toast.makeText(getApplicationContext(),e.toString(),Toast.LENGTH_LONG).show();
        }

        initView();

        View.OnClickListener stopV = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               stopRecord();
            }
        };
        Button stpVid = (Button) findViewById(R.id.stopVid);
        stpVid.setEnabled(false);
        stpVid.setOnClickListener(stopV);

    }

    @SuppressWarnings("deprecation")
    private void initView() {

        mSurfaceView = (SurfaceView) findViewById(mResources.getIdentifier("video_recorder_preview", "id", mPackageName));
        mButton = (ImageButton) findViewById(mResources.getIdentifier("record", "id", mPackageName));
        mIbtnCancel = (ImageButton) findViewById(mResources.getIdentifier("cancel", "id", mPackageName));
        mIbtnOk = (ImageButton) findViewById(mResources.getIdentifier("save", "id", mPackageName));
        mIbtnCancel.setOnClickListener(mCancelListener);
        mIbtnOk.setOnClickListener(mOkListener);
        mIbtnCancel.setVisibility(View.INVISIBLE);
        mIbtnOk.setVisibility(View.INVISIBLE);
        mTvTimeCount = (TextView) findViewById(mResources.getIdentifier("timer", "id", mPackageName));
        mTvTimeCount.setVisibility(View.INVISIBLE);

        //mButton.setBackgroundResource(mResources.getIdentifier("video", "mipmap", mPackageName));
        mButton.setOnClickListener(mBtnListener);

        mSurfaceHolder = mSurfaceView.getHolder();
        mSurfaceHolder.addCallback(this);

        if(Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB) {
            try {
                mSurfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
            } catch (Exception e) {
                //  Log.e(TAG, e);
            }
        }

    }


    private void exit(final int resultCode, final Intent data) {
        if(mIsRecording) {
            new AlertDialog.Builder(Video.this)
                    .setTitle("--")
                    .setMessage("--，--？")
                    .setPositiveButton("--", new DialogInterface.OnClickListener() {

                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            stopRecord();
                            if(resultCode == RESULT_CANCELED) {
                                deleteFile(mOutputFile);
                            }
                            setResult(resultCode, data);
                            finish();
                        }
                    })
                    .setNegativeButton("--", new DialogInterface.OnClickListener() {

                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            // TODO Auto-generated method stub

                        }
                    }).show();
            return;
        }
        if(resultCode == RESULT_CANCELED) {
            deleteFile(mOutputFile);
        }
        setResult(resultCode, data);
        finish();
    }

    private void deleteFile(File delFile) {
        if(delFile == null) {
            return;
        }
        final File file = new File(delFile.getAbsolutePath());
        delFile = null;
        new Thread() {
            @Override
            public void run() {
                super.run();
                if(file.exists()) {
                    file.delete();
                }
            }
        }.start();
    }

    @SuppressLint("HandlerLeak")
    private  Handler mHandler = new Handler() {

        @Override
        public void handleMessage(android.os.Message msg) {
            switch (msg.what) {
                case Config.YUNINFO_ID_TIME_COUNT:
                    if(mIsRecording) {
                        if(msg.arg1 > msg.arg2) {
//					mTvTimeCount.setVisibility(View.INVISIBLE);
                            mTvTimeCount.setText("00:00");
                            stopRecord();
                        } else {
                            mTvTimeCount.setText("00:0" + (msg.arg2 - msg.arg1));
                            Message msg2 = mHandler.obtainMessage(Config.YUNINFO_ID_TIME_COUNT, msg.arg1 + 1, msg.arg2);
                            mHandler.sendMessageDelayed(msg2, 1000);
                        }
                    }
                    break;

                default:
                    break;
            }
        };

    };

    private View.OnClickListener mBtnListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            // TODO Auto-generated method stub
            if(mIsRecording) {
                stopRecord();
            } else {
                startRecord();
            }
        }

    };


    private View.OnClickListener mCancelListener = new View.OnClickListener() {

        @Override
        public void onClick(View v) {
            exit(RESULT_CANCELED, null);
        }
    };

    private View.OnClickListener mOkListener = new View.OnClickListener() {

        @Override
        public void onClick(View v) {
            Intent data = new Intent();
            if(mOutputFile != null && !StringUtil.isEmpty(mOutputFile.getAbsolutePath())) {
                data.putExtra(Config.YUNINFO_RESULT_DATA, mOutputFile);
            }
            exit(RESULT_OK, data);
        }
    };

    @SuppressLint("NewApi")
    private void openCamera() {
        //Open camera
        try {
            this.mCamera = Camera.open();
            Camera.Parameters parameters = mCamera.getParameters();
            //parameters.setRotation(90);
            System.out.println(parameters.flatten());
            parameters.set("orientation", "portrait");
//			parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_AUTO);
            mCamera.setParameters(parameters);

          setCameraDisplayOrientation(this, findFrontFacingCameraID(),mCamera);
           mCamera.lock();
            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.FROYO) {
                try {
                 //   mCamera.setDisplayOrientation(90);
                } catch (NoSuchMethodError e) {
                    e.printStackTrace();
                }
            }
            mSupportVideoSizes = parameters.getSupportedVideoSizes();
            if(mSupportVideoSizes == null || mSupportVideoSizes.isEmpty()) {  //For some device can't get supported video size
                String videoSize = parameters.get("video-size");
                LogUtil.i(TAG, videoSize);
                mSupportVideoSizes = new ArrayList<Camera.Size>();
                if(!StringUtil.isEmpty(videoSize)) {
                    String [] size = videoSize.split("x");
                    if(size.length > 1) {
                        try {
                            int width = Integer.parseInt(size[0]);
                            int height = Integer.parseInt(size[1]);
                            mSupportVideoSizes.add(mCamera.new Size(width, height));
                        } catch (Exception e) {
                            LogUtil.e(TAG, e.toString());
                        }
                    }
                }
            }
            for (Size size : mSupportVideoSizes) {
                LogUtil.i(TAG, size.width + "<>" + size.height);
            }
        } catch (Exception e) {
            LogUtil.e(TAG, "Open Camera error\n" + e.toString());
        }
    }

    @SuppressLint("NewApi")
    private boolean initVideoRecorder() {

        mCamera.unlock();
        mMediaRecorder = new MediaRecorder();

        // Step 1: Unlock and set camera to MediaRecorder
        LogUtil.i("Camera", mCamera);
        LogUtil.i("Camera", mMediaRecorder);
        mMediaRecorder.setCamera(mCamera);

        // Step 2: Set sources
        try {
            mMediaRecorder.setAudioSource(MediaRecorder.AudioSource.DEFAULT);
            mMediaRecorder.setVideoSource(MediaRecorder.VideoSource.DEFAULT);
        } catch (Exception e) {
            mMediaRecorder.setAudioSource(MediaRecorder.AudioSource.DEFAULT);
            mMediaRecorder.setVideoSource(MediaRecorder.VideoSource.DEFAULT);
            e.printStackTrace();
        }

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.FROYO) {
            try {
                CamcorderProfile lowProfile = CamcorderProfile.get(CamcorderProfile.QUALITY_LOW);
                CamcorderProfile hightProfile = CamcorderProfile.get(CamcorderProfile.QUALITY_HIGH);
                if(lowProfile != null && hightProfile != null) {
                    int audioBitRate = lowProfile.audioBitRate > 128000 ? 128000 : lowProfile.audioBitRate;
                    lowProfile.audioBitRate = audioBitRate > hightProfile.audioBitRate ? hightProfile.audioBitRate : audioBitRate;
                    lowProfile.audioSampleRate = 48000 > hightProfile.audioSampleRate ? hightProfile.audioSampleRate : 48000;
//					lowProfile.duration = 20 > hightProfile.duration ? hightProfile.duration : 20;
//					lowProfile.videoFrameRate = 20 > hightProfile.videoFrameRate ? hightProfile.videoFrameRate : 20;
                    lowProfile.duration = hightProfile.duration;
                    lowProfile.videoFrameRate = hightProfile.videoFrameRate;
                    lowProfile.videoBitRate = 1500000 > hightProfile.videoBitRate ? hightProfile.videoBitRate : 1500000;;
                    if(mSupportVideoSizes != null && !mSupportVideoSizes.isEmpty()) {
                        int width = 640;
                        int height = 480;
                        Collections.sort(mSupportVideoSizes, new SizeComparator());
                        int lwd = mSupportVideoSizes.get(0).width;
                        for (Size size : mSupportVideoSizes) {
                            int wd = Math.abs(size.width - 640);
                            if(wd < lwd) {
                                width = size.width;
                                height = size.height;
                                lwd = wd;
                            } else {
                                break;
                            }
                        }
                        lowProfile.videoFrameWidth = width;
                        lowProfile.videoFrameHeight = height;
                    }
                    System.out.println(lowProfile.audioBitRate);
                    System.out.println(lowProfile.audioChannels);
                    System.out.println(lowProfile.audioCodec);
                    System.out.println(lowProfile.audioSampleRate);
                    System.out.println(lowProfile.duration);
                    System.out.println(lowProfile.fileFormat);
                    System.out.println(lowProfile.quality);
                    System.out.println(lowProfile.videoBitRate);
                    System.out.println(lowProfile.videoCodec);
                    System.out.println(lowProfile.videoFrameHeight);
                    System.out.println(lowProfile.videoFrameWidth);
                    System.out.println(lowProfile.videoFrameRate);

                    mMediaRecorder.setProfile(lowProfile);
                }
            } catch (Exception e) {
                try {
                    mMediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.DEFAULT);
                } catch (Exception ex) {
                    e.printStackTrace();
                }
                try {
                    mMediaRecorder.setVideoEncoder(MediaRecorder.VideoEncoder.DEFAULT);
                } catch (Exception ex) {
                    e.printStackTrace();
                }
                if(mSupportVideoSizes != null && !mSupportVideoSizes.isEmpty()) {
                    Collections.sort(mSupportVideoSizes, new SizeComparator());
                    Size size = mSupportVideoSizes.get(0);
                    try {
                        mMediaRecorder.setVideoSize(size.width, size.height);
                    } catch (Exception ex) {
                        e.printStackTrace();
                    }
                } else {
                    try {
                        mMediaRecorder.setVideoSize(640, 480); // Its is not on android docs but
                        // it needs to be done. (640x480
                        // = VGA resolution)
                    } catch (Exception ex) {
                        e.printStackTrace();
                    }
                }
                e.printStackTrace();
            }
        } else {
            try {
                mMediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.DEFAULT);
            } catch (Exception e) {
                e.printStackTrace();
            }
            try {
                mMediaRecorder.setVideoEncoder(MediaRecorder.VideoEncoder.DEFAULT);
            } catch (Exception e) {
                e.printStackTrace();
            }
            if(mSupportVideoSizes != null && !mSupportVideoSizes.isEmpty()) {
                Collections.sort(mSupportVideoSizes, new SizeComparator());
                Size size = mSupportVideoSizes.get(0);
                try {
                    mMediaRecorder.setVideoSize(size.width, size.height);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                try {
                    mMediaRecorder.setVideoSize(640, 480); // Its is not on android docs but
                    // it needs to be done. (640x480
                    // = VGA resolution)
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

        }

        // Step 4: Set output file
        myData.open();
        int vidNum = myData.retrieveVideoNum() + 1;

        mOutputFile = new File(Environment.getExternalStorageDirectory() + "/IKDC/commonStorage/videos", "Video_"
                + vidNum + ".3gp");


        mMediaRecorder.setOutputFile(mOutputFile.getAbsolutePath());

        // Step 5: Set the preview output
        mMediaRecorder.setPreviewDisplay(mSurfaceHolder.getSurface());

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD) {
            try {
                mMediaRecorder.setOrientationHint(90);
            } catch (NoSuchMethodError e) {
                e.printStackTrace();
            }
        }


        // Step 6: Prepare configured MediaRecorder
        try {
            mMediaRecorder.prepare();
        } catch (IllegalStateException e) {
            Log.d("VideoPreview", "IllegalStateException preparing MediaRecorder: "	+ e.getMessage());
            releaseMediaRecorder();
            return false;
        } catch (IOException e) {
            Log.d("VideoPreview", "IOException preparing MediaRecorder: " + e.getMessage());
            releaseMediaRecorder();
            return false;
        }
//		 mMediaRecorder.setCamera(mCamera);
//
//	        mMediaRecorder.setAudioSource(MediaRecorder.AudioSource.CAMCORDER);
//	        mMediaRecorder.setVideoSource(MediaRecorder.VideoSource.CAMERA);
//	        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB) {
//
//	            mMediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.DEFAULT);
//	            mMediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.DEFAULT);
//	            mMediaRecorder.setVideoEncoder(MediaRecorder.VideoEncoder.DEFAULT);
//
//	        } else {
//	        	CamcorderProfile camcorderProfile = CamcorderProfile.get(CamcorderProfile.QUALITY_HIGH);
//	        	mMediaRecorder.setProfile(camcorderProfile);
//
//	            mMediaRecorder.setVideoSize(720, 480);
//	        }
//
//	        mOutputFile = new File(Environment.getExternalStorageDirectory(), "Video_"
//					+ DateUtil.getSystemDate("yyyy_MM_dd_HHmmss") + ".mp4");
//			mMediaRecorder.setOutputFile(mOutputFile.getAbsolutePath());
//	        mMediaRecorder.setMaxDuration(60000);
//	        mMediaRecorder.setPreviewDisplay(mSurfaceHolder.getSurface());
//
//	        try {
//	            mMediaRecorder.prepare();
//	        } catch (IllegalStateException e) {
//	            releaseMediaRecorder();
//	            return false;
//	        } catch (IOException e) {
//	            releaseMediaRecorder();
//	            return false;
//	        }

        myData.createCVideo(mOutputFile.getName(), mOutputFile.getAbsolutePath());

        myData.close();
        return true;
    }

    private void releaseMediaRecorder() {
        if (mMediaRecorder != null) {
            mMediaRecorder.reset(); // clear recorder configuration
            mMediaRecorder.release(); // release the recorder object
            mMediaRecorder = null;
            mCamera.lock(); // lock camera for later use
        }
    }

    private void releaseCamera() {
        if (mCamera != null) {
            mCamera.release(); // release the camera for other applications
            mCamera = null;
        }
    }

    private void startRecord() {
        try {
            // initialize video camera
            if (initVideoRecorder()) {
                // Camera is available and unlocked, MediaRecorder is prepared,
                // now you can start recording
                mMediaRecorder.start();
              
               // mButton.setBackgroundResource(mResources.getIdentifier("stop_btn", "drawable", mPackageName));
				mButton.setEnabled(false);
                Button stpVid = (Button) findViewById(R.id.stopVid);
                stpVid.setEnabled(true);


            } else {
                // prepare didn't work, release the camera
                releaseMediaRecorder();
                // inform user
              //  mButton.setBackgroundResource(mResources.getIdentifier("rec_btn", "drawable", mPackageName));
            }
            mTvTimeCount.setVisibility(View.VISIBLE);
            mTvTimeCount.setText("00:0" + (Config.YUNINFO_MAX_VIDEO_DURATION / 1000));
            Message msg = mHandler.obtainMessage(Config.YUNINFO_ID_TIME_COUNT, 1, Config.YUNINFO_MAX_VIDEO_DURATION / 1000);
            mHandler.sendMessage(msg);
            mIsRecording = true;
        } catch (Exception e) {
            // showShortToast("--");
            e.printStackTrace();
            //exit(RESULT_ERROR, null);
        }
    }


    private void stopRecord() {
        // stop recording and release camera
        try {
            mMediaRecorder.stop(); // stop the recording
        } catch (Exception e) {
            if(mOutputFile != null && mOutputFile.exists()) {
                mOutputFile.delete();
                mOutputFile = null;
            }
            LogUtil.e(TAG, e.toString());
        }
        releaseMediaRecorder(); // release the MediaRecorder object
        mCamera.lock(); // take camera access back from MediaRecorder
//		releaseCamera(); // release camera
      //  mButton.setBackgroundResource(mResources.getIdentifier("rec_btn", "drawable", mPackageName));
        mIsRecording = false;
Button stpVid = (Button) findViewById(R.id.stopVid);
        FrameLayout container = (FrameLayout) findViewById(R.id.containerMain);
        mButton.setVisibility(View.GONE);
        stpVid.setVisibility(View.GONE);
        container.setVisibility(View.GONE);
        mIbtnCancel.setVisibility(View.VISIBLE);
        mIbtnOk.setVisibility(View.VISIBLE);
    }

    /**
     *
     * @param activity
     * @param cameraId
     * @param camera
     */
    @SuppressLint("NewApi")
    public static void setCameraDisplayOrientation(Activity activity, int cameraId, Camera camera) {
        Camera.CameraInfo info = new Camera.CameraInfo(); //Since API level 9
        Camera.getCameraInfo(cameraId, info);
        int rotation = activity.getWindowManager().getDefaultDisplay().getRotation();
        int degrees = 0;
        switch (rotation) {
            case Surface.ROTATION_0: degrees = 0; break;
            case Surface.ROTATION_90: degrees = 90; break;
            case Surface.ROTATION_180: degrees = 180; break;
            case Surface.ROTATION_270: degrees = 270; break;
        }

        int result;
        if (info.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
            result = (info.orientation + degrees) % 360;
            result = (360 - result) % 360;  // compensate the mirror
        } else {  // back-facing
            result = (info.orientation - degrees + 360) % 360;
        }
        camera.setDisplayOrientation(result);
    }

    private int findFrontFacingCameraID() {
        int cameraId = -1;
        // Search for the front facing camera
        int numberOfCameras = Camera.getNumberOfCameras();
        for (int i = 0; i < numberOfCameras; i++) {
            CameraInfo info = new CameraInfo();
            Camera.getCameraInfo(i, info);
            if (info.facing == CameraInfo.CAMERA_FACING_BACK) {
                Log.d(TAG, "Camera found");
                cameraId = i;
                break;
            }
        }
        return cameraId;
    }

    @Override
    protected void onResume() {
        super.onResume();
        openCamera();
    }

    @Override
    protected void onPause() {
        super.onPause();
        releaseCamera();
    }


    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        if(mCamera != null) {
            try {
                mCamera.setPreviewDisplay(holder);
                mCamera.startPreview();
            } catch (Exception e) {
                LogUtil.e(TAG, "Error setting camera preview: " + e.toString());
            }
        }
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width,
                               int height) {
        // TODO Auto-generated method stub

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        // TODO Auto-generated method stub
        if (mCamera != null) {
            try {
                mCamera.stopPreview();
            } catch (Exception e) {
                LogUtil.e(TAG, "Error setting camera preview: " + e.toString());
            }
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode == KeyEvent.KEYCODE_BACK) {
            exit(RESULT_CANCELED, null);
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }


    private class SizeComparator implements Comparator<Size> {

        @Override
        public int compare(Size lhs, Size rhs) {
            return rhs.width - lhs.width;
        }
    }

}
