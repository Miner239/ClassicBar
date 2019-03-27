package tfar.classicbar.config;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.common.config.Config;
import net.minecraftforge.common.config.ConfigManager;
import net.minecraftforge.fml.client.event.ConfigChangedEvent;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent;
import tfar.classicbar.ClassicBar;

import static lumien.randomthings.asm.ClassTransformer.logger;
import static tfar.classicbar.config.IdiotHandler.idiots;

@Config(modid = ClassicBar.MODID)
public class ModConfig {

    @Config.Comment({"General Options"})
    public static ConfigGeneral general = new ConfigGeneral();

    @Config.Comment({"Color Options"})
    public static ConfigColors colors = new ConfigColors();

    @Config.Comment({"Number Options"})
    public static ConfigNumbers numbers = new ConfigNumbers();

    @Config.Comment({"Warnings"})
    public static ConfigWarnings warnings = new ConfigWarnings();

    public static class ConfigGeneral {
        @Config.Name("Bar Overlays")
        @Config.Comment("Tweak the bars themselves")
        public BarOverlays overlays = new BarOverlays();
        @Config.Name("Show Icons")
        @Config.Comment("Whether to show icons next to the bars")
        public boolean displayIcons = true;

        public class BarOverlays {

            @Config.Name("Hunger Bar Overlays")
            public HungerBarConfig hunger = new HungerBarConfig();

            @Config.Name("Display Armor Toughness Bar")
            @Config.RequiresMcRestart
            @Config.Comment("REQUIRES A RESTART TO APPLY!")
            public boolean displayToughnessBar = true;

            @Config.Name("Draw full absorption Bar")
            public boolean fullAbsorptionBar = false;

            public class HungerBarConfig {

                @Config.Name("Show Saturation Bar")
                public boolean showSaturationBar = true;

                @Config.Name("Show Held Food Overlay")
                public boolean showHeldFoodOverlay = true;

                @Config.Name("Show Exhaustion Overlay")
                public boolean showExhaustionOverlay = true;

                @Config.Name("Transistion speed of bar")
                @Config.RangeDouble(min = 0.001, max = .2)
                public float transitionSpeed = .02f;
            }
        }

    }

    public static class ConfigNumbers {
        @Config.Name("Percentage based")
        public boolean showPercent = false;

        @Config.Comment("Numbers info")

        @Config.Name("Show Numbers")
        public boolean showNumbers = true;
    }

    public static class ConfigColors {
        @Config.Name("Advanced Options")
        public AdvancedColors advancedColors = new AdvancedColors();
        @Config.Name("Hunger Bar Color")
        public String hungerBarColor = "#B34D00";
        @Config.Name("Oxygen Bar Color")
        public String oxygenBarColor = "#00E6E6";
        @Config.Name("Saturation Bar Color")
        public String saturationBarColor = "#FFCC00";
        @Config.Name("Absorption Bar Color")
        public String absorptionBarColor = "#D4AF37";

        public class AdvancedColors {
            @Config.Comment("Colors must be specified in #RRGGBB format")
            @Config.Name("Armor color values")
            public String[] armorColorValues = new String[]{"#AAAAAA", "#FF5500", "#FFC747", "#27FFE3", "#00FF00", "#7F00FF"};
            @Config.Name("Health fractions")
            public Float[] healthFractions = new Float[]{.25f, .5f, .75f};
            @Config.Name("Colors")
            public String[] hexColors = new String[]{"#FF0000", "#FFFF00", "#00FF00"};
        }
    }

    public static class ConfigWarnings {
        @Config.Name("Show Advanced Rocketry warning")
        @Config.Comment("Warning when advanced rocketry is installed")
        public boolean advancedRocketryWarning = true;

        @Config.Name("Show Rustic warning")
        @Config.Comment("Warning when Rustic is installed")
        public boolean rusticWarning = true;
    }

    @Mod.EventBusSubscriber(modid = ClassicBar.MODID)
    public static class ConfigEventHandler {

        @SubscribeEvent
        public static void onConfigChanged(ConfigChangedEvent.OnConfigChangedEvent event) {
            if (event.getModID().equals(ClassicBar.MODID)) {
                ConfigManager.sync(ClassicBar.MODID, Config.Type.INSTANCE);
                idiots.idiotsTryingToParseBadHexColorsDOTJpeg();
                idiots.emptyArrayFixer();
                logger.info("Syncing Classic Bar Configs");
            }
        }

        @SubscribeEvent
        public static void onPlayerJoin(PlayerEvent.PlayerLoggedInEvent e) {
            idiots.idiotsTryingToParseBadHexColorsDOTJpeg();
            EntityPlayer p = e.player;
            if (Loader.isModLoaded("advancedrocketry") && warnings.advancedRocketryWarning && general.overlays.displayToughnessBar) {

                p.sendMessage(new TextComponentString(TextFormatting.RED + "Toughness bar may not display correctly, change the placement in advanced rocketry config." +
                        " This is NOT a bug."));
            }
            if (Loader.isModLoaded("rustic") && warnings.rusticWarning) {
                p.sendMessage(new TextComponentString(TextFormatting.RED + "Armor bar may not display correctly, disable Rustic's extra armor overlay amd restart the game." +
                        " This is NOT a bug."));
            }
        }
    }
}