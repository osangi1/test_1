package io.rainbow.billiards.springboot;

import java.util.ArrayList;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;
import org.opencv.core.MatOfPoint;
import org.opencv.core.MatOfRect;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;

public class Test {
	@SuppressWarnings("static-access")
	public static void main(String[] args) {
		
		SquareDetector sd = new SquareDetector();
		
		System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
        Mat imageArray=Imgcodecs.imread("E:\\movie\\image\\7.png");
        
        Mat rgb = new Mat();  //rgb color matrix
        rgb = imageArray.clone();
        Mat grayImage = new Mat();  //grey color matrix
        Mat gradThresh = new Mat();  //matrix for threshold 
        Mat hierarchy = new Mat();    //matrix for contour hierachy
        
        ArrayList<MatOfPoint> squares = new ArrayList<MatOfPoint>();
        squares = (ArrayList<MatOfPoint>) sd.detectBiggestSquare(imageArray);
        
        Imgproc.cvtColor(rgb, grayImage, Imgproc.COLOR_RGB2GRAY);
        Imgproc.adaptiveThreshold(grayImage, gradThresh, 255, Imgproc.ADAPTIVE_THRESH_MEAN_C, Imgproc.THRESH_BINARY_INV, 3, 12);  //block size 3
        //Imgproc.findContours(gradThresh, squares, hierarchy, Imgproc.RETR_EXTERNAL, Imgproc.CHAIN_APPROX_SIMPLE, new Point(0, 0));
        
        for(int i=0;i<squares.size();i++) {
        	Rect rect = Imgproc.boundingRect(squares.get(i));
        	Mat roi = sd.rectangleSubROI(imageArray, rect);
        	
        	Imgcodecs.imwrite("E:\\movie\\image\\7_tmp"+i+".png", roi);
        	roi.release();
        }
        
        
        
        //Imgcodecs.imwrite("E:\\movie\\image\\7_tmp1.png", rgb);
    	//Imgcodecs.imwrite("E:\\movie\\image\\7_tmp2x.png", grayImage);
    	//Imgcodecs.imwrite("E:\\movie\\image\\7_tmp3.png", gradThresh);
    	//Imgcodecs.imwrite("E:\\movie\\image\\7_tmp4.png", hierarchy);
	}
}
