package wraith.waystones.util;

import com.google.gson.*;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.LiteralText;
import net.minecraft.util.Identifier;
import org.apache.logging.log4j.Logger;
import wraith.waystones.Waystones;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public final class Config {
    private static Config instance = null;

    public NbtCompound configData;
    private final Logger LOGGER = Waystones.LOGGER;
    private static final String CONFIG_FILE = "config/waystones/config.json";

    private Config() {
    }

    public static Config getInstance() {
        if (instance == null) {
            instance = new Config();
        }
        return instance;
    }

    public boolean generateInVillages() {
        return configData.getBoolean("generate_in_villages");
    }

    public boolean canOwnersRedeemPayments() {
        return configData.getBoolean("can_owners_redeem_payments");
    }

    public Identifier teleportCostItem() {
        if ("item".equals(configData.getString("cost_type"))) {
            String[] item = configData.getString("cost_item").split(":");
            return (item.length == 2) ? new Identifier(item[0], item[1]) : new Identifier(item[0]);
        }
        return null;
    }

    public boolean storeWaystoneNbt() {
        return true;
    }

    public String teleportType() {
        return configData.getString("cost_type");
    }

    public int teleportCost() {
        return Math.abs(configData.getInt("cost_amount"));
    }

    public float getHardness() {
        return configData.getFloat("waystone_block_hardness");
    }

    public int getMiningLevel() {
        return configData.getInt("waystone_block_required_mining_level");
    }

    public boolean consumeInfiniteScroll() {
        return configData.getBoolean("consume_infinite_knowledge_scroll_on_use");
    }

    private NbtCompound getDefaults() {
        NbtCompound defaultConfig = new NbtCompound();

        defaultConfig.putBoolean("generate_in_villages", true);
        defaultConfig.putBoolean("consume_infinite_knowledge_scroll_on_use", false);
        defaultConfig.putBoolean("can_owners_redeem_payments", false);
        defaultConfig.putBoolean("store_waystone_data_on_sneak_break", true);
        defaultConfig.putInt("cost_amount", 1);
        defaultConfig.putString("cost_type", "level");
        defaultConfig.putString("cost_item", "minecraft:ender_pearl");
        defaultConfig.putFloat("waystone_block_hardness", 4F);
        defaultConfig.putInt("waystone_block_required_mining_level", 1);

        NbtCompound recipesTag = new NbtCompound();

        HashMap<String, String> itemMap = new HashMap<>();
        itemMap.put("S", "minecraft:stone");
        itemMap.put("A", "waystones:abyss_watcher");
        itemMap.put("O", "minecraft:obsidian");
        itemMap.put("E", "minecraft:emerald");
        recipesTag.putString("waystone", Utils.createRecipe("SAS_SES_SOS", itemMap, "waystones:waystone", 1).toString());

        itemMap.clear();
        itemMap.put("S", "minecraft:sandstone");
        itemMap.put("A", "waystones:abyss_watcher");
        itemMap.put("O", "minecraft:obsidian");
        itemMap.put("E", "minecraft:emerald");
        recipesTag.putString("desert_waystone", Utils.createRecipe("SAS_SES_SOS", itemMap, "waystones:desert_waystone", 1).toString());

        itemMap.clear();
        itemMap.put("S", "#minecraft:stone_bricks");
        itemMap.put("A", "waystones:abyss_watcher");
        itemMap.put("O", "minecraft:obsidian");
        itemMap.put("E", "minecraft:emerald");
        recipesTag.putString("stone_brick_waystone", Utils.createRecipe("SAS_SES_SOS", itemMap, "waystones:stone_brick_waystone", 1).toString());

        itemMap.clear();
        itemMap.put("A", "waystones:abyss_watcher");
        itemMap.put("S", "minecraft:nether_star");
        itemMap.put("P", "minecraft:blaze_powder");
        recipesTag.putString("pocket_wormhole", Utils.createRecipe(" A _PSP_ P ", itemMap, "waystones:pocket_wormhole", 1).toString());

        itemMap.clear();
        itemMap.put("E", "minecraft:ender_pearl");
        itemMap.put("F", "minecraft:flint");
        recipesTag.putString("abyss_watcher", Utils.createRecipe("FEF", itemMap, "waystones:abyss_watcher", 1).toString());

        itemMap.clear();
        itemMap.put("P", "minecraft:paper");
        itemMap.put("S", "minecraft:stick");
        recipesTag.putString("waystone_scroll", Utils.createRecipe("SPS_PPP_SPS", itemMap, "waystones:waystone_scroll", 1).toString());

        itemMap.clear();
        itemMap.put("A", "waystones:abyss_watcher");
        itemMap.put("B", "minecraft:blaze_powder");
        recipesTag.putString("local_void", Utils.createRecipe(" B _BAB_ B ", itemMap, "waystones:local_void", 1).toString());

        recipesTag.putString("scroll_of_infinite_knowledge", "none");

        defaultConfig.put("recipes", recipesTag);

        return defaultConfig;
    }

    private JsonObject toJson(NbtCompound tag) {
        boolean overwrite = false;
        JsonObject json = new JsonObject();

        NbtCompound defaults = getDefaults();

        boolean generateInVillages;
        if (tag.contains("generate_in_villages")) {
            generateInVillages = tag.getBoolean("generate_in_villages");
        } else {
            overwrite = true;
            generateInVillages = defaults.getBoolean("generate_in_villages");
        }
        json.addProperty("generate_in_villages", generateInVillages);

        boolean consumeInfiniteScroll;
        if (tag.contains("consume_infinite_knowledge_scroll_on_use")) {
            consumeInfiniteScroll = tag.getBoolean("consume_infinite_knowledge_scroll_on_use");
        } else {
            overwrite = true;
            consumeInfiniteScroll = defaults.getBoolean("consume_infinite_knowledge_scroll_on_use");
        }
        json.addProperty("consume_infinite_knowledge_scroll_on_use", consumeInfiniteScroll);

        boolean storeWaystoneDataOnBreak;
        if (tag.contains("store_waystone_data_on_sneak_break")) {
            storeWaystoneDataOnBreak = tag.getBoolean("store_waystone_data_on_sneak_break");
        } else {
            overwrite = true;
            storeWaystoneDataOnBreak = defaults.getBoolean("store_waystone_data_on_sneak_break");
        }
        json.addProperty("store_waystone_data_on_sneak_break", storeWaystoneDataOnBreak);

        boolean canOnwersRedeem;
        if (tag.contains("can_owners_redeem_payments")) {
            canOnwersRedeem = tag.getBoolean("can_owners_redeem_payments");
        } else {
            overwrite = true;
            canOnwersRedeem = defaults.getBoolean("can_owners_redeem_payments");
        }
        json.addProperty("can_owners_redeem_payments", canOnwersRedeem);

        int costAmount;
        if (tag.contains("cost_amount")) {
            costAmount = tag.getInt("cost_amount");
        } else {
            overwrite = true;
            costAmount = defaults.getInt("cost_amount");
        }
        json.addProperty("cost_amount", costAmount);

        String costType;
        if (tag.contains("cost_type")) {
            costType = tag.getString("cost_type").toLowerCase();
        } else {
            overwrite = true;
            costType = defaults.getString("cost_type");
        }
        json.addProperty("cost_type", costType);

        String costItem;
        if (tag.contains("cost_item")) {
            costItem = tag.getString("cost_item").toLowerCase();
        } else {
            overwrite = true;
            costItem = defaults.getString("cost_item");
        }
        json.addProperty("cost_item", costItem);

        int blockHardness;
        if (tag.contains("waystone_block_hardness")) {
            blockHardness = tag.getInt("waystone_block_hardness");
        } else {
            overwrite = true;
            blockHardness = defaults.getInt("waystone_block_hardness");
        }
        json.addProperty("waystone_block_hardness", blockHardness);

        float miningLevel;
        if (tag.contains("waystone_block_required_mining_level")) {
            miningLevel = tag.getFloat("waystone_block_required_mining_level");
        } else {
            overwrite = true;
            miningLevel = defaults.getFloat("waystone_block_required_mining_level");
        }
        json.addProperty("waystone_block_required_mining_level", miningLevel);

        JsonObject recipesJson = new JsonObject();
        NbtCompound recipesTag = tag.getCompound("recipes");
        NbtCompound defaultRecipes = defaults.getCompound("recipes");
        for (String recipe : defaultRecipes.getKeys()) {
            String recipeString;
            if (recipesTag.contains(recipe)) {
                recipeString = recipesTag.getString(recipe);
            } else {
                overwrite = true;
                recipeString = defaultRecipes.getString(recipe);
            }
            recipesJson.addProperty(recipe, recipeString);
        }
        json.add("recipes", recipesJson);
        createFile(json, overwrite);
        return json;
    }

    private NbtCompound toNbtCompound(JsonObject json) {
        boolean overwrite = false;
        NbtCompound tag = new NbtCompound();

        NbtCompound defaults = getDefaults();

        boolean generateInVillages;
        if (json.has("generate_in_villages")) {
            generateInVillages = json.get("generate_in_villages").getAsBoolean();
        } else {
            overwrite = true;
            generateInVillages = defaults.getBoolean("generate_in_villages");
        }
        tag.putBoolean("generate_in_villages", generateInVillages);

        boolean consumeInfiniteScroll;
        if (json.has("consume_infinite_knowledge_scroll_on_use")) {
            consumeInfiniteScroll = json.get("consume_infinite_knowledge_scroll_on_use").getAsBoolean();
        } else {
            overwrite = true;
            consumeInfiniteScroll = defaults.getBoolean("consume_infinite_knowledge_scroll_on_use");
        }
        tag.putBoolean("consume_infinite_knowledge_scroll_on_use", consumeInfiniteScroll);

        boolean storeWaystoneDataOnBreak;
        if (json.has("store_waystone_data_on_sneak_break")) {
            storeWaystoneDataOnBreak = json.get("store_waystone_data_on_sneak_break").getAsBoolean();
        } else {
            overwrite = true;
            storeWaystoneDataOnBreak = defaults.getBoolean("store_waystone_data_on_sneak_break");
        }
        tag.putBoolean("store_waystone_data_on_sneak_break", storeWaystoneDataOnBreak);

        boolean canOnwersRedeem;
        if (json.has("can_owners_redeem_payments")) {
            canOnwersRedeem = json.get("can_owners_redeem_payments").getAsBoolean();
        } else {
            overwrite = true;
            canOnwersRedeem = defaults.getBoolean("can_owners_redeem_payments");
        }
        tag.putBoolean("can_owners_redeem_payments", canOnwersRedeem);

        int costAmount;
        if (json.has("cost_amount")) {
            costAmount = json.get("cost_amount").getAsInt();
        } else {
            overwrite = true;
            costAmount = defaults.getInt("cost_amount");
        }
        tag.putInt("cost_amount", costAmount);

        String costItem;
        if (json.has("cost_item")) {
            costItem = json.get("cost_item").getAsString();
        } else {
            overwrite = true;
            costItem = defaults.getString("cost_item");
        }
        tag.putString("cost_item", costItem);

        String costType;
        if (json.has("cost_type")) {
            costType = json.get("cost_type").getAsString();
        } else {
            overwrite = true;
            costType = defaults.getString("cost_type");
        }
        tag.putString("cost_type", costType);

        int blockHardness;
        if (json.has("waystone_block_hardness")) {
            blockHardness = json.get("waystone_block_hardness").getAsInt();
        } else {
            overwrite = true;
            blockHardness = defaults.getInt("waystone_block_hardness");
        }
        tag.putInt("waystone_block_hardness", blockHardness);

        float miningLevel;
        if (json.has("waystone_block_required_mining_level")) {
            miningLevel = json.get("waystone_block_required_mining_level").getAsFloat();
        } else {
            overwrite = true;
            miningLevel = defaults.getFloat("waystone_block_required_mining_level");
        }
        tag.putFloat("waystone_block_required_mining_level", miningLevel);

        JsonObject recipesJson = json.get("recipes").getAsJsonObject();
        NbtCompound recipesTag = new NbtCompound();

        NbtCompound defaultRecipes = defaults.getCompound("recipes");
        for (String recipe : defaultRecipes.getKeys()) {
            String recipeString;
            if (recipesJson.has(recipe)) {
                recipeString = recipesJson.get(recipe).toString();
            } else {
                overwrite = true;
                recipeString = defaultRecipes.getString(recipe);
            }
            recipesTag.putString(recipe, recipeString);
        }
        tag.put("recipes", recipesTag);

        createFile(toJson(tag), overwrite);
        return tag;
    }

    public boolean loadConfig() {
        try {
            return loadConfig(getJsonObject(readFile(new File(CONFIG_FILE))));
        } catch (Exception e) {
            LOGGER.info("Found error with config. Using default config.");
            this.configData = getDefaults();
            createFile(toJson(this.configData), true);
            return false;
        }
    }

    private boolean loadConfig(JsonObject fileConfig) {
        try {
            this.configData = toNbtCompound(fileConfig);
            return true;
        } catch (Exception e) {
            LOGGER.info("Found error with config. Using default config.");
            this.configData = getDefaults();
            createFile(toJson(this.configData), true);
            return false;
        }
    }

    public boolean loadConfig(NbtCompound config) {
        try {
            this.configData = config;
            return true;
        } catch (Exception e) {
            LOGGER.info("Found error with config. Using default config.");
            this.configData = getDefaults();
            createFile(toJson(this.configData), true);
            return false;
        }
    }

    private void createFile(JsonObject contents, boolean overwrite) {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        contents = new JsonParser().parse(gson.toJson(contents)).getAsJsonObject();

        StringBuilder recipes = new StringBuilder();
        if (contents != null && contents.has("recipes")) {
            for (Map.Entry<String, JsonElement> recipe : contents.get("recipes").getAsJsonObject().entrySet()) {
                if ("none".equals(recipe.getValue().getAsString())) {
                    continue;
                }
                recipes.append("\"").append(recipe.getKey()).append("\": ")
                        .append(gson.toJson(new JsonParser().parse(recipe.getValue().getAsString()).getAsJsonObject()))
                        .append(",");
            }
            recipes = new StringBuilder(recipes.toString().replace("\n", "").replace("\r", ""));
            recipes = new StringBuilder(recipes.substring(0, recipes.length() - 1));
            recipes.append("}}");
            contents.remove("recipes");
        }

        File file = new File(Config.CONFIG_FILE);
        if (file.exists() && !overwrite) {
            return;
        }
        file.getParentFile().mkdirs();
        try {
            file.createNewFile();
        } catch (Exception e) {
            e.printStackTrace();
        }
        file.setReadable(true);
        file.setWritable(true);
        file.setExecutable(true);
        if (contents == null) {
            return;
        }
        try (FileWriter writer = new FileWriter(file)) {
            String json = gson.toJson(contents).replace("\n", "").replace("\r", "");
            if (!"".equals(recipes.toString())) {
                json = json.substring(0, json.length() - 1) + ",\"recipes\":{" + recipes;
            }
            writer.write(gson.toJson(new JsonParser().parse(json).getAsJsonObject()));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public NbtCompound toNbtCompound() {
        return configData;
    }

    public static String readFile(File file) throws FileNotFoundException {
        Scanner scanner = new Scanner(file);
        scanner.useDelimiter("\\Z");
        var result = scanner.next();
        scanner.close();
        return result;
    }

    public static JsonObject getJsonObject(String json) {
        return new JsonParser().parse(json).getAsJsonObject();
    }

    public HashMap<String, JsonElement> getRecipes() {
        JsonObject json = toJson(configData).get("recipes").getAsJsonObject();
        HashMap<String, JsonElement> recipes = new HashMap<>();
        for (Map.Entry<String, JsonElement> recipe : json.entrySet()) {
            String recipeString = recipe.getValue().getAsString();
            if ("none".equals(recipeString)) {
                continue;
            }
            recipes.put(recipe.getKey(), new JsonParser().parse(recipeString));
        }
        return recipes;
    }

    public void print(ServerPlayerEntity player) {
        for (Map.Entry<String, JsonElement> config : toJson(configData).entrySet()) {
            if (config.getValue().isJsonObject()) {
                continue;
            }
            player.sendMessage(new LiteralText("§6[§e" + config.getKey() + "§6] §3 " + config.getValue()), false);
        }
    }

}
