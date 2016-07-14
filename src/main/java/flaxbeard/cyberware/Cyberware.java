package flaxbeard.cyberware;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.Mod.Instance;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import flaxbeard.cyberware.api.ICyberwareTabItem;
import flaxbeard.cyberware.api.ICyberwareTabItem.EnumCategory;
import flaxbeard.cyberware.common.CommonProxy;
import flaxbeard.cyberware.common.CyberwareConfig;

@Mod(modid = Cyberware.MODID, version = Cyberware.VERSION)
public class Cyberware
{
	public static final String MODID = "cyberware";
	public static final String VERSION = "@VERSION@";
	
	@Instance(MODID)
	public static Cyberware INSTANCE;
		
	@SidedProxy(clientSide = "flaxbeard.cyberware.client.ClientProxy", serverSide = "flaxbeard.cyberware.common.CommonProxy")
	public static CommonProxy proxy;
	
	@EventHandler
	public void preInit(FMLPreInitializationEvent event)
	{
		CyberwareConfig.loadConfig(event);
		proxy.preInit();
	}
	
	@EventHandler
	public void init(FMLInitializationEvent event)
	{
		proxy.init();
	}
	
	@EventHandler
	public void postInit(FMLPostInitializationEvent event)
	{
		proxy.postInit();
	}
	
	public static CreativeTabs creativeTab = new CreativeTabs(MODID)
	{
		@Override
		public Item getTabIconItem()
		{
			return null;
		}
		@Override
		public ItemStack getIconItemStack()
		{
			return new ItemStack(Items.REDSTONE);
		}
		
		@Override
		@SideOnly(Side.CLIENT)
		public void displayAllRelevantItems(List<ItemStack> list)
		{
			Map<EnumCategory, List<ItemStack>> subLists = new EnumMap<EnumCategory, List<ItemStack>>(EnumCategory.class);
			for (EnumCategory category : EnumCategory.values())
			{
				subLists.put(category, new ArrayList<ItemStack>());
			}
			List<ItemStack> unsorted = new ArrayList<ItemStack>();
			
			for (Item item : Item.REGISTRY)
			{
				if (item == null)
				{
					continue;
				}
				for (CreativeTabs tab : item.getCreativeTabs())
				{
					if (tab == this)
					{
						if (item instanceof ICyberwareTabItem)
						{
							List<ItemStack> tempList = new ArrayList<ItemStack>();	
							item.getSubItems(item, this, tempList);
							
							for (ItemStack stack : tempList)
							{
								if (stack != null)
								{
									EnumCategory cat = ((ICyberwareTabItem) stack.getItem()).getCategory(stack);
									subLists.get(cat).add(stack);
								}
							}
						}
						else
						{
							item.getSubItems(item, this, unsorted);
						}
					}
				}
			}
			
			for (EnumCategory category : EnumCategory.values())
			{
				List<ItemStack> toAdd = subLists.get(category);
				int blank = 9 - (toAdd.size() % 9);

				list.addAll(toAdd);

				
			}
			
			list.addAll(unsorted);

			if (this.getRelevantEnchantmentTypes() != null)
			{
				this.addEnchantmentBooksToList(list, this.getRelevantEnchantmentTypes());
			}
		}
	};
}
