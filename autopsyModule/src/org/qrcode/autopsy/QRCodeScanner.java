
package org.qrcode.autopsy;

import com.google.zxing.Binarizer;
import com.google.zxing.BinaryBitmap;
import com.google.zxing.DecodeHintType;
import com.google.zxing.LuminanceSource;
import com.google.zxing.MultiFormatReader;
import com.google.zxing.NotFoundException;
import com.google.zxing.Result;
import com.google.zxing.client.j2se.BufferedImageLuminanceSource;
import com.google.zxing.common.HybridBinarizer;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.imageio.ImageIO;
import jp.sourceforge.qrcode.QRCodeDecoder;
import jp.sourceforge.qrcode.data.QRCodeImage;
import jp.sourceforge.qrcode.data.QRCodeSymbol;
import jp.sourceforge.qrcode.reader.QRCodeImageReader;
import jp.sourceforge.qrcode.exception.DecodingFailedException;

/**
 * Process record and update list which record information of imagefiles
 * @author Harley
 */
public class QRCodeScanner {
    String path = "";
    public QRCodeScanner(String path){
        this.path = path;
    }
   
    public List<String> decode() throws NotFoundException {

        File imageFile = new File(path);
        BufferedImage image = null;
        List<String> decodedData = new ArrayList<String>(){{add("Not an Image"); add("null");add("null");add("null");}};
        
        decodedData.set(2, this.getHash());
        
        SimpleDateFormat form = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
        decodedData.set(3,form.format(imageFile.lastModified()));
        try {
            image = ImageIO.read(imageFile);
        }catch(IOException  e) {
            return decodedData;
        }
        try{
            LuminanceSource source = new BufferedImageLuminanceSource(image);
            Binarizer binarizer = new HybridBinarizer(source);  
            BinaryBitmap binaryBitmap = new BinaryBitmap(binarizer);
            Map<DecodeHintType, Object> hints = new HashMap<DecodeHintType, Object>();
            hints.put(DecodeHintType.CHARACTER_SET, "UTF-8");
            Result result = new MultiFormatReader().decode(binaryBitmap, hints);
            decodedData.set(0, result.getText());
        } catch (Exception e){
        	revalidate(decodedData, image);
        	return decodedData;
        }
        List<String> data = new ArrayList<String>();
        try {
        	data.addAll(decodedData);
        	data.set(1, revalidate(decodedData,image).get(1));
        }catch(Exception e) {
        	return decodedData;
        }
        return data;
    }
    public List<String> revalidate(List<String> decodedData, BufferedImage image) {

        QRCodeDecoder decoder = new QRCodeDecoder();
        try {
            decodedData.set(0,new String(decoder.decode(new J2SEImageGucas(image))));
        }
        catch (DecodingFailedException e) {
            decodedData.set(0, "Not a QR Code");
            return decodedData;
        }
        int[][] intImage = imageToIntArray(new J2SEImageGucas(image));
        QRCodeImageReader imageReader = new QRCodeImageReader();
        QRCodeSymbol qrCodeSymbol = imageReader.getQRCodeSymbol(intImage);
        decodedData.set(1, qrCodeSymbol.getVersionReference());
        return decodedData;
    }
    private static int[][] imageToIntArray(QRCodeImage image) {
	int width = image.getWidth();
	int height = image.getHeight();
	int[][] intImage = new int[width][height];
	for (int y = 0; y < height; ++y) {
            for (int x = 0; x < width; ++x) {
                intImage[x][y] = image.getPixel(x,y);
            }
	}
	return intImage;
    }
    
    private String getHash(){
        String ans = "";
        try{
            InputStream is = Files.newInputStream(Paths.get(path));
            ans = org.apache.commons.codec.digest.DigestUtils.md5Hex(is);
        }catch(Exception e){
            ans = "null";
        }
        return ans;
    }
   
}
class J2SEImageGucas implements QRCodeImage {
    BufferedImage image;

    public J2SEImageGucas(BufferedImage image) {
        this.image = image;
    }

    public int getWidth() {
        return image.getWidth();
    }

    public int getHeight() {
        return image.getHeight();
    }

    public int getPixel(int x, int y) {
        return image.getRGB(x, y);
    }
}
