package cat.xlagunas.data.user.register

import cat.xlagunas.data.common.converter.UserConverter
import cat.xlagunas.data.common.db.UserDao
import cat.xlagunas.domain.commons.User
import cat.xlagunas.domain.schedulers.RxSchedulers
import cat.xlagunas.domain.user.register.RegisterRepository
import io.reactivex.Completable
import io.reactivex.Flowable

class RegisterRepositoryImpl(private val registerApi: RegisterApi,
                             private val userDao: UserDao,
                             private val userConverter: UserConverter,
                             private val schedulers: RxSchedulers) : RegisterRepository {

    override fun findUser(): Flowable<User> {
        return userDao.loadAll()
                .map(userConverter::toUser)
    }


    override fun registerUser(user: User): Completable {
        val userDto = userConverter.toUserDto(user)
        val userEntity = userConverter.toUserEntity(user)

        return registerApi.registerUser(userDto)
                .andThen(Completable.fromAction { userDao.insert(userEntity) })
                .observeOn(schedulers.mainThread)
                .subscribeOn(schedulers.io)

    }
}