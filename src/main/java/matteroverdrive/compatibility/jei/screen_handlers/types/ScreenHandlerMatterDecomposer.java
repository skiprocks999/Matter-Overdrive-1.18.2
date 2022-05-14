package matteroverdrive.compatibility.jei.screen_handlers.types;

import java.util.List;

import matteroverdrive.client.screen.ScreenMatterDecomposer;
import matteroverdrive.compatibility.jei.screen_handlers.AbstractScreenHandler;
import net.minecraft.client.renderer.Rect2i;

public class ScreenHandlerMatterDecomposer extends AbstractScreenHandler<ScreenMatterDecomposer> {

	@Override
	public List<Rect2i> getGuiExtraAreas(ScreenMatterDecomposer screen) {
		List<Rect2i> rectangles = super.getGuiExtraAreas(screen);
		if (screen.menu.isExtended) {
			rectangles.add(new Rect2i(screen.getGuiRight(), screen.getGuiTop() + 33, 37, 143));
		}
		return rectangles;
	}

}
