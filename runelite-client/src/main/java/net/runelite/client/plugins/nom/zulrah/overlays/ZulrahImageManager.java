/*Nomnom
 * Copyright (c) 2017, Devin French <https://github.com/devinfrench>
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice, this
 *    list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 nomnom */
package net.runelite.client.plugins.nom.zulrah.overlays;

import lombok.extern.slf4j.Slf4j;
import net.runelite.api.Prayer;
import net.runelite.client.plugins.nom.zulrah.ZulrahPlugin;
import net.runelite.client.plugins.nom.zulrah.phase.ZulrahType;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;

@Slf4j
public class ZulrahImageManager
{
	private static final BufferedImage[] zulrahBufferedImages = new BufferedImage[3];
	private static final BufferedImage[] smallZulrahBufferedImages = new BufferedImage[3];
	private static final BufferedImage[] prayerBufferedImages = new BufferedImage[3];
	private static final BufferedImage[] itemBufferedImages = new BufferedImage[1];

	public static BufferedImage getZulrahBufferedImage(ZulrahType type)
	{
		switch (type)
		{
			case RANGE:
				if (zulrahBufferedImages[0] == null)
				{
					zulrahBufferedImages[0] = getBufferedImage("/skill_icons/ranged.png");
				}
				return zulrahBufferedImages[0];
			case MAGIC:
				if (zulrahBufferedImages[1] == null)
				{
					zulrahBufferedImages[1] = getBufferedImage("/skill_icons/magic.png");
				}
				return zulrahBufferedImages[1];
			case MELEE:
				if (zulrahBufferedImages[2] == null)
				{
					zulrahBufferedImages[2] = getBufferedImage("/skill_icons/attack.png");
				}
				return zulrahBufferedImages[2];
		}
		return null;
	}

	public static BufferedImage getSmallZulrahBufferedImage(ZulrahType type)
	{
		switch (type)
		{
			case RANGE:
				if (smallZulrahBufferedImages[0] == null)
				{
					smallZulrahBufferedImages[0] = getBufferedImage("/skill_icons_small/ranged.png");
				}
				return smallZulrahBufferedImages[0];
			case MAGIC:
				if (smallZulrahBufferedImages[1] == null)
				{
					smallZulrahBufferedImages[1] = getBufferedImage("/skill_icons_small/magic.png");
				}
				return smallZulrahBufferedImages[1];
			case MELEE:
				if (smallZulrahBufferedImages[2] == null)
				{
					smallZulrahBufferedImages[2] = getBufferedImage("/skill_icons_small/attack.png");
				}
				return smallZulrahBufferedImages[2];
		}
		return null;
	}

	public static BufferedImage getProtectionPrayerBufferedImage(Prayer prayer)
	{
		switch (prayer)
		{
			case PROTECT_FROM_MAGIC:
				if (prayerBufferedImages[0] == null)
				{
					prayerBufferedImages[0] = getBufferedImage("/prayers/protect_from_magic.png");
				}
				return prayerBufferedImages[0];
			case PROTECT_FROM_MISSILES:
				if (prayerBufferedImages[1] == null)
				{
					prayerBufferedImages[1] = getBufferedImage("/prayers/protect_from_missiles.png");
				}
				return prayerBufferedImages[1];
		}

		if (prayerBufferedImages[2] == null)
		{
			prayerBufferedImages[2] = getBufferedImage("/prayers/prayer_off.png");
		}
		return prayerBufferedImages[2];
//		return null;
	}

	public static BufferedImage getRecoilBufferedImage()
	{
		if (itemBufferedImages[0] == null)
		{
			itemBufferedImages[0] = getBufferedImage("/prayers/ring_of_recoil.png");
		}
		return itemBufferedImages[0];
	}

	private static BufferedImage getBufferedImage(String path)
	{
		BufferedImage image = null;
		try
		{
//			log.info(ZulrahPlugin.class.getResource("/").getPath());
			InputStream in = ZulrahPlugin.class.getResourceAsStream(path);
			image = ImageIO.read(in);
		}
		catch (IOException e)
		{
			log.info("Error loading image {}", e);
		}
		return image;
	}
}
