package com.example.repository;

import com.example.dao.KafkaDAO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;


public interface KafKaRepository extends JpaRepository<KafkaDAO, Long>{

    @Transactional
    @Modifying
    @Query(nativeQuery = true, value= "insert into kafka_log (timestamp , code, guid, msg) values (:#{#log.timestamp}, :#{#log.code}, :#{#log.guid} ,:#{#log.msg})")
    Integer insert(@Param("log") KafkaDAO log);
}
