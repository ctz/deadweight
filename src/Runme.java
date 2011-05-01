import com.badlogic.gdx.backends.lwjgl.LwjglApplication;


public class Runme
{
  public static void main(String []args)
  {
    new LwjglApplication(new GoAloneListener(), "Deadweight", 1024, 768, false);
  }
}
