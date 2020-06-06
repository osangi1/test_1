package io.rainbow.billiards.springboot;

import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.MatOfPoint2f;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;

import java.util.ArrayList;
import java.util.List;

// Based on OpenCV4Android 3.1
public class SquareDetector {

    /**
     * �簢�� ������ ���� Iteration Ƚ��
     */
    public static int REPEAT = 11;

    /**
     * ���Ϳ��� �ڻ��� ���� ���մϴ�.
     *
     * @param pt1 ����Ʈ 1
     * @param pt2 ����Ʈ 2
     * @param pt0 ����Ʈ 0
     * @return �� �� ������ �ڻ��� ��
     */
    private static double angle (Point pt1, Point pt2, Point pt0) {
        double dx1 = pt1.x - pt0.x;
        double dy1 = pt1.y - pt0.y;
        double dx2 = pt2.x - pt0.x;
        double dy2 = pt2.y - pt0.y;

        return (dx1 * dx2 + dy1 * dy2) /
                Math.sqrt((dx1 * dx1 + dy1 * dy1) * (dx2 * dx2 + dy2 * dy2) + 1e-10);
    }

    /**
     * Approximates a polygonal curve with the specified precision. (like Python API)
     *
     * @param curve Input vector of a 2D point stored in Mat
     * @param epsilon Parameter specifying the approximation accuracy.
     *                (This is the maximum distance between the original curve and its approximation.)
     * @param closed If true, the approximated curve is closed. Otherwise, it is not closed.
     * @return Result of the approximation.
     */
    private static MatOfPoint approxPolyDP (MatOfPoint curve, double epsilon, boolean closed) {
        MatOfPoint2f tempMat = new MatOfPoint2f();
        Imgproc.approxPolyDP(new MatOfPoint2f(curve.toArray()), tempMat, epsilon, closed);
        return new MatOfPoint(tempMat.toArray());
    }

    /**
     * �̹������� �簢���� �����մϴ�.
     *
     * @param image ������ ���� �̹���
     * @return �簢���� ���
     */
    private static List<MatOfPoint> detectSquareList (Mat image) {
        ArrayList<MatOfPoint> squares = new ArrayList<>();

        Mat smallerImg = new Mat(new Size(image.width() / 2, image.height() / 2), image.type());
        Mat gray0 = new Mat(image.size(), CvType.CV_8U);

        // ����� �����ϱ� ���� �ٿ������ �� ���������մϴ�.
        Imgproc.pyrDown(image, smallerImg, smallerImg.size());
        Imgproc.pyrUp(smallerImg, image, image.size());

        // ��� ����鿡�� �簢���� ã���ϴ�.
        for (int c = 0; c < 3; c++) {
            Mat gray = new Mat(image.size(), image.type());
            Core.extractChannel(image, gray, c);

            // ���� ��ġ�� �׽�Ʈ�غ��ϴ�.
            for (int l = 1; l < REPEAT; l++) {
                Imgproc.threshold(gray, gray0, (l+1) * 255 / REPEAT, 255, Imgproc.THRESH_BINARY);

                // ����� ��� ���������� list�� �����մϴ�.
                List<MatOfPoint> contours = new ArrayList<>();
                Imgproc.findContours(gray0, contours, new Mat(), Imgproc.RETR_LIST, Imgproc.CHAIN_APPROX_SIMPLE);

                // ����� �������� �簢���� ���������� ã���ϴ�.
                for (int i = 0; i < contours.size(); i++) {
                	
                    // �������� �ֺ��� ����Ͽ� �뷫���� ������ ���մϴ�.
                    double epsilon = Imgproc.arcLength(new MatOfPoint2f(contours.get(i).toArray()), true) * 0.02;
                    MatOfPoint approx = approxPolyDP(contours.get(i), epsilon, true);

                    // �簢�� �������� �ֺ��� ������ 4���� �������� ������ �־���մϴ�
                    // ���� : ���밪�� ���̴� ������ �������� ���⿡ ���� ������ ���� �޶����� �����Դϴ�.
                    if (approx.toArray().length == 4 
                    		&& Math.abs(Imgproc.contourArea(approx)) > 1000
                            && Imgproc.isContourConvex(approx)) {

                        // ������ ��������� ������� ���� ���̿��� �ִ� �ڻ��� ���� ���մϴ�.
                        double maxCosine = 0;
                        for (int j = 2; j < 5; j++) {
                            maxCosine = Math.max(maxCosine, Math.abs(angle(approx.toArray()[j % 4],
                                    approx.toArray()[j - 2], approx.toArray()[j - 1])));
                        }
                        squares.add(approx);
                        // ��� ������ �ڻ����� ���� ��� �簢������ �߰��մϴ�.
                        if (maxCosine < 0.3) {
                        	//squares.add(approx);
                        }
                    }
                }
            }
        }

        return squares;
    }

    /**
     * �̹������� ���� ū �簢���� �����մϴ�.
     *
     * @param image ������ ���� �̹���
     * @return �簢��
     */
    public static List<MatOfPoint> detectBiggestSquare (Mat image) {
        List<MatOfPoint> squares = detectSquareList(image);
        ArrayList<MatOfPoint> retVal = new ArrayList<>();
        MatOfPoint point = null;

        System.out.println("�簢�� ����  : " + squares.size());
        
        for (MatOfPoint square: squares) {
            if (point == null) {
            	point = square;
            } else if (Math.abs(Imgproc.contourArea(point)) < Math.abs(Imgproc.contourArea(square))) {
                point = square;
            }
            //retVal.add(square);
        }
        
        if (point != null) {
        	retVal.add(point);
        }
        System.out.println("�簢�� ����  : " + retVal.size());
        return retVal;
    }
    
    public Mat rectangleSubROI(Mat input, Rect rect) {
    	final Mat maskCopyTo = Mat.zeros(input.size(), CvType.CV_8UC1);
    	
    	final Mat maskFloodFill = Mat.zeros(new Size(input.cols()+2, input.rows()+2), CvType.CV_8UC1);
    	
    	Imgproc.rectangle(maskCopyTo, rect.tl(), rect.br(), Scalar.all(255), 2, 8, 0);
    	Imgproc.floodFill(maskCopyTo, maskFloodFill
    			, new Point((rect.tl().x + rect.br().x) /2, (rect.tl().y + rect.br().y) / 2), Scalar.all(255), null
    			, Scalar.all(20), Scalar.all(20), 4);
    	
    	final Mat imgRectROI = new Mat();
    	input.copyTo(imgRectROI, maskCopyTo);
    	
    	return imgRectROI;
    }
}