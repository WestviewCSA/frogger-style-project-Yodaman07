import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.geom.AffineTransform;
import java.net.URL;

public class Boo{
	private Image forward; //, backward, left, right; 	
	private AffineTransform tx;
	
	int dir = 0; 					//0-forward, 1-backward, 2-left, 3-right
	int width, height;				//collision detection (hit box)
	int x, y;						//position of the object
	int vx, vy;						//movement variables
	double scaleWidth = 2.0;		//change to scale image
	double scaleHeight = 2.0; 		//change to scale image
	int spacing = 100; // default spacing 

	//Boo moves to the right and loops around
	public Boo() {
		//load the main image (front or forward view)
		forward 	= getImage("/imgs/"+"BooSMW_Big.png"); //load the image for Boo

		//alter these
		//width and height for hit box
		width = (int) (16*scaleWidth); //The hitbox should be 16 x 16 (image size is bigger though
		height = (int) (16*scaleHeight);
		//used for placement on the JFrame
		x = -width; //off screen for now
		y = 500; 
		
		vx = 2;
		vy = 0;
		
		tx = AffineTransform.getTranslateInstance(0, 0);
		
		init(x, y); 				//initialize the location of the image
									//use your variables
		
	}
	
	
	//2nd constructor - allow setting x and y during construction
	public Boo(int x, int y, int spacing) {
		//call the default constructor for all the normal stuff
		this(); // invokes default constructor
		
		//do the specific task for THIS constructor
		this.x = x;
		this.y = y;	
		this.spacing = spacing;
		
	}
	
	public Rectangle getHitbox() {
		return new Rectangle(x + (width/4), y + (height/4), width, height);
	}

	public void paint(Graphics g) {
		//these are the 2 lines of code needed draw an image on the screen
		Graphics2D g2 = (Graphics2D) g;
		
		x+=vx;
		y+=vy;	
		
		if (x >= Frame.width + spacing - (Frame.width % spacing) ) {
//			System.out.println(x);
//			System.out.println(Frame.width);
			
			x = -spacing;
		}
		
		init(x,y);
		
		
		g2.drawImage(forward, tx, null);
		
		//draw hit box based on x, y, width, height
		//for collision detection
		if (Frame.debugging) {
			//draw hitbox only if debugging
			g.setColor(Color.green);
			g.drawRect(x + (width/4), y + (height/4), width, height);
			// The (width/4) and (height/4) part moves down the hitbox to center on the sprite instead of being in the top right corner, 
			//as the sprite is 24x24 because it contains extra space for colors
			
		}
		
	}
	
	private void init(double a, double b) {
		tx.setToTranslation(a, b);
		tx.scale(scaleWidth, scaleHeight);
	}

	private Image getImage(String path) {
		Image tempImage = null;
		try {
			URL imageURL = Boo.class.getResource(path);
			tempImage = Toolkit.getDefaultToolkit().getImage(imageURL);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return tempImage;
	}

}
