import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.Timer;

public class Frame extends JPanel implements ActionListener, MouseListener, KeyListener {
	
	//for any debugging code we add
	public static boolean debugging = false;
	public static int width = 600;
	public static int height = 800;
	
	//Timer related variables
	int waveTimer = 5; //each wave of enemies is 20s
	long ellapseTime = 0;
	Font timeFont = new Font("Courier", Font.BOLD, 70);
	int level = 0;
	boolean canLock = false;
	boolean locked = false;
	
	
	Font myFont = new Font("Courier", Font.BOLD, 40);
	SimpleAudioPlayer backgroundMusic = new SimpleAudioPlayer("scifi.wav", false);
//	Music soundBang = new Music("bang.wav", false);
//	Music soundHaha = new Music("haha.wav", false);
	
	
	Luigi luigi = new Luigi();
	
	Boo[] row1 = new Boo[8]; // For scrolling, make sure you have JUST ENOUGH boos so only 1 is offscreen
	//A row of boos (moving)
	Boo[] row2 = new Boo[10];
	
	//A static row of lava tiles
	Lava[][] lavaRows = new Lava[3][19];
	
	KoopaShell[] koopaShells = new KoopaShell[1];
	
	
	

	public void paint(Graphics g) {
		super.paintComponent(g);
		
		//paint the other objects on the screen
		for (Boo b : row1) { 
			b.paint(g);
			if (luigi.getHitbox().intersects(b.getHitbox())) {
				System.out.println("Death to Boo");
				//TODO: Implement game end and restart
			}
		}
		
		for (Boo b : row2) { 
			b.paint(g);
			if (luigi.getHitbox().intersects(b.getHitbox())) {
				System.out.println("Death to Boo");
				//TODO: Implement game end and restart
			}
		}
		
		for (Lava l : lavaRows[0]) { 
			l.paint(g);
			if (( luigi.getHitbox().intersects(l.getHitbox()) && luigi.getHitbox().intersects(koopaShells[0].getHitbox())) && !luigi.getBottomHitbox().intersects(l.getHitbox())) {
				if (!locked) { canLock = true; }
			}
		} // Bottom Row
		
		for (Lava l : lavaRows[1]) { l.paint(g);}
		for (Lava l : lavaRows[2]) { l.paint(g);} // Top Row
		for (KoopaShell ks : koopaShells) {ks.paint(g);}
		
		
		
		if (locked) {
			luigi.x = koopaShells[0].x + 2; // Luigi width is 14 while other textures are 16; 16-14 = 2
			luigi.y = koopaShells[0].y - 25; // Arbitrary number that makes Luigi Look good on the shell
		}
		
		luigi.paint(g);

	}
	
	public static void main(String[] arg) {
		Frame f = new Frame();
		
	}
	
	public Frame() {
		JFrame f = new JFrame("Frogger (Mario edition)");
		f.setSize(new Dimension(width, height));
		f.setBackground(Color.white);
		f.add(this);
		f.setResizable(false);
 		f.addMouseListener(this);
		f.addKeyListener(this);
	
//		backgroundMusic.play();

		/*
		 * Setup any 1D array here! - create the objects that go in them ;) 
		 */
		

		for (int i = 0; i < row1.length; i++) {
			row1[i] = new Boo((int) (i*110), 600, 110);
		}
		
		for (int i = 0; i < row2.length; i++) {
			row2[i] = new Boo((int) (i*100), 500, 100);
		}// TODO: Set up the second row of boos with an offset
		
		for (int j = 0; j < lavaRows[0].length; j++) {		
			lavaRows[0][j] = new Lava((int) (j*32), 400);
			lavaRows[1][j] = new Lava((int) (j*32), 368); //400 - 32 ( scaled height = height * 2 ) in pixels
			lavaRows[2][j] = new Lava((int) (j*32), 336);
		}
		
		for (int k = 0; k < koopaShells.length; k++) {
			koopaShells[k] = new KoopaShell(400, 336);
		}
		
		
		//the cursor image must be outside of the src folder
		//you will need to import a couple of classes to make it fully 
		//functional! use eclipse quick-fixes
		setCursor(Toolkit.getDefaultToolkit().createCustomCursor(
				new ImageIcon("torch.png").getImage(),
				new Point(0,0),"custom cursor"));	
		
		
		Timer t = new Timer(16, this);
		t.start();
		f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		f.setVisible(true);
	}
	
	
	@Override
	public void mouseClicked(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseEntered(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseExited(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mousePressed(MouseEvent m) {
		
	
		
	}

	@Override
	public void mouseReleased(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void actionPerformed(ActionEvent arg0) {
		// TODO Auto-generated method stub
		repaint();
	}

	@Override
	public void keyPressed(KeyEvent arg0) {
//		 TODO Auto-generated method stub
//		System.out.println(arg0.getKeyCode());
		
		switch (arg0.getKeyCode()) {
			case (87): // W
				if (locked) {break;}
				luigi.vy = -3;
				break;
			case(65): // A
				if (locked) {break;}
				luigi.vx = -3;
				break;
			case(83): // S
				if (locked) {break;}
				luigi.vy = 3;
				break;
			case(68): // D
				if (locked) {break;}
				luigi.vx = 3;
				break;
			case (32):
				//Mounting and dismounting
				if (canLock) {
					//Mounting
					System.out.println("Mounting");
					locked = true;
					canLock = false;
					break;
				}
				if (locked) {
					//Dismounting
					System.out.println("Dismount");
					locked = false;
					luigi.y -= 30;
					break;
				}
				
				break;
				//Implement locking mechanic
		}
		
	}

	@Override
	public void keyReleased(KeyEvent arg0) {
		// TODO Auto-generated method stub
		switch (arg0.getKeyCode()) {
		case (87):
			luigi.vy = 0;
			break;
		case(65):
			luigi.vx = 0;
			break;
		case(83):
			luigi.vy = 0;
			break;
		case(68):
			luigi.vx = 0;
			break;
		}
	}

	@Override
	public void keyTyped(KeyEvent arg0) {
		// TODO Auto-generated method stub
		
	}

}
