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
import java.util.ArrayList;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.Timer;

public class Frame extends JPanel implements ActionListener, MouseListener, KeyListener {
	
	//for any debugging code we add
	public static boolean debugging = false;
	public static int width = 600;
	public static int height = 800;
	boolean gameOver = false;
	
	int totalGames = 1;
	int totalAttempts = 1;
	
	String deathMsg = "";

	boolean canLock = false; // KoopaShell Logic
	boolean locked = false; // KoopaShell Logic
	public int ridingAny;
	
	
	/*
	 * For anyone playing this game, note that basic Luigi control is WASD, and that
	 * when trying to mount a koopa shell, you need to press Spacebar to do so.
	 */
	
	Font myFont = new Font("Courier", Font.BOLD, 30);
	Font otherFont = new Font("Courier", Font.BOLD, 20);
	SimpleAudioPlayer backgroundMusic = new SimpleAudioPlayer("other.wav", true);
	SimpleAudioPlayer winMusic = new SimpleAudioPlayer("complete.wav", false);
//	Music soundBang = new Music("bang.wav", false);
//	Music soundHaha = new Music("haha.wav", false);
	
	
	Luigi luigi = new Luigi();
	
	Boo[] row1 = new Boo[8]; // For scrolling, make sure you have JUST ENOUGH boos so only 1 is offscreen
	Boo[] row2 = new Boo[10]; 	//A row of boos (moving)
	DryBones[] row3 = new DryBones[5]; //3rd row of scrolling objects
		
	ArrayList<DonutLift> newLifts_1 = new ArrayList<DonutLift>(); // Horizontally moving donut lifts
	ArrayList<DonutLift> newLifts_2 = new ArrayList<DonutLift>();
	ArrayList<LifeImage> lives = new ArrayList<LifeImage>();
	
	StaticTexture[][] bgRows = new StaticTexture[25][19]; 	//All background textures
	
	KoopaShell[] koopaShells = new KoopaShell[1];
	Door[] enter = new Door[19];
	
	Door[] exit = new Door[19];
	

	public void paint(Graphics g) {
		super.paintComponent(g);
		for (int i = 0; i < bgRows.length; i++) {
			for (StaticTexture l : bgRows[i]) {  l.paint(g); }
		} // Paints Background Textures NEEDS TO BE FIRST
		
		ridingAny = 0;
		
		
		newLifts_1.forEach((dl)->{
			if (luigi.getBottomHitbox().intersects(dl.getHitbox())) {//If luigi is on the lift, move luigi as well
				luigi.setRiding(true);
				ridingAny++;
				dl.setTimer(true);
			}else {dl.setTimer(false);}
			dl.paint(g);
		});

		
		newLifts_2.forEach((dl)->{
			if (luigi.getBottomHitbox().intersects(dl.getHitbox())) { 
				luigi.setRiding(true); 
				ridingAny++; 
				dl.setTimer(true);
			}else {dl.setTimer(false);}
			dl.paint(g);
		});
		
		
		if (ridingAny==0) {luigi.setRiding(false); }
		
		//drawing the life counter images
		for (LifeImage obj: lives) {
			//drawing the LifeImage objects
			obj.paint(g);
		}
		
		
		for (int i = 0; i < bgRows.length; i++) {
			for (StaticTexture l : bgRows[i]) { 
				
				if (i == 0) { // Logic that only applies to the bottom lava row
					if (( luigi.getHitbox().intersects(l.getHitbox()) && luigi.getHitbox().intersects(koopaShells[0].getHitbox())) && !luigi.getBottomHitbox().intersects(l.getHitbox())) {
						if (!locked) { canLock = true; }
					}
				}
				
				if (l.isDangerous()) { //Only for "dangerous" textures (lava)
					if (luigi.getBottomHitbox().intersects(l.getHitbox()) && !locked && !luigi.isRiding()) {
						gameOver = true; deathMsg = "Death To Lava";
					}
				}				
			}
		} //Paint background first
		
		//paint the other objects on the screen
		for (Boo b : row1) { 
			b.paint(g);
			if (luigi.getHitbox().intersects(b.getHitbox())) {
				gameOver = true; deathMsg = "Death To Boo";
			}
		}
		
		for (Boo b : row2) { 
			b.paint(g);
			if (luigi.getHitbox().intersects(b.getHitbox())) {
				gameOver = true; deathMsg = "Death To Boo";
			}
		}
		
		for (DryBones d : row3) { 
			d.paint(g);
			if (luigi.getHitbox().intersects(d.getHitbox())) {
				gameOver = true; deathMsg = "Death To DryBones";
				//Sets the game state and death messages
			}
		}
		
		for (Door dr : enter) { 
			if (luigi.getHitbox().intersects(dr.getHitbox())) { dr.setDoorOpened(true);}
			else {dr.setDoorOpened(false);}
			dr.paint(g);
		}
		
		for (Door dr : exit) { 
			if (luigi.getHitbox().intersects(dr.getHitbox())) { dr.setDoorOpened(true);}
			else {dr.setDoorOpened(false);}
			dr.paint(g);
		}
		
		
		for (KoopaShell ks : koopaShells) {ks.paint(g);}
		
		
		if (locked) {
			luigi.setX(koopaShells[0].getX() + 2); // Luigi width is 14 while other textures are 16; 16-14 = 2
			luigi.setY(koopaShells[0].getY() - 25); // Arbitrary number that makes Luigi Look good on the shell
		}
		
		luigi.paint(g);
		
		
		if (gameOver && !luigi.isCompleted()) {
			if (this.lives.size()>0) {
				this.reset(); 
				totalAttempts+=1;
				this.lives.remove(this.lives.size()-1);
			}else {
				this.gameOver(deathMsg, g);
			}
		}
		if (luigi.isCompleted()) {this.completed(g);}
		//Decides weather the game is completed or if the game over screen should show
	}
	
	public void gameOver(String deathText, Graphics g) {
		g.setFont(myFont);
		//Renders the 'game over' screen
		g.setColor(new Color(0, 0, 0, 100));
		g.fillRect(0, 0, width, height);
		g.setColor(new Color(255, 255, 255));
		g.fillRect(width/4, height/4, width/2, 300);
		g.setColor(new Color (0, 0, 0));
		if (deathText.equals("Death To DryBones")){ g.drawString(deathText, width/4 , height/4+50);}
		else {g.drawString(deathText, width/4 + 40 , height/4+50);}
		g.setFont(otherFont);
		g.drawString("Press 'p' to play again", width/4 + 15 , height/4+150);
	}
	
	public void completed(Graphics g) {
		winMusic.play();
		g.setFont(myFont);
		//Renders the 'completed' screen
		g.setColor(new Color(0, 0, 0, 100));
		g.fillRect(0, 0, width, height);
		g.setColor(new Color (0, 60, 0));
		g.fillRect(width/4, height/4, width/2, 300);
		g.setColor(new Color(255, 255, 255));
		
		g.drawString("Completed Stage!", width/4+5 , height/4+50);
		g.setFont(otherFont);
		g.drawString("Press 'p' to play again", width/4 + 15 , height/4+150);
		
		g.drawString("Total Attempts: " + totalAttempts, width/4 + 15 , height/4+200);
		g.drawString("Total Games: " + totalGames, width/4 + 15 , height/4+250);
	}
	
	public void reset() {
		winMusic = new SimpleAudioPlayer("complete.wav", false);
		gameOver = false;
		luigi = new Luigi();
		koopaShells[0] = new KoopaShell(400, 336);
		
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
		
		for (int dr = 0; dr < enter.length; dr++) {
			enter[dr] = new Door((int) (32*dr), 592 + (32*2));
		}
		
		for (int dr = 0; dr < exit.length; dr++) {
			exit[dr] = new Door((int) (32*dr), 336-(32*9));
		}
		
		
		for (int d = 0; d < 10; d++) { //10 is the length of the lifts
			int space = 0;
			if (d>1) {space = 32*2;}
			if (d>4) {space = 32*5;}
			if (d>5) {space = 32*7;} //space is the space to the RIGHT of the lift
			if (d>7) {space = 32*8;}
			if (d>8) {space = 32*10;}
			this.newLifts_1.add(new DonutLift(d*32+space,336-(32*6)));	
		}
		
		//Adds the spacing for the donut lifts
		for (int d = 0; d < 10; d++) { // bottom row
			int space = 0;
			if (d>1) {space = 32;}
			if (d>3) {space = 32*5;}
			if (d>4) {space = 32*7;} 
			if (d>6) {space = 32*8;}
			if (d>7) {space = 32*10;}
			this.newLifts_2.add(new DonutLift(d*32+40+space,336-(32*7)));
		}

		
		for (int j = 0; j < bgRows[0].length; j++) {
			
			bgRows[24][j] = new StaticTexture((int) (j*32), 336-(32*11), "/imgs/Stone3.png");
			bgRows[23][j] = new StaticTexture((int) (j*32), 336-(32*10), "/imgs/Stone3.png");
			bgRows[22][j] = new StaticTexture((int) (j*32), 336-(32*9), "/imgs/Stone3.png");
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

			
			bgRows[21][j] = new StaticTexture((int) (j*32), 592 + (32*2), "/imgs/Stone3.png");
			bgRows[17][j] = new StaticTexture((int) (j*32), 592 + (32*3), "/imgs/Stone3.png");
			bgRows[14][j] = new StaticTexture((int) (j*32), 592 + (32*4), "/imgs/Stone3.png");
			bgRows[15][j] = new StaticTexture((int) (j*32), 592 + (32*5), "/imgs/Stone3.png");
		}
		
		
		for (int k = 0; k < koopaShells.length; k++) {
			koopaShells[k] = new KoopaShell(400, 336);
		}
		
		
		for (int l = 0; l < 3; l++) { this.lives.add(new LifeImage(l*40, 10));}
		
		
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
		

		switch (arg0.getKeyCode()) {
			case (87): // W
				if (locked || gameOver) {break;}
				luigi.setVY(-3);
				break;
			case(65): // A
				if (locked || gameOver) {break;}
				luigi.setVX(-3);
				break;
			case(83): // S
				if (locked || gameOver) {break;}
				luigi.setVY(3);
				break;
			case(68): // D
				if (locked || gameOver) {break;}
				luigi.setVX(3);				
				break;
			case (32):
				if (gameOver) {break;}
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
					luigi.setY(luigi.getY() - 30);
					break;
				}
				
				break;
			case (80): //P
				if (gameOver || luigi.isCompleted()) {
					totalGames+=1;
					if (luigi.isCompleted()) {
						totalAttempts = 1;
						totalGames = 1;
					}
					this.reset();
					this.lives.clear();
					for (int l = 0; l < 3; l++) { this.lives.add(new LifeImage(l*40, 10));}
					break;
				}
		}
		
	}

	@Override
	public void keyReleased(KeyEvent arg0) {
		// TODO Auto-generated method stub
		switch (arg0.getKeyCode()) {
		case (87):
			luigi.setVY(0);
			break;
		case(65):
			luigi.setVX(0);
			break;
		case(83):
			luigi.setVY(0);
			break;
		case(68):
			luigi.setVX(0);
			break;
		}
	}

	@Override
	public void keyTyped(KeyEvent arg0) {
		// TODO Auto-generated method stub
		
	}

}
