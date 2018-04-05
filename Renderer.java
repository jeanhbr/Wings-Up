import java.awt.Graphics; //Needed to draw in the window
import javax.swing.JPanel; //Needed to create the JPanel

public class Renderer extends JPanel
{
    //Set up of the JPanel
	private static final long serialVersionUID = 1L;

	@Override
	protected void paintComponent(Graphics g)
	{
		super.paintComponent(g);
        //Repaint the game in the JPanel
		Game.game.repaint(g);
	}
	
}
