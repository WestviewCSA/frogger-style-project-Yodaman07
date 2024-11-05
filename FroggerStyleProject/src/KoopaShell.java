import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.geom.AffineTransform;
import java.net.URL;

public class KoopaShell{
	private Image forward; //, backward, left, right; 	
	private AffineTransform tx;
	
	int dir = 0; 					//0-forward, 1-backward, 2-left, 3-right
	int width, height;				//collision detection (hit box)
	int lower_b, upper_b;
	int x, y;						//position of the object
	int vx, vy;						//movement variables
	double scaleWidth = 2.0;		//change to scale image
	double scaleHeight = 2.0; 		//change to scale image
	
	//A koopa shell that bounces around the lava
	public KoopaShell() {
		//load the main image (front or forward view)
		forward 	= getImage("/imgs/"+"DryBones_Shell.png"); //load the image for the DryBonesShell

		//alter these
		//width and height for hit box
		width = (int) (16*scaleWidth); //14 and 22 are sprite sizes
		height = (int) (16*scaleHeight);
		//used for placement on the JFrame
		x = 0; //off screen for now
		y = 300; 
		
		vx = 2;
		vy = 2;
		lower_b = Frame.height; // flipped because (0,0) is the top left corner of the screen
		upper_b = 0;
		
		tx = AffineTransform.getTranslateInstance(0, 0);
		
		init(x, y); 				//initialize the location of the image
									//use your variables
		
	}
	
	
	//2nd constructor - allow setting x and y during construction
	public KoopaShell(int lower_b, int upper_b) {
		//call the default constructor for all the normal stuff
		this(); // invokes default constructor
		
		//do the specific task for THIS constructor
		
		//generate random positions within bounds for the shell
		
		this.x = (int)(Math.random() * (Frame.width+1));
		this.y = (int)(Math.random() * (lower_b-upper_b+1)) + upper_b;
		
		this.lower_b = lower_b;
		this.upper_b = upper_b;
		
	}
	
	public Rectangle getHitbox() {
		return new Rectangle(x, y, width, height);
	}

	public void paint(Graphics g) {
		//these are the 2 lines of code needed draw an image on the screen
		Graphics2D g2 = (Graphics2D) g;
		
		x+=vx;
		y+=vy;	
		
		if ((x >= Frame.width) || (x <= 0)) {
			vx*=-1;
		}
		
		
		if ((y >= lower_b)|| (y <= upper_b)) {
			vy*=-1;
		}
		
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
			URL imageURL = KoopaShell.class.getResource(path);
			tempImage = Toolkit.getDefaultToolkit().getImage(imageURL);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return tempImage;
	}

}
