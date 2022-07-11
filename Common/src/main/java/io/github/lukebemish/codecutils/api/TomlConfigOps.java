package io.github.lukebemish.codecutils.api;

import com.electronwill.nightconfig.core.Config;
import com.electronwill.nightconfig.toml.TomlFormat;
import io.github.lukebemish.codecutils.impl.NightConfigOps;

public class TomlConfigOps extends NightConfigOps {
    public static final TomlConfigOps INSTANCE = new TomlConfigOps();

    @Override
    protected Config newConfig() {
        return TomlFormat.newConfig();
    }
}
