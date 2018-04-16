package websockets;

import database.Position;

public class PositionEdit {
	private PositionController positionController;
	private Position p;

	public PositionEdit(PositionController positionController, int id, boolean loesch) {
		this.positionController = positionController;
		p = positionController.getPosition(new Long(id));
		if (p != null) {
			if (p.isLoesch() == loesch) {
				p.setLoesch(!loesch);
				positionController.update(p);
			} else {
				System.out.println("loesch fehler");
			}

		} else {
			System.out.println("pos unknown");

		}

	}

	public boolean getLoesch() {
		if (p != null)
			return p.isLoesch();
		return false;
	}

}
