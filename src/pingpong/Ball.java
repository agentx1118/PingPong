/*
 * Name: Sean Huston 
 * Date: 
 * Lab: 
 */
package pingpong;
import javax.swing.*;
import java.awt.*;
import java.awt.geom.*;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;

/**
 *
 * @author agency
 */
public class Ball extends JComponent{
    private int x;
    private int y;
    private int width;
    private int height;
    private int speed;
    private int velocityX;
    private int velocityY;
    private boolean offScreen;
    private boolean hitPaddle;
    private boolean hitXBoundary;
    private boolean side; //If side is true, object is on the right side of the screen
    private int ballWidth;
    private int ballHeight;
    private double speed2D;
    
    public Ball(boolean initSide, int initY, ReentrantLock mutex)
    {
        side = initSide;
        x = side ? 0:500;
        //y=0;
        y = (int)(Math.random()*470);
        width = 0;
        height = 0;
        speed = 3;
        speed2D = speed;
        velocityX = (side) ? speed:-speed;
        velocityY = (velocityY == 0) ? velocityY = ((Math.random() > .5) ? -1*speed:1*speed):Math.round((int)(Math.random()*speed));
        offScreen = false;
        hitXBoundary = false;
        Thread animate = new Thread(new Runnable()
            {
                public void run()
                {
                    while(true)
                    {
                        try {
                            mutex.tryLock(50L,TimeUnit.MILLISECONDS);
                        } catch (InterruptedException ex) {
                            
                        }
                        repaint();
                        try{Thread.sleep(20);}catch(Exception ex){}
                        finally{
                            if(mutex.isHeldByCurrentThread())
                                mutex.unlock();
                        }
                    }
                }
            });
        animate.start();
    }
    
    public Ball(boolean initSide, int initSpeed, int initVelocityY)
    {
        x = 550;
        y = 550;
        width = 0;
        height = 0;
        speed = initSpeed;
        speed2D = speed;
        velocityX = speed;
        velocityY = initVelocityY;
        offScreen = false;
        hitXBoundary = false;
        side = initSide;
    }
    
    public void paintComponent(Graphics g)
    {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D)g;
        width = 500;
        height = 500;
        ballWidth = 15;
        ballHeight = 15;
        
        if(x + velocityX >= width+ballWidth && (velocityX > 0 || velocityY > 0) && side)
        {
            if(velocityX > 0)
                velocityX--;
            if(velocityX < 0)
                velocityX++;
            if(velocityY > 0)
                velocityY--;
            if(velocityY < 0)
                velocityY++;
            if(velocityX == 0 && velocityY == 0)
                offScreen = true;
            //System.out.println("offscreen");
        }
        else if(x + velocityX < 0 - ballWidth && (velocityX > 0 || velocityY > 0) && !side)
        {
            if(velocityX > 0)
                velocityX--;
            if(velocityX < 0)
                velocityX++;
            if(velocityY > 0)
                velocityY--;
            if(velocityY < 0)
                velocityY++;
            if(velocityX == 0 && velocityY == 0)
                offScreen = true;
        }
        else if(x+velocityX < 0 && side)
        {
            hitXBoundary = true;
            //System.out.println("MAKE YOUR SIDE METHODS");
        }
        else if(x+velocityX > width-ballWidth && !side)
        {
            hitXBoundary = true;
        }
        else if(y + velocityY >= height-ballHeight-30 || y + velocityY <= 0)
        {
            velocityY = -velocityY;
        }
        
        if(hitPaddle && side)
        {
            velocityX = Math.abs(speed)*-1;
            System.out.println(hitPaddle);
            hitPaddle = false;
        }
        else if(hitPaddle)
        {
            velocityX = Math.abs(speed);
            System.out.println(hitPaddle);
            hitPaddle = false;
        }
        
        if(!side)
        {
            //System.out.println("Ball " + x + " " + y + "\tVx " + velocityX + " Vy " + velocityY);
        }
        int newX = x + velocityX;
        int newY = y + velocityY;
        
        //g2.setColor(new Color(PingPong.getOppR(),PingPong.getOppG(),PingPong.getOppB()));
        g2.setColor(new Color(PingPong.getOppR(), PingPong.getOppG(), PingPong.getOppB()));
        g2.fillOval(x,y,ballWidth,ballHeight);
        x = newX;
        y = newY;
        
        //System.out.println("Ball " + x + " " + y + "\tVx " + velocityX + " Vy " + velocityY);
    }
    
    public boolean isOffScreen()
    {
        return(offScreen);
    }
    
    public void reset()
    {
        x = 0;
        y = (int)(Math.random()*height);
        velocityY = (int)(Math.random() * speed);
        hitXBoundary = false;
    }
    
    public void setHitPaddle(boolean newState)
    {
        hitPaddle = true;
    }
    
    public boolean isHitXBoundary()
    {
        return(hitXBoundary);
    }

    public int getSpeed() {
        return speed;
    }

    public int getVelocityX() {
        return velocityX;
    }

    public int getVelocityY() {
        return velocityY;
    }
    
    public int getNewY()
    {
        return(y);
    }
    
    public int getNewX()
    {
        return(x);
    }
    
    public int getBallWidth()
    {
        return(ballWidth);
    }
    
    public int getBallHeight()
    {
        return(ballHeight);
    }
    
    public void setVelocityX(int vX)
    {
        velocityX = vX;
    }
    
    public boolean getSide()
    {
        return(side);
    }
    
    public void incSpeed()
    {
        speed2D += .125;
        speed = (int)speed2D;
    }
}
