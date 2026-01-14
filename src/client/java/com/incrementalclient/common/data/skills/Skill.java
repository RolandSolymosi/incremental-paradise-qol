package com.incrementalclient.common.data.skills;

import com.incrementalclient.common.data.World;
import dev.isxander.yacl3.api.NameableEnum;
import org.jetbrains.annotations.NotNull;

public interface Skill extends NameableEnum {
    String getName();
    @NotNull SkillCategory getCategory();
    // Note: getRealm not used right now; may be used later.
    @NotNull World.Realm getRealm();

    boolean isActiveUpgrade();
}
