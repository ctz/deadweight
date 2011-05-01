import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.utils.MathUtils;

class CloudTile extends WorldTile
{
  float depth;
  float xorig, yorig;
  
  CloudTile(LevelStage ls, float x, float y)
  {
    super(ls, x, y, "cloud", false);
    this.xorig = x;
    this.yorig = y;
    float sz = MathUtils.random(0.1f, 0.5f);
    this.setsz(sz * 20);
    this.depth = sz;
  }
  
  void move(float x, float y)
  {
    this.pos.x = this.xorig + x * this.depth;
    this.pos.y = this.yorig + y * this.depth;
  }
}

public class WorldTile
{
  Vector2 pos = new Vector2();
  Vector2 size = new Vector2();
  TextureRegion texture;
  Body body;
  boolean top;
  boolean deadly;
  
  WorldTile(LevelStage ls, float x, float y, String tile, boolean physics)
  {
    this.pos.set(x, y);
    this.size.set(1, 1);
    this.texture = Textures.find(tile);
    this.top = !physics;
    this.deadly = tile.equals("spikes");

    if (!physics)
      return;
    
    float height = tile.equals("dirt") ? 1 : Consts.platHeight;
    
    BodyDef def = new BodyDef();
    def.position.x = x;
    def.position.y = y;
    def.type = BodyType.StaticBody;
    
    this.body = ls.w.createBody(def);
    
    PolygonShape ps = new PolygonShape();
    Vector2 points[] = {
        new Vector2(0,            0),
        new Vector2(this.size.x,  0),
        new Vector2(this.size.x,  this.size.y * height),
        new Vector2(0,            this.size.y * height)
    };
    ps.set(points);
    Fixture f = this.body.createFixture(ps, 1);
    f.setFriction(Consts.worldFriction);
    f.setRestitution(Consts.worldBounce);
    ps.dispose();
  }
  
  void draw(SpriteBatch batch)
  {
    batch.draw(this.texture,
        this.pos.x,
        this.pos.y,
        this.size.x,
        this.size.y);
  }
  
  void setsz(float x)
  {
    this.size.x = x;
    this.size.y = this.size.x / ((float) this.texture.getRegionWidth() / (float) this.texture.getRegionHeight());
  }

  public void setdoor()
  {
    this.setsz(3);
    this.pos.y -= Consts.platHeight + 0.1f;
    this.pos.x -= this.size.x / 3;
    this.body.getFixtureList().get(0).setSensor(true);
  }
}
