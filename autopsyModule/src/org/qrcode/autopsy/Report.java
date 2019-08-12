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
		//第一步创建workbook
                    wb = new HSSFWorkbook();
		
		//第二步创建sheet
                    sheet = wb.createSheet("QR Code Information");
		
		//第三步创建行row:添加表头0行
                    HSSFRow row = sheet.createRow(0);
                    CellStyle  style = wb.createCellStyle();    
		//style.setVerticalAlignment(VerticalAlignment.MIDDLE);  //居中
		
		
		//第四步创建单元格
                    HSSFCell cell = row.createCell(0);         //第一个单元格
                    cell.setCellValue("File Name");                  //设定值
                    cell.setCellStyle(style);                   //内容居中
		
                    cell = row.createCell(1);                   //第二个单元格   
                    cell.setCellValue("Path");
                    cell.setCellStyle(style);
                
                    cell = row.createCell(2);                   //第二个单元格   
                    cell.setCellValue("Content");
                    cell.setCellStyle(style);
                
                    cell = row.createCell(3);                   //第二个单元格   
                    cell.setCellValue("Timestamp");
                    cell.setCellStyle(style);
                
                    cell = row.createCell(4);                   //第二个单元格   
                    cell.setCellValue("Type of QRcode");
                    cell.setCellStyle(style);

                    cell = row.createCell(5);                   //第二个单元格   
                    cell.setCellValue("File Type");
                    cell.setCellStyle(style);

                    cell = row.createCell(6);                   //第二个单元格   
                    cell.setCellValue("Hash");
                    cell.setCellStyle(style);
                
                    cell = row.createCell(7);                   //第二个单元格   
                    cell.setCellValue("Version");
                    cell.setCellStyle(style);
                }
            }catch(Exception e){
                e.printStackTrace();
            }
        }
	
        public synchronized void  generateReport(){
		//第五步插入数据
		//List<FileAttributes> list = Report.getErrorCondition();
		for (int i = 0; i < this.list.size(); ++i) {
			FileAttributes file_attr = this.list.get(i);
			//创建行
                        
			HSSFRow row = sheet.createRow(sheet.getLastRowNum() + 1);
                        
			//创建单元格并且添加数据
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
		
		//第六步将生成excel文件保存到指定路径下
		try {
			FileOutputStream fout = new FileOutputStream( this.path);
			wb.write(fout);
			fout.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void addFileAttributes(String name,String path, String con, String ts, String qt, String ex, String hash, String ver){
		//ArrayList<ErrorCondition> list = new ArrayList<ErrorCondition>();
		
		FileAttributes f1 = new FileAttributes(name, path, con, ts, qt, ex, hash, ver);
		
		this.list.add(f1);
	}
        public String parseQR(String S){
            boolean isurl = false;
            String urlRegex = "[(https://|http://|[a-z0-9.#]+[.]|www.)?]*";

            Pattern pat = Pattern.compile(urlRegex.trim());//比对
            Matcher mat = pat.matcher(S.trim());
            isurl = mat.matches();//判断是否匹配
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