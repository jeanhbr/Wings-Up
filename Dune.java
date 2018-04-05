//General Imports
import java.awt.Graphics;
import java.awt.Color;

public class Dune {

    double DistanceToNextX;
    double DistanceFromLastX = 0.0;
    double y = 0.0;
    double slope = -0.02;
    //'Amplitude' de la dune
    double A = 0.7;
    
    double colorchange =1;
    double colorchangeslope = 0.0001;
    
    public int H;
    public int W;
    
    public int[] courbe;
    
    
    /* Constructor of the object
     * @param int: Height of the screen
     * @param int: Width of the screen
     */
    public Dune(int h, int w){
        //Recuperation de la largeur de la dune à l'ecran
        W=w;
        H=h;
        //Initialisation du tableau contenant la continuité de pixel formant la courbe de la dune
        courbe = new int[(int)W];
        //Initialisation du prochain sommet
        DistanceToNextX = W/5.0;
        //Initalisatoin de la dune
        setup();
    }
    
    
    /* Fill the table with the curve for the first time
     */
    
    private void setup(){
        //Parcours du tableau de pixel
        for(int i=0;i<courbe.length;i++){
            //Calcul du prochain pixel
            y += A*slope*Math.cos(Math.PI*Math.abs(DistanceToNextX/2.0-DistanceFromLastX)/(DistanceToNextX));
            //Enregistrement dans le tableau
            courbe[(int) i] = (int) y;
            //Pixel suivant
            DistanceFromLastX++;
            //Si nous sommes au pixel ou se trouve le sommet
            if (DistanceToNextX<DistanceFromLastX){
                //Réinitialisation de la position par rapport au sommet
                DistanceFromLastX = 0;
                //Calcul de la distance jusqu'au prochain sommet
                DistanceToNextX = (Math.random()/4.0+1/15.0)*W;
                //Calcul de l'amplitude de la pente (en prenant soin de changer l'orientation de celle ci) 
                if (slope>0) {
                    slope = - 1-Math.random();
                }else {
                    slope =  1+Math.random();
                }
                
            }
        }
    }
    
    
    
    /* Call to refresh the table containing the curve
     * @param int: current speed of the bird
     */
    public void refresh(int vitesse) {
        //Décalage des pixel de droite vers la gauche jusqu'a n-1-vitesse
        //Note la vitesse est donnée par le nombre de cases décalées
        for(int i=0;i<courbe.length-vitesse;i++){
            courbe[i]=courbe[i+vitesse];
        }

        //Calcul des derniers points
        for(int i=courbe.length-vitesse;i<courbe.length;i++){
            //Calcul du prochain pixel
            y += A*slope*Math.cos(Math.PI*Math.abs(DistanceToNextX/2.0-DistanceFromLastX)/(DistanceToNextX));
            //Enregistrement dans le tableau
            courbe[(int) i] = (int) y;
            //Pixel suivant
            DistanceFromLastX++;
            //Si nous sommes au pixel ou se trouve le sommet
            if (DistanceToNextX<DistanceFromLastX){
                //Réinitialisation de la position par rapport au sommet
                DistanceFromLastX = 0;
                //Calcul de la distance jusqu'au prochain sommet
                DistanceToNextX = (Math.random()/4.0+1/15.0)*W;
                //Calcul de l'amplitude de la pente
                if (slope>0) {
                    slope = - 1-Math.random();
                }else {
                    slope =  1+Math.random();
                }
                
            }
        }
        
        
    }
    
    /* Draw the landscape on the screen
     * @param Graphics: current graphical proprieties
     * @param Bird: Bird that is on the screen
     * @param int: last X position
     * @param int: current X position
     * @param boolean: is the game over ?
     */
    public void draw(Graphics g, Bird bird,int lx, int cx,boolean gameover) {
        if (gameover||cx-lx<0){
            refresh(1);
        }else {
            refresh(cx-lx);
        }
        //Dessin du ciel en fond avec une fluctuation de couleur
        g.setColor(new Color((int) (((1-colorchange)*(130)+89)),(int) ( (colorchange)*140+33), (int) ((colorchange)*(150)+100)));
        g.fillRect (0, 0, W, H);
        
        //Marquage du point à suivre sur l'ecran
        //Ce point devant être au milieu de la fenetre
        int center = H/3 - (int) bird.y;
        
        //Parcour la fenetre en large
        for (int i=0; i<courbe.length;i++){
            //Regle la couleur en fluctuation sur celle de la dune
            
            g.setColor(new Color((int) (((colorchange)*(44))+192),(int) ( (colorchange)*(30)+171), (int) ((colorchange)*(20)+83)));
            
            g.drawRect (i, center+courbe[i], 1, H-(center+courbe[i]));
        }
        
        //Actualisation de la fluctuation des couleurs (de sorte que 0<colorchange<1)
        if ((colorchange>=0&&colorchange<=1)){
            colorchange += colorchangeslope*Math.max(1,cx-lx);
        }else {
            colorchangeslope = - colorchangeslope;
            if (colorchange>1) {
                colorchange = 1;
            }else {
                colorchange = 0;
            }
        }
    }
    

}
