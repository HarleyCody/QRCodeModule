/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.qrcode.autopsy;

/**
 * class for every records. store it to list and export list to .xls file
 * @author Harley
 */
public class FileAttributes {
	private String name; 
	private String path; 
        private String content;
        private String timeStamp; 
        private String QRType;
        private String extention;
        private String hash;
        private String version;

 
	FileAttributes(String name,String path, String con, String ts, String qt, String ex, String hash, String version){
		this.name = name;
		this.path = path;
                this.content = con;
                this.timeStamp = ts;
                this.QRType = qt;
                this.extention = ex;
                this.hash = hash;
                this.version=version;

	}
	
	public String getName() {
		return name;
	}
 
 
	public String getPath() {
		return path;
	}
        
        public String getContent(){
            return this.content;
        }
        
        public String getExtention(){
            return this.extention;
        }
        
        public String getQRType(){
            return this.QRType;
        }
        
        public String getTimeStamp(){
            return this.timeStamp;
        }
        
        public String getHash(){
            if(this.hash == null){
                return "null";
            }
            return this.hash;
        }
        
        public String getVersion(){
            return this.version;
        }

}