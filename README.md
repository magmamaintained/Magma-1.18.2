<div align="center">
<img src="https://i.imgur.com/zTCTCWG.png" alt="Magma logo" align="middle"></img>

[![](https://img.shields.io/badge/Minecraft%20Forge-1.18.2%20--%2040.1.86%20--%201087e26-orange.svg)](https://files.minecraftforge.net/net/minecraftforge/forge/index_1.18.2.html)
[![](https://img.shields.io/badge/Bukkit-1.18%20r2-blue)](https://hub.spigotmc.org/stash/projects/SPIGOT/repos/bukkit/browse)
[![](https://img.shields.io/badge/CraftBukkit-Build%20b02801aa-orange)](https://hub.spigotmc.org/stash/projects/SPIGOT/repos/craftbukkit/browse?at=refs%2Fheads%2Fversion%2F1.16.5)
[![](https://img.shields.io/badge/Spigot-Build%20b0819150-yellow)](https://hub.spigotmc.org/stash/projects/SPIGOT/repos/spigot/browse)
![TeamCity build status](https://teamcity.magmafoundation.org/app/rest/builds/buildType:id:MagmaFoundation_Magma118x_Build/statusIcon.svg)
[![](https://img.shields.io/discord/612695539729039411.svg?logo=discord&logoWidth=18&colorB=7289DA)](https://discord.gg/magma)
</div>

## ❓ About

Magma is the next generation of hybrid minecraft server softwares.

Magma is based on **Forge and Paper**, meaning it can run both **Craftbukkit/Spigot/Paper plugins and Forge mods**.

We hope to eliminate all issues with craftbukkit forge servers. In the end, we envision a seamless, low lag Magma experience with support for newer 1.12+ versions of Minecraft.

## 🌐 BungeeCord/Velocity

Magma 1.18 is **not** compatible with **vanilla** BungeeCord or any of its forks. This is **caused by Forge** and not a fault of Magma. We cannot fix this ourselves without modifying the client.
You might be able to use the Waterfall fork called [Lightfall](https://github.com/ArclightPowered/lightfall), but it also requires a clientside-mod in order to work and is not officially supported.

You can use **our [Velocity fork](https://git.magmafoundation.org/magmafoundation/Velocity)** with our [clientside mod](https://git.magmafoundation.org/magmafoundation/Forge-Client-Reset-Packet/-/tree/1.18). Every player needs to install this mod in order to join the server!

Magma 1.18 is **not** compatible with **vanilla** [Velocity](https://velocitypowered.com/downloads/). This is **caused by Forge** and not a fault of Magma. We cannot fix this ourselves without modifying the client and the proxy.

## 🧪 Magma for 1.12+/1.16+

Magma for Minecraft 1.12 and above can be found in their own repositories.

- Click [here](https://git.magmafoundation.org/magmafoundation/Magma) to visit the 1.12 repository.
- Click [here](https://git.magmafoundation.org/magmafoundation/Magma-1-16-x) to visit the 1.16 repository. (1.16 is End Of Life and not actively maintained anymore)

## 🪣 Deployment

### Installation

1. Download the recommended builds from the [**Releases** section](https://git.magmafoundation.org/magmafoundation/Magma-1-18-x/-/releases) (**Download** the one that ends in server) 
   1. Or Download the latest jar from [Magma Site](https://magmafoundation.org/)
2. Make a new directory (folder) for the server
3. Move the jar that you downloaded into the new directory
4. Run the jar with your command prompt or terminal, going to your directory and entering `java -jar Magma-[version]-server.jar`. Change [version] to your Magma version number.

### Building the sources

- Clone the Project
    - You can use Git GUI (like GitHub Desktop/GitKraken) or clone using the terminal using:
        - `git clone http://git.magmafoundation.org/magmafoundation/Magma-1-18-x.git`
- Building
    - First you want to run the build command
        - `./gradlew setup magmaJar`
    - Now go and get a drink this may take some time
    - Navigate to `projects/magma/build/libs` directory of the compiled source code
    - Copy the Jar to a new server directory (see Installation)
    
## ⚙️ Contributing

If you wish to inspect Magma, submit PRs, or otherwise work with Magma itself, you're in the right place!.

Please read the [CONTRIBUTING.md](https://git.magmafoundation.org/magmafoundation/Magma-1-18-x/-/blob/1.18.x/CONTRIBUTING.md) to see how to contribute, setup, and run.

## 💬 Chat

You are welcome to visit Magma's Discord server [here](https://discord.gg/Magma) (recommended).

You could also go to Magma's subreddit [here](https://www.reddit.com/r/Magma).

## 👥 Partners

![YourKit-Logo](https://www.yourkit.com/images/yklogo.png)

[YourKit](http://www.yourkit.com/), makers of the outstanding java profiler, support open source projects of all kinds with their full featured [Java](https://www.yourkit.com/java/profiler/index.jsp) and [.NET](https://www.yourkit.com/.net/profiler/index.jsp) application profilers.
