package infra

import model.ClassCode
import model.CollegeClass
import model.KeyNotRegisteredException
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class CollegeClassControllerTest {
    private var controller = CollegeClassController()
    private val aClassCode = ClassCode("c1", "a1", "h1", "c1", "s1", "classPlan")
    private val anotherClassCode = ClassCode("c2", "a2", "h2", "c2", "s2", null)
    private val aCollegeClass = CollegeClass(emptyList(), 123, "classPlan")

    @BeforeEach
    fun setup() {
        controller = CollegeClassController()
    }

    @Test
    fun throwsWhenClassIsNotCached() {
        assertThrows(KeyNotRegisteredException::class.java) {
            controller.getCollegeClasses(123)
        }
    }

    @Test
    fun returnsCachedClass() {
        val expected = mapOf("c1" to aCollegeClass, "c2" to null)
        val key = controller.queueClassesToProcess(listOf(aClassCode, anotherClassCode))

        controller.cache("c1", aCollegeClass)
        val actual = controller.getCollegeClasses(key)

        assertEquals(actual, expected)
    }

    @Test
    fun classesToProcessAreFIFO() {
        controller.queueClassesToProcess(listOf(aClassCode, anotherClassCode))

        assertEquals(aClassCode, controller.getClassToProcess())
        assertEquals(anotherClassCode, controller.getClassToProcess())
        assertNull(controller.getClassToProcess())
    }
}