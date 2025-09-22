package com.hm.viscosityauto.room.audit

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.hm.viscosityauto.room.admin.AdminRecords
import com.hm.viscosityauto.room.test.TestRecords

@Dao
interface AuditDao {
    //添加  传递一个参数 对象
    @Insert
    suspend fun insert(record: AuditRecords)

    //删除
    @Delete
    suspend fun delete( record: AuditRecords): Int

    //删除全部
    @Query("DELETE FROM audit")
    suspend fun deleteAll(): Int

    //修改 传入对象 设置 id 进行修改某一个
    @Update
    suspend fun updateData(record: AuditRecords): Int

    //查询 根据id倒序
    @Query("select * from audit order by id desc")
    suspend fun getRecords(): List<AuditRecords>


    //分页查询
    @Query("select * from audit  WHERE (:dateStr = '' OR date = :dateStr)  AND (:adminStr = '' OR user = :adminStr) order by id desc limit :limit offset :page")
    suspend fun getPageRecord(page: Int, limit: Int,dateStr:String,adminStr:String): List<AuditRecords>


    //根据id查询
    @Query("select * from audit where id =:numb")
    suspend fun getRecord(numb: Int): AuditRecords

    //数据总量
    @Query("SELECT COUNT(*) FROM audit")
    suspend fun getRecordCount(): Int
}
