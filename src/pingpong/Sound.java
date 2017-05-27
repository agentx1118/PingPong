/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pingpong;
import java.io.*;
import java.net.URL;
import java.util.concurrent.TimeUnit;
import javax.sound.sampled.*;
import java.util.concurrent.locks.ReentrantLock;

/**
 *
 * @author Sean
 */
public class Sound {
    private static SourceDataLine auxLine = null;
    private ReentrantLock mutex;
    
    public Sound(ReentrantLock initMutex)
    {
        mutex = initMutex;     
    }
    
    public void start()
    {
        try{
            File file = new File("pong.wav");
            AudioInputStream auxIn = AudioSystem.getAudioInputStream(file);
            AudioFormat audioFormat = auxIn.getFormat();
            DataLine.Info info = new DataLine.Info(SourceDataLine.class, audioFormat);
            auxLine = (SourceDataLine) AudioSystem.getLine(info);
            auxLine.open(audioFormat);
            auxLine.start();
            Thread play = new Thread(new Runnable()
            {
                int nBytesRead = 0;
                byte[] sampledData = new byte[16*1024];
                public void run()
                {
                    try{
                        mutex.tryLock(50L, TimeUnit.MILLISECONDS);
                        while (nBytesRead != -1) {
                            nBytesRead = auxIn.read(sampledData, 0, sampledData.length);
                            if (nBytesRead >= 0) {
                                auxLine.write(sampledData, 0, nBytesRead);
                            }
                        }
                    }
                    catch(Exception ex){}
                    finally{
                        if(mutex.isHeldByCurrentThread())
                                mutex.unlock();
                    }
                }                
            });
            play.setName("Sound");
            if(!mutex.hasQueuedThread(play))
                play.start();
        }
        catch(Exception ex){}  
    }
}
