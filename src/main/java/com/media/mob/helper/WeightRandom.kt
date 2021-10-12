package com.media.mob.helper

import com.media.mob.bean.SlotTactics
import java.util.SortedMap
import java.util.TreeMap

class WeightRandom(slotTacticsList: ArrayList<SlotTactics>) {

    private val weightMap: TreeMap<Double, SlotTactics> = TreeMap<Double, SlotTactics>()

    init {
        for (slotTactics in slotTacticsList) {
            val summation: Double = if (this.weightMap.size == 0) {
                0.00
            } else {
                this.weightMap.lastKey()
            }

            this.weightMap[slotTactics.tacticsWeight + summation] = slotTactics
        }
    }

    fun random(): SlotTactics? {
        val randomWeight = weightMap.lastKey() * Math.random()

        val tailMap: SortedMap<Double, SlotTactics>? = weightMap.tailMap(randomWeight, false)

        return tailMap?.get(tailMap.firstKey())
    }
}