package flaxbeard.cyberware.common;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.entity.EnumCreatureType;
import net.minecraft.entity.monster.EntityZombie;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.init.SoundEvents;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.Item;
import net.minecraft.item.Item.ToolMaterial;
import net.minecraft.item.ItemArmor.ArmorMaterial;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Potion;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.WeightedRandom;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.Biome.SpawnListEntry;
import net.minecraftforge.common.util.EnumHelper;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.event.FMLInterModComms;
import net.minecraftforge.fml.common.registry.EntityRegistry;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.oredict.OreDictionary;
import net.minecraftforge.oredict.ShapedOreRecipe;
import net.minecraftforge.oredict.ShapelessOreRecipe;
import flaxbeard.cyberware.Cyberware;
import flaxbeard.cyberware.api.CyberwareAPI;
import flaxbeard.cyberware.api.item.ICyberware.EnumSlot;
import flaxbeard.cyberware.common.block.BlockBeacon;
import flaxbeard.cyberware.common.block.BlockBeaconLarge;
import flaxbeard.cyberware.common.block.BlockBeaconPost;
import flaxbeard.cyberware.common.block.BlockBlueprintArchive;
import flaxbeard.cyberware.common.block.BlockCharger;
import flaxbeard.cyberware.common.block.BlockComponentBox;
import flaxbeard.cyberware.common.block.BlockEngineeringTable;
import flaxbeard.cyberware.common.block.BlockRFIDContainer;
import flaxbeard.cyberware.common.block.BlockScanner;
import flaxbeard.cyberware.common.block.BlockSurgery;
import flaxbeard.cyberware.common.block.BlockSurgeryChamber;
import flaxbeard.cyberware.common.effect.PotionNeuropozyne;
import flaxbeard.cyberware.common.entity.EntityBytebug;
import flaxbeard.cyberware.common.entity.EntityCyberZombie;
import flaxbeard.cyberware.common.integration.botania.BotaniaIntegration;
import flaxbeard.cyberware.common.integration.tan.ToughAsNailsIntegration;
import flaxbeard.cyberware.common.item.ItemArmUpgrade;
import flaxbeard.cyberware.common.item.ItemArmorCyberware;
import flaxbeard.cyberware.common.item.ItemBlueprint;
import flaxbeard.cyberware.common.item.ItemBodyPart;
import flaxbeard.cyberware.common.item.ItemBoneUpgrade;
import flaxbeard.cyberware.common.item.ItemBrainUpgrade;
import flaxbeard.cyberware.common.item.ItemCreativeBattery;
import flaxbeard.cyberware.common.item.ItemCybereyeUpgrade;
import flaxbeard.cyberware.common.item.ItemCybereyes;
import flaxbeard.cyberware.common.item.ItemCyberheart;
import flaxbeard.cyberware.common.item.ItemCyberlimb;
import flaxbeard.cyberware.common.item.ItemCyberware;
import flaxbeard.cyberware.common.item.ItemCyberwareBase;
import flaxbeard.cyberware.common.item.ItemDenseBattery;
import flaxbeard.cyberware.common.item.ItemExpCapsule;
import flaxbeard.cyberware.common.item.ItemEyeUpgrade;
import flaxbeard.cyberware.common.item.ItemFootUpgrade;
import flaxbeard.cyberware.common.item.ItemHandUpgrade;
import flaxbeard.cyberware.common.item.ItemHeartUpgrade;
import flaxbeard.cyberware.common.item.ItemLegUpgrade;
import flaxbeard.cyberware.common.item.ItemLowerOrgansUpgrade;
import flaxbeard.cyberware.common.item.ItemLungsUpgrade;
import flaxbeard.cyberware.common.item.ItemMuscleUpgrade;
import flaxbeard.cyberware.common.item.ItemNeuropozyne;
import flaxbeard.cyberware.common.item.ItemSkinUpgrade;
import flaxbeard.cyberware.common.item.ItemSwordCyberware;
import flaxbeard.cyberware.common.item.VanillaWares.SpiderEyeWare;
import flaxbeard.cyberware.common.misc.BlueprintCraftingHandler;
import flaxbeard.cyberware.common.misc.CyberwareDyingHandler;

public class CyberwareContent
{
	public static final int RARE = 10;
	public static final int UNCOMMON = 25;
	public static final int COMMON = 50;
	public static final int VERY_COMMON = 100;

	public static Block surgeryApparatus;
	public static BlockSurgeryChamber surgeryChamber;
	public static Block charger;
	public static BlockEngineeringTable engineering;
	public static Block scanner;
	public static Block blueprintArchive;
	public static BlockComponentBox componentBox;
	public static BlockBeacon radio;
	public static BlockBeaconLarge radioLarge;
	public static Block radioPost;
	public static Block rfidContainer;

	private static ArmorMaterial shadesMat1;
	private static ArmorMaterial shadesMat2;
	public static Item shades;
	public static Item shades2;
	
	private static ArmorMaterial jacketMat;
	public static Item jacket;
	
	public static ArmorMaterial trenchMat;
	public static ItemArmorCyberware trenchcoat;
	
	private static ToolMaterial katanaMat;
	public static Item katana;

	public static Item bodyPart;
	public static ItemCyberware cybereyes;
	public static ItemCyberware cybereyeUpgrades;
	public static ItemCyberware eyeUpgrades;

	public static ItemCyberware brainUpgrades;
	public static Item expCapsule;
	public static ItemCyberware heartUpgrades;
	public static ItemCyberware cyberheart;
	public static ItemCyberware lungsUpgrades;
	public static ItemCyberware lowerOrgansUpgrades;
	public static ItemCyberware denseBattery;
	public static ItemCyberware skinUpgrades;
	public static ItemCyberware muscleUpgrades;
	public static ItemCyberware boneUpgrades;
	public static ItemCyberware armUpgrades;
	public static ItemCyberware handUpgrades;
	public static ItemCyberware legUpgrades;
	public static ItemCyberware footUpgrades;
	public static ItemCyberware cyberlimbs;
	public static ItemCyberware creativeBattery;
	
	public static Item component;
	public static Item neuropozyne;
	public static Item blueprint;

	public static Potion neuropozyneEffect;

	public static List<Item> items;
	public static List<Block> blocks;
	
	public static List<NumItems> numItems;

	public static void preInit()
	{
		
		items = new ArrayList<Item>();
		blocks = new ArrayList<Block>();
		
		numItems = new ArrayList<NumItems>();
		numItems.add(new NumItems(50, 4));
		numItems.add(new NumItems(25, 3));
		numItems.add(new NumItems(25, 5));
		numItems.add(new NumItems(15, 6));
		numItems.add(new NumItems(5, 10));


		if (!CyberwareConfig.NO_ZOMBIES)
		{
			EntityRegistry.registerModEntity(EntityCyberZombie.class, "cyberzombie", 0, Cyberware.INSTANCE, 80, 3, true);
			EntityRegistry.registerEgg(EntityCyberZombie.class, 0x6B6B6B, 0x799C65);
			if (Loader.isModLoaded("EnderIO"))
			{
				FMLInterModComms.sendMessage("EnderIO", "poweredSpawner:blacklist:add", "cyberware.cyberzombie");
			}
		}
		
		EntityRegistry.registerModEntity(EntityBytebug.class, "bytebug", 1, Cyberware.INSTANCE, 80, 3, true);
		EntityRegistry.registerEgg(EntityBytebug.class, 0x6B6B6B, 0x008E8E);
		if (Loader.isModLoaded("EnderIO"))
		{
			FMLInterModComms.sendMessage("EnderIO", "poweredSpawner:blacklist:add", "cyberware.bytebug");
		}
		
		neuropozyneEffect = new PotionNeuropozyne("neuropozyne", false, 0x47453d);

		blueprintArchive = new BlockBlueprintArchive();
		componentBox = new BlockComponentBox();

		surgeryApparatus = new BlockSurgery();
		surgeryChamber = new BlockSurgeryChamber();
		charger = new BlockCharger();
		engineering = new BlockEngineeringTable();
		scanner = new BlockScanner();
		radio = new BlockBeacon();
		radioLarge = new BlockBeaconLarge();
		radioPost = new BlockBeaconPost();
		rfidContainer = new BlockRFIDContainer();

		neuropozyne = new ItemNeuropozyne("neuropozyne");
		blueprint = new ItemBlueprint("blueprint");
		component = new ItemCyberwareBase("component", "actuator", "reactor", "titanium", "ssc", "plating", "fiberoptics", "fullerene", "synthnerves", "storage", "microelectric");
		
		if (CyberwareConfig.CLOTHES)
		{
			shadesMat1 = EnumHelper.addArmorMaterial("SHADES", "cyberware:vanity", 5, new int[]{1, 2, 3, 1}, 15, SoundEvents.ITEM_ARMOR_EQUIP_GENERIC, 0.0F);
			shadesMat1.customCraftingMaterial = Item.getItemFromBlock(Blocks.GLASS);
			shadesMat2 = EnumHelper.addArmorMaterial("SHADES2", "cyberware:vanity_2", 5, new int[]{1, 2, 3, 1}, 15, SoundEvents.ITEM_ARMOR_EQUIP_GENERIC, 0.0F);
			shadesMat2.customCraftingMaterial = Item.getItemFromBlock(Blocks.GLASS);
			shades = new ItemArmorCyberware("shades", shadesMat1, 0, EntityEquipmentSlot.HEAD);
			shades2 = new ItemArmorCyberware("shades2", shadesMat2, 0, EntityEquipmentSlot.HEAD);
			
			jacketMat = EnumHelper.addArmorMaterial("JACKET", "cyberware:jacket", 5, new int[]{1, 2, 3, 1}, 15, SoundEvents.ITEM_ARMOR_EQUIP_LEATHER, 0.0F);
			jacketMat.customCraftingMaterial = Items.LEATHER;
			jacket = new ItemArmorCyberware("jacket", jacketMat, 0, EntityEquipmentSlot.CHEST);
			
			trenchMat = EnumHelper.addArmorMaterial("TRENCHCOAT", "cyberware:trenchcoat", 5, new int[]{1, 2, 3, 1}, 15, SoundEvents.ITEM_ARMOR_EQUIP_LEATHER, 0.0F);
			trenchMat.customCraftingMaterial = Items.LEATHER;
			trenchcoat = new ItemArmorCyberware("trenchcoat", trenchMat, 0, EntityEquipmentSlot.CHEST);
		}

		if (CyberwareConfig.KATANA)
		{
			katanaMat = EnumHelper.addToolMaterial("KATANA", 
					ToolMaterial.IRON.getHarvestLevel(), 
					ToolMaterial.IRON.getMaxUses(), 
					ToolMaterial.IRON.getEfficiencyOnProperMaterial(), 
					ToolMaterial.IRON.getDamageVsEntity(), 
					ToolMaterial.IRON.getEnchantability());
			katanaMat.setRepairItem(new ItemStack(component, 1, 4));
			
			katana = new ItemSwordCyberware("katana", katanaMat);
		}
		
		bodyPart = new ItemBodyPart("bodyPart", 
				new EnumSlot[] { EnumSlot.EYES, EnumSlot.CRANIUM, EnumSlot.HEART, EnumSlot.LUNGS, EnumSlot.LOWER_ORGANS, EnumSlot.SKIN, EnumSlot.MUSCLE, EnumSlot.BONE, EnumSlot.ARM, EnumSlot.ARM, EnumSlot.LEG, EnumSlot.LEG },
				new String[] { "eyes", "brain", "heart", "lungs", "stomach", "skin", "muscles", "bones", "armLeft", "armRight", "legLeft", "legRight"});
		
		
		cybereyes = new ItemCybereyes("cybereyes", EnumSlot.EYES);
		cybereyes.setEssenceCost(8); // 0.2.0 Changed from 10
		cybereyes.setWeights(UNCOMMON);
		cybereyes.setComponents(new ItemStack[] { new ItemStack(component, 1, 4), new ItemStack(component, 2, 5), new ItemStack(component, 2, 7) });
		
		cybereyeUpgrades = new ItemCybereyeUpgrade("cybereyeUpgrades", EnumSlot.EYES,
				new String[] { "nightVision", "underwaterVision", "hudjack", "targeting", "zoom" });
		cybereyeUpgrades.setEssenceCost(2, 2, 1, 1, 1);
		cybereyeUpgrades.setWeights(UNCOMMON, UNCOMMON, UNCOMMON, UNCOMMON, UNCOMMON);
		cybereyeUpgrades.setComponents(
				new ItemStack[] { new ItemStack(component, 1, 4), new ItemStack(component, 2, 5), new ItemStack(component, 1, 7) },
				new ItemStack[] { new ItemStack(component, 2, 5), new ItemStack(component, 1, 7) },
				new ItemStack[] { new ItemStack(component, 1, 3), new ItemStack(component, 1, 5), new ItemStack(component, 1, 6), new ItemStack(component, 2, 7) },
				new ItemStack[] { new ItemStack(component, 2, 3), new ItemStack(component, 1, 5), new ItemStack(component, 1, 6), new ItemStack(component, 1, 7) },
				new ItemStack[] { new ItemStack(component, 2, 5), new ItemStack(component, 4, 7) }
				);
		
		eyeUpgrades = new ItemEyeUpgrade("eyeUpgrades", EnumSlot.EYES,
				new String[] { "hudlens" });
		eyeUpgrades.setEssenceCost(1);
		eyeUpgrades.setWeights(VERY_COMMON);
		eyeUpgrades.setComponents(
				new ItemStack[] { new ItemStack(component, 1, 3), new ItemStack(component, 1, 5), new ItemStack(component, 1, 6), new ItemStack(component, 2, 7) }
				);

		CyberwareAPI.linkCyberware(Items.SPIDER_EYE, new SpiderEyeWare());
		
		brainUpgrades = new ItemBrainUpgrade("brainUpgrades", EnumSlot.CRANIUM,
				new String[] { "corticalStack", "enderJammer", "consciousnessTransmitter", "neuralContextualizer", "matrix", "radio" });
		brainUpgrades.setEssenceCost(3, 10, 2, 2, 8, 2); // TMC 0.2.0 changed from 10
		brainUpgrades.setWeights(RARE, UNCOMMON, UNCOMMON, COMMON, UNCOMMON, UNCOMMON);
		brainUpgrades.setComponents(
				new ItemStack[] { new ItemStack(component, 2, 1), new ItemStack(component, 1, 7), new ItemStack(component, 2, 8) },
				new ItemStack[] { new ItemStack(component, 1, 2), new ItemStack(component, 1, 3), new ItemStack(component, 1, 5), new ItemStack(component, 2, 9) },
				new ItemStack[] { new ItemStack(component, 1, 3), new ItemStack(component, 1, 6), new ItemStack(component, 3, 7) },
				new ItemStack[] { new ItemStack(component, 1, 3), new ItemStack(component, 1, 6), new ItemStack(component, 3, 7) },
				new ItemStack[] { new ItemStack(component, 3, 3), new ItemStack(component, 1, 5), new ItemStack(component, 2, 9) },
				new ItemStack[] { new ItemStack(component, 1, 2), new ItemStack(component, 1, 3), new ItemStack(component, 1, 5), new ItemStack(component, 1, 9) }
				);
		expCapsule = new ItemExpCapsule("expCapsule");
		
		cyberheart = new ItemCyberheart("cyberheart", EnumSlot.HEART);
		cyberheart.setEssenceCost(5);
		cyberheart.setWeights(COMMON);
		cyberheart.setComponents(new ItemStack[] { new ItemStack(component, 2, 0), new ItemStack(component, 1, 2), new ItemStack(component, 1, 7) });
		
		denseBattery = new ItemDenseBattery("denseBattery", EnumSlot.LOWER_ORGANS);
		denseBattery.setEssenceCost(15);
		denseBattery.setWeights(RARE);
		denseBattery.setComponents(new ItemStack[] { new ItemStack(component, 3, 6), new ItemStack(component, 4, 9) });

		creativeBattery = new ItemCreativeBattery("creativeBattery", EnumSlot.LOWER_ORGANS);
		creativeBattery.setEssenceCost(0);
		
		heartUpgrades = new ItemHeartUpgrade("heartUpgrades", EnumSlot.HEART,
				new String[] { "defibrillator", "platelets", "medkit", "coupler" });
		heartUpgrades.setEssenceCost(10, 5, 15, 10);
		heartUpgrades.setWeights(COMMON, UNCOMMON, UNCOMMON, VERY_COMMON);
		heartUpgrades.setComponents(
				new ItemStack[] { new ItemStack(component, 1, 0), new ItemStack(component, 2, 6), new ItemStack(component, 2, 9) },
				new ItemStack[] { new ItemStack(component, 1, 0), new ItemStack(component, 2, 1), new ItemStack(component, 1, 8) },
				new ItemStack[] { new ItemStack(component, 3, 1), new ItemStack(component, 1, 6), new ItemStack(component, 1, 7) },
				new ItemStack[] { new ItemStack(component, 1, 1), new ItemStack(component, 1, 7), new ItemStack(component, 2, 9) }
				);
		
		lungsUpgrades = new ItemLungsUpgrade("lungsUpgrades", EnumSlot.LUNGS,
				new String[] { "oxygen", "hyperoxygenation" });
		lungsUpgrades.setEssenceCost(15, 2);
		lungsUpgrades.setWeights(UNCOMMON, COMMON);
		lungsUpgrades.setComponents(
				new ItemStack[] { new ItemStack(component, 2, 1), new ItemStack(component, 2, 8) },
				new ItemStack[] { new ItemStack(component, 1, 1), new ItemStack(component, 2, 8), new ItemStack(component, 1, 9) }
				);
		
		lowerOrgansUpgrades = new ItemLowerOrgansUpgrade("lowerOrgansUpgrades", EnumSlot.LOWER_ORGANS,
				new String[] { "liverFilter", "metabolic", "battery", "adrenaline" });
		lowerOrgansUpgrades.setEssenceCost(5, 5, 10, 5);
		lowerOrgansUpgrades.setWeights(UNCOMMON, COMMON, VERY_COMMON, UNCOMMON);
		lowerOrgansUpgrades.setComponents(
				new ItemStack[] { new ItemStack(component, 3, 1), new ItemStack(component, 2, 8) },
				new ItemStack[] { new ItemStack(component, 3, 1), new ItemStack(component, 1, 3), new ItemStack(component, 1, 9) },
				new ItemStack[] { new ItemStack(component, 1, 1), new ItemStack(component, 2, 8), new ItemStack(component, 3, 9) },
				new ItemStack[] { new ItemStack(component, 1, 0), new ItemStack(component, 2, 1) }
				);
		
		skinUpgrades = new ItemSkinUpgrade("skinUpgrades", EnumSlot.SKIN,
				new String[] { "solarSkin", "subdermalSpikes", "fakeSkin", "immuno"}); // Solar changed from 15 0.2.0
		skinUpgrades.setEssenceCost(12, 12, 0, -25);
		skinUpgrades.setWeights(VERY_COMMON, UNCOMMON, UNCOMMON, RARE);
		skinUpgrades.setComponents(
				new ItemStack[] { new ItemStack(component, 1, 1), new ItemStack(component, 1, 4), new ItemStack(component, 2, 5), new ItemStack(component, 1, 9) },
				new ItemStack[] { new ItemStack(component, 1, 0), new ItemStack(component, 2, 2), new ItemStack(component, 1, 4), new ItemStack(component, 1, 9) },
				new ItemStack[] { new ItemStack(component, 1, 1), new ItemStack(component, 3, 4), new ItemStack(component, 2, 5) },
				new ItemStack[] { new ItemStack(component, 3, 1), new ItemStack(component, 1, 2), new ItemStack(component, 1, 7), new ItemStack(component, 1, 8), new ItemStack(component, 1, 9) }
				);
		
		muscleUpgrades = new ItemMuscleUpgrade("muscleUpgrades", EnumSlot.MUSCLE,
				new String[] { "wiredReflexes", "muscleReplacements" });
		muscleUpgrades.setEssenceCost(5, 15);
		muscleUpgrades.setWeights(UNCOMMON, RARE);
		muscleUpgrades.setComponents(
				new ItemStack[] { new ItemStack(component, 1, 3), new ItemStack(component, 1, 5), new ItemStack(component, 3, 7) },
				new ItemStack[] { new ItemStack(component, 3, 0), new ItemStack(component, 1, 2), new ItemStack(component, 2, 5) }
				);

		boneUpgrades = new ItemBoneUpgrade("boneUpgrades", EnumSlot.BONE,
				new String[] { "bonelacing", "boneflex", "bonebattery" });
		boneUpgrades.setEssenceCost(3, 5);
		boneUpgrades.setWeights(UNCOMMON, RARE, UNCOMMON);
		boneUpgrades.setComponents(
				new ItemStack[] { new ItemStack(component, 1, 1), new ItemStack(component, 2, 2), new ItemStack(component, 2, 6) },
				new ItemStack[] { new ItemStack(component, 3, 1), new ItemStack(component, 2, 2), new ItemStack(component, 2, 8) },
				new ItemStack[] { new ItemStack(component, 2, 1), new ItemStack(component, 2, 8), new ItemStack(component, 1, 9) }
				);
		
		armUpgrades = new ItemArmUpgrade("armUpgrades", EnumSlot.ARM,
				new String[] { "bow"  });
		armUpgrades.setEssenceCost(3);
		armUpgrades.setWeights(RARE);
		armUpgrades.setComponents(
				new ItemStack[] { new ItemStack(component, 4, 0), new ItemStack(component, 2, 4) });
		
		handUpgrades = new ItemHandUpgrade("handUpgrades", EnumSlot.HAND,
				new String[] { "craftHands", "claws", "mining" });
		handUpgrades.setEssenceCost(2, 2, 1);
		handUpgrades.setWeights(RARE, RARE, RARE);
		handUpgrades.setComponents(
				new ItemStack[] { new ItemStack(component, 4, 0), new ItemStack(component, 1, 3), new ItemStack(component, 1, 4) },
				new ItemStack[] { new ItemStack(component, 2, 0), new ItemStack(component, 2, 2), new ItemStack(component, 1, 4), new ItemStack(component, 1, 6), new ItemStack(component, 2, 8) },
				new ItemStack[] { new ItemStack(component, 2, 0), new ItemStack(component, 1, 2), new ItemStack(component, 1, 4), new ItemStack(component, 2, 6)}
				);
		
		legUpgrades = new ItemLegUpgrade("legUpgrades", EnumSlot.LEG,
				new String[] { "jumpBoost", "fallDamage" });
		legUpgrades.setEssenceCost(3, 2);
		legUpgrades.setWeights(RARE, RARE);
		legUpgrades.setComponents(
				new ItemStack[] { new ItemStack(component, 2, 0), new ItemStack(component, 2, 2) },
				new ItemStack[] { new ItemStack(component, 3, 2), new ItemStack(component, 1, 4), new ItemStack(component, 1, 5) });
		
		footUpgrades = new ItemFootUpgrade("footUpgrades", EnumSlot.FOOT,
				new String[] { "spurs", "aqua", "wheels" });
		footUpgrades.setEssenceCost(1, 2, 3);
		footUpgrades.setWeights(UNCOMMON, RARE, UNCOMMON);
		footUpgrades.setComponents(
				new ItemStack[] { new ItemStack(component, 1, 0), new ItemStack(component, 1, 2), new ItemStack(component, 1, 4) },
				new ItemStack[] { new ItemStack(component, 2, 0), new ItemStack(component, 1, 2), new ItemStack(component, 1, 9) },
				new ItemStack[] { new ItemStack(component, 2, 0), new ItemStack(component, 2, 9) });

		cyberlimbs = new ItemCyberlimb("cyberlimbs", 
				new EnumSlot[] { EnumSlot.ARM, EnumSlot.ARM, EnumSlot.LEG, EnumSlot.LEG },
				new String[] { "cyberarmLeft", "cyberarmRight", "cyberlegLeft", "cyberlegRight" });
		cyberlimbs.setEssenceCost(25, 25, 25, 25);
		cyberlimbs.setComponents(
				new ItemStack[] { new ItemStack(component, 4, 0), new ItemStack(component, 2, 2), new ItemStack(component, 2, 4), new ItemStack(component, 1, 5), new ItemStack(component, 1, 7) },
				new ItemStack[] { new ItemStack(component, 4, 0), new ItemStack(component, 2, 2), new ItemStack(component, 2, 4), new ItemStack(component, 1, 5), new ItemStack(component, 1, 7) },
				new ItemStack[] { new ItemStack(component, 4, 0), new ItemStack(component, 2, 2), new ItemStack(component, 2, 4), new ItemStack(component, 1, 5), new ItemStack(component, 1, 7) },
				new ItemStack[] { new ItemStack(component, 4, 0), new ItemStack(component, 2, 2), new ItemStack(component, 2, 4), new ItemStack(component, 1, 5), new ItemStack(component, 1, 7) }
				);
		
		
		ItemStack actuator = new ItemStack(component, 1, 0);
		ItemStack reactor = new ItemStack(component, 1, 1);
		ItemStack reinforcement = new ItemStack(component, 1, 2);
		ItemStack circuit = new ItemStack(component, 1, 3);
		ItemStack plating = new ItemStack(component, 1, 4);
		ItemStack fiber = new ItemStack(component, 1, 5);
		ItemStack material = new ItemStack(component, 1, 6);
		ItemStack nerves = new ItemStack(component, 1, 7);
		ItemStack storage = new ItemStack(component, 1, 8);
		ItemStack cell = new ItemStack(component, 1, 9);

		
		
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(surgeryChamber.ib),
				"III",
				"IBI",
				"IDI",
				Character.valueOf('I'), "ingotIron", Character.valueOf('B'), "blockIron", Character.valueOf('D'), new ItemStack(Items.IRON_DOOR)
				));
		
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(charger),
				"IFI",
				"IRI",
				"III",
				Character.valueOf('I'), "ingotIron", Character.valueOf('R'), "blockRedstone", Character.valueOf('F'), new ItemStack(Blocks.IRON_BARS)
				));
		
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(scanner),
				"IEI",
				"IDI",
				"III",
				Character.valueOf('I'), "ingotIron", Character.valueOf('D'), "gemDiamond", Character.valueOf('E'), new ItemStack(cybereyes, 1, OreDictionary.WILDCARD_VALUE)
				));
		
		ItemStack salvagedEye = new ItemStack(cybereyes);
		salvagedEye = cybereyes.setQuality(salvagedEye, CyberwareAPI.QUALITY_SCAVENGED);
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(scanner),
				"IEI",
				"IDI",
				"III",
				Character.valueOf('I'), "ingotIron", Character.valueOf('D'), "gemDiamond", Character.valueOf('E'), salvagedEye
				));
		
		GameRegistry.addRecipe(new BlueprintCraftingHandler());
		GameRegistry.addRecipe(new ShapelessOreRecipe(new ItemStack(Items.PAPER),
				new ItemStack(blueprint, 1, OreDictionary.WILDCARD_VALUE)
				));
		
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(engineering.ib),
				" PI",
				"III",
				"ICI",
				Character.valueOf('I'), "ingotIron", Character.valueOf('P'), new ItemStack(Blocks.PISTON), Character.valueOf('C'), new ItemStack(Blocks.CRAFTING_TABLE)
				));
		
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(blueprintArchive),
				"PPP",
				"AAA",
				"PPP",
				Character.valueOf('P'), "ingotIron", Character.valueOf('A'), "paper"
				));
		
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(componentBox),
				" O ",
				"ICI",
				" I ",
				Character.valueOf('I'), "ingotIron", Character.valueOf('C'), "chestWood", Character.valueOf('O'), new ItemStack(component, 1, OreDictionary.WILDCARD_VALUE)
				));
		
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(radio),
				"F  ",
				"III",
				"ICI",
				Character.valueOf('I'), "ingotIron", Character.valueOf('F'), fiber.copy(),
				Character.valueOf('C'), circuit.copy()
				));
		
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(radioLarge),
				"IEI",
				"PRP",
				"ICI",
				Character.valueOf('I'), "ingotIron", Character.valueOf('R'), new ItemStack(radio), Character.valueOf('E'), new ItemStack(Items.ENDER_EYE),
				Character.valueOf('P'), reinforcement.copy(), Character.valueOf('C'), circuit.copy()
				));
		
		
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(radioPost, 6),
				"B B",
				"BFB",
				"BPB",
				Character.valueOf('B'), new ItemStack(Blocks.IRON_BARS), Character.valueOf('P'), plating.copy(),
				Character.valueOf('F'), fiber.copy()
				));
		
		GameRegistry.addRecipe(new CyberwareDyingHandler());
		
		if (CyberwareConfig.SURGERY_CRAFTING)
		{
			GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(surgeryApparatus),
					"III",
					"IRI",
					"PSA",
					Character.valueOf('I'), "ingotIron", Character.valueOf('R'), "blockRedstone", Character.valueOf('P'), new ItemStack(Items.DIAMOND_PICKAXE), Character.valueOf('S'), new ItemStack(Items.DIAMOND_SWORD), Character.valueOf('A'), new ItemStack(Items.DIAMOND_AXE)
					));
		}
		
		if (Loader.isModLoaded("Botania"))
		{
			BotaniaIntegration.preInit();
		}
		
		if (Loader.isModLoaded("ToughAsNails"))
		{
			ToughAsNailsIntegration.preInit();
		}
	}
	
	public static void postInit()
	{
		
		if (!CyberwareConfig.NO_ZOMBIES)
		{
			List<Biome> biomes = new ArrayList<Biome>();
			
			for (ResourceLocation key : Biome.REGISTRY.getKeys())
			{
				Biome biome = Biome.REGISTRY.getObject(key);
				for (SpawnListEntry entry : biome.getSpawnableList(EnumCreatureType.MONSTER))
				{
					if (entry.entityClass == EntityZombie.class)
					{
						biomes.add(biome);
					}
				}
			}
			EntityRegistry.addSpawn(EntityCyberZombie.class, CyberwareConfig.ZOMBIE_WEIGHT, CyberwareConfig.ZOMBIE_MIN_PACK, CyberwareConfig.ZOMBIE_MAX_PACK, EnumCreatureType.MONSTER, biomes.toArray(new Biome[0]));
		}

	}
	

	public static class NumItems extends WeightedRandom.Item
	{
		public int num;
		public NumItems(int weight, int num)
		{
			super(weight);
			this.num = num;
		}

		@Override
		public boolean equals(Object target)
		{
			return target instanceof NumItems && num == ((NumItems)target).num;
		}
	}



}
