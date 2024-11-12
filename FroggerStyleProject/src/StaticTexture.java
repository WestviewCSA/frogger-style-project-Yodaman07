import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.geom.AffineTransform;
import java.net.URL;

public class StaticTexture{
	private Image forward; //, backward, left, right; 	
	private AffineTransform tx;
	
	int dir = 0; 					//0-forward, 1-backward, 2-left, 3-right
	int width, height;				//collision detection (hit box)
	int x, y;						//position of the object
	int vx, vy;						//movement variables
	double scaleWidth = 2.0;		//change to scale image
	double scaleHeight = 2.0; 		//change to scale image
	boolean dangerous = false;

	public StaticTexture(String path) {
		//load the main image (front or forward view)
		forward 	= getImage(path); //load the image

		//alter these
		//width and height for hit box
		width = (int) (16*scaleWidth);
		height = (int) (16*scaleHeight);
		//used for placement on the JFrame
		x = -width;
		y = 600;
		
		vx = 0;
		vy = 0;
		
		tx = AffineTransform.getTranslateInstance(0, 0);
		
		init(x, y); 				//initialize the location of the image
									//use your variables
		
	}
	
	
	//2nd constructor - allow setting x and y during construction
	public StaticTexture(int x, int y, String path) {
		//call the default constructor for all the normal stuff
		this(path); // invokes default constructor
		
		//do the specific task for THIS constructor
		this.x = x;
		this.y = y;	
		
	}
	
	//3nd constructor - allow for setting texture danger (does the player die when hitting?
	public StaticTexture(int x, int y, String path, boolean dangerous) {
		//call the default constructor for all the normal stuff
		this(path); // invokes default constructor
		
		//do the specific task for THIS constructor
		this.x = x;
		this.y = y;
		this.dangerous = dangerous;
		
	}
	
	public Rectangle getHitbox() {
		return new Rectangle(x, y, width, height);
	}

	public void paint(Graphics g) {
		//these are the 2 lines of code needed draw an image on the screen
		Graphics2D g2 = (Graphics2D) g;
		
		x+=vx;
		y+=vy;
		
		
		init(x,y);
		
		
		g2.drawImage(forward, tx, null);
		
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
			URL imageURL = StaticTexture.class.getResource(path);
			tempImage = Toolkit.getDefaultToolkit().getImage(imageURL);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return tempImage;
	}

}
