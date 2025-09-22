package com.hm.viscosityauto.room.admin

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update

@Dao
interface AdminDao {

    //添加  传递一个参数 对象
    @Insert
    suspend fun addAdmin(people: AdminRecords)
    //删除
    @Delete
    suspend fun deleteAdmin(people: AdminRecords): Int

    //删除全部
    @Query("DELETE FROM admin")
    suspend fun deleteAll(): Int

    //修改 传入对象 设置 id 进行修改某一个
    @Update
    suspend fun updateData( people: AdminRecords): Int

    //查询 根据id倒序
    @Query("select * from admin order by id desc")
    suspend fun getAdmins(): List<AdminRecords>

    //根据id查询
    @Query("select * from admin where name =:name")
    suspend fun getAdmin(name: String): AdminRecords?

    //数据总量
    @Query("SELECT COUNT(*) FROM admin")
    suspend fun getAdminCount(): Int

}