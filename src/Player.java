import java.util.Iterator;
import java.util.List;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.Joint;
import com.badlogic.gdx.physics.box2d.JointEdge;


public class Player extends WithBlinkyEyes
{
  TextureRegion texture, overlay;

  public Player(LevelStage ls)
  {
    this.ls = ls;
    this.texture = Textures.find("player");
    this.overlay = Textures.find("player-overlay");
    this.eyes = new Eyes(ls, this);
    
    this.mkcircle(ls, 0f, 0f, Consts.playerRadius, true);
    
    this.body.setAngularDamping(Consts.playerDamping);
  }

  public void draw(SpriteBatch batch)
  {
    Help.draw(batch, this.texture, this, this.rotation, Help.WhiteColor);
    Help.draw(batch, this.overlay, this, 0, Help.WhiteColor);
    this.eyes.draw(batch);
  }

  public void moveto(float x, float y)
  {
    this.body.setTransform(this.body.getPosition().set(x, y).add(this.size.x / 2, this.size.y / 2), 0);
  }
  
  final float torque = Consts.playerGroundTorque;
  final Vector2 jumpforce = new Vector2(0, Consts.playerJumpForce);
  final Vector2 leftairmoveforce = new Vector2(-Consts.playerAirForce, 0);
  final Vector2 rightairmoveforce = new Vector2(Consts.playerAirForce, 0);

  public void left()
  {
    if (this.istouchingground(Consts.playerMoveNormalX, Consts.playerMoveNormalY))
      this.body.applyTorque(torque);
    else
      this.body.applyForce(leftairmoveforce, this.body.getWorldCenter());
  }

  public void right()
  {
    if (this.istouchingground(Consts.playerMoveNormalX, Consts.playerMoveNormalY))
      this.body.applyTorque(-torque);
    else
      this.body.applyForce(rightairmoveforce, this.body.getWorldCenter());
  }
  
  private boolean istouchingground(float nx, float ny)
  {
    Vector2 vec = null;
    
    for (Contact c : this.ls.w.getContactList())
    {
      if (!c.isTouching())
        continue;
      
      if (!c.getFixtureA().getBody().equals(this.body) && !c.getFixtureB().getBody().equals(this.body))
        continue;
      
      vec = c.GetWorldManifold().getNormal();
      vec.nor();
      if (false) //vec.y < ny || vec.x > nx || vec.x < -nx)
      {
        System.out.println("bad manifold normal " + vec);
        continue;
      }

      return true;
    }
    
    return false;
  }

  int jumpedrecently; // jump debounce
  
  public void jump()
  {
    if (this.jumpedrecently == 0 && this.istouchingground(Consts.playerJumpNormalX, Consts.playerJumpNormalY))
    {
      GameSound.jump();
      this.body.applyLinearImpulse(jumpforce, this.body.getWorldCenter());
      this.jumpedrecently = Consts.playerJumpFrames;
    }
  }
  
  public void postphys()
  {
    super.postphys();
    if (this.jumpedrecently > 0)
      this.jumpedrecently--;
    
    if (this.touchingspikes() || this.position.y < 0)
    {
      this.eyes.sleep = true;
      this.ls.youdied();
    }
  }
  
}
