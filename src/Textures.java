import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;


public class Textures
{
  static TextureAtlas atlas = new TextureAtlas(Gdx.files.internal("gfx/pack"));

  public static TextureRegion find(String name)
  {
    TextureRegion tr = Textures.atlas.findRegion(name);
    if (tr == null)
      System.out.println("Warn: texture " + name + " not in atlas");
    return tr;
  }
}
