package me.pepsi.xeros.cooking;

public enum Food {
	
	NONE("", -1),
	SHRIMP("Shrimp", 317),
	ANCHOVIES("Anchovies", 321),
	RAWKARAMBWAN("Raw karambwan", 3142),
	TROUT("Trout", 335),
	SALMON("Salmon", 331),
	TUNA("Tuna", 359),
	BASS("Bass", 363),
	LOBSTER("Lobster", 377),
	MONKFISH("Monk Fish", 7944),
	SHARK("Shark", 383),
	MANTARAY("Manta Ray", 389);
	
	private final String name;
	private final int objectId;
	
	Food(final String name, final int objectId) {
		this.name = name;
		this.objectId = objectId;
	}

	public int getObjectId() {
		return objectId;
	}
	
	public String getName() {
		return name;
	}
	
	@Override
	public String toString() {
		return name().substring(0, 1).toUpperCase() + name().substring(1).toLowerCase();
	}
	

}