package cat.xlagunas.conference.data.dto.mapper

import cat.xlagunas.conference.data.dto.ConferenceeDto
import cat.xlagunas.conference.domain.model.Conferencee

class ConferenceeMapper {

    private fun fromDto(conferenceeDto: ConferenceeDto): Conferencee {
        return Conferencee(conferenceeDto.userId)
    }

    fun fromDto(conferencee: cat.xlagunas.conference.data.dto.Conferencee): List<Conferencee> {
        return conferencee.users.map { fromDto(it) }
    }
}