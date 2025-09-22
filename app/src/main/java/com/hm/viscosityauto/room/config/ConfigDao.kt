package com.hm.viscosityauto.room.config

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update

@Dao
interface ConfigDao {
    //添加  传递一个参数 对象
    @Insert
    suspend fun insert(record: ConfigModel)

    //删除
    @Delete
    suspend fun delete( record: ConfigModel): Int


    @Query("DELETE FROM config WHERE id IN (:ids)")
    fun deleteUsersByIds(ids: List<Long?>?)


    //删除全部
    @Query("DELETE FROM config")
    suspend fun deleteAll(): Int

    //修改 传入对象 设置 id 进行修改某一个
    @Update
    suspend fun updateData(record: ConfigModel): Int

    //查询 根据id倒序
    @Query("select * from config order by id desc")
    suspend fun getRecords(): List<ConfigModel>


    //分页查询
    @Query("select * from config  WHERE type = :type order by id desc limit :limit offset :page")
    suspend fun getPageRecord(page: Int, limit: Int,type:Int): List<ConfigModel>


    //根据id查询
    @Query("select * from config where id =:numb")
    suspend fun getRecord(numb: Int): ConfigModel

    //数据总量
    @Query("SELECT COUNT(*) FROM config")
    suspend fun getRecordCount(): Int
}
