import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.geom.AffineTransform;
import java.net.URL;

public class Door{
	private Image closed, open; 	
	private AffineTransform tx;
	private boolean doorOpened = false;
	
	private int width, height;				//collision detection (hit box)
	private int x, y;						//position of the object
	private int vx, vy;						//movement variables
	private double scaleWidth = 2.0;		//change to scale image
	private double scaleHeight = 2.0; 		//change to scale image

	public Door() {
		//loads the two door state images
		closed 	= getImage("/imgs/Door_Closed.png"); //load the image
		open 	= getImage("imgs/Door_Open.png"); //load the image

		//width and height for hit box
		width = (int) (14*scaleWidth); //14 by 28
		height = (int) (28*scaleHeight);
		//used for placement on the JFrame
		x = -width;
		y = 600;
		
		vx = 0;
		vy = 0;
		
		tx = AffineTransform.getTranslateInstance(0, 0);
		
		init(x, y); 				//initialize the location of the image		
	}
	
	
	//2nd constructor - allow setting x and y during construction
	public Door(int x, int y) {
		//call the default constructor for all the normal stuff
		this(); // invokes default constructor
		
		//do the specific task for THIS constructor
		this.x = x;
		this.y = y;		
	}
	
	public void setDoorOpened(boolean status) { doorOpened = status;}
	public boolean isDoorOpened() { return doorOpened;}
	
	public Rectangle getHitbox() {
		return new Rectangle(x, y, width, height);
	}

	public void paint(Graphics g) {
		//these are the 2 lines of code needed draw an image on the screen
		Graphics2D g2 = (Graphics2D) g;
		
		x+=vx;
		y+=vy;
		
		init(x,y);
		
		if (!doorOpened) {g2.drawImage(closed, tx, null);}
		else if (doorOpened) {g2.drawImage(open, tx, null);}
		
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
			URL imageURL = Door.class.getResource(path);
			tempImage = Toolkit.getDefaultToolkit().getImage(imageURL);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return tempImage;
	}
}
