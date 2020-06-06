package io.rainbow.billiards.springboot;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.imgcodecs.Imgcodecs;
 
public class OpenCv {    
    public static void main(String[] args) {
        //Load native library
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
    	//System.loadLibrary("opencv-4.3.0-1");
        //image container object
        Mat imageArray;
        //Read image from file system
        imageArray=Imgcodecs.imread("E:\\movie\\image\\7.png");
        //Get image with & height
        System.out.println(imageArray.rows());
        System.out.println(imageArray.cols());
    }
}