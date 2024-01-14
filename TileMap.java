import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import javax.imageio.ImageIO;

public class TileMap {
  private int x;
  
  private int y;
  
  private int tileSize;
  
  private int[][] map;
  
  private int mapWidth;
  
  private int mapHeight;
  
  private BufferedImage dirt;
  
  private BufferedImage tree;
  
  private BufferedImage wood;
  
  private BufferedImage stone;
  
  private BufferedImage grass;
  
  private BufferedImage moleHit;
  
  private BufferedImage water;
  
  private BufferedImage[] moles = new BufferedImage[5];
  
  private int loopCount = -1;
  
  private int displayCount = 0;
  
  private int preRow = 0;
  
  private int preCol = 0;
  
  public TileMap(String s, int tileSize) {
    this.tileSize = tileSize;
    System.out.println(s);
    URL filename = TileMap.class.getResource(s);
    System.out.println(filename);
    try {
      BufferedReader urlReader = new BufferedReader(new InputStreamReader(filename.openStream()));
      this.mapWidth = Integer.parseInt(urlReader.readLine());
      this.mapHeight = Integer.parseInt(urlReader.readLine());
      this.map = new int[this.mapHeight][this.mapWidth];
      String delimiters = " ";
      for (int row = 0; row < this.mapHeight; row++) {
        String line = urlReader.readLine();
        String[] tokens = line.split(delimiters);
        for (int j = 0; j < tokens.length; j++);
        for (int col = 0; col < this.mapWidth; col++)
          this.map[row][col] = Integer.parseInt(tokens[col]); 
      } 
    } catch (Exception e) {
      e.printStackTrace();
    } 
    getImage();
    for (int i = 0; i < this.mapHeight; i++) {
      for (int j = 0; j < this.mapWidth; j++)
        System.out.println(String.valueOf(this.map[i][j]) + " "); 
      System.out.println();
    } 
  }
  
  public int getx() {
    return this.x;
  }
  
  public int gety() {
    return this.y;
  }
  
  public int getColTile(int x) {
    return x / this.tileSize;
  }
  
  public int getRowTile(int y) {
    return y / this.tileSize;
  }
  
  public int getTile(int row, int col) {
    return this.map[row][col];
  }
  
  public int getTileSize() {
    return this.tileSize;
  }
  
  public void setx(int i) {
    this.x = i;
  }
  
  public void sety(int i) {
    this.y = i;
  }
  
  public void setMap(int row, int col, int colour) {
    this.map[row][col] = colour;
  }
  
  public void update(int row, int col, int mapCount) {
    if (mapCount == -2)
      return; 
    if (row != this.preRow || col != this.preCol)
      this.displayCount = 0; 
    if (row != 0) {
      this.loopCount++;
      if (this.loopCount == 3) {
        setMap(row, col, 9);
        if (this.displayCount != 4)
          this.displayCount++; 
        this.loopCount = 0;
      } 
    } 
    this.preRow = row;
    this.preCol = col;
  }
  
  public void getImage() {
    try {
      this.tree = ImageIO.read(getClass().getResourceAsStream("tiles/tree.png"));
      this.dirt = ImageIO.read(getClass().getResourceAsStream("tiles/dirt.png"));
      this.wood = ImageIO.read(getClass().getResourceAsStream("tiles/mole.png"));
      this.stone = ImageIO.read(getClass().getResourceAsStream("tiles/stone.png"));
      this.grass = ImageIO.read(getClass().getResourceAsStream("tiles/grass.png"));
      this.moleHit = ImageIO.read(getClass().getResourceAsStream("tiles/molehit.png"));
      this.water = ImageIO.read(getClass().getResourceAsStream("tiles/water.png"));
      for (int i = 0; i < 4; i++) {
        String a = "tiles/moles" + (i + 1) + ".png";
        this.moles[i] = ImageIO.read(getClass().getResourceAsStream(a));
      } 
    } catch (Exception e) {
      e.printStackTrace();
    } 
  }
  
  public void draw(Graphics2D g) {
    BufferedImage image = null;
    for (int row = 0; row < this.mapHeight; row++) {
      for (int col = 0; col < this.mapWidth; col++) {
        int rc = this.map[row][col];
        if (rc == 0)
          image = this.tree; 
        if (rc == 1)
          image = this.grass; 
        if (rc == 2)
          image = this.wood; 
        if (rc == 3)
          image = this.moleHit; 
        if (rc == 4)
          image = this.stone; 
        if (rc == 5)
          image = this.water; 
        if (rc == 9)
          image = this.moles[this.displayCount]; 
        g.drawImage(
            image, 
            this.x + col * this.tileSize, 
            this.y + row * this.tileSize, 
            this.tileSize, this.tileSize, 
            null);
      } 
    } 
  }
}
