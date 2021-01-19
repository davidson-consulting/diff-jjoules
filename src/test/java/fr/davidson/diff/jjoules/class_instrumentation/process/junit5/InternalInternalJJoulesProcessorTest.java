package fr.davidson.diff.jjoules.class_instrumentation.process.junit5;

import fr.davidson.diff.jjoules.class_instrumentation.process.JJoulesProcessor;
import org.junit.jupiter.api.Test;
import spoon.Launcher;
import spoon.reflect.code.CtLocalVariable;
import spoon.reflect.declaration.CtClass;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public class InternalInternalJJoulesProcessorTest {

    @Test
    void test() {
        final CtClass<?> ctClass = Launcher.parseClass("/*\n" +
                " * Copyright (C) 2008 Google Inc.\n" +
                " *\n" +
                " * Licensed under the Apache License, Version 2.0 (the \"License\");\n" +
                " * you may not use this file except in compliance with the License.\n" +
                " * You may obtain a copy of the License at\n" +
                " *\n" +
                " * http://www.apache.org/licenses/LICENSE-2.0\n" +
                " *\n" +
                " * Unless required by applicable law or agreed to in writing, software\n" +
                " * distributed under the License is distributed on an \"AS IS\" BASIS,\n" +
                " * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.\n" +
                " * See the License for the specific language governing permissions and\n" +
                " * limitations under the License.\n" +
                " */\n" +
                "\n" +
                "package com.google.gson.functional;\n" +
                "\n" +
                "import com.google.gson.ExclusionStrategy;\n" +
                "import com.google.gson.FieldAttributes;\n" +
                "import com.google.gson.Gson;\n" +
                "import com.google.gson.GsonBuilder;\n" +
                "import com.google.gson.JsonObject;\n" +
                "import com.google.gson.JsonPrimitive;\n" +
                "import java.lang.annotation.ElementType;\n" +
                "import java.lang.annotation.Retention;\n" +
                "import java.lang.annotation.RetentionPolicy;\n" +
                "import java.lang.annotation.Target;\n" +
                "import junit.framework.TestCase;\n" +
                "\n" +
                "/**\n" +
                " * Performs some functional tests when Gson is instantiated with some common user defined\n" +
                " * {@link ExclusionStrategy} objects.\n" +
                " *\n" +
                " * @author Inderjeet Singh\n" +
                " * @author Joel Leitch\n" +
                " */\n" +
                "public class ExclusionStrategyFunctionalTest extends TestCase {\n" +
                "  private static final ExclusionStrategy EXCLUDE_SAMPLE_OBJECT_FOR_TEST = new ExclusionStrategy() {\n" +
                "    @Override public boolean shouldSkipField(FieldAttributes f) {\n" +
                "      return false;\n" +
                "    }\n" +
                "    @Override public boolean shouldSkipClass(Class<?> clazz) {\n" +
                "      return clazz == SampleObjectForTest.class;\n" +
                "    }\n" +
                "  };\n" +
                "\n" +
                "  private SampleObjectForTest src;\n" +
                "\n" +
                "  @Override\n" +
                "  protected void setUp() throws Exception {\n" +
                "    super.setUp();\n" +
                "    src = new SampleObjectForTest();\n" +
                "  }\n" +
                "\n" +
                "  public void testExclusionStrategySerialization() throws Exception {\n" +
                "    Gson gson = createGson(new MyExclusionStrategy(String.class), true);\n" +
                "    String json = gson.toJson(src);\n" +
                "    assertFalse(json.contains(\"\\\"stringField\\\"\"));\n" +
                "    assertFalse(json.contains(\"\\\"annotatedField\\\"\"));\n" +
                "    assertTrue(json.contains(\"\\\"longField\\\"\"));\n" +
                "  }\n" +
                "\n" +
                "  public void testExclusionStrategySerializationDoesNotImpactDeserialization() {\n" +
                "    String json = \"{\\\"annotatedField\\\":1,\\\"stringField\\\":\\\"x\\\",\\\"longField\\\":2}\";\n" +
                "    Gson gson = createGson(new MyExclusionStrategy(String.class), true);\n" +
                "    SampleObjectForTest value = gson.fromJson(json, SampleObjectForTest.class);\n" +
                "    assertEquals(1, value.annotatedField);\n" +
                "    assertEquals(\"x\", value.stringField);\n" +
                "    assertEquals(2, value.longField);\n" +
                "  }\n" +
                "\n" +
                "  public void testExclusionStrategyDeserialization() throws Exception {\n" +
                "    Gson gson = createGson(new MyExclusionStrategy(String.class), false);\n" +
                "    JsonObject json = new JsonObject();\n" +
                "    json.add(\"annotatedField\", new JsonPrimitive(src.annotatedField + 5));\n" +
                "    json.add(\"stringField\", new JsonPrimitive(src.stringField + \"blah,blah\"));\n" +
                "    json.add(\"longField\", new JsonPrimitive(1212311L));\n" +
                "\n" +
                "    SampleObjectForTest target = gson.fromJson(json, SampleObjectForTest.class);\n" +
                "    assertEquals(1212311L, target.longField);\n" +
                "\n" +
                "    // assert excluded fields are set to the defaults\n" +
                "    assertEquals(src.annotatedField, target.annotatedField);\n" +
                "    assertEquals(src.stringField, target.stringField);\n" +
                "  }\n" +
                "\n" +
                "  public void testExclusionStrategySerializationDoesNotImpactSerialization() throws Exception {\n" +
                "    Gson gson = createGson(new MyExclusionStrategy(String.class), false);\n" +
                "    String json = gson.toJson(src);\n" +
                "    assertTrue(json.contains(\"\\\"stringField\\\"\"));\n" +
                "    assertTrue(json.contains(\"\\\"annotatedField\\\"\"));\n" +
                "    assertTrue(json.contains(\"\\\"longField\\\"\"));\n" +
                "  }\n" +
                "\n" +
                "  public void testExclusionStrategyWithMode() throws Exception {\n" +
                "    SampleObjectForTest testObj = new SampleObjectForTest(\n" +
                "        src.annotatedField + 5, src.stringField + \"blah,blah\",\n" +
                "        src.longField + 655L);\n" +
                "\n" +
                "    Gson gson = createGson(new MyExclusionStrategy(String.class), false);\n" +
                "    JsonObject json = gson.toJsonTree(testObj).getAsJsonObject();\n" +
                "    assertEquals(testObj.annotatedField, json.get(\"annotatedField\").getAsInt());\n" +
                "    assertEquals(testObj.stringField, json.get(\"stringField\").getAsString());\n" +
                "    assertEquals(testObj.longField, json.get(\"longField\").getAsLong());\n" +
                "\n" +
                "    SampleObjectForTest target = gson.fromJson(json, SampleObjectForTest.class);\n" +
                "    assertEquals(testObj.longField, target.longField);\n" +
                "\n" +
                "    // assert excluded fields are set to the defaults\n" +
                "    assertEquals(src.annotatedField, target.annotatedField);\n" +
                "    assertEquals(src.stringField, target.stringField);\n" +
                "  }\n" +
                "\n" +
                "  public void testExcludeTopLevelClassSerialization() {\n" +
                "    Gson gson = new GsonBuilder()\n" +
                "        .addSerializationExclusionStrategy(EXCLUDE_SAMPLE_OBJECT_FOR_TEST)\n" +
                "        .create();\n" +
                "    assertEquals(\"null\", gson.toJson(new SampleObjectForTest(), SampleObjectForTest.class));\n" +
                "  }\n" +
                "\n" +
                "  public void testExcludeTopLevelClassSerializationDoesNotImpactDeserialization() {\n" +
                "    Gson gson = new GsonBuilder()\n" +
                "        .addSerializationExclusionStrategy(EXCLUDE_SAMPLE_OBJECT_FOR_TEST)\n" +
                "        .create();\n" +
                "    String json = \"{\\\"annotatedField\\\":1,\\\"stringField\\\":\\\"x\\\",\\\"longField\\\":2}\";\n" +
                "    SampleObjectForTest value = gson.fromJson(json, SampleObjectForTest.class);\n" +
                "    assertEquals(1, value.annotatedField);\n" +
                "    assertEquals(\"x\", value.stringField);\n" +
                "    assertEquals(2, value.longField);\n" +
                "  }\n" +
                "\n" +
                "  public void testExcludeTopLevelClassDeserialization() {\n" +
                "    Gson gson = new GsonBuilder()\n" +
                "        .addDeserializationExclusionStrategy(EXCLUDE_SAMPLE_OBJECT_FOR_TEST)\n" +
                "        .create();\n" +
                "    String json = \"{\\\"annotatedField\\\":1,\\\"stringField\\\":\\\"x\\\",\\\"longField\\\":2}\";\n" +
                "    SampleObjectForTest value = gson.fromJson(json, SampleObjectForTest.class);\n" +
                "    assertNull(value);\n" +
                "  }\n" +
                "\n" +
                "  public void testExcludeTopLevelClassDeserializationDoesNotImpactSerialization() {\n" +
                "    Gson gson = new GsonBuilder()\n" +
                "        .addDeserializationExclusionStrategy(EXCLUDE_SAMPLE_OBJECT_FOR_TEST)\n" +
                "        .create();\n" +
                "    String json = gson.toJson(new SampleObjectForTest(), SampleObjectForTest.class);\n" +
                "    assertTrue(json.contains(\"\\\"stringField\\\"\"));\n" +
                "    assertTrue(json.contains(\"\\\"annotatedField\\\"\"));\n" +
                "    assertTrue(json.contains(\"\\\"longField\\\"\"));\n" +
                "  }\n" +
                "\n" +
                "  private static Gson createGson(ExclusionStrategy exclusionStrategy, boolean serialization) {\n" +
                "    GsonBuilder gsonBuilder = new GsonBuilder();\n" +
                "    if (serialization) {\n" +
                "      gsonBuilder.addSerializationExclusionStrategy(exclusionStrategy);\n" +
                "    } else {\n" +
                "      gsonBuilder.addDeserializationExclusionStrategy(exclusionStrategy);\n" +
                "    }\n" +
                "    return gsonBuilder\n" +
                "        .serializeNulls()\n" +
                "        .create();\n" +
                "  }\n" +
                "\n" +
                "  @Retention(RetentionPolicy.RUNTIME)\n" +
                "  @Target({ElementType.FIELD})\n" +
                "  private static @interface Foo {\n" +
                "    // Field tag only annotation\n" +
                "  }\n" +
                "\n" +
                "  private static class SampleObjectForTest {\n" +
                "    @Foo\n" +
                "    private final int annotatedField;\n" +
                "    private final String stringField;\n" +
                "    private final long longField;\n" +
                "\n" +
                "    public SampleObjectForTest() {\n" +
                "      this(5, \"someDefaultValue\", 12345L);\n" +
                "    }\n" +
                "\n" +
                "    public SampleObjectForTest(int annotatedField, String stringField, long longField) {\n" +
                "      this.annotatedField = annotatedField;\n" +
                "      this.stringField = stringField;\n" +
                "      this.longField = longField;\n" +
                "    }\n" +
                "  }\n" +
                "\n" +
                "  private static final class MyExclusionStrategy implements ExclusionStrategy {\n" +
                "    private final Class<?> typeToSkip;\n" +
                "\n" +
                "    private MyExclusionStrategy(Class<?> typeToSkip) {\n" +
                "      this.typeToSkip = typeToSkip;\n" +
                "    }\n" +
                "\n" +
                "    @Override public boolean shouldSkipClass(Class<?> clazz) {\n" +
                "      return (clazz == typeToSkip);\n" +
                "    }\n" +
                "\n" +
                "    @Override public boolean shouldSkipField(FieldAttributes f) {\n" +
                "      return f.getAnnotation(Foo.class) != null;\n" +
                "    }\n" +
                "  }\n" +
                "}\n");
        final Map<String, List<String>> testsList = new HashMap<>();
        testsList.put("A", Collections.singletonList("testExclusionStrategyWithMode"));
        final JJoulesProcessor jjoulesProcessor = new JJoulesProcessor(testsList, "");
        jjoulesProcessor.process(ctClass.getMethodsByName("testExclusionStrategyWithMode").get(0));
        final CtClass<?> instrumentedCtType = ctClass.getFactory().Class().get(ctClass.getQualifiedName() + "_" + "testExclusionStrategyWithMode");
        System.out.println(instrumentedCtType);
    }

    @Test
    void testJUnit3() {
        final Map<String, List<String>> testsList = new HashMap<>();
        testsList.put("A", Collections.singletonList("testToBeProcessed"));
        final JJoulesProcessor jjoulesProcessor = new JJoulesProcessor(testsList, "");
        final CtClass<?> ctClass = Launcher.parseClass(
                "class A extends TestCase {" +
                        " void m() {" +
                        " System.out.println(\"yeah\");" +
                        "} " +
                        "public void testToBeProcessed() {\n" +
                        "InternalClass c = new InternalClass();" +
                        "}" +
                        "public void testNoToBeProcessed() {\n" +
                        "}" +
                        "private static class InternalClass {" +
                        "" +
                        "}" +
                        "}"
        );
        jjoulesProcessor.process(ctClass.getMethodsByName("testToBeProcessed").get(0));
        final CtClass<?> instrumentedCtType = ctClass.getFactory().Class().get(ctClass.getQualifiedName() + "_" + "testToBeProcessed");
        assertEquals(10, instrumentedCtType.getMethods().stream().filter(method -> method.getSimpleName().startsWith("testToBeProcessed")).count());
        assertTrue(instrumentedCtType.getMethodsByName("noToBeProcessed").isEmpty());
        assertNotNull(instrumentedCtType.getMethodsByName("m").get(0));
        /*assertNotNull(instrumentedCtType.getMethodsByName("init").get(0));
        assertNotNull(instrumentedCtType.getMethodsByName("cleanUp").get(0));*/
        assertEquals("A_testToBeProcessed$InternalClass",
                ((CtLocalVariable<?>) instrumentedCtType.getMethodsByName("testToBeProcessed")
                        .get(0)
                        .getBody()
                        .getStatement(0)
                ).getType().getQualifiedName()
        );
        System.out.println(instrumentedCtType);
    }

    @Test
    void testJUnit45() {
        final Map<String, List<String>> testsList = new HashMap<>();
        testsList.put("A", Collections.singletonList("toBeProcessed"));
        final JJoulesProcessor jjoulesProcessor = new JJoulesProcessor(testsList, "");
        final CtClass<?> ctClass = Launcher.parseClass(
                "class A {" +
                        " void m() {" +
                        " System.out.println(\"yeah\");" +
                        "} " +
                        "@Test\n" +
                        "public void toBeProcessed() {\n" +
                        "InternalClass c = new InternalClass();" +
                        "}" +
                        "@Test\n" +
                        "public void noToBeProcessed() {\n" +
                        "}" +
                        "private static class InternalClass {" +
                        "" +
                        "}" +
                        "}"
        );
        jjoulesProcessor.process(ctClass.getMethodsByName("toBeProcessed").get(0));
        final CtClass<?> instrumentedCtType = ctClass.getFactory().Class().get(ctClass.getQualifiedName() + "_" + "toBeProcessed");
        assertNotNull(instrumentedCtType);
        assertEquals("A_toBeProcessed", instrumentedCtType.getSimpleName());
        assertEquals(10, instrumentedCtType.getMethods().stream().filter(method -> method.getSimpleName().startsWith("toBeProcessed")).count());
        assertTrue(instrumentedCtType.getMethodsByName("noToBeProcessed").isEmpty());
        assertNotNull(instrumentedCtType.getMethodsByName("m").get(0));
        assertNotNull(instrumentedCtType.getMethodsByName("init").get(0));
        assertNotNull(instrumentedCtType.getMethodsByName("cleanUp").get(0));
        assertEquals("A_toBeProcessed$InternalClass",
                ((CtLocalVariable<?>) instrumentedCtType.getMethodsByName("toBeProcessed")
                        .get(0)
                        .getBody()
                        .getStatement(0)
                ).getType().getQualifiedName()
        );
    }
}
