/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.qrcode.autopsy;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * use regular express to determine the type of QR code
 * @author Harley
 */
public class QRClassify {
    public boolean isHttpUrl(String text) {
        boolean isurl= false;
        String regex = "(((https|http)?://)?([a-z0-9]+[.])|(www.))" + "\\w+[.|\\/]([a-z0-9]{0,})?[[.]([a-z0-9]{0,})]+((/[\\S&&[^,;\u4E00-\u9FA5]]+)+)?([.][a-z0-9]{0,}+|/?)";
        Pattern pat=Pattern.compile(regex.trim());
        Matcher mat= pat.matcher(text.trim());
        isurl=mat.matches();
        return isurl;
    } 
}
