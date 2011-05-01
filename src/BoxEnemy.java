import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;


class BoxHelper extends WithBlinkyEyes
{
  TextureRegion texture;

  public BoxHelper(LevelStage ls, float x, float y)
  {
    this.ls = ls;
    this.texture = Textures.find("box-enemy");
    this.eyes = new Eyes(ls, this);
    
    this.mkbox(ls, x, y, 0.98f, 0.98f);
    
    this.body.setAngularDamping(Consts.playerDamping);
  }

  @Override
  void draw(SpriteBatch batch)
  {
    Help.draw(batch, this.texture, this, this.rotation, Help.WhiteColor);
    this.eyes.draw(batch);
  }
}

class BallHelper extends WithBlinkyEyes
{
  TextureRegion texture, overlay;

  public BallHelper(LevelStage ls, float x, float y)
  {
    this.ls = ls;
    this.texture = Textures.find("ball-helper");
    this.overlay = Textures.find("player-overlay");
    this.eyes = new Eyes(ls, this);

    this.mkcircle(ls, x, y, 0.96f * 0.5f, false);
    
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
}