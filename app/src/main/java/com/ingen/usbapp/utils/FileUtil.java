package com.ingen.usbapp.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Base64;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URL;

import okhttp3.ResponseBody;

public class FileUtil {

    public static File getImagesDir(Context context) {
        File parentDir = new File(context.getFilesDir().getParent());
        File imagesDir = new File(parentDir, "images");
        if (imagesDir.exists() == false) {
            imagesDir.mkdir();
        }
        return imagesDir;
    }

    public static File getSettingImagesDir(Context context) {
        File settingImageDir = new File(getImagesDir(context), "setting");
        if (settingImageDir.exists() == false) {
            settingImageDir.mkdir();
        }
        return settingImageDir;
    }

    public static File getSurveyCaptureImageDir(Context context) {
        File settingImageDir = new File(getImagesDir(context), "SurveyCaptureImage");
        if (settingImageDir.exists() == false) {
            settingImageDir.mkdir();
        }
        return settingImageDir;
    }

    public static File getFaceScanImageDir(Context context) {
        File imageDir = new File(getImagesDir(context), "FaceScanImages");
        if (imageDir.exists() == false) {
            imageDir.mkdir();
        }
        return imageDir;
    }

    public static File getImagesFile(Context context, String url) {
        String fileName = getFileNameFromURL(url);
        File targetFile = new File(getImagesDir(context), fileName);
        return targetFile;
    }

    public static File getSettingImagesFile(Context context, String url) {
        String fileName = getFileNameFromURL(url);
        File targetFile = new File(getSettingImagesDir(context), fileName);
        return targetFile;
    }

    public static boolean writeResponseBodyToDisk(Context context, ResponseBody body, File targetFile) {
        try {
            File tempFile = new File(getImagesDir(context), "tempImage.dat");

            if (tempFile.exists()) {
                tempFile.delete();
            }
            InputStream inputStream = null;
            OutputStream outputStream = null;

            try {
                byte[] fileReader = new byte[4096];
                long fileSize = body.contentLength();
                long fileSizeDownloaded = 0;

                inputStream = body.byteStream();
                outputStream = new FileOutputStream(tempFile);

                while (true) {
                    int read = inputStream.read(fileReader);
                    if (read == -1) {
                        break;
                    }
                    outputStream.write(fileReader, 0, read);
                    fileSizeDownloaded += read;
                    //Logger.d("Download File", "file download: " + fileSizeDownloaded + " of " + fileSize);
                }
                outputStream.flush();

                // rename temp file to target file
                tempFile.renameTo(targetFile);
                return true;
            } catch (IOException e) {
                return false;
            } finally {
                if (inputStream != null) {
                    inputStream.close();
                }
                if (outputStream != null) {
                    outputStream.close();
                }
            }
        } catch (IOException e) {
            return false;
        }
    }

    public static String getFileNameFromURL(String url) {
        if (url == null) {
            return "";
        }
        try {
            URL resource = new URL(url);
            String host = resource.getHost();
            if (host.length() > 0 && url.endsWith(host)) {
                // handle ...example.com
                return "";
            }
        } catch (MalformedURLException e) {
            return "";
        }

        int startIndex = url.lastIndexOf('/') + 1;
        int length = url.length();

        // find end index for ?
        int lastQMPos = url.lastIndexOf('?');
        if (lastQMPos == -1) {
            lastQMPos = length;
        }

        // find end index for #
        int lastHashPos = url.lastIndexOf('#');
        if (lastHashPos == -1) {
            lastHashPos = length;
        }

        // calculate the end index
        int endIndex = Math.min(lastQMPos, lastHashPos);
        return url.substring(startIndex, endIndex);
    }

    public static String getBase64FromPath(File file) {
        String base64 = "";
        try {
            if (file != null) {
                byte[] buffer = new byte[(int) file.length() + 100];
                @SuppressWarnings("resource")
                int length = new FileInputStream(file).read(buffer);
                base64 = Base64.encodeToString(buffer, 0, length,
                        Base64.NO_WRAP);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return base64;
    }

    public static boolean saveBitmap(Bitmap bitmap, File outFile) {
        FileOutputStream outputStream = null;
        try {
            if (outFile.exists()) {
                outFile.delete();
            }

            outputStream = new FileOutputStream(outFile);
            Bitmap compressBmp = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight());
            compressBmp.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);
            outputStream.flush();
            outputStream.close();

            return true;
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return false;
    }
}
