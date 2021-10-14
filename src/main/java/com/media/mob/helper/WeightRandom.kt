package com.media.mob.helper

import com.media.mob.bean.TacticsConfig
import com.media.mob.bean.TacticsInfo
import java.util.SortedMap
import java.util.TreeMap

class WeightRandom(tacticsConfig: TacticsConfig) {

    private val weightMap: TreeMap<Double, TacticsInfo> = TreeMap<Double, TacticsInfo>()

    init {
        for (slotTactics in tacticsConfig.tacticsInfoList) {
            val summation: Double = if (this.weightMap.size == 0) {
                0.00
            } else {
                this.weightMap.lastKey()
            }

            this.weightMap[slotTactics.tacticsWeight + summation] = slotTactics
        }
    }

    fun random(): TacticsInfo? {
        val randomWeight = weightMap.lastKey() * Math.random()

        val tailMap: SortedMap<Double, TacticsInfo>? = weightMap.tailMap(randomWeight, false)

        return tailMap?.get(tailMap.firstKey())
    }
}