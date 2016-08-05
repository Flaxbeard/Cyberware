package flaxbeard.cyberware.client.integration.jei;

import mezz.jei.api.IJeiHelpers;
import mezz.jei.api.IJeiRuntime;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.IModRegistry;
import mezz.jei.api.JEIPlugin;

@JEIPlugin
public class JEICyberwarePlugin implements IModPlugin
{

	@Override
	public void register(IModRegistry registry)
	{
		System.out.println("Augmenting JEI");
		
		IJeiHelpers jeiHelpers = registry.getJeiHelpers();
		//registry.addRecipeCategories(recipeCategories);
	}

	@Override
	public void onRuntimeAvailable(IJeiRuntime jeiRuntime)
	{
		// TODO Auto-generated method stub
		
	}

}
