package net.runelite.client.plugins.nom.nomobjectindicators;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.awt.*;

@Data
@NoArgsConstructor // Added for Gson
public class ObjectPoint
{
	private int id;
	private String name;
	private int regionId;
	private int regionX;
	private int regionY;
	private int z;
	private int bucket;
	private Color borderColor;
	private Color fillColor;
	private Boolean hull;
	private Boolean outline;
	private Boolean clickbox;
	private Boolean tile;

	// Explicit constructor to resolve compilation error
	public ObjectPoint(int id, String name, int regionId, int regionX, int regionY, int z)
	{
		this.id = id;
		this.name = name;
		this.regionId = regionId;
		this.regionX = regionX;
		this.regionY = regionY;
		this.z = z;
	}
}
