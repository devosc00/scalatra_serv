package com.scalatra.serv

import org.scalatra._
import servlet.{MultipartConfig, SizeConstraintExceededException, FileUploadSupport}
import xml.Node
import scala.App
import java.io.File



class MyScalatraServlet extends ScalatraServlet with FileUploadSupport with FlashMapSupport with DbAccess{
  configureMultipartHandling(MultipartConfig(maxFileSize = Some(6*1024*1024)))

  def displayPage(content: Seq[Node]) = Template.page("File upload", content, url(_))

  error {
    case e: SizeConstraintExceededException =>
      RequestEntityTooLarge(displayPage(
        <p>The file you uploaded exceeded the 6 MB limit.</p>))
  }

  get("/") {
    displayPage(
      <form action={url("/upload1")} method="post" enctype="multipart/form-data">
       <p>File to upload: <input type="file" name="file" /></p>
       <p>Key to file: <input type="text" name="key" value="" /></p>
       <p><input type="submit" value="Upload1" /></p>
      </form>
      <p>
        Upload a file using the key to encrypt it.
      </p>

      <p>
        The maximum file size accepted is 6 MB.
      </p>)
  }

 post("/upload1") {
    val file = fileParams.get("file") match{
      case Some(file) => (file.get(), file.name)
    }
    val key = params.getOrElse("key", "0")
    val encryptFile = xorEncrypt(file._1, keyToByteArray(key))
    dbInsert(encryptFile, key, file._2)
    redirect("/file")
  }
  
get("/file"){
    displayPage(
      <form action={url("/GetFile")} method="post">
       <p>Type key to get decrypted file: <input type="text" name="decrypt" value="" /></p>
       <p><input type="submit" value="Get file" /></p>
      </form>)
 }
    
  post("/GetFile") {
    val decrypt = params.getOrElse("decrypt", "0")
    fileDownload(toKeyString(decrypt)) 
 }
     
  def fileDownload(fil: String) = {  
    val file = getFromDb(fil)
         contentType = "application/octet-stream"
         response.setHeader("Content-Disposition", "attachment; filename=\"" + file.getName + "\"")
    file
}
  
post("/upload")  {   
  fileParams.get("file") match {
      case Some(file) =>
        Ok(file.get(), Map(
          "Content-Type"        -> (file.contentType.getOrElse("application/octet-stream")),
          "Content-Disposition" -> ("attachment; filename=\"" + file.name + "\"")
        ))
        
      case None =>
        BadRequest(displayPage(
          <p>
            Hey! You forgot to select a file.
          </p>))
    }
 }
}
