package cloud.bytepulse.bp.common.util;


import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.ByteArrayInputStream;
import java.io.StringWriter;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author jiejiebiezheyang
 * @since 2025-06-05 20:00
 */
public class SVGUtils {

    /**
     * 去除width, height 属性
     * fill 设置为 currentColor
     * 去除多余空格
     */
    public static String sanitizeSvgXml(String svgContent) throws Exception {
        if (svgContent == null || svgContent.isEmpty()) return "";

        // 1. 解析 SVG 为 DOM
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        factory.setNamespaceAware(true);
        factory.setIgnoringComments(true);
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document doc = builder.parse(new ByteArrayInputStream(svgContent.getBytes("UTF-8")));

        // 2. 处理节点
        processElementRecursively(doc.getDocumentElement());

        // 3. DOM 转字符串
        Transformer transformer = TransformerFactory.newInstance().newTransformer();
        transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
        transformer.setOutputProperty(OutputKeys.INDENT, "no");
        StringWriter writer = new StringWriter();
        transformer.transform(new DOMSource(doc), new StreamResult(writer));
        String rawSvg = writer.toString();

        // 4. 清理格式：去除 >   < 中的空白
        rawSvg = rawSvg.replaceAll(">\\s+<", "><");

        // 5. 清理标签内多余空格（Java 8 方式）
        rawSvg = cleanAttributeSpaces(rawSvg);

        return rawSvg.trim();
    }

    private static void processElementRecursively(Element element) {
        element.removeAttribute("width");
        element.removeAttribute("height");
        element.setAttribute("fill", "currentColor");

        NodeList children = element.getChildNodes();
        for (int i = 0; i < children.getLength(); i++) {
            Node child = children.item(i);
            if (child.getNodeType() == Node.ELEMENT_NODE) {
                processElementRecursively((Element) child);
            }
        }
    }

    private static String cleanAttributeSpaces(String svg) {
        // 处理类似 <svg    xmlns="..."   viewBox="..."> 这种
        Pattern pattern = Pattern.compile("<(\\w+)(\\s+[^>]*?)\\s*>"); // 标签+属性们
        Matcher matcher = pattern.matcher(svg);
        StringBuilder sb = new StringBuilder();

        while (matcher.find()) {
            String tag = matcher.group(1);
            String attrs = matcher.group(2).trim().replaceAll("\\s{2,}", " ");
            matcher.appendReplacement(sb, "<" + tag + " " + attrs + ">");
        }
        matcher.appendTail(sb);
        return sb.toString();
    }
}
