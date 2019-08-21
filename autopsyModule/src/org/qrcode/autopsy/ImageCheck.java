/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.qrcode.autopsy;

import java.io.File;
import javax.activation.MimetypesFileTypeMap;

/**
 * check if file that is being read is image file
 * @author Harley
 */
public class ImageCheck {
    private  MimetypesFileTypeMap mtftp;

    public ImageCheck(){
        mtftp = new MimetypesFileTypeMap();
        mtftp.addMimeTypes("image png tif jpg jpeg bmp");
    }
    public boolean isImage(File file){
        String mimetype= mtftp.getContentType(file);
        String type = mimetype.split("/")[0];
        return type.equals("image");
    }
}
