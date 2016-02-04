package simulizer.ui.layout;

import simulizer.ui.interfaces.WindowEnum;

public class Layouts {
	// TODO: Save/Load layout locations
	// TODO: Find a way to scale to window size
	
	public static Layout original() {
		WindowLocation[] defaultLayout = new WindowLocation[3];
		defaultLayout[0] = new WindowLocation(WindowEnum.CODE_EDITOR, 20, 35, 400, 685);
		defaultLayout[1] = new WindowLocation(WindowEnum.CPU_VISUALISER, 440, 35, 600, 400);
		defaultLayout[2] = new WindowLocation(WindowEnum.REGISTERS, 440, 440, 600, 280);
		return new Layout(defaultLayout);
	}

	public static Layout alternative() {
		WindowLocation[] altLayout = new WindowLocation[4];
		altLayout[0] = new WindowLocation(WindowEnum.CODE_EDITOR, 5, 35, 1303, 974);
		altLayout[1] = new WindowLocation(WindowEnum.REGISTERS, 1315, 428, 600, 185);
		altLayout[2] = new WindowLocation(WindowEnum.CPU_VISUALISER, 1315, 35, 600, 380);
		altLayout[3] = new WindowLocation(WindowEnum.LOGGER, 1315, 625, 600, 380);
		return new Layout(altLayout);
	}
}
