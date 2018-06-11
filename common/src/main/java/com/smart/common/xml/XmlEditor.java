package com.smart.common.xml;

import com.smart.common.net.SendCmd;
import com.smart.common.util.ThreadUtil;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

public class XmlEditor implements XmlEditorInter {
    
    /** xml解析成的文档对象 */
    private Document xmlDoc = null;
    
    /** xml文件本地保存路径 */
    private String xmlPath = null;
    
    /** 根节点 */
    private Element root = null;
    
    /** xml编辑方法接口 */
    private EditorInter mEditor = null;
    
    /** xml编辑器单例 */
    private static XmlEditor instance = null;
    
    /** 保存数据标签 */
    private final static String ROOT_ELEMENT_TAG = "map";
    
    public static XmlEditor getXmlEditor(String path) {
        return getXmlEditor(path, false);
    }
    
    public static XmlEditor getXmlEditor(String path, boolean reset) {
        if (instance == null || reset) {
            instance = new XmlEditor(path);
        }
        return instance;
    }
    
    private XmlEditor(String path) {
        initXmlFile(path);
        initDoc(path);
    }
    
    private void initDoc(String path) {
        xmlPath = path;
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        dbf.setIgnoringElementContentWhitespace(true);
        DocumentBuilder db;
        try {
            db = dbf.newDocumentBuilder();
            xmlDoc = db.parse("file://" + xmlPath);
            // 创建父标签
            root = xmlDoc.getDocumentElement();
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("init parse xml failed");
        }
    }
    
    @Override
    public EditorInter edit() {
        if (mEditor == null) {
            mEditor = new EditorInter() {
                
                @Override
                public void put(String key, String value) {
                    try {
                        putValue(key, value);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                
                @Override
                public void commit() {
                    try {
                        commitValues();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                
                @Override
                public void delete(String key) {
                    try {
                        deleteValue(key);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            };
        }
        return mEditor;
    }
    
    @Override
    public int getInt(String key, int defaultValue) {
        Node node = selectSingleNode(initSelectExpress(key), root);
        int result = defaultValue;
        if (node != null) {
            try {
                result = Integer.valueOf(node.getTextContent());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return result;
    }
    
    @Override
    public String getString(String key) {
        Node node = selectSingleNode(initSelectExpress(key), root);
        String result = null;
        if (node != null) {
            result = node.getTextContent();
        }
        return result;
    }
    
    @Override
    public boolean getBoolean(String key, boolean defaultValue) {
        Node node = selectSingleNode(initSelectExpress(key), root);
        boolean result = defaultValue;
        if (node != null) {
            try {
                result = Boolean.valueOf(node.getTextContent());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return result;
    }
    
    @Override
    public long getLong(String key, long defaultValue) {
        Node node = selectSingleNode(initSelectExpress(key), root);
        long result = defaultValue;
        if (node != null) {
            try {
                result = Long.valueOf(node.getTextContent());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return result;
    }
    
    @Override
    public float getFloat(String key, float defaultValue) {
        Node node = selectSingleNode(initSelectExpress(key), root);
        float result = defaultValue;
        if (node != null) {
            try {
                result = Float.valueOf(node.getTextContent());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return result;
    }
    
    @Override
    public double getDouble(String key, double defaultValue) {
        Node node = selectSingleNode(initSelectExpress(key), root);
        double result = defaultValue;
        if (node != null) {
            try {
                result = Double.valueOf(node.getTextContent());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return result;
    }
    
    /**
     * 提交修改属性，覆盖源文件
     */
    private void commitValues() {
        TransformerFactory factory = TransformerFactory.newInstance();
        try {
            Transformer former = factory.newTransformer();
            former.transform(new DOMSource(xmlDoc), new StreamResult(new File(xmlPath)));
        } catch (TransformerException e) {
            e.printStackTrace();
        }
    }
    
    /**
     * 根据查询key拼接处筛选表达式
     * @param key 查询key
     * @return 筛选表达式
     */
    private String initSelectExpress(String key) {
        return "/" + ROOT_ELEMENT_TAG + "/" + key;
    }
    
    /**
     * 根据选择条件从source节点中择出目标节点
     * @param express 筛选表达式
     * @param source 母节点
     * @return 目标节点
     */
    private Node selectSingleNode(String express, Element source) {
        Node result = null;
        XPathFactory xpathFactory = XPathFactory.newInstance();
        XPath xpath = xpathFactory.newXPath();
        try {
            result = (Node)xpath.evaluate(express, source, XPathConstants.NODE);
        } catch (XPathExpressionException e) {
            e.printStackTrace();
        }
        return result;
    }
    
    /**
     * 保存键值对
     * @param key 保存的Key
     * @param value 保存的value
     */
    private void putValue(String key, String value) {
        Node node = selectSingleNode(initSelectExpress(key), root);
        if (node != null) {
            node.setTextContent(value);
        } else {
            Element e = xmlDoc.createElement(key);
            e.setTextContent(value);
            root.appendChild(e);
        }
    }
    
    /**
     * 删除key对应的节点
     * @param key 要删除的key
     */
    private void deleteValue(String key) {
        Node node = selectSingleNode(initSelectExpress(key), root);
        if (node != null) {
            root.removeChild(node);
            commitValues();
        }
    }
    
    /**
     * 根据路径创建文件
     * @param path 文件路径
     */
    private void initXmlFile(String path) {
        File file = new File(path);
        if (!file.exists() || !file.isFile()) {
            FileOutputStream fos = null;
            try {
                file.getParentFile().mkdirs();
                changeFilePermission();
                file.createNewFile();
                fos = new FileOutputStream(file);
                fos.write("<?xml version='1.0' encoding='UTF-8' standalone='no'?> <map></map>".getBytes("utf-8"));
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (fos != null) {
                    try {
                        fos.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }
    
    /**
     * 改变目录权限
     */
    private void changeFilePermission() {
        ThreadUtil.getSinglePool().submit(new Runnable() {
            @Override
            public void run() {
                //修改文件权限
                SendCmd.get().etc_rwx();
            }
        });
    }
}
