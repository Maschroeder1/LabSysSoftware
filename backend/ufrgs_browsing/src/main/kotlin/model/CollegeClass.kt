package model

data class CollegeClass(val timeslots: List<Timeslot>, val credits: Int, var classPlan: String?)

data class Timeslot(
    val classIdentifier: String, val availableSlots: Int, val professors: List<String>, val scheduledTimes: List<ScheduleTime>
)

data class ScheduleTime(
    val day: String, val shortDay: String?, val startTime: String, val endTime: String, val location: String, val locationMap: String?
)