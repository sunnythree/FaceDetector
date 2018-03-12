package face;
import java.awt.Graphics;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.JFrame;
import javax.swing.JPanel;

import org.opencv.core.CvType;
import org.opencv.core.Mat;


/**
 * An Gui extention for opencv3.0 using Swing, which can't display images in java.
 * @author Dechao
 * 
 * Description:
 * 
 * examples:
 *  System.loadLibrary("opencv_java300");  
 *  Mat mat = Mat.eye(1000, 2000, CvType.CV_8UC3);
 *  String window_name = "mat"
 *  ImageGui ig = new ImageGui(mat,window_name);
 *  ig.imshow();
 *  ig.waitKey(0);
 *
 */
public class ImageGui extends JPanel implements KeyListener {


    public ImageGui(Mat m, String window) {
        super();
        init(m, window);
    }

    //Elements for paint.
    private Mat mat;
    private boolean firstPaint = true; 
    private BufferedImage out;
    int type;
    private String WINDOW = ""; 
    private JFrame jframe = new JFrame();        
    byte[] data;
    private void Mat2BufIm(){               
        mat.get(0, 0, data);
        out.getRaster().setDataElements(0, 0, mat.cols(), mat.rows(), data);
    }
    private void init(Mat m,String window){
        this.mat = m;
        data = new byte[mat.cols() * mat.rows() * (int)mat.elemSize()];

        WINDOW = window;

        if(mat.channels() == 1)
            type = BufferedImage.TYPE_BYTE_GRAY;
        else
            type = BufferedImage.TYPE_3BYTE_BGR;
        out = new BufferedImage(mat.cols(), mat.rows(), type);
        Mat2BufIm();    
        jframe.add(this);  
        jframe.setSize(mat.cols(), mat.rows());  
        jframe.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); 
        jframe.setTitle(WINDOW);
        jframe.addKeyListener(this);

    }
    @Override  
    public void paintComponent(Graphics g) {  
            g.drawImage(out, 0, 0, null);   
        }
    public void imshow(){
        if(firstPaint){
            jframe.setVisible(true); 
            firstPaint = false;
            }
        Mat2BufIm();
        this.repaint();
    }








    //Elements for waitKey.
    private static Object mt = new Object();
    private static int lastKey = 0;
    private static int key = 0;
    public static int waitKey(int millisecond){
        //TODO ÊµÏÖ¼àÌý¼üÅÌ
        try {
            if(millisecond == 0){
                synchronized(mt){
                    mt.wait();
                }


                }
            Thread.sleep(millisecond);
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        int ret = -1;
        if(key != lastKey){
            ret = key;
            lastKey = key;          
        }

        return ret;

    }
    @Override
    public void keyPressed(KeyEvent e) {
        // TODO Auto-generated method stub
//	      System.out.println(e.getKeyChar() + ", " + e.getKeyCode()
//	                + ", " + KeyEvent.getKeyText(e.getKeyCode())
//	                + ", isActionKey: " + e.isActionKey());
        synchronized(mt){
            mt.notifyAll();
        }
        this.key = e.getKeyCode();

    }
    @Override
    public void keyReleased(KeyEvent arg0) {
        // TODO Auto-generated method stub

    }
    @Override
    public void keyTyped(KeyEvent arg0) {
        // TODO Auto-generated method stub

    }  

}
