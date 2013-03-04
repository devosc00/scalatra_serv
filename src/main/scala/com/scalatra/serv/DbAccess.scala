package com.scalatra.serv

import java.sql.DriverManager
import java.io.File

trait DbAccess extends FileHandler{
  
    def dbInsert(file: Array[Byte], key: String, name: String) { //plik i hasło do bazy
    val db = connect
    val st = db.prepareStatement("insert into file_key (file_,key_,name_) values (?,?,?)")
    //println("db con")
    try {
      st.setBytes(1, file)
      st.setString(2, key)
      st.setString(3, name)
      st.executeUpdate()
    } catch {
      case e => e.printStackTrace
    }
    db.close
    //println("db close")
  }
  
  def getFromDb(select: String) = {// pobieranie z bazy na pdst zgodności hasła
    val db = connect
    var file: File = null
    try {
      val st = db.prepareStatement("Select file_ , key_, name_ from file_key where key_ = " + select)
      val rs = st.executeQuery()
      while (rs.next()) {
        val fileLikeByteArray = xorEncrypt(rs.getBytes("file_"),keyToByteArray(rs.getString("key_")))
        file = toFile(rs.getString("name_"), fileLikeByteArray)
      }
      rs.close()
    } catch {
      case e => e.printStackTrace
    }
    db.close
    println(file.getName())
    file
  }
  

  
    def connect(): java.sql.Connection = {
    val db = DriverManager.getConnection("jdbc:postgresql://localhost/files_db", "postuser1", "postpass")
    db
  }
    
    def toKeyString(string: String) = {
      val key = ("'" + string + "'")
      key
    }

}