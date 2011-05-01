import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;


public class GoAloneListener implements ApplicationListener
{
  SpriteBatch batch;
  LevelStage stage;
  BitmapFont tmpfont;
  GameSound sound;
  Input input;
  int currentLevel = 0;
  
  static GoAloneListener instance;
  
  @Override
  public void create()
  {
    this.batch = new SpriteBatch();
    this.sound = new GameSound();
    
    this.input = new Input();
    Gdx.input.setInputProcessor(this.input);
    
    GoAloneListener.instance = this;
    this.restart();
  }
  
  void next()
  {
    this.currentLevel++;
    if (!Help.levelExists(this.currentLevel))
    {
      this.currentLevel = 0;
    }
    
    this.restart();
  }
  
  void restart()
  {
    this.stage = new LevelStage();
    this.input.setLevel(this.stage);
    this.stage.load(currentLevel);
    this.resize(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
    this.sound.startmusic();
  }
  
  @Override
  public void dispose()
  {
  }
  
  @Override
  public void pause()
  {
  }
  
  @Override
  public void render()
  {
    float delta = Gdx.graphics.getDeltaTime();
    this.stage.act(delta);
    
    this.stage.pan();
    
    this.stage.background();
    this.batch.begin();
    this.stage.draw(this.batch);
    this.batch.end();
  }
  
  @Override
  public void resize(int x, int y)
  {
    this.stage.resize(x, y);
  }
  
  @Override
  public void resume()
  {
  }
}
