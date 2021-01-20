
import org.w3c.dom.Document
import org.w3c.dom.Element
import java.io.File
import java.io.OutputStreamWriter
import java.io.FileInputStream
import java.io.InputStreamReader
import java.io.BufferedReader

import javax.xml.parsers.DocumentBuilderFactory
import javax.xml.xpath.XPath
import javax.xml.xpath.XPathFactory
import javax.xml.xpath.XPathConstants
import javax.xml.transform.stream.StreamResult
import javax.xml.transform.dom.DOMSource
import javax.xml.transform.OutputKeys
import javax.xml.transform.TransformerFactory

import java.lang.StringBuilder
import java.util.Vector

class InputParser {
    private var xmlDoc: Document = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument()
    private var xPath: XPath = XPathFactory.newInstance().newXPath()
    fun process(f:File) {
        /*
		 * Holding place for incoming elements
		 */
        val dataElemList: Vector<DataElement> = Vector<DataElement>()

            var line: String? = null
            /*
			 * Read from input file
			 */
            val read = BufferedReader(InputStreamReader(FileInputStream(f)))
            while (read.readLine().also { line = it } != null) {
                val parts = line!!.split("\\|".toRegex()).toTypedArray()
                for (part in parts) {
                    val de = DataElement(part)
                    dataElemList.add(de)
                }
            }
            read.close()

        /*
		 * Verify we have the data pre-loaded
		 */
        for (de in dataElemList) {
            println(de)
        }

        /*
		 * Load data into an XML Document.
		 */
        for (de in dataElemList) {
            /*
			 * Create XML Element to append to XML Document
			 */
            val elem = xmlDoc.createElement("DataElem")
            elem.setAttribute("node_id", de.node_id)
            elem.setAttribute("node_name", de.node_name)
            if ("null" == de.parent_id) {
                xmlDoc.appendChild(elem)
            } else {
                try {
                    val expr = xPath.compile(java.lang.String.format("//DataElem[@node_id=\"%s\"]/self::node()", de.parent_id))
                    val lookup = expr.evaluate(xmlDoc, XPathConstants.NODE) as Element
                    lookup.appendChild(elem)
                } catch (err: Exception) {
                    err.printStackTrace()
                }
            }
        }
        println();
        /*
		 * XML Document populated, dump to System.out
		 */
        val tfactory = TransformerFactory.newInstance()
        tfactory.setAttribute("indent-number", 3)
        val xtransform = tfactory.newTransformer()

        xtransform.setOutputProperty(OutputKeys.INDENT, "yes")
        xtransform.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes")
        val src = DOMSource(xmlDoc)
        val res = StreamResult(OutputStreamWriter(System.out, "utf-8"))
        xtransform.transform(src, res)
    }
    private class DataElement(part: String) {
        var parent_id: String
        var node_id: String
        var node_name: String
        init {
            val bits = part.split(",".toRegex()).toTypedArray()
            if (bits.size == 3) {
                parent_id = bits[0]
                node_id = bits[1]
                node_name = bits[2]
            } else {
                parent_id=""
                node_id=""
                node_name=""
            }
        }
        override fun toString(): String {
            val sb = StringBuilder()
            sb.append(parent_id).append(',').append(node_id).append(',').append(node_name)
            return sb.toString()
        }
    }
}
