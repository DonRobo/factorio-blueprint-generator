package com.donrobo.fpbg.pathfinder

import com.donrobo.fpbg.blueprint.BeltLayer
import com.donrobo.fpbg.blueprint.Direction
import com.donrobo.fpbg.blueprint.DirectionalInt2
import com.donrobo.fpbg.blueprint.toBeltBlueprint
import com.donrobo.fpbg.util.Map2D
import com.donrobo.fpbg.util.asMap
import com.google.gson.Gson
import org.apache.commons.io.FileUtils
import org.apache.commons.io.IOUtils
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.Parameterized
import org.junit.runners.Parameterized.Parameters
import java.io.File

data class PathCalculation(val pathCost: Int, val calculationTime: Int, val visualization: String)
data class PathfinderScenario(val connections: Int, val startDirection: Direction = Direction.RIGHT, val endDirection: Direction = Direction.RIGHT, var fastestPath: PathCalculation? = null, var fastestCalculation: PathCalculation? = null)

@RunWith(Parameterized::class)
class PathfinderTest(val testcase: String) {

    @Test
    fun testPathfinding() {
        val content = readResourceToString("pathfinder/$testcase.scenario") ?: throw RuntimeException("All scenarios need a scenario file!")
        val scenarioMap = content.asMap()

        val metaResource = readResourceToString("pathfinder/$testcase.meta")
        val metaObj = if (metaResource == null) PathfinderScenario(connections = -1) else parseMeta(metaResource)

        val blueprintMap = Map2D().apply {
            scenarioMap.asMap().filterValues { value ->
                value == '^' ||
                        value == '>' ||
                        value == 'v' ||
                        value == '<'
            }.forEach { k, v ->
                set(k, v)
            }
        }
        val blueprint = blueprintMap.toBeltBlueprint()
        val starts = scenarioMap.asMap().filterValues { it in '1'..'9' }
                .map { entry -> entry.value to entry.key }.toMap().toSortedMap()
                .values.toList().map { DirectionalInt2(it, metaObj.startDirection) }
        val ends = scenarioMap.asMap().filterValues { it in 'a'..'i' }
                .map { entry -> entry.value to entry.key }.toMap().toSortedMap()
                .values.toList().map { DirectionalInt2(it, metaObj.endDirection) }

        assert(starts.size == ends.size)
        assert(starts.size == metaObj.connections)

        val paths = starts.mapIndexed { index, start -> start to ends[index] }
        val generatedPaths = BeltLayer.generatePaths(blueprint, paths)

        BeltLayer.layBelts(blueprint, generatedPaths) //TODO check if belt was laid correctly

        val cost = generatedPaths.map { it.f }.sum()
        val timeTaken = Int.MAX_VALUE

        val fastestPathCost = metaObj.fastestPath?.pathCost ?: Int.MAX_VALUE
        val fastestPathTime = metaObj.fastestPath?.calculationTime ?: Int.MAX_VALUE
        val fastestCalcCost = metaObj.fastestCalculation?.pathCost ?: Int.MAX_VALUE
        val fastestCalcTime = metaObj.fastestCalculation?.calculationTime ?: Int.MAX_VALUE

        val currentPath = PathCalculation(cost, timeTaken, blueprint.visualize())

        if (cost < fastestPathCost || (cost == fastestPathCost && timeTaken <= fastestPathTime))
            metaObj.fastestPath = currentPath
        if (timeTaken < fastestCalcTime || (timeTaken == fastestCalcTime && cost <= fastestCalcCost))
            metaObj.fastestCalculation = currentPath

        saveMeta(metaObj)
    }

    private fun saveMeta(metaObj: PathfinderScenario) {
        FileUtils.writeStringToFile(File("src/test/resources/pathfinder/$testcase.meta"), Gson().toJson(metaObj).toString(), "UTF-8")
    }

    private fun parseMeta(meta: String): PathfinderScenario {
        return Gson().fromJson(meta, PathfinderScenario::class.java)
    }

    companion object {

        private fun readResourceToString(resource: String): String? {
            val resUrl = PathfinderTest::class.java.classLoader.getResource(resource) ?: return null
            return IOUtils.toString(resUrl, "UTF-8") ?: throw RuntimeException("Resource not found")
        }

        @Parameters(name = "\"{0}\"")
        @JvmStatic
        fun data(): Collection<String> = (readResourceToString("pathfinder/")?.split("\n") ?: emptyList()).filter { it.isNotBlank() }.filter { it.endsWith(".scenario") }
                .map { it.substring(0..(it.length - ".scenario".length - 1)) }
    }

}