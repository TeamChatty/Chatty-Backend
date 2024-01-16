package com.chatty.repository.user;

import com.chatty.entity.user.User;

import java.util.List;
import java.util.Optional;

import org.locationtech.jts.geom.Point;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findUserByMobileNumberAndDeviceId(String mobileNumber, String deviceId);

    Optional<User> findUserByMobileNumber(String mobileNumber);

    Boolean existsUserByMobileNumber(String mobileNumber);

    Optional<User> findUserById(long id);

    Optional<User> findByNickname(String nickname);

    @Query(value = "select u.*, ST_Distance_Sphere(u.location, :point) as dis " +
            "from Users u " +
            "having dis <= :scope", nativeQuery = true)
    List<User> customFindByDistance(@Param("point") Point point,
                                    @Param("scope") Double scope);

    @Query(value = "select ST_Distance_Sphere(:point, :point2) as dis " +
            "having dis <= :scope", nativeQuery = true)
    Double customFindByDistance2(@Param("point") Point point,
                                    @Param("point2") Point point2,
                                    @Param("scope") Double scope);
}
