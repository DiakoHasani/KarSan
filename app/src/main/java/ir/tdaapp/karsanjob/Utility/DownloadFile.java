package ir.tdaapp.karsanjob.Utility;

import android.os.Environment;
import android.os.Handler;
import android.os.Looper;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import ir.tdaapp.karsanjob.Services.onDownloadPDF;

public class DownloadFile {

    boolean isRuning = true;

    public void downloadPDF(String Url, String fileName, onDownloadPDF downloadPDF) {
        new Thread(() -> {
            try {
                URL url = new URL(Url);
                HttpURLConnection c = (HttpURLConnection) url.openConnection();
//                    c.setRequestMethod("GET");
//                    c.setDoOutput(true);
                c.connect();

                String PATH = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).toString();
                File file = new File(PATH);
                if (!file.exists()) {
                    file.mkdirs();
                }
                File outputFile = new File(file, fileName);
                FileOutputStream fos = new FileOutputStream(outputFile);
                InputStream is = c.getInputStream();
                byte[] buffer = new byte[1024];
                int len1 = 0;
                while ((len1 = is.read(buffer)) != -1) {
                    fos.write(buffer, 0, len1);
                }
                fos.flush();
                fos.close();
                is.close();

                new Handler(Looper.getMainLooper()).post(() -> {
                    if (isRuning)
                        downloadPDF.onSuccess(file.getPath());
                });
            } catch (Exception e) {
                e.printStackTrace();

                new Handler(Looper.getMainLooper()).post(() -> {
                    if (isRuning)
                        downloadPDF.onError(e);
                });

            }
        }).start();
    }

    public void cancel() {
        isRuning = false;
    }

}
