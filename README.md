# Hang

Use HOCON for translating Minecraft mods!

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

To make Hang work you have to make a mixin into the class "ClientLanguageMap". \
Then @Inject into the `<init>` method and call Hang's initialization method. \
\
An example mixin for 1.16.5 Forge:

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