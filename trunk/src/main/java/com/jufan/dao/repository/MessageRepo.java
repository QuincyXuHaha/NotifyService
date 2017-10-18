package com.jufan.dao.repository;

import com.jufan.entity.MessageEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.util.Date;
import java.util.List;

/**
 *
 * {@link MessageEntity}的Repository
 *
 * @author 李尧
 * @since  0.1.0
 */
@Repository
@Transactional
public interface MessageRepo extends JpaRepository<MessageEntity, String>, JpaSpecificationExecutor<MessageEntity> {

    List<MessageEntity> findTop50000ByStatusAndFirstSendTimeLessThanEqual(Integer status, Date date);

    @Modifying
    @Query("update MessageEntity set status = ?1 where id in ?2")
    void updateStatus(Integer status, List<String> idList);

    @Modifying
    @Query("update MessageEntity set updateTime = ?2, status = ?3, nowLevel =?4, lastSendTime = ?5 where id = ?1")
    void updateMessage(String id, Date updateTime, Integer status, String nowLevel, Date lastSendTime);

}
