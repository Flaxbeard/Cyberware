package flaxbeard.cyberware.api.item;

import java.util.ArrayList;
import java.util.List;

public class CyberwareTag
{
	
	private static int count = 0;
	private static List<CyberwareTag> tags = new ArrayList<CyberwareTag>();
	
	
	public static CyberwareTag UTILITY = new CyberwareTag("cyberware.tag.utility");
	public static CyberwareTag MOBILITY = new CyberwareTag("cyberware.tag.mobility");
	public static CyberwareTag BUILDING = new CyberwareTag("cyberware.tag.building");
	public static CyberwareTag COMBAT = new CyberwareTag("cyberware.tag.combat");
	public static CyberwareTag POWERGEN = new CyberwareTag("cyberware.tag.powergen");
	public static CyberwareTag POWERSTORAGE = new CyberwareTag("cyberware.tag.powerstorage");
	public static CyberwareTag SURVIVABILITY = new CyberwareTag("cyberware.tag.survivability");
	public static CyberwareTag MINING = new CyberwareTag("cyberware.tag.mining");
	public static CyberwareTag INFO = new CyberwareTag("cyberware.tag.info");
	public static CyberwareTag REPLACMENT = new CyberwareTag("cyberware.tag.replacement");

	public static List<CyberwareTag> getTags()
	{
		return tags;
	}

	private final String unlocalizedName;
	private final int id;
	
	public CyberwareTag(String name)
	{
		unlocalizedName = name;
		tags.add(this);
		id = count;
		count++;
	}
	
	public String getUnlocalizedName()
	{
		return unlocalizedName;
	}
	
	public int ordinal()
	{
		return id;
	}
	
}
