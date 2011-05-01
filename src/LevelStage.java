import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.graphics.Mesh;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.VertexAttribute;
import com.badlogic.gdx.graphics.VertexAttributes.Usage;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.BitmapFont.HAlignment;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.BoundingBox;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.World;

public class LevelStage
{
  OrthographicCamera camera = new OrthographicCamera(10, 10);
  OrthographicCamera pancamera = new OrthographicCamera(10, 10);
  BoundingBox bounds;
  World w = new World(new Vector2(0, -9.81f), true);
  Body fixed;
  Mesh backg;
  Player player;
  Sister weight;
  
  Box2DDebugRenderer debugrend;
  
  Map<Body, WithPosition> bodyToThing = new HashMap<Body, WithPosition> ();

  List<WorldTile> worldtiles = new ArrayList<WorldTile>();
  List<CloudTile> cloudtiles = new ArrayList<CloudTile>();
  List<WithPhysicsBody> otheractors = new ArrayList<WithPhysicsBody>();

  float stepAccum = 0f;
  static final float wantFrames = 30.0f; /* physics fps */
  static final float wantStep = 1.0f / wantFrames;
  static final int maxSteps = 10;
  
  Vector3 v3tmp = new Vector3();
  boolean stopped = false;
  boolean retry = false;
  String message = null;
  int inputblocked;
  
  BitmapFont font;
  private String[] dialogue;
  
  LevelStage()
  {
    this.debugrend = new Box2DDebugRenderer();
    
    BodyDef def = new BodyDef();
    def.position.set(0, 0);
    this.fixed = w.createBody(def);

    this.player = new Player(this);
    this.weight = new Sister(this);
    
    this.bounds = new BoundingBox();
    this.bounds.clr();
    this.bounds.ext(0, 0, 0);
    this.bounds.ext(18, 18 * (3.0f / 4.0f), 0);
    
    font = new BitmapFont(Gdx.files.internal("gfx/thing.fnt"), false);
    font.setScale(0.013f);
    
    this.makeBack();
  }
  
  void dispose()
  {
    this.w.dispose();
  }
  
  void youdied()
  {
    this.stop(true, Consts.youDiedDialogue);
  }
  
  void stop(boolean again, String message)
  {
    if (this.stopped)
      return;
    if (again)
      GameSound.fallout();
    else
      GameSound.win();
    this.message = message;
    this.stopped = true;
    this.retry = again;
    GoAloneListener.instance.input.blockinput();
  }
  
  void sisterdied()
  {
    this.stop(true, Consts.sisterDiedDialogue);
  }
  
  void WHAATTT()
  {
    this.stop(true, null);
    this.message = Consts.motherDiedDialogue;
  }
  
  void win()
  {
    this.stop(false, Consts.winDialogue);
  }
  
  void next()
  {
    if (this.retry)
      GoAloneListener.instance.restart();
    else if (this.stopped)
      GoAloneListener.instance.next();
    else
      this.nextdialogue();
  }
  
  void makeBack()
  {
    BoundingBox b = this.bounds;
    
    this.backg = new Mesh(true, 4, 6,
        new VertexAttribute(Usage.Position, 3, "a_position"),
        new VertexAttribute(Usage.ColorPacked, 4, "a_color")
        );
    this.backg.setVertices(
        new float[] {
            b.min.x, b.min.y, 0, Color.toFloatBits(0xBE, 0xCE, 0xDC, 0xFF),
            b.max.x, b.min.y, 0, Color.toFloatBits(0xBE, 0xCE, 0xDC, 0xFF),
            b.max.x, b.max.y, 0, Color.toFloatBits(0x66, 0x99, 0xCC, 0xFF),
            b.min.x, b.max.y, 0, Color.toFloatBits(0x66, 0x99, 0xCC, 0xFF),
        }
        );
    this.backg.setIndices(new short[] { 0, 1, 2, 0, 2, 3 });
  }

  public void resize(int x, int y)
  {
    Vector3 dims = this.bounds.getDimensions();
    Vector3 cent = this.bounds.getCenter();
    
    float aspect = (float) x / (float) y;
    float wantAspect = dims.x / dims.y;
    
    if (aspect < wantAspect)
    {
      this.camera.viewportWidth = dims.x;
      this.camera.viewportHeight = this.camera.viewportWidth / aspect;
    } else {
      this.camera.viewportHeight = dims.y;
      this.camera.viewportWidth = this.camera.viewportHeight * aspect;
    }
    
    System.out.println("setRatio dims " + dims + " center " + cent);
    
    this.camera.position.x = cent.x;
    this.camera.position.y = cent.y;
    
    this.camera.update();

    this.pancamera.viewportHeight = this.camera.viewportHeight;
    this.pancamera.viewportWidth = this.camera.viewportWidth;
    this.pancamera.position.set(this.camera.position);
    this.pancamera.update();
  }
  
  public void pan()
  {
    Vector3 dims = this.bounds.getDimensions();
    v3tmp.set(this.player.position.x, this.player.position.y, 0);
    if (v3tmp.y < dims.y / 2)
      v3tmp.y = dims.y / 2;
    float len = v3tmp.tmp().sub(this.pancamera.position).len();
    len *= len;
    len *= 0.01f;
    if (len > 1)
      len = 0.8f;
    this.pancamera.position.lerp(v3tmp, len);
    this.pancamera.update();

    for (CloudTile ct : this.cloudtiles)
      ct.move(this.pancamera.position.x, this.pancamera.position.y);
  }

  public void background()
  {
    this.camera.apply(Gdx.gl10);
    Gdx.gl.glClear(GL10.GL_COLOR_BUFFER_BIT);
    this.backg.render(GL10.GL_TRIANGLES, 0, 6);
  }

  public void draw(SpriteBatch batch)
  {
    this.pancamera.apply(Gdx.gl10);
    
    for (WorldTile t : this.cloudtiles)
    {
      t.draw(batch);
    }
    
    for (WorldTile t : this.worldtiles)
    {
      if (!t.top)
        t.draw(batch);
    }

    for (WithPosition wp : this.otheractors)
    {
      wp.draw(batch);
    }

    this.weight.draw(batch);
    this.player.draw(batch);
    
    for (WorldTile t : this.worldtiles)
    {
      if (t.top)
        t.draw(batch);
    }
    
    if (this.message != null)
    {
      this.font.drawMultiLine(batch, this.message,
          this.pancamera.position.x,
          this.pancamera.position.y + 4,
          0,
          HAlignment.CENTER
          );
    }
    //this.debugrend.render(this.w);
  }
  
  public void act(float delta)
  {
    this.stepAccum += delta;
    
    int steps = (int) (this.stepAccum / LevelStage.wantStep);
    if (steps > LevelStage.maxSteps)
      steps = LevelStage.maxSteps;
    this.stepAccum -= LevelStage.wantStep * steps;
    
    while (steps != 0)
    {
      this.runPhysics();
      steps--;
    }
  }

  private void runPhysics()
  {
    GoAloneListener.instance.input.step();
  
    if (!this.stopped && this.message == null)
    {
      this.player.prephys();
      this.weight.prephys();
      
      for (WithPhysicsBody wpb : this.otheractors)
      {
        wpb.prephys();
      }
    }
    
    this.w.step(this.stopped ? LevelStage.wantStep * 0.25f : LevelStage.wantStep, 10, 10);

    this.player.postphys();
    this.weight.postphys();
    
    for (WithPhysicsBody wpb : this.otheractors)
    {
      wpb.postphys();
    }
  }
  
  private WorldTile addworld(float x, float y, String tile, boolean phys)
  {
    WorldTile w = new WorldTile(this, x, y, tile, phys);
    this.worldtiles.add(w);
    return w;
  }
  
  private void addenemy(WithPhysicsBody wb)
  {
    this.bodyToThing.put(wb.body, wb);
    this.otheractors.add(wb);
  }
  
  void nextdialogue()
  {
    if (this.dialogue == null)
      return;
    
    for (int i = 0; i < this.dialogue.length; i++)
    {
      System.out.println("we have dialogue " + this.dialogue[i] + " cur " + this.message + " eq? " + (this.dialogue[i] == this.message));
      if (this.message == null)
      {
        this.emitdialogue(this.dialogue[i]);
        return;
      }
      
      if (this.dialogue[i] == this.message)
      {
        if (this.dialogue.length == i + 1)
          this.emitdialogue(null);
        else
          this.emitdialogue(this.dialogue[i + 1]);
        return;
      }
    }
  }
  
  void emitdialogue(String what)
  {
    this.message = what;
    this.inputblocked = this.message != null ? 15 : 0;
  }
  
  void loaddialogue(int level)
  {
    FileHandle fh = Gdx.files.internal("levels/" + Integer.toString(level) + ".txt");
    String txt = fh.readString();
    this.dialogue = txt.split("\\|");
    this.nextdialogue();
  }

  public void load(int level)
  {
    Pixmap pm = new Pixmap(Gdx.files.internal("levels/" + Integer.toString(level) + ".png"));
    int height = pm.getHeight();
    
    for (int x = 0; x < pm.getWidth(); x++)
    {
      for (int y = 0; y < height; y++)
      {
        int col = pm.getPixel(x, height - y);
        if (col == 0x000000ff)
        {
          continue;
        }
        
        switch (col >> 16 & 0xff)
        {
        case 0xff:
          this.addworld(x, y, "ground", true);
          if (Help.random(1f) < 0.1f)
            this.addworld(x, y, "overlay-flower", false);
          if (Help.random(1f) < 0.1f)
            this.addworld(x, y, "overlay-rocks", false);
          break;
        case 0xcc:
          this.addworld(x, y, "ground-r", true);
          break;
        case 0x99:
          this.addworld(x, y, "ground-l", true);
          break;
        case 0x66:
          this.addworld(x, y, "ground-lr", true);
          break;
        case 0x33:
          this.addworld(x, y, "spikes", true);
          break;
        case 0x88:
          this.addworld(x, y, "dirt", true);
          break;
        }
        
        switch (col >> 8 & 0xff)
        {
        case 0xff:
          this.addenemy(new BallEnemy(this, x, y, 1.3f));
          break;
        case 0xcc:
          this.addenemy(new BallEnemy(this, x, y, 2));
          break;
        case 0x99:
          this.addenemy(new BallEnemy(this, x, y, 3));
          break;
        case 0x33:
          this.addenemy(new BallEnemy(this, x, y, 4));
          break;
        case 0x88:
          this.addenemy(new BallHelper(this, x, y));
          break;
        case 0x66:
          this.addenemy(new BoxHelper(this, x, y));
          break;
        }
        
        switch (col >> 24 & 0xff)
        {
        case 0xff:
          System.out.println("player starts at " + x + ", " + y);
          this.player.moveto(x, y);
          break;
        case 0x99:
          System.out.println("deadweight ends at " + x + ", " + y);
          WorldTile wt = this.addworld(x, y, "door", true);
          wt.setdoor();
          break;
        case 0x66:
          System.out.println("deadweight starts at " + x + ", " + y);
          this.weight.moveto(x, y);
          break;
        case 0x33:
          System.out.println("mother starts at " + x + ", " + y);
          Mother w = new Mother(this);
          w.moveto(x, y);
          this.otheractors.add(w);
          break;
        }
      }
    }
    
    this.haveLevelBounds(pm.getWidth(), pm.getHeight());
    
    this.loaddialogue(level);
  }

  private void haveLevelBounds(float width, float height)
  {
    
    int clouds = (int) Help.random(10, 30);
    for (int i = 0; i < clouds; i++)
    {
      this.cloudtiles.add(new CloudTile(this, Help.random(width), Help.random(height)));
    }
  }

  public WithPosition findThingFromBody(Body body)
  {
    return this.bodyToThing.get(body);
  }

  public boolean deadlyStaticP(Body body)
  {
    for (WorldTile wt : this.worldtiles)
    {
      if (body.equals(wt.body))
        return wt.deadly;
    }
    return false;
  }
  
  
  
}
