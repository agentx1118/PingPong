/*
 * Name: Sean Huston 
 * Date: 
 * Lab: 
 */
package pingpong;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;
import javax.swing.InputMap;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.List;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;
import java.util.Collections;
import java.util.logging.Level;
import java.util.logging.Logger;
/**
 *
 * @author 39443
 */
public class PingPong implements FocusListener, KeyListener{
    private boolean hadFocus;
    private Paddle paddleR;
    private Paddle paddleL;
    private static int r;
    private static int g;
    private static int b;
    private List<Integer> keys;
    
    public PingPong()
    {
        hadFocus = false;
        r = (int)(Math.random()*256);
        g = (int)(Math.random()*256);
        b = (int)(Math.random()*256);
    }
    
    public void initComponents() {
        ReentrantLock mutex = new ReentrantLock(false);
        keys = Collections.synchronizedList(new ArrayList<>());
        
        double sidePick = Math.random();
        boolean side = sidePick > .5;
                   
        Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
        System.out.println(dim);
        JFrame frameL = new JFrame();
        frameL.setSize(new Dimension(500,500));
        frameL.setLocation((int)(0),(int)(dim.getHeight()/2-dim.getHeight()/4));
        frameL.getContentPane().setBackground(new Color(getR(),getG(),getB()));
        frameL.setLayout(null);
        paddleL = new Paddle(false,0,0,mutex);
        paddleL.setBounds(0,0,500,500);
        frameL.add(paddleL);
        Ball ballL = new Ball(false,500,mutex);
        ballL.setBounds(0,0,500,500);
        frameL.add(ballL);
        frameL.addKeyListener(this);
        //frameL.addKeyListener(this);
        
        
        JFrame frameR = new JFrame();
        frameR.setLayout(null);
        frameR.getContentPane().setBackground(new Color(getR(),getG(),getB()));
        frameR.setSize(new Dimension(500,500));
        paddleR = new Paddle(true,500,500,mutex);
        paddleR.setBounds(0,0,500,500);
        Ball ballR = new Ball(true,500,mutex);
        ballR.setBounds(0,0,500,500);
        frameR.add(ballR);
        frameR.add(paddleR);
        frameR.addKeyListener(this);
        //rameR.addKeyListener(this);
        System.out.println(frameR.getSize());
        
        
        frameR.setLocation((int)(dim.getWidth()-frameR.getWidth()),(int)(dim.getHeight()/2-dim.getHeight()/4));
        
        System.out.println(frameR.getMaximumSize());
        
        
        frameL.setVisible(true);
        frameL.setTitle("Ping!");
        frameR.setVisible(true);
        frameR.setTitle("Pong!");
        frameL.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frameR.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        int count = 1;
        Thread main = new Thread(new Runnable(){
            public synchronized void run(){
                while(true){
                    try {
                        mutex.tryLock(50L,TimeUnit.MILLISECONDS);
                   }catch (InterruptedException ex) {}    
                    int loopCount = 0;
                    for(int e: keys)
                    {
                        switch (e) {
                            case KeyEvent.VK_UP:
                                paddleR.decY();
                                break;
                            case KeyEvent.VK_DOWN:
                                paddleR.incY();
                                break;
                            case KeyEvent.VK_W:
                                paddleL.decY();
                                break;
                            case KeyEvent.VK_S:
                                paddleL.incY();
                                break;
                            default:
                                keys.remove(loopCount);
                                loopCount--;
                                break;
                            }
                        loopCount++;
                    }
                    try{Thread.sleep(50);}catch(Exception ex){}
                    finally
                    {
                        if(mutex.isHeldByCurrentThread())
                            mutex.unlock();
                    }
               }
            }
        });
        main.start();
        while(true)
        {
            
            if((ballR.getNewX()+ballR.getBallWidth()) > paddleR.getNewX() && (ballR.getNewX()+ballR.getBallWidth()) < (paddleR.getNewX()+paddleR.getPaddleWidth()) && (ballR.getNewY()+ballR.getBallHeight()) > paddleR.getNewY() && (ballR.getNewY()) < (paddleR.getNewY()+paddleR.getPaddleHeight()))
            {
                ballR.setHitPaddle(true);
                //System.out.println("Switch");
            }
            if((ballL.getNewX()+ballL.getBallWidth()) > paddleL.getNewX() && (ballL.getNewX()) < (paddleL.getNewX()+paddleL.getPaddleWidth()) && (ballL.getNewY()+ballL.getBallHeight()) > paddleL.getNewY() && (ballL.getNewY()) < (paddleL.getNewY()+paddleL.getPaddleHeight()))
            {
                ballL.setHitPaddle(true);
                //System.out.println("Collision");
            }
            count++;
            if(count == 100)
            {
                r = (int)(Math.random()*256);
                g = (int)(Math.random()*256);
                b = (int)(Math.random()*256);
                count = 1;
                ballR.incSpeed();
                ballL.incSpeed();
            }
            frameR.getContentPane().setBackground(new Color(r,g,b));
            frameL.getContentPane().setBackground(new Color(r,g,b));
            try {
                Thread.sleep(50);
            } catch (InterruptedException ex) {}
        }
        
    }
    
    public void focusLost(FocusEvent e)
    {
        if(hadFocus)
            System.exit(0);
    }
    
    public void focusGained(FocusEvent e)
    {
        hadFocus = !hadFocus;
    }

    @Override
    public void keyTyped(KeyEvent e) {
        
    }

    @Override
    public void keyPressed(KeyEvent e) {
        if(e.getKeyCode() == KeyEvent.VK_UP ||
                e.getKeyCode() == KeyEvent.VK_DOWN ||
                e.getKeyCode() == KeyEvent.VK_W ||
                e.getKeyCode() == KeyEvent.VK_S)
        {
            boolean exists = false;
            for(int i: keys)
            {
                if(i == e.getKeyCode())
                {
                    exists = true;
                }
            }
            if(!exists)
                keys.add(e.getKeyCode());
        }
        System.out.println(keys);
    }

    @Override
    public void keyReleased(KeyEvent e) {
        for(int i = 0; i < keys.size(); i++)
        {
            if(e.getKeyCode() == keys.get(i))
                keys.remove(i);
        }
    }
    
    public static int getR()
    {   
        return(r);
    }
    
    public static int getG()
    {
        return(g);
    }
    
    public static int getB()
    {
        return(b);
    }
    
    public static int getOppR()
    {
        return(255-r);
    }
    
    public static int getOppG()
    {
        return(255-g);
    }
    
    public static int getOppB()
    {
        return(255-b);
    }
}
