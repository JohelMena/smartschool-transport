package com.johel.smartschool

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent

// Data (in-memory)
import com.johel.smartschool.data.MemoryDataManager

// Controllers
import com.johel.smartschool.controllers.StudentController
import com.johel.smartschool.controllers.RouteController
import com.johel.smartschool.controllers.AttendanceController
import com.johel.smartschool.controllers.GuardianController
import com.johel.smartschool.controllers.DriverController
import com.johel.smartschool.controllers.BusController

// Domain
import com.johel.smartschool.domain.Student
import com.johel.smartschool.domain.Route
import com.johel.smartschool.domain.Attendance
import com.johel.smartschool.domain.Guardian
import com.johel.smartschool.domain.Driver
import com.johel.smartschool.domain.Bus

// Utils
import com.johel.smartschool.util.IdGenerator

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Si aún no tienes UI, deja este contenedor vacío.
        setContent { /* TODO: UI Compose/XML más adelante */ }

        // ------------------------------------
        // 1) Data managers (en memoria)
        // ------------------------------------
        val studentMgr  = MemoryDataManager<Student>   { it.id }
        val guardianMgr = MemoryDataManager<Guardian>  { it.id }
        val driverMgr   = MemoryDataManager<Driver>    { it.id }
        val busMgr      = MemoryDataManager<Bus>       { it.id }
        val routeMgr    = MemoryDataManager<Route>     { it.id }
        val attMgr      = MemoryDataManager<Attendance>{ it.id }

        // ------------------------------------
        // 2) Controllers
        // ------------------------------------
        val students  = StudentController(studentMgr)
        val guardians = GuardianController(guardianMgr)
        val drivers   = DriverController(driverMgr)
        val buses     = BusController(busMgr)
        val routes    = RouteController(routeMgr)
        val att       = AttendanceController(attMgr)

        // ------------------------------------
        // 3) Seed de ejemplo (datos de prueba)
        //    Ajusta los nombres de propiedades
        //    si tus data classes usan español.
        // ------------------------------------

        // Encargado / Guardian
        val g1 = guardians.create(
            Guardian(
                id = IdGenerator.newId(),
                fullName = "María López",
                phone = "8888-8888",
                address = "San José Centro",
                idCopyUrl = null,
                paymentMethod = "Efectivo"
            )
        )

        // Chofer / Driver
        val d1 = drivers.create(
            Driver(
                id = IdGenerator.newId(),
                fullName = "Carlos Pérez",
                nationalId = "1-1111-1111",
                licenseNumber = "B3-556677",
                phone = "7000-0000",
                email = "carlos@empresa.com",
                available = true
            )
        )

        // Bus
        val b1 = buses.create(
            Bus(
                id = IdGenerator.newId(),
                plate = "ABC-123",
                capacity = 30,
                status = "activo",
                type = "micro",
                nextMaintenance = "2025-12-01"
            )
        )

        // Estudiante
        val s1 = students.create(
            Student(
                id = IdGenerator.newId(),
                fullName = "Ana López",
                birthDate = "2015-03-10",
                grade = "2°",
                medicalNotes = null,
                guardianId = g1.id,
                enrollmentCode = IdGenerator.enrollmentCode(seq = 1)
            )
        )

        // Ruta
        val r1 = routes.create(
            Route(
                id = IdGenerator.newId(),
                name = "Ruta Norte",
                pickupPoints = listOf("Parada 1", "Parada 2", "Parada 3"),
                driverId = null,
                busId = null
            )
        )

        // Asignaciones
        routes.assignStudent(routeId = r1.id, studentId = s1.id)
        routes.assignDriverAndBus(routeId = r1.id, driverId = d1.id, busId = b1.id)

        // Asistencia (check-in / check-out)
        val a1 = att.checkIn(
            Attendance(
                id = IdGenerator.newId(),
                studentId = s1.id,
                date = "2025-10-24",
                checkIn = "07:05",
                checkOut = null
            )
        )
        att.checkOut(id = a1.id, time = "12:45")

        // ------------------------------------
        // 4) Logs para verificar en Logcat
        //    Filtra con:  tag:DBG
        // ------------------------------------
        Log.d("DBG", "Guardians: ${guardians.list()}")
        Log.d("DBG", "Drivers:   ${drivers.list()}")
        Log.d("DBG", "Buses:     ${buses.list()}")
        Log.d("DBG", "Students:  ${students.list()}")
        Log.d("DBG", "Routes:    ${routes.list()}")
        Log.d("DBG", "Attend.:   ${att.byStudent(s1.id)}")
    }
}
