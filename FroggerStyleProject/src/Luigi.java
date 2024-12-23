import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.geom.AffineTransform;
import java.net.URL;

public class Luigi{
	private Image forward; 	
	private AffineTransform tx;
	private boolean riding = false;
	
	private int dir = 0; 					//0-forward, 1-backward, 2-left, 3-right
	private int width, height;				//collision detection (hit box)
	private int x, y;						//position of the object
	private int vx, vy;						//movement variables
	private double scaleWidth = 2.0;		//change to scale image
	private double scaleHeight = 2.0; 		//change to scale image
	

	public Luigi() {
		forward 	= getImage("/imgs/"+"LuigiSMW.png"); //load the image for Luigi
		
		//width and height for hit box
		width = (int) (14*scaleWidth); // 14x22 texture
		height = (int) (22*scaleHeight);
		//used for placement on the JFrame
		x = 600/2 - width/2;
		y = 800-(height*2)+15; //not sure about the + 15 but it works?
		vx = 0;
		vy = 0;
		
		tx = AffineTransform.getTranslateInstance(0, 0);
		init(x, y); 				//initialize the location of the image
	}
	
	
	//2nd constructor - allow setting x and y during construction
	public Luigi(int x, int y) {
		//call the default constructor for all the normal stuff
		this(); // invokes default constructor
		
		//do the specific task for THIS constructor
		this.x = x;
		this.y = y;			
	}
	
	public Rectangle getBottomHitbox(){ return new Rectangle(x, y+(3*height/4), width, height/4);}//Bottom forth of Luigi
	
	public Rectangle getHitbox() { return new Rectangle(x, y, width, height);}
	public boolean isCompleted() {
		return y<=50;
	}
	public void paint(Graphics g) {
		//these are the 2 lines of code needed draw an image on the screen
		Graphics2D g2 = (Graphics2D) g;
		if (riding) {x+=vx+1;}
		else {x+=vx;}
		y+=vy;
		
		if (x >= Frame.width-28) { // 14 is player width
			x = Frame.width-28;
		}else if (x <= 0){
			x = 0;
		}
		
		if (y >= Frame.height - 70) {
			y = Frame.height-70;
		} else if (y <= 0) {
			y = 0;
		}
		
		init(x,y);
		g2.drawImage(forward, tx, null);
		
		//draw hit box based on x, y, width, height
		//for collision detection
		if (Frame.debugging) {
			//draw hitbox only if debugging
			g.setColor(Color.green);
			g.drawRect(x, y, width, height);
			
			g.setColor(Color.red);
			g.drawRect(x, y+(3*height/4), width, height/4);
		}
		
	}
	public int getX() {return x;}
	public int getY() {return y;}
	public void setX(int x) {this.x = x;}
	public void setY(int y) {this.y = y;}
	
	public void setVX(int vx) {this.vx = vx;}
	public void setVY(int vy) {this.vy = vy;}
	
	public boolean isRiding() { return riding;}
	public void setRiding(boolean r) { riding = r;}
	
	private void init(double a, double b) {
		tx.setToTranslation(a, b);
		tx.scale(scaleWidth, scaleHeight);
	}

	private Image getImage(String path) {
		Image tempImage = null;
		try {
			URL imageURL = Luigi.class.getResource(path);
			tempImage = Toolkit.getDefaultToolkit().getImage(imageURL);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return tempImage;
	}

}
