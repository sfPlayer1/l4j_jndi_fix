package to.player.l4j_jndi_fix;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.lang.reflect.Field;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.core.lookup.Interpolator;

import net.minecraftforge.fml.common.Mod;

@Mod("l4j_jndi_fix")
public class FmlMod {
	static {
		((org.apache.logging.log4j.core.LoggerContext) LogManager.getContext(false)).addPropertyChangeListener(new PropertyChangeListener() {
			@Override
			public void propertyChange(PropertyChangeEvent evt) {
				if (evt.getPropertyName().equals("config")) {
					apply();
				}
			}
		});

		apply();
	}

	@SuppressWarnings("unchecked")
	private static void apply() {
		try {
			Interpolator interpolator = (Interpolator) ((org.apache.logging.log4j.core.LoggerContext) LogManager.getContext(false)).getConfiguration().getStrSubstitutor().getVariableResolver();
			if (interpolator == null) return;

			boolean removed = false;

			for (Field field : Interpolator.class.getDeclaredFields()) {
				if (Map.class.isAssignableFrom(field.getType())) {
					field.setAccessible(true);
					removed = ((Map<String, ?>) field.get(interpolator)).remove("jndi") != null;
					if (removed) break;
				}
			}

			if (!removed) throw new RuntimeException("couldn't find jndi lookup entry");

			System.out.println("Removed JNDI lookup");
		} catch (Throwable t) {
			t.printStackTrace();
			Runtime.getRuntime().halt(1);
			throw new RuntimeException("application failed");
		}
	}
}
