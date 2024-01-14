import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;

public class Player {
  private double x;
  
  private double y;
  
  private double dx;
  
  private double dy;
  
  private int width;
  
  private int height;
  
  private boolean left;
  
  private boolean right;
  
  private boolean up;
  
  private boolean down;
  
  private double moveSpeed;
  
  private TileMap tileMap;
  
  private boolean topLeft;
  
  private boolean topRight;
  
  private boolean bottomLeft;
  
  private boolean bottomRight;
  
  private boolean tL2;
  
  private boolean tR2;
  
  private boolean botLeft2;
  
  private boolean botRight2;
  
  private boolean changed = false;
  
  private BufferedImage leftP;
  
  private BufferedImage leftP2;
  
  private BufferedImage rightP;
  
  private BufferedImage rightP2;
  
  private BufferedImage idlerightP;
  
  private BufferedImage idleleftP;
  
  private BufferedImage idlerightP2;
  
  private BufferedImage idleleftP2;
  
  private int prePosition = 0;
  
  private int spriteNumber = 1;
  
  private int spriteCounter = 0;
  
  private GamePanel gamePanel;
  
  private boolean isMole = false;
  
  public Player(GamePanel gp, TileMap tm) {
    this.tileMap = tm;
    this.width = 50;
    this.height = 65;
    this.moveSpeed = 15.0D;
    this.gamePanel = gp;
    getImage();
  }
  
  public void setx(int i) {
    this.x = i;
  }
  
  public void sety(int i) {
    this.y = i;
  }
  
  public void setLeft(boolean b) {
    this.left = b;
  }
  
  public void setRight(boolean b) {
    this.right = b;
  }
  
  public void setUp(boolean b) {
    this.up = b;
  }
  
  public void setDown(boolean b) {
    this.down = b;
  }
  
  public void update() {
    if (this.left) {
      this.dx = -this.moveSpeed;
      this.dy = 0.0D;
    } else if (this.right) {
      this.dx = this.moveSpeed;
      this.dy = 0.0D;
    } else if (this.up) {
      this.dy = -this.moveSpeed;
      this.dx = 0.0D;
    } else if (this.down) {
      this.dy = this.moveSpeed;
      this.dx = 0.0D;
    } else {
      this.dx = 0.0D;
      this.dy = 0.0D;
    } 
    int currCol = this.tileMap.getColTile((int)this.x);
    int currRow = this.tileMap.getRowTile((int)this.y);
    double tox = this.x + this.dx;
    double toy = this.y + this.dy;
    double tempx = this.x;
    double tempy = this.y;
    calculateCorners(this.x, toy);
    if (this.dy < 0.0D) {
      if (this.tL2 || this.tR2)
        changeMap(); 
      if (this.topLeft || this.topRight) {
        this.dy = 0.0D;
        tempy = (currRow * this.tileMap.getTileSize() + this.height / 2);
      } else {
        tempy += this.dy;
      } 
    } 
    if (this.dy > 0.0D) {
      if (this.botRight2 || this.botLeft2)
        changeMap(); 
      if (this.bottomLeft || this.bottomRight) {
        this.dy = 0.0D;
        tempy = ((currRow + 1) * this.tileMap.getTileSize() - this.height / 2);
      } else {
        tempy += this.dy;
      } 
    } 
    calculateCorners(tox, this.y);
    if (this.dx < 0.0D) {
      if (this.tL2 || this.botLeft2)
        changeMap(); 
      if (this.topLeft || this.bottomLeft) {
        this.dx = 0.0D;
        tempx = (currCol * this.tileMap.getTileSize() + this.width / 2);
      } else {
        tempx += this.dx;
      } 
    } 
    if (this.dx > 0.0D) {
      if (this.tR2 || this.botRight2)
        changeMap(); 
      if (this.topRight || this.bottomRight) {
        this.dx = 0.0D;
        tempx = ((currCol + 1) * this.tileMap.getTileSize() - this.width / 2);
      } else {
        tempx += this.dx;
      } 
    } 
    this.x = tempx;
    this.y = tempy;
    this.tileMap.setx((int)(640.0D - this.x));
    this.tileMap.sety((int)(360.0D - this.y));
    this.spriteCounter++;
    if (this.spriteCounter > 3) {
      if (this.spriteNumber == 1) {
        this.spriteNumber = 2;
      } else if (this.spriteNumber == 2) {
        this.spriteNumber = 1;
      } 
      this.spriteCounter = 0;
    } 
  }
  
  private void calculateCorners(double x, double y) {
    int leftTile = this.tileMap.getColTile((int)(x - (this.width / 2)));
    int rightTile = this.tileMap.getColTile((int)(x + (this.width / 2)) - 1);
    int topTile = this.tileMap.getColTile((int)(y - (this.height / 2)));
    int bottomTile = this.tileMap.getColTile((int)(y + (this.height / 2)) - 1);
    this.topLeft = !(this.tileMap.getTile(topTile, leftTile) != 0 && this.tileMap.getTile(topTile, leftTile) != 4 && this.tileMap.getTile(topTile, leftTile) != 5);
    this.topRight = !(this.tileMap.getTile(topTile, rightTile) != 0 && this.tileMap.getTile(topTile, rightTile) != 4 && this.tileMap.getTile(topTile, rightTile) != 5);
    this.bottomLeft = !(this.tileMap.getTile(bottomTile, leftTile) != 0 && this.tileMap.getTile(bottomTile, leftTile) != 4 && this.tileMap.getTile(bottomTile, leftTile) != 5);
    this.bottomRight = !(this.tileMap.getTile(bottomTile, rightTile) != 0 && this.tileMap.getTile(bottomTile, rightTile) != 4 && this.tileMap.getTile(bottomTile, rightTile) != 5);
    this.tL2 = (this.tileMap.getTile(topTile, leftTile) == 2);
    this.tR2 = (this.tileMap.getTile(topTile, rightTile) == 2);
    this.botLeft2 = (this.tileMap.getTile(bottomTile, leftTile) == 2);
    this.botRight2 = (this.tileMap.getTile(bottomTile, rightTile) == 2);
  }
  
  public void getImage() {
    try {
      this.leftP = ImageIO.read(getClass().getResourceAsStream("player/left.png"));
      this.rightP = ImageIO.read(getClass().getResourceAsStream("player/right.png"));
      this.idlerightP = ImageIO.read(getClass().getResourceAsStream("player/idleright.png"));
      this.idleleftP = ImageIO.read(getClass().getResourceAsStream("player/idleleft.png"));
      this.leftP2 = ImageIO.read(getClass().getResourceAsStream("player/left2.png"));
      this.rightP2 = ImageIO.read(getClass().getResourceAsStream("player/right2.png"));
      this.idlerightP2 = ImageIO.read(getClass().getResourceAsStream("player/idleright2.png"));
      this.idleleftP2 = ImageIO.read(getClass().getResourceAsStream("player/idleleft2.png"));
    } catch (Exception e) {
      e.printStackTrace();
    } 
  }
  
  public void draw(Graphics2D g) {
    int tx = this.tileMap.getx();
    int ty = this.tileMap.gety();
    BufferedImage image = this.idleleftP;
    if (!this.isMole) {
      if (this.left) {
        this.prePosition = 0;
        if (this.spriteNumber == 1)
          image = this.leftP; 
        if (this.spriteNumber == 2)
          image = this.leftP2; 
      } else if (this.right) {
        this.prePosition = 1;
        if (this.spriteNumber == 1)
          image = this.rightP; 
        if (this.spriteNumber == 2)
          image = this.rightP2; 
      } else if (this.up || this.down) {
        if (this.spriteNumber == 1)
          if (this.prePosition == 0) {
            image = this.leftP;
          } else if (this.prePosition == 1) {
            image = this.rightP;
          }  
        if (this.spriteNumber == 2)
          if (this.prePosition == 0) {
            image = this.leftP2;
          } else if (this.prePosition == 1) {
            image = this.rightP2;
          }  
      } else {
        if (this.spriteNumber == 1)
          if (this.prePosition == 0) {
            image = this.idleleftP;
          } else if (this.prePosition == 1) {
            image = this.idlerightP;
          }  
        if (this.spriteNumber == 2)
          if (this.prePosition == 0) {
            image = this.idleleftP2;
          } else if (this.prePosition == 1) {
            image = this.idlerightP2;
          }  
      } 
      g.drawImage(
          image, 
          (int)(tx + this.x - (this.width / 2)), 
          (int)(ty + this.y - (this.height / 2)), 
          this.width, this.height, 
          null);
    } 
  }
  
  public void changeMap() {
    if (this.changed)
      return; 
    this.changed = true;
    System.out.println("Map changed");
    this.isMole = true;
    this.gamePanel.setTileMap();
  }
}
