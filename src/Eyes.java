
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.QueryCallback;
import com.badlogic.gdx.utils.MathUtils;

class Eye extends WithPosition
{
  static final float RADIUS = 0.2f;
  static final float XSPACE = 0.12f;
  static final float YSPACE = 0.3f;
  
  Eye()
  {
    this.size.set(RADIUS, RADIUS);
    this.center.set(this.size).mul(0.5f);
  }

  @Override
  void draw(SpriteBatch batch)
  {
  }
}

class Eyes
{
  WithPhysicsBody parent;
  Eye a = new Eye();
  Eye b = new Eye();
  TextureRegion tex;
  TextureRegion tex_closed, tex_closing, tex_sleep;
  int squinting = 0;
  int blinking = 0;
  Vector2 tmp = new Vector2();
  Vector2 target = new Vector2();
  LevelStage ls;
  WithPosition targetobj;
  boolean sleep;

  static final int BLINK_LEN = 10;
  static final int SQUINT_OUT = 2;
  
  Eyes(LevelStage ls, WithPhysicsBody ao)
  {
    this.ls = ls;
    this.parent = ao;
    this.tex = Textures.find("eye-open");
    this.tex_closed = Textures.find("eye-closed");
    this.tex_closing = Textures.find("eye-closing");
    this.tex_sleep = Textures.find("eye-sleep");
  }

  public void draw(SpriteBatch batch)
  {
    if (this.sleep)
    {
      Help.draw(batch, this.tex_sleep, this.a, this.parent.rotation + 180, Help.WhiteColor);
      Help.draw(batch, this.tex_sleep, this.b, this.parent.rotation, Help.WhiteColor);
    }
    else if (this.squinting > 0)
    {
      this.squinting--;
      Help.draw(batch, this.tex_closing, this.a, this.parent.rotation + 180, Help.WhiteColor);
      Help.draw(batch, this.tex_closing, this.b, this.parent.rotation, Help.WhiteColor);
    } else if (this.blinking > 0) {
      this.blinking--;
      Help.draw(batch, this.tex_closed, this.a, this.parent.rotation + 180, Help.WhiteColor);
      Help.draw(batch, this.tex_closed, this.b, this.parent.rotation, Help.WhiteColor);
    
      if (this.blinking == 0)
        this.squinting = SQUINT_OUT;
    } else {
      Help.draw(batch, this.tex, this.a, this.a.rotation, Help.WhiteColor);
      Help.draw(batch, this.tex, this.b, this.b.rotation, Help.WhiteColor);
    }
    
    // Help.debug(batch, this.target.x, this.target.y);
  }

  public void prephys()
  { 
    Vector2 vel = this.parent.body.getLinearVelocity();
    this.targetobj = null;
    
    if (vel.len() > 0.5)
    {
      //System.out.println("we have prephys vel");
      this.target.set(this.parent.position);
      this.target.add(vel.mul(3));
      //System.out.println("we have prephys vel = " + this.target);
    } else {
      //System.out.println("we have prephys search");
      
      this.ls.w.QueryAABB(new QueryCallback() {
          @Override
          public boolean reportFixture(Fixture fixture)
          {
            WithPosition ao = Eyes.this.ls.findThingFromBody(fixture.getBody());
            if (ao != null && ao != Eyes.this.parent && ao instanceof WithBlinkyEyes)
            {
              Eyes.this.targetobj = (WithBlinkyEyes) ao;
              return false;
            }
            return true;
          }
        },
        this.parent.position.x - Consts.findSlopX,
        this.parent.position.y - Consts.findSlopY,
        this.parent.position.x + Consts.findSlopX,
        this.parent.position.y + Consts.findSlopY);

      //System.out.println("we have prephys search = " + this.targetobj);
      if (this.targetobj == null)
      {
        this.target.set(this.parent.position);
        this.target.add(MathUtils.random(-4, 4), MathUtils.random(-2, 2));
        //System.out.println("we have prephys rand = " + this.target);
      } else {
        this.lookAtTargetEyes();
      }
    }
    
    if (this.blinking < BLINK_LEN)
    {
      this.blinking = BLINK_LEN;
      this.squinting = SQUINT_OUT;
    }
  }
  
  private void lookAtTargetEyes()
  {
    if (this.targetobj != null)
    {
      //System.out.println("we have target object");
      this.tmp.set(this.targetobj.center);
      this.tmp.x = 0;
      this.tmp.y -= Eye.YSPACE;
      this.tmp.rotate(this.targetobj.rotation);
      this.tmp.add(this.targetobj.position);
      this.target.set(this.tmp);
    }
  }

  public void postphys()
  {
    this.lookAtTargetEyes();
    
    float targangle;
    
    this.tmp.set(this.parent.center);
    this.tmp.x = Eye.XSPACE;
    this.tmp.y -= Eye.YSPACE;
    this.tmp.rotate(this.parent.rotation);
    this.tmp.add(this.parent.position);
    this.a.position.set(this.tmp);
    targangle = Help.angleOfVec(this.tmp.sub(this.target));
    
    this.a.rotation = Help.lerpAngle(this.a.rotation, targangle + 180, 0.12f);
    
    this.tmp.set(this.parent.center);
    this.tmp.x = - Eye.XSPACE;
    this.tmp.y -= Eye.YSPACE;
    this.tmp.rotate(this.parent.rotation);
    this.tmp.add(this.parent.position);
    this.b.position.set(this.tmp);
    targangle = Help.angleOfVec(this.tmp.sub(this.target));
    
    this.b.rotation = Help.lerpAngle(this.b.rotation, targangle + 180, 0.12f);
  }
  
  boolean straining = false;

  public void strain()
  {
    if (!this.straining)
    {
      this.blinking = Integer.MAX_VALUE;
      this.squinting = SQUINT_OUT;
      this.straining = true;
    }
  }

  public void unstrain()
  {
    if (this.straining)
    {
      this.blinking = 1;
      this.straining = false;
    }
  }
}