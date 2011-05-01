import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

class Mother extends Sister
{

  public Mother(LevelStage ls)
  {
    super();
    this.ls = ls;
    this.texture = Textures.find("deadweight");
    this.overlay = Textures.find("player-overlay");
    this.eyes = new Eyes(ls, this);
    
    this.mkcircle(ls, 0f, 0f, Consts.playerRadius * 1.4f, false);
    
    this.body.setAngularDamping(Consts.playerDamping);
  }
  
  public void postphys()
  {
    super.postphys();

    if (this.touchingspikes())
      this.ls.WHAATTT();

    if (this.position.y < 0)
      this.ls.WHAATTT();
  }
}

public class Sister extends WithBlinkyEyes
{
  TextureRegion texture, overlay;

  public Sister(){}

  public Sister(LevelStage ls)
  {
    this.ls = ls;
    this.texture = Textures.find("deadweight");
    this.overlay = Textures.find("player-overlay");
    this.eyes = new Eyes(ls, this);
    this.eyes.sleep = true;
    
    this.mkcircle(ls, 0f, 0f, Consts.playerRadius, false);
    
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
  
  public void postphys()
  {
    super.postphys();
    
    if (this.touchingspikes())
      this.ls.sisterdied();

    if (this.position.y < 0)
      this.ls.sisterdied();
    
    if (this.touchinggoal())
    {
      this.ls.win();
    }
  }
}