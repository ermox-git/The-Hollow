# The Hollow (Fabric, Minecraft 1.21.1)

A small horror mod. Requires Fabric Loader 0.16+, Fabric API, Java 21.

## What's in it
- **The Watcher**: a tall mob that spawns in dark places. It freezes while you look at it
  or while you hold a Ward Lantern within 10 blocks. It is immune to all damage except the Veilcutter
  (and /kill).
- **Hollow Sigil**: rare crafting item.
  `S E S / E N E / S E S` with S = netherite scrap, E = echo shard, N = nether star.
- **Veilcutter**: netherite sword + Hollow Sigil + netherite ingot (shapeless). The only weapon that hurts the Watcher.
- **Ward Lantern**: soul lantern + glowstone dust + amethyst shard (shapeless).
- **Dread events**: occasional footsteps, knocking, and whispers when you are in the dark. Sounds only.
- Test with the Watcher Spawn Egg (creative menu) or `/summon thehollow:watcher`.

## Build without installing anything (GitHub)
1. Make a free GitHub repo and upload the contents of this folder.
   The hidden `.github` folder must be included. If the web uploader skips it, use
   Add file > Create new file, name it `.github/workflows/build.yml`, and paste the file's contents.
2. Open the Actions tab, run "build", then download the `thehollow-jar` artifact.
3. Put the jar in `.minecraft/mods` next to Fabric API.

## Build locally
Install JDK 21 and Gradle 8.10, then run `gradle build`. The jar appears in `build/libs/`.
