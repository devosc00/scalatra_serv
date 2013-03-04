package com.scalatra.serv
import java.io.File
import java.io.BufferedInputStream
import java.io.FileInputStream
import java.io.InputStream
import java.io.FileOutputStream
import org.scalatra.BadRequest



trait FileHandler {

  def readFileToByteArray(name: InputStream) = { 
    val in = new BufferedInputStream(name)
    val size = in.available
    val bytes = Array.ofDim[Byte](size)
    in.read(bytes)
    in.close()
    bytes
  }
    
  def keyToByteArray(key: String) = { 
    val x = key.toCharArray()
    val y = x.map(_.toByte)
    y
  }
    
  def xorEncrypt(file: Array[Byte], key: Array[Byte]) = {
    val in_array = Array.ofDim[Byte](file.length)
    for (i <- 0 until file.length) {
      val out_array = (file(i) ^ key(i % key.length)).toByte
      in_array.update(i, out_array)
    }
    in_array
  }
   
  def toFile(name: String, bytes: Array[Byte]) = {
    val file = new File(name)
    val out = new FileOutputStream(file)
    out.write(bytes)
    out.close()
    println(file.getName())
    file
  }
  
  
    def prin(a: Array[Byte]) = {
    for (i <- 0 until a.length) print(a(i).toString)
  }
  
}