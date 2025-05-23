/*
 * Copyright (c) 2019, Ganom <https://github.com/Ganom>
 * Copyright (c) 2019, Lucas <https://github.com/lucwousin>
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
package net.runelite.client.plugins.nom.fightcave;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import lombok.*;
import net.runelite.api.*;

import java.awt.*;

import static net.runelite.client.plugins.nom.fightcave.FightCavePlugin.*;

@Getter(AccessLevel.PACKAGE)
public class FightCaveContainer
{
	@Getter
	private NPC npc;
	private String npcName;
	private int npcIndex;
	private int npcSize;
	private int attackSpeed;
	private int priority;
	private ImmutableSet<Integer> animations;
	@Setter(AccessLevel.PACKAGE)
	private int ticksUntilAttack;
	@Setter(AccessLevel.PACKAGE)
	private Actor npcInteracting;
	@Setter(AccessLevel.PACKAGE)
	private AttackStyle attackStyle;

	FightCaveContainer(NPC npc)
	{
		this.npc = npc;
		this.npcName = npc.getName();
		this.npcIndex = npc.getIndex();
		this.npcInteracting = npc.getInteracting();
		this.attackStyle = AttackStyle.UNKNOWN;
		this.ticksUntilAttack = -1;
		final NPCComposition composition = npc.getTransformedComposition();
		if (composition != null)
		{
			this.npcSize = composition.getSize();
		}

		BossMonsters monster = BossMonsters.of(npc.getId());

		if (monster != null)
		{
			this.attackSpeed = monster.attackSpeed;
			this.animations = monster.animations;
			this.attackStyle = monster.attackStyle;
			this.priority = monster.priority;
		}
		else
		{
			this.attackSpeed = 0;
			this.animations = ImmutableSet.of(-1);
			this.attackStyle = AttackStyle.UNKNOWN;
			this.priority = -1;
		}
	}

	@RequiredArgsConstructor
	enum BossMonsters
	{
		TOK_XIL1(NpcID.TOKXIL_3121, AttackStyle.RANGE, 4, ImmutableSet.of(TOK_XIL_RANGE_ATTACK, TOK_XIL_MELEE_ATTACK), 1),
		TOK_XIL2(NpcID.TOKXIL_3122, AttackStyle.RANGE, 4, ImmutableSet.of(TOK_XIL_RANGE_ATTACK, TOK_XIL_MELEE_ATTACK), 1),
		KETZEK1(NpcID.KETZEK, AttackStyle.MAGE, 4, ImmutableSet.of(KET_ZEK_MAGE_ATTACK, KET_ZEK_MELEE_ATTACK), 0),
		KETZEK2(NpcID.KETZEK_3126, AttackStyle.MAGE, 4, ImmutableSet.of(KET_ZEK_MAGE_ATTACK, KET_ZEK_MELEE_ATTACK), 0),
		YTMEJKOT1(NpcID.YTMEJKOT, AttackStyle.MELEE, 4, ImmutableSet.of(MEJ_KOT_HEAL_ATTACK, MEJ_KOT_MELEE_ATTACK), 2),
		YTMEJKOT2(NpcID.YTMEJKOT_3124, AttackStyle.MELEE, 4, ImmutableSet.of(MEJ_KOT_HEAL_ATTACK, MEJ_KOT_MELEE_ATTACK), 2),
		TZTOKJAD1(NpcID.TZTOKJAD, AttackStyle.UNKNOWN, 8, ImmutableSet.of(TZTOK_JAD_MAGIC_ATTACK, TZTOK_JAD_RANGE_ATTACK, TZTOK_JAD_MELEE_ATTACK), 0),
		TZTOKJAD2(NpcID.TZTOKJAD_6506, AttackStyle.UNKNOWN, 8, ImmutableSet.of(TZTOK_JAD_MAGIC_ATTACK, TZTOK_JAD_RANGE_ATTACK, TZTOK_JAD_MELEE_ATTACK), 0);

		private static final ImmutableMap<Integer, BossMonsters> idMap;

		static
		{
			ImmutableMap.Builder<Integer, BossMonsters> builder = ImmutableMap.builder();

			for (BossMonsters monster : values())
			{
				builder.put(monster.npcID, monster);
			}

			idMap = builder.build();
		}

		private final int npcID;
		private final AttackStyle attackStyle;
		private final int attackSpeed;
		private final ImmutableSet<Integer> animations;
		private final int priority;

		static BossMonsters of(int npcID)
		{
			return idMap.get(npcID);
		}
	}

	@Getter(AccessLevel.PACKAGE)
	@AllArgsConstructor
	enum AttackStyle
	{
		MAGE("Mage", Color.CYAN, Prayer.PROTECT_FROM_MAGIC),
		RANGE("Range", Color.GREEN, Prayer.PROTECT_FROM_MISSILES),
		MELEE("Melee", Color.RED, Prayer.PROTECT_FROM_MELEE),
		UNKNOWN("Unknown", Color.WHITE, null);

		private String name;
		private Color color;
		private Prayer prayer;
	}
}