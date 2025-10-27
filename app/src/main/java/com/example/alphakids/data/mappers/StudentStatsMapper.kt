package com.example.alphakids.data.mappers

import com.example.alphakids.data.firebase.models.EstadisticasEstudiante as StatsDto
import com.example.alphakids.domain.models.StudentStats

object StudentStatsMapper {

    fun toDomain(dto: StatsDto): StudentStats {
        return StudentStats(
            totalPartidas = dto.totalPartidas,
            promedioPuntuacion = dto.promedioPuntuacion,
            promedioIntentosExitosos = dto.promedioIntentosExitosos,
            promedioTiempoPartida = dto.promedioTiempoPartida,
            palabrasAprendidas = dto.palabrasAprendidas,
            ultimaActividadMillis = dto.ultimaActividad?.toDate()?.time
        )
    }

    fun fromDomain(domain: StudentStats): StatsDto {
        return StatsDto(
            totalPartidas = domain.totalPartidas,
            promedioPuntuacion = domain.promedioPuntuacion,
            promedioIntentosExitosos = domain.promedioIntentosExitosos,
            promedioTiempoPartida = domain.promedioTiempoPartida,
            palabrasAprendidas = domain.palabrasAprendidas
        )
    }
}
