package face;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Vector;

public class BinaryDatasetReader {
	public static class LabelAndImage{
		public int label;
		public byte[] image;
	}
	public static Vector<LabelAndImage> readBina(String name,int width,int height,int channel){
		File file = new File(name);
		if(!file.exists() || !file.isFile()){
			return null;
		}
		Vector<LabelAndImage> vector = new Vector<LabelAndImage>();
		try {
			FileInputStream in = new FileInputStream(file);
			final long fileSize = file.length();
			final int imgSize = width*height*channel;
			final int imgNumber = (int) (fileSize/(imgSize+1));
			for(int i=0;i<imgNumber;i++){
				LabelAndImage lai = new LabelAndImage();
				lai.image = new byte[imgSize];
				lai.label = in.read();
				int readSize = in.read(lai.image);
				if(readSize!=imgSize){
					System.out.println("read error");
					return null;
				}
				vector.add(lai);
			}
			
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return vector;
	}
}
