package face;

import java.util.Vector;

import org.opencv.core.Mat;
import org.opencv.core.Size;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;
import org.w3c.dom.css.Rect;

import cupcnn.Network;

public class ScanImagePyramid {
	private Mat img1x;
	private FaceNetwork faceNetwork;
	private final int trainedFaceSize = 19;
	private int minFaceSize = 19;
	private int maxFaceSize = 95;
	private float scaleStep = 0.8f;
	private int scanStep = 4;
	private float faceThreshod = 0.85f;
	
	public ScanImagePyramid(Mat img,FaceNetwork faceNetwork){
		assert img.width()==trainedFaceSize && img.height()==trainedFaceSize && img.channels()==1:"img size error";
		img1x = img.clone();
		this.faceNetwork = faceNetwork;
	}
	
	public void setMinFaceSize(int size){
		minFaceSize = size;
	}
	public void setMaxFaceSize(int size){
		maxFaceSize = size;
	}
	
	public void setScaleStep(float scaleStep){
		this.scaleStep = scaleStep;
	}
	
	public void setFaceThreshod(float faceThreshod){
		this.faceThreshod = faceThreshod;
	}
	
	public void setScanStep(int scanStep){
		this.scanStep = scanStep;
	}
	
	public static Mat getScaledMat(Mat src,float scale){
        Mat dst=src.clone();  
        //复制矩阵进入dst  
        float width=src.width();  
        float height=src.height();  
          
        Imgproc.resize(src, dst, new Size(width*scale,height*scale)); 
        return dst;
	}
	
	public static int calOverlapArea(FaceInfo a,FaceInfo b){
		int maxL = Math.max(a.startX, b.startX);
		int minR = Math.min(a.stopX, b.stopX);
		int maxT = Math.max(a.startY, b.startY);
		int minB = Math.min(a.stopY, b.stopY);
		int width = minR-maxL;
		int height = minB-maxT;
		if(width>0 && height >0){
			return width*height;
		}
		return 0;
	}
	
	public static int calFaceArea(FaceInfo a){
		return (a.stopX-a.startX)*(a.stopY-a.startY);
	}
	
	private void excludeRepeatFaces(Vector<FaceInfo> faces){
		int index = 0;
		for(FaceInfo info:faces){
			for(int i=index;i<faces.size();i++){
				int overLapArea = calOverlapArea(info,faces.get(i));
				int firstFaceArea = calFaceArea(info);
				int secondFaceArea = calFaceArea(faces.get(i));
				int minFaceArea = Math.min(firstFaceArea, secondFaceArea);
				if(overLapArea>(minFaceArea/2)){
					faces.remove(i);
				}
			}
			index ++;
		}
	}
	
	public Vector<FaceInfo> startScan(){
		Vector<FaceInfo> faces = new Vector<FaceInfo>();
		//人脸越大，需要将图像缩放的越小
		//maxFaceScale是将一张maxFaceScale大小的人脸缩放至trainedFaceSize的人脸所需要的缩放倍数
		final float maxFaceScale = ((float)trainedFaceSize)/maxFaceSize;
		final float minFaceScale = ((float)trainedFaceSize)/minFaceSize;
		for(float startScale = maxFaceScale;startScale<minFaceScale;startScale/=scaleStep){
			Mat img = getScaledMat(img1x,startScale);
			for(int i=0;i<img.height()-trainedFaceSize;i+=scanStep){
				for(int j=0;j<img.width()-trainedFaceSize;j+=scanStep){
					Mat subImg = img.submat(i, i+trainedFaceSize, j, j+trainedFaceSize);
					byte[] imgData = new byte[trainedFaceSize*trainedFaceSize];
					subImg.get(0, 0, imgData);
					double isFaceProb = faceNetwork.predict(imgData);
					if(isFaceProb>faceThreshod){
						FaceInfo faceInfo = new FaceInfo((int)(j/startScale),(int)((j+trainedFaceSize)/startScale),
								(int)(i/startScale),(int)((i+trainedFaceSize)/startScale),isFaceProb);
						if(faces.size()==0){
							faces.add(faceInfo);
						}else{
							//exclude repeat faces
							boolean isOverLap = false;
							for(int k=0;k<faces.size();k++){
								int overLapArea = calOverlapArea(faceInfo,faces.get(k));
								int firstFaceArea = calFaceArea(faceInfo);
								int secondFaceArea = calFaceArea(faces.get(k));
								//System.out.println("overLapArea "+overLapArea+" firstFaceArea "+firstFaceArea+" secondFaceArea "+secondFaceArea);
								int minFaceArea = Math.min(firstFaceArea, secondFaceArea);
								if(overLapArea>(minFaceArea/2)){
									isOverLap = true;
									if(faces.get(k).isFaceProb<faceInfo.isFaceProb){
										faces.remove(k);
										faces.add(faceInfo);
									}
								}
							}
							if(!isOverLap){
								faces.add(faceInfo);
							}
						}
						
					}
				}
			}
		}

		return faces;
	}
}
