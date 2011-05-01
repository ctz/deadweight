import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.utils.MathUtils;

abstract class WithPosition
{
  Vector2 position = new Vector2(0, 0);
  Vector2 size = new Vector2(0, 0);
  float rotation;
  Vector2 center  = new Vector2(0, 0);
  
  abstract void draw(SpriteBatch batch);
}

abstract class WithBlinkyEyes extends WithPhysicsBody
{
  Eyes eyes;
  int blinkcount;
  int blinkstep = 60;
  
  public void prephys()
  {
    super.prephys();
    this.blinkcount++;
    if (this.blinkcount % this.blinkstep == 0)
    {
      this.eyes.prephys();
      this.blinkstep = MathUtils.random(50, 100);
    }
  }
  
  public void postphys()
  {
    super.postphys();
    this.eyes.postphys();
  }
}

public abstract class WithPhysicsBody extends WithPosition
{
  Body body;
  LevelStage ls;
  Vector2 lastvel = new Vector2();

  public void postphys()
  {
    this.position = this.body.getPosition();
    this.rotation = this.body.getAngle() * MathUtils.radiansToDegrees;
    
    Vector2 dv = this.body.getLinearVelocity();
    dv.sub(this.lastvel);
    float dvc = dv.len();
    
    if (dvc > 2f)
    {
      float vol = dvc / 40;
      if (vol > 1)
        vol = 1;
      GameSound.bosh(vol);
    }
    
    this.lastvel.set(this.body.getLinearVelocity());
  }

  public void prephys()
  {
  }
  
  public boolean touchingspikes()
  {
    for (Contact c : this.ls.w.getContactList())
    {
      if (!c.isTouching())
        continue;
      
      if (c.getFixtureA().getBody().equals(this.body))
      {
        if (this.ls.deadlyStaticP(c.getFixtureB().getBody()))
          return true;
      }
    
      if (c.getFixtureB().getBody().equals(this.body))
      {
        if (this.ls.deadlyStaticP(c.getFixtureA().getBody()))
          return true;
      }
    }
  
    return false;
  }
  
  public boolean touchinggoal()
  {
    for (Contact c : this.ls.w.getContactList())
    {
      if (!c.isTouching())
        continue;
      
      if (c.getFixtureA().getBody().equals(this.body))
      {
        if (c.getFixtureB().isSensor())
          return true;
      }
    
      if (c.getFixtureB().getBody().equals(this.body))
      {
        if (c.getFixtureA().isSensor())
          return true;
      }
    }
  
    return false;
  }
  
  public void mkcircle(LevelStage st, float x, float y, float r, boolean player)
  {
    BodyDef def = new BodyDef();
    def.position.set(x, y - Consts.platDrop).add(r, r);
    def.type = BodyType.DynamicBody;
    this.body = st.w.createBody(def);
    
    CircleShape cs = new CircleShape();
    cs.setRadius(r);
    Fixture f = this.body.createFixture(cs, player ? Consts.playerDensity : Consts.thingDensity);
    f.setFriction(player ? Consts.playerFriction : Consts.enemyFriction);
    f.setRestitution(player ? Consts.playerBounce : Consts.enemyBounce);
    cs.dispose();
    
    this.center.set(r, r);
    this.size.set(this.center).mul(2);
  }
  
  public void mkbox(LevelStage st, float x, float y, float w, float h)
  {
    float w2 = w/2;
    float h2 = h/2;
    BodyDef def = new BodyDef();
    def.position.set(x, y - Consts.platDrop).add(w2, h2);
    def.type = BodyType.DynamicBody;
    this.body = st.w.createBody(def);
    
    PolygonShape ps = new PolygonShape();
    Vector2 points[] = {
        new Vector2(-w2, -h2),
        new Vector2(w2, -h2),
        new Vector2(w2, h2),
        new Vector2(-w2, h2)
    };
    ps.set(points);
    Fixture f = this.body.createFixture(ps, Consts.thingDensity);
    f.setFriction(Consts.enemyFriction);
    f.setRestitution(Consts.enemyBounce);
    ps.dispose();
    
    this.center.set(w2, h2);
    this.size.set(w, h);
    
  }
}
