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
import java.awt.Point;
//import java.io.PrintStream;
import java.util.Vector;
import java.awt.Robot;
import java.awt.event.InputEvent;
import javax.swing.JOptionPane;

public class Track 
{
    Robot rob;
    static final int NO_STATE = -1;
    static final int DOWN = 0;
    static final int UP = 1;
    static final int LEFT = 2;
    static final int RIGHT = 4;
    int width;
    int height;
    Vector v = new Vector();
    int prevIndex = -1;
    int currentIndex = -1;
    boolean isNew = true;

    Track(int w, int h) 
    {
        this.width = w;
        this.height = h;
        System.out.println("width=" + w + " height=" + h);
        try
        {
            rob=new Robot();
        }
        catch(Exception e)
        {
            System.out.println("robot error");
        }
    }

    boolean getNew() 
    {
        return this.isNew;
    }

    void setNew(boolean x) 
    {
        this.isNew = x;
    }

    void search(byte[] buffer) 
    {
        int i;
        System.out.println("search was called");
        for (i = 0; i < buffer.length; i += 3) 
        {
            if ((buffer[i] & 255) > 30) 
                continue;
            System.out.println("found at " + i);
            int x = i % (3 * this.width);
            int y = i % this.height;
            this.v.add(new Point(x, y));
            break;
        }
        if (i >= buffer.length) 
        {
            this.getGesture();
            this.v.clear();
        }
        System.out.println("search ended " + i + "\n v= " + this.v);
    }

    void getGesture() 
    {
        System.out.println("gesture was called");
        if (this.v.size() == 0) {
            return;
        }
        Point prev = (Point)this.v.firstElement();
        Point curr = (Point)this.v.lastElement();
        int dx = prev.x - curr.x;
        int dy = prev.y - curr.y;
        if (Math.abs(dx) <= 30 && Math.abs(dy) <= 30) 
        {
            System.out.println("NO_STATE");
            //JOptionPane.showMessageDialog(null, "NO_STATE " + this.v.firstElement() +" "+v.lastElement()+"dx= "+dx +"dy=" +dy);
            return;
        }
        if (Math.abs(dx) > Math.abs(dy)) 
        {
            if (dx > 0) 
            {
                System.out.println("LEFT " + this.v.firstElement() +" "+v.lastElement()+"dx= "+dx +"dy=" +dy);
                rob.mousePress(InputEvent.BUTTON3_MASK);
                try
                {
                    Thread.sleep(1);
                }
                catch(Exception e)
                {
                    
                }
                rob.mouseRelease(InputEvent.BUTTON3_MASK);
                //JOptionPane.showMessageDialog(null, "LEFT " + this.v.firstElement() +" "+v.lastElement()+"dx= "+dx +"dy=" +dy);
            }
            else 
            {
                System.out.println("RIGHT " + this.v.firstElement() +" "+v.lastElement()+"dx= "+dx +"dy=" +dy);
                rob.mousePress(InputEvent.BUTTON1_MASK);
                try
                {
                    Thread.sleep(1);
                }
                catch(Exception e)
                {
                    
                }
                rob.mouseRelease(InputEvent.BUTTON1_MASK);
                //JOptionPane.showMessageDialog(null, "RIGHT " + this.v.firstElement() +" "+v.lastElement()+"dx= "+dx +"dy=" +dy);
            }
        } 
        //else if (Math.abs(dy) > Math.abs(dx)) 
        else
        {
            if (dy < 0) 
            {
                System.out.println("UP");
                
                //JOptionPane.showMessageDialog(null, "UP " + this.v.firstElement() +" "+v.lastElement()+"dx= "+dx +"dy=" +dy);
            }
            else 
            {
                System.out.println("DOWN");
                
                //JOptionPane.showMessageDialog(null, "DOWN " + this.v.firstElement() +" "+v.lastElement()+"dx= "+dx +"dy=" +dy);
            }
        }
        /*
        else 
        {
            JOptionPane.showMessageDialog(null, "UNRESOLVED " + this.v.firstElement() +" "+v.lastElement()+"dx= "+dx +"dy=" +dy);
            System.out.println("UNRESOLVED");
        }
                */
        System.out.println("gesture ended");
    }
}