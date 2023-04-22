package com.zyq.parttime.repository.unit;

import com.zyq.parttime.entity.Employer;
import com.zyq.parttime.entity.Unit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import javax.transaction.Transactional;
import java.util.Date;

public interface UnitRepository extends JpaRepository<Unit, Integer>, JpaSpecificationExecutor<Unit> {
    @Query(value = "select * from unit where unit_name = ?1", nativeQuery = true)
    Unit findUnitByUnitName(String unit_name);

    @Query(value = "select * from unit where u_id = ?1", nativeQuery = true)
    Unit findUnitByUnitId(int u_id);

    @Transactional
    @Modifying
    @Query(value = "insert into unit(unit_name,job_nums) values(?1,?2)", nativeQuery = true)
    void createUnitByUnitName(String telephone, int job_nums);

    @Query(value = "select u_id from unit where unit_name = ?1", nativeQuery = true)
    int findUnitByUnitNameWithUId(String unit_name);

    @Transactional
    @Modifying
    @Query(value = "update unit set job_nums=job_nums+1 where u_id=?1", nativeQuery = true)
    void addJobNums(int u_id);

    @Transactional
    @Modifying
    @Query(value = "update unit set job_nums=job_nums-1 where u_id=?1", nativeQuery = true)
    void minusJobNums(int u_id);

    @Transactional
    @Modifying
    @Query(value = "update unit set descriptions=?1,loc=?2 where u_id=?3", nativeQuery = true)
    void editEmpUnitInfo(String descriptions, String loc, int u_id);

}