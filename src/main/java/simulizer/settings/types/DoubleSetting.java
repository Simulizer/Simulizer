package simulizer.settings.types;

import simulizer.settings.SettingValue;

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
		if (lowBound == Double.NaN || highBound == Double.NaN)
			return true;
		else
			return lowBound <= value && value <= highBound;
	}

	@Override
	public String getSettingType() {
		return "Double";
	}

}
