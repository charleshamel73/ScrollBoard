package com.example.keyboardv6;

import android.content.Context;
import android.hardware.Camera;
import android.hardware.Camera.CameraInfo;
import android.util.Log;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.WindowManager;

public class CameraSurfaceView extends SurfaceView implements SurfaceHolder.Callback{
	private SurfaceHolder mHolder;
	public Camera camera = null;
	
	public CameraSurfaceView(Context context) {
		super(context);
		mHolder = getHolder();
		mHolder.addCallback(this);
	}
	
	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width,
			int height) {
		Camera.Parameters params = camera.getParameters();
		WindowManager windowManager = (WindowManager) getContext().getSystemService(Context.WINDOW_SERVICE);
        int rotation = windowManager.getDefaultDisplay().getRotation();
        if(rotation == Surface.ROTATION_0){
        	//Log.d("Camera", "msg: rotation is 0");
        	camera.setDisplayOrientation(90);
        }
        else if(rotation == Surface.ROTATION_90){
        	//Log.d("Camera", "msg: rotation is 90");
        	camera.setDisplayOrientation(0);
        }
        else if(rotation == Surface.ROTATION_180){
        	//Log.d("Camera", "msg: rotation is 180");
        	camera.setDisplayOrientation(270);
        }
        else if(rotation == Surface.ROTATION_270){
        	//Log.d("Camera", "msg: rotation is 270");
        	camera.setDisplayOrientation(180);
        }
        params.setPreviewSize(1280, 720);
		camera.setParameters(params);
		camera.startPreview();
	}
	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		int camID = getAvailableCameraID();
		//Log.d("Camera", "msg: Camera: "+ camID);
		camera = Camera.open(camID);
		try{
			camera.setPreviewDisplay(mHolder);
		}catch(Exception e){
			Log.d("Camera", "msg: Issue with Camera");
			camera.release();
		}
		
	}
	
	/**
	 * This grabs the camera available prioritizing the front-end, back-end and if it does not find anything a -1
	 * @return the index within the Cameras of the device or -1 if not found
	 */
	private int getAvailableCameraID(){
		int mNumberOfCameras =Camera.getNumberOfCameras();
		Camera.CameraInfo cameraInfo = new CameraInfo();
		for  (int  i = 0; i < mNumberOfCameras; i++) { 
            Camera.getCameraInfo(i, cameraInfo); 
            if  (cameraInfo.facing == CameraInfo.CAMERA_FACING_BACK) { 
                return i;
            } 
        } 
		for  (int  j = 0; j < mNumberOfCameras; j++) { 
            Camera.getCameraInfo(j, cameraInfo); 
            if  (cameraInfo.facing == CameraInfo.CAMERA_FACING_FRONT) { 
                return j;
            } 
        }
		return -1;
	}

	

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		camera.stopPreview();
		camera.release();
		camera = null;
		
	}

}
