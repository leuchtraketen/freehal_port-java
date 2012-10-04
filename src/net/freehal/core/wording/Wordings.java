package net.freehal.core.wording;

public class Wordings {

	private static Wording wording = new FakeWording();

	public static Wording getWording() {
		return wording;
	}

	public static void setWording(Wording wording) {
		Wordings.wording = wording;
	}
}
