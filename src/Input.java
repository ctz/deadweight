import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.Input.Keys;


public class Input implements InputProcessor
{
  LevelStage ls;
  int inputblocked;

  public Input()
  {
    this.ls = null;
  }

  public void setLevel(LevelStage ls)
  {
    this.ls = ls;
  }
  
  boolean left, right, jump;
  
  public void step()
  {
    if (this.inputblocked > 0)
      this.inputblocked--;
    
    if (this.ls == null)
      return;
    
    if (this.left)
      this.ls.player.left();
    if (this.right)
      this.ls.player.right();
    if (this.jump)
      this.ls.player.jump();
    
    if (this.left || this.right || this.jump)
      this.ls.player.eyes.strain();
    else
      this.ls.player.eyes.unstrain();
  }

  @Override
  public boolean keyDown(int keycode)
  {
    if (this.ls == null)
      return false;
    
    if (this.inputblocked != 0)
      return false;
      
    if (this.ls.stopped || this.ls.message != null)
    {
      this.ls.next();
      return true;
    }
    process(keycode, true);
    return true;
  }

  @Override
  public boolean keyUp(int keycode)
  {
    process(keycode, false);
    return true;
  }
   
  void process(int keycode, boolean onoff)
  {
    switch (keycode)
    {
    case Keys.DPAD_LEFT:
    case Keys.A:
      this.left = onoff;
      break;
      
    case Keys.DPAD_RIGHT:
    case Keys.D:
      this.right = onoff;
      break;

    case Keys.DPAD_CENTER:
    case Keys.DPAD_UP:
    case Keys.W:
    case Keys.SPACE:
      this.jump = onoff;
      break;

    case Keys.END:
    case Keys.ENDCALL:
    case Keys.ESCAPE:
      GoAloneListener.instance.restart();
      break;
      
    }
  }

  @Override
  public boolean keyTyped(char character)
  {
    return false;
  }

  @Override
  public boolean touchDown(int x, int y, int pointer, int button)
  {
    return false;
  }

  @Override
  public boolean touchUp(int x, int y, int pointer, int button)
  {
    return false;
  }

  @Override
  public boolean touchDragged(int x, int y, int pointer)
  {
    return false;
  }

  @Override
  public boolean touchMoved(int x, int y)
  {
    return false;
  }

  @Override
  public boolean scrolled(int amount)
  {
    return false;
  }

  public void blockinput()
  {
    this.inputblocked = 10;
  }
  
}
