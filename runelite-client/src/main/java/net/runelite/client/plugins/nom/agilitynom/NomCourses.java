/*
 * Copyright (c) 2018, Seth <http://github.com/sethtroll>
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
 */
package net.runelite.client.plugins.nom.agilitynom;

import com.google.common.collect.ImmutableMap;
import lombok.Getter;
import net.runelite.api.coords.WorldPoint;
import net.runelite.client.plugins.nom.agilitynom.rooftops.*;

import java.util.Map;

enum NomCourses
{
	GNOME(new Gnome(),86.5, 46, 9781),
	DRAYNOR(new Draynor(),120.0, 79, 12338),
	AL_KHARID(new AlKharid(),180.0, 0, 13105, new WorldPoint(3299, 3194, 0)),
	VARROCK(new Varrock(),238.0, 125, 12853),
	CANIFIS(new Canifis(),240.0, 175, 13878),
	FALADOR(new Falador(),440, 180, 12084),
	SEERS(new Camelot(),570.0, 435, 10806),
	POLLNIVNEACH(new Pollivneach(),890.0, 540, 13358),
	RELLEKA(new Rellekka(),780.0, 475, 10553),
	ARDOUGNE(new Ardougne(),793.0, 529, 10547);

	private final static Map<Integer, NomCourses> coursesByRegion;

	@Getter
	private final Base courseData;

	@Getter
	private final double totalXp;

	@Getter
	private final int lastObstacleXp;

	@Getter
	private final int regionId;

	@Getter
	private final WorldPoint[] courseEndWorldPoints;

	static
	{
		ImmutableMap.Builder<Integer, NomCourses> builder = new ImmutableMap.Builder<>();

		for (NomCourses course : values())
		{
			builder.put(course.regionId, course);
		}

		coursesByRegion = builder.build();
	}

	NomCourses(Base courseData, double totalXp, int lastObstacleXp, int regionId, WorldPoint... courseEndWorldPoints)
	{
		this.courseData = courseData;
		this.totalXp = totalXp;
		this.lastObstacleXp = lastObstacleXp;
		this.regionId = regionId;
		this.courseEndWorldPoints = courseEndWorldPoints;
	}

	static NomCourses getCourse(int regionId)
	{
		return coursesByRegion.get(regionId);
	}
}
