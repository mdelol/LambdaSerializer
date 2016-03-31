package org.bwsw.lambdaserializer

import java.io._
import java.net.{URLClassLoader, URL}
import java.nio.file._

import scala.io.Source

/**
 * Created by krickiy_sp on 28.03.16.
 */
object Serializer
{
  private case class Serialized(lambda: Array[Byte], byteCode: Array[Byte], name: String)

  def serialize[A, B](f: (A) => B, path: String): Unit = {
    val lambda: Array[Byte] = getSerializedLambda(f)
    val byteCode: Array[Byte] = getByteCode(f)

    val stream: FileOutputStream = new FileOutputStream(path)
    val objectStream = new ObjectOutputStream(stream)
    val string: String = f.getClass.toString
    objectStream.writeObject(Serialized(lambda, byteCode, string.drop("class ".length)))

    val i = 0
  }

  private def getByteCode[A, B](f: (A) => B): Array[Byte] = {
    val location: URL = f.getClass.getProtectionDomain.getCodeSource.getLocation
    val file: File = new File(location.toURI.getPath)



    if (file.isDirectory) {
      val listFiles = file.recursiveListFiles.filter(x => x.isFile).map(x => {
        val string1: String = x.toString
        string1.take(string1.length() - 6)
      })
      val string: String = f.getClass.toString
      val strtofind = string.drop("class ".length)
      val find = listFiles.find(x => {
        val replace: String = x.replace('/', '.')
        replace.endsWith(strtofind)
      })
      find match {
        case None => throw new NotImplementedError()
        case Some(pth) =>
          val allBytes: Array[Byte] = Files.readAllBytes(Paths.get(pth + ".class"))
          allBytes
      }
    } else {
      Array[Byte]()

    }
  }

  private implicit class FileUtils(f: File){
     def recursiveListFiles: Array[File] = {
      val these = f.listFiles
      these ++ these.filter(_.isDirectory).flatMap(x=>x.recursiveListFiles)
    }
  }

  private def getSerializedLambda[A, B](f: (A) => B)= {
    val stream = new ByteArrayOutputStream()
    val objectStream = new ObjectOutputStream(stream)
    objectStream.writeObject(f)
    stream.toByteArray
  }

  def deserialize[A, B](pth: String): (A) => B = {
    val stream = new FileInputStream(pth)
    val objectStream = new ObjectInputStream(stream)
    val serialized: Serialized = objectStream.readObject().asInstanceOf[Serialized]

    val path: Path = Paths.get(s"test/${serialized.name.replace('.', '/')}.class")
    val parent: Path = path.getParent
    Files.createDirectories(parent)
    if(Files.exists(path)){
      Files.delete(path)
    }

    Files.write(path, serialized.byteCode, StandardOpenOption.CREATE_NEW)
    val file: File = new File("test/")
    val l: URL = file.toURI.toURL

    val loader = new URLClassLoader(Array(l),java.lang.Thread.currentThread().getContextClassLoader)

    val readObject: AnyRef = new ClassLoaderObjectInputStream(loader, new ByteArrayInputStream(serialized.lambda)).readObject()
    readObject.asInstanceOf[A=>B]
  }


}

