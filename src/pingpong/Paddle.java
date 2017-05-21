/*
 * Name: Sean Huston 
 * Date: 
 * Lab: 
 */
package pingpong;

import javax.swing.*;
import java.awt.*;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;

/**
 *
 * @author 39443
 */
public class Paddle extends JComponent{

    private int x;
    private int y;
    private int paddleWidth;
    private int paddleHeight;
    private boolean isKeyHeld;
    private boolean side;
    private boolean first;
    
    public Paddle(boolean initSide, int initX, int initY, ReentrantLock mutex)
    {
        x = initX;
        y = initY;
        paddleWidth = 15;
        paddleHeight = 45;
        isKeyHeld = false;
        side = initSide;
        first = true;
        Thread animate = new Thread(new Runnable()
            {
                public void run()
                {
                    while(true)
                    {
                        try {
                            mutex.tryLock(50L,TimeUnit.MILLISECONDS);
                        } catch (InterruptedException ex) {}
                        repaint();
                        try{Thread.sleep(25);}catch(Exception ex){}
                        finally
                        {
                            if(mutex.isHeldByCurrentThread())
                                mutex.unlock();
                        }
                    }
                }
            });
        animate.start();
    }
    
    
    public void paintComponent(Graphics g)
    {
        super.paintComponent(g);
        if(side && first)
        {
            x += -40;
            y = y/2-paddleHeight/2;
            first = false;
        }
        else if(first)
        {
            x = 10;
            y = super.getHeight()/2-paddleHeight/2;
            first = false;
        }
        //System.out.println("(" + x + "," + y + ")");
        Graphics2D g2 = (Graphics2D)g;
        g2.setColor(new Color(PingPong.getOppR(),PingPong.getOppG(),PingPong.getOppB()));
        //g2.setColor(new Color((int)(Math.random()*256),(int)(Math.random()*256),(int)(Math.random()*256)));
        g2.fillRect(x, y, paddleWidth, paddleHeight);
        //System.out.println("Paddle " + x + " " + y);
    }
    
    public void incY()
    {
        if(y+45 < 453)
            y += 5;
    }
    
    public void decY()
    {
        if(y-15 > 0)
            y -= 5;
    }
    
    public int getNewX()
    {
        return(x);
    }
    
    public int getNewY()
    {
        return(y);
    }
    
    public int getPaddleWidth()
    {
        return(paddleWidth);
    }
    
    public int getPaddleHeight()
    {
        return(paddleHeight);
    }
}
