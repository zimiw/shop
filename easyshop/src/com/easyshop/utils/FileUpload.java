package com.easyshop.utils;
import javax.servlet.ServletContext; 

public class FileUpload {
	private ServletContext sc;        
    public String getPath(String path) {           
        return sc.getRealPath(path);       
    }
}
