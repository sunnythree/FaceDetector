package face;

import java.io.File;
import java.util.Vector;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;


public class FaceDetectorTest {
	public static void trainFaceNetwork(){
		Vector<BinaryDatasetReader.LabelAndImage> datas = BinaryDatasetReader.readBina("res/data/face_detection.bin", 19, 19, 1);
		FaceNetwork fn = new FaceNetwork();
		fn.buildNetwork();
		fn.train(datas,30);
		fn.saveModel("model/face.model");
	}
	
	public static void drawFaces(Mat img,Vector<FaceInfo> faces){
		for(FaceInfo f:faces){
			Imgproc.rectangle(img, new Point(f.startX,f.startY), new Point(f.stopX,f.stopY), new Scalar(0,255,0));
		}
	}
	
	public static void testFaceDetector(){
		FaceNetwork fn = new FaceNetwork();
		fn.loadModel("model/face.model");
	
		Mat src = Imgcodecs.imread("res/Test/1.png");
		Mat grayImage = new Mat(); 
		Imgproc.cvtColor(src, grayImage, Imgproc.COLOR_BGR2GRAY,0); 
		ScanImagePyramid sip = new ScanImagePyramid(grayImage,fn);
		sip.setScaleStep(0.8f);
		sip.setMinFaceSize(18);
		sip.setMaxFaceSize(30);
		sip.setScanStep(4);
		sip.setFaceThreshod(0.6f);
		Vector<FaceInfo> faces = sip.startScan();
		System.out.println("facetector finished,get "+faces.size()+" faces ");
		drawFaces(src,faces);
		imshow(src,"test");
		waitKey(0);
	}
	
	public static void imshow(Mat src,String windowName){
		ImageGui ig = new ImageGui(src,"test");
		ig.imshow();
	}
	
	// 0 for wait key press
	public static void waitKey(int mils){
		ImageGui.waitKey(0);
	}

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		System.loadLibrary( Core.NATIVE_LIBRARY_NAME );
		//if you want train nework,Uncomment the following code
		//trainFaceNetwork();
		
		//if you want test,Uncomment the following code
		testFaceDetector();
	}

}
