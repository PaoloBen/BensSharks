package net.mcreator.sharks.configuration; // <--- CHANGED THIS LINE

import net.minecraftforge.common.ForgeConfigSpec;
import org.apache.commons.lang3.tuple.Pair;

public class SharkAggroConfig {
    public static final Common COMMON;
    public static final ForgeConfigSpec COMMON_SPEC;

    static {
        final Pair<Common, ForgeConfigSpec> specPair = new ForgeConfigSpec.Builder().configure(Common::new);
        COMMON_SPEC = specPair.getRight();
        COMMON = specPair.getLeft();
    }

    public static class Common {
        public final ForgeConfigSpec.BooleanValue enableDolphinAggro;
        public final ForgeConfigSpec.BooleanValue enableDolphinEating;

        public Common(ForgeConfigSpec.Builder builder) {
            builder.push("vanilla_settings");

            enableDolphinAggro = builder
                    .comment("If true, Dolphins will hunt and attack live Sardines.")
                    .define("enable_dolphin_aggro", true);

            enableDolphinEating = builder
                    .comment("If true, Dolphins will eat dropped Sardine items.")
                    .define("enable_dolphin_eating", true);

            builder.pop();
        }
    }
}