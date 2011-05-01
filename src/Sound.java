import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;

class GameSound
{
  Music music;
  
  Sound fx_fallout;
  Sound fx_jump;
  Sound fx_win;
  Sound fx_bosh;
  
  GameSound()
  {    
    this.music = Gdx.audio.newMusic(Gdx.files.internal("sfx/game.ogg"));
    this.music.setLooping(true);

    this.fx_fallout = Gdx.audio.newSound(Gdx.files.internal("sfx/fallout.wav"));
    this.fx_jump = Gdx.audio.newSound(Gdx.files.internal("sfx/jump.wav"));
    this.fx_win = Gdx.audio.newSound(Gdx.files.internal("sfx/win.wav"));
    this.fx_bosh = Gdx.audio.newSound(Gdx.files.internal("sfx/bosh.wav"));
  }
  
  void startmusic()
  {
    this.music.play();
  }
  
  void pausemusic()
  {
    this.music.pause();
  }
  
  static void fallout()
  {
    GoAloneListener.instance.sound.fx_fallout.play();
    GoAloneListener.instance.sound.pausemusic();
  }
  
  static void win()
  {
    GoAloneListener.instance.sound.fx_win.play();
    GoAloneListener.instance.sound.pausemusic();
  }
  
  static void jump()
  {
    GoAloneListener.instance.sound.fx_jump.play();
  }
  
  static void bosh(float vol)
  {
    GoAloneListener.instance.sound.fx_bosh.play(vol);
  }
}
