package flaxbeard.cyberware.client.gui.hud;

import java.io.File;

import net.minecraftforge.common.config.Configuration;
import flaxbeard.cyberware.Cyberware;
import flaxbeard.cyberware.api.hud.IHudSaveData;
import flaxbeard.cyberware.common.CyberwareConfig;

public class HudConfigData implements IHudSaveData
{
	private Configuration config;
	
	public HudConfigData(String name)
	{
		config = new Configuration(new File(CyberwareConfig.configDirectory, "cyberware_hud/" + name + ".cfg"));
	}
	

	@Override
	public void setString(String key, String s)
	{
		//config.getString(key, category, defaultValue, comment)
	}

	@Override
	public void setInteger(String key, int i)
	{
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setBoolean(String key, boolean b)
	{
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setFloat(String key, float f)
	{
		// TODO Auto-generated method stub
		
	}

	@Override
	public String getString(String key)
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int getInteger(String key)
	{
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public boolean getBoolean(String key)
	{
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public float getFloat(String key)
	{
		// TODO Auto-generated method stub
		return 0;
	}
}
