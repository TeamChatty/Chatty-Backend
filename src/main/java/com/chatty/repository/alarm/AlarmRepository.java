package com.chatty.repository.alarm;

import com.chatty.entity.alarm.Alarm;
import com.chatty.entity.user.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AlarmRepository extends JpaRepository<Alarm, Long> {

    Page<Alarm> findByIdLessThanAndUserOrderByIdDesc(Long id, User user, PageRequest pageRequest);
}
