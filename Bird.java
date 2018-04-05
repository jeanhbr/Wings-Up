//Similar imports than Game.java
import java.awt.Graphics;
import java.awt.Color;
import javax.imageio.ImageIO;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.WindowConstants;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.sound.sampled.*;
import java.awt.FontMetrics;

//Needed to handle the rotation of the bird
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;


public class Bird {
    
    //Image of the bird for several states
    private BufferedImage IMAGE_NORMAL; //Normal state
    private BufferedImage IMAGE_HEAVY; //Heavy state
    private BufferedImage IMAGE_CONTENT; //Happy state
    
    //Current image of the bird
    private BufferedImage image;
    
    //Clips containing several bird's sounds effect
    private Clip clipWEEE; //When the bird will take off after a perfect landing
    private Clip clipWHOSH; //When the bird take off generally
    private Clip clipPERFECT; //When the bird land
    
    //Position
    public double x = 0;
    public double y = 0;
    
    private double lastXScored=0;
    
    private double alpha = 0;
    private boolean gameover=false;
    private int animX = 0;
    
    //Velocity
    private double vx = 0;
    private double vy = 0;
    
    //Acceleration
    private double ax = 0;
    private double ay = 0;
    
    private double axMin = -0.17;
    
    //X position on the screen
    private int DisplayPos;
    
    //Screen dimension
    private int W;
    private int H;
    
    //Game variables
    public int multi = 1; //Score multiplicator
    public int score = 0; //Score itself
    
    private boolean landingWasPerfect = false;
    private static boolean heavy = false; //True when the bird is heavy
    
    private boolean onGround = false; //True when the bird is landed on the dune
    
    private double slope; //Will contain the slope of the dune

    /* Constructor of the class
     * @param String: Path of the image of the bird
     * @param int: Width of the screen
     * @param int: height of the screen
     */
    public Bird(String t,int w,int h) {
        
        try {
            
            //try to recover bird images from the path given
            IMAGE_NORMAL = ImageIO.read(new File(t+"_NORMAL.png"));
            IMAGE_HEAVY = ImageIO.read(new File(t+"_LOURD.png"));
            IMAGE_CONTENT = ImageIO.read(new File(t+"_CONTENT.png"));

            //Try to recover the audioStream from the files in the data folder
            AudioInputStream WEEE = AudioSystem.getAudioInputStream( new File("DATAS/WEEE.wav"));
            AudioInputStream PERFECT = AudioSystem.getAudioInputStream( new File("DATAS/PERFECT.wav"));
            AudioInputStream WHOSH = AudioSystem.getAudioInputStream( new File("DATAS/WHOSH.wav"));
            
            //Open all the audioStream in their clip
            clipWEEE = AudioSystem.getClip();
            clipWEEE.open(WEEE);
        
            clipWHOSH = AudioSystem.getClip();
            clipWHOSH.open(WHOSH);
            
            clipPERFECT = AudioSystem.getClip();
            clipPERFECT.open(PERFECT);
            
        //Catching all possible errors in terminal and quit the program if one occurs like in game.java
        } catch(IOException ex) {
            System.out.println(ex);
            System.exit(0); //Quit the programm
        } catch(LineUnavailableException ex) {
            System.out.println(ex);
            System.exit(0); //Quit the programm
        } catch(UnsupportedAudioFileException ex) {
            System.out.println(ex);
            System.exit(0); //Quit the programm
        }
        
        image = IMAGE_NORMAL; //Set up of the current bird image on the normal one
        
        //Initiate variables
        W=w;
        H=h;
        DisplayPos = (int) W/6; //The bird is placed at 1/6 of the screen's width
    }
    
    /* Refresh the bird position considering the landscape
     * @param Dune: the landscape of the game
     * @return int: the new X position of the bird
     */
    public int refresh(Dune dune){
        //Ground y-coordinate at the x-coordinate of the bird
        double ground = (double) dune.courbe[(int) DisplayPos];
        
        //Approximation of the slope of the dune (average on 9 values)
        slope = ((double)(dune.courbe[(int) DisplayPos+5]-dune.courbe[(int) DisplayPos-3]))/9;
        
        
        if (!gameover){
            //If the game is not over
            
            if (heavy){
                //If the bird is heavy (user has interacted with a key or the mouse)
                
                ay = 2.8; //Y-acceleration 
                //Air friction
                ax = -0.02; //X-acceleration
                axMin = -vx*0.02-0.3; //Minimum X-Acceleration of the bird (vx>0 always)
                
            }else {
                //else if the bird is not especially heavy (no interaction)
                //Constants are not the same and provide to the bird a more realistic physical interaction
                
                ay = 0.58;
                ax = -0.05;
                axMin = -vx*0.004-0.05;
            }
            
            
            if (onGround){
                //If the bird is on the ground
                
                ay = ay*slope; //Y-acceleration is given by this formula according to the dynamics equations
                ax = Math.max(axMin,ay*(1.68-slope)); //X-Acceleration 1.68 is chosen to make the game more dynamic and difficult, the normal value should be one.
                
                 //Integration of acceleration to get velocity
                vy += ay;
                vx += ax;
                
                if (landingWasPerfect){
                    //If the landing was 'Perfect'
                    //Add some difficulty to the game by accelerating the bird
                    vy+= 0.22;
                    vx+= 0.55;
                }

                if (slope>-0.2&&vy<(ground-y)-2) {
                    //If the bird is on the hill and its velocity on y permits to take off
                    //Note: if vy>0, the bird goes upward (inversed cause of JFrame pixels)
                    //... (ground-y)-2 is the trigger value for the bird to take off because (ground-y)
                    //... is the value of vy if it stays on the ground, we add the -2 to be sure it has enough speed
                    
                    y += vy; //integrate its velocity on y
                    
                    onGround = false; //not anymore on the ground
                    
                    //Play the Whosh sound from the begining
                    clipWHOSH.setFramePosition(0);
                    clipWHOSH.start();
                    
                    //If landing was perfect, play the WEEE sound from the beginning
                    if (landingWasPerfect){
                        clipWEEE.setFramePosition(0);
                        clipWEEE.start();
                    }
                }else {
                    //Else if the bird cannot take off
                    
                    vy = ground - y; //Computing the y-velocity value to be in accordance with the shape of the dune
                    y = ground; //Set the y value
                }
                
                //If vx<1, the bird must however go forward, to ensure that :
                vx = Math.max(1,vx);
                //Integrate X coordinate speed to get the X position of the bird
                x += vx;
                
            }else {
                //If the bird is not on the ground
                //Integrate its position
                vy += ay;
                vx += ax;
                
                vx = Math.max(1,vx);
                
                y += vy;
                x += vx;
                
                //If the bird is touching the ground
                if (y>ground){
                    //Don't let it go further into the ground
                    y=ground;
                    //Tell the program it is on the ground
                    onGround = true;
                    
                    //If the slope corresponds to a hilly climb, and if the speed is high enough, or if the bird is heavy,
                    //the game must be over
                    if (slope<-0.45&&(vy>19||heavy)){
                        //GAME OVER
                        gameover = true;
                    }
                    
                    if (slope>vy/100&&heavy) {
                        //If the angle of the bird (vy/100) corresponds to the slope and the bird is heavy
                        
                        landingWasPerfect = true; //The landing is perfect
                        //We play the corresponding FX
                        clipPERFECT.setFramePosition(0);
                        clipPERFECT.start();
                        
                        //If the multi is 1,
                        if (multi==1){
                            multi = 0;//we set it to 0
                        }
                        multi+=5;// to then add 5 to 5 (we don't want x6 but x5 same for x11...)
                    }else {
                        //Else we tell the program that landing wasn't perfect
                        landingWasPerfect = false;
                        //We reset the multi at 1 to keep scoring the game
                        multi = 1;
                    }
                }
            }
        }else {
            //If the game is over
            animX++;//We simply move the bird with the landscape to get it out of the window
        }
        //Computing the bird's angle
         alpha = vy;
        
        //Setting the right image for the bird
        if (heavy){
            image = IMAGE_HEAVY;
        }else if (gameover){
            image = IMAGE_HEAVY;
        }else if (onGround){
            image = IMAGE_NORMAL;
        }else {
            image = IMAGE_CONTENT;
        }
        
        //Scoring
        if (x>lastXScored+600/multi){
            //if x is greater than the last x (at which we added 1 to the score) plus 600/multi -> which gives us a credible score
            //NOTE: Higher is multi, quicker we will add one to the score
            score+=1;//Add one to the score
            lastXScored = x;
        }
        
        //returing the new X position of the bird
        return (int) x;
    }
    
    
    /* Draw the bird and other informations on the screen
     * @param Graphics: current graphical proprieties
     * @param boolean: tells if there is something on the screen that would hide the text we wanna write
     * @return boolean: true if the game must be over, false otherwise
     */
    
    public boolean draw(Graphics g,boolean writing){
        
        // Rotation information
        
        double rotationRequired = Math.toRadians (alpha);
        double locationX = image.getWidth() / 2;
        double locationY = image.getHeight() / 2;
        AffineTransform tx = AffineTransform.getRotateInstance(rotationRequired, locationX, locationY);
        AffineTransformOp op = new AffineTransformOp(tx, AffineTransformOp.TYPE_BILINEAR);
        
        // Drawing the rotated image at the required drawing locations
        g.drawImage(op.filter(image, null),DisplayPos-37-animX,H/3-65, null);//We added an offset for the bird to be well-placed
        
        
        if (landingWasPerfect&&!writing){
            //If the landing was perfect and there is no other writing on the view that could...
            //...interfere with the multiplicator text
            
            g.setColor(Color.white); //Drawing color to white
            FontMetrics metrics = g.getFontMetrics(); //Get font Metrics of the current one
            int x = 0 + (W - metrics.stringWidth("x"+multi)) / 2; //Determine the X coordinate dor the text to be centered
            int y = H/10 + ((0 - metrics.getHeight()) / 2) + metrics.getAscent(); // Determine the Y coordinate for the text
            
            g.drawString("x"+multi,x,y); //Writing the multiplicator on the screen
    
        }
        
        return gameover;
        
    }
    
    /*Tell the program if the bird must be heavy or not
     * @param boolean: variable telling if the bird must be heavy or not
     */
    public static void touched(boolean b){
        heavy = b; //Transfering the information in a local variable
    }
    
}
