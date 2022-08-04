[hang]: https://dev.gloryx.net/api/badge/latest/main/net/gloryx/hang?color=40c14a&name=Hang&prefix=v
[download]: #download


# Hang

Use HOCON for translating Minecraft mods!

## Make it work with your mod

To make Hang work, you first have to make mixin into the class "ClientLanguageMap" (can be different in fabric and other
versions). \
Then @ModifyArg into the `func_239497_a` (the name can be different on fabric and other versions) at the invocation of
the ClientLanguageMap's constructor and change the map parameter as shown in the example down here.

###### An example mixin for 1.16.5 Forge:

```java
// ClientLanguageMapMixin.java
@Mixin(ClientLanguageMap.class)
public class ClientLanguageMapMixin {
    @ModifyArg(
            method = "func_239497_a_",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/resources/ClientLanguageMap;<init>(Ljava/util/Map;Z)V"
            ),
            index = 0
    )
    private static Map<String, String> updateMap(Map<String, String> map) {
        return new Hang("YOUR_MOD_ID", YourModClass.class.getClassLoader()).init(map).preferJson(false).get();
    }
}
```

## Download

Add our repository: https://dev.gloryx.net/main \
Then add the dependency, and replace `${hangVersion}` with the suitable version of Hang:

###### Gradle:

```kotlin
implementation("net.gloryx:hang:${hangVersion}")
```

###### Maven:

```xml

<dependency>
    <groupId>net.gloryx</groupId>
    <artifactId>hang</artifactId>
    <version>${hangVersion}</version>
</dependency>
```

**!!! YOU DON'T HAVE TO !!!** \
\
@Deprecated\
~~**!!! IMPORTANT !!!**~~ \
~~You still need to create language .json-s (for cross-modloader compatibility),~~ \
~~and **add the property _hang_** with the language code of that language file (e.g. en_us)!~~ \
~~Otherwise this won't work.~~

~~Example en_us.json:~~

```json5
// {
  // "hang": "en_us"
// }
```
