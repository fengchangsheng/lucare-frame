package com.lucare.common.utils;

import org.w3c.dom.*;
import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Lucare.Feng on 2016/3/24.
 */
public class DomNode {
    private static final String DEFAULT_ENCODING_CHARSET = "UTF-8";
    private Element e;

    private DomNode(Element element) {
        this.e = element;
    }

    public static DomNode newDomNode(String rootName) {
        try {
            DocumentBuilder e = DocumentBuilderFactory.newInstance().newDocumentBuilder();
            Document doc = e.newDocument();
            doc.setXmlStandalone(true);
            Element root = doc.createElement(rootName);
            doc.appendChild(root);
            return new DomNode(root);
        } catch (Exception var4) {
            throw new XmlException(var4.getMessage(), var4);
        }
    }

    public static DomNode getRoot(InputStream is) {
        DomNode var5;
        try {
            DocumentBuilderFactory e = DocumentBuilderFactory.newInstance();
            DocumentBuilder docBuilder = e.newDocumentBuilder();
            docBuilder.setEntityResolver(new EntityResolver() {
                public InputSource resolveEntity(String publicId, String systemId) throws SAXException, IOException {
                    return systemId.contains("http://java.sun.com/dtd/web-app_2_3.dtd")?new InputSource(new StringReader("")):null;
                }
            });
            Document doc = docBuilder.parse(is);
            Element root = doc.getDocumentElement();
            var5 = new DomNode(root);
        } catch (Exception var14) {
            throw new XmlException(var14.getMessage(), var14);
        } finally {
            try {
                if(is != null) {
                    is.close();
                }
            } catch (Exception var13) {
                ;
            }

        }

        return var5;
    }

    public static DomNode getRootFromClassPath(String filename) {
        return getRoot(CommonUtils.getInputStreamFromClassPath(filename));
    }

    public static DomNode getRoot(String xmlNamePath) {
        try {
            return getRoot((InputStream)(new FileInputStream(xmlNamePath)));
        } catch (FileNotFoundException var2) {
            throw CommonUtils.illegalStateException(var2);
        }
    }

    public Element getDomElement() {
        return this.e;
    }

    public String getTagName() {
        return this.e.getTagName();
    }

    public String attributeValue(String attributeName) {
        return this.attributeValue(attributeName, (String)null);
    }

    public String attributeValue(String attributeName, String def) {
        String value = this.e.getAttribute(attributeName);
        return value != null && !value.trim().isEmpty()?value.trim():def;
    }

    public int attributeInt(String attributeName) {
        return this.attributeInt(attributeName, 0);
    }

    public int attributeInt(String attributeName, int def) {
        String value = this.attributeValue(attributeName);
        return CommonUtils.isEmpty(value)?def:Integer.valueOf(value).intValue();
    }

    public boolean existElement(String elementName) {
        NodeList nodeList = this.e.getElementsByTagName(elementName);
        return nodeList != null && nodeList.getLength() >= 1;
    }

    public String elementText(String elementName) {
        String text = null;
        Element element = (Element)this.e.getElementsByTagName(elementName).item(0);
        if(element != null && element.getFirstChild() != null) {
            text = element.getFirstChild().getNodeValue();
        }

        return text == null?null:text.trim();
    }

    public int elementInt(String elementName) {
        return this.elementInt(elementName, 0);
    }

    public int elementInt(String elementName, int def) {
        try {
            return Integer.valueOf(this.elementText(elementName)).intValue();
        } catch (Exception var4) {
            return def;
        }
    }

    public boolean elementBoolean(String elementName, boolean def) {
        String value = this.elementText(elementName);
        return CommonUtils.isEmpty(value)?def:Boolean.valueOf(value.trim()).booleanValue();
    }

    public DomNode element(String elementName) {
        NodeList nodeList = this.e.getElementsByTagName(elementName);
        if(nodeList != null && nodeList.getLength() >= 1) {
            Element element = (Element)nodeList.item(0);
            return new DomNode(element);
        } else {
            return null;
        }
    }

    public List<DomNode> getChildNodes() {
        ArrayList list = new ArrayList();
        NodeList nodeList = this.e.getChildNodes();
        if(nodeList != null) {
            for(int i = 0; i < nodeList.getLength(); ++i) {
                Node node = nodeList.item(i);
                if(node.getNodeType() == 1) {
                    Element element = (Element)node;
                    list.add(new DomNode(element));
                }
            }
        }

        return list;
    }

    public List<DomNode> elements(String elementName) {
        ArrayList list = new ArrayList();
        NodeList nodeList = this.e.getElementsByTagName(elementName);
        if(nodeList != null) {
            for(int i = 0; i < nodeList.getLength(); ++i) {
                Node node = nodeList.item(i);
                if(node.getNodeType() == 1) {
                    Element element = (Element)node;
                    list.add(new DomNode(element));
                }
            }
        }

        return list;
    }

    public DomNode addElement(String name) {
        Document document = this.e.getOwnerDocument();
        Element element = document.createElement(name);
        this.e.appendChild(element);
        return new DomNode(element);
    }

    public DomNode addElement(String name, String value) {
        Document document = this.e.getOwnerDocument();
        Element element = document.createElement(name);
        this.e.appendChild(element);
        Text text = document.createTextNode(value);
        element.appendChild(text);
        return new DomNode(element);
    }

    public DomNode setAttribute(String name, String value) {
        this.e.setAttribute(name, value);
        return this;
    }

    public void remove(DomNode subDom) {
        this.e.removeChild(subDom.getDomElement());
    }

    public void removeElement(String name) {
        NodeList nodeList = this.e.getElementsByTagName(name);
        if(nodeList != null) {
            for(int i = 0; i < nodeList.getLength(); ++i) {
                this.e.removeChild(nodeList.item(i));
            }
        }

    }

    public void removeAttribute(String name) {
        this.e.removeAttribute(name);
    }

    public DomNode updateElementText(String name, String value) {
        Element element = (Element)this.e.getElementsByTagName(name).item(0);
        Node textNode = element.getFirstChild();
        textNode.setNodeValue(value);
        return new DomNode(element);
    }

    public DomNode updateElementText(String value) {
        Node textNode = this.e.getFirstChild();
        textNode.setNodeValue(value);
        return this;
    }

    public String getElementText() {
        Node textNode = this.e.getFirstChild();
        return textNode.getNodeValue();
    }

    public void write(OutputStream os) {
        this.write(os, "UTF-8");
    }

    public void write(String xmlFile) throws XmlException {
        this.write(xmlFile, "UTF-8");
    }

    public void write(String xmlFile, String encoding) throws XmlException {
        try {
            FileOutputStream e = new FileOutputStream(xmlFile);
            this.write((OutputStream)e, encoding);
            e.close();
        } catch (Exception var4) {
            throw new XmlException(var4.getMessage(), var4);
        }
    }

    public void write(OutputStream os, String encoding) {
        try {
            TransformerFactory e = TransformerFactory.newInstance();
            e.setAttribute("indent-number", Integer.valueOf(2));
            Transformer transformer = e.newTransformer();
            transformer.setOutputProperty("encoding", encoding);
            transformer.setOutputProperty("indent", "yes");
            transformer.setOutputProperty("standalone", "yes");
            transformer.transform(new DOMSource(this.e.getOwnerDocument()), new StreamResult(new OutputStreamWriter(os)));
        } catch (TransformerConfigurationException var5) {
            var5.printStackTrace();
        } catch (TransformerFactoryConfigurationError var6) {
            var6.printStackTrace();
        } catch (TransformerException var7) {
            var7.printStackTrace();
        }

    }

    public void printNodes() {
        NodeList nodeList = this.e.getChildNodes();
        if(nodeList != null) {
            for(int i = 0; i < nodeList.getLength(); ++i) {
                Node node = nodeList.item(i);
                System.out.println("节点名: " + node.getNodeName() + ", 节点值: " + node.getNodeValue() + ", 节点类型: " + node.getNodeType());
            }
        }

    }

    public boolean attributeBoolean(String attributeName) {
        return this.attributeBoolean(attributeName, false);
    }

    public boolean attributeBoolean(String attributeName, boolean def) {
        String value = this.attributeValue(attributeName);
        return CommonUtils.isEmpty(value)?def:Boolean.valueOf(value).booleanValue();
    }
}
