package flaxbeard.cyberware.api.hud;

public interface IHudSaveData
{
	public void setString(String key, String s);
	public void setInteger(String key, int i);
	public void setBoolean(String key, boolean b);
	public void setFloat(String key, float f);
	
	public String getString(String key);
	public int getInteger(String key);
	public boolean getBoolean(String key);
	public float getFloat(String key);
}
