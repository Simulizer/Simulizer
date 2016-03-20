package simulizer.settings.types;

import simulizer.settings.SettingType;
import simulizer.settings.SettingValue;

/**
 * Represents a double setting
 * 
 * @author Michael
 *
 */
public class DoubleSetting extends SettingValue<Double> {
	private double lowBound = Double.NaN, highBound = Double.NaN;

	public DoubleSetting(String jsonName, String humanName, String description) {
		super(jsonName, humanName, description, 0.0);
	}

	public DoubleSetting(String jsonName, String humanName, String description, double defaultValue) {
		super(jsonName, humanName, description, defaultValue);
	}

	public DoubleSetting(String jsonName, String humanName, String description, double defaultValue, double lowBound, double highBound) {
		super(jsonName, humanName, description, defaultValue);
		this.lowBound = lowBound;
		this.highBound = highBound;
	}

	@Override
	public boolean isValid(Double value) {
		if (Double.isNaN(lowBound) || Double.isNaN(highBound))
			return true;
		else
			return lowBound <= value && value <= highBound;
	}

	@Override
	public SettingType getSettingType() {
		return SettingType.DOUBLE;
	}

	public double getLowBound() {
		return lowBound;
	}

	public double getHighBound() {
		return highBound;
	}

}
