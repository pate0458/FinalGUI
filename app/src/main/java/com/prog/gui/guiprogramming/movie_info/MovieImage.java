package com.prog.gui.guiprogramming.movie_info;

import android.os.AsyncTask;
import android.os.Environment;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * MovieImage is an AsyncTask used to storing poster-images to local storage
 * */
public class MovieImage extends AsyncTask<Void, Integer, String> {

    //URL to fetch poster-image
    private String imgURL;

    /**
     * @param imgURL URL to fetch poster-image
     * */
    public MovieImage(String imgURL) {
        this.imgURL = imgURL;
    }

    @Override
    protected String doInBackground(Void... voids) {
        InputStream input = null;
        try {
            URL url = new URL(imgURL);

            String fileName = imgURL.substring(imgURL.lastIndexOf('/') + 1, imgURL.length());
            File storagePath = new File(Environment.getExternalStorageDirectory().getPath() +
                    File.separator + "MOVIE_INFO_APP" + File.separator + "MOVIE_IMAGES");
            //If folder does not exists, then create a new one
            if(!storagePath.exists()){
                storagePath.mkdirs();
            }

            //If poster-image already exists, then return
            if(new File(storagePath + "/" + fileName).exists()){
                return null;
            }

            //Downloading image using 1024 sized buffer,
            // and saving it to directory : GP_MOVIE_IMAGES inside DCIM directory on local storage
            input = url.openStream();
            OutputStream output = new FileOutputStream(storagePath + "/" + fileName);
            try {
                byte[] buffer = new byte[1024];
                int bytesRead = 0;
                //reading file in byte form.
                while ((bytesRead = input.read(buffer, 0, buffer.length)) >= 0) {
                    output.write(buffer, 0, bytesRead);
                }
            } finally {
                output.close();
                input.close();
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
