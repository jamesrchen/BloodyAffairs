package ninja.jrc.bloodyaffairs.managers;

import ninja.jrc.bloodyaffairs.BloodyAffairs;
import ninja.jrc.bloodyaffairs.objects.CustomItem;
import ninja.jrc.bloodyaffairs.objects.enchants.EmptyEnchant;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.RecipeChoice;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class CustomItemManager {
    private final BloodyAffairs plugin;
    private final NamespacedKey itemKey;

    // Custom Items
    private final Map<CustomItem, ItemStack> customItems = new HashMap<>();

    private NamespacedKey denseStarRecipeKey;
    private ItemStack denseStar;

    private NamespacedKey ultraDenseStarRecipeKey;
    private ItemStack ultraDenseStar;

    private NamespacedKey reinforcedElytraRecipeKey;
    private ItemStack reinforcedElytra;

    public CustomItemManager(BloodyAffairs plugin){
        this.plugin = plugin;
        this.itemKey = new NamespacedKey(plugin, "BA");
        setupCustomItems();
    }

    private void setupCustomItems(){

        Enchantment emptyEnchant = Enchantment.getByKey(NamespacedKey.minecraft("emptyenchant"));
        if(emptyEnchant == null){
            try {
                Field f = Enchantment.class.getDeclaredField("acceptingNew");
                f.setAccessible(true);
                f.set(null, true);
            } catch (Exception e) {
                plugin.getLogger().severe(e.toString());
                plugin.getServer().getPluginManager().disablePlugin(plugin);
                return;
            }
            emptyEnchant = new EmptyEnchant();
            Enchantment.registerEnchantment(emptyEnchant);
        }
        // Dense star
        {
            denseStarRecipeKey = new NamespacedKey(plugin, "RECIPE_DENSE_STAR");
            plugin.getServer().removeRecipe(denseStarRecipeKey);

            denseStar = new ItemStack(Material.NETHER_STAR);
            ItemMeta itemMeta = denseStar.getItemMeta();
            itemMeta.getPersistentDataContainer().set(itemKey, PersistentDataType.STRING, "DENSE_STAR");
            itemMeta.setDisplayName("Dense Star");
            itemMeta.setLore(Arrays.asList("Bloodyaffairs", "Dense Star"));
            denseStar.setItemMeta(itemMeta);
            customItems.put(CustomItem.DENSE_STAR, denseStar);


            ShapedRecipe shapedRecipe = new ShapedRecipe(denseStarRecipeKey, denseStar);
            shapedRecipe.shape(" C ","C C"," C ");
            shapedRecipe.setIngredient('C', Material.NETHER_STAR);
            plugin.getServer().addRecipe(shapedRecipe);

        }

        // Ultra Dense Star
        {
            ultraDenseStarRecipeKey = new NamespacedKey(plugin, "RECIPE_ULTRA_DENSE_STAR");
            plugin.getServer().removeRecipe(ultraDenseStarRecipeKey);

            ultraDenseStar = new ItemStack(Material.NETHER_STAR);
            ItemMeta itemMeta = ultraDenseStar.getItemMeta();
            itemMeta.getPersistentDataContainer().set(itemKey, PersistentDataType.STRING, "ULTRA_DENSE_STAR");
            itemMeta.setDisplayName("Ultra Dense Star");
            itemMeta.setLore(Arrays.asList("Bloodyaffairs", "Ultra Dense Star"));
            ultraDenseStar.setItemMeta(itemMeta);

            ShapedRecipe shapedRecipe = new ShapedRecipe(ultraDenseStarRecipeKey, ultraDenseStar);
            shapedRecipe.shape(" C ","C C"," C ");
            shapedRecipe.setIngredient('C', new RecipeChoice.ExactChoice(denseStar));
            plugin.getServer().addRecipe(shapedRecipe);

        }

        // Reinforced Elytra
        {
            reinforcedElytraRecipeKey = new NamespacedKey(plugin, "RECIPE_REINFORCED_ELYTRA");
            plugin.getServer().removeRecipe(reinforcedElytraRecipeKey);

            reinforcedElytra = new ItemStack(Material.ELYTRA);
            ItemMeta itemMeta = reinforcedElytra.getItemMeta();
            itemMeta.getPersistentDataContainer().set(itemKey, PersistentDataType.STRING, "REINFORCED_ELYTRA");
            itemMeta.setDisplayName("Reinforced Elytra");
            itemMeta.addEnchant(emptyEnchant, 1, true);
            reinforcedElytra.setItemMeta(itemMeta);

            ShapedRecipe shapedRecipe = new ShapedRecipe(reinforcedElytraRecipeKey, reinforcedElytra);
            shapedRecipe.shape("IDI","DED","IDI");
            shapedRecipe.setIngredient('D', Material.DIAMOND);
            shapedRecipe.setIngredient('I', Material.IRON_INGOT);
            shapedRecipe.setIngredient('E', Material.ELYTRA);
            plugin.getServer().addRecipe(shapedRecipe);

        }

    }

    public ItemStack getDenseStar() {
        return denseStar;
    }

    public ItemStack getUltraDenseStar() {
        return ultraDenseStar;
    }

    public ItemStack getReinforcedElytra() {
        return reinforcedElytra;
    }
}