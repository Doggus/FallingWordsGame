package fallingWords;

import javax.swing.*;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.FileInputStream;
import java.io.IOException;

import java.util.Scanner;
import java.util.concurrent.*;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
//model is separate from the view.

public class WordApp {
//shared variables
	static int noWords=4;
	static int totalWords;

   	static int frameX=1000;
	static int frameY=600;
	static int yLimit=480;

	static WordDictionary dict = new WordDictionary(); //use default dictionary, to read from file eventually

	static WordRecord[] words;
	static volatile boolean done;  //must be volatile
	public static Score score = new Score();

	static WordPanel w;
        
        static Timer t;
        static boolean stp = false; 
      
	public static void setupGUI(int frameX,int frameY,int yLimit) 
        {
            // Frame init and dimensions
            JFrame frame = new JFrame("WordGame"); 
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setSize(frameX, frameY);
    	
            JPanel g = new JPanel();
            g.setLayout(new BoxLayout(g, BoxLayout.PAGE_AXIS)); 
            g.setSize(frameX,frameY);
 
    	
            w = new WordPanel(words,yLimit,totalWords);
            w.setSize(frameX,yLimit+100);
	    g.add(w);
	    
	    
	    JPanel txt = new JPanel();
	    txt.setLayout(new BoxLayout(txt, BoxLayout.LINE_AXIS)); 
	    JLabel caught = new JLabel("Caught: " + score.getCaught() + "    ");
	    JLabel missed = new JLabel("Missed:" + score.getMissed()+ "    ");
	    JLabel scr = new JLabel("Score:" + score.getScore()+ "    ");    
	    txt.add(caught);
	    txt.add(missed);
	    txt.add(scr);
    
            t = new Timer(20,new ActionListener() { //checks to see if w.Missed() has changed 
                                      //every 20 milliseconds and sets text appropriatly
                
                @Override
                public synchronized void actionPerformed(ActionEvent e) {
                    missed.setText("Missed:" + score.getMissed()+ "    ");  //previously Missed()
                    
                    if (score.getTotal() == totalWords  && stp == false)
                    {
                        JOptionPane.showMessageDialog(null, "Well done, The game is Over." + "\n" + 
                                                            "Your Score was: " + score.getScore() + "\n" +
                                                            "You caught " + score.getCaught() + " words" + "\n" +
                                                            "and missed " + score.getMissed() + " words");
                        w.end();
                        stp = true;
                        
                        score.resetScore();
                        
                        caught.setText("Caught: " + score.getCaught() + "    ");
                        scr.setText("Score:" + score.getScore() + "    ");
                        missed.setText("Missed:" + score.getMissed() + "    ");
                        
                        w.ChangeColour();
                        
                        t.stop();
                    }
                   
                }
            });
            
	    
            int[] len = new int[noWords];
            
	    final JTextField textEntry = new JTextField("",20);
	    textEntry.addActionListener(new ActionListener()
	    {
	      public void actionPerformed(ActionEvent evt) 
              {
	          String text = textEntry.getText();
                 
                  for (int i = 0; i < noWords; i++)
                  {
                    
                    len[i] = words[i].getWord().length();
                    
                    if (words[i].matchWord(text))
                    {
                      w.reset(i, words[i]);
                      score.caughtWord(len[i]);
                      caught.setText("Caught: " + score.getCaught() + "    ");
                      scr.setText("Score:" + score.getScore()+ "    "); //score working through use of len array
                     
                    }
                  }
                  
	          textEntry.setText("");
	          textEntry.requestFocus();
	      }
	    });
	   
	   txt.add(textEntry);
	   txt.setMaximumSize( txt.getPreferredSize() );
	   g.add(txt);
	    
	    JPanel b = new JPanel();
            b.setLayout(new BoxLayout(b, BoxLayout.LINE_AXIS)); 
            
                    JButton startB = new JButton("Start");;
		
		    // add the listener to the jbutton to handle the "pressed" event
		    startB.addActionListener(new ActionListener()
		    {
		      public void actionPerformed(ActionEvent e)
		      {
                          w.Go();
                          t.start();
                          
                          stp = false;
                          
		    	  textEntry.requestFocus();  //return focus to the text entry field
		      }
		    });
                            JButton endB = new JButton("End");;
			
				// add the listener to the jbutton to handle the "pressed" event
			    endB.addActionListener(new ActionListener()
			    {
			      public void actionPerformed(ActionEvent e)
			      {
                                w.end();
                                t.stop();
                                stp = false;
                                
                                caught.setText("Caught: " + score.getCaught() + "    ");
                                scr.setText("Score:" + score.getScore() + "    ");
                                missed.setText("Missed:" + score.getMissed() + "    ");
			      }
			    });
                            
                            JButton pseB = new JButton("Pause");;
			
				// add the listener to the jbutton to handle the "pressed" event
			    pseB.addActionListener(new ActionListener()
			    {
			      public void actionPerformed(ActionEvent e)
			      {
                                w.go=false;
			      }
			    });
                            
                            JButton quitB = new JButton("Quit");;
			
				// add the listener to the jbutton to handle the "pressed" event
			    quitB.addActionListener(new ActionListener()
			    {
			      public void actionPerformed(ActionEvent e)
			      {
                                  System.exit(0); //quits game
                                  t.stop();
			      }
			    });
		
		b.add(startB);
		b.add(endB);
                b.add(pseB);
                b.add(quitB);
		
		g.add(b);
    	
      	frame.setLocationRelativeTo(null);  // Center window on screen.
      	frame.add(g); //add contents to window
        frame.setContentPane(g);     
       	//frame.pack();  // don't do this - packs it into small space
        frame.setVisible(true);

	}

	
public static String[] getDictFromFile(String filename) {
		String [] dictStr = null;
		try {
			Scanner dictReader = new Scanner(new FileInputStream(filename));
			int dictLength = dictReader.nextInt();
			//System.out.println("read '" + dictLength+"'");

			dictStr=new String[dictLength];
			for (int i=0;i<dictLength;i++) {
				dictStr[i]=new String(dictReader.next());
				//System.out.println(i+ " read '" + dictStr[i]+"'"); //for checking
			}
			dictReader.close();
		} catch (IOException e) {
	        System.err.println("Problem reading file " + filename + " default dictionary will be used");
	    }
		return dictStr;

	}

  
            public static void main(String[] args) {
            
                //deal with command line arguments
                Scanner scn = new Scanner(System.in);
                System.out.println("Please enter the total amount of words you would like to play with:");
                int totW = scn.nextInt();
                System.out.println("Please enter the maximum amount of words that will be displayed on the screen at any given time:");
                int noW = scn.nextInt();
                System.out.println("Please enter the name of the file that contains the dicitionary of words:");
                String fname = scn.next();
		 
                totalWords = totW; //total words to fall
	
                noWords = noW; // total words falling at any point
                
                String[] tmpDict=getDictFromFile(fname); //file of words (example_dict.txt)
		if (tmpDict!=null)
			dict = new WordDictionary(tmpDict); //dict = words from file
                
                WordRecord.dict=dict; //set the class dictionary for the words.
                
		//assert(totalWords>=noWords);
                
                if (totalWords>=noWords)
                {
                    
                words = new WordRecord[noWords];  //shared array of current words
                
                int x_inc = (int)frameX/noWords;
	  	//initialize shared array of current words

		for (int i=0;i<noWords;i++) 
                {
			words[i] = new WordRecord(dict.getNewWord(),i*x_inc,yLimit);
		}

                //Start WordPanel thread - for redrawing animation
                setupGUI(frameX, frameY, yLimit); 
                
                }
                else
                {
                    System.out.println("The Total number of words cannot be smaller than the number of words on the screen");
                    System.out.println("Please Restart");
                }
		
                
                
                
                
	}

    

}
