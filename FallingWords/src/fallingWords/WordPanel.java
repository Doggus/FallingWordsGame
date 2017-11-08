package fallingWords;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.concurrent.CountDownLatch;
import java.util.*;
import javax.swing.JPanel;
import javax.swing.Timer;

public class WordPanel extends JPanel implements ActionListener 
{
		public static volatile boolean done;
		private final WordRecord[] words;
                private final int maxY;
		private final int noWords;
                public int totalWords;  //try atomic rather than volatile
                private final int tw;
                
                int[] x; //x = vertical position of panel 
                double[] vex;  //vex = speed of moving words
                
                Timer tm = new Timer(20,this);
                
                boolean go = false;
                boolean stop = false;
                
                private Random r = new Random();
                Color c;
		
		public void paintComponent(Graphics g) 
                {
		    int width = getWidth();
		    int height = getHeight();
		    g.clearRect(0,0,width,height);
                    
		    g.setColor(Color.red);
		    g.fillRect(0,maxY-10,width,height);
		    g.setColor(c);
		    g.setFont(new Font("Helvetica", Font.PLAIN, 26));
		   //draw the words
		   //animation must be added 
		    for (int i=0;i<noWords;i++)
                    {	    	
		    	//g.drawString(words[i].getWord(),words[i].getX(),words[i].getY());	//makes them start above ceiling
		    	g.drawString(words[i].getWord(),words[i].getX(),x[i]-10);  //y-offset so that you can see the words	
		    }
		   tm.start();
		  }
		
		WordPanel(WordRecord[] w, int maxY, int tw) 
                {
			
                    this.words=w; 
                    noWords = words.length;
                    done = false;
                    this.maxY = maxY;	
                    this.totalWords = tw;
                    this.tw = totalWords;
                          
                    x = new int[words.length];
                    vex = new double[words.length];
                    
                    for (int i = 0; i < words.length; i++)
                    {
                        x[i] = 0;
                    }
                    
                    for (int i = 0; i < words.length; i++)
                    {
                        vex[i] = words[i].getSpeed();
                    }
                    
                    ChangeColour();
                    
		}
		
                public void Go()
                {
                    go = true;
                    stop = false;
                }
                
                public synchronized void reset(int i, WordRecord wr)
                {
                    if (totalWords-(noWords) <=0)
                    {
                       x[i] = 0;
                       vex[i] = 0;
                    }
                    else
                    {
                    x[i] = 0;
                    words[i].resetWord();
                    vex[i] = r.nextInt(5)+1;
                    totalWords--;
                    
                    }
                    
                }
                
                public void end()
                {
                    stop = true;
                    totalWords = tw;
                    WordApp.score.resetScore();
                    
                    ChangeColour();
                }
                
                public void ChangeColour()
                {
                    int ran = r.nextInt(6);
                    
                    if (ran==0)
                    {
                        c = Color.BLACK;
                    }
                    
                    if (ran==1)
                    {
                        c = Color.BLUE;
                    }
                    
                    if (ran==2)
                    {
                        c = Color.GREEN;
                    }
                    
                    if (ran==3)
                    {
                        c = Color.RED;
                    }
                    
                    if (ran==4)
                    {
                        c = Color.MAGENTA;
                    }
                    
                    if (ran==5)
                    {
                        c = Color.ORANGE;
                    }
                    
                    
                }
                
                @Override
                public void actionPerformed(ActionEvent e)
                {
                    if (go==true && totalWords+(noWords)>0)
                    {
                        for (int i = 0; i < x.length; i++)
                        {
                           if(x[i] < 480) //drops until it hits the red rectangle 
                           {
                               x[i] = (int)(x[i] + vex[i]); 
                           }
                           else
                           {
                               reset(i,words[i]);
                               WordApp.score.missedWord();
                           }
                           
                           if(stop==true)
                            {
                               x[i] = 0;
                               go = false;
                               words[i].resetWord();
                               vex[i] = r.nextInt(5)+1;
                            }
                          
                              
                        }
                        
                        repaint();
                      
                    }
                    
                   
                    
                   
                }

	}


