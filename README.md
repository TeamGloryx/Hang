# Hang

Use HOCON for translating Minecraft mods! \
Latest version: 0.1.1

## Dependency

Add our repository: https://dev.gloryx.net/main; \
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

## Make it work with your mod

To make Hang work, you first have to make mixin into the class "ClientLanguageMap" (can be different in fabric and other
versions). \
Then @Inject into the `<init>` method and call Hang's initialization method. \
\
###### An example mixin for 1.16.5 Forge:
```java

@Mixin(ClientLanguageMap.class)
public class ClientLanguageMapMixin {
    @Mutable
    @Shadow
    @Final
    private Map<String, String> field_239495_c_; // this is the MCP name for the property "map"

    @Inject(at = @At("TAIL"), method = "<init>")
    private void injectHang(Map<String, String> map, boolean bidirectional, CallbackInfo info) {
        this.field_239495_c_ = new Hang("YOUR_MOD_ID",
                YourModMainClass.class.getClassLoader())
                .init(map)
                .get();
    }
}
```

**!!! IMPORTANT !!!** \
You still need to create language .json-s (for cross-modloader compatibility), \
and **add the property _hang_** with the language code of that language file (e.g. en_us)! \
Otherwise this won't work.

Example en_us.json:
```json
{
  "hang": "en_us"
}
```