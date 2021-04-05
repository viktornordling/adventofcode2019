package d01

data class Triangle(val name: String, val vertexes: List<String>)
data class Vertex(val name: String, val x: String, val y: String, val z: String)

object Solution {
    fun solve() {
        val vertices =
            """
V0  = (0.0f, 0.0f,  C1)
V1  = (0.0f, 0.0f, -C1)
V2  = ( C1, 0.0f, 0.0f)
V3  = (-C1, 0.0f, 0.0f)
V4  = (0.0f,  C1, 0.0f)
V5  = (0.0f, -C1, 0.0f)
V6  = ( C0,  C0,  C0)
V7  = ( C0,  C0, -C0)
V8  = ( C0, -C0,  C0)
V9  = ( C0, -C0, -C0)
V10 = (-C0,  C0,  C0)
V11 = (-C0,  C0, -C0)
V12 = (-C0, -C0,  C0)
V13 = (-C0, -C0, -C0)
""".trimIndent()
        val verts = vertices.lines().map { parseVertex(it) }

        val faces =
            """
{  0,  6, 10 }
{  0, 10, 12 }
{  0, 12,  8 }
{  0,  8,  6 }
{  1,  7,  9 }
{  1,  9, 13 }
{  1, 13, 11 }
{  1, 11,  7 }
{  2,  6,  8 }
{  2,  8,  9 }
{  2,  9,  7 }
{  2,  7,  6 }
{  3, 10, 11 }
{  3, 11, 13 }
{  3, 13, 12 }
{  3, 12, 10 }
{  4,  6,  7 }
{  4,  7, 11 }
{  4, 11, 10 }
{  4, 10,  6 }
{  5,  8, 12 }
{  5, 12, 13 }
{  5, 13,  9 }
{  5,  9,  8 }
""".trimIndent()
        val tris = faces.lines().mapIndexed { index, string -> parse(string, index) }
//        println(tris)
        val vertexMap = verts.associateBy { it.name }

        val blueTri = tris.first()
        val triColors = mutableMapOf<Triangle, Boolean>()
        triColors[blueTri] = true

        val uncoloredTris = tris.minusElement(blueTri).toMutableSet()

        while (uncoloredTris.isNotEmpty()) {
            val randomTri: Triangle = uncoloredTris.random()
            val coloredTris: Set<Triangle> = triColors.keys.toSet()
            for (tri in coloredTris) {
                if (tri.vertexes.toSet().intersect(randomTri.vertexes.toSet()).size == 2) {
//                    println(
//                        "Tri ${randomTri.name} is adjacent to ${tri.name}, which is ${getTriangleColor(
//                            tri,
//                            triColors
//                        )}, so it must be ${getOppositeTriangleColor(tri, triColors)}"
//                    )
                    val color: Boolean = !triColors[tri]!!
                    triColors[randomTri] = color
                    uncoloredTris.remove(randomTri)
                    break
                }
            }
        }

        // test solution
        // for each triangle, check that all neighboring triangles are of different colors
        triColors.keys.forEach { tri: Triangle ->
            //            println("Checking $tri")
            val neighbors = triColors.keys.filter { it != tri && it.vertexes.toSet().intersect(tri.vertexes.toSet()).size == 2 }
//            println("Neighbors: $neighbors")
            val curTriColor = triColors[tri]
            for (neighbor in neighbors) {
                if (triColors[neighbor] == curTriColor) {
                    println("THIS IS WRONG, TRIANGLE $tri has same color ($curTriColor) as $neighbor")
                }
            }
        }

        tris.forEachIndexed { index, triangle ->
            val blue = triColors[triangle]!!
            val color = if (blue) "0.0f, 0.0f, 1.0f" else "0.4f, 0.4f, 0.4f"
            val v1 = vertexMap[triangle.vertexes[0]]!!
            val v2 = vertexMap[triangle.vertexes[1]]!!
            val v3 = vertexMap[triangle.vertexes[2]]!!
            val normal = calcNormal(v1, v2)
            // Tri tri1 { 1, glm::vec4(-C0, C0, C0, 1.0f), glm::vec4(C0, C0, C0, 1.0f), glm::vec4(0.0f, 0.0f, C1, 1.0f), glm::vec3(0.4, 0.4, 0.4), {-C0, C0, C0, 0.0, 0.0, 1.0, C0, C0, C0, 0.0, 0.0, 1.0, 0.0, 0.0, C1, 0.0, 0.0, 1.0} };
            val centroid = "(${v1.x} + ${v2.x} + ${v3.x}) / 3.0f, (${v1.y} + ${v2.y} + ${v3.y}) / 3.0f, (${v1.z} + ${v2.z} + ${v3.z}) / 3.0f"
            println("Tri tri$index = Tri($index, glm::vec4(${v1.x}, ${v1.y}, ${v1.z}, 1.0f), glm::vec4(${v2.x}, ${v2.y}, ${v2.z}, 1.0f), glm::vec4(${v3.x}, ${v3.y}, ${v3.z}, 1.0f), glm::vec3($color));")
//            println("Tri tri$index { $index, glm::vec4(${v1.x}, ${v1.y}, ${v1.z}, 1.0f), glm::vec4(${v2.x}, ${v2.y}, ${v2.z}, 1.0f), glm::vec4(${v3.x}, ${v3.y}, ${v3.z}, 1.0f), glm::vec3($centroid), { ${vertexToString(v1, color, normal)}, ${vertexToString(v2, color, normal)}, ${vertexToString(v3, color, normal)}}, 0.0f };")
//            println("${vertexToString(v1, color)}, ${vertexToString(v2, color)}, ${vertexToString(v3, color)},")
        }

//        val sortedMap = triColors.toSortedMap(compareBy { it.name })
//        println("Colors = $sortedMap ")
    }


    private fun calcNormal(a: Vertex, b: Vertex): Vertex {
        val x = "${multiply(a.y, b.z)} - ${multiply(a.z, b.y)}"
        val y = "${multiply(a.z, b.x)} - ${multiply(a.x, b.z)}"
        val z = "${multiply(a.x, b.y)} - ${multiply(a.y, b.x)}"
        return Vertex("normal", x, y, z)
    }

    private fun multiply(a: String, b: String) = "($a * $b)"

    private fun getTriangleColor(triangle: Triangle, triColors: MutableMap<Triangle, Boolean>): String {
        val isBlue: Boolean = triColors[triangle]!!
        return if (isBlue) {
            "blue"
        } else {
            "grey"
        }
    }

    private fun getOppositeTriangleColor(triangle: Triangle, triColors: MutableMap<Triangle, Boolean>): String {
        val isBlue: Boolean = triColors[triangle]!!
        return if (isBlue) {
            "grey"
        } else {
            "blue"
        }
    }

    //V0  = (0.0, 0.0,  C1)
    private fun parseVertex(line: String): Vertex {
        val parts = line.replace("(", "").replace(")", "").replace("=", "").replace(",", " ").split(Regex("\\s+"))
        return Vertex(name = parts[0], x = parts[1], y = parts[2], z = parts[3])
    }

    // {  0,  6, 10 }
    private fun parse(line: String, index: Int): Triangle {
        val parts = line.replace("{","").replace("}","").replace(" ", "").split(",")
        val name = "T$index"
        val v1 = "V${parts[0]}"
        val v2 = "V${parts[1]}"
        val v3 = "V${parts[2]}"
        return Triangle(name, listOf(v1, v2, v3))
    }

    private fun vertexToString(vertex: Vertex, color: String, normal: Vertex): String {
        return "${vertex.x}, ${vertex.y}, ${vertex.z}, $color, ${normal.x}, ${normal.y}, ${normal.z}"
    }

//    private fun calcFuelRec(mass: Int, sum: Int = 0): Int {
//        val fuel = max(calculateFuel(mass), 0)
//        if (fuel == 0) {
//            return sum
//        }
//        return calcFuelRec(fuel, sum + fuel)
//    }
//
//    private fun calculateFuel(mass: Int) = mass / 3 - 2
}

fun main(args: Array<String>) {
    Solution.solve()
}