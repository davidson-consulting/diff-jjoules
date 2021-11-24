package fr.davidson.diff.jjoules;

import fr.davidson.diff.jjoules.util.Constants;
import org.junit.jupiter.api.Test;

public class Playground {
    @Test
    void test() {
        System.out.println(Constants.FILE_SEPARATOR);
        System.out.println(Constants.PATH_SEPARATOR);
    }

    //    @Test
//    void test() {
//        Launcher launcher = new Launcher();
//        launcher.addInputResource("src/test/java/fr/davidson/diff/jjoules/Playground.java");
//        launcher.buildModel();
//        final Factory factory = launcher.getFactory();
//        final CtType<?> aType = factory.Type().get("fr.davidson.diff.jjoules.Playground");
//
//        // type du field
//        final CtTypeReference<?> reference = factory.Type().createReference("fr.davidson.diff.jjoules.Playground");
//        final CtField<?> myField = factory.Field().create(
//                aType, // type qui contiendra le nouveau field
//                Collections.singleton(ModifierKind.STATIC), // modifiers public/static/final/etc
//                reference, // type du field
//                "myField" // nom du field
//                // eventuellement tu peux créer une valeur par défault en dernier paramètre
//        );
//        System.out.println(aType);
//    }
}