/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.qrcode.autopsy;

import java.io.File;
import java.util.List;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.qrcode.autopsy.FileAttributes;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.sl.usermodel.VerticalAlignment;
import org.apache.poi.ss.usermodel.CellStyle;

/**
 *
 * @author Harley
 */
public class Report {
    
        private ArrayList<FileAttributes> list;
        private HSSFWorkbook wb;
        private HSSFSheet sheet;
        private String path;
        
        
	public void startup(String path) {
            this.path = path + ".xls";
            File file = new File(path);
            try{
                if(!file.exists()){
                
                    this.list = new ArrayList<FileAttributes>();
                    
                    //crreate workbook
                    wb = new HSSFWorkbook();
		
                    //create sheet
                    sheet = wb.createSheet("QR Code Information");
		
                    //create row: initialize row 0
                    HSSFRow row = sheet.createRow(0);
                    CellStyle  style = wb.createCellStyle();    
                    
                    //style.setVerticalAlignment(VerticalAlignment.MIDDLE); Style- mid
		
		
                    // create grid
                    HSSFCell cell = row.createCell(0);
                    cell.setCellValue("File Name");    
                    cell.setCellStyle(style);                  
                    
                    // second grid
                    cell = row.createCell(1); 
                    cell.setCellValue("Path");
                    cell.setCellStyle(style);
                    
                    // third grid
                    cell = row.createCell(2);                   
                    cell.setCellValue("Content");
                    cell.setCellStyle(style);
                    
                    // forth grid
                    cell = row.createCell(3);           
                    cell.setCellValue("Timestamp");
                    cell.setCellStyle(style);
                    
                    // fifth grid
                    cell = row.createCell(4);                   
                    cell.setCellValue("Type of QRcode");
                    cell.setCellStyle(style);
                    
                    // sixth grid
                    cell = row.createCell(5);                     
                    cell.setCellValue("File Type");
                    cell.setCellStyle(style);
                    
                    // seventh grid
                    cell = row.createCell(6);                   
                    cell.setCellValue("Hash");
                    cell.setCellStyle(style);
                    
                    // eighth grid
                    cell = row.createCell(7);                   
                    cell.setCellValue("Version");
                    cell.setCellStyle(style);
                }
            }catch(Exception e){
                e.printStackTrace();
            }
        }
	
        public synchronized void  generateReport(){
		//Insert data
		for (int i = 0; i < this.list.size(); ++i) {
			FileAttributes file_attr = this.list.get(i);
                        
			HSSFRow row = sheet.createRow(sheet.getLastRowNum() + 1);
                        
			//create new grids and insert data
                        if(sheet.getLastRowNum() <= list.size()){
                            row.createCell(0).setCellValue(file_attr.getName());
                            row.createCell(1).setCellValue(file_attr.getPath());
                            row.createCell(2).setCellValue(file_attr.getContent());
                            row.createCell(3).setCellValue(file_attr.getTimeStamp());
                            row.createCell(4).setCellValue(file_attr.getQRType());
                            row.createCell(5).setCellValue(file_attr.getExtention());
                            row.createCell(6).setCellValue(file_attr.getHash());
                            row.createCell(7).setCellValue(file_attr.getVersion());
                        }
		}
		
		//save file to specific directory with specific name
		try {
			FileOutputStream fout = new FileOutputStream( this.path);
			wb.write(fout);
			fout.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
        // update list of records
	public void addFileAttributes(String name,String path, String con, String ts, String qt, String ex, String hash, String ver){
		
		FileAttributes f1 = new FileAttributes(name, path, con, ts, qt, ex, hash, ver);
		
		this.list.add(f1);
	}
        
        // parse QR code and return content types(eg, plain text or URL or payment code
        public String parseQR(String S){
            boolean isurl = false;
            S.toLowerCase();
            String urlRegex = "(https?|ftp|file)://[-A-Za-z0-9+&@#/%?=~_|!:,.;]+[-A-Za-z0-9+&@#/%=~_|]";
            
            // regular express to match content of QR code
            Pattern pat = Pattern.compile(urlRegex.trim());
            Matcher mat = pat.matcher(S.trim());
            isurl = mat.matches();
            if (isurl) {
                return "URL";
            }
            
            // Pay Code
            try{
                String header = S.substring(0,2);
                int val = Integer.parseInt(header);
                int len = S.length();
                if( 16 <= len || len >= 24 
                        || len == 18 && ( !(10 <= val && val <= 15 ) || (25 <= val && val <= 30))
                        || !(25 <= val && val <= 30))
                    return "Plain text";
                for(int i = 2; i < len; ++i){
                    if( '0' > S.charAt(i) || S.charAt(i) > '9')return "Plain text";
                }
                return len == 18 ? "WeChat PayCode" : "AliPay PayCode";
            }catch(NumberFormatException e ){
                return  "Plain text";
            }
        }
}