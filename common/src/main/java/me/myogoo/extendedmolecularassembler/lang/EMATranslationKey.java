package me.myogoo.extendedmolecularassembler.lang;

import me.myogoo.myotus.client.MyoTranslateKey;

/**
 * Translation keys shared by EMA loader modules.
 *
 * <p>This enum intentionally stores only raw String keys. Do not add
 * game-specific or loader-specific value types here.</p>
 */
public enum EMATranslationKey implements MyoTranslateKey {
    ITEM_GROUP("itemGroup.extendedmolecularassembler");

    private final String key;

    EMATranslationKey(String key) {
        this.key = key;
    }

    @Override
    public String key() {
        return key;
    }

    public enum BLOCK implements MyoTranslateKey {
        EXTENDED_MOLECULAR_ASSEMBLER("block.extendedmolecularassembler.extended_molecular_assembler"),
        EX_EXTENDED_MOLECULAR_ASSEMBLER("block.extendedmolecularassembler.ex_extended_molecular_assembler"),
        BASIC_ME_CRAFTING_PROVIDER("block.extendedmolecularassembler.basic_me_crafting_provider"),
        ADVANCED_ME_CRAFTING_PROVIDER("block.extendedmolecularassembler.advanced_me_crafting_provider"),
        ELITE_ME_CRAFTING_PROVIDER("block.extendedmolecularassembler.elite_me_crafting_provider"),
        ULTIMATE_ME_CRAFTING_PROVIDER("block.extendedmolecularassembler.ultimate_me_crafting_provider"),
        EXTENDED_ASSEMBLER_MATRIX_PATTERN_CORE("block.extendedmolecularassembler.extended_assembler_matrix_pattern_core"),
        EXTENDED_ASSEMBLER_MATRIX_CRAFTING_CORE("block.extendedmolecularassembler.extended_assembler_matrix_crafting_core"),
        EXTENDED_ASSEMBLER_MATRIX_PATTERN_UPLOADER("block.extendedmolecularassembler.extended_assembler_matrix_pattern_uploader"),
        EXTENDED_ASSEMBLER_MATRIX_PATTERN_CORE_PLUS("block.extendedmolecularassembler.extended_assembler_matrix_pattern_core_plus"),
        EXTENDED_ASSEMBLER_MATRIX_CRAFTING_CORE_PLUS("block.extendedmolecularassembler.extended_assembler_matrix_crafting_core_plus"),
        EXTENDED_QUANTUM_CRAFTER("block.extendedmolecularassembler.extended_quantum_crafter"),
        RE_AVARITIA_SCULK_ME_CRAFTING_PROVIDER("block.extendedmolecularassembler.re_avaritia_sculk_me_crafting_provider"),
        RE_AVARITIA_NETHER_ME_CRAFTING_PROVIDER("block.extendedmolecularassembler.re_avaritia_nether_me_crafting_provider"),
        RE_AVARITIA_END_ME_CRAFTING_PROVIDER("block.extendedmolecularassembler.re_avaritia_end_me_crafting_provider"),
        XTREME_ME_CRAFTING_PROVIDER("block.extendedmolecularassembler.xtreme_me_crafting_provider");

        private final String key;

        BLOCK(String key) {
            this.key = key;
        }

        @Override
        public String key() {
            return key;
        }
    }

    public enum ITEM implements MyoTranslateKey {
        EXTENDED_PATTERN_ENCODING_TERMINAL("item.extendedmolecularassembler.extended_pattern_encoding_terminal"),
        WIRELESS_EXTENDED_PATTERN_ENCODING_TERMINAL("item.extendedmolecularassembler.wireless_extended_pattern_encoding_terminal"),
        EXTENDED_CRAFTING_PATTERN("item.extendedmolecularassembler.extended_crafting_pattern");

        private final String key;

        ITEM(String key) {
            this.key = key;
        }

        @Override
        public String key() {
            return key;
        }
    }

    public enum GUI implements MyoTranslateKey {
        EXTENDED_MOLECULAR_ASSEMBLER("gui.extendedmolecularassembler.extendedMolecularAssembler"),
        EXTENDED_MOLECULAR_ASSEMBLER_JOB("gui.extendedmolecularassembler.extendedMolecularAssembler.job"),
        EXTENDED_MOLECULAR_ASSEMBLER_NEXT_JOB("gui.extendedmolecularassembler.extendedMolecularAssembler.nextJob"),
        EXTENDED_MOLECULAR_ASSEMBLER_PREVIOUS_JOB("gui.extendedmolecularassembler.extendedMolecularAssembler.previousJob"),
        EXTENDED_MOLECULAR_ASSEMBLER_CLEAR_CURRENT_JOB("gui.extendedmolecularassembler.extendedMolecularAssembler.clearCurrentJob"),
        EXTENDED_PATTERN_ENCODING_TERMINAL("gui.extendedmolecularassembler.extendedPatternEncodingTerminal"),
        MATRIX_ASSEMBLER_MATRIX("gui.extendedmolecularassembler.matrix.assemblerMatrix"),
        MATRIX_OPEN_EXTENDED_PATTERNS("gui.extendedmolecularassembler.matrix.openExtendedPatterns"),
        MATRIX_EXTENDED_PATTERN_PAGE("gui.extendedmolecularassembler.matrix.extendedPatternPage"),
        MATRIX_SHARED_RESOURCES("gui.extendedmolecularassembler.matrix.sharedResources"),
        MATRIX_EXTENDED_PATTERNS("gui.extendedmolecularassembler.matrix.extendedPatterns"),
        MATRIX_BACK_TO_MATRIX("gui.extendedmolecularassembler.matrix.backToMatrix"),
        MATRIX_PATTERN_CORE("gui.extendedmolecularassembler.matrix.patternCore"),
        MATRIX_PATTERN_CORE_SHORT("gui.extendedmolecularassembler.matrix.patternCoreShort"),
        MATRIX_STORED_PATTERNS("gui.extendedmolecularassembler.matrix.storedPatterns"),
        MATRIX_ACTIVE_JOBS("gui.extendedmolecularassembler.matrix.activeJobs"),
        MATRIX_THREADS("gui.extendedmolecularassembler.matrix.threads"),
        MATRIX_CANCEL_JOBS("gui.extendedmolecularassembler.matrix.cancelJobs"),
        MATRIX_SEARCH_PATTERNS("gui.extendedmolecularassembler.matrix.searchPatterns"),
        MATRIX_SHOW_IN_PATTERN_ACCESS("gui.extendedmolecularassembler.matrix.showInPatternAccess"),
        MATRIX_HIDE_FROM_PATTERN_ACCESS("gui.extendedmolecularassembler.matrix.hideFromPatternAccess"),
        EXTENDED_PATTERN_ENCODING_TERMINAL_UPLOAD_TO_MATRIX("gui.extendedmolecularassembler.extendedPatternEncodingTerminal.uploadToMatrix"),
        EXTENDED_PATTERN_ENCODING_TERMINAL_CONFIG_TITLE(
                "gui.extendedmolecularassembler.config.extendedPatternEncodingTerminal.title"),
        EXTENDED_PATTERN_ENCODING_TERMINAL_REMEMBER_RECIPE_TYPE(
                "gui.extendedmolecularassembler.config.extendedPatternEncodingTerminal.rememberRecipeType");

        private final String key;

        GUI(String key) {
            this.key = key;
        }

        @Override
        public String key() {
            return key;
        }
    }

    public enum MESSAGE implements MyoTranslateKey {
        MATRIX_UPLOAD_NO_PATTERN("message.extendedmolecularassembler.matrix_upload.no_pattern"),
        MATRIX_UPLOAD_INVALID_PATTERN("message.extendedmolecularassembler.matrix_upload.invalid_pattern"),
        MATRIX_UPLOAD_NO_EXTENDEDAE("message.extendedmolecularassembler.matrix_upload.no_extendedae"),
        MATRIX_UPLOAD_NO_EXTENDEDAE_PLUS("message.extendedmolecularassembler.matrix_upload.no_extendedae_plus"),
        MATRIX_UPLOAD_NO_NETWORK("message.extendedmolecularassembler.matrix_upload.no_network"),
        MATRIX_UPLOAD_NO_MATRIX("message.extendedmolecularassembler.matrix_upload.no_matrix"),
        MATRIX_UPLOAD_DUPLICATE("message.extendedmolecularassembler.matrix_upload.duplicate"),
        MATRIX_UPLOAD_FULL("message.extendedmolecularassembler.matrix_upload.full"),
        MATRIX_UPLOAD_SUCCESS("message.extendedmolecularassembler.matrix_upload.success"),
        NO_MENU("message.extendedmolecularassembler.no_menu"),
        ME_CRAFTING_PROVIDER_STATUS("message.extendedmolecularassembler.me_crafting_provider.status");

        private final String key;

        MESSAGE(String key) {
            this.key = key;
        }

        @Override
        public String key() {
            return key;
        }
    }

    public enum TOOLTIP implements MyoTranslateKey {
        TABLE("tooltip.extendedmolecularassembler.table"),
        ME_CRAFTING_PROVIDER_EXPERT_MODE("tooltip.extendedmolecularassembler.me_crafting_provider.expert_mode"),
        TIERED_MODE_ENABLED("tooltip.extendedmolecularassembler.tiered_mode.enabled"),
        TIERED_MODE_LAST_REJECT("tooltip.extendedmolecularassembler.tiered_mode.last_reject"),
        TIERED_MODE_MISSING_PROVIDER("tooltip.extendedmolecularassembler.tiered_mode.missing_provider"),
        TIERED_MODE_OFFLINE_GRID("tooltip.extendedmolecularassembler.tiered_mode.offline_grid"),
        TIERED_MODE_UNSUPPORTED_TIER("tooltip.extendedmolecularassembler.tiered_mode.unsupported_tier"),
        ME_CRAFTING_PROVIDER_TIER("tooltip.extendedmolecularassembler.me_crafting_provider.tier"),
        ME_CRAFTING_PROVIDER_PROVIDES("tooltip.extendedmolecularassembler.me_crafting_provider.provides"),
        ME_CRAFTING_PROVIDER_REQUIREMENT("tooltip.extendedmolecularassembler.me_crafting_provider.requirement");

        private final String key;

        TOOLTIP(String key) {
            this.key = key;
        }

        @Override
        public String key() {
            return key;
        }
    }

    public enum TABLE implements MyoTranslateKey {
        UNKNOWN("table.extendedmolecularassembler.unknown"),
        MINECRAFT_CRAFTING_TABLE("table.extendedmolecularassembler.minecraft.crafting_table"),
        EXTENDEDCRAFTING_BASIC_TABLE("table.extendedmolecularassembler.extendedcrafting.basic_table"),
        EXTENDEDCRAFTING_ADVANCED_TABLE("table.extendedmolecularassembler.extendedcrafting.advanced_table"),
        EXTENDEDCRAFTING_ELITE_TABLE("table.extendedmolecularassembler.extendedcrafting.elite_table"),
        EXTENDEDCRAFTING_ULTIMATE_TABLE("table.extendedmolecularassembler.extendedcrafting.ultimate_table"),
        REAVARITIA_SCULK_TABLE("table.extendedmolecularassembler.reavaritia.sculk_table"),
        REAVARITIA_NETHER_TABLE("table.extendedmolecularassembler.reavaritia.nether_table"),
        REAVARITIA_END_TABLE("table.extendedmolecularassembler.reavaritia.end_table"),
        REAVARITIA_EXTREME_TABLE("table.extendedmolecularassembler.reavaritia.extreme_table"),
        AVARITIANEO_EXTREME_TABLE("table.extendedmolecularassembler.avaritianeo.extreme_table");

        private final String key;

        TABLE(String key) {
            this.key = key;
        }

        @Override
        public String key() {
            return key;
        }
    }

    public enum TIER implements MyoTranslateKey {
        BASIC("tier.extendedmolecularassembler.basic"),
        ADVANCED("tier.extendedmolecularassembler.advanced"),
        ELITE("tier.extendedmolecularassembler.elite"),
        ULTIMATE("tier.extendedmolecularassembler.ultimate"),
        RE_AVARITIA_SCULK("tier.extendedmolecularassembler.re_avaritia_sculk"),
        RE_AVARITIA_NETHER("tier.extendedmolecularassembler.re_avaritia_nether"),
        RE_AVARITIA_END("tier.extendedmolecularassembler.re_avaritia_end"),
        XTREME("tier.extendedmolecularassembler.xtreme");

        private final String key;

        TIER(String key) {
            this.key = key;
        }

        @Override
        public String key() {
            return key;
        }
    }
}
