package com.example.cameraandroid;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.Image;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Objects;

class ImageSaver implements Runnable {
    /**
     * The JPEG image
     */
    private final Image mImage;
    /**
     * The file we save the image into.
     */
    private final File mFile;

    private final String mCameraId;

    ImageSaver(Image image, File file, String cameraId) {
        mImage = image;
        mFile = file;
        mCameraId = cameraId;
    }

    @Override
    public void run() {
        ByteBuffer buffer = mImage.getPlanes()[0].getBuffer();
        byte[] bytes = new byte[buffer.remaining()];
        buffer.get(bytes);
        FileOutputStream output = null;
        try {
            output = new FileOutputStream(mFile + "/" + getNowDate() + ".jpg");
            Bitmap bmp = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
            System.out.println(mCameraId);
            if(mCameraId.equals("1"))
                output.write(rotateToDegrees(bmp,-90));
            if(mCameraId.equals("0"))
                output.write(rotateToDegrees(bmp,90));
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            mImage.close();
            if (null != output) {
                try {
                    output.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private byte[] rotateToDegrees(Bitmap b, int rotate) {
        if (b != null) {
            Matrix m = new Matrix();
            m.setRotate(rotate, (float) b.getWidth() / 2, (float) b.getHeight() / 2);
            Bitmap b2 = Bitmap.createBitmap(
                    b, 0, 0, b.getWidth(), b.getHeight(), m, true);
            if (b != b2) {
                b.recycle();
                b = b2;
            }
        }
        return BitmapToBytes(Objects.requireNonNull(b));
    }

    private byte[] BitmapToBytes(Bitmap bm) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bm.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
        return byteArrayOutputStream.toByteArray();
    }

    private String getNowDate(){
        Format formatter = new SimpleDateFormat("yyyy-MM-dd_hh-mm-ss");
        return String.valueOf(formatter.format(new Date()));
    }
}