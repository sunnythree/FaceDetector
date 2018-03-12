package face;


public class FaceInfo {
	int startX,stopX;
	int startY,stopY;
	double isFaceProb;
	public FaceInfo(int startX,int stopX,int startY,int stopY,double isFaceProb){
		this.isFaceProb = isFaceProb;
		this.startX = startX;
		this.stopX = stopX;
		this.startY = startY;
		this.stopY =stopY;
	}
	
	@Override
	public String toString(){
		return "startX "+startX+" stopX "+stopX+" startY "+startY+" stopY "+stopY+" isFaceProb "+isFaceProb;
	}
}	
