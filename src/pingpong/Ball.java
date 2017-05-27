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
    private boolean side; 
    //If side is true, object is on the right side of the screen
    private int ballWidth;
    private int ballHeight;
    private double speed2D;
    private static int scoreL;
    private static int scoreR;
    private Sound sound;
    private ReentrantLock mutex;
    private boolean soundPlayed;
    
    public Ball(boolean initSide, ReentrantLock initMutex)
    {
        soundPlayed = false;
        mutex = initMutex;
        sound = new Sound(mutex);
        side = initSide;
        x = side ? 10:460;
        //y=0;
        y = (int)(Math.random()*430)+15;
        width = 0;
        height = 0;
        speed = 3;
        speed2D = speed;
        velocityX = (side) ? speed:-speed;
        velocityY = (velocityY == 0) ? velocityY = ((Math.random() > .5) ?
                -1*speed:1*speed):Math.round((int)(Math.random()*speed));
        offScreen = false;
        hitXBoundary = false;
        scoreR = 0;
        scoreL = 0;
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
                        try{}catch(Exception ex){}
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
        
        if(x + velocityX >= width+ballWidth && (velocityX > 0 || velocityY > 0)
                && side)
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
            {
                offScreen = true;
                scoreL++;
                for(int i = 0; i < 500; i++)
                {
                    //System.out.println(i);
                }
                reset();
            }
            //System.out.println("offscreen");
        }
        else if(x + velocityX < 0 - ballWidth &&
                (velocityX > 0 || velocityY > 0) && !side)
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
            {
                offScreen = true;
                scoreR++;
                for(int i = 0; i < 500; i++)
                {
                    //System.out.println(i);
                }
                reset();
            }
        }
        else if(x+velocityX < 5 && side)
        {
            hitXBoundary = true;
            //System.out.println("MAKE YOUR SIDE METHODS");
        }
        else if(x+velocityX > width-ballWidth/2-25 && !side)
        {
            hitXBoundary = true;
        }
        else if(y + velocityY >= height-ballHeight-45 || y + velocityY <= 15)
        {
            velocityY = -velocityY;
        }
        if(hitPaddle && side)
        {
            velocityX = Math.abs(speed)*-1;
            System.out.println(hitPaddle);
            hitPaddle = false;
            if(!soundPlayed)
            {
                sound.start();
                soundPlayed = true;
            }
        }
        else if(hitPaddle)
        {
            velocityX = Math.abs(speed);
            System.out.println(hitPaddle);
            hitPaddle = false;
            if(!soundPlayed)
            {
                sound.start();
                soundPlayed = true;
            }
        }
        else
        {
            soundPlayed = false;
        }
        
        if(!side)
        {
            //System.out.println("Ball " + x + " " + y + "\tVx " + velocityX
            //+ " Vy " + velocityY);
        }
        int newX = x + velocityX;
        int newY = y + velocityY;
        
        g2.setColor(new Color(PingPong.getOppR(), PingPong.getOppG(),
                PingPong.getOppB()));
        g2.fillOval(x,y,ballWidth,ballHeight);
        
        x = newX;
        y = newY;
        if(hitXBoundary)
        {
            side = !side;
            x = (side) ? 10:460;
            hitXBoundary = false;
            JFrame frameL = PingPong.getFrameL();
            JFrame frameR = PingPong.getFrameR();
            JFrame temp = (JFrame)getParent().getParent().
                    getParent().getParent();
            if(temp.equals(frameR) && !side)
            {
                frameR.remove(this);
                //System.out.println("removeR");
                frameL.add(this);
                //System.out.println("addL");
            }
            else if(temp.equals(frameL) && side)
            {
                frameL.remove(this);
                //System.out.println("removeL");
                frameR.add(this);
                //System.out.println("addR");
            }
        }
        //System.out.println("Ball " + x + " " + y + "\tVx " + 
        //velocityX + " Vy " + velocityY + " Side: " + side + " Speed2D: " +
          //      speed2D);
    }
    
    public boolean isOffScreen()
    {
        return(offScreen);
    }
    
    public void reset()
    {
        boolean oldSide = side;
        double sidePick = Math.random();
        boolean side = sidePick > .5;
        x = side ? 10:460;
        y = (int)(Math.random()*432)+15;
        speed2D = speed = 3;
        velocityX = (side) ? speed:-speed;
        velocityY = (velocityY == 0) ? velocityY = ((Math.random() > .5) ?
                -1*speed:1*speed):Math.round((int)(Math.random()*speed));
        offScreen = false;
        if(oldSide != side && !side)
        {
            hitXBoundary = true;
        }
        if(oldSide != side && side)
        {
            hitXBoundary = true;
        }
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
        speed2D += .25;
        speed = (int)speed2D;
    }
    
    public static int getScoreL()
    {
        return(scoreL);
    }
    
    public static int getScoreR()
    {
        return(scoreR);
    }
}
