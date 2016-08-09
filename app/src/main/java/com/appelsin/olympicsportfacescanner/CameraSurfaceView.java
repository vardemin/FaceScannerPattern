package com.appelsin.olympicsportfacescanner;

import android.annotation.SuppressLint;
import android.content.Context;
import android.hardware.Camera;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import com.appelsin.olympicsportfacescanner.cam.FaceDetectionCamera;

import java.util.List;

@SuppressLint("ViewConstructor") // View can only be inflated programatically
public class CameraSurfaceView extends SurfaceView implements SurfaceHolder.Callback {

    private final FaceDetectionCamera camera;
    private final FaceDetectionCamera.Listener listener;


    public CameraSurfaceView(Context context, FaceDetectionCamera camera, FaceDetectionCamera.Listener listener) {
        super(context);
        this.camera = camera;
        this.camera.setView(this);
        this.listener = listener;
        // Listen for when the surface is ready to be drawn on
        this.getHolder().addCallback(this);
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        // When the surface is ready to be drawn on
        // tell our camera to use this to show a preview
        camera.initialise(listener, holder);
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        if (surfaceDoesNotExist()) {
            return;
        }
        // When the surface changes we need to re-attach it to our camera
        camera.initialise(listener, holder);
    }

    private boolean surfaceDoesNotExist() {
        return getHolder().getSurface() == null;
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        camera.stopRel();
        // empty. Take care of releasing the Camera preview in your activity.
        // (done in FrontCameraRetriever for us)
    }

    public void ChangeOrientation() {
        camera.ChangeOrientation();
    }
    private Camera.Size getOptimalPreviewSize(List<Camera.Size> sizes, int w, int h) {
        final double ASPECT_TOLERANCE = 0.1;
        double targetRatio=(double)h / w;

        if (sizes == null) return null;

        Camera.Size optimalSize = null;
        double minDiff = Double.MAX_VALUE;

        int targetHeight = h;

        for (Camera.Size size : sizes) {
            double ratio = (double) size.width / size.height;
            if (Math.abs(ratio - targetRatio) > ASPECT_TOLERANCE) continue;
            if (Math.abs(size.height - targetHeight) < minDiff) {
                optimalSize = size;
                minDiff = Math.abs(size.height - targetHeight);
            }
        }

        if (optimalSize == null) {
            minDiff = Double.MAX_VALUE;
            for (Camera.Size size : sizes) {
                if (Math.abs(size.height - targetHeight) < minDiff) {
                    optimalSize = size;
                    minDiff = Math.abs(size.height - targetHeight);
                }
            }
        }
        return optimalSize;
    }
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        final int width = resolveSize(getSuggestedMinimumWidth(), widthMeasureSpec);
        final int height = resolveSize(getSuggestedMinimumHeight(), heightMeasureSpec);
        setMeasuredDimension(width, height);

        if (camera.getSupportedSizes() != null) {
            camera.setmPreviewSize(getOptimalPreviewSize(camera.getSupportedSizes(), width, height));
        }
    }
}
