import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

public class Weight extends WithBlinkyEyes
{
  LevelStage ls;
  TextureRegion texture, overlay;

  public Weight(LevelStage ls)
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
    this.body.setTransform(this.body.getPosition().set(x, y), 0);
  }
}