package net.runelite.client.plugins.nom.peenscreen;

import net.runelite.api.Client;
import net.runelite.api.Model;
import net.runelite.client.ui.overlay.Overlay;
import net.runelite.client.ui.overlay.OverlayLayer;
import net.runelite.client.ui.overlay.OverlayPosition;
import net.runelite.client.ui.overlay.OverlayPriority;

import javax.inject.Inject;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

public class PeenScreenOverlay extends Overlay
{
	private Client client;
	private PeenScreenPlugin plugin;
	private PeenScreenConfig config;

	@Inject
	public PeenScreenOverlay(Client client, PeenScreenPlugin plugin, PeenScreenConfig config) {
		super(plugin);
		setPosition(OverlayPosition.DYNAMIC);
		setLayer(OverlayLayer.UNDER_WIDGETS);
		setPriority(OverlayPriority.LOW);
		this.client = client;
		this.config = config;
	}
//	BufferedImage image;
	@Override
	public Dimension render(Graphics2D g) {

//		image = image == null ? new BufferedImage(client.getCanvasWidth(), client.getCanvasHeight(), BufferedImage.TYPE_4BYTE_ABGR) : image;
//		Graphics g = image.getGraphics();

		g.setColor(config.greenscreenColor());
		g.fillRect(0, 0, client.getCanvasWidth(), client.getCanvasHeight());

//		Polygon[] polygons = client.getLocalPlayer().getPolygons();
//		Triangle[] triangles = getTriangles(client.getLocalPlayer().getModel());
//
//		for (int i = 0; i < polygons.length; i++) {
//			Triangle t = triangles[i];
//			if (!(t.getA().getY() == 6 && t.getB().getY() == 6 && t.getC().getY() == 6)) {
//				clearPolygon(image, polygons[i]);
//			}
//		}

//		graphics.drawImage(image, 0, 0, null);

		return null;
	}

	private void clearPolygon(BufferedImage image, Polygon p) {
		Rectangle bounds = p.getBounds();
		for (double y = bounds.getMinY(); y < bounds.getMaxY(); y++) {
			for (double x = bounds.getMinX(); x < bounds.getMaxX(); x++) {
				if (p.contains(x, y)
					&& x >= 0
					&& x < client.getCanvasWidth()
					&& y >= 0
					&& y < client.getCanvasHeight()
				) {
					image.setRGB((int)x, (int)y, 0x00000000);
				}
			}
		}
	}

	private List<Vertex> getVertices(Model model)
	{
		float[] verticesX = model.getVerticesX();
		float[] verticesY = model.getVerticesY();
		float[] verticesZ = model.getVerticesZ();

		int count = model.getVerticesCount();

		List<Vertex> vertices = new ArrayList(count);

		for (int i = 0; i < count; ++i)
		{
			Vertex v = new Vertex(
				verticesX[i],
				verticesY[i],
				verticesZ[i]
			);
			vertices.add(v);
		}

		return vertices;
	}

	private Triangle[] getTriangles(Model model)
	{
		int[] trianglesX = model.getFaceIndices1();
		int[] trianglesY = model.getFaceIndices2();
		int[] trianglesZ = model.getFaceIndices3();

		List<Vertex> vertices = getVertices(model);

		int count = model.getFaceCount();
		Triangle[] triangles = new Triangle[count];

		for (int i = 0; i < count; ++i)
		{
			int triangleX = trianglesX[i];
			int triangleY = trianglesY[i];
			int triangleZ = trianglesZ[i];

			Triangle triangle = new Triangle(
				vertices.get(triangleX),
				vertices.get(triangleY),
				vertices.get(triangleZ)
			);
			triangles[i] = triangle;
		}

		return triangles;
	}
}
