import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.MassData;

public class BallEnemy extends WithBlinkyEyes
{
  TextureRegion texture, overlay;
  float sz;

  public BallEnemy(LevelStage ls, float x, float y, float sz)
  {
    this.ls = ls;
    this.texture = Textures.find("ball-enemy");
    this.overlay = Textures.find("player-overlay");
    this.eyes = new Eyes(ls, this);
    this.sz = sz;
    
    this.mkcircle(ls, x, y, (Consts.playerRadius / 2) * sz, false);
    MassData md = this.body.getMassData();
    md.mass *= sz;
    this.body.setMassData(md);
    
    this.body.setAngularDamping(Consts.playerDamping);
  }

  @Override
  void draw(SpriteBatch batch)
  {
    Help.draw(batch, this.texture, this, this.rotation, Help.WhiteColor);
    Help.draw(batch, this.overlay, this, 0, Help.WhiteColor);
    this.eyes.draw(batch);
  }
  
  public void postphys()
  {
    super.postphys();
    
    if (this.touchingspikes())
    {
      this.eyes.sleep = true;
      this.body.setType(BodyType.StaticBody);
    }
  }

  
  Vector2 v = new Vector2();
  public void prephys()
  {
    super.prephys();
    
    float force = Consts.playerAttractForce * this.size.x * this.size.x;
    float maxdist = Consts.playerAttractDist;
    float mindist = this.size.x;
    
    if (this.sz >= 3.9)
      force *= 2;
    
    v.set(this.ls.player.position);
    v.sub(this.position);
    float len = v.len();
    if (len > maxdist)
      return;
    if (len < mindist)
      return;
    
    len = maxdist - len;
    
    float intensity = force;
    v.nor().mul(intensity);
    //this.body.applyForce(v, );
    this.body.applyLinearImpulse(v, this.body.getWorldCenter());
  }
}