package me.prisonranksx.utilities;

import java.util.function.Supplier;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public class SyncIf {

	private boolean b;
	private static final JavaPlugin PLUGIN = JavaPlugin.getProvidingPlugin(SyncIf.class);

	public static boolean get(Supplier<Boolean> supplier) {
		SyncIf si = new SyncIf();
		return si.syncIf(supplier);
	}

	public boolean syncIf(Supplier<Boolean> supplier) {
		Bukkit.getScheduler().runTask(PLUGIN, () -> b = supplier.get());
		return b;
	}

}
