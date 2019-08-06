/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.qrcode.autopsy;

public class FileAttributes {
	private String name; // 姓名
	private String path; // 身份证
        private String content; // 内容
        private String timeStamp; 
        private String QRType;
        private String extention;
        private String hash;
        private String version;
	//private String status; // 错误状态
	//private String message; // 错误信息
 
	FileAttributes(String name,String path, String con, String ts, String qt, String ex, String hash, String version){
		this.name = name;
		this.path = path;
                this.content = con;
                this.timeStamp = ts;
                this.QRType = qt;
                this.extention = ex;
                this.hash = hash;
                this.version=version;
		//this.status = status;
		//this.message = message;
	}
	
	public String getName() {
		return name;
	}
 
//	public void setName(String name) {
//		this.name = name;
//	}
 
	public String getPath() {
		return path;
	}
 
//	public void setPath(String path) {
//		this.path = path;
//	}
        
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