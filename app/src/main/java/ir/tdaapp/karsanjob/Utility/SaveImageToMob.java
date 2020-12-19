package ir.tdaapp.karsanjob.Utility;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Environment;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Created by Diako on 7/9/2019.
 */

public class SaveImageToMob {

    public static boolean SaveImageToSdCard(Bitmap img,String Name) {

        File image = new File(GetPath()+"/"+Name);

        File DirectoryExist=new File(GetPath());

        boolean success = false;

        FileOutputStream outStream;

        try {

            if (!DirectoryExist.exists()) {
                DirectoryExist.mkdirs();
            }

            outStream = new FileOutputStream(image);
            img.compress(Bitmap.CompressFormat.JPEG, 100, outStream);
        /* 100 to keep full quality of the image */

            outStream.flush();
            outStream.close();
            success = true;

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return success;
    }

    public static String SaveImageToSdCard(String Name,Bitmap bmp){

        File image = new File(GetPath()+"/"+Name);

        File DirectoryExist=new File(GetPath());


        FileOutputStream outStream;

        try {

            if (!DirectoryExist.exists()) {
                DirectoryExist.mkdirs();
            }

            outStream = new FileOutputStream(image);
            bmp.compress(Bitmap.CompressFormat.JPEG, 100, outStream);
        /* 100 to keep full quality of the image */

            outStream.flush();
            outStream.close();

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return image.getPath();
    }

    public static String GetPath(){
        File sdCardDirectory = Environment.getExternalStorageDirectory();

        String Path=sdCardDirectory.getPath()+"/Karsan";

        return Path;
    }

    public static String SaveImageCamera(String Name,Bitmap bmp,Context context){

        File sdCardDirectory = Environment.getExternalStorageDirectory();

        String Path=sdCardDirectory.getPath();

        File image = new File(Path+"/"+Name);


        FileOutputStream outStream;

        try {
            outStream = new FileOutputStream(image);
            bmp.compress(Bitmap.CompressFormat.JPEG, 100, outStream);
        /* 100 to keep full quality of the image */

            outStream.flush();
            outStream.close();

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return image.getPath();
    }

}
