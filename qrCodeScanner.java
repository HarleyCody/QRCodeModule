/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

import com.swetake.util.Qrcode;
import jp.sourceforge.qrcode.QRCodeDecoder;
import jp.sourceforge.qrcode.data.QRCodeImage;
import jp.sourceforge.qrcode.data.QRCodeSymbol;
import jp.sourceforge.qrcode.exception.DecodingFailedException;
import jp.sourceforge.qrcode.reader.QRCodeImageReader;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

class QRCodeScanner {
    String path = "";
    public QRCodeScanner(String path){
        this.path = path;
    }
    public List<String> decode() {
        //System.out.println("开始解析二维码！！");
        /* 读取二维码图像数据 */
        File imageFile = new File(path);
        BufferedImage image;
        List<String> decodedData = new ArrayList<String> (){{add("Not an Image");add("null");}};
        try {
            image = ImageIO.read(imageFile);
        }
        catch (IOException e) {
            //System.out.println("读取二维码图片失败： " + e.getMessage());
            return decodedData;
        }
        //System.out.println("读取二维码图片失败： " + e.getMessage());
        /* 解二维码 */
        QRCodeDecoder decoder = new QRCodeDecoder();
        try {
            decodedData.set(0,new String(decoder.decode(new J2SEImageGucas(image))));
        }
        catch (DecodingFailedException e) {
            decodedData.set(0, "Not a QR Code");
            //System.out.println("读取二维码图片失败： " + e.getMessage());
            return decodedData;
        }
        int[][] intImage = imageToIntArray(new J2SEImageGucas(image));
        QRCodeImageReader imageReader = new QRCodeImageReader();
        //QRCodeImageReader imageReader = new QRCodeImageReader();
        QRCodeSymbol qrCodeSymbol = imageReader.getQRCodeSymbol(intImage);
        decodedData.set(1, qrCodeSymbol.getVersionReference());
        /*这里打印出来的就是version和mask pattern 怎么返回就交给你了
        int[][] intImage = imageToIntArray(new J2SEImageGucas(image));
        imageReader = new QRCodeImageReader();
        QRCodeImageReader imageReader = new QRCodeImageReader();
        QRCodeSymbol qrCodeSymbol = imageReader.getQRCodeSymbol(intImage);

        System.out.println("解析内容如下："+decodedData);
        System.out.println("Version: " + qrCodeSymbol.getVersionReference());
        System.out.println("Mask pattern: " + qrCodeSymbol.getMaskPatternRefererAsString());
        */
        return decodedData;
    }
    private static int[][] imageToIntArray(QRCodeImage image) {
        int width = image.getWidth();
        int height = image.getHeight();
        int[][] intImage = new int[width][height];
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                intImage[x][y] = image.getPixel(x,y);
            }
        }
        return intImage;
    }

    public static void main(String[] args) {
        QRCodeScanner sc = new QRCodeScanner("E:/can/3.jpg");
        System.out.println(sc.decode());
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
