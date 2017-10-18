package com.jufan.dao.repository;

import com.jufan.entity.NetLogEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 *
 * {@link NetLogEntity}的Repository
 *
 * @author 李尧
 * @since  0.2.0
 */
@Repository
public interface NetLogRepo extends JpaRepository<NetLogEntity, String> {
}
