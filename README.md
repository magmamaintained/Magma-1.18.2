<div align="center">
<img src="https://i.imgur.com/zTCTCWG.png" alt="Magma logo" align="middle"></img>

[![](https://img.shields.io/badge/Minecraft%20Forge-1.18.2%20--%2040.2.14%20--%209b2c52b3-orange.svg)](https://files.minecraftforge.net/net/minecraftforge/forge/index_1.18.2.html)
[![](https://img.shields.io/badge/Bukkit-1.18%20r2-blue)](https://hub.spigotmc.org/stash/projects/SPIGOT/repos/bukkit/browse)
[![](https://img.shields.io/badge/CraftBukkit-Build%20b02801aa-orange)](https://hub.spigotmc.org/stash/projects/SPIGOT/repos/craftbukkit/browse)
[![](https://img.shields.io/badge/Spigot-Build%20b0819150-yellow)](https://hub.spigotmc.org/stash/projects/SPIGOT/repos/spigot/browse)
[![](https://img.shields.io/discord/1175785262475923556.svg?logo=discord&logoWidth=18&colorB=7289DA)](https://discord.gg/STZjCzRZn9)
</div> 

## ❓ About

Magma is the next generation of hybrid minecraft server softwares.

Magma is based on **Forge and Spigot**, meaning it can run both **CraftBukkit/Spigot plugins and Forge mods**.

## 🌐 BungeeCord/Velocity

Magma 1.18.2 is **not** compatible with **vanilla** BungeeCord or any of its forks. This is **caused by Forge** and not a fault of Magma. We cannot fix this ourselves without modifying the client.
You might be able to use the Waterfall fork called [Lightfall](https://github.com/ArclightPowered/lightfall), but it also requires a clientside-mod in order to work and is not officially supported.

Magma 1.18.2 is **not** compatible with **vanilla** [Velocity](https://velocitypowered.com/downloads/). This is **caused by Forge** and not a fault of Magma. We cannot fix this ourselves without modifying the client and the proxy.

## 🧪 Other versions

- For 1.20.2 use [Ketting](https://github.com/kettingpowered)
- For 1.20.1 [here](https://github.com/magmamaintained/Magma-1.20.1)
- For 1.19.3 [here](https://github.com/magmamaintained/Magma-1.19.3)
- For 1.12.2 [here](https://github.com/magmamaintained/Magma-1.12.2)

## 🪣 Deployment

### Installation

1. Download the latest builds from the [**Releases** section](https://github.com/magmamaintained/Magma-1.18.2/releases/latest) (**Download** the one that ends in server)
2. Make a new directory (folder) for the server
3. Move the jar that you downloaded into the new directory
4. Run the jar with your command prompt or terminal, going to your directory and entering `java -jar Magma-[version]-server.jar`. Change [version] to your Magma version number.

### Building the sources

- Clone the Project
    - You can use Git GUI (like GitHub Desktop/GitKraken) or clone using the terminal using:
        - `git clone https://github.com/magmamaintained/Magma-1.18.2.git`
- Building
    - First you want to run the build command
        - `./gradlew setup magmaJar`
    - Now go and get a drink this may take some time
    - Navigate to `projects/magma/build/libs` directory of the compiled source code
    - Copy the Jar to a new server directory (see Installation)

## ⚙️ Contributing

If you wish to inspect Magma, submit PRs, or otherwise work with Magma itself, you're in the right place!.

Please read the [CONTRIBUTING.md](https://git.magmafoundation.org/magmafoundation/Magma-1-20-x/-/blob/1.20/CONTRIBUTING.md) to see how to contribute, setup, and run.

## 💬 Chat

You are welcome to visit Magma's Discord server [here](https://discord.gg/STZjCzRZn9) (recommended).

You could also go to Magma's subreddit [here](https://discord.gg/STZjCzRZn9).
