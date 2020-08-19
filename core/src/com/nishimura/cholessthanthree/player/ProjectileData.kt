package com.nishimura.cholessthanthree.player

data class ProjectileData(val startingXPcnt: Float = 0f,
                          val startingYPcnt: Float = 0f,
                          val startingAngle: Float = 0f,
                          val imageString: String? = null,
                          val delayPcnt: Float = 0f,
                          val travelDuration: Float = 0f,
                          val preReleaseActions: List<ProjectileAction> = emptyList())