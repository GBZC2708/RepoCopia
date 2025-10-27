package com.example.alphakids.data.mappers

import com.example.alphakids.data.firebase.models.Institucion as InstitucionDto
import com.example.alphakids.domain.models.Institution

object InstitutionMapper {

    fun toDomain(dto: InstitucionDto): Institution {
        return Institution(
            id = dto.id,
            nombre = dto.nombre,
            direccion = dto.direccion,
            telefono = dto.telefono,
            correo = dto.correo,
            fechaCreacionMillis = dto.fechaCreacion?.toDate()?.time
        )
    }

    fun fromDomain(domain: Institution): InstitucionDto {
        return InstitucionDto(
            id = domain.id,
            nombre = domain.nombre,
            direccion = domain.direccion,
            telefono = domain.telefono,
            correo = domain.correo
        )
    }
}
