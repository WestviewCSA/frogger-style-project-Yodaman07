import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.geom.AffineTransform;
import java.net.URL;

public class DryBones{
	private Image forward; 	
	private AffineTransform tx;
	
	private int width, height;				//collision detection (hit box)
	private int x, y;						//position of the object
	private int vx, vy;						//movement variables
	private double scaleWidth = 2.0;		//change to scale image
	private double scaleHeight = 2.0; 		//change to scale image
	private int spacing = 100; // default spacing 

	public DryBones() {
		forward 	= getImage("/imgs/"+"DryBones.png"); //load the image for the DryBones
		
		//width and height for hit box
		width = (int) (16*scaleWidth); //The hitbox should be 16 x 27
		height = (int) (27*scaleHeight);
		//used for placement on the JFrame
		x = width; //off screen for now
		y = 500; 
		
		vx = -5;
		vy = 0;
		
		tx = AffineTransform.getTranslateInstance(0, 0);
		
		init(x, y); 				//initialize the location of the image
	}
	
	
	//2nd constructor - allow setting x and y during construction
	public DryBones(int x, int y, int spacing) {
		//call the default constructor for all the normal stuff
		this(); // invokes default constructor
		
		//do the specific task for THIS constructor
		this.x = x;
		this.y = y;	
		this.spacing = spacing;
		
	}
	
	public Rectangle getHitbox() { return new Rectangle(x, y, width, height); }

	public void paint(Graphics g) {
		//these are the 2 lines of code needed draw an image on the screen
		Graphics2D g2 = (Graphics2D) g;
		
		x+=vx;
		y+=vy;	
		
		if (x <= -spacing) {
			x = Frame.width + spacing + (Frame.width % spacing);
		}
		
		init(x,y);
		g2.drawImage(forward, tx, null);
		
		//draw hit box based on x, y, width, height
		//for collision detection
		if (Frame.debugging) {
			//draw hitbox only if debugging
			g.setColor(Color.green);
			g.drawRect(x, y , width, height);
		}
		
	}
	
	private void init(double a, double b) {
		tx.setToTranslation(a, b);
		tx.scale(scaleWidth, scaleHeight);
	}

	private Image getImage(String path) {
		Image tempImage = null;
		try {
			URL imageURL = DryBones.class.getResource(path);
			tempImage = Toolkit.getDefaultToolkit().getImage(imageURL);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return tempImage;
	}
}
