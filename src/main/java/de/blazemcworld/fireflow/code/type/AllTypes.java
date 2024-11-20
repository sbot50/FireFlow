package de.blazemcworld.fireflow.code.type;

import java.util.Set;

public class AllTypes {

    public static final Set<WireType<?>> list = Set.of(
            ConditionType.INSTANCE,
            NumberType.INSTANCE,
            PlayerType.INSTANCE,
            SignalType.INSTANCE,
            StringType.INSTANCE,
            TextType.INSTANCE
    );

}
