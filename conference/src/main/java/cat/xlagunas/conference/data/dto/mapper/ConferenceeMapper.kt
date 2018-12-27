package cat.xlagunas.conference.data.dto.mapper

import cat.xlagunas.conference.data.dto.ConferenceeDto
import cat.xlagunas.conference.data.dto.RoomDetailsDto
import cat.xlagunas.conference.domain.model.Conferencee

class ConferenceeMapper {

    private fun fromDto(conferenceeDto: ConferenceeDto) : Conferencee{
        return Conferencee(conferenceeDto.userId)
    }

    fun fromDto(roomDetailsDto: RoomDetailsDto) : List<Conferencee> {
        return roomDetailsDto.users.map { fromDto(it) }
    }

}