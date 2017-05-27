/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pingpong;
import java.awt.*;
import java.io.File;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;
import javax.swing.*;
/**
 *
 * @author Sean
 */
public class Background extends JComponent{
    private boolean side;
    private String scoreL;
    private String scoreR;
    private int score;
    
    public Background(boolean initSide, ReentrantLock mutex)
    {
        side = initSide;
        score = 5;
        System.out.println("construct");
        createStrasua();
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
    
    public void paintComponent(Graphics g)
    {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D)g;
        g2.setColor(new Color(255,255,255));
        g2.fillRect(0, 5, 500, 10);
        g2.fillRect(0, 447, 500, 10);
        if(!side)
        {
            for(int y = 8; y < 447; y+=15)
               g2.fillRect(475, y, 10, 10);
        }
        if(side)
        {
            for(int y = 8; y < 447; y+=15)
               g2.fillRect(0, y, 10, 10);
        }
        scoreL = "" + Ball.getScoreL();
        scoreR = "" + Ball.getScoreR();
        paintScore(g2);
    }
    
    public void paintScore(Graphics2D g2)
    {
       Font font = new Font("Strasua", Font.PLAIN, 100);
       g2.setFont(font);
       if(!side)
           g2.drawString(scoreL,370,100);
       else if(side)
           g2.drawString(scoreR,70,100);
       font = new Font("Strasua", Font.PLAIN, 75);
       g2.setFont(font);
       if(Ball.getScoreL() == score && !side)
       {
           g2.drawString("YOU WIN", 100, 250);
       }
       else if(Ball.getScoreR() == score && side)
       {
           g2.drawString("YOU WIN", 100, 250);
       }
       if(Ball.getScoreL() == score && side)
       {
           g2.drawString("YOU LOSE", 60, 250);
       }
       else if(Ball.getScoreR() == score && !side)
       {
           g2.drawString("YOU LOSE", 60, 250);
       }
       font = new Font("Strasua", Font.PLAIN, 20);
       g2.setFont(font);
       if(Ball.getScoreL() == score && !side)
       {
           g2.drawString("PRESS \"R\" TO RESTART", 110, 350);
       }
       if(Ball.getScoreL() == score && side)
       {
           g2.drawString("PRESS \"R\" TO RESTART", 65, 350);
       }
    }
    
    private void createStrasua()
    {
        try{
            Font customFont = Font.createFont(Font.TRUETYPE_FONT, new File("strasua.ttf")).deriveFont(12f);
            GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
            //register the font
            ge.registerFont(customFont);
        }
        catch(Exception ex){}
    }
}
