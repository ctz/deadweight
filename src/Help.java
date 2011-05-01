import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.collision.BoundingBox;
import com.badlogic.gdx.utils.MathUtils;


public class Help
{
  public static final Color WhiteColor = new Color(0xff, 0xff, 0xff, 0xff);

  public static void draw(SpriteBatch batch, TextureRegion tx, WithPosition thing, float angle, Color col)
  {
    batch.setColor(col);
    batch.draw(tx,
        thing.position.x - thing.center.x,
        thing.position.y - thing.center.y,
        thing.center.x,
        thing.center.y,
        thing.size.x,
        thing.size.y,
        1, 1,
        angle);
  }

  public static TextureRegion debugtx = null;
  public static void debug(SpriteBatch batch, float x, float y)
  {
    float sz = 0.2f;
    if (Help.debugtx == null)
    {
      Help.debugtx = Textures.find("ground");
    }
    
    batch.draw(Help.debugtx, x - sz / 2, y - sz / 2, sz, sz);
  }
  
  public static float lerp(float a, float b, float alpha)
  {
    return a * (1 - alpha) + b * (alpha);
  }

  public static float lerpAngle(float a, float b, float alpha)
  {
    float delta;
    a %= 360;
    b %= 360;
    delta = b - a;
    
    if (Math.abs(delta) <= 180)
      return a + delta * alpha;
    
    if (a > b)
    {
      delta = 360 - a;
      a = 0;
      b += delta;
    } else {
      delta = 360 - b;
      b = 0;
      a += delta;
    }
    
    return ((a + (b - a) * alpha) - delta) % 360;
  }
  
  public static float angleOfVec(Vector2 v)
  {
    // bug in 195 libgdx!!
    float angle = (float) Math.atan2(v.y, v.x) * MathUtils.radiansToDegrees;
    if (angle < 0)
      angle += 360;
    return angle;
  }

  public static boolean levelExists(int currentLevel)
  {
    String prefix = "levels/" + Integer.toString(currentLevel);
    
    return Gdx.files.internal(prefix + ".png").exists() &&
           Gdx.files.internal(prefix + ".txt").exists();
  }
  
}
