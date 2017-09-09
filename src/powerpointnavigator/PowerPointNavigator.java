/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package powerpointnavigator;

/**
 *
 * @author enzo
 */

import org.opencv.imgproc.Imgproc;
import java.awt.event.*;
import java.awt.*;
import java.awt.image.*;
import java.util.Arrays;
import javax.swing.*;
import org.opencv.core.Size;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Size;
import org.opencv.core.Scalar;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.videoio.Videoio;
import org.opencv.videoio.VideoCapture;

public class PowerPointNavigator extends JFrame
{

    /**
     * @param args the command line arguments
     */
    Track track;
    VideoCapture capture;
    Mat image,result;
    int min[]=new int[3];
    int max[]=new int[3];
    JLabel label;
    BufferedImage bi;
    int height,width;
    
    static
    {
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
    }
    public static void main(String[] args) {
        // TODO code application logic here
        try
        {    
        new PowerPointNavigator();
    
        }
        catch(Exception e)
        {
            System.out.println("ERROR h bro" +e);
            JOptionPane.showMessageDialog(null,e.toString());
        }
    }
    
    PowerPointNavigator() throws Exception
    {
        init();
    }
    void init() throws Exception
    {
        
        label=new JLabel();
        add(label);
        capture=new VideoCapture(0);
        capture.set(Videoio.CAP_PROP_FRAME_WIDTH,720);
        capture.set(Videoio.CAP_PROP_FRAME_HEIGHT,720);
        height=(int)capture.get(Videoio.CAP_PROP_FRAME_HEIGHT);
        width=(int)capture.get(Videoio.CAP_PROP_FRAME_WIDTH);
        if( ! capture.isOpened())
        {
            throw new Exception("camera not open");
        }
        track=new Track(width,height);
        //for laser
        min[0]=0;
        min[1]=0;
        min[2]=0;
        max[0]=230;
        max[1]=214;
        max[2]=233;
        
        /*
        
        for red cap of bottle
        min[0]=0;
        min[1]=74;
        min[2]=183;
        max[0]=255;
        max[1]=255;
        max[2]=255;
        */
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setVisible(true);
        start();
    }
    void start()
    {
        image=new Mat();
        while(true)
        {
            capture.read(image);
            if( image.empty())
            {
                System.out.println("frame not captured");
                continue;
            }
            result=new Mat(width,height,CvType.CV_8U);
            applyThreshold();
        }
    }
    void applyThreshold()
    {
        //Mat newImage=new Mat(image.rows(),image.cols(),CvType.CV_8U);
        //Mat image=Imgcodecs.imread("F:\\java programs\\java programs\\openCV\\Thresholding\\redstrips.jpg");
        //System.out.println("\n\nmin array="+Arrays.toString(min));
        //System.out.println("max array="+Arrays.toString(max)+"\n");
        Imgproc.GaussianBlur(image, result,  new Size(11.0, 11.0), 0);
        //Imgproc.medianBlur(image, newImage,  3);
        //System.out.println(image.dump()+"\n");
        threshold(image,result,min,max);
        Imgproc.GaussianBlur(result, result,  new Size(11.0, 11.0), 0);
        //Imgproc.medianBlur(newImage, newImage,  3);
        //System.out.println(image.dump()+"\n");
        bi=toBufferedImage(result);
        label.setIcon(new ImageIcon(bi));
        byte[]b=new byte[result.channels()*result.rows()*result.cols()];
        result.get(0,0,b);
        track.search(b);
        pack();
    }
    BufferedImage toBufferedImage(Mat m)
        {
            int type=BufferedImage.TYPE_BYTE_GRAY;
            if(m.channels()>1)
                type=BufferedImage.TYPE_3BYTE_BGR;
            BufferedImage i=new BufferedImage(m.cols(),m.rows(),type);
            byte target[]=((DataBufferByte)i.getRaster().getDataBuffer()).getData();
            int size=m.channels()*m.rows()*m.cols();
            byte buffer[]=new byte[size];
            m.get(0,0,buffer);
            System.arraycopy(buffer,0,target,0,size);
            return i;
        }
    void threshold(Mat src,Mat dest,int min[],int max[])
    {
        //System.out.println("total="+src.total());
        byte buffer[]=new byte[(int)(src.total()*src.channels())];
        src.get(0,0,buffer);
        //System.out.println("buffer[2]="+(buffer[2] & 0xff));
        //for(int i=0;i<3;i++)
        {
            for(int j=0;j<buffer.length;j=j+3)
            {
                boolean b1=(0xff & buffer[j])>=min[0] && (0xff & buffer[j])<=max[0];
                boolean b2=(0xff & buffer[j+1])>=min[1] && (0xff & buffer[j+1])<=max[1];
                boolean b3=(0xff & buffer[j+2])>=min[2] && (0xff & buffer[j+2])<=max[2];
                if(b1 && b2 && b3)
                {
                    //System.out.println("buffer[j]="+buffer[j]+" buffer[j+1]="+buffer[j+1]+
                     //       " buffer[j+2]="+buffer[j+2]);
                    buffer[j]=(byte)255;
                    buffer[j+1]=(byte)255;
                    buffer[j+2]=(byte)255;
                }
                else
                {
                    buffer[j]=(byte)0;
                    buffer[j+1]=(byte)0;
                    buffer[j+2]=(byte)0;
                }
            }
        }
        dest.put(0, 0, buffer);
    }
}
