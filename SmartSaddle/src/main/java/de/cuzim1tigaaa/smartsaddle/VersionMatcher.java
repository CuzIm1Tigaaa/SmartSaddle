package de.cuzim1tigaaa.smartsaddle;

import lombok.Getter;
import org.bukkit.Bukkit;

import java.util.*;

@Getter
public class VersionMatcher {

	private static final Map<String, String> VERSION_TO_REVISION = new HashMap<>() {
		{
			this.put("1.20", "1_21_1");
			this.put("1.20.1", "1_21_1");
			this.put("1.20.2", "1_21_1");
			this.put("1.20.3", "1_21_1");
			this.put("1.20.4", "1_21_1");
			this.put("1.20.5", "1_21_1");
			this.put("1.21", "1_21_1");
			this.put("1.21.1", "1_21_1");
			this.put("1.21.2", "1_21_1");
			this.put("1.21.3", "1_21_1");
			this.put("1.21.4", "1_21_4");
		}
	};
	/* This needs to be updated to reflect the newest available version wrapper */
	private static final String FALLBACK_REVISION = "1_21_1";

	@Getter
	private static boolean isNautilusAvailable, isHappyGhastAvailable;

	public static void initAvailability() {
		final String version = Bukkit.getBukkitVersion().split("-")[0];

		try {
			String[] parts = version.split("\\.");
			int major = Integer.parseInt(parts[1]);
			int minor = parts.length > 2 ? Integer.parseInt(parts[2]) : 0;

			isHappyGhastAvailable = major > 21 || (major == 21 && minor >= 6);
			isNautilusAvailable = major > 21 || (major == 21 && minor >= 11);
		}catch(Exception ignored) {
		}
	}

	public static EntityData match() {
		final String version = Bukkit.getBukkitVersion().split("-")[0];
		String rVersion = VERSION_TO_REVISION.getOrDefault(version, FALLBACK_REVISION);

		try {
			return (EntityData) Class.forName(VersionMatcher.class.getPackage().getName() + ".EntityData" + rVersion)
					.getDeclaredConstructor()
					.newInstance();
		}catch(ClassNotFoundException exception) {
			throw new IllegalStateException("SmartSaddle does not support server version \"" + rVersion + "\"", exception);
		}catch(ReflectiveOperationException exception) {
			throw new IllegalStateException("Failed to instantiate horse data for version " + rVersion, exception);
		}
	}
}