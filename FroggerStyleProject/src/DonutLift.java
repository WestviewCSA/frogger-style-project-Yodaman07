import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.geom.AffineTransform;
import java.net.URL;

public class DonutLift{
	private Image[] stages = new Image[6];  	
	private AffineTransform tx;
	private int timer = 0;
	private boolean timerOn = false;
	
	private int width, height;				//collision detection (hit box)
	private int x, y;						//position of the object
	private int vx, vy;						//movement variables
	private double scaleWidth = 2.0;		//change to scale image
	private double scaleHeight = 2.0; 		//change to scale image
	

	public DonutLift() {
		//loads all states of the donut lift
		for (int i = 1; i <= stages.length; i++) {
			stages[i-1] = getImage("/imgs/"+"Donut_Lift_" + i + ".png");
		}
		
		width = (int) (16*scaleWidth); //16x16 sprite
		height = (int) (16*scaleHeight);
		//used for placement on the JFrame
		x = 0; //off screen for now
		y = 300; 
		
		vx = 1;
		vy = 0;
		
		tx = AffineTransform.getTranslateInstance(0, 0);
		
		init(x, y); 				//initialize the location of the image
	}
	
	//2nd constructor - allow setting x and y during construction
	public DonutLift(int x, int y) {
		//call the default constructor for all the normal stuff
		this(); // invokes default constructor
		
		//do the specific task for THIS constructor
		this.x = x;
		this.y = y;
	}
	
	public void setTimer(boolean state) {timerOn = state;}
	public boolean getTimer() {return timerOn;}
	
	public Rectangle getHitbox() { return new Rectangle(x, y, width, height);}

	public void paint(Graphics g) {
		//these are the 2 lines of code needed draw an image on the screen
		Graphics2D g2 = (Graphics2D) g;
		if (timerOn) {
			timer++;
			if (timer == 120) {timer =0;}
		} else {timer = 0;}
		
		x+=vx;
		y+=vy;	
		
		if (x >= Frame.width) { x = -32;}
		
		init(x,y);
		
		g2.drawImage(stages[timer/20], tx, null);
		
		//draw hit box based on x, y, width, height
		//for collision detection
		if (Frame.debugging) {
			//draw hitbox only if debugging
			g.setColor(Color.green);
			g.drawRect(x, y, width, height);
		}
		
	}
	
	private void init(double a, double b) {
		tx.setToTranslation(a, b);
		tx.scale(scaleWidth, scaleHeight);
	}
	

	private Image getImage(String path) {
		Image tempImage = null;
		try {
			URL imageURL = DonutLift.class.getResource(path);
			tempImage = Toolkit.getDefaultToolkit().getImage(imageURL);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return tempImage;
	}
}
