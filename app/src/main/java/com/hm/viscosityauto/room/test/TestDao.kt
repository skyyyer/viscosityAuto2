package com.hm.viscosityauto.room.test

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.hm.viscosityauto.room.audit.AuditRecords

@Dao
interface TestDao {
    //添加  传递一个参数 对象
    @Insert
    suspend fun insert(people: TestRecords)

    //删除
    @Delete
    suspend fun deleteData( people: TestRecords): Int

    //删除全部
    @Query("DELETE FROM test")
    suspend fun deleteAll(): Int

    @Query("DELETE FROM test WHERE id IN (:ids)")
    fun deleteByIds(ids: List<Long?>?)

    //修改 传入对象 设置 id 进行修改某一个
    @Update
    suspend fun updateData( people: TestRecords): Int

    //查询 根据id倒序
    @Query("select * from test order by id desc")
    suspend fun getTestRecords(): List<TestRecords>

    //根据id查询
    @Query("select * from test where id =:numb")
    suspend fun getTestRecord(numb: Int): TestRecords

    //分页查询
    @Query("select * from test  WHERE (:dateStr = '' OR date = :dateStr) order by id desc limit :limit offset :page")
    suspend fun getPageTestRecord(page: Int, limit: Int,dateStr:String): List<TestRecords>

    //数据总量
    @Query("SELECT COUNT(*) FROM test")
    suspend fun getRecordCount(): Int
}