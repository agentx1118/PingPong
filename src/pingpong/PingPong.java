/*
 * Name: Sean Huston 
 * Date: 
 * Lab: 
 */
package pingpong;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.concurrent.locks.ReentrantLock;
import java.util.List;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;
import java.util.Collections;
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
    private List<Ball> ballList;
    private static JFrame frameL;
    private static JFrame frameR;
    private int score;
    
    public PingPong()
    {
        hadFocus = false;
        r = 0;
        g = 0;
        b = 0;
        score = 5;
    }
    
    public void initComponents() {
        ReentrantLock mutex = new ReentrantLock(true);
        keys = Collections.synchronizedList(new ArrayList<>());
        ballList = Collections.synchronizedList(new ArrayList<>());
        
        double sidePick = Math.random();
        boolean side = sidePick > .5;
        Ball ball = new Ball(side, mutex);  
        ball.setBounds(0,0,500,500);
        System.out.println(ball);
        ballList.add(ball);
        
        Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
        System.out.println(dim);
        frameL = new JFrame();
        frameL.setSize(new Dimension(500,500));
        frameL.setLocation((int)(0),(int)(dim.getHeight()/2-dim.getHeight()/4));
        frameL.getContentPane().setBackground(new Color(getR(),getG(),getB()));
        frameL.setLayout(null);
        paddleL = new Paddle(false,0,0,mutex);
        paddleL.setBounds(0,0,500,500);
        frameL.add(paddleL);
        if(!side)
            frameL.add(ballList.get(0));
        Background backgroundL = new Background(false,mutex);
        backgroundL.setBounds(0,0,500,500);
        frameL.add(backgroundL);
        frameL.addKeyListener(this);
        //frameL.addKeyListener(this);
        
        
        frameR = new JFrame();
        frameR.setLayout(null);
        frameR.getContentPane().setBackground(new Color(getR(),getG(),getB()));
        frameR.setSize(new Dimension(500,500));
        paddleR = new Paddle(true,500,500,mutex);
        paddleR.setBounds(0,0,500,500);
        frameR.add(paddleR);
        if(side)
            frameR.add(ballList.get(0));
        Background backgroundR = new Background(true,mutex);
        backgroundR.setBounds(0,0,500,500);
        frameR.add(backgroundR);
        frameR.addKeyListener(this);
        //rameR.addKeyListener(this);
        System.out.println(frameR.getSize());
        
        
        frameR.setLocation((int)(dim.getWidth()-frameR.getWidth()),
                (int)(dim.getHeight()/2-dim.getHeight()/4));
        
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
                    try{Thread.sleep(25);}catch(Exception ex){}
                    finally
                    {
                        if(mutex.isHeldByCurrentThread())
                            mutex.unlock();
                    }
               }
            }
        });
        main.setName("frame");
        main.start();
        while(true)
        {
            
            if((ballList.get(0).getNewX()+ballList.get(0).getBallWidth()) >
                    paddleR.getNewX() && (ballList.get(0).getNewX()+
                    ballList.get(0).getBallWidth()) <
                    (paddleR.getNewX()+paddleR.getPaddleWidth()) &&
                    (ballList.get(0).getNewY()+ball.getBallHeight()) >
                    paddleR.getNewY() && (ballList.get(0).getNewY()) <
                    (paddleR.getNewY()+paddleR.getPaddleHeight()) &&
                    ballList.get(0).getSide())
            {
                ballList.get(0).setHitPaddle(true);
                //System.out.println("Switch");
            }
            if((ballList.get(0).getNewX()+ballList.get(0).getBallWidth()) >
                    paddleL.getNewX() && (ballList.get(0).getNewX()) <
                    (paddleL.getNewX()+paddleL.getPaddleWidth()) &&
                    (ballList.get(0).getNewY()+ballList.get(0).getBallHeight())
                    > paddleL.getNewY() && (ballList.get(0).getNewY()) <
                    (paddleL.getNewY()+paddleL.getPaddleHeight()) &&
                    !ballList.get(0).getSide())
            {
                ballList.get(0).setHitPaddle(true);
                //System.out.println("Collision");
            }
            count++;
            if(count == 100)
            {
                count = 1;
                ballList.get(0).incSpeed();
            }
            frameR.getContentPane().setBackground(new Color(r,g,b));
            frameL.getContentPane().setBackground(new Color(r,g,b));
            try {
                Thread.sleep(50);
            } catch (InterruptedException ex) {}
            if(Ball.getScoreL() == score)
            {
                ballList.get(0).getParent().getParent().getParent().getParent()
                        .remove(ballList.get(0));
            }
            else if(Ball.getScoreR() == score)
            {
                ballList.get(0).getParent().getParent().getParent().getParent()
                        .remove(ballList.get(0));
            }
            System.out.println(mutex.getQueueLength() + "holds on the mutex");
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
        //System.out.println(keys);
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
    
    public static JFrame getFrameL()
    {
        return(frameL);
    }
    
    public static JFrame getFrameR()
    {
        return(frameR);
    }
}