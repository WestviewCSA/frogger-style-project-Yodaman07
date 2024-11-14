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
	boolean canLock = false; // KoopaShell Logic
	boolean locked = false; // KoopaShell Logic
	
	Font myFont = new Font("Courier", Font.BOLD, 40);
	SimpleAudioPlayer backgroundMusic = new SimpleAudioPlayer("scifi.wav", false);
//	Music soundBang = new Music("bang.wav", false);
//	Music soundHaha = new Music("haha.wav", false);
	
	//TODO: Make all class variables private and access w/getters and setters
	Luigi luigi = new Luigi();
	
	Boo[] row1 = new Boo[8]; // For scrolling, make sure you have JUST ENOUGH boos so only 1 is offscreen
	Boo[] row2 = new Boo[10]; 	//A row of boos (moving)
	DryBones[] row3 = new DryBones[5]; //3rd row of scrolling objects
	
	DonutLift[] donutLifts_1 = new DonutLift[12]; // Horizontally moving donut lifts
	DonutLift[] donutLifts_2 = new DonutLift[12];
	
	StaticTexture[][] bgRows = new StaticTexture[21][19]; 	//All background textures
	
	KoopaShell[] koopaShells = new KoopaShell[1];
	
	
	

	public void paint(Graphics g) {
		super.paintComponent(g);
		
		for (int i = 0; i < bgRows.length; i++) {
			for (StaticTexture l : bgRows[i]) { 
				l.paint(g);
				
				if (i == 0) { // Logic that only applies to the bottom lava row
					if (( luigi.getHitbox().intersects(l.getHitbox()) && luigi.getHitbox().intersects(koopaShells[0].getHitbox())) && !luigi.getBottomHitbox().intersects(l.getHitbox())) {
						if (!locked) { canLock = true; }
					}
				}
				
				if (l.dangerous) { //Only for "dangerous" textures (lava)
					if (luigi.getBottomHitbox().intersects(l.getHitbox()) && !locked) {
//						System.out.println("LAVA DEATH");
					}
				}				
			}
		} //Paint background first
		
		//paint the other objects on the screen
		for (Boo b : row1) { 
			b.paint(g);
			if (luigi.getHitbox().intersects(b.getHitbox())) {
//				System.out.println("Death to Boo");
				//TODO: Implement game end and restart
			}
		}
		
		for (Boo b : row2) { 
			b.paint(g);
			if (luigi.getHitbox().intersects(b.getHitbox())) {
//				System.out.println("Death to Boo");
				//TODO: Implement game end and restart
			}
		}
		
		for (DryBones d : row3) { 
			d.paint(g);
			if (luigi.getHitbox().intersects(d.getHitbox())) {
//				System.out.println("Death to DryBones");
			}
		}
		
		for (KoopaShell ks : koopaShells) {ks.paint(g);}
		
		int ridingAny = 0;
		for (DonutLift dl : donutLifts_1) {
			if (luigi.getBottomHitbox().intersects(dl.getHitbox())) {//If luigi is on the lift, move luigi as well
				ridingAny++;
				luigi.setRiding(true);
			}
			dl.paint(g);
		}
		
		for (DonutLift dl : donutLifts_2) {
			if (luigi.getBottomHitbox().intersects(dl.getHitbox())) { luigi.setRiding(true); ridingAny++;}
			dl.paint(g);
		}
		
		if (ridingAny==0) {luigi.setRiding(false);}
		
		
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
		f.setResizable(true);
 		f.addMouseListener(this);
		f.addKeyListener(this);
	
//		backgroundMusic.play();

		// Setup any 1D array here! - create the objects that go in them ;) 

		for (int i = 0; i < row1.length; i++) {
			row1[i] = new Boo((int) (i*110), 592, 110);
		}
		
		for (int i = 0; i < row2.length; i++) {
			row2[i] = new Boo((int) (i*100), 500, 100);
		}
		
		for (int l = 0; l < row3.length; l++) {
			row3[l] = new DryBones((int) (l*200), 336 - (32*4), 200);
		}
		
		for (int d = 0; d < donutLifts_1.length; d++) {
			int space = 0;
			if (d>2) {space = 32;}
			if (d>3) {space = 64;}
			if (d>4) {space = 128;}
			if (d>6) {space = 144;}
			if (d>9) {space = 176;}
			if (d>10) {space = 208;}
			donutLifts_1[d] = new DonutLift(d*32+space,336-(32*6));	
		}
		
		for (int d = 0; d < donutLifts_2.length; d++) { // bottom row
			int space = 0;
			if (d>2) {space = 32;}
			if (d>4) {space = 64;}
			if (d>7) {space = 128;}
			if (d>8) {space = 144;}
			if (d>10) {space = 176;}
			if (d>12) {space = 208;}
			donutLifts_2[d] = new DonutLift(d*32+50+space,336-(32*7));
		}

		
		for (int j = 0; j < bgRows[0].length; j++) {
			
			bgRows[18][j] = new StaticTexture((int) (j*32), 336-(32*8), "/imgs/Stone2.png");
			
			bgRows[19][j] = new StaticTexture((int) (j*32), 336-(32*7), "/imgs/Lava.png", true);
			bgRows[20][j] = new StaticTexture((int) (j*32), 336-(32*6), "/imgs/Lava.png", true);
			
			
			bgRows[16][j] = new StaticTexture((int) (j*32), 336-(32*5), "/imgs/Stone2.png");
			
			bgRows[7][j] = new StaticTexture((int) (j*32), 336-(32*4), "/imgs/Stone.png");
			bgRows[8][j] = new StaticTexture((int) (j*32), 336-(32*3), "/imgs/Stone.png");
			
			bgRows[6][j] = new StaticTexture((int) (j*32), 336-64, "/imgs/Stone2.png");
			bgRows[5][j] = new StaticTexture((int) (j*32), 336-32, "/imgs/Stone.png");
			
			bgRows[2][j] = new StaticTexture((int) (j*32), 336, "/imgs/Lava.png", true); // Top Lava Row
			bgRows[1][j] = new StaticTexture((int) (j*32), 368, "/imgs/Lava.png", true); //400 - 32 ( scaled height = height * 2 ) in pixels
			bgRows[0][j] = new StaticTexture((int) (j*32), 400, "/imgs/Lava.png", true); //Bottom Lava Row
			
			bgRows[3][j] = new StaticTexture((int) (j*32), 400+32, "/imgs/Stone.png");
			bgRows[4][j] = new StaticTexture((int) (j*32), 400+64, "/imgs/Stone2.png");
			
			
			bgRows[9][j] = new StaticTexture((int) (j*32), 496, "/imgs/Dark_Grey.png");
			bgRows[10][j] = new StaticTexture((int) (j*32), 496+32, "/imgs/Dark_Grey.png");
			bgRows[11][j] = new StaticTexture((int) (j*32), 496+(32*2), "/imgs/Dark_Grey.png");
			bgRows[12][j] = new StaticTexture((int) (j*32), 496+(32*3), "/imgs/Dark_Grey.png");
			bgRows[13][j] = new StaticTexture((int) (j*32), 592 + 32, "/imgs/Dark_Grey.png");
			
			bgRows[17][j] = new StaticTexture((int) (j*32), 592 + (32*2), "/imgs/Stone2.png");
			
			bgRows[14][j] = new StaticTexture((int) (j*32), 592 + (32*4), "/imgs/Grass.png");
			bgRows[15][j] = new StaticTexture((int) (j*32), 592 + (32*5), "/imgs/Grass.png");
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
