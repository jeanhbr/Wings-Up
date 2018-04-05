/* Copyright © 2018 INSA de Lyon. All rights reserved.
 * Developped by Ines Lebouteiller, Salima Makhloufi, Amaury Chapelle and Jean Haberer
 *
 * The font and the musics of this game are not propriety of the INSA de Lyon and has been chosen for entertainment only,
 * They cannot be used for a commercial use !
 *
 * Class Game is the main class of Wings'Up
 */

//Display
import javax.swing.JFrame; //Needed to create a frame
import java.awt.Graphics; //Needed to handle graphical drawing
import java.awt.Font; //Needed to set a custom font
import java.awt.FontMetrics; //Needed to set titles'position right in place
import java.awt.Dimension; //Needed to get the dimensions of the screen
import java.awt.Toolkit; //Needed to get the dimensions of the screen
import java.awt.Color; //Needed to color the drawing
import javax.swing.Timer; //Needed to animate the drawing by setting a timer that will run a refresh function each 30 milliseconds

//Listener
import java.awt.event.ActionEvent; //Needed to handle an action (for the Timer)
import java.awt.event.ActionListener; //Needed to catch the Timer's event each 30 milliseconds and refresh the view
import java.awt.event.KeyEvent; //Needed to handle an action on the user keyboard
import java.awt.event.KeyListener; //Needed to catch the action on the user keyboard
import java.awt.event.MouseEvent; //Needed to handle an action on the user mouse
import java.awt.event.MouseListener; //Needed to catch the action on the user mouse

//File managment & Imports
import java.io.File; //Needed to import a file in the program
import java.io.IOException; //Needed to import a file and catch eventual errors
import java.awt.FontFormatException; //Needed to import a new font and catch eventual errors

//Audio
import javax.sound.sampled.*; //Needed to handle the audio background

//Pop-ups
import javax.swing.JOptionPane; //Needed to display a pop-up

public class Game extends JFrame implements ActionListener, MouseListener, KeyListener {
    
    //Declarations
    private Graphics g; //We use it to draw in the view
    
    private Dune dune; //Is the class that will draw the landscape (dune and sky behind the bird)
    private Bird bird; //Is the bird with which the user will interact
    private Renderer renderer; //We will draw in it to get a smooth animation
    public static Game game; //Will be created in the main to create the object Game (this class) & start the game
	private Timer timer; //Is the timer that will trigg the refresh of the drawing
    private int H; //Will contain the screen's height
    private int W; //Will contain the screen's width
    public Font customFont; //Will contain the font chosen for the game
    
    private boolean pause = false; //Boolean that tells if the game is paused
    private boolean menu = true; //Boolean that tells if the game is displaying the menu
    private boolean gameover = false;  //Boolean that tells if the game is over
    private boolean playingGame = false;  //Boolean that tells if the game is running
    
    private Clip clipMENU; //will contain the music chosen for the the Menu
    private Clip clipGAME; //will contain the music chosen for the Game
    private Clip clipFXGAMEOVER; //will contain the FX sound chosen for the crash of the bird
   
    private int X=0;  //Distance of the bird from the begining of the game (in pixel)
    
    /* Constructor of the class
     */
	public Game() {
        super("Wings'Up"); //Set the windows' title to >Wings'Up<
        
        try {
            
            //Try to create the custom font to use from the file in the datas folder
            customFont = Font.createFont(Font.TRUETYPE_FONT, new File("DATAS/FONT.ttf")).deriveFont(12f);
            
            //Try to recover the AudioInputStreams from the files in the datas folder
            AudioInputStream MENU = AudioSystem.getAudioInputStream( new File("DATAS/MENU.wav"));
            AudioInputStream GAME = AudioSystem.getAudioInputStream( new File("DATAS/GAME.wav"));
            AudioInputStream FXGAMEOVER = AudioSystem.getAudioInputStream( new File("DATAS/GAMEOVER.wav"));
            
            //For each sound AudioInputStream, try to open it in a Clip to play it later
            clipMENU = AudioSystem.getClip();
            clipMENU.open(MENU);
            
            clipGAME = AudioSystem.getClip();
            clipGAME.open(GAME);
            
            clipFXGAMEOVER = AudioSystem.getClip();
            clipFXGAMEOVER.open(FXGAMEOVER);
            
            //If all theses try have succeeded, then start to play the menu's music
            clipMENU.start();
            
        } catch(IOException ex) {
            //For any error occuring when trying to open a file in the data folder
            System.out.println("Oops, it seems that a problem occured trying to opening a file. If this error persists, please send us the text below with a short description of what happened"); //Print a message in the terminal
            System.out.println(ex); //Print the error in the terminal
            System.exit(0); //Quit the programm
        } catch(FontFormatException ex) {
            //For any error occuring when trying to recover the custom font from the file
            System.out.println("Oops, it seems that a problem occured trying to open the custom font. If this error persists, please send us the text below with a short description of what happened"); //Print a message in the terminal
            System.out.println(ex); //Print the error in the terminal
            System.exit(0); //Quit the programm
        } catch(LineUnavailableException ex) {
            //For any error occuring when trying to recover the audioStream of a music from its file
            System.out.println("Oops, it seems that a problem occured trying to open the audioStream of a music. If this error persists, please send us the text below with a short description of what happened"); //Print a message in the terminal
            System.out.println(ex); //Print the error in the terminal
            System.exit(0); //Quit the programm
        } catch(UnsupportedAudioFileException ex) {
            //For any error occuring when trying to recover a music from its file
            System.out.println("Oops, it seems that a problem occured trying to open a music. If this error persists, please send us the text below with a short description of what happened"); //Print a message in the terminal
            System.out.println(ex); //Print the error in the terminal
            System.exit(0); //Quit the programm
        }
        
        
        //Get screen sizes
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        H = (int)screenSize.getHeight();
        W = (int)screenSize.getWidth();
        //Create the object 'dune', the landscape with in parameters the screen's dimensions
        dune = new Dune(H,W);
        //Create the bird by giving it the path of its image and the screen's dimensions
        bird = new Bird("DATAS/bird1",W,H);
        //Create the renderer
        renderer = new Renderer();
        //and add it to the JFrame so we can draw in it
        add(renderer);
        
        //Display the JFrame and add some proprieties
        this.addMouseListener(this);
        this.addKeyListener(this);
        this.setResizable(false);
		this.setVisible(true);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setBounds(0,0,W,H);

        
        //First painting of the window
        renderer.repaint();
        //Create the timer
        timer = new Timer(30,this);
        //Start the timer
        timer.start();
        
    }
    
    //Called when running the Game class to create a new game and start it
    public static void main(String[] arg) {
        game = new Game();//Create the game (and start it)
    }
    
    //Called each 30 milliseconds by the Timer
	public void actionPerformed(ActionEvent e){
        //Refresh the drawing if the game is neither paused or displaying the menu.
        if ((!pause&&!menu)){
            renderer.repaint();
        }
	}
    
    //Called to repaint (refresh) the drawing
    public void repaint(Graphics g){
        
        //Save the previous X position of the bird
        int lastX = X;
        //Refresh the state of the bird and returning its new X position ...
        //... by giving in parameters landscape's proporieties
        X = bird.refresh(dune);
        //Set the custom font with a size adapted with the current score multiplicator
        g.setFont(customFont.deriveFont((float)30+2*bird.multi));
        //Draw the landscape by giving the current Graphical proprieties, bird proprieties, ...
        //... the last X, the actual X position and the gameover boolean
        dune.draw(g,bird,lastX,X,gameover);
        //Draw the bird over the lanscape by giving the current Graphical proprieties, ...
        //... and a boolean true if either the game is paused or displaying the menu
        gameover = bird.draw(g,menu||pause);
        
        if (gameover){
            //If the game is over
            
            g.setFont(customFont.deriveFont((float)100)); //Set the custom font to the point size of 100
            g.setColor(Color.white); //Set the current drawing color on white
            
            FontMetrics metrics = g.getFontMetrics(); //Get the metrics of the current font
            
            int y = H/4 + ((0 - metrics.getHeight()) / 2) + metrics.getAscent(); // Determine the Y coordinate for the text (note we add the ascent, as in java 2d 0 is top of the screen)
            
            drawCenteredText(W,g,"game over !",y); //Draw the text centered in the window
            
            g.setFont(customFont.deriveFont((float)50)); //Set the custom font to the point size of 50
            metrics = g.getFontMetrics(); //Get its new metrics
            
            y = H/3 + ((0 - metrics.getHeight()) / 2) + metrics.getAscent() + 80;// Determine the Y coordinate for the text
            
            drawCenteredText(W,g,"YOUR SCORE IS  "+bird.score,y); //Draw the text centered in the window
            
            g.setFont(customFont.deriveFont((float)30)); //Set the custom font to the point size of 30
            metrics = g.getFontMetrics(); //Get its new metrics
            
            y = H/3 + ((0 - metrics.getHeight()) / 2) + metrics.getAscent();// Determine the base Y coordinate for the following texts -> with the current metrics
            
            drawCenteredText(W,g,"Press P to PLAY AGAIN",y+160); //Draw the text centered in the window at y+160
            drawCenteredText(W,g,"Press I to see the instructions",y+220); //Draw the text centered in the window at y+220
            drawCenteredText(W,g,"Press Q to QUIT the game",y+280); //Draw the text centered in the window at y+280
            
            if (playingGame){
                //If the Game was not over just before
                clipGAME.stop(); //Stop the music of the game
                clipFXGAMEOVER.setFramePosition(0); //Set the FX of the brid's crash to its begining
                clipFXGAMEOVER.start(); //Play the FX
                clipMENU.setFramePosition(0); //Set the music of the menu to its begining
                clipMENU.start(); //Play the menu's music
                playingGame = false; //Playing game is now false
            }
            
        }else if (!menu){
            //If the menu is not displayed and the game is not over
            
            g.setFont(customFont.deriveFont((float)40)); //Set the custom font to 40
            g.setColor(Color.white); //Set the drawing color to white
            FontMetrics metrics = g.getFontMetrics(); //Get custom font metrics
            int x = 0 + (W/6 - metrics.stringWidth(""+bird.score)) / 2;// Determine the custom X coordinate for the text
            int y = H/14 + ((0 - metrics.getHeight()) / 2) + metrics.getAscent();// Determine the Y coordinate for the text
            g.drawString(""+bird.score,x,y);//Draw the score
        }
        
        if (menu) {
            //If the menu is said to be displayed, then... display it on the screen !
        
            g.setFont(customFont.deriveFont((float)100)); //Set the custom font to 40
            g.setColor(Color.white); //Set the drawing color to white
            FontMetrics metrics = g.getFontMetrics(); //Get custom font current metrics
            
            int y = H/4 + ((0 - metrics.getHeight()) / 2) + metrics.getAscent(); //Determine the Y coordinate for the text
        
            drawCenteredText(W,g,"MENU",y);
            
            g.setFont(customFont.deriveFont((float)30));
            metrics = g.getFontMetrics();
            
            // Determine the Y coordinate for the text
             y = H/3 + ((0 - metrics.getHeight()) / 2) + metrics.getAscent();
            
            drawCenteredText(W,g,"Press P to PLAY or PAUSE",y+100); //Draw the text centered in the window at y+100
            drawCenteredText(W,g,"Press M to see or quit the menu",y+160); //Draw the text centered in the window at y+160
            drawCenteredText(W,g,"Press I to see the instructions",y+220); //Draw the text centered in the window at y+220
            drawCenteredText(W,g,"Press Q to QUIT the game",y+280); //Draw the text centered in the window at y+280
            
        }else if (pause) {
            //If the game is paused
            
            g.setFont(customFont.deriveFont((float)100)); //Set the custom font to 100
            g.setColor(Color.white); //Set the drawing color to white
            FontMetrics metrics = g.getFontMetrics(); //Get custom font current metrics
            
            // Determine the Y coordinate for the text (note we add the ascent, as in java 2d 0 is top of the screen)
            int y = H/2 + ((0 - metrics.getHeight()) / 2) + metrics.getAscent();
            
            drawCenteredText(W,g,"PAUSE !",y); //Draw the text centered in the window at y
            
        }
		
	}
    
    /* Compute the x coordinate for the text to be centered and display it
     * @param int : the width of the screen
     * @param Graphics : The current Graphical instance
     * @param String: The text to display
     * @param int : the y coordinate of the text to display
     */
    public void drawCenteredText(int W, Graphics g, String txt, int y){
        FontMetrics metrics = g.getFontMetrics(); // Determine the current font metrics
        int x = (W - metrics.stringWidth(txt)) / 2; // Determine the X coordinate for the text to be centered
        g.drawString(txt,x,y); //Draw the text centered in the window at (x,y)
    }
    
    
    
    @Override
    public void mouseClicked(MouseEvent e)
    {
        //If the mouse is clicked by the user
        //Note: Unused but has to be there
    }
    
    @Override
    public void keyReleased(KeyEvent e)
    {
        //If a key is released then tell the bird to switch to normal state
        Bird.touched(false);
    }
    
    @Override
    public void mousePressed(MouseEvent e)
    {
        //If the mouse is pressed then tell the bird to switch to heavy state
        Bird.touched(true);
    }
    
    @Override
    public void mouseReleased(MouseEvent e)
    {
        //If the mouse is released then tell the bird to switch to normal state
        Bird.touched(false);
    }
    
    @Override
    public void mouseEntered(MouseEvent e)
    {
        //If the mouse entered the window
        //Note: Unused but has to be there
    }
    
    @Override
    public void mouseExited(MouseEvent e)
    {
        //If the mouse is excited by the user
        //Note: Unused but has to be there
    }
    
    @Override
    public void keyTyped(KeyEvent e)
    {
        //If a key is typed by the user
        //Note: Unused but has to be there
    }
    
    @Override
    public void keyPressed(KeyEvent e)
    {
        //If a key is pressed by the user
        
        if (e.getKeyChar()=='p'){
            //If the key P is pressed (For Play or Pause)
            
            if (!gameover){
                //If the game is not over
                
                //The following line permits to tell the program to pause the game if it was playing...
                //...and play it if it was paused
                pause = !pause;
                
                if (menu){
                    //If menu is displayed
                    
                    pause = false; //Switch the game to not paused
                    menu = false; //Switch to not displaying the menu
                    // In other words, switch to Play !
                    
                    clipMENU.stop(); //Stop playing the Menu's music
                    
                    if (!playingGame){
                        //If the game is not playing (occurs the first time because game is not over, it has never been played yet)
                        
                        clipGAME.setFramePosition(0);//Rewind the game music
                        playingGame = true; //Switch to playing game
                    }
                    clipGAME.start(); //Play the game's music
                    
                    
                }else if (pause){
                    //If the user decided to pause the game
                    
                    renderer.repaint(); //Paint a last time the window to paint the paused view (with the text)
                }
            }else {
                //If the game is over
                
                gameover = false; //The game is not over anymore
                menu = false; //Menu will disappear
                
                //Create a new landscape (with screen's dimension)
                dune = new Dune(H,W);
                //Recreate a bird with the same template than in the constructor
                bird = new Bird("DATAS/bird1",W,H);
                
                clipMENU.stop(); //Stop the Menu's Music
                clipGAME.setFramePosition(0); //Rewind the game's music
                clipGAME.start(); //And play it
                playingGame = true; //Tell the programm that the game is playing
                    
            }
            
        }else if (e.getKeyChar()=='m'){
            //If the key M is pressed (For Menu)
            
            if (!gameover){
                //If the game is not over
                
                //The following line permits to tell the program to display the menu if it was not...
                //...and remove it if it was displayed
                menu = !menu;
                
                if (menu){
                    //If the menu will be presented
                    pause = false; //The game is not displaying the pause view but the menu one
                    clipGAME.stop(); //Stop the game's music
                    clipMENU.setFramePosition(0); //Rewind the menu's music
                    clipMENU.start(); //And play it
                    renderer.repaint(); //Draw the menu
                    
                }else {
                    //If the menu will be removed
                    
                    if (!playingGame){
                        //If the game is not playing
                        //Then we cannot remove the menu
                        
                        menu = true; //So we force it to remain true
                        
                    }else {
                        //If the game is playing
                        
                        clipMENU.stop(); //Stop the menu's music
                        clipGAME.start(); //play the game's music
                        
                    }
                }
            }
        }else if (e.getKeyChar()=='i'){
            //If the key I is pressed (For Instructions or Informations)
            
            if (!menu&&!gameover) {
                //If the menu is not presented and the game is not over
                pause=true; //The we pause it
                renderer.repaint();//And we draw the paused view
            }
            
            //Then we present the pop-up
            JOptionPane.showMessageDialog(null,"WELCOME !\n\n\nSimply press a key or click your mouse to increase \nthe gravity effect on the bird and try \nto speed up by landing on a descent. \nIf you do a perfect landing, \nyou will get huge bonus !\nBut be careful not to crash too hard on a uphill." );
        }else if (e.getKeyChar()=='q'){
            //If the key q is pressed (for QUIT)
            System.exit(0); //Quit the program
        }else {
            //If any other key is pressed
            Bird.touched(true);//Tell the bird to switch to a heavy state
        }
        
    }

}
