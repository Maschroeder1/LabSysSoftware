package infra

import model.ClassCode
import model.CollegeClass
import model.KeyNotRegisteredException
import java.util.*
import kotlin.collections.HashMap

open class CollegeClassController {
    private val collegeClassCache = HashMap<String, CollegeClass>()
    private val userRequestClassName = HashMap<Int, List<String>>()
    private val toProcessClasses: Deque<ClassCode> = LinkedList()

    open fun queueClassesToProcess(classCodes: List<ClassCode>): Int {
        val key = classCodes.hashCode() and 0xfffffff
        userRequestClassName[key] = classCodes.map { classCode -> classCode.className }
        toProcessClasses.addAll(classCodes)
        return key
    }

    open fun getCollegeClasses(key: Int): Map<String, CollegeClass?> {
        if (!userRequestClassName.containsKey(key)) {
            throw KeyNotRegisteredException()
        }
        val classNames = userRequestClassName[key]!!

        return classNames.associateBy(
            { name -> name }, { name -> collegeClassCache.getOrDefault(name, null)})
    }

    open fun getClassToProcess(): ClassCode? {
        return if (toProcessClasses.isNotEmpty()) toProcessClasses.remove() else null
    }

    open fun cache(className: String, collegeClass: CollegeClass) {
        collegeClassCache[className] = collegeClass
    }
}