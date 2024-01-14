import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import javax.swing.JPanel;

public class GamePanel extends JPanel implements Runnable, KeyListener, MouseListener {
  public static final int WIDTH = 1920;
  
  public static final int HEIGHT = 1080;
  
  private Thread thread;
  
  private static boolean running = false;
  
  private static boolean gameIsMole = false;
  
  private BufferedImage image;
  
  private BufferedImage startScreen;
  
  private BufferedImage fullHeart;
  
  private BufferedImage emptyHeart;
  
  private BufferedImage instructionsMole;
  
  private BufferedImage instructionsMaze;
  
  private BufferedImage genInstructions;
  
  private BufferedImage winScreen;
  
  private BufferedImage loseScreen;
  
  private Graphics2D g;
  
  private int FPS = 30;
  
  private int targetTime = 1000 / this.FPS;
  
  private static TileMap tileMap;
  
  private static Player player;
  
  private boolean gameOver = false;
  
  private int numMoles = 10;
  
  private long popUpTime = 0L;
  
  private int moleLives = 4;
  
  private int randRow = 0;
  
  private int randCol = 0;
  
  private int row = 0;
  
  private int column = 0;
  
  private int counter = 0;
  
  private boolean hitThisRound = false;
  
  private int[] lastRands = new int[2];
  
  private boolean hit = false;
  
  private long[][] hitTime = new long[12][20];
  
  private int stayUpTime = 1500;
  
  private int moleTileSize = 100;
  
  private int mapCount = -1;
  
  public GamePanel() {
    setPreferredSize(new Dimension(1920, 1080));
    setFocusable(true);
    requestFocus();
  }
  
  public void addNotify() {
    super.addNotify();
    if (this.thread == null) {
      this.thread = new Thread(this);
      this.thread.start();
    } 
    addKeyListener(this);
    addMouseListener(this);
  }
  
  public void getImage() {
    try {
      this.image = ImageIO.read(getClass().getResourceAsStream("maps/startscreen.png"));
      this.startScreen = ImageIO.read(getClass().getResourceAsStream("maps/startscreen.png"));
      this.fullHeart = ImageIO.read(getClass().getResourceAsStream("player/heart_whole.png"));
      this.emptyHeart = ImageIO.read(getClass().getResourceAsStream("player/heart_empty.png"));
      this.instructionsMaze = ImageIO.read(getClass().getResourceAsStream("maps/instructionsmaze.png"));
      this.instructionsMole = ImageIO.read(getClass().getResourceAsStream("maps/instructionsmole.png"));
      this.genInstructions = ImageIO.read(getClass().getResourceAsStream("maps/geninstructions.png"));
      this.winScreen = ImageIO.read(getClass().getResourceAsStream("maps/winscreen.png"));
      this.loseScreen = ImageIO.read(getClass().getResourceAsStream("maps/losescreen.png"));
    } catch (Exception e) {
      e.printStackTrace();
    } 
  }
  
  public void run() {
    getImage();
    init();
    while (running) {
      long startTime = System.nanoTime();
      if (this.mapCount != -1) {
        update();
        render();
      } 
      draw();
      if (this.mapCount == -2)
        continue; 
      long urdTime = (System.nanoTime() - startTime) / 1000000L;
      long waitTime = this.targetTime - urdTime;
      try {
        Thread.sleep(waitTime);
      } catch (Exception e) {
        System.out.println("sleep failed");
      } 
      if (gameIsMole)
        if (this.hitTime[this.row][this.column] != 0L && System.currentTimeMillis() - this.hitTime[this.row][this.column] > 2000L) {
          tileMap.setMap(this.row, this.column, 2);
          this.hitTime[this.row][this.column] = 0L;
        }  
      if (this.hit) {
        molePopDown(this.row, this.column);
        continue;
      } 
      if (!this.hit && this.popUpTime > 0L && System.currentTimeMillis() - this.popUpTime > this.stayUpTime)
        molePopDown(this.row, this.column); 
    } 
  }
  
  public void molePopUp() {
    if (this.mapCount == -2)
      return; 
    this.hit = false;
    do {
      this.randRow = (int)(Math.random() * 5.0D + 1.0D);
      this.randCol = (int)(Math.random() * 9.0D + 1.0D);
    } while (this.randRow == this.lastRands[0] && this.randCol == this.lastRands[1]);
    this.lastRands[0] = this.randRow;
    this.lastRands[1] = this.randCol;
    this.row = this.randRow * 2 - 1;
    this.column = this.randCol * 2 - 1;
    tileMap.setMap(this.row, this.column, 2);
    this.popUpTime = System.currentTimeMillis();
    this.counter++;
  }
  
  public void molePopDown(int row, int col) {
    if (this.hit) {
      this.hitTime[row][col] = System.currentTimeMillis();
      hit();
      return;
    } 
    if (!this.hit) {
      tileMap.setMap(row, col, 1);
      if (!this.hitThisRound)
        this.moleLives--; 
    } 
    if (this.moleLives == 0 && !this.gameOver) {
      winLose(false);
      return;
    } 
    this.hitThisRound = false;
    this.hit = false;
    if (this.counter == this.numMoles - 1) {
      System.out.println("counter++");
      this.popUpTime = 0L;
      gameIsMole = false;
      if (this.moleLives > 0 && this.mapCount < 4) {
        this.numMoles = 0;
        changeMap();
        return;
      } 
      if (!this.gameOver)
        winLose(true); 
    } 
    molePopUp();
  }
  
  public void winLose(boolean win) {
    gameIsMole = false;
    this.gameOver = true;
    if (win) {
      this.mapCount = 10;
    } else {
      this.mapCount = 11;
    } 
  }
  
  private void init() {
    running = true;
    this.image = new BufferedImage(1920, 1080, 1);
    this.g = (Graphics2D)this.image.getGraphics();
    tileMap = new TileMap("res/maps/map0.txt", this.moleTileSize);
    player = new Player(this, tileMap);
  }
  
  private void initMole() {
    running = true;
    this.image = new BufferedImage(1920, 1080, 1);
    this.g = (Graphics2D)this.image.getGraphics();
    tileMap = new TileMap("res/maps/molemap.txt", this.moleTileSize);
    molePopUp();
    gameIsMole = true;
  }
  
  private void hit() {
    int currentLives = this.moleLives;
    this.hitThisRound = true;
    tileMap.setMap(this.row, this.column, 3);
    this.popUpTime = System.currentTimeMillis() - 500L;
    this.hit = false;
    this.moleLives++;
    if (this.moleLives == currentLives + 1)
      this.moleLives = currentLives; 
  }
  
  private void changeMap() {
    gameIsMole = false;
    this.mapCount++;
    tileMap = new TileMap("res/maps/map" + this.mapCount + ".txt", this.moleTileSize);
    tileMap = new TileMap("res/maps/map" + this.mapCount + ".txt", this.moleTileSize);
    player = new Player(this, tileMap);
    player.setx(1450);
    player.sety(1200);
    this.moleLives = 3;
    this.counter = 0;
    levels(this.mapCount);
  }
  
  private void levels(int level) {
    if (level == 0) {
      this.stayUpTime = 1800;
      this.numMoles = 8;
    } else if (level == 1) {
      this.stayUpTime = 1600;
      this.numMoles = 10;
      player = new Player(this, tileMap);
      player.setx(1750);
      player.sety(3080);
    } else if (level == 2) {
      this.stayUpTime = 1400;
      this.numMoles = 12;
      player = new Player(this, tileMap);
      player.setx(1250);
      player.sety(3150);
    } else if (level == 3) {
      this.stayUpTime = 1100;
      this.numMoles = 14;
      player = new Player(this, tileMap);
      player.setx(3000);
      player.sety(2200);
    } else {
      this.stayUpTime = 950;
      this.numMoles = 16;
      player = new Player(this, tileMap);
      player.setx(1420);
      player.sety(900);
    } 
  }
  
  public void setTileMap() {
    initMole();
  }
  
  private void update() {
    tileMap.update(this.row, this.column, this.mapCount);
    player.update();
  }
  
  private void render() {
    tileMap.draw(this.g);
    player.draw(this.g);
  }
  
  private void draw() {
    if (this.mapCount == -1) {
      this.g.drawImage(this.startScreen, 0, 0, null);
    } else if (this.mapCount == -3) {
      this.g.drawImage(this.instructionsMole, 0, 0, null);
    } else if (this.mapCount == -4) {
      this.g.drawImage(this.instructionsMaze, 0, 0, null);
    } else if (this.mapCount == -5) {
      this.g.drawImage(this.genInstructions, 0, 0, null);
    } else if (this.mapCount == 10) {
      this.g.drawImage(this.winScreen, 0, 0, null);
    } else if (this.mapCount == 11) {
      this.g.drawImage(this.loseScreen, 0, 0, null);
    } 
    if (gameIsMole) {
      int i;
      for (i = 0; i < this.moleLives; i++)
        this.g.drawImage(this.fullHeart, i * 55 + 1720, 30, null); 
      for (i = 0; i < 3 - this.moleLives; i++)
        this.g.drawImage(this.emptyHeart, (2 - i) * 55 + 1720, 30, null); 
    } 
    Graphics g2 = getGraphics();
    g2.drawImage(this.image, 0, 0, null);
    g2.dispose();
  }
  
  public void instructions(int page) {
    if (page == -1) {
      this.mapCount = -5;
    } else if (page == -5) {
      this.mapCount = -4;
    } else {
      this.mapCount = -3;
    } 
  }
  
  public void keyTyped(KeyEvent key) {}
  
  public void keyPressed(KeyEvent key) {
    int code = key.getKeyCode();
    if (code == 37)
      player.setLeft(true); 
    if (code == 39)
      player.setRight(true); 
    if (code == 38)
      player.setUp(true); 
    if (code == 40)
      player.setDown(true); 
  }
  
  public void keyReleased(KeyEvent key) {
    int code = key.getKeyCode();
    if (code == 37)
      player.setLeft(false); 
    if (code == 39)
      player.setRight(false); 
    if (code == 38)
      player.setUp(false); 
    if (code == 40)
      player.setDown(false); 
  }
  
  public void mousePressed(MouseEvent e) {}
  
  public void mouseClicked(MouseEvent e) {
    if (this.popUpTime == 0L && this.mapCount != -1 && this.mapCount != -4 && this.mapCount != -3 && this.mapCount != -5)
      return; 
    if (e.getY() > this.row * this.moleTileSize && e.getY() < (this.row + 1) * this.moleTileSize && 
      e.getX() < (this.column + 1) * this.moleTileSize && e.getX() > this.column * this.moleTileSize)
      this.hit = true; 
    if (this.mapCount == -1) {
      if (e.getY() > 520 && e.getY() < 658 && e.getX() > 698 && e.getX() < 1203) {
        changeMap();
      } else if (e.getY() > 738 && e.getY() < 875 && e.getX() > 505 && e.getX() < 1414) {
        instructions(this.mapCount);
      } 
    } else if (this.mapCount == -5 && e.getY() > 847 && e.getY() < 950 && e.getX() > 1372 && e.getX() < 1741) {
      instructions(this.mapCount);
    } else if (this.mapCount == -4 && e.getY() > 847 && e.getY() < 950 && e.getX() > 1372 && e.getX() < 1741) {
      instructions(this.mapCount);
    } else if (this.mapCount == -3 && e.getY() > 847 && e.getY() < 950 && e.getX() > 1372 && e.getX() < 1741) {
      this.mapCount = -1;
      changeMap();
    } else if (this.mapCount == 10 || (this.mapCount == 11 && e.getY() > 800 && e.getY() < 938 && e.getX() > 937 && e.getX() < 1442)) {
      System.exit(0);
    } 
  }
  
  public void mouseEntered(MouseEvent e) {}
  
  public void mouseExited(MouseEvent e) {}
  
  public void mouseReleased(MouseEvent e) {}
}
