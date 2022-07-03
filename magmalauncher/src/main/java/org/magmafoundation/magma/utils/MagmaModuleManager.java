/*
 * MohistMC
 * Copyright (C) 2018-2022.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 */

package org.magmafoundation.magma.utils;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.module.*;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.AccessControlContext;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Project: Magma
 *
 * @author Malcolm (M1lc0lm)
 * @date 03.07.2022 - 17:19
 *
 * Made with help of Shawizz
 */
public class MagmaModuleManager {

	private static final MethodHandles.Lookup IMPL_LOOKUP = Unsafe.lookup();

	List<Module> loadedModules = new ArrayList<>();
	private boolean moduleOptionAvailable = false;

	public MagmaModuleManager(List<String> args) {
		try {
			/*
			This code allows to call methods in the jdk.internal.module.Modules class.
			If this fails, methods won't be able to be called, but modules can still be loaded.
			 */
			sun.misc.Unsafe unsafe = this.getUnsafe();
			unsafe.putObject(MagmaModuleManager.class, unsafe.objectFieldOffset(Class.class.getDeclaredField("module")), Class.class.getModule());
			this.moduleOptionAvailable = true;

			this.applyLaunchArgs(args);
		} catch (Exception e) {
			e.printStackTrace();
			/*
			Adding module options cannot be done dynamically, but we can still add modules dynamically.
			The other way to add module options is run the server jar again with the needed flags.
			 */
		}
	}

	public void applyLaunchArgs(List<String> args) {
		//Just read each lines of launch args
		for(String arg : args) {
			if(arg.startsWith("-p ")) {
				try {
					loadModules(arg.substring(2).trim());
				} catch (Throwable ignored) {}
			} else if(arg.startsWith("--add-opens")) {
				String option = arg.split("--add-opens ")[1];
				this.addOpens(option.split("/")[0], option.split("/")[1].split("=")[0], option.split("=")[1]);
			} else if(arg.startsWith("--add-exports")) {
				String option = arg.split("--add-exports ")[1];
				this.addExports(option.split("/")[0], option.split("/")[1].split("=")[0], option.split("=")[1]);
			} else if(arg.startsWith("-D")) {
				String[] params = arg.split("=");
				if (params.length == 2) {
					System.setProperty(params[0].replace("-D", ""), params[1]);
				}
			}
		}

		this.addExportsToAllUnnamed("cpw.mods.bootstraplauncher", "cpw.mods.bootstraplauncher");
	}


	/*
	Find and get a module by its name in the current module layer and the modules that are dynamically loaded.
	 */
	public Optional<Module> findModule(String name) {
		return Stream.concat(this.getDefaultModuleLayer().modules().stream(), this.loadedModules.stream()).filter(module -> module.getName().equals(name)).findAny();
	}

	public boolean addOpens(String moduleName, String packageName, String applyTo) {
		return this.addModuleOption("addOpens", moduleName, packageName, applyTo);
	}

	public boolean addExports(String moduleName, String packageName, String applyTo) {
		return this.addModuleOption("addExports", moduleName, packageName, applyTo);
	}

	public boolean addExportsToAllUnnamed(String moduleName, String packageName) {
		return this.addModuleOption("addExportsToAllUnnamed", moduleName, packageName, null);
	}

	/*
	Methods that should be used: addExports, addExportsToAllUnnamed, addOpens, addOpensToAllUnnamed

	This method allows to dynamically add a module option
	 */
	private boolean addModuleOption(String methodName, String moduleFrom, String packageName, String moduleTo) {
		try {
			Optional<Module> moduleFrom_ = findModule(moduleFrom);
			Optional<Module> moduleTo_ = findModule(moduleTo);
			if(!moduleFrom_.isPresent()) return false; //The module hasn't been found, we can't add the module option.

			//The target module has been found
			if(moduleTo_.isPresent()) {
				Class.forName("jdk.internal.module.Modules").getMethod(methodName, Module.class, String.class, Module.class).invoke(null, moduleFrom_.get(), packageName, moduleTo_.get());
			} else if(methodName.endsWith("Unnamed")) {
				//The target module hasn't been found, the only option is to use allUnnamed methods.
				Class.forName("jdk.internal.module.Modules").getMethod(methodName, Module.class, String.class).invoke(null, moduleFrom_.get(), packageName);
			}

			return true;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

	public static void addToPath(Path path) {
		try {
			ClassLoader loader = ClassLoader.getPlatformClassLoader();
			Field ucpField;
			try {
				ucpField = loader.getClass().getDeclaredField("ucp");
			} catch (NoSuchFieldException e) {
				ucpField = loader.getClass().getSuperclass().getDeclaredField("ucp");
			}
			long offset = Unsafe.objectFieldOffset(ucpField);
			Object ucp = Unsafe.getObject(loader, offset);
			if (ucp == null) {
				var cl = Class.forName("jdk.internal.loader.URLClassPath");
				var handle = Unsafe.lookup().findConstructor(cl, MethodType.methodType(void.class, URL[].class, AccessControlContext.class));
				ucp = handle.invoke(new URL[]{}, (AccessControlContext) null);
				Unsafe.putObjectVolatile(loader, offset, ucp);
			}
			Method method = ucp.getClass().getDeclaredMethod("addURL", URL.class);
			Unsafe.lookup().unreflect(method).invoke(ucp, path.toUri().toURL());
		} catch (Throwable t) {
			t.printStackTrace();
		}
	}

    //Code snipped from (https://github.com/IzzelAliz/Arclight/blob/f98046185ebfc183a242ac5497619dc35d741042/forge-installer/src/main/java/io/izzel/arclight/forgeinstaller/ForgeInstaller.java#L420)
	public void loadModules(String modulePath) throws Throwable{
		// Find all extra modules
		ModuleFinder finder = ModuleFinder.of(Arrays.stream(modulePath.split(OSUtil.getOS() == OSUtil.OS.WINDOWS ? ";" : ":")).map(Paths::get).peek(MagmaModuleManager::addToPath).toArray(Path[]::new));
		MethodHandle loadModuleMH = IMPL_LOOKUP.findVirtual(Class.forName("jdk.internal.loader.BuiltinClassLoader"), "loadModule", MethodType.methodType(void.class, ModuleReference.class));

		// Resolve modules to a new config
		Configuration config = Configuration.resolveAndBind(finder, List.of(ModuleLayer.boot().configuration()), finder, finder.findAll().stream().peek(mref -> {
			try {
				// Load all extra modules in system class loader (unnamed modules for now)
				loadModuleMH.invokeWithArguments(ClassLoader.getSystemClassLoader(), mref);
			} catch (Throwable throwable) {
				throw new RuntimeException(throwable);
			}
		}).map(ModuleReference::descriptor).map(ModuleDescriptor::name).collect(Collectors.toList()));

		// Copy the new config graph to boot module layer config
		MethodHandle graphGetter = IMPL_LOOKUP.findGetter(Configuration.class, "graph", Map.class);
		HashMap<ResolvedModule, Set<ResolvedModule>> graphMap = new HashMap<>((Map<ResolvedModule, Set<ResolvedModule>>) graphGetter.invokeWithArguments(config));
		MethodHandle cfSetter = IMPL_LOOKUP.findSetter(ResolvedModule.class, "cf", Configuration.class);
		// Reset all extra resolved modules config to boot module layer config
		graphMap.forEach((k, v) -> {
			try {
				cfSetter.invokeWithArguments(k, ModuleLayer.boot().configuration());
				v.forEach(m -> {
					try {
						cfSetter.invokeWithArguments(m, ModuleLayer.boot().configuration());
					} catch (Throwable throwable) {
						throw new RuntimeException(throwable);
					}
				});
			} catch (Throwable throwable) {
				throw new RuntimeException(throwable);
			}
		});
		graphMap.putAll((Map<ResolvedModule, Set<ResolvedModule>>) graphGetter.invokeWithArguments(ModuleLayer.boot().configuration()));
		IMPL_LOOKUP.findSetter(Configuration.class, "graph", Map.class).invokeWithArguments(ModuleLayer.boot().configuration(), new HashMap<>(graphMap));

		// Reset boot module layer resolved modules as new config resolved modules to prepare define modules
		Set<ResolvedModule> oldBootModules = ModuleLayer.boot().configuration().modules();
		MethodHandle modulesSetter = IMPL_LOOKUP.findSetter(Configuration.class, "modules", Set.class);
		HashSet<ResolvedModule> modulesSet = new HashSet<>(config.modules());
		modulesSetter.invokeWithArguments(ModuleLayer.boot().configuration(), new HashSet<>(modulesSet));

		// Prepare to add all of the new config "nameToModule" to boot module layer config
		MethodHandle nameToModuleGetter = IMPL_LOOKUP.findGetter(Configuration.class, "nameToModule", Map.class);
		HashMap<String, ResolvedModule> nameToModuleMap = new HashMap<>((Map<String, ResolvedModule>) nameToModuleGetter.invokeWithArguments(ModuleLayer.boot().configuration()));
		nameToModuleMap.putAll((Map<String, ResolvedModule>) nameToModuleGetter.invokeWithArguments(config));
		IMPL_LOOKUP.findSetter(Configuration.class, "nameToModule", Map.class).invokeWithArguments(ModuleLayer.boot().configuration(), new HashMap<>(nameToModuleMap));

		// Define all extra modules and add all of the new config "nameToModule" to boot module layer config
		((Map<String, Module>) IMPL_LOOKUP.findGetter(ModuleLayer.class, "nameToModule", Map.class).invokeWithArguments(ModuleLayer.boot())).putAll((Map<String, Module>) IMPL_LOOKUP.findStatic(Module.class, "defineModules", MethodType.methodType(Map.class, Configuration.class, Function.class, ModuleLayer.class)).invokeWithArguments(ModuleLayer.boot().configuration(), (Function<String, ClassLoader>) name -> ClassLoader.getSystemClassLoader(), ModuleLayer.boot()));

		// Add all of resolved modules
		modulesSet.addAll(oldBootModules);
		modulesSetter.invokeWithArguments(ModuleLayer.boot().configuration(), new HashSet<>(modulesSet));

		// Reset cache of boot module layer
		IMPL_LOOKUP.findSetter(ModuleLayer.class, "modules", Set.class).invokeWithArguments(ModuleLayer.boot(), null);
		IMPL_LOOKUP.findSetter(ModuleLayer.class, "servicesCatalog", Class.forName("jdk.internal.module.ServicesCatalog")).invokeWithArguments(ModuleLayer.boot(), null);

		// Add reads from extra modules to jdk modules
		MethodHandle implAddReadsMH = IMPL_LOOKUP.findVirtual(Module.class, "implAddReads", MethodType.methodType(void.class, Module.class));
		config.modules().forEach(rm -> ModuleLayer.boot().findModule(rm.name()).ifPresent(m -> oldBootModules.forEach(brm -> ModuleLayer.boot().findModule(brm.name()).ifPresent(bm -> {
			try {
				implAddReadsMH.invokeWithArguments(m, bm);
			} catch (Throwable throwable) {
				throw new RuntimeException(throwable);
			}
		}))));
	}

	/*
	Get the module layer (calling ModuleLayer.boot)
	 */
	private ModuleLayer getDefaultModuleLayer() {
		return ModuleLayer.boot();
	}

	/*
	Get the unsafe class instance
	 */
	public sun.misc.Unsafe getUnsafe() throws IllegalAccessException, NoSuchFieldException {
		Field f = sun.misc.Unsafe.class.getDeclaredField("theUnsafe");
		f.setAccessible(true);
		return (sun.misc.Unsafe) f.get(null);
	}
}

